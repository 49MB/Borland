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
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.borland.jbcl.model.BasicVectorContainer;
import com.borland.jbcl.model.BasicViewManager;
import com.borland.jbcl.model.ItemEditor;
import com.borland.jbcl.model.SingleVectorSelection;
import com.borland.jbcl.model.VectorModel;
import com.borland.jbcl.model.VectorModelListener;
import com.borland.jbcl.model.VectorSelectionListener;
import com.borland.jbcl.model.VectorSubfocusListener;
import com.borland.jbcl.model.VectorViewManager;
import com.borland.jbcl.model.WritableVectorModel;
import com.borland.jbcl.model.WritableVectorSelection;

public class ListView extends JScrollPane implements VectorView, java.io.Serializable
{
  public ListView() {
    super();
    setDoubleBuffered(true);
    super.setBorder(UIManager.getBorder("Table.scrollPaneBorder")); 
    getViewport().setView(core);
    setModel(createDefaultModel());
    setViewManager(createDefaultViewManager());
    setSelection(createDefaultSelection());
    super.setBackground(UIManager.getColor("List.background")); 
    super.setForeground(UIManager.getColor("List.foreground")); 
    // for JDK 1.3, remove JScrollPane's default key action map
    // so GridCore can process its own keystrokes
    if (BeanPanel.is1dot3) {
      SwingUtilities.replaceUIActionMap(this, null);
    }
  }

  public void updateUI() {
    super.updateUI();
    setBackground(UIManager.getColor("List.background")); 
    setForeground(UIManager.getColor("List.foreground")); 
    super.setBorder(UIManager.getBorder("Table.scrollPaneBorder")); 
  }

  protected VectorModel createDefaultModel() {
    return new BasicVectorContainer();
  }

  protected VectorViewManager createDefaultViewManager() {
    return new BasicViewManager(
      new FocusableItemPainter(new SelectableItemPainter(new TextItemPainter())),
      new TextItemEditor());
  }

  protected WritableVectorSelection createDefaultSelection() {
    return new SingleVectorSelection();
  }

  public void addNotify() {
    super.addNotify();
    if (isVisible())
      core.repaintItems();
  }

  // PROPERTIES

  /**
   * Returns the contained ListCore (non-public) class instance as a Component.
   */
  public Component getCoreComponent() { return core; }

  /**
   * The model property defines the VectorModel that this list is displaying data from.  If the
   * current model is an instance of WritableVectorModel, an external user can get access to it
   * using getWriteModel().
   */
  public void setModel(VectorModel vm) { core.setModel(vm); }
  public VectorModel getModel() { return core.getModel(); }
  public WritableVectorModel getWriteModel() { return core.getWriteModel(); }

  /**
   * The viewManager property defines the VectorViewManager that will 'broker' ItemPainters and
   * ItemEditors to this list.
   */
  public void setViewManager(VectorViewManager vvm) { core.setViewManager(vvm); }
  public VectorViewManager getViewManager() { return core.getViewManager(); }

  /**
   * The readOnly property is used when the model property is actually a WritableVectorModel, but
   * the user wishes it to be treated as a read-only model.  This is commonly used to allow users
   * to browse, but not edit normally writable vectors of data.
   */
  public void setReadOnly(boolean ro) { core.setReadOnly(ro); }
  public boolean isReadOnly() { return core.isReadOnly(); }

  /**
   * The selection property defines a WritableVectorSelection manager.  This allows multiple
   * vector viewers to share the same selection pool - and for the list to handle selection in
   * a generic manner.  This allows users to plug in their own implemention of a selection
   * manager to have custom selection behavior.
   */
  public void setSelection(WritableVectorSelection vs) { core.setSelection(vs); }
  public WritableVectorSelection getSelection() { return core.getSelection(); }

  /**
   * The subfocus property defines the 'current' item (defined by an integer index) in the list.
   * This is the item that is receiving keyboard input.
   */
  public void setSubfocus(int index) { core.setSubfocus(index); }
  public int getSubfocus() { return core.getSubfocus(); }

