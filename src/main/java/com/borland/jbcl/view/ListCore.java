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
import java.awt.Font;
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

import javax.swing.JScrollPane;
import javax.swing.JToolTip;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import com.borland.dx.dataset.CustomPaintSite;
import com.borland.dx.text.Alignment;
import com.borland.jb.util.Diagnostic;
import com.borland.jb.util.EventMulticaster;
import com.borland.jb.util.Trace;
import com.borland.jbcl.model.BasicVectorSelection;
import com.borland.jbcl.model.ItemEditSite;
import com.borland.jbcl.model.ItemEditor;
import com.borland.jbcl.model.ItemPainter;
import com.borland.jbcl.model.NullVectorSelection;
import com.borland.jbcl.model.SubfocusEvent;
import com.borland.jbcl.model.ToggleItemEditor;
import com.borland.jbcl.model.VectorModel;
import com.borland.jbcl.model.VectorModelEvent;
import com.borland.jbcl.model.VectorModelListener;
import com.borland.jbcl.model.VectorModelMulticaster;
import com.borland.jbcl.model.VectorSelectionEvent;
import com.borland.jbcl.model.VectorSelectionListener;
import com.borland.jbcl.model.VectorSelectionMulticaster;
import com.borland.jbcl.model.VectorSubfocusEvent;
import com.borland.jbcl.model.VectorSubfocusListener;
import com.borland.jbcl.model.VectorViewManager;
import com.borland.jbcl.model.WritableVectorModel;
import com.borland.jbcl.model.WritableVectorSelection;
import com.borland.jbcl.util.ImageTexture;
import com.borland.jbcl.util.KeyMulticaster;
import com.borland.jbcl.util.SelectFlags;

     class ListCore
   extends BeanPanel
