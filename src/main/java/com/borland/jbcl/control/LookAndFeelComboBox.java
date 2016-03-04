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

package com.borland.jbcl.control;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * A very simple ComboBox that allows a user to select a LookAndFeel at runtime.
 */
public class LookAndFeelComboBox extends JComboBox implements com.borland.jbcl.util.BlackBox
{
  protected UIManager.LookAndFeelInfo[] lafi = UIManager.getInstalledLookAndFeels();

  public LookAndFeelComboBox() {
    setEditable(false);
    changingItem = true;
    for (int i = 0; i < lafi.length; i++)
      addItem(lafi[i].getName());
    changingItem = false;
  }

  public void addNotify() {
    super.addNotify();
    lafi = UIManager.getInstalledLookAndFeels();
    removeAllItems();
    changingItem = true;
    for (int i = 0; i < lafi.length; i++)
      addItem(lafi[i].getName());
    setSelectedItem(UIManager.getLookAndFeel().getName());
    changingItem = false;
  }

  boolean changingItem = false;
  public void updateUI() {
    super.updateUI();
    changingItem = true;
    setSelectedItem(UIManager.getLookAndFeel().getName());
    changingItem = false;
  }

  protected Component findTopParent() {
    Component c = this;
    Component p = getParent();
    while (p != null) {
      c = p;
      p = c.getParent();
    }
    return c;
  }

  protected void selectedItemChanged() {
    super.selectedItemChanged();
    if (changingItem)
      return;
    int index = getSelectedIndex();
    if (index >= 0 && index < lafi.length) {
      try {
        UIManager.setLookAndFeel(lafi[index].getClassName());
      }
      catch (Exception x) {
        x.printStackTrace();
        changingItem = true;
        setSelectedItem(UIManager.getLookAndFeel().getName());
        changingItem = false;
        return;
      }
      SwingUtilities.updateComponentTreeUI(findTopParent());
    }
  }
}
