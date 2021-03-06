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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.StringTokenizer;

import com.borland.jbcl.model.MatrixLocation;

public class MatrixLocationEditor implements PropertyEditor {

  public void setValue(Object o) {
    value = (MatrixLocation)o;
    fire();
  }

  public Object getValue() {
    return value;
  }

  public boolean isPaintable() {
    return false;
  }

  public void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box) {
    // Silent noop.
  }

  public String getAsText() {
    if (value == null)
      return null;
    return String.valueOf(value.row) + ", " + value.column;  
  }

  public String getJavaInitializationString() {
    if (value == null)
      return null;
    return "new com.borland.jbcl.model.MatrixLocation(" + value.row + ", " + value.column + ")";  
  }

  public void setAsText(String text) throws java.lang.IllegalArgumentException {
    StringTokenizer tokenizer = new StringTokenizer(text, ",");
    String[] argv = new String[tokenizer.countTokens()];
    int argc = 0;
    while (tokenizer.hasMoreTokens())
      argv[argc++] = tokenizer.nextToken().trim();

    if (argc != 2)
      throw new IllegalArgumentException();

    try {
      value = new MatrixLocation(Integer.parseInt(argv[0]), Integer.parseInt(argv[1]));
    }
    catch (NumberFormatException x) {
      throw new java.lang.IllegalArgumentException();
    }
  }

  public String[] getTags() {
    return null;
  }

  public java.awt.Component getCustomEditor() {
    return null;
  }

  public boolean supportsCustomEditor() {
    return false;
  }

  private void fire() {
    if (listener != null) {
      listener.propertyChange(new PropertyChangeEvent(this, "???", null/*???*/, value));  
    }
  }

  public void addPropertyChangeListener(PropertyChangeListener l) {
    listener = l;
  }

  public void removePropertyChangeListener(PropertyChangeListener l) {
    listener = null;
  }

  private PropertyChangeListener listener;
  private MatrixLocation value;
}
