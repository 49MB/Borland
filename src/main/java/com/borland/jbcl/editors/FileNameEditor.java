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

import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;

import com.borland.jb.util.FastStringBuffer;

public class FileNameEditor implements PropertyEditor
{
  public String getJavaInitializationString() {
//    System.err.println("FileNameEditor.getJavaInitializationString() for " + value);
    return FastStringBuffer.textToSource(value, false).toString();
  }

  public java.awt.Component getCustomEditor() {
    if (editor == null)
      editor =  new FileNameCustomEditor(value);
    return editor;
  }

  public boolean supportsCustomEditor() {
    return true;
  }

  public Object getValue() {
    if (editor != null) {
       value   = editor.textField1.getText();
    }
//    System.err.println("FileNameEditor.getValue()");
    return value;
  }

  public void setValue(Object parm1) {
    value = (String)parm1;
    if (editor != null)
      editor.textField1.setText(value);
    fire();
  }

  public boolean isPaintable() {
    return false;
  }

  public void paintValue(Graphics g, Rectangle rect) {}

  public String getAsText() {
    return value;
  }

  public void setAsText(String text) {
    value = text;
  }

  public String[] getTags() {
    return null;
  }

  public void addPropertyChangeListener(PropertyChangeListener l) {
    listener = l;
  }

  public void removePropertyChangeListener(PropertyChangeListener l) {
    listener = null;
  }

  private void fire() {
    if (listener != null) {
      listener.propertyChange(new PropertyChangeEvent(this, "???", null/*???*/, value));
    }
  }
  private String value;
  private PropertyChangeListener listener;
  FileNameCustomEditor editor;
}