  /**
   * The showFocus property enables/disables the painting of the focus rectangle on the
   * current subfocus item.  In reality, the showFocus property turns on/off the FOCUSED
   * bit in the state information that is passed to the ItemPainter when an item is painted.
   * If an ItemPainter plugged into the list ignores the FOCUSED bit, this property will
   * have no effect.  By default, showFocus is true.
   */
  public void setShowFocus(boolean focus) { core.setShowFocus(focus); }
  public boolean isShowFocus() { return core.isShowFocus(); }

  /**
   * The showRollover property enables/disables the repainting of the rollover item.  The
   * rollover item is the item that currently has the mouse floating over it.
   * If an ItemPainter plugged into the list ignores the ROLLOVER bit, this property will
   * have no effect.  By default, showRollover is true.
   */
  public void setShowRollover(boolean showRollover) { core.setShowRollover(showRollover); }
  public boolean isShowRollover() { return core.isShowRollover(); }

  /**
   * The dataToolTip property enables/disables the automatic tooltip mechanism
   * to display the contents of the model (as text) in a tooltip window when
   * the mouse is floating over an item.  By default, this property is false.
   * If set to true, the text stored in the toolTipText property is discarded.
   */
  public void setDataToolTip(boolean dataToolTip) { core.setDataToolTip(dataToolTip); }
  public boolean isDataToolTip() { return core.isDataToolTip(); }

  public void setToolTipText(String text) { core.setToolTipText(text); }
  public String getToolTipText() { return core.getToolTipText(); }

  /**
   * The dragFocus property enables/disables dragging of the subfocus item when dragging the mouse
   * pointer over the list (with the button depressed).  By default this property is true.
   */
  public void setDragSubfocus(boolean dragSubfocus) { core.setDragSubfocus(dragSubfocus); }
  public boolean isDragSubfocus() { return core.isDragSubfocus(); }

  /**
   * The snapOrigin property controls wether or not the list will automatically snap the
   * scroll position to align the top item with the top edge of the list.  If set to fasle,
   * the list will not snap the scroll position, and it will scroll smoothly with changes to
   * the subfocus item.  By default, this property is true.
   */
  public void setSnapOrigin(boolean snapOrigin) { core.setSnapOrigin(snapOrigin); }
  public boolean isSnapOrigin() { return core.isSnapOrigin(); }

  /**
   * The editInPlace property enables/disables item editing in the list.  By default, this
   * property is true, and a user can edit values in any item on the list.  If set to false,
   * the vector data cannot by modified by the user - except through programmatic access to
   * the list's model.
   */
  public void setEditInPlace(boolean editInPlace) { core.setEditInPlace(editInPlace); }
  public boolean isEditInPlace() { return core.isEditInPlace(); }

  /**
   * The postOnEndEdit property controls wether or not the list will post changes to an item back
   * to the model when the user clicks or tabs off of the item.  If false, editing an item's value
   * will not 'stick' unless committed by hitting ENTER - clicking off the item being edited will
   * revert the value back to its original state.  By default, this property is set to true.
   */
  public boolean isPostOnEndEdit() { return core.isPostOnEndEdit(); }
  public void setPostOnEndEdit(boolean post) { core.setPostOnEndEdit(post); }

  /**
   * The autoEdit property enables/disables automatic item editing in the list.  By default, this
   * property is true, and a user can edit values in any item on the list by typing a character on
   * the keyboard.  If set to false, the user must hit F2, Ctrl+Enter, or double click to start an
   * edit session.
   */
  public void setAutoEdit(boolean autoEdit) { core.setAutoEdit(autoEdit); }
  public boolean isAutoEdit() { return core.isAutoEdit(); }

  /**
   * The growEditor property enables/disables automatic sizing of the ItemEditor.  In some
   * look & feel settings, the ItemEditors will need to grow vertically in order to property edit
   * the data.  By default, this property is set to true.
   */
  public void setGrowEditor(boolean growEditor) { core.setGrowEditor(growEditor); }
  public boolean isGrowEditor() { return core.isGrowEditor(); }

