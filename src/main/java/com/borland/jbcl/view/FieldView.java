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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JToolTip;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import com.borland.dx.dataset.CustomPaintSite;
import com.borland.dx.text.Alignment;
import com.borland.jbcl.model.BasicSingletonContainer;
import com.borland.jbcl.model.BasicViewManager;
import com.borland.jbcl.model.ItemEditSite;
import com.borland.jbcl.model.ItemEditor;
import com.borland.jbcl.model.ItemPainter;
import com.borland.jbcl.model.SingletonModel;
import com.borland.jbcl.model.SingletonModelEvent;
import com.borland.jbcl.model.SingletonModelListener;
import com.borland.jbcl.model.SingletonModelMulticaster;
import com.borland.jbcl.model.SingletonViewManager;
import com.borland.jbcl.model.ToggleItemEditor;
import com.borland.jbcl.model.WritableSingletonModel;
import com.borland.jbcl.util.KeyMulticaster;

public class FieldView extends BeanPanel implements SingletonView,
    SingletonModelListener, ItemEditSite, KeyListener, FocusListener,
    Serializable {
  private static final long serialVersionUID = 200L;
  
  public FieldView() {
    super(null);
    super.setBackground(UIManager.getColor("TextField.background"));
    super.setForeground(UIManager.getColor("TextField.foreground"));
    // super.setFont(UIManager.getFont("TextField.font")); // NORES
    super.setBorder(UIManager.getBorder("TextField.border"));
    super.addKeyListener(keyMulticaster);
    setModel(createDefaultModel());
    setViewManager(createDefaultViewManager());
  }
  
  public void updateUI() {
    super.updateUI();
    super.setBackground(UIManager.getColor("TextField.background"));
    super.setForeground(UIManager.getColor("TextField.foreground"));
    // super.setFont(UIManager.getFont("TextField.font")); // NORES
    super.setBorder(UIManager.getBorder("TextField.border"));
  }
  
  protected SingletonModel createDefaultModel() {
    return new BasicSingletonContainer();
  }
  
  protected SingletonViewManager createDefaultViewManager() {
    return new BasicViewManager(new FocusableItemPainter(
        new SelectableItemPainter(new TextItemPainter())), new TextItemEditor());
  }
  
  /**
   * The model property defines the SingletonModel that this field is displaying
   * data from. If the current model is an instance of WritableSingletonModel,
   * an external user can get access to it using getWriteModel().
   */
  public SingletonModel getModel() {
    return model;
  }
  
  public void setModel(SingletonModel sm) {
    safeEndEdit();
    if (model != null) {
      model.removeModelListener(this);
      model.removeModelListener(modelMulticaster);
    }
    model = sm;
    if (model != null) {
      model.addModelListener(this);
      model.addModelListener(modelMulticaster);
    }
    if (model instanceof WritableSingletonModel)
      writeModel = (WritableSingletonModel) model;
    else
      writeModel = null;
    repaint(100);
  }
  
  public WritableSingletonModel getWriteModel() {
    return readOnly ? null : writeModel;
  }
  
  public void addModelListener(SingletonModelListener l) {
    modelMulticaster.add(l);
  }
  
  public void removeModelListener(SingletonModelListener l) {
    modelMulticaster.remove(l);
  }
  
  /**
   * The readOnly property is used when the model property is actually a
   * WritableSingletonModel, but the user wishes it to be treated as a read-only
   * model. This is commonly used to allow users to view, but not edit normally
   * writable items of data.
   */
  public void setReadOnly(boolean ro) {
    readOnly = ro;
  }
  
  public boolean isReadOnly() {
    return readOnly ? true : writeModel == null;
  }
  
  /**
   * The editInPlace property enables/disables editing in the field. By default,
   * this property is true, and a user can edit the value in the field. If set
   * to false, the model data cannot by modified by the user - except through
   * programmatic access to the field's model.
   */
  public void setEditInPlace(boolean editInPlace) {
    this.editInPlace = editInPlace;
  }
  
  public boolean isEditInPlace() {
    return editInPlace;
  }
  
  /**
   * The postOnEndEdit property controls wether or not the field will post
   * changes to an item back to the model when the user clicks or tabs off of
   * the field. If false, editing the value will not 'stick' unless committed by
   * hitting ENTER - clicking off the field while editing will revert the value
   * back to its original state. By default, this property is set to true.
   */
  public boolean isPostOnEndEdit() {
    return postOnEndEdit;
  }
  
  public void setPostOnEndEdit(boolean post) {
    postOnEndEdit = post;
  }
  
  /**
   * The autoEdit property enables/disables automatic editing for the field. By
   * default, this property is true, and a user can edit the value at any time
   * by typing a character on the keyboard. If set to false, the user must hit
   * F2, Ctrl+Enter, or double click to start an edit session.
   */
  public void setAutoEdit(boolean edit) {
    autoEdit = edit;
  }
  
  public boolean isAutoEdit() {
    return autoEdit;
  }
  
  /**
   * The growEditor property enables/disables automatic sizing of an item's
   * ItemEditor. In some look & feel settings, the ItemEditors will need to grow
   * vertically in order to property edit the data. By default, this property is
   * set to true.
   */
  public void setGrowEditor(boolean growEditor) {
    this.growEditor = growEditor;
  }
  
  public boolean isGrowEditor() {
    return growEditor;
  }
  
  /**
   * The editing property (read only) returns true if the item is currently
   * being edited.
   */
  public boolean isEditing() {
    return editor != null;
  }
  
  /**
   * If 'editor' property (read-only) returns the current ItemEditor being used
   * - or null if the field is not currently editing.
   */
  public ItemEditor getEditor() {
    return editor;
  }
  
  /**
   * The 'postOnFocusLost' property controls wether or not the field will post
   * its value to the model when it loses focus. By default, this property is
   * true.
   */
  public boolean isPostOnFocusLost() {
    return postOnFocusLost;
  }
  
  public void setPostOnFocusLost(boolean post) {
    postOnFocusLost = post;
  }
  
  /**
   * The showFocus property enables/disables the painting of the focus rectangle
   * on the field. In reality, the showFocus property turns on/off the FOCUSED
   * bit in the state information that is passed to the ItemPainter when an item
   * is painted. If an ItemPainter plugged into the field ignores the FOCUSED
   * bit, this property will have no effect. By default, showFocus is true.
   */
  public void setShowFocus(boolean visible) {
    if (showFocus != visible) {
      showFocus = visible;
      repaint(100);
    }
  }
  
  public boolean isShowFocus() {
    return showFocus;
  }
  
  public void setFlat(boolean flat) {
    if (flat != this.flat) {
      this.flat = flat;
      if (flat)
        setBorder(null);
      else
        setBorder(UIManager.getBorder("TextField.border"));
      invalidate();
      repaint(100);
    }
  }
  
  public boolean isFlat() {
    return flat;
  }
  
  public Insets getItemMargins() {
    return margins;
  }
  
  public void setItemMargins(Insets margins) {
    this.margins = margins;
    repaint(100);
  }
  
  public int getAlignment() {
    return alignment;
  }
  
  public void setAlignment(int align) {
    alignment = align;
  }
  
  /**
   * The viewManager property defines the SingletonViewManager that will
   * 'broker' ItemPainters and ItemEditors to this field.
   */
  public void setViewManager(SingletonViewManager viewManager) {
    safeEndEdit();
    this.viewManager = viewManager;
    repaint();
  }
  
  public SingletonViewManager getViewManager() {
    return viewManager;
  }
  
  public boolean isSelectable() {
    return selectable;
  }
  
  public void setSelectable(boolean select) {
    selectable = select;
    if (!selectable) {
      state &= -ItemPainter.SELECTED;
      repaint(100);
    }
  }
  
  /**
   * The dataToolTip property enables/disables the automatic tooltip mechanism
   * to display the contents of the model (as text) in a tooltip window when the
   * mouse is floating over the field. By default, this property is false. If
   * set to true, the text stored in the toolTipText property is discarded.
   */
  public void setDataToolTip(boolean dataTip) {
    toolTip.active = dataTip;
    ToolTipManager ttm = ToolTipManager.sharedInstance();
    if (toolTip.active)
      ttm.registerComponent(this);
    else if (getToolTipText() == null)
      ttm.unregisterComponent(this);
  }
  
  public boolean isDataToolTip() {
    return toolTip.active;
  }
  
  public boolean isSelected() {
    return selectable && (state & ItemPainter.SELECTED) != 0;
  }
  
  public void setSelected(boolean selected) {
    if (selectable && selected)
      state |= ItemPainter.SELECTED;
    else
      state &= ~ItemPainter.SELECTED;
    repaint(100);
  }
  
  /**
   * The showRollover property enables/disables the repainting of the rollover
   * state. Rollover is when the mouse is floating over it the field. If an
   * ItemPainter plugged into the field ignores the ROLLOVER bit, this property
   * will have no effect. By default, showRollover is false.
   */
  public void setShowRollover(boolean showRollover) {
    this.showRollover = showRollover;
  }
  
  public boolean isShowRollover() {
    return showRollover;
  }
  
  protected String paramString() {
    return super.paramString() + ",selectable=" + selectable + ",state="
        + state;
  }
  
  public void update(Graphics g) {
    paint(g);
  }
  
  public Point getEditClickPoint() {
    return editClickPoint;
  }
  
  public boolean isTransparent() {
    return texture != null ? true : !isOpaque();
  }
  
  public Graphics getSiteGraphics() {
    Graphics g = getGraphics();
    if (g != null)
      g.setFont(getFont());
    return g;
  }
  
  public Component getSiteComponent() {
    return this;
  }
  
  /**
   * Starts an edit session. If editInPlace is false or if readOnly is true,
   * this method is a no-op.
   */
  public void startEdit() {
    if (model == null || viewManager == null || !editInPlace || isReadOnly()
        || !writeModel.canSet(true))
      return;
    Object data = model.get();
    editor = getEditor(data);
    if (editor != null) {
      Component editorComponent = editor.getComponent();
      if (editorComponent != null) {
        editorComponent.setVisible(false);
        add(editorComponent);
      }
      Rectangle r = getEditorRect();
      editor.addKeyListener(this);
      editor.addKeyListener(keyMulticaster);
      editor.startEdit(data, r, this);
      resyncEditor();
      if (editor != null && editor.getComponent() != null) // some ItemEditors
                                                           // do not know their
                                                           // component until
                                                           // after startEdit is
                                                           // called
        editor.getComponent().addFocusListener(this);
      editClickPoint = null;
    }
  }
  
  protected Rectangle getEditorRect() {
    Rectangle rect = null;
    if (editor != null) {
      rect = outerRect();
      if (rect != null) {
        if (growEditor) {
          Component c = editor.getComponent();
          if (c != null) {
            Dimension ps = c.getPreferredSize();
            if (ps.height > rect.height)
              rect.height = ps.height;
          }
        }
      }
    }
    return rect;
  }
  
  protected void resyncEditor() {
    if (editor != null) {
      Rectangle er = getEditorRect();
      editor.changeBounds(er != null ? er : new Rectangle());
    }
  }
  
  private boolean isToggleItem() {
    if (model == null || viewManager == null || !editInPlace)
      return false;
    Object data = model.get();
    ItemEditor ie = getEditor(data);
    if (ie instanceof ToggleItemEditor)
      return !isReadOnly()
          && ((ToggleItemEditor) ie).isToggle(data, new Rectangle(0, 0,
              getSize().width, getSize().height), this)
          && writeModel.canSet(false);
    else
      return false;
  }
  
  /**
   * Ends the current edit session (if any). If the value has been modified, it
   * will be posted if postOnEndEdit is set to true (the default). If
   * postOnEndEdit is false, the edit session will be terminated without saving
   * the changes to the item's value.
   */
  public void endEdit() throws Exception {
    endEdit(postOnEndEdit);
  }
  
  /**
   * Ends the current edit session (if any). If the value has been modified, it
   * will be posted if post is set to true. If post is false, the edit session
   * will be terminated without saving the changes to the item's value.
   */
  public void endEdit(boolean post) throws Exception {
    endEditFailed = false;
    ItemEditor editor = this.editor; // keep in local in case of reentrancy
    this.editor = null;
    if (editor != null) {
      boolean okToEnd = true;
      try {
        if (!post || (okToEnd = editor.canPost())) {
          if (post && okToEnd) {
            writeModel.set(editor.getValue());
          }
          if (okToEnd && editor != null) {
            Component editorComponent = editor.getComponent();
            editor.endEdit(post);
            editor.removeKeyListener(this);
            editor.removeKeyListener(keyMulticaster);
            if (editorComponent != null) {
              remove(editorComponent);
              editorComponent.removeFocusListener(this);
            }
            editClickPoint = null;
            editor = null;
            requestFocus();
          }
        }
      } catch (Exception x) {
        endEditFailed = true;
        this.editor = editor;
        throw x;
      }
    }
    this.editor = editor;
  }
  
  /**
   * Ends the current edit session (if any), catching any exceptions. If the
   * value has been modified, it will be posted if postOnEndEdit is set to true
   * (the default). If postOnEndEdit is false, the edit session will be
   * terminated without saving the changes to the item's value.
   */
  public void safeEndEdit() {
    safeEndEdit(postOnEndEdit);
  }
  
  /**
   * Ends the current edit session (if any), catching any exceptions. If the
   * value has been modified, it will be posted if post is set to true. If post
   * is false, the edit session will be terminated without saving the changes to
   * the item's value.
   */
  public void safeEndEdit(boolean post) {
    try {
      endEdit(post);
    } catch (Exception x) {
      // if (editor instanceof ExceptionHandler)
      // ((ExceptionHandler)editor).handleException(x);
      // else if (model instanceof ExceptionHandler)
      // ((ExceptionHandler)model).handleException(x);
    }
  }
  
  /**
   * @DEPRECATED - use getInnerRect()
   */
  public Rectangle getInnerRect(Graphics g) {
    return getInnerRect();
  }
  
  public Rectangle getInnerRect() {
    if (flat)
      return outerRect();
    Dimension outerSz = getSize();
    Insets bin = getBorder() != null ? getBorder().getBorderInsets(this)
        : new Insets(0, 0, 0, 0);
    Dimension innerSz = new Dimension(outerSz.width - bin.left - bin.right,
        outerSz.height - bin.top - bin.bottom);
    return new Rectangle(bin.left, bin.top, innerSz.width, innerSz.height);
  }
  
  private Rectangle outerRect() {
    Dimension outerSz = getSize();
    return new Rectangle(0, 0, outerSz.width, outerSz.height);
  }
  
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Dimension outerSz = getSize();
    Object data = model != null ? model.get() : null;
    ItemPainter painter = getPainter(data);
    Rectangle r = getInnerRect();
    if (painter != null) {
      g.setColor(getBackground());
      g.setFont(getFont());
      painter.paint(data, g, r, state, this);
    } else {
      g.setColor(Color.red);
      g.fillRect(r.x, r.y, r.width, r.height);
    }
  }
  
  // Key Events (on embedded editor)
  public void keyTyped(KeyEvent e) {
  }
  
  public void keyPressed(KeyEvent e) {
    // Diagnostic.trace(Trace.KeyEvents, "FieldView.editor.keyPressed : " + e);
    if (editor == null)
      return;
    switch (e.getKeyCode()) {
    case KeyEvent.VK_ENTER:
      safeEndEdit(true);
      if (!endEditFailed) {
        e.consume();
        fireActionEvent();
      }
      break;
    case KeyEvent.VK_ESCAPE:
      safeEndEdit(false);
      e.consume();
      break;
    case KeyEvent.VK_TAB:
      if (!e.isConsumed()) {
        Component c = getNextFocusableComponent();
        if (c != null)
          c.requestFocus();
      }
      break;
    }
  }
  
  public void keyReleased(KeyEvent e) {
  }
  
  // keyPressed on FieldView (not embedded editor)
  //
  protected void processKeyPressed(KeyEvent e) {
    // Diagnostic.trace(Trace.KeyEvents, "FieldView.KEY_PRESSED : " + e);
    switch (e.getKeyCode()) {
    case KeyEvent.VK_ENTER:
      if (e.isControlDown() && editor == null && !isReadOnly()
          && writeModel.canSet(false))
        startEdit();
      break;
    case KeyEvent.VK_F2:
      if (editor == null && !isToggleItem() && !isReadOnly()
          && writeModel.canSet(false))
        startEdit();
      break;
    case KeyEvent.VK_SPACE:
      if (isToggleItem()) {
        startEdit();
        e.consume();
      }
      break;
    case KeyEvent.VK_KANJI:
    case 0xE5: // VK_PROCESSKEY
      // ktien
      // For Asian keyboards: activate the edit, so that the IME
      // entry will take place in the edit window.
      if (editor == null && !isReadOnly() && writeModel.canSet(false))
        startEdit();
      break;
    }
  }
  
  // keyTyped on FieldView (not embedded editor)
  // This should only be printable characters...
  // ktien: Ctrl+Alt is Alt-GR: used on European
  // keyboards for printable accented characters,
  // so the test should be Alt XOR Ctrl.
  protected void processKeyTyped(KeyEvent e) {
    char kChar = e.getKeyChar();
    if (editor != null || !autoEdit || e.isConsumed() || isReadOnly()
        || kChar == 0 || kChar == '\t' || kChar == '\r' || kChar == '\n'
        || kChar == ' ' || kChar == 27
        || // ESCAPE
        isToggleItem() || (e.isAltDown() ^ e.isControlDown())
        || !writeModel.canSet(false))
      return;
    startEdit();
    if (editor != null) {
      Component eComp = editor.getComponent();
      if (eComp != null)
        eComp.dispatchEvent(e);
    }
  }
  
  // Focus Events
  
  public void focusGained(FocusEvent e) {
  }
  
  public void focusLost(FocusEvent e) {
    state &= ~ItemPainter.FOCUSED;
    
    if (/* !e.isTemporary() && */postOnFocusLost) {
      safeEndEdit(postOnEndEdit);
    }
  }
  
  protected void processFocusEvent(FocusEvent e) {
    super.processFocusEvent(e);
    switch (e.getID()) {
    case FocusEvent.FOCUS_GAINED:
      if (showFocus)
        state |= ItemPainter.FOCUSED;
      if (editor != null && editor.getComponent() != null)
        editor.getComponent().requestFocus();
      state &= ~ItemPainter.NOT_FOCUS_OWNER;
      repaint();
      break;
    case FocusEvent.FOCUS_LOST:
      state &= ~ItemPainter.FOCUSED;
      state |= ItemPainter.NOT_FOCUS_OWNER;
      repaint();
      break;
    }
  }
  
  // Mouse events
  
  protected void processMousePressed(MouseEvent e) {
    super.processMousePressed(e);
    if (e.isConsumed()) {
      return;
    }
    state &= ~ItemPainter.ROLLOVER;
    if (!e.isMetaDown()) {
      if (selectable) {
        if (isSelected()) {
          state &= -ItemPainter.SELECTED;
        } else {
          state |= ItemPainter.SELECTED;
        }
      }
      if ((state & ItemPainter.FOCUSED) != 0 && editor == null && !isReadOnly()
          && writeModel.canSet(false)) {
        editClickPoint = new Point(e.getX(), e.getY());
        startEdit();
        return;
      } else {
        state |= ItemPainter.FOCUSED;
      }
      if (isToggleItem()) {
        editClickPoint = new Point(e.getX(), e.getY());
        startEdit();
      }
    }
    repaint();
  }
  
  protected Dimension getPreferredInnerSize(Object data) {
    Graphics g = getSiteGraphics();
    ItemPainter painter = getPainter(data);
    Dimension size = new Dimension(0, 0);
    if (painter != null)
      size = painter.getPreferredSize(data, g, state, this);
    return size;
  }
  
  public JToolTip createToolTip() {
    return toolTip;
  }
  
  protected void processMouseEntered(MouseEvent e) {
    if (showRollover) {
      state |= ItemPainter.ROLLOVER;
      repaint();
    }
    if (toolTip.active) {
      Object data = model != null ? model.get() : null;
      Dimension size = getPreferredInnerSize(data);
      Rectangle r = getInnerRect();
      if (!r.contains(size.width, size.height)) {
        String text = data != null ? data.toString() : Res._NullData;
        setToolTipText(text);
        toolTip.data = data;
        toolTip.painter = getPainter(data);
        toolTip.state = state;
        return;
      }
      toolTip.painter = null;
      setToolTipText(null);
    }
  }
  
  public Point getToolTipLocation(MouseEvent e) {
    if (toolTip.active && getToolTipText(e) != null) {
      Rectangle r = getInnerRect();
      if (r != null && model != null) {
        Object data = model.get();
        toolTip.data = data;
        toolTip.painter = getPainter(data);
        toolTip.state = state;
        return new Point(r.x, r.y);
      }
    }
    toolTip.painter = null;
    return null;
  }
  
  protected void processMouseExited(MouseEvent e) {
    if (showRollover) {
      state &= ~ItemPainter.ROLLOVER;
      repaint();
    }
  }
  
  public boolean isEnabled() {
    return (state & ItemPainter.DISABLED) == 0;
  }
  
  public void setEnabled(boolean enabled) {
    if (enabled)
      state &= ~ItemPainter.DISABLED;
    else
      state |= ItemPainter.DISABLED;
    super.setEnabled(enabled);
  }
  
  protected ItemPainter getPainter(Object data) {
    ItemPainter painter = viewManager != null ? viewManager.getPainter(data,
        state) : null;
    if (painter != null && customizeListeners != null) {
      customPainter.setPainter(painter);
      fireCustomizeItemEvent(data, state, customPainter);
      return customPainter;
    }
    return painter;
  }
  
  protected ItemEditor getEditor(Object data) {
    ItemEditor editor = viewManager != null ? viewManager
        .getEditor(data, state) : null;
    if (editor != null && customizeListeners != null) {
      customEditor.setEditor(editor);
      fireCustomizeItemEvent(data, state, customEditor);
      return customEditor;
    }
    return editor;
  }
  
  public void modelContentChanged(SingletonModelEvent e) {
    if (editor != null)
      safeEndEdit(false);
    repaint();
  }
  
  public Dimension getPreferredSize() {
    Graphics g = getSiteGraphics();
    Object data = model != null ? model.get() : null;
    ItemPainter painter = getPainter(data);
    Dimension size;
    if (painter != null)
      size = painter.getPreferredSize(data, g, state, this);
    else
      size = new Dimension(0, 0);
    Insets bin = getBorder() != null ? getBorder().getBorderInsets(this)
        : new Insets(0, 0, 0, 0);
    size.width += bin.left + bin.right;
    size.height += bin.top + bin.bottom;
    if (preferredHeight > size.height)
      size.height = preferredHeight;
    if (preferredWidth > size.width)
      size.width = preferredWidth;
    return size;
  }
  
  public void doLayout() {
    if (editor != null) {
      Rectangle r = outerRect();
      editor.changeBounds(r);
    }
  }
  
  public void setPreferredHeight(int preferredHeight) {
    this.preferredHeight = preferredHeight;
  }
  
  public int getPreferredHeight() {
    return preferredHeight;
  }
  
  public void setPreferredWidth(int preferredWidth) {
    this.preferredWidth = preferredWidth;
  }
  
  public int getPreferredWidth() {
    return preferredWidth;
  }
  
  public void addKeyListener(KeyListener l) {
    keyMulticaster.add(l);
  }
  
  public void removeKeyListener(KeyListener l) {
    keyMulticaster.remove(l);
  }
  
  private void fireActionEvent() {
    Object item = model != null ? model.get() : null;
    String action = item != null ? item.toString() : "";
    processActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
        action));
  }
  
  protected void fireCustomizeItemEvent(Object data, int state,
      CustomPaintSite cps) {
    if (customizeListeners != null) {
      cps.reset();
      for (int i = 0; i < customizeListeners.size(); i++)
        ((CustomItemListener) customizeListeners.elementAt(i)).customizeItem(
            null, data, state, cps);
    }
  }
  
  public synchronized void addCustomItemListener(CustomItemListener l) {
    if (customizeListeners == null)
      customizeListeners = new Vector<CustomItemListener>();
    customizeListeners.addElement(l);
  }
  
  public synchronized void removeCustomItemListener(CustomItemListener l) {
    if (customizeListeners != null)
      customizeListeners.removeElement(l);
    if (customizeListeners.size() == 0)
      customizeListeners = null;
  }
  
  // Serialization support
  
  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    Hashtable<String, Object> hash = new Hashtable<String, Object>(2);
    if (model instanceof Serializable)
      hash.put("m", model);
    if (viewManager instanceof Serializable)
      hash.put("v", viewManager);
    s.writeObject(hash);
  }
  
  private void readObject(ObjectInputStream s) throws IOException,
      ClassNotFoundException {
    s.defaultReadObject();
    Hashtable<?, ?> hash = (Hashtable<?, ?>) s.readObject();
    Object data = hash.get("m");
    if (data instanceof SingletonModel)
      model = (SingletonModel) data;
    if (model instanceof WritableSingletonModel)
      writeModel = (WritableSingletonModel) model;
    data = hash.get("v");
    if (data instanceof SingletonViewManager)
      viewManager = (SingletonViewManager) data;
  }
  
  private transient SingletonModel model;
  private transient WritableSingletonModel writeModel;
  private transient SingletonViewManager viewManager;
  
  private boolean readOnly;
  private ItemEditor editor;
  private Point editClickPoint;
  private boolean selectable = false;
  private boolean postOnEndEdit = true;
  private boolean autoEdit = true;
  private boolean growEditor = true;
  private boolean editInPlace = true;
  private boolean showFocus = true;
  private boolean showRollover = false;
  private DataToolTip toolTip = new DataToolTip(this);
  private boolean flat = false;
  private int state;
  private Insets margins = new Insets(2, 2, 2, 2);
  private int alignment = Alignment.LEFT | Alignment.MIDDLE;
  private int preferredHeight = 20;
  private int preferredWidth = 100;
  private boolean endEditFailed = false;
  private transient KeyMulticaster keyMulticaster = new KeyMulticaster();
  private transient SingletonModelMulticaster modelMulticaster = new SingletonModelMulticaster();
  protected boolean postOnFocusLost = true;
  private transient CustomItemPainter customPainter = new CustomItemPainter();
  private transient CustomItemEditor customEditor = new CustomItemEditor();
  private transient Vector<CustomItemListener> customizeListeners;
}
