
/*
 * Copyright (C) 2015 Hiroshi Miura
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package bitext2tmx.core;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import bitext2tmx.util.RuntimePreferences;

import static bitext2tmx.util.Localization.getString;

/**
 *
 * @author miurahr
 */
@SuppressWarnings("serial")
public class Document {

  final private ArrayList<String> documentSegments;

  public Document() {
    documentSegments = new ArrayList<>();
  }

  public boolean isEmpty(){
    return documentSegments.isEmpty();
  }

  public int size(){
    return documentSegments.size();
  }

  public String remove(int index){
    return documentSegments.remove(index);
  }

  public void add(int index, String ele){
    documentSegments.add(index, ele);
  }

  public void add(String ele){
    documentSegments.add(ele);
  }

  public String get(int index){
    return  documentSegments.get(index);
  }

  public void set(int index, String content){
    documentSegments.set(index, content);
  }
  
  /**
   * Perform alignments:join.
   *
   * joins the selected row with the following.
   *
   * @param index: join index and index+1
   */
  public void join(final int index) {
    String cad;
    int length = documentSegments.size() - 1;
    cad = documentSegments.get(index);
    cad = cad.concat(" ");
    cad = cad.concat(documentSegments.get(index + 1));
    documentSegments.set(index, cad.trim());
    for (int i = index + 1; i < length; i++) {
      documentSegments.set(i, documentSegments.get(i + 1));
    }
    documentSegments.set(length, "");
  }
  
  /**
   * Perform alignments: delete.
   *
   * deletes the selected row
   *
   * @param index: to be deleted
   */
  public void delete(final int index) {
    int length = documentSegments.size() -1;
    for (int i = index ; i < length; i++) {
      documentSegments.set(i, documentSegments.get(i + 1));
    }
    documentSegments.set(length, "");
  }

  /**
   * Perform alignments: split.
   *
   * splits the selected row at the given position creating two
   * rows.
   *
   * @param index: join index and index+1
   * @param position: position at which the split is performed
   */
  public void split(final int index, final int position) {
    String cad;
    int length = documentSegments.size() - 1;
    assert length >= index;
    if (length == index ||
        !documentSegments.get(length).equals("")) {
      documentSegments.add(length + 1, documentSegments.get(length));
      length++;
    }
    for (int i = length; i > (index+1); i--) {
      documentSegments.set(i, documentSegments.get(i-1));
    }
    cad = documentSegments.get(index);
    if (position == 0) {
      documentSegments.set(index, "");
    } else {
      documentSegments.set(index, cad.substring(0, position).trim());
    }
    documentSegments.set(index+1, cad.substring(position).trim());
  }
}