  /**
   * The autoAppend property enables/disables automatic item appending at the end of the list.  By
   * default, this property is false, and a user must insert rows with the Insert key or by navigating
   * to the end of the list and pressing Ctrl+Down.  Setting this property to true allows the user
   * to append new items by navigating past the last item.
   */
  public void setAutoAppend(boolean autoAppend) { core.setAutoAppend(autoAppend); }
  public boolean isAutoAppend() { return core.isAutoAppend(); }

  /**
   * The batchMode property enables/disables all painting in the list.  This is used for programmatic
   * mass updates to the list's model, selection, or whatever - without triggering repaint messages.
   */
  public void setBatchMode(boolean batchMode) { core.setBatchMode(batchMode); }
  public boolean isBatchMode() { return core.isBatchMode(); }

  /**
   * The opaque property controls the grid's opacity.  If a texture is set,
   * the opaque property will automatically be 'true'.  By default, it is true.
   */
  public void setOpaque(boolean opaque) { 
    // workaround for new JScrollPane behavior in JDK 1.3, which invokes
    // setOpaque from within the default constructor.
    if (core != null) {
      core.setOpaque(opaque);
    }
  }
  public boolean isOpaque() { return core.isOpaque(); }

  /**
   * The texture property defines the background image texture.
   */
  public void setTexture(Image texture) { core.setTexture(texture); }
  public Image getTexture() { return core.getTexture(); }

  /**
   * The uniformWidth property specifies wether the data items coming from the VectorModel are all
   * the same width.  If true, painting calculations are much faster, as the items in the model
   * do not have to be queried to find their widths.  By default, this property is false, and the
   * list will scan the data to determine the correct scrollbar widths and locations.
   */
  public void setUniformWidth(boolean uniformWidth) { core.setUniformWidth(uniformWidth); }
  public boolean isUniformWidth() { return core.isUniformWidth(); }

  /**
   * The itemWidth property defines the width of the items in the VectorModel - this value is only
   * used if uniformWidth is set to true.  If uniformWidth is set to true, and the itemWidth is not
   * set (0 or less), the width of the first item in the VectorModel is used.
   */
  public void setItemWidth(int newWidth) { core.setItemWidth(newWidth); }
  public int getItemWidth() { return core.getItemWidth(); }

  /**
   * The uniformHeight property specifies wether the data items coming from the VectorModel are all
   * the same height.  If true, painting calculations are much faster, as the items in the model
   * do not have to be queried to find their heights.  By default, this property is true, and the
   * list will use the height of the first item in the model to calculate the height of all items.
   */
  public void setUniformHeight(boolean uniformHeight) { core.setUniformHeight(uniformHeight); }
  public boolean isUniformHeight() { return core.isUniformHeight(); }

  /**
   * The itemHeight property defines the height of the items in the VectorModel - this value is
   * only used if uniformHeight is set to true.  If uniformHeight is set to true, and the
   * itemHeight is not set (0 or less), the height of the first item in the VectorModel is used.
   */
  public void setItemHeight(int newHeight) { core.setItemHeight(newHeight); }
  public int getItemHeight() { return core.getItemHeight(); }

  /**
   * The topIndex property represents which item index is at the top-most of the visible items in
   * the list.  Setting this property will not move the subfocus item within the list, but will
   * scroll the list to the appropriate position.
   */
  public void setTopIndex(int index) { core.setTopIndex(index); }
  public int getTopIndex() { return core.getTopIndex(); }

  /**
   * The alignment property controls the alignment of the items displayed in the list.
   * @see com.borland.jbcl.util.Alignment for alignment settings.
   */
  public void setAlignment(int alignment) { core.setAlignment(alignment); }
  public int getAlignment() { return core.getAlignment(); }

  /**
   * The itemMargins property controls spacing between items in the list.
   */
  public void setItemMargins(Insets margins) { core.setItemMargins(margins); }
  public Insets getItemMargins() { return core.getItemMargins(); }

  public void setBackground(Color color) {
    super.setBackground(color);
    if (core != null)
      core.setBackground(color);
  }

  public void setForeground(Color color) {
    super.setForeground(color);
    if (core != null)
      core.setForeground(color);
  }

  public void setFont(Font font) {
    super.setFont(font);
    if (core != null)
      core.setFont(font);
  }

  // METHODS

