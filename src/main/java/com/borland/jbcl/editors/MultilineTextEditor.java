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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.TextArea;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;

public class MultilineTextEditor implements PropertyEditor
{
  public void setValue(Object o) {
    if (o instanceof String)
      textEditor.setText((String)o);
  }

  public Object getValue() {
    return textEditor.getText();
  }

  public boolean isPaintable() {
    return false;
  }

  public void paintValue(Graphics g, Rectangle box) {
  }

  public String getAsText() {
    String s = textEditor.getText();
    return (s == null) ? null : StringEditor.rawTextToDisplay(s);
  }

  public String getJavaInitializationString() {
    return StringEditor.textToSource(textEditor.getText());
  }

  public void setAsText(String text) throws IllegalArgumentException {
    textEditor.setText((text == null) ? null : StringEditor.displayTextToRaw(text));
  }

  public String[] getTags() {
    return null;
  }

  public Component getCustomEditor() {
    if (panel == null) {
      panel = new Panel();
      panel.add(textEditor, BorderLayout.CENTER);
    }
    return panel;
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

  TextArea textEditor = new TextArea(10, 40);
  Panel panel;
  PropertyChangeListener listener;

  private void fire() {
    if (listener != null)
      listener.propertyChange(new PropertyChangeEvent(this, "MultilineTextEditor???", null, textEditor.getText()));  
  }
}
