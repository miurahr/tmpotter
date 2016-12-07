/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2016 Hiroshi Miura
 *
 *  This file is part of TMPotter.
 *
 *  TMPotter is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  TMPotter is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with TMPotter.  If not, see http://www.gnu.org/licenses/.
 *
 * *************************************************************************/

package org.tmpotter.util.gui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.UIManager;


public class NegativeDefaultButtonJOptionPane {

  public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType) {
    List<Object> options = new ArrayList<Object>();
    Object defaultOption;
    switch(optionType){
      case JOptionPane.OK_CANCEL_OPTION:
        options.add(UIManager.getString("OptionPane.okButtonText"));
        options.add(UIManager.getString("OptionPane.cancelButtonText"));
        defaultOption = UIManager.getString("OptionPane.cancelButtonText");
        break;
      case JOptionPane.YES_NO_OPTION:
        options.add(UIManager.getString("OptionPane.yesButtonText"));
        options.add(UIManager.getString("OptionPane.noButtonText"));
        defaultOption = UIManager.getString("OptionPane.noButtonText");
        break;
      case JOptionPane.YES_NO_CANCEL_OPTION:
        options.add(UIManager.getString("OptionPane.yesButtonText"));
        options.add(UIManager.getString("OptionPane.noButtonText"));
        options.add(UIManager.getString("OptionPane.cancelButtonText"));
        defaultOption = UIManager.getString("OptionPane.cancelButtonText");
        break;
      default:
        throw new IllegalArgumentException("Unknown optionType "+optionType);
    }
    return JOptionPane.showOptionDialog(parentComponent, message, title, optionType, JOptionPane.QUESTION_MESSAGE, null, options.toArray(), defaultOption);
  }

}
