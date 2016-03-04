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

import com.borland.jbcl.model.BasicTreeContainer;
import com.borland.jbcl.model.BasicViewManager;
import com.borland.jbcl.model.GraphLocation;
import com.borland.jbcl.model.GraphModel;
import com.borland.jbcl.model.GraphModelListener;
import com.borland.jbcl.model.GraphSelectionListener;
import com.borland.jbcl.model.GraphSubfocusListener;
import com.borland.jbcl.model.GraphViewManager;
import com.borland.jbcl.model.ItemEditor;
import com.borland.jbcl.model.SingleGraphSelection;
import com.borland.jbcl.model.WritableGraphModel;
import com.borland.jbcl.model.WritableGraphSelection;

public class TreeView extends JScrollPane implements GraphView, java.io.Serializable
{
  public static final int STYLE_PLUSES = 0x0001;
  public static final int STYLE_ARROWS = 0x0002;

  public TreeView() {
    super();
    super.setDoubleBuffered(true);
    getViewport().setView(core);
    getHorizontalScrollBar().setUnitIncrement(16);
    setModel(createDefaultModel());
    setViewManager(createDefaultViewManager());
    setSelection(createDefaultSelection());
    super.setBackground(UIManager.getColor("Tree.background")); 
    super.setBorder(UIManager.getBorder("Table.scrollPaneBorder")); 
    // for JDK 1.3, remove JScrollPane's default key action map
    // so GridCore can process its own keystrokes
    if (BeanPanel.is1dot3) {
      SwingUtilities.replaceUIActionMap(this, null);
    }
  }

  public void updateUI() {
    super.updateUI();
    setBackground(UIManager.getColor("Tree.background")); 
    super.setBorder(UIManager.getBorder("Table.scrollPaneBorder")); 
  }

  protected GraphModel createDefaultModel() {
    return new BasicTreeContainer();
  }

  protected GraphViewManager createDefaultViewManager() {
    return new BasicViewManager(
      new FocusableItemPainter(new SelectableItemPainter(new TextItemPainter())),
      new ExpandingTextItemEditor());
  }

  protected WritableGraphSelection createDefaultSelection() {
    return new SingleGraphSelection();
  }

  public void addNotify() {
    super.addNotify();
    if (isVisible())
      core.repaintNodes();
  }

  /**
   * Returns the contained TreeCore (non-public) class instance as a Component.
   */
  public Component getCoreComponent() { return core; }

  public void refresh() { core.refresh(); }

  /**
   * The model property defines the GraphModel that this tree is displaying data from.  If the
   * current model is an instance of WritableGraphModel, an external user can get access to it
   * using getWriteModel().
   */
  public void setModel(GraphModel model) { core.setModel(model); }
  public GraphModel getModel() { return core.getModel(); }
  public WritableGraphModel getWriteModel() { return core.getWriteModel(); }

  /**
   * The viewManager property defines the GraphViewManager that will 'broker' ItemPainters and
   * ItemEditors to this tree.
   */
  public void setViewManager(GraphViewManager viewManager) { core.setViewManager(viewManager); }
  public GraphViewManager getViewManager() { return core.getViewManager(); }

  /**
   * The readOnly property is used when the model property is actually a WritableGraphModel, but
   * the user wishes it to be treated as a read-only model.  This is commonly used to allow users
   * to browse, but not edit normally writable graphs of data.
   */
  public void setReadOnly(boolean readOnly) { core.setReadOnly(readOnly); }
  public boolean isReadOnly() { return core.isReadOnly(); }

  /**
   * The selection property defines a WritableGraphSelection manager.  This allows multiple
   * graph viewers to share the same selection pool - and for the tree to handle selection in
   * a generic manner.  This allows users to plug in their own implemention of a selection
   * manager to have custom selection behavior.
   */
  public void setSelection(WritableGraphSelection selection) { core.setSelection(selection); }
  public WritableGraphSelection getSelection() { return core.getSelection(); }