  /**
   * Starts an edit session at 'index'.  If editInPlace is false or if readOnly
   * is true, this method is a no-op.
   * @param index The item index to start the edit session at.
   */
  public void startEdit(int index) {
    core.startEdit(index);
  }

  /**
   * Ends the current edit session (if any).  If the value has been modified, it will be posted if
   * postOnEndEdit is set to true (the default).  If postOnEndEdit is false, the edit session will
   * be terminated without saving the changes to the item's value.
   */
  public void endEdit() throws Exception { core.endEdit(); }

  /**
   * Ends the current edit session (if any).  If the value has been modified, it will be posted if
   * post is set to true.  If post is false, the edit session will be terminated without saving the
   * changes to the item's value.
   */
  public void endEdit(boolean post) throws Exception { core.endEdit(post); }

  /**
   * Ends the current edit session (if any), catching any exceptions.  If the value has been modified,
   * it will be posted if postOnEndEdit is set to true (the default).  If postOnEndEdit is false, the
   * edit session will be terminated without saving the changes to the item's value.
   */
  public void safeEndEdit() { core.safeEndEdit(); }

  /**
   * Ends the current edit session (if any), catching any exceptions.  If the value has been modified,
   * it will be posted if post is set to true.  If post is false, the edit session will be terminated
   * without saving the changes to the item's value.
   */
  public void safeEndEdit(boolean post) { core.safeEndEdit(post); }

  /**
   * The editing property (read only) returns true if an item is currently being edited in the list.
   */
  public boolean isEditing() { return core.isEditing(); }

  /**
   * If 'editor' property (read-only) returns the current ItemEditor being used in the list - or null
   * if the list is not currently editing.
   */
  public ItemEditor getEditor() { return core.getEditor(); }

  /**
   * Repaints the item at the passed index.
   * @param index The index that you want repainted.
   */
  public void repaintItem(int index) { core.repaintItem(index); }

  /**
   * Repaints all items in the list.
   */
  public void repaintItems() { core.repaintItems(); }

  /**
   * Returns the index of the item at the Y coordinate specified.  Coordinates are relative to
   * the entire scrollable region inside of the ListView.  Use getViewport().getViewPosition() and
   * getViewport().getExtentSize() to calculate relative point positions to external components.
   * @param y The y location.
   * @return The hit index, or -1 if nothing was hit.
   */
  public int hitTest(int y) { return core.hitTest(y); }

  /**
   * Returns the Rectangle (in pixels) that bounds the item at the specified index.  Coordinates
   * are relative to the entire scrollable region inside of the ListView.  Use getViewport().getViewPosition()
   * and getViewport().getExtentSize() to calculate relative point positions to external components.
   * @param index The item index that you want the rectangle for.
   * @return The bounding rectangle of the item.
   */
  public Rectangle getItemRect(int index) { return core.getItemRect(index); }

  public void checkParentWindow() { core.checkParentWindow(); }

  // EVENTS

  /**
   * A VectorModelListener will get notifications about changes to this list's data structure.
   */
  public void addModelListener(VectorModelListener listener) { core.addModelListener(listener); }
  public void removeModelListener(VectorModelListener listener) { core.removeModelListener(listener); }

  /**
   * A VectorSelectionListener will get notifications about changes to this list's selection pool.
   */
  public void addSelectionListener(VectorSelectionListener listener) { core.addSelectionListener(listener); }
  public void removeSelectionListener(VectorSelectionListener listener) { core.removeSelectionListener(listener); }

  /**
   * A VectorSubfocusListener will get notifications when the list's subfocus index changes.
   */
  public void addSubfocusListener(VectorSubfocusListener listener) { core.addSubfocusListener(listener); }
  public void removeSubfocusListener(VectorSubfocusListener listener) { core.removeSubfocusListener(listener); }

  /**
   * An ActionListener will get notifications when a user double-clicks an item, or hits ENTER to
   * post a change to an item.
   */
  public void addActionListener(ActionListener l) { core.addActionListener(l); }
  public void removeActionListener(ActionListener l) { core.removeActionListener(l); }

