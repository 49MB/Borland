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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;

public class LookAndFeelEditor implements PropertyEditor, java.io.Serializable
{
  public LookAndFeelEditor() {
    lookAndFeel = UIManager.getLookAndFeel();
  }

  public void setValue(Object value) {
    if (value instanceof LookAndFeel)
      lookAndFeel = (LookAndFeel)value;
  }

  public Object getValue() {
    return lookAndFeel;
  }

  public boolean isPaintable() { return false; }
  public void paintValue(Graphics g, Rectangle rect) {}

  public String getJavaInitializationString() {
    return "new " + lookAndFeel.getClass().getName() + "()"; 
  }

  public String getAsText() {
    return lookAndFeel.getName();
  }

  public void setAsText(String text) throws IllegalArgumentException {
    try {
      String className = getClassNameFromLAFName(text);
      lookAndFeel = (LookAndFeel)Class.forName(className).newInstance();
    }
    catch (Exception x) {
      throw new IllegalArgumentException(x.getMessage());
    }
  }

  public String[] getTags() {
    String[] tags = new String[list.length];
    for (int i = 0; i < list.length; i++)
      tags[i] = list[i].getName();
    return tags;
  }

  public boolean supportsCustomEditor() { return false; }
  public Component getCustomEditor() { return null; }

  public void addPropertyChangeListener(PropertyChangeListener l) {
    propertyChanges.addPropertyChangeListener(l);
  }
  public void removePropertyChangeListener(PropertyChangeListener l) {
    propertyChanges.removePropertyChangeListener(l);
  }

  protected String getClassNameFromLAFName(String text) {
    for (int i = 0; i < list.length; i++) {
      String name = list[i].getName();
      if (name == text || name != null & name.equals(text))
        return list[i].getClassName();
    }
    return text;
  }

  protected LookAndFeel lookAndFeel;
  protected UIManager.LookAndFeelInfo[] list = UIManager.getInstalledLookAndFeels();
  protected PropertyChangeSupport propertyChanges = new PropertyChangeSupport(this);
}