implements ItemEditSite, KeyListener, FocusListener,
           VectorModelListener, VectorSelectionListener, VectorView, Serializable
{
  private static final long serialVersionUID = 200L;

  public ListCore(JScrollPane host) {
    super();
    setLayout(null);
    scroller = host;
    super.addKeyListener(keyMulticaster);
    scroller.getVerticalScrollBar().setUnitIncrement(20);
    scroller.getHorizontalScrollBar().setUnitIncrement(10);
    super.setBackground(UIManager.getColor("List.background")); 
    super.setForeground(UIManager.getColor("List.foreground")); 
  }

  public void updateUI() {
    super.updateUI();
    setBackground(UIManager.getColor("List.background")); 
    setForeground(UIManager.getColor("List.foreground")); 
  }

  public boolean isPostOnEndEdit() { return postOnEndEdit; }
  public void setPostOnEndEdit(boolean post) { postOnEndEdit = post; }

  public void setAutoEdit(boolean edit) { autoEdit = edit; }
  public boolean isAutoEdit() { return autoEdit; }

  public void setGrowEditor(boolean growEditor) { this.growEditor = growEditor; }
  public boolean isGrowEditor() { return growEditor; }

  public void setAutoAppend(boolean autoAppend) { this.autoAppend = autoAppend; }
  public boolean isAutoAppend() { return autoAppend; }

  public VectorModel getModel() { return model; }
  public WritableVectorModel getWriteModel() { return readOnly ? null : writeModel; }
  public void setModel(VectorModel vm) {
    if (model != null) {
      model.removeModelListener(this);
      model.removeModelListener(modelMulticaster);
    }
    model = vm;
    if (model != null) {
      model.addModelListener(this);
      model.addModelListener(modelMulticaster);
    }
    if (model instanceof WritableVectorModel)
      writeModel = (WritableVectorModel)model;
    else
      writeModel = null;
    invalidate();
    repaintItems();
    if (isShowing() && !batchMode)
      scroller.validate();
  }

  public void addModelListener(VectorModelListener l) { modelMulticaster.add(l); }
  public void removeModelListener(VectorModelListener l) { modelMulticaster.remove(l); }

  public boolean isReadOnly() { return readOnly ? true : writeModel == null; }
  public void setReadOnly(boolean ro) { readOnly = ro; }

  public boolean isShowFocus() { return showFocus; }
  public void setShowFocus(boolean focus) {
    showFocus = focus;
    repaintItem(subfocus);
  }

  public void setShowRollover(boolean showRollover) { this.showRollover = showRollover; }
  public boolean isShowRollover() { return showRollover; }

  public void setDataToolTip(boolean dataTip) {
    toolTip.active = dataTip;
    ToolTipManager ttm = ToolTipManager.sharedInstance();
    if (toolTip.active)
      ttm.registerComponent(this);
    else if (getToolTipText() == null)
      ttm.unregisterComponent(this);
  }
  public boolean isDataToolTip() { return toolTip.active; }

  public boolean isSnapOrigin() { return snapOrigin; }
  public void setSnapOrigin(boolean snapOrigin) { this.snapOrigin = snapOrigin; }

  public boolean isEditInPlace() { return editInPlace; }
  public void setEditInPlace(boolean editInPlace) { this.editInPlace = editInPlace; }

  public boolean isEditing() { return editor != null; }

  public ItemEditor getEditor() { return editor; }

  public void setBatchMode(boolean batchMode) {
    if (this.batchMode != batchMode) {
      this.batchMode = batchMode;
      if (!this.batchMode) {
        repaintItems();
        if (isShowing())
          scroller.validate();
      }
    }
  }
  public boolean isBatchMode() { return batchMode; }

  public void setDragSubfocus(boolean dragSubfocus) { this.dragSubfocus = dragSubfocus; }
  public boolean isDragSubfocus() { return dragSubfocus; }

  public boolean isUniformWidth() { return uniformWidth; }
  public void setUniformWidth(boolean newUniformWidth) {
    uniformWidth = newUniformWidth;
    invalidate();
    if (scroller.isShowing() && !batchMode)
      scroller.validate();
  }

  public int getItemWidth() { return itemWidth; }
  public void setItemWidth(int newWidth) {
    uniformWidth = (newWidth > 0) ? true : false;
    itemWidth = newWidth;
    invalidate();
    if (scroller.isShowing() && !batchMode)
      scroller.validate();
  }

  public boolean isUniformHeight() { return uniformHeight; }
  public void setUniformHeight(boolean newUniformHeight) {
    uniformHeight = newUniformHeight;
    invalidate();
    if (scroller.isShowing() && !batchMode)
      scroller.validate();
  }

  public int getItemHeight() { return itemHeight; }
  public void setItemHeight(int newHeight) {
    uniformHeight = (newHeight > 0) ? true : false;
    itemHeight = newHeight;
    invalidate();
    if (scroller.isShowing() && !batchMode)
      scroller.validate();
  }

  public VectorViewManager getViewManager() { return viewManager; }
  public void setViewManager(VectorViewManager vvm) {
    viewManager = vvm;
    invalidate();
    repaintItems();
    if (isShowing() && !batchMode)
      scroller.validate();
  }

  public WritableVectorSelection getSelection() { return selection; }
  public void setSelection(WritableVectorSelection vs) {
    if (selection != null) {
      selection.removeSelectionListener(this);
      selection.removeSelectionListener(selectionMulticaster);
    }
    selection = vs;
    if (selection != null) {
      selection.addSelectionListener(this);
      selection.addSelectionListener(selectionMulticaster);
    }
    repaintItems();
  }

  public void addSelectionListener(VectorSelectionListener l) { selectionMulticaster.add(l); }
  public void removeSelectionListener(VectorSelectionListener l) { selectionMulticaster.remove(l); }

  public Insets getItemMargins() { return itemMargins; }
  public void setItemMargins(Insets margins) {
    itemMargins = margins;
    invalidate();
    repaintItems();
  }

  public int getAlignment() { return alignment; }
  public void setAlignment(int align) {
    alignment = align;
    repaintItems();
  }

  public void setBackground(Color color) {
    super.setBackground(color);
    repaintItems();
  }

  public void setForeground(Color color) {
    super.setForeground(color);
    repaintItems();
  }

  public void setFont(Font font) {
    super.setFont(font);
    invalidate();
    repaintItems();
  }

  public int getTopIndex() {
    return hitTest(scroller.getViewport().getViewPosition().y);
  }

  public void setTopIndex(int index) {
    if (index < 0 || index >= getCount()) return;
    Rectangle vRect = getVisibleScrollRect();
    Rectangle fRect = getItemRect(subfocus);
    if (uniformHeight) {
      checkItemHeight();
      Object data = model.get(0);
      ItemPainter painter = getPainter(0, data, 0);
      Graphics g = getSiteGraphics();
      int height = itemHeight > 0 ? itemHeight : painter != null ? painter.getPreferredSize(data, g, 0, this).height : 0;
      int heightAfterIndex = (getCount() - index) * height;
      if (vRect.height <= heightAfterIndex)
        scroller.getViewport().setViewPosition(new Point(vRect.x, index * height));
      else if (vRect.height <= getCount() * height)
        scroller.getViewport().setViewPosition(new Point(vRect.x, getCount() * height - vRect.height));
      else
        scroller.getViewport().setViewPosition(new Point(vRect.x, 0));
    }
    scroller.getVerticalScrollBar().setUnitIncrement(fRect.height);
  }

  private void scrollView() {
    if (scroller.getViewport() == null)
      return;
    Rectangle vRect = getVisibleScrollRect();
    Rectangle fRect = getItemRect(subfocus);
    if (fRect != null) {
      if (fRect.y < vRect.y) {
        scroller.getViewport().setViewPosition(new Point(vRect.x, fRect.y));
      }
      else if (fRect.y + fRect.height > vRect.y + vRect.height) {
        int h = getSize().height;
        int y = (h - vRect.height) < (fRect.y + fRect.height - vRect.height) ? h - vRect.height : fRect.y + fRect.height - vRect.height;
        if (snapOrigin) {
          int o = hitTest(y);
          o++;
          Rectangle oRect = getItemRect(o);
          if (oRect != null)
            y = oRect.y;
        }
        scroller.getViewport().setViewPosition(new Point(vRect.x, y));
      }
      scroller.getVerticalScrollBar().setUnitIncrement(fRect.height);
    }
  }

  public int getSubfocus() { return subfocus; }

  public void setSubfocus(int index) {
    setSubfocus(index, SelectFlags.CLEAR | SelectFlags.ADD_ITEM | SelectFlags.RESET_ANCHOR);
  }

  protected void setSubfocus(int index, int flags) {
    int count = getCount();
    if (count <= 0)
      return;
    if (index < 0)
      index = 0;
    else if (index >= count)
      index = count - 1;
/*
    System.err.print("setSubfocus(" + index + ") flags=");
    if ((flags & SelectFlags.CLEAR) != 0)        System.err.print(" CLEAR");
    if ((flags & SelectFlags.ADD_ITEM) != 0)     System.err.print(" ADD_ITEM");
    if ((flags & SelectFlags.TOGGLE_ITEM) != 0)  System.err.print(" TOGGLE_ITEM");
    if ((flags & SelectFlags.ADD_RANGE) != 0)    System.err.print(" ADD_RANGE");
    if ((flags & SelectFlags.RESET_ANCHOR) != 0) System.err.print(" RESET_ANCHOR");
    System.err.println();
*/
    if (editor != null) {
      if (lockSubfocus)
        return;  // if editor says stay put, then stay put.
      else
        safeEndEdit();
    }

    if ((index >= count) || (index < 0) || subfocus == index ||
      !preprocessSubfocusEvent(new VectorSubfocusEvent((Object)this, SubfocusEvent.SUBFOCUS_CHANGING, index)))
      return;

    if (selectAnchor < 0)
      selectAnchor = index;

    int oldFocus = subfocus;
    subfocus = index;

    if ((flags & SelectFlags.CLEAR) != 0)
      selection.removeAll();
    if ((flags & SelectFlags.ADD_ITEM) != 0)
      selection.add(subfocus);
    if ((flags & SelectFlags.TOGGLE_ITEM) != 0) {
      if (selection.contains(subfocus))
        selection.remove(subfocus);
      else
        selection.add(subfocus);
    }
    if ((flags & SelectFlags.ADD_RANGE) != 0) {
      dumpingRange = true;
      selection.removeRange(selectAnchor, oldFocus);
      selection.addRange(selectAnchor, subfocus);
    }
    if ((flags & SelectFlags.RESET_ANCHOR) != 0)
      selectAnchor = index;

    // repaint the un-focused and newly focused items
    repaintItem(oldFocus);
    repaintItem(subfocus);

    scrollView(); // scroll the view to display the subfocus item

    processSubfocusEvent(new VectorSubfocusEvent((Object)this, SubfocusEvent.SUBFOCUS_CHANGED, subfocus));
  }

  // VectorModelListener events...

  public void modelContentChanged(VectorModelEvent e) {
    Diagnostic.trace(Trace.ModelEvents, "ListCore.modelContentChanged(" + e + ")"); 
    switch (e.getChange()) {
      case (VectorModelEvent.CONTENT_CHANGED):
        repaintItems();
        break;
      case (VectorModelEvent.ITEM_CHANGED):
      case (VectorModelEvent.ITEM_TOUCHED):
        if (editor != null && editorLocation == e.getLocation())
          safeEndEdit(false);
        repaintItem(e.getLocation());
        break;
    }
  }

  public void modelStructureChanged(VectorModelEvent e) {
    Diagnostic.trace(Trace.ModelEvents, "ListCore.modelStructureChanged(" + e + ")"); 
    if (editor != null)
      safeEndEdit(false);
    invalidate();
    if (getCount() <= subfocus)
      setSubfocus(getCount() - 1);
    repaintItems();
    if (isShowing() && !batchMode) {
      scroller.validate();
      scrollView();
    }
  }

  // VectorSelectionListener events...

  public void selectionItemChanged(VectorSelectionEvent e) {
    Diagnostic.trace(Trace.SelectionEvents, "ListCore.selectionItemChanged(" + e + ")"); 
    repaintItem(e.getLocation());
    oldSelected = e.getSelection().getAll();
  }

  public void selectionRangeChanged(VectorSelectionEvent e) {
    Diagnostic.trace(Trace.SelectionEvents, "ListCore.selectionRangeChanged(" + e + ")"); 
    for (int i = e.getRangeStart() ; i < e.getRangeEnd() ; i++ )
      repaintItem(i);
    oldSelected = e.getSelection().getAll();
  }

  public void selectionChanged(VectorSelectionEvent e) {
    Diagnostic.trace(Trace.SelectionEvents, "ListCore.selectionChanged(" + e + ")"); 
    int[] sels = e.getSelection().getAll();
    WritableVectorSelection old = new BasicVectorSelection(oldSelected);
    for (int i = 0; i < sels.length; i++) {
      if (!old.contains(sels[i]))
        repaintItem(sels[i]);
      else
        old.remove(sels[i]);
    }
    oldSelected = old.getAll();
    for (int i = 0; i < oldSelected.length; i++)
      repaintItem(oldSelected[i]);
    oldSelected = e.getSelection().getAll();
  }

  protected void processMousePressed(MouseEvent e) {
    boolean hadFocus = hasFocus;
    hasFocus = true;
    super.processMousePressed(e);
    int index = hitTest(e.getY());
    if (index == -1) return;

    rollover = -1;
    mouseDown = index;

    boolean shift   = e.isShiftDown();
    boolean control = e.isControlDown();
    boolean alt     = e.isAltDown();
    boolean right   = e.isMetaDown();
    int     flags;

    if (shift && control)
      flags = SelectFlags.ADD_RANGE;
    else if (shift)
      flags = SelectFlags.CLEAR | SelectFlags.ADD_RANGE;
    else if (control)
      flags = SelectFlags.TOGGLE_ITEM | SelectFlags.RESET_ANCHOR;
    else
      flags = SelectFlags.CLEAR | SelectFlags.ADD_ITEM | SelectFlags.RESET_ANCHOR;

    if (selectAnchor < 0)
      selectAnchor = subfocus;

    if (editor != null) {
      if (editorLocation == index)
        return;
      else
        safeEndEdit();
    }
    if (index == subfocus) {
      if (!right && e.getClickCount() == 2)
        fireActionEvent();
      if (!control && !shift && !alt) {
        if (hadFocus && !selection.contains(index)) {
          selection.removeAll();
          selection.add(index);
        }
        else if (!hadFocus) {
          selection.removeAll();
          selection.add(index);
        }
      }
      else if (control && !shift) {
        if (selection.contains(index))
          selection.remove(index);
        else
          selection.add(index);
      }
      if (hadFocus && !right && !control && !shift && !isToggleItem(index) && canSet(index, false)) {
        doStartEdit = true;
        return;
      }
    }
    rangeSelecting = true;
    setSubfocus(index, flags);
  }

  protected void processMouseDragged(MouseEvent e) {
    rollover = -1;
    if (dragSubfocus && !e.isMetaDown() && rangeSelecting) {
      int index = hitTest(e.getY());
      if (index != -1) {
        int flags = e.isControlDown() ? SelectFlags.RESET_ANCHOR : SelectFlags.ADD_RANGE;
        setSubfocus(index, flags);
      }
    }
  }

  boolean doStartEdit = false;
  protected void processMouseReleased(MouseEvent e) {
    int y = e.getY();
    boolean shift   = e.isShiftDown();
    boolean control = e.isControlDown();
    boolean right   = e.isMetaDown();

    rollover = -1;
    rangeSelecting = false;
    if (editor != null && subfocus == editorLocation && editor.getComponent() != null)
      editor.getComponent().requestFocus();

    int index = hitTest(y);
    if (index >= 0 && index == mouseDown) {
      if (!right && (doStartEdit || isToggleItem(index))) {
        editClickPoint = new Point(e.getX(), e.getY());
        startEdit(index);
        doStartEdit = false;
      }
      else if (!right && !shift && !control && selection.getCount() > 1) {
        selection.removeAll();
        selection.add(index);
      }
    }
    mouseDown = -1;
  }

  protected Dimension getPreferredItemSize(int index, Object data) {
    int state = getState(index);
    ItemPainter painter = getPainter(index, data, state);
    Dimension size = new Dimension(0,0);
    if (painter != null)
      size = painter.getPreferredSize(data, getSiteGraphics(), state, this);
    return size;
  }

  public Dimension getMinimumSize() {
    return new Dimension(20, 20);
  }

  protected void processMouseMoved(MouseEvent e) {
    if (showRollover) {
      int hit = hitTestAbs(e.getY());
      if (hit != rollover) {
        int oldRollover = rollover;
        rollover = hit;
        if (oldRollover >= 0)
          repaintItem(oldRollover);
        if (rollover >= 0)
          repaintItem(rollover);
      }
    }
  }

  public JToolTip createToolTip() {
    return toolTip;
  }

  public String getToolTipText(MouseEvent e) {
    if (toolTip.active) {
      int hit = hitTestAbs(e.getY());
      if (hit != -1 && model != null && viewManager != null) {
        Object data = model.get(hit);
        if (data != null) {
          Rectangle r = getItemRect(hit);
          Dimension size = getPreferredItemSize(hit, data);
          Rectangle vRect = getVisibleScrollRect();
          if (r != null && (!r.contains(r.x + size.width - 1, r.y + size.height - 1) ||
              !vRect.contains(r.x, r.y) || !vRect.contains(r.x + size.width - 1, r.y + size.width - 1))) {
            int state = getState(hit);
            toolTip.data    = data;
            toolTip.painter = getPainter(hit, data, state);
            toolTip.state   = state;
            return data.toString();
          }
        }
      }
      return null;
    }
    toolTip.painter = null;
    return getToolTipText();
  }

  public Point getToolTipLocation(MouseEvent e) {
    if (toolTip.active && getToolTipText(e) != null) {
      int hit = hitTestAbs(e.getY());
      if (hit != -1) {
        Rectangle r = getItemRect(hit);
        if (r != null && model != null) {
          Object data = model.get(hit);
          int state = getState(hit);
          toolTip.data    = data;
          toolTip.painter = getPainter(hit, data, state);
          toolTip.state   = state;
          return new Point(r.x - 1, r.y - 1);
        }
      }
    }
    toolTip.painter = null;
    return null;
  }

  protected void processMouseExited(MouseEvent e) {
    if (showRollover) {
      int oldRollover = rollover;
      rollover = -1;
      repaintItem(oldRollover);
    }
  }

  protected void startEdit(int newEditorLocation) {
    if (model == null || viewManager == null || !editInPlace || batchMode || !canSet(newEditorLocation, true))
      return;
    rollover = -1;
    editorLocation = newEditorLocation;
    selection.removeAll();
    selection.add(editorLocation);
    Object data = model.get(editorLocation);
    editor = getEditor(editorLocation, data, 0);
    if (editor != null) {
      Component editorComponent = editor.getComponent();
      if (editorComponent != null) {
        editorComponent.setVisible(false);
        add(editorComponent);
      }
      Rectangle r = getEditorRect();
      editor.addKeyListener(this);
      editor.addKeyListener(keyMulticaster);
      // This redundancy is here only for the dataset Variant object
      data = model.get(editorLocation);
      editor.startEdit(data, r, this);
      // in case the editor needs to grow
      resyncEditor();
      if (editor != null && editor.getComponent() != null)
        editor.getComponent().addFocusListener(this);
      editClickPoint = null;
    }
  }

  protected Rectangle getEditorRect() {
    Rectangle rect = null;
    if (editorLocation >= 0 && editor != null) {
      rect = getItemRect(editorLocation);
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
    if (editorLocation >= 0 && editor != null) {
      Rectangle er = getEditorRect();
      editor.changeBounds(er != null ? er : new Rectangle());
    }
  }

  private boolean isToggleItem(int index) {
    if (model == null || viewManager == null || !editInPlace || batchMode)
      return false;
    Object data = model.get(index);
    int state = getState(index);
    ItemEditor ie = getEditor(index, data, state);
    if (ie instanceof ToggleItemEditor) {
      Rectangle rect = getItemRect(index);
      return ((ToggleItemEditor)ie).isToggle(data, rect, this) && canSet(index, false);
    }
    else
      return false;
  }

  private boolean lockSubfocus = false;
  public void endEdit() throws Exception {
    endEdit(postOnEndEdit);
  }
  public void endEdit(boolean post) throws Exception {
    ItemEditor editor = this.editor;  // keep in local in case of reentrancy
    this.editor = null;
    if (editor != null) {
      try {
        boolean okToEnd = editor.canPost();
        if (okToEnd == false)
          lockSubfocus = true;
        if (okToEnd || !post) {
          lockSubfocus = false;
          if (post && okToEnd && writeModel.canSet(editorLocation, true)) {
            writeModel.set(editorLocation, editor.getValue());
            fireActionEvent();
          }
          Component editorComponent = editor.getComponent();
          editor.endEdit(post);
          editor.removeKeyListener(this);
          editor.removeKeyListener(keyMulticaster);
          if (editorComponent != null) {
            remove(editorComponent);
            editorComponent.removeFocusListener(this);
          }
          repaintItem(editorLocation);
          editorLocation = -1;
          editClickPoint = null;
          editor = null;
          requestFocus();
        }
        else {
          lockSubfocus = true;
        }
      }
      catch (Exception x) {
        lockSubfocus = true;
        this.editor = editor;
        throw x;
      }
    }
    this.editor = editor;
  }

  public void safeEndEdit() {
    safeEndEdit(postOnEndEdit);
  }
  public void safeEndEdit(boolean post) {
    try {
      endEdit(post);
    }
    catch (Exception x) {
//      if (editor instanceof ExceptionHandler)
//        ((ExceptionHandler)editor).handleException(x);
//      else if (model instanceof ExceptionHandler)
//        ((ExceptionHandler)model).handleException(x);
    }
  }

  public void doLayout() {
    if (editorLocation > -1 && editor != null) {
      Rectangle r = getItemRect(editorLocation);
      if (r != null) {
        editor.changeBounds(new Rectangle(r.x, r.y, r.width, r.height));
      }
      else
        editor.changeBounds(new Rectangle(0,0,0,0));
    }
  }

  private void repaintSelection() {
//    Diagnostic.printlnc("ListView.repaintSelection()");
    for (int i = 0 ; i < oldSelected.length ; i++) {
//      Diagnostic.print("\t"+oldSelected[i]);
      repaintItem(oldSelected[i]);
    }
//    Diagnostic.println();
  }

  public void addSubfocusListener(VectorSubfocusListener l) { subfocusListeners.add(l); }
  public void removeSubfocusListener(VectorSubfocusListener l) { subfocusListeners.remove(l); }

  protected void processSubfocusEvent(VectorSubfocusEvent e) {
//    Diagnostic.trace(Trace.FocusEvents, "ListCore.processSubfocusEvent : " + e);
    if (subfocusListeners.hasListeners())
      subfocusListeners.dispatch(e);
  }

  protected boolean preprocessSubfocusEvent(VectorSubfocusEvent e) {
//    Diagnostic.trace(Trace.FocusEvents, "ListCore.preprocessSubfocusEvent : " + e);
    return subfocusListeners.hasListeners() ? subfocusListeners.vetoableDispatch(e) : true;
  }

  public void windowActiveChanged(boolean active) {
    super.windowActiveChanged(active);
    repaintSelection();
  }

  public void focusGained(FocusEvent e) {
//    Diagnostic.printlnc("ListView.editor.FOCUS_GAINED");
  }

  public void focusLost(FocusEvent e) {
//    Diagnostic.printlnc("ListView.editor.FOCUS_LOST");
    if (hasFocus) {
      hasFocus = false;
      repaintItem(subfocus);
    }
  }

  protected void processFocusEvent(FocusEvent e) {
    super.processFocusEvent(e);
    switch (e.getID()) {
      case FocusEvent.FOCUS_GAINED:
        if (selectAnchor < 0)
          selectAnchor = subfocus;
//        Diagnostic.printlnc("ListView.FOCUS_GAINED");
        if (editor != null && editor.getComponent() != null)
          editor.getComponent().requestFocus();
        if (!hasFocus) {
          hasFocus = true;
        }
        break;
      case FocusEvent.FOCUS_LOST:
//        Diagnostic.printlnc("ListView.FOCUS_LOST");
        if (editor == null) {
          hasFocus = false;
        }
        break;
    }
    repaintItem(subfocus);
  }

  // keyPressed on embedded editor
  //
  public void keyPressed(KeyEvent e) {
    boolean alt = e.isAltDown();

    if (editor == null || e.isConsumed())
      return;
//    Diagnostic.trace(Trace.KeyEvents, "ListView.editor.keyPressed : " + e);
    switch (e.getKeyCode()) {
      case KeyEvent.VK_ENTER:
        safeEndEdit(true);
        if (!lockSubfocus) {
          e.consume();
          fireActionEvent();
        }
        break;
      case KeyEvent.VK_ESCAPE:
        safeEndEdit(false);
        e.consume();
        break;
      case KeyEvent.VK_UP:
      case KeyEvent.VK_DOWN:
      case KeyEvent.VK_PAGE_UP:
      case KeyEvent.VK_PAGE_DOWN:
        if (!alt) {
          safeEndEdit();
          processKeyPressed(e); // pass it on
        }
        break;
    }
  }
  public void keyReleased(KeyEvent e) {}
  public void keyTyped(KeyEvent e) {}

  // keyPressed on ListCore (not embedded editor)
  //
  protected void processKeyPressed(KeyEvent e) {
    int     key     = e.getKeyCode();
    boolean control = e.isControlDown();
    boolean shift   = e.isShiftDown();
    boolean alt     = e.isAltDown();
    int     flags;

    if (shift && control)
      flags = SelectFlags.ADD_RANGE;
    else if (shift)
      flags = SelectFlags.ADD_RANGE;
    else if (control)
      flags = SelectFlags.RESET_ANCHOR;
    else
      flags = SelectFlags.CLEAR | SelectFlags.ADD_ITEM | SelectFlags.RESET_ANCHOR;

    switch (key) {
      case KeyEvent.VK_DOWN:
        if (alt)
          break;
        if ((autoAppend || control) && subfocus == (getCount()-1) && !isReadOnly() && writeModel.isVariableSize()) {
          writeModel.addItem(null);
          setSubfocus(getCount() - 1, SelectFlags.CLEAR | SelectFlags.ADD_ITEM | SelectFlags.RESET_ANCHOR);
          e.consume();
        }
        else if (subfocus != (getCount()-1)) {
          setSubfocus(subfocus + 1, flags);
          e.consume();
        }
        break;
      case KeyEvent.VK_UP:
        if (alt)
          break;
        if (subfocus > 0) {
          setSubfocus(subfocus - 1, flags);
          e.consume();
        }
        break;
      case KeyEvent.VK_PAGE_UP:
        if (alt)
          break;
        if (subfocus > 0) {
          pageJump(false, flags);
          e.consume();
        }
        break;
      case KeyEvent.VK_PAGE_DOWN:
        if (alt)
          break;
        if (subfocus != (getCount()-1)) {
          pageJump(true, flags);
          e.consume();
        }
        break;
      case KeyEvent.VK_HOME:
        if (alt)
          break;
        if (subfocus > 0) {
          if (control && !shift)
            setSubfocus(0, SelectFlags.CLEAR | SelectFlags.ADD_ITEM | SelectFlags.RESET_ANCHOR);
          else
            setSubfocus(0, flags);
          e.consume();
        }
        break;
      case KeyEvent.VK_END:
        if (alt)
          break;
        if (subfocus != (getCount()-1)) {
          if (control && !shift)
            setSubfocus(getCount() - 1, SelectFlags.CLEAR | SelectFlags.ADD_ITEM | SelectFlags.RESET_ANCHOR);
          else
            setSubfocus(getCount() - 1, flags);
          e.consume();
        }
        break;
      case KeyEvent.VK_SPACE:
        if (!alt && !shift) {
          if (control && selection.contains(subfocus))
            selection.remove(subfocus);
          else
            selection.add(subfocus);
          if (isToggleItem(subfocus))
            startEdit(subfocus);
          e.consume();
        }
        break;
      case KeyEvent.VK_INSERT:
        if (alt)
          break;
        if (!isReadOnly() && writeModel.isVariableSize()) {
          writeModel.addItem(subfocus, null);
          e.consume();
        }
        break;
      case KeyEvent.VK_DELETE:
        if (alt)
          break;
        if (control && !isReadOnly() && writeModel.isVariableSize() && getCount() > 0) {
          writeModel.remove(subfocus);
          if (subfocus == getCount())
            setSubfocus(getCount() - 1);
          e.consume();
        }
        break;
      case KeyEvent.VK_ENTER:
        if (control && editor == null && canSet(subfocus, false))
          startEdit(subfocus);
        else
          fireActionEvent();
        e.consume();
        break;
      case KeyEvent.VK_F2:
        if (editor == null && !isToggleItem(subfocus) && canSet(subfocus, false)) {
          startEdit(subfocus);
          e.consume();
        }
        break;
      case KeyEvent.VK_J: // debug painting
        if (shift && control && alt)
          debugPaint = !debugPaint;
        break;
      case KeyEvent.VK_KANJI:
      case 0xE5:  // VK_PROCESSKEY:
        startEdit(subfocus);
        break;
      default: return;
    }
  }

  // keyTyped on ListCore (not embedded editor)
  // This should only be printable characters...
  //
  protected void processKeyTyped(KeyEvent e) {
    char kChar = e.getKeyChar();
    if (editor != null ||
        !autoEdit ||
        e.isConsumed() ||
        isReadOnly() ||
        kChar == 0 ||
        kChar == '\t' ||
        kChar == '\r' ||
        kChar == '\n' ||
        kChar == ' ' || 
        kChar == 27 || // ESCAPE
        isToggleItem(subfocus) ||
        (e.isAltDown() ^ e.isControlDown()) ||
        !canSet(subfocus, false))
      return;
    startEdit(subfocus);
    Component eComp = null;
    if (editor != null && (eComp = editor.getComponent()) != null) {
      eComp.dispatchEvent(e);
    }
  }

  private void pageJump(boolean pageDown, int flags) {
    int count = getCount();
    if (count == 0)
      count++;
    int avHeight = getSize().height / count;
    if (avHeight == 0)
      avHeight++;
    Rectangle vRect = getVisibleScrollRect();
    int jump = (vRect.height / avHeight) - 1;
    jump = pageDown ? jump : -jump;
    setSubfocus(subfocus + jump, flags);
  }

  public void repaintItem(int index) {
    if (batchMode)
      return;
    Rectangle dirty = getItemRect(index);
    if (dirty != null)
      repaint(dirty.x, dirty.y, dirty.width, dirty.height);
  }

  public void repaintItems() {
    if (batchMode)
      return;
    repaint(100);
  }

  /**
   * Used by editors which wish to set the insertion point at the clicked position.
   * This method returns the mouse click position, or null if no mouse click initiated the editing
   */
  public Point getEditClickPoint() { return editClickPoint; }

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

  private int getCount() { return model != null ? model.getCount() : 0; }
  private boolean canSet(int index, boolean startingEdit) { return !isReadOnly() ? writeModel.canSet(index, startingEdit) : false; }

  // Painting Guts

  public void update(Graphics g) { paint(g); }

  private int getState(int index) {
    int state = isEnabled() ? 0 : ItemPainter.DISABLED;
    if (selection.contains(index))
      state |= ItemPainter.SELECTED;
    if (!isEnabled())
      state |= ItemPainter.DISABLED | ItemPainter.INACTIVE;
    else {
      if (showFocus && ((focusState & ItemPainter.FOCUSED) != 0) && subfocus == index)
        state |= ItemPainter.FOCUSED;
      if ((focusState & ItemPainter.INACTIVE) != 0)
        state |= ItemPainter.INACTIVE;
      if (showRollover && rollover >= 0 && rollover == index)
        state |= ItemPainter.ROLLOVER;
    }
    if (!hasFocus)
      state |= ItemPainter.NOT_FOCUS_OWNER;
    return state;
  }

  public int hitTest(int yPos) {
    int y = 0;
    int count = getCount();
    if (getCount() < 1)
      return -1;
    Graphics g = getSiteGraphics();
    if (uniformHeight) {
      checkItemHeight();
      Object data = model.get(0);
      ItemPainter painter = getPainter(0, data, 0);
      int height = itemHeight > 0 ? itemHeight : painter != null ? painter.getPreferredSize(data, g, 0, this).height : 0;
      if (height == 0)
        return -1;
      int itemIndex = yPos / height;
      if (itemIndex < count)
        return itemIndex;
      else
        return count - 1;
    }
    else {
      for (int i = 0; i < count; i++) {
        Object data = model.get(i);
        ItemPainter painter = getPainter(i, data, 0);
        y += painter != null ? painter.getPreferredSize(data, g, 0, this).height : 0;
        if (y >= yPos)
          return i;
      }
      if (yPos >= y)
        return count - 1;
      else
        return 0;
    }
  }

  /**
   * Returns the hit index at (?, yPos) or -1 if an index was not hit.
   */
  public int hitTestAbs(int yPos) {
    int y = 0;
    int count = getCount();
    if (getCount() < 1)
      return -1;
    Graphics g = getSiteGraphics();
    if (uniformHeight) {
      checkItemHeight();
      Object data = model.get(0);
      ItemPainter painter = getPainter(0, data, 0);
      int height = itemHeight > 0 ? itemHeight : painter != null ? painter.getPreferredSize(data, g, 0, this).height : 0;
      if (height == 0)
        return -1;
      int itemIndex = yPos / height;
      if (itemIndex < count)
        return itemIndex;
      else
        return -1;
    }
    else {
      for (int i = 0; i < count; i++) {
        Object data = model.get(i);
        ItemPainter painter = getPainter(i, data, 0);
        y += painter != null ? painter.getPreferredSize(data, g, 0, this).height : 0;
        if (y >= yPos)
          return i;
      }
      return -1;
    }
  }

  public Rectangle getItemRect(int index) {
    if (index >= 0 && index < getCount()) {
      int count = getCount();
      if (scroller.getViewport() == null)
        return null;
      Rectangle vRect = getVisibleScrollRect();
      Rectangle rect = new Rectangle(0, 0, vRect.width, 0);
      Graphics g = getSiteGraphics();
      if (uniformHeight) {
        checkItemHeight();
        Object data = model.get(0);
        ItemPainter painter = getPainter(0, data, 0);
        Dimension size = painter != null ? painter.getPreferredSize(data, g, 0, this) : new Dimension(0,0);
        if (itemHeight > 0) {
          rect.y = itemHeight * index;
          rect.height = itemHeight;
        }
        else {
          rect.y = size.height * index;
          rect.height = size.height;
        }
        rect.width = getSize().width;
        return rect;
      }
      else {
        for (int i = 0; i < count; i++) {
          Object data = model.get(i);
          ItemPainter painter = getPainter(i, data, 0);
          Dimension size = painter != null ? painter.getPreferredSize(data, g, 0, this) : new Dimension(0,0);
          rect.height = size.height;
          rect.width = getSize().width;
          if (i == index)
            return rect;
          rect.y += size.height;
        }
      }
    }
    return null;
  }

  void checkItemHeight() {
    if (uniformHeight && itemHeight < 1 && model != null && model.getCount() > 0) {
      Object data = model.get(0);
      int state = getState(0);
      ItemPainter painter = getPainter(0, data, state);
      if (painter != null) {
        Dimension sz = painter.getPreferredSize(data, getSiteGraphics(), state, this);
        itemHeight = sz.height;
      }
    }
  }

  public void paintComponent(Graphics g) {
    if (batchMode)
      return;
    super.paintComponent(g);
    g.clipRect(0,0,getSize().width, getSize().height);
    Rectangle vRect = getVisibleScrollRect();
    Rectangle c = g.getClipBounds();
    if (c == null)
      return;
    Rectangle clip;
    checkItemHeight();
    if (c.width > vRect.width || c.height > vRect.height)
      clip = c.intersection(vRect);
    else
      clip = c;
    if (clip.width <= 0 || clip.height <= 0 || vRect.width <= 0 || vRect.height <= 0) {
      return;
    }
    g.setClip(clip.x, clip.y, clip.width, clip.height);
    int first = hitTest(clip.y);
    int last = hitTest(clip.y + clip.height);
    Rectangle r = getItemRect(first);
    if (r != null) {
      for (int i = first; i <= last; i++) {
        Object     data   = model.get(i);
        int        state  = getState(i);
        ItemPainter painter = getPainter(i, data, state);
        if (painter != null) {
          Dimension prefSize = painter.getPreferredSize(data, g, state, this);
          Rectangle rect = new Rectangle(r.x, r.y, r.width, prefSize.height);
          if (uniformHeight && itemHeight > 0)
            rect.height = itemHeight;
          g.setFont(getFont());
          g.setColor(getBackground());
          painter.paint(data, g, rect, state, this);
          r.y += uniformHeight && itemHeight > 0 ? itemHeight : prefSize.height;
        }
      }
      if (r.y < vRect.y + vRect.height) {
        if (texture != null)
          ImageTexture.texture(texture, g, vRect.x, vRect.y + r.y, vRect.width, vRect.y + vRect.height - r.y);
        else if (isOpaque()) {
          g.setColor(getBackground());
          g.fillRect(vRect.x, vRect.y + r.y, vRect.width, vRect.y + vRect.height - r.y);
        }
      }
    }
    else {
      if (texture != null)
        ImageTexture.texture(texture, g, clip.x, clip.y, clip.width, clip.height);
      else if (isOpaque()) {
        g.setColor(getBackground());
        g.fillRect(clip.x, clip.y, clip.width, clip.height);
      }
    }

    // For debugging purposes only
    // draws a colored rectangle around the clip rectangle
    // with diagonal hash lines (for tracing paint calls)
    if (debugPaint) {
      GridCore.debugRect(g, clip.x, clip.y, clip.width, clip.height);
    }
  }

  public Rectangle getVisibleScrollRect() {
    if (scroller.getViewport() == null)
      return new Rectangle(0,0,0,0);
    Rectangle vRect = scroller.getViewport().getViewRect();
    return vRect;
  }

  protected ItemPainter getPainter(int index, Object data, int state) {
    ItemPainter painter = viewManager != null ? viewManager.getPainter(index, data, state) : null;
    if (painter != null && customizeListeners != null) {
      customPainter.setPainter(painter);
      fireCustomizeItemEvent(new Integer(index), data, state, customPainter);
      return customPainter;
    }
    return painter;
  }

  protected ItemEditor getEditor(int index, Object data, int state) {
    ItemEditor editor = viewManager != null ? viewManager.getEditor(index, data, state) : null;
    if (editor != null && customizeListeners != null) {
      customEditor.setEditor(editor);
      fireCustomizeItemEvent(new Integer(index), data, state, customEditor);
      return customEditor;
    }
    return editor;
  }

  public Dimension getPreferredSize() {
    Dimension sz = new Dimension(0, 0);
    int count = getCount();
    if (count > 0) {
      Graphics g = getSiteGraphics();
      if (uniformHeight && uniformWidth) { // BOTH ARE SET
        checkItemHeight();
        Object data = model.get(0);
        ItemPainter painter = getPainter(0, data, 0);
        Dimension size = painter.getPreferredSize(data, g, 0, this);
        sz.width  = (itemWidth > 0) ? itemWidth : size.width;
        sz.height = (itemHeight > 0) ? itemHeight * count : size.height * count;
        scroller.getVerticalScrollBar().setUnitIncrement(itemHeight > 0 ? itemHeight : size.height);
      }
      else {
        boolean needSize = true;
        for (int i = 0; i < count; i++) {
          Object data = model.get(i);
          ItemPainter painter = getPainter(i, data, 0);
          Dimension size = painter != null ? painter.getPreferredSize(data, g, 0, this) : new Dimension(0,0);
          if (needSize && size.height > 0) {
            scroller.getVerticalScrollBar().setUnitIncrement(size.height);
            needSize = false;
          }
          if (size.width > sz.width) // width is max over all items
            sz.width = size.width;
          sz.height += size.height;
        }
        // Grab first item's dimensions and reset if uniformSize flags are set...
        if (uniformWidth || uniformHeight) {
          Object data = model.get(0);
          ItemPainter painter = getPainter(0, data, 0);
          Dimension size = painter != null ? painter.getPreferredSize(data, g, 0, this) : new Dimension(0,0);
          if (uniformWidth)
            sz.width  = (itemWidth > 0) ? itemWidth : size.width;
          if (uniformHeight) {
            sz.height = (itemHeight > 0) ? itemHeight * count : size.height * count;
            if (scroller != null && scroller.getVerticalScrollBar() != null)
              scroller.getVerticalScrollBar().setUnitIncrement(itemHeight > 0 ? itemHeight : size.height);
          }
        }
      }
    }
    return sz;
  }

  public void addKeyListener(KeyListener l) { keyMulticaster.add(l); }
  public void removeKeyListener(KeyListener l) { keyMulticaster.remove(l); }

  public void checkParentWindow() {
    findParentWindow();
  }

  private void fireActionEvent() {
    Object item = model != null ? model.get(subfocus) : null;
    String action = item != null ? item.toString() : "";
    processActionEvent(new ActionEvent(scroller, ActionEvent.ACTION_PERFORMED, action));
  }

  protected void fireCustomizeItemEvent(Object address, Object data, int state, CustomPaintSite cps) {
    if (customizeListeners != null) {
      cps.reset();
      for (int i = 0; i < customizeListeners.size(); i++)
        ((CustomItemListener)customizeListeners.elementAt(i)).customizeItem(address, data, state, cps);
    }
  }

  public synchronized void addCustomItemListener(CustomItemListener l) {
    if (customizeListeners == null)
      customizeListeners = new Vector();
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
    Hashtable hash = new Hashtable(3);
    if (model instanceof Serializable)
      hash.put("m", model); 
    if (viewManager instanceof Serializable)
      hash.put("v", viewManager); 
    if (selection instanceof Serializable)
      hash.put("s", selection); 
    s.writeObject(hash);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    Hashtable hash = (Hashtable)s.readObject();
    Object data = hash.get("m"); 
    if (data instanceof VectorModel)
      model = (VectorModel)data;
    if (model instanceof WritableVectorModel)
      writeModel = (WritableVectorModel)model;
    data = hash.get("v"); 
    if (data instanceof VectorViewManager)
      viewManager = (VectorViewManager)data;
    data = hash.get("s"); 
    if (data instanceof WritableVectorSelection)
      selection = (WritableVectorSelection)data;
  }

  private transient VectorModel   model;
  private transient WritableVectorModel writeModel;
  private transient VectorViewManager viewManager;
  private transient WritableVectorSelection selection = new NullVectorSelection();

  private transient int[]         oldSelected    = new int[0]; // ignore for serialization
  private boolean                 readOnly       = false;
  private boolean                 showFocus      = true;
  private boolean                 hasFocus       = false;
  private boolean                 rangeSelecting = false;
  private boolean                 dumpingRange   = false;
  private boolean                 postOnEndEdit  = true;
  private boolean                 uniformWidth   = false;
  private boolean                 uniformHeight  = true;
  private int                     itemWidth      = 0;
  private int                     itemHeight     = 0;
  private int                     subfocus       = 0;
  private boolean                 snapOrigin     = true;
  private int                     selectAnchor   = -1;
  private int                     rollover       = -1;
  private int                     mouseDown      = -1;
  private transient ItemEditor    editor; // ignore for serialization
  private int                     editorLocation;
  private Point                   editClickPoint;
  private transient JScrollPane   scroller;
  private Insets                  itemMargins    = new Insets(2,2,2,2);
  private int                     alignment      = Alignment.LEFT | Alignment.MIDDLE;
  private boolean                 editInPlace    = true;
  private boolean                 autoEdit       = true;
  private boolean                 growEditor     = true;
  private boolean                 autoAppend     = false;
  private boolean                 dragSubfocus   = true;
  private boolean                 debugPaint     = false;
  private boolean                 batchMode      = false;
  private boolean                 showRollover   = false;
  private DataToolTip             toolTip        = new DataToolTip(this);

  private transient CustomItemPainter customPainter = new CustomItemPainter();
  private transient CustomItemEditor  customEditor  = new CustomItemEditor();
  private transient Vector customizeListeners;

  private transient EventMulticaster subfocusListeners = new EventMulticaster();
  private transient KeyMulticaster keyMulticaster = new KeyMulticaster();
  private transient VectorModelMulticaster modelMulticaster = new VectorModelMulticaster();
  private transient VectorSelectionMulticaster selectionMulticaster = new VectorSelectionMulticaster();
}