  public void addKeyListener(KeyListener l) { core.addKeyListener(l); }
  public void removeKeyListener(KeyListener l) { core.removeKeyListener(l); }

  public void addFocusListener(FocusListener l) {
    if (core != null) {
      core.addFocusListener(l);
    } else {
      super.addFocusListener(l);
    }
  }

  public void removeFocusListener(FocusListener l) { core.removeFocusListener(l); }

  public void addMouseListener(MouseListener l) { core.addMouseListener(l); }
  public void removeMouseListener(MouseListener l) { core.removeMouseListener(l); }

  public void addMouseMotionListener(MouseMotionListener l) { core.addMouseMotionListener(l); }
  public void removeMouseMotionListener(MouseMotionListener l) { core.removeMouseMotionListener(l); }

  public void addCustomItemListener(CustomItemListener l) { core.addCustomItemListener(l); }
  public void removeCustomItemListener(CustomItemListener l) { core.removeCustomItemListener(l); }

  // Deprecated methods, properties, etc.

  /** @DEPRECATED - Use autoEdit property */
  public void setAlwaysEdit(boolean autoEdit) { core.setAutoEdit(autoEdit); }
  /** @DEPRECATED - Use autoEdit property */
  public boolean isAlwaysEdit() { return core.isAutoEdit(); }
  /** @DEPRECATED - Use JComponent.paintImmediately(...) method */
  public void setDirectDraw(boolean direct) {}
  /** @DEPRECATED - Use JComponent.paintImmediately(...) method */
  public boolean isDirectDraw() { return false; }

  /** @DEPRECATED - Use getViewport().setViewPosition(Point vp) method */
  public void setScrollPosition(int x, int y) {
    getViewport().setViewPosition(new Point(x, y));
  }
  /** @DEPRECATED - Use getViewport().setViewPosition(Point vp) method */
  public void setScrollPosition(Point p) {
    getViewport().setViewPosition(p);
  }
  /** @DEPRECATED - Use getViewport().getViewPosition() method */
  public Point getScrollPosition() {
    return getViewport().getViewPosition();
  }
  /** @DEPRECATED - Use getViewport().getExtentSize() method */
  public Dimension getViewportSize() {
    return getViewport().getExtentSize();
  }
  /** @DEPRECATED - Use setDoubleBuffered(boolean buffer) method */
  public void setDoubleBuffer(boolean buffer) {
    setDoubleBuffered(buffer);
  }
  /** @DEPRECATED - Use isDoubleBuffered() method */
  public boolean isDoubleBuffer() {
    return isDoubleBuffered();
  }

  // Internal

  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    core.setEnabled(enabled);
    getHorizontalScrollBar().setEnabled(enabled);
    getVerticalScrollBar().setEnabled(enabled);
    core.repaint(100);
  }

  public void requestFocus() {
    core.requestFocus();
  }
  public boolean hasFocus() {
    return core.hasFocus();
  }

  public Dimension getPreferredSize() {
    Dimension ps = core.getPreferredSize();
    if (ps.width < 100)
      ps.width = 100;
    if (ps.height < 100)
      ps.height = 100;
    Insets in = getInsets();
    ps.width += in.left + in.right;
    ps.height += in.top + in.bottom;
    return ps;
  }

  public Dimension getMinimumSize() {
    // get the core's miminum size
    Dimension cms = core.getMinimumSize();
    // add column header height
    if (getColumnHeader() != null) {
      Component chv = getColumnHeader().getView();
      if (chv != null && chv.isVisible())
        cms.height += chv.getSize().height;
    }
    // add row header width
    if (getRowHeader() != null) {
      Component rhv = getRowHeader().getView();
      if (rhv != null && rhv.isVisible())
        cms.width += rhv.getSize().width;
    }
    // add horizontal scrollbar height
    JScrollBar hsb = getHorizontalScrollBar();
    if (hsb != null && hsb.isVisible())
      cms.height += hsb.getSize().height;
    // add vertical scrollbar width
    JScrollBar vsb = getVerticalScrollBar();
    if (vsb != null && vsb.isVisible())
      cms.width += vsb.getSize().width;
    return cms;
  }

  private ListCore core = new ListCore(this);
}
