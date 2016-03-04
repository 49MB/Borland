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

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.FocusListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.Vector;

import com.borland.jbcl.util.BlackBox;
import com.borland.jbcl.util.FocusMulticaster;
import com.borland.jbcl.util.ImageLoader;
import com.borland.jbcl.util.ItemMulticaster;
import com.borland.jbcl.util.KeyMulticaster;
import com.borland.jbcl.util.MouseMulticaster;
import com.borland.jbcl.util.Orientation;

@SuppressWarnings("serial")
public class CheckboxPanel extends BevelPanel implements BlackBox,
    java.io.Serializable {
  public CheckboxPanel() {
    super.setLayout(gridLayout);
    setMargins(new Insets(6, 6, 6, 6));
    setBevelInner(RAISED);
    setBevelOuter(LOWERED);
    setOrientation(Orientation.VERTICAL);
  }
  
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    for (int i = 0; i < getComponentCount(); i++)
      getComponent(i).setEnabled(enabled);
  }
  
  public void setBackground(Color color) {
    super.setBackground(color);
    for (int i = 0; i < getComponentCount(); i++) {
      getComponent(i).setBackground(color);
    }
  }
  
  public void setForeground(Color color) {
    super.setForeground(color);
    for (int i = 0; i < getComponentCount(); i++) {
      getComponent(i).setForeground(color);
    }
  }
  
  public void setFont(Font font) {
    super.setFont(font);
    for (int i = 0; i < getComponentCount(); i++) {
      getComponent(i).invalidate();
    }
  }
  
  public void setOrientation(int o) {
    if (orientation != o) {
      orientation = o;
      gridLayout.setColumns(1); // do this so we don't hit 0,0
      gridLayout.setRows(orientation == Orientation.HORIZONTAL ? 1 : 0);
      gridLayout.setColumns(orientation == Orientation.HORIZONTAL ? 0 : 1);
      invalidate();
      repaint(100);
    }
  }
  
  public int getOrientation() {
    return orientation;
  }
  
  public void setGrouped(boolean grouped) {
    if (grouped != (group != null)) {
      group = grouped ? new CheckboxGroup() : null;
      for (int i = 0; i < getComponentCount(); i++)
        ((Checkbox) getComponent(i)).setCheckboxGroup(group);
    }
    if (grouped && (groupSelectedIndex > 0 || groupSelectedLabel != null)) {
      if (groupSelectedIndex > 0)
        setSelectedIndex(groupSelectedIndex);
      else
        setSelectedLabel(groupSelectedLabel);
    }
  }
  
  public void setTextureName(String path) {
    if (path != null && !path.equals("")) {
      Image i = ImageLoader.load(path, this);
      if (i != null) {
        ImageLoader.waitForImage(this, i);
        textureName = path;
        setTexture(i);
      } else {
        throw new IllegalArgumentException(path);
      }
    } else {
      textureName = null;
      setTexture(null);
    }
  }
  
  public String getTextureName() {
    return textureName;
  }
  
  public void setLayout(LayoutManager layout) {
    // throw new IllegalArgumentException(Res._LayoutNotSupported);
  }
  
  public boolean isGrouped() {
    return group != null;
  }
  
  public void setLabels(String[] labels) {
    int count = getComponentCount();
    for (int c = 0; c < count; c++) {
      Checkbox cbox = (Checkbox) getComponent(c);
      cbox.removeItemListener(itemMulticaster);
      cbox.removeFocusListener(focusMulticaster);
      cbox.removeKeyListener(keyMulticaster);
      cbox.removeMouseListener(mouseMulticaster);
    }
    removeAll();
    if (labels != null) {
      for (int i = 0; i < labels.length; i++) {
        Checkbox cb = new Checkbox(labels[i], false, group);
        cb.addItemListener(itemMulticaster);
        cb.addFocusListener(focusMulticaster);
        cb.addKeyListener(keyMulticaster);
        cb.addMouseListener(mouseMulticaster);
        cb.setEnabled(isEnabled());
        add(cb);
      }
    }
    repaint(100);
  }
  
  public String[] getLabels() {
    String[] labels = new String[getComponentCount()];
    for (int i = 0; i < labels.length; i++)
      labels[i] = ((Checkbox) getComponent(i)).getLabel();
    return labels;
  }
  
  public void setSelectedLabels(String[] selectedLabels) {
    Vector<String> sLabs = new Vector<String>();
    synchronized (selectedLabels) {
      for (int i = 0, count = selectedLabels.length; i < count; i++) {
        sLabs.addElement(selectedLabels[i]);
      }
    }
    int count = getComponentCount();
    for (int i = 0; i < count; i++) {
      Checkbox cb = (Checkbox) getComponent(i);
      cb.setState(sLabs.contains(cb.getLabel()));
    }
  }
  
  public String[] getSelectedLabels() {
    String[] labels = new String[getComponentCount()];
    int selCount = 0;
    for (int i = 0; i < labels.length; i++) {
      Checkbox cb = (Checkbox) getComponent(i);
      if (cb.getState()) {
        selCount++;
        labels[i] = cb.getLabel();
      }
    }
    String[] selectedLabels = new String[selCount];
    if (selCount > 0) {
      int count = 0;
      for (int i = 0; i < labels.length; i++) {
        if (labels[i] != null)
          selectedLabels[count++] = labels[i];
      }
    }
    return selectedLabels;
  }
  
  public void setLabel(int index, String label) {
    ((Checkbox) getComponent(index)).setLabel(label);
  }
  
  public void addLabel(String label) {
    Checkbox cb = new Checkbox(label, false, group);
    cb.addItemListener(itemMulticaster);
    cb.addFocusListener(focusMulticaster);
    cb.addKeyListener(keyMulticaster);
    cb.addMouseListener(mouseMulticaster);
    cb.setEnabled(isEnabled());
    add(cb);
    repaint(100);
  }
  
  public String getLabel(int index) {
    return ((Checkbox) getComponent(index)).getLabel();
  }
  
  /**
   * the index of the selected checkbox if grouped
   */
  public void setSelectedIndex(int index) {
    if (group != null)
      group.setSelectedCheckbox((Checkbox) getComponent(index));
    else
      groupSelectedIndex = index;
  }
  
  public int getSelectedIndex() {
    if (group != null) {
      Component cb = group.getSelectedCheckbox();
      for (int i = 0; i < getComponentCount(); i++)
        if (getComponent(i) == cb)
          return i;
    }
    return -1;
  }
  
  /**
   * Sets the selected page, as an object
   */
  public void setSelectedLabel(String label) {
    if (group != null) {
      for (int i = 0; i < getComponentCount(); i++) {
        Checkbox cb = (Checkbox) getComponent(i);
        if (cb.getLabel().equals(label)) {
          group.setSelectedCheckbox(cb);
          return;
        }
      }
    } else
      groupSelectedLabel = label;
  }
  
  public String getSelectedLabel() {
    if (group != null) {
      Checkbox scb = group.getSelectedCheckbox();
      if (scb != null)
        return scb.getLabel();
    }
    return null;
  }
  
  public void addFocusListener(FocusListener l) {
    // anti-hack for swing serialization hack
    if (focusMulticaster == null)
      super.addFocusListener(l);
    else
      focusMulticaster.add(l);
  }
  
  public void removeFocusListener(FocusListener l) {
    focusMulticaster.remove(l);
  }
  
  public void addKeyListener(KeyListener l) {
    keyMulticaster.add(l);
  }
  
  public void removeKeyListener(KeyListener l) {
    keyMulticaster.remove(l);
  }
  
  public void addItemListener(ItemListener l) {
    itemMulticaster.add(l);
  }
  
  public void removeItemListener(ItemListener l) {
    itemMulticaster.remove(l);
  }
  
  public void addMouseListener(MouseListener l) {
    mouseMulticaster.add(l);
  }
  
  public void removeMouseListener(MouseListener l) {
    mouseMulticaster.remove(l);
  }
  
  private int orientation = Orientation.HORIZONTAL;
  private CheckboxGroup group;
  private int groupSelectedIndex = -1;
  private String groupSelectedLabel;
  private GridLayout gridLayout = new GridLayout();
  private transient ItemMulticaster itemMulticaster = new ItemMulticaster();
  private transient FocusMulticaster focusMulticaster = new FocusMulticaster();
  private transient KeyMulticaster keyMulticaster = new KeyMulticaster();
  private transient MouseMulticaster mouseMulticaster = new MouseMulticaster();
  protected String textureName;
}