  /**
   * The showFocus property enables/disables the painting of the focus rectangle on the
   * current subfocus node.  In reality, the showFocus property turns on/off the FOCUSED
   * bit in the state information that is passed to the ItemPainter when a node is painted.
   * If an ItemPainter plugged into the tree ignores the FOCUSED bit, this property will
   * have no effect.  By default, showFocus is true.
   */
  public void setShowFocus(boolean show) { core.setShowFocus(show); }
  public boolean isShowFocus() { return core.isShowFocus(); }

  /**
   * The showRollover property enables/disables the repainting of the rollover item.  The
   * rollover item is the item that currently has the mouse floating over it.
   * If an ItemPainter plugged into the tree ignores the ROLLOVER bit, this property will
   * have no effect.  By default, showRollover is false.
   */
  public void setShowRollover(boolean showRollover) { core.setShowRollover(showRollover); }
  public boolean isShowRollover() { return core.isShowRollover(); }

  /**
   * The dataToolTip property enables/disables the automatic tooltip mechanism
   * to display the contents of the model (as text) in a tooltip window when
   * the mouse is floating over a node.  By default, this property is false.
   * If set to true, the text stored in the toolTipText property is discarded.
   */
  public void setDataToolTip(boolean dataToolTip) { core.setDataToolTip(dataToolTip); }
  public boolean isDataToolTip() { return core.isDataToolTip(); }

  public void setToolTipText(String text) { core.setToolTipText(text); }
  public String getToolTipText() { return core.getToolTipText(); }

  /**
   * The subfocus property defines the 'current' node (defined by a GraphLocation) in the tree.
   * This is the node that is receiving keyboard input.
   */
  public void setSubfocus(GraphLocation subfocus) { core.setSubfocus(subfocus); }
  public GraphLocation getSubfocus() { return core.getSubfocus(); }

  /**
   * The dragFocus property enables/disables dragging of the subfocus node when dragging the mouse
   * pointer over the tree (with the button depressed).  By default this property is true.
   */
  public void setDragSubfocus(boolean drag) { core.setDragSubfocus(drag); }
  public boolean isDragSubfocus() { return core.isDragSubfocus(); }

  /**
   * The postOnEndEdit property controls wether or not the tree will post changes to a node back
   * to the model when the user clicks or tabs off of the node.  If false, editing a node's value
   * will not 'stick' unless committed by hitting ENTER - clicking off the node being edited will
   * revert the value back to its original state.  By default, this property is set to true.
   */
  public void setPostOnEndEdit(boolean post) { core.setPostOnEndEdit(post); }
  public boolean isPostOnEndEdit() { return core.isPostOnEndEdit(); }

  /**
   * The snapOrigin property controls wether or not the list will automatically snap the
   * scroll position to align the top item with the top edge of the list.  If set to fasle,
   * the list will not snap the scroll position, and it will scroll smoothly with changes to
   * the subfocus item.  By default, this property is true.
   */
  public void setSnapOrigin(boolean snapOrigin) { core.setSnapOrigin(snapOrigin); }
  public boolean isSnapOrigin() { return core.isSnapOrigin(); }

  public void setHSnap(boolean hSnap) { core.setHSnap(hSnap); }
  public boolean isHSnap() { return core.isHSnap(); }

  /**
   * The editInPlace property enables/disables node editing in the tree.  By default, this
   * property is true, and a user can edit values in any node on the tree.  If set to false,
   * the matrix data cannot by modified by the user - except through programmatic access to
   * the tree's model.
   */
  public void setEditInPlace(boolean editInPlace) { core.setEditInPlace(editInPlace); }
  public boolean isEditInPlace() { return core.isEditInPlace(); }

  /**
   * The autoEdit property enables/disables automatic node editing in the tree.  By default, this
   * property is true, and a user can edit values in any node on the tree by typing a character on
   * the keyboard.  If set to false, the user must hit F2, Ctrl+Enter, or double click to start an
   * edit session.
   */
  public void setAutoEdit(boolean autoEdit) { core.setAutoEdit(autoEdit); }
  public boolean isAutoEdit() { return core.isAutoEdit(); }

