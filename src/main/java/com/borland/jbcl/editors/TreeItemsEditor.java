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

import com.borland.jb.util.FastStringBuffer;
import com.borland.jbcl.control.TreeItems;

public class TreeItemsEditor implements PropertyEditor
{
  public TreeItemsEditor() {
    editorPanel = new TreeItemsEditorPanel();
  }

  static final String NULLSTRING = "null";  

  public void setValue(Object o) {
    String[] sa = (String[])o;
    value = sa;
    if (sa != null && editorPanel != null) {
      editorPanel.tree.setItems(value);
      editorPanel.checkButtons();
    }
  }

  public Object getValue() {
    if (editorPanel != null) {
      TreeItems ti = new TreeItems(editorPanel.tree.getModel());
      value = ti.getItems();
    }
    return value;
  }

  public boolean isPaintable() {
    return false;
  }

  public void paintValue(Graphics g, Rectangle box) {
  }

  public String getAsText() {
    return stringArrayToText(value);
  }

  public String getJavaInitializationString() {
    return javaInitializationForArrayOfStrings(value);
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
          fsb.append("\n        ");  
        }
      }
    }
    fsb.append("}");
    return fsb.toString();
  }

  public void setAsText(String text) throws IllegalArgumentException {
    value = textToStringArray(text);
    editorPanel.tree.setItems(value);
    editorPanel.checkButtons();
  }

  public String[] getTags() {
    return null;
  }

  public Component getCustomEditor() {
    if (editorPanel == null) {
      editorPanel = new TreeItemsEditorPanel();
    }
    editorPanel.checkButtons();
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

  TreeItemsEditorPanel editorPanel;
  PropertyChangeListener listener;
  String[] value;

  private void fire() {
    if (listener != null)
      listener.propertyChange(new PropertyChangeEvent(this, "TreeItemsEditor???", null, value));  
  }

  private static final String stringArrayToText(String[] array) {
    FastStringBuffer fsb = new FastStringBuffer("");
    if (array != null)
      for (int i = 0; i < array.length; i++)
        fsb.append(array[i] + "\n");
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
