/**
 * Copyright (c) 1996-2004 Borland Software Corp. All Rights Reserved.
 *
 * This SOURCE CODE FILE, which has been provided by Borland as part
 * of a Borland product for use ONLY by licensed users of the product,
 * includes CONFIDENTIAL and PROPRIETARY information of Borland.
 *
 * USE OF THIS SOFTWARE IS GOVERNED BY THE TERMS AND CONDITIONS
 * OF THE LICENSE STATEMENT AND LIMITED WARRANTY FURNISHED WITH
 * THE PRODUCT.
 *
 * IN PARTICULAR, YOU WILL INDEMNIFY AND HOLD BORLAND, ITS RELATED
 * COMPANIES AND ITS SUPPLIERS, HARMLESS FROM AND AGAINST ANY
 * CLAIMS OR LIABILITIES ARISING OUT OF THE USE, REPRODUCTION, OR
 * DISTRIBUTION OF YOUR PROGRAMS, INCLUDING ANY CLAIMS OR LIABILITIES
 * ARISING OUT OF OR RESULTING FROM THE USE, MODIFICATION, OR
 * DISTRIBUTION OF PROGRAMS OR FILES CREATED FROM, BASED ON, AND/OR
 * DERIVED FROM THIS SOURCE CODE FILE.
 */
//--------------------------------------------------------------------------------------------------
// Copyright (c) 1996 - 2004 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------
package com.borland.jbcl.editors;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;

import javax.swing.table.TableCellEditor;

import com.borland.jb.util.FastStringBuffer;

public class String2DArrayEditor implements PropertyEditor
{
  public String2DArrayEditor() {
    //editorPanel = new String2DArrayEditorPanel();
  }

  static final String NULLSTRING = "null";  

  public void setValue(Object o) {
    String[][] sa = (String[][])o;
    value = sa;
/*
   System.err.println("setValue called from:");
    new NullPointerException().printStackTrace();
    if (sa != null) {
      for (int i = 0; i < sa.length; ++i) {
        String[] ss = sa[i];
        for (int j = 0; j < ss.length; ++j) {
          System.err.println("[" + i + "][" + j + "] is: " + ss[j]);
        }
      }
    }
*/
    if (sa != null && editorPanel != null)
      editorPanel.setItems(value);
  }

  public Object getValue() {
    if (editorPanel != null) {
      if (editorPanel.grid.isEditing()){
         TableCellEditor editor =editorPanel.grid.getCellEditor();
         editor.stopCellEditing();
         //grid.removeEditor();
      }
      value = editorPanel.getItems(); 
    }
    return value;
  }

  public boolean isPaintable() {
    return false;
  }

  public void paintValue(Graphics g, Rectangle box) {
  }

  public String getAsText() {
    return StringEditor.rawTextToDisplay(string2DArrayToText(value));
  }

  public String getJavaInitializationString() {
    return javaInitializationForArrayOfStringArrays(value);
  }

  public static final String javaInitializationForArrayOfStrings(String[] strings) {
    if (strings == null || strings.length == 0)
      return NULLSTRING;
    int lineLength = 0;
    FastStringBuffer fsb = new FastStringBuffer("new String[] {");  
    for (int i = 0; i < strings.length; ++i) {
      fsb.append(StringEditor.textToSource(strings[i]));

      if ((i+1) < strings.length)   // separate by commas (except for the last one)
        fsb.append(", ");  

      // The compiler will choke on lines longer than 1024, not to mention most editors, so try to inject
      // some linefeeds from time to time to keep the length of each line shorter than 100
      if (strings[i] != null) {
        lineLength += strings[i].length();
        if (lineLength > 92) {
          lineLength = 0;
          fsb.append("\n\t");  
        }
      }
    }
    fsb.append("}");
    return fsb.toString();
  }

  public static final String javaInitializationForArrayOfStringArrays(String[][] strings) {
    return javaInitializationForArrayOfStringArrays(strings, false);
  }

  public static final String javaInitializationForArrayOfStringArrays(String[][] strings, boolean newLineEachRow) {
    if (strings == null || strings.length == 0)
      return NULLSTRING;
    int lineLength = 0;
    FastStringBuffer fsb = new FastStringBuffer("new String[][] {");  
    if (newLineEachRow)
      fsb.append("\n\t");
    for (int i = 0; i < strings.length; ++i) {
      fsb.append("{");
      fsb.append(stringArrayToText(strings[i]));
      fsb.append("},");
      if (newLineEachRow)
        fsb.append("\n\t");

    }
    fsb.append("}");
    return fsb.toString();
  }

  public void setAsText(String text) throws IllegalArgumentException {
    value = textToString2DArray(StringEditor.displayTextToRaw(text));
    //if (editorPanel.grid.isEditing())
    //  editorPanel.grid.safeEndEdit(false);
    if (value != null && editorPanel != null)
      editorPanel.setItems(value);
  }

  public String[] getTags() {
    return null;
  }

  public Component getCustomEditor() {
    if (editorPanel == null) {
      editorPanel = new String2DArrayEditorPanel();
    }
    return editorPanel;
  }

  public boolean supportsCustomEditor() {
    return true;
  }

  public void addPropertyChangeListener(PropertyChangeListener l) {
    listener = l;
  }

  public void removePropertyChangeListener(PropertyChangeListener l) {
    listener = null;
  }

  //

  String2DArrayEditorPanel editorPanel;
  PropertyChangeListener listener;
  String[][] value;

  private void fire() {
    if (listener != null)
      listener.propertyChange(new PropertyChangeEvent(this, "String2DArrayEditor???", null, value));  
  }

  private static final String string2DArrayToText(String[][] array) {
    FastStringBuffer fsb = new FastStringBuffer("");
    if (array != null)
      for (int i = 0; i < array.length; i++)
        fsb.append(stringArrayToText(array[i]));
    return fsb.toString();
  }

  private static final String[][] textToString2DArray(String text) {
    int textLength = text.length();
    int e = 0;
    int start = 0;
    int end;
    while ((end = text.indexOf('\n', start)) != -1) {
      start = end+1;
      e++;
    }
    if (start < text.length())
      e++;
    String[][] array = new String[1][e]; 
    start = 0;
    e = 0;
    while ((end = text.indexOf('\n', start)) != -1) {
      array[0][e] = text.substring(start, end); 
      start = end+1;
      e++;
    }
    if (start < text.length())
      array[0][e] = text.substring(start);
    return array;
  }

  private static final String stringArrayToText(String[] array) {
    FastStringBuffer fsb = new FastStringBuffer("");
    if (array != null) {
      int lineLen = 0;
      for (int i = 0; i < array.length; i++) {
        String s = StringEditor.textToSource(array[i]);
        fsb.append(s);
        if ((i+1) < array.length)
          fsb.append(", ");
        lineLen += s.length();
        if (lineLen > 90) {
          fsb.append("\n\t");
          lineLen = 0;
        }
      }
    }
    return fsb.toString();
  }

  private static final String[] textToStringArray(String text) {
    int textLength = text.length();
    int e = 0;
    int start = 0;
    int end;
    while ((end = text.indexOf('\n', start)) != -1) {
      start = end+1;
      e++;
    }
    if (start < text.length())
      e++;

    String[] array = new String[e];

    start = 0;
    e = 0;
    while ((end = text.indexOf('\n', start)) != -1) {
      array[e] = text.substring(start, end);
      start = end+1;
      e++;
    }
    if (start < text.length())
      array[e] = text.substring(start);

    return array;
  }
}
