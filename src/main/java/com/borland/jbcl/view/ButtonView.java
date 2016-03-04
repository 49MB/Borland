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
package com.borland.jbcl.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.Border;

import com.borland.dx.text.Alignment;
import com.borland.jbcl.model.BasicSingletonContainer;
import com.borland.jbcl.model.BasicViewManager;
import com.borland.jbcl.model.ItemPaintSite;
import com.borland.jbcl.model.ItemPainter;
import com.borland.jbcl.model.SingletonModel;
import com.borland.jbcl.model.SingletonModelEvent;
import com.borland.jbcl.model.SingletonModelListener;
import com.borland.jbcl.model.SingletonModelMulticaster;
import com.borland.jbcl.model.SingletonViewManager;
import com.borland.jbcl.model.WritableSingletonModel;

public class ButtonView
     extends BeanPanel
  implements SingletonView, SingletonModelListener, ItemPaintSite, Serializable
{
  private static final long serialVersionUID = 200L;

  public ButtonView() {
    super(null);
    super.setBackground(UIManager.getColor("Button.background")); 
    super.setForeground(UIManager.getColor("Button.foreground")); 
    upBorder = BorderFactory.createRaisedBevelBorder();
    dnBorder = BorderFactory.createLoweredBevelBorder();
    setModel(createDefaultModel());
    setViewManager(createDefaultViewManager());
  }

  public void updateUI() {
    super.updateUI();
    super.setBackground(UIManager.getColor("Button.background")); 
    super.setForeground(UIManager.getColor("Button.foreground")); 
    upBorder = BorderFactory.createRaisedBevelBorder();
    dnBorder = BorderFactory.createLoweredBevelBorder();
  }

  protected SingletonModel createDefaultModel() {
    return new BasicSingletonContainer();
  }

  protected SingletonViewManager createDefaultViewManager() {
    return new BasicViewManager(new FocusableItemPainter(new TextItemPainter()));
  }

  public void setItemMargins(Insets margins) {
    this.margins = margins;
  }
  public Insets getItemMargins() {
    return margins;
  }

  // remainder of ItemPaintSite
  public boolean isTransparent() { return texture != null ? true : !isOpaque(); }
  public Component getSiteComponent() { return this; }

  // properties

  /**
   * @DEPRECATED
   * This is a ButtonControl property
   */
  public String getLabel() {
    Object contents = model != null ? model.get() : null;
    return contents != null ? contents.toString() : "";
  }
  /**
   * @DEPRECATED
   * This is a ButtonControl property
   */
  public void setLabel(String label) {
    if (!isReadOnly()) {
      writeModel.set(label);
      repaint(100);
    }
  }

  public void setFocusAware(boolean aware) {
    super.setFocusAware(aware);
    repaint(100);
  }

  public boolean isFocusAware() {
    return super.isFocusAware();
  }

  public void setAlignment(int alignment) {
    this.alignment = alignment;
  }
  public int getAlignment() {
    return alignment;
  }

  /**
   * The showRollover property enables/disables the repainting of the rollover state.
   * Rollover is when the mouse is floating over it the field.
   * If an ItemPainter plugged into the field ignores the ROLLOVER bit, this property will
   * have no effect.  By default, showRollover is false.
   */
  public void setShowRollover(boolean showRollover) {
    this.showRollover = showRollover;
//    borderPainter.setShowRollover(showRollover);
    repaint(100);
  }
  public boolean isShowRollover() { return showRollover; }

  public boolean isSelected() {
    return (state&ItemPainter.SELECTED) != 0;
  }
  public void setSelected(boolean selected) {
    if (selected)
      this.state |= ItemPainter.SELECTED;
    else
      this.state &= ~ItemPainter.SELECTED;

    // code to process exclusive-selection for sticky buttons...
/*
    if (selected) {
      Container container = getParent();
      if (container != null) {
        int nMembers = container.countComponents();
        for (int i = 0; i < nMembers; i++) {
          Component component = container.getComponent(i);
          if (component != this && component instanceof ImageButton) {
            ImageButton other = (ImageButton) component;
            if (other.getSelected())
              other.setSelected(false);
          }
        }
      }
    }
*/
    repaint(100);
  }

  public void setEnabled(boolean enable) {
    if (isEnabled() != enable) {
      if (enable)
        state &= ~ItemPainter.DISABLED;
      else {
        state |= ItemPainter.DISABLED;
        state &= ~ItemPainter.ROLLOVER;
        state &= ~ItemPainter.SELECTED;
      }
      super.setEnabled(enable);
      repaint(100);
    }
  }

  public void setModel(SingletonModel sm) {
    if (model != null) {
      model.removeModelListener(this);
      model.removeModelListener(modelMulticaster);
    }
    model = sm;
    if (model != null) {
      model.addModelListener(this);
      model.addModelListener(modelMulticaster);
    }
    writeModel = (model instanceof WritableSingletonModel) ? (WritableSingletonModel)model : null;
    repaint(100);
  }
  public SingletonModel getModel() {
    return model;
  }
  public WritableSingletonModel getWriteModel() {
    return readOnly ? null : writeModel;
  }

  public void addModelListener(SingletonModelListener l) { modelMulticaster.add(l); }
  public void removeModelListener(SingletonModelListener l) { modelMulticaster.remove(l); }

  public void setReadOnly(boolean ro) {
    readOnly = ro;
  }
  public boolean isReadOnly() {
    return readOnly ? true : writeModel == null;
  }

  public SingletonViewManager getViewManager() {
    return viewManager;
  }
  public void setViewManager(SingletonViewManager vm) {
    viewManager = vm;
  }

  public void modelContentChanged(SingletonModelEvent e) {
    repaint(100);
  }

  /**
   * Sets the command name of the action event fired by this button.
   * By default this will be set to the label of the button.
   */
  public void setActionCommand(String command) {
    actionCommand = command;
  }

  /**
   * Returns the command name of the action event fired by this button.
   */
  public String getActionCommand() {
    return actionCommand;
  }

  // overriden Component methods

  public Dimension getPreferredSize() {
    Graphics g = getGraphics();
    Border b = (state & ItemPainter.SELECTED) != 0 ? dnBorder : upBorder;
    Insets bin = b.getBorderInsets(this);
    if (model != null && viewManager != null) {
      Object contents = model.get();
      if (contents != null) {
        Dimension innerSize = viewManager.getPainter(contents, state|focusState).getPreferredSize(contents, g, state|focusState, this);
        return new Dimension(innerSize.width + bin.left + bin.right, innerSize.height + bin.top + bin.bottom);
      }
    }
    return new Dimension(bin.left + bin.right + 20, bin.top + bin.bottom + 10);
  }

  // TODO. This gets Navigator buttons to repaint properly after a resize.
  public void setBounds(int x, int y, int width, int height) {
    super.setBounds(x, y, width, height);
    // Had to be slow for vcd - vcd would hang sometimes when
    // adding other components to a frame/panel that had a navigator
    // in it.
    //
    repaint(300);
  }

  public void update(Graphics g) {
    paint(g);
  }

  public void paintBorder(Graphics g) {
    if ((state & ItemPainter.SELECTED) != 0)
      dnBorder.paintBorder(this, g, 0, 0, getSize().width, getSize().height);
    else if (!(showRollover && (state & ItemPainter.ROLLOVER) == 0))
      upBorder.paintBorder(this, g, 0, 0, getSize().width, getSize().height);
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Dimension outerSize = getSize();

    Insets bin = new Insets(0,0,0,0);
    if ((state & ItemPainter.SELECTED) != 0)
      bin = dnBorder.getBorderInsets(this);
    else if (!(showRollover && (state & ItemPainter.ROLLOVER) == 0))
      bin = upBorder.getBorderInsets(this);

    Dimension innerSize = new Dimension(outerSize.width - bin.left - bin.right, outerSize.height - bin.top - bin.bottom);

    Object contents = model != null ? model.get() : null;

    ItemPainter painter = viewManager != null ? viewManager.getPainter(contents, state|focusState) : null;
    if (painter != null) {
      Rectangle r = new Rectangle(bin.left, bin.top, innerSize.width, innerSize.height);
      Rectangle clip = g.getClipBounds();
      g.clipRect(r.x, r.y, r.width, r.height);
      g.setColor((state & ItemPainter.SELECTED) != 0 ? UIManager.getColor("Button.pressed") : getBackground());  
      g.setFont(getFont());
      painter.paint(contents, g, r, state|focusState, this);
      g.setClip(clip);
    }
  }

  public void setVisible(boolean visible) {
    if (!visible) {
      mouseDown = false;
      setSelected(false);
    }
    super.setVisible(visible);
  }

  // Events

  protected void processFocusEvent(FocusEvent e) {
    super.processFocusEvent(e);
    if (e.getID() == e.FOCUS_LOST)
      state &= ~ItemPainter.SELECTED;
    if (focusAware)
      repaint(100);
  }

  protected void processKeyPressed(KeyEvent e) {
    switch (e.getKeyCode()) {
      case KeyEvent.VK_SPACE:
        state |= ItemPainter.SELECTED;
        repaint();
        break;
    }
  }

  protected void processKeyReleased(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
      state &= ~ItemPainter.SELECTED;
      repaint();
      processActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getActionCommand()));
    }
  }

  protected void processMousePressed(MouseEvent e) {
    super.processMousePressed(e);
    if (!e.isConsumed()) {
      state |= ItemPainter.SELECTED;
      state &= ~ItemPainter.ROLLOVER;
      mouseDown = true;
      mouseOver = true;
      repaint();
    }
  }

  protected void processMouseReleased(MouseEvent e) {
    super.processMouseReleased(e);
    if (!e.isConsumed()) {
      dragging = false;
      boolean action = (state & ItemPainter.SELECTED) != 0;
      state &= ~ItemPainter.SELECTED;
      mouseDown = false;
      if (mouseOver && showRollover)
        state |= ItemPainter.ROLLOVER;
      repaint();
      if (action)
        processActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getActionCommand()));
    }
  }

  boolean dragOut = false;
  boolean dragging = false;
  protected void processMouseDragged(MouseEvent e) {
    super.processMouseDragged(e);
    if (!e.isConsumed()) {
      state &= ~ItemPainter.ROLLOVER;
      dragging = true;
      if (!contains(e.getX(), e.getY())) {
        if (!dragOut) {
          dragOut = true;
          mouseOver = false;
          if (mouseDown) {
            state &= ~ItemPainter.SELECTED;
            repaint();
          }
        }
      }
      else if (dragOut) {
        dragOut = false;
        mouseOver = true;
        if (mouseDown) {
          state |= ItemPainter.SELECTED;
          repaint();
        }
      }
    }
  }

  protected void processMouseEntered(MouseEvent e) {
    super.processMouseEntered(e);
    if (!e.isConsumed()) {
      if (dragging)
        return;
      mouseOver = true;
      boolean paint = false;
      if (mouseDown) {
        state |= ItemPainter.SELECTED;
        paint = true;
      }
      if (showRollover) {
        state |= ItemPainter.ROLLOVER;
        paint = true;
      }
      if (paint)
        repaint();
    }
  }

  protected void processMouseExited(MouseEvent e) {
    super.processMouseExited(e);
    if (!e.isConsumed()) {
      if (dragging)
        return;
      mouseOver = false;
      boolean paint = false;
      if (mouseDown) {
        state &= ~ItemPainter.SELECTED;
        paint = true;
      }
      if (showRollover && !mouseDown) {
        state &= ~ItemPainter.ROLLOVER;
        paint = true;
      }
      if (paint)
        repaint();
    }
  }

  protected String paramString() {
    return super.paramString() + ",label=" + getLabel() + ",state=" + state;  
  }

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    Hashtable hash = new Hashtable(2);
    if (model instanceof Serializable)
      hash.put("m", model); 
    if (viewManager instanceof Serializable)
      hash.put("v", viewManager); 
    s.writeObject(hash);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    Hashtable hash = (Hashtable)s.readObject();
    Object data = hash.get("m"); 
    if (data instanceof SingletonModel)
      model = (SingletonModel)data;
    if (model instanceof WritableSingletonModel)
      writeModel = (WritableSingletonModel)model;
    data = hash.get("v"); 
    if (data instanceof SingletonViewManager)
      viewManager = (SingletonViewManager)data;
  }

  private transient SingletonModel         model;
  private transient WritableSingletonModel writeModel;
  private transient SingletonViewManager   viewManager;

  private boolean readOnly;
  private transient SingletonModelMulticaster modelMulticaster = new SingletonModelMulticaster();
  private boolean showRollover = false;
  protected Insets margins = new Insets(2, 5, 2, 5);

  protected String            actionCommand;
  protected int               state;
  protected int               alignment = Alignment.CENTER | Alignment.MIDDLE;
  protected boolean           mouseDown;
  protected boolean           mouseOver;

  protected Border upBorder = BorderFactory.createRaisedBevelBorder();
  protected Border dnBorder = BorderFactory.createLoweredBevelBorder();
}