  /**
   * The growEditor property enables/disables automatic sizing of an item's ItemEditor.  In some
   * look & feel settings, the ItemEditors will need to grow vertically in order to property edit
   * the data.  By default, this property is set to true.
   */
  public void setGrowEditor(boolean growEditor) { core.setGrowEditor(growEditor); }
  public boolean isGrowEditor() { return core.isGrowEditor(); }

  /**
   * The editing property (read only) returns true if the item is currently being edited.
   */
  public boolean isEditing() { return core.isEditing(); }

  /**
   * If 'editor' property (read-only) returns the current ItemEditor being used in the tree - or null
   * if the tree is not currently editing.
   */
  public ItemEditor getEditor() { return core.getEditor(); }

  /**
   * The batchMode property enables/disables all painting in the tree.  This is used for programmatic
   * mass updates to the tree's model, selection, or whatever - without triggering repaint messages.
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
   * @DEPRECATED
   * Implemented correctly in future version
   */
  public void setShowRoot(boolean show) { core.setShowRoot(show); }
  /**
   * @DEPRECATED
   * Implemented correctly in future version
   */
  public boolean isShowRoot() { return core.isShowRoot(); }

  public void setExpandByDefault(boolean expand) { core.setExpandByDefault(expand); }
  public boolean isExpandByDefault() { return core.isExpandByDefault(); }

  public void setStyle(int style) { core.setStyle(style); }
  public int getStyle() { return core.getStyle(); }

  public void setAlignment(int alignment) { core.setAlignment(alignment); }
  public int getAlignment() { return core.getAlignment(); }

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

  public void setItemMargins(Insets margins) { core.setItemMargins(margins); }
  public Insets getItemMargins() { return core.getItemMargins(); }

  public void setLeftMargin(int leftMargin) { core.setLeftMargin(leftMargin); }
  public int getLeftMargin() { return core.getLeftMargin(); }

  public void setTopMargin(int topMargin) { core.setTopMargin(topMargin); }
  public int getTopMargin() { return core.getTopMargin(); }

  public void setBoxSize(Dimension boxSize) { core.setBoxSize(boxSize); }
  public Dimension getBoxSize() { return core.getBoxSize(); }

  public void setVgap(int vgap) { core.setVgap(vgap); }
  public int getVgap() { return core.getVgap(); }

  public void setHIndent(int hIndent) { core.setHIndent(hIndent); }
  public int getHIndent() { return core.getHIndent(); }

  public void setItemOffset(int itemOffset) { core.setItemOffset(itemOffset); }
  public int getItemOffset() { return core.getItemOffset(); }

  public Rectangle getNodeRect(GraphLocation node) { return core.getNodeRect(node); }

  // Methods

  /**
   * Starts an edit session at 'node'.  If editInPlace is false or if readOnly is true,
   * this method is a no-op.
   * @param node The GraphLocation to start the edit session at.
   */
  public void startEdit(GraphLocation node) {
    core.startEdit(node);
  }

  /**
   * Ends the current edit session (if any).  If the value has been modified, it will be posted if
   * postOnEndEdit is set to true (the default).  If postOnEndEdit is false, the edit session will
   * be terminated without saving the changes to the cell value.
   */
  public void endEdit() throws Exception { core.endEdit(); }

  /**
   * Ends the current edit session (if any).  If the value has been modified, it will be posted if
   * post is set to true.  If post is false, the edit session will be terminated without saving the
   * changes to the cell value.
   */
  public void endEdit(boolean post) throws Exception { core.endEdit(post); }

  /**
   * Ends the current edit session (if any), catching any exceptions.  If the value has been modified,
   * it will be posted if postOnEndEdit is set to true (the default).  If postOnEndEdit is false, the
   * edit session will be terminated without saving the changes to the cell value.
   */
  public void safeEndEdit() { core.safeEndEdit(); }

