/**************************************************************************
 *
 *  bitext2tmx - Bitext Aligner/TMX Editor
 *
 *  Copyright (C) 2000-2006 Keith Godfrey and Maxym Mykhalchuk
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  This file is part of bitext2tmx.
 *
 *  bitext2tmx is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  bitext2tmx is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with bitext2tmx.  If not, see http://www.gnu.org/licenses/.
 *
 **************************************************************************/

package bitext2tmx.segmentation.datamodels;

import bitext2tmx.segmentation.MapRule;
import bitext2tmx.segmentation.Rule;
import bitext2tmx.segmentation.SRX;
import bitext2tmx.util.Localization;

import java.beans.ExceptionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import javax.swing.table.AbstractTableModel;


/**
 * Table Model for Sets of segmentation rules.
 * 
 * @author Maxym Mykhalchuk
 */
@SuppressWarnings("serial")
public class MappingRulesModel extends AbstractTableModel {

  private SRX srx;

  /**
   * Creates a new instance of MappingRulesModel
   */
  public MappingRulesModel(SRX srx) {
    this.srx = srx;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    MapRule maprule = srx.getMappingRules().get(rowIndex);
    switch (columnIndex) {
      case 0:
        return maprule.getLanguage();
      case 1:
        return maprule.getPattern();
      default:
        return null;
    }
  }

  public int getRowCount() {
    return srx.getMappingRules().size();
  }

  public int getColumnCount() {
    return 2;
  }

  /**
   * The names of table columns
   */
  private static String[] COLUMN_NAMES = new String[]{
    Localization.getString("CORE_SRX_TABLE_HEADER_Language_Name"),
    Localization.getString("CORE_SRX_TABLE_HEADER_Language_Pattern")};

  public String getColumnName(int column) {
    return COLUMN_NAMES[column];
  }

  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return true;
  }

  /**
   * Set value at (row, column).
   * 
   * @param aValue value to set
   * @param rowIndex where to set in row
   * @param columnIndex where to set in column
   */
  @Override
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    MapRule maprule = srx.getMappingRules().get(rowIndex);
    switch (columnIndex) {
      case 0:
        maprule.setLanguage((String) aValue);
        break;
      case 1:
        try {
          maprule.setPattern((String) aValue);
        } catch (PatternSyntaxException pse) {
          fireException(pse);
        }
        break;
      default:
        // Do nothing.
    }
  }

  /**
   * Get column as class.
   *
   * @param columnIndex to get
   * @return class to represent column
   */
  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return String.class;
  }

  /**
   * Adds a new empty mapping rule.
   * @return  row number
   */
  public int addRow() {
    int rows = srx.getMappingRules().size();
    srx.getMappingRules().add(
            new MapRule(Localization.getString("SEG_NEW_LN_CO"), "LN-CO", new ArrayList<Rule>()));
    fireTableRowsInserted(rows, rows);
    return rows;
  }

  /**
   * Removes a mapping rule.
   */
  public void removeRow(int row) {
    srx.getMappingRules().remove(row);
    fireTableRowsDeleted(row, row);
  }

  /**
   * Moves a mapping rule up an order.
   */
  public void moveRowUp(int row) {
    MapRule maprulePrev = srx.getMappingRules().get(row - 1);
    MapRule maprule = srx.getMappingRules().get(row);
    srx.getMappingRules().remove(row - 1);
    srx.getMappingRules().add(row, maprulePrev);
    fireTableRowsUpdated(row - 1, row);
  }

  /**
   * Moves a mapping rule down an order.
   */
  public void moveRowDown(int row) {
    MapRule mapruleNext = srx.getMappingRules().get(row + 1);
    MapRule maprule = srx.getMappingRules().get(row);
    srx.getMappingRules().remove(row + 1);
    srx.getMappingRules().add(row, mapruleNext);
    fireTableRowsUpdated(row, row + 1);
  }

    //
  // Managing Listeners of Errorneous Input
  //
  /**
   * List of listeners
   */
  protected List<ExceptionListener> listeners = new ArrayList<ExceptionListener>();

  public void addExceptionListener(ExceptionListener l) {
    listeners.add(l);
  }

  public void removeTableModelListener(ExceptionListener l) {
    listeners.remove(l);
  }

  public void fireException(Exception e) {
    for (int i = listeners.size() - 1; i >= 0; i--) {
      ExceptionListener l = listeners.get(i);
      l.exceptionThrown(e);
    }
  }

}