  /**
   * Ends the current edit session (if any), catching any exceptions.  If the value has been modified,
   * it will be posted if post is set to true.  If post is false, the edit session will be terminated
   * without saving the changes to the cell value.
   */
  public void safeEndEdit(boolean post) { core.safeEndEdit(post); }

  public void requestFocus() {
    core.requestFocus();
  }
  public boolean hasFocus() {
    return core.hasFocus();
  }

  /**
   * Check if a particular node is expanded.
   * @param location The GraphLocation to check.
   * @return True if the location is expanded, false if not.
   */
  public boolean isExpanded(GraphLocation location) {
    return core.isExpanded(location);
  }

  /**
   * Expand a particular node.
   * @param location The GraphLocation to expand.
   */
  public void expand(GraphLocation location) {
    core.expand(location);
  }

  /**
   * Collapse a particular node.
   * @param location The GraphLocation to collapse.
   */
  public void collapse(GraphLocation location) {
    core.collapse(location);
  }

  /**
   * Toggle the state of a particular node.
   * @param location The GraphLocation to toggle.
   */
  public void toggleExpanded(GraphLocation location) {
    core.toggleExpanded(location);
  }

  /**
   * Expand a particular node and all of its children
   * @param location The GraphLocation to expand.
   */
  public void expandAll(GraphLocation location) {
    core.expandAll(location);
  }

  /**
   * Collapse a particular node and all of its children
   * @param location The GraphLocation to collapse.
   */
  public void collapseAll(GraphLocation location) {
    core.collapseAll(location);
  }

  public void repaintNode(GraphLocation location) { core.repaintNode(location); }
  public void repaintNodes() { core.repaintNodes(); }
  public GraphLocation hitTest(int xPos, int yPos) { return core.hitTest(xPos, yPos); }

  public void checkParentWindow() { core.checkParentWindow(); }

  // Event Sets

  public void addModelListener(GraphModelListener listener) { core.addModelListener(listener); }
  public void removeModelListener(GraphModelListener listener) { core.removeModelListener(listener); }

  public void addSelectionListener(GraphSelectionListener listener) { core.addSelectionListener(listener); }
  public void removeSelectionListener(GraphSelectionListener listener) { core.removeSelectionListener(listener); }

  public void addSubfocusListener(GraphSubfocusListener l)    { core.addSubfocusListener(l); }
  public void removeSubfocusListener(GraphSubfocusListener l) { core.removeSubfocusListener(l); }

  public void addTreeListener(TreeListener l) { core.addTreeListener(l); }
  public void removeTreeListener(TreeListener l) { core.removeTreeListener(l); }

  public void addActionListener(ActionListener l) { core.addActionListener(l); }
  public void removeActionListener(ActionListener l) { core.removeActionListener(l); }

  public void addFocusListener(FocusListener l) {
    if (core != null) {
      core.addFocusListener(l);
    } else {
      super.addFocusListener(l);
    }
  }
  public void removeFocusListener(FocusListener l) { core.removeFocusListener(l); }

  public void addKeyListener(KeyListener l) { core.addKeyListener(l); }
  public void removeKeyListener(KeyListener l) { core.removeKeyListener(l); }

  public void addMouseListener(MouseListener l) { core.addMouseListener(l); }
  public void removeMouseListener(MouseListener l) { core.removeMouseListener(l); }

  public void addMouseMotionListener(MouseMotionListener l) { core.addMouseMotionListener(l); }
  public void removeMouseMotionListener(MouseMotionListener l) { core.removeMouseMotionListener(l); }

  public void addCustomItemListener(CustomItemListener l) { core.addCustomItemListener(l); }
  public void removeCustomItemListener(CustomItemListener l) { core.removeCustomItemListener(l); }

  // Deprecated

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

  private TreeCore core = new TreeCore(this);
}
