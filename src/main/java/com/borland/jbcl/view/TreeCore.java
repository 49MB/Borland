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
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JToolTip;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import com.borland.dx.dataset.CustomPaintSite;
import com.borland.dx.text.Alignment;
import com.borland.jb.util.Diagnostic;
import com.borland.jbcl.model.GraphLocation;
import com.borland.jbcl.model.GraphModel;
import com.borland.jbcl.model.GraphModelEvent;
import com.borland.jbcl.model.GraphModelListener;
import com.borland.jbcl.model.GraphModelMulticaster;
import com.borland.jbcl.model.GraphSelectionEvent;
import com.borland.jbcl.model.GraphSelectionListener;
import com.borland.jbcl.model.GraphSelectionMulticaster;
import com.borland.jbcl.model.GraphSubfocusEvent;
import com.borland.jbcl.model.GraphSubfocusListener;
import com.borland.jbcl.model.GraphViewManager;
import com.borland.jbcl.model.ItemEditSite;
import com.borland.jbcl.model.ItemEditor;
import com.borland.jbcl.model.ItemPainter;
import com.borland.jbcl.model.LinkedTreeNode;
import com.borland.jbcl.model.NullGraphSelection;
import com.borland.jbcl.model.SubfocusEvent;
import com.borland.jbcl.model.ToggleItemEditor;
import com.borland.jbcl.model.WritableGraphModel;
import com.borland.jbcl.model.WritableGraphSelection;
import com.borland.jbcl.util.DottedLine;
import com.borland.jbcl.util.ImageLoader;
import com.borland.jbcl.util.ImageTexture;
import com.borland.jbcl.util.KeyMulticaster;
import com.borland.jbcl.util.SelectFlags;
import com.borland.jbcl.util.SerializableImage;
import com.borland.jbcl.util.TriState;

class TreeCore extends BeanPanel implements GraphSelectionListener,
    GraphModelListener, GraphView, KeyListener, FocusListener, ItemEditSite,
    Serializable {
  private static final long serialVersionUID = 200L;
  
  public TreeCore(JScrollPane hostScroller) {
    scroller = hostScroller;
    setFocusAware(true);
    super.addKeyListener(keyMulticaster);
    scroller.getVerticalScrollBar().setUnitIncrement(20);
    scroller.getHorizontalScrollBar().setUnitIncrement(10);
    super.setBackground(UIManager.getColor("Tree.background"));
  }
  
  public void updateUI() {
    super.updateUI();
    setBackground(UIManager.getColor("Tree.background"));
  }
  
  public void refresh() {
    updateViewState();
    Graphics g = getSiteGraphics();
    if (g != null) {
      updateNodeRects(firstNode, g);
      recalcVisible(true);
    }
    repaintNodes(); // JPBS
  }
  
  // Properties
  
  public int getStyle() {
    return style;
  }
  
  public void setStyle(int style) {
    this.style = style;
    if (style == TreeView.STYLE_ARROWS && expandedArrow == null) {
      contractedArrow = ImageLoader.loadFromResource(
          "image/contractedArrow.gif", this);
      expandedArrow = ImageLoader.loadFromResource("image/expandedArrow.gif",
          this);
    }
    if (style == TreeView.STYLE_ARROWS)
      setBoxSize(new Dimension(12, 12));
    else
      setBoxSize(new Dimension(9, 9));
    repaintNodes();
  }
  
  public boolean isExpandByDefault() {
    return expandByDefault;
  }
  
  public void setExpandByDefault(boolean expandByDefault) {
    this.expandByDefault = expandByDefault;
  }
  
  public Insets getItemMargins() {
    return margins;
  }
  
  public void setItemMargins(Insets margins) {
    this.margins = margins;
    invalidate();
    repaintNodes();
  }
  
  public int getLeftMargin() {
    return leftMargin;
  }
  
  public void setLeftMargin(int leftMargin) {
    this.leftMargin = leftMargin;
    refresh();
  }
  
  public int getTopMargin() {
    return topMargin;
  }
  
  public void setTopMargin(int topMargin) {
    this.topMargin = topMargin;
    refresh();
  }
  
  public int getAlignment() {
    return alignment;
  }
  
  public void setAlignment(int alignment) {
    this.alignment = alignment;
  }
  
  public void setBackground(Color color) {
    super.setBackground(color);
    repaintNodes();
  }
  
  public void setForeground(Color color) {
    super.setForeground(color);
    repaintNodes();
  }
  
  public void setFont(Font font) {
    super.setFont(font);
    refresh();
    if (isVisible() && !batchMode)
      validate();
  }
  
  public Dimension getBoxSize() {
    return boxSize;
  }
  
  public void setBoxSize(Dimension boxSize) {
    if (boxSize != null) {
      this.boxSize = boxSize;
      repaintNodes();
    }
  }
  
  public int getVgap() {
    return vgap;
  }
  
  public void setVgap(int vgap) {
    this.vgap = vgap;
    repaintNodes();
  }
  
  public int getHIndent() {
    return hIndent;
  }
  
  public void setHIndent(int hIndent) {
    this.hIndent = hIndent;
    repaintNodes();
  }
  
  public int getItemOffset() {
    return itemOffset;
  }
  
  public void setItemOffset(int itemOffset) {
    this.itemOffset = itemOffset;
    repaintNodes();
  }
  
  public boolean isSnapOrigin() {
    return snapOrigin;
  }
  
  public void setSnapOrigin(boolean snapOrigin) {
    this.snapOrigin = snapOrigin;
  }
  
  public void setDragSubfocus(boolean dragSubfocus) {
    this.dragSubfocus = dragSubfocus;
  }
  
  public boolean isDragSubfocus() {
    return dragSubfocus;
  }
  
  public boolean isHSnap() {
    return hSnap;
  }
  
  public void setHSnap(boolean hSnap) {
    this.hSnap = hSnap;
  }
  
  public void setShowRollover(boolean showRollover) {
    this.showRollover = showRollover;
  }
  
  public boolean isShowRollover() {
    return showRollover;
  }
  
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
  
  public boolean isEditInPlace() {
    return editInPlace;
  }
  
  public void setEditInPlace(boolean editInPlace) {
    this.editInPlace = editInPlace;
  }
  
  public boolean isAutoEdit() {
    return autoEdit;
  }
  
  public void setAutoEdit(boolean autoEdit) {
    this.autoEdit = autoEdit;
  }
  
  public void setGrowEditor(boolean growEditor) {
    this.growEditor = growEditor;
  }
  
  public boolean isGrowEditor() {
    return growEditor;
  }
  
  public boolean isEditing() {
    return editor != null;
  }
  
  public ItemEditor getEditor() {
    return editor;
  }
  
  public void setBatchMode(boolean batchMode) {
    if (this.batchMode != batchMode) {
      this.batchMode = batchMode;
      if (!this.batchMode) {
        repaintNodes();
        if (isShowing())
          scroller.validate();
      }
    }
  }
  
  public boolean isBatchMode() {
    return batchMode;
  }
  
  public GraphModel getModel() {
    return model;
  }
  
  public WritableGraphModel getWriteModel() {
    return readOnly ? null : writeModel;
  }
  
  public void setModel(GraphModel model) {
    if (model != this.model) {
      if (this.model != null) {
        this.model.removeModelListener(this);
        this.model.removeModelListener(modelMulticaster);
      }
      this.model = model;
      if (this.model != null) {
        this.model.addModelListener(this);
        this.model.addModelListener(modelMulticaster);
      }
      writeModel = model instanceof WritableGraphModel ? (WritableGraphModel) model
          : null;
      
      // Rebuild view state cache
      // Assume that the new model has new nodes, so don't keep state
      cache.clear();
      firstNode = null;
      subfocus = null;
      selectAnchor = null;
      updateViewState();
      Graphics g = getSiteGraphics();
      if (g != null) {
        updateNodeRects(firstNode, g);
        recalcVisible(true);
      }
      if (model != null)
        setSubfocus(model.getRoot());
    }
  }
  
  public Rectangle getNodeRect(GraphLocation node) {
    NodeState ns = getViewState(node);
    if (ns != null)
      return new Rectangle(ns.itemRect);
    else
      return null;
  }
  
  public void addModelListener(GraphModelListener l) {
    modelMulticaster.add(l);
  }
  
  public void removeModelListener(GraphModelListener l) {
    modelMulticaster.remove(l);
  }
  
  public boolean isReadOnly() {
    return readOnly ? true : writeModel == null;
  }
  
  public void setReadOnly(boolean ro) {
    readOnly = ro;
  }
  
  public boolean isPostOnEndEdit() {
    return postOnEndEdit;
  }
  
  public void setPostOnEndEdit(boolean post) {
    this.postOnEndEdit = post;
  }
  
  public boolean isShowFocus() {
    return showFocus;
  }
  
  public void setShowFocus(boolean show) {
    this.showFocus = show;
  }
  
  public boolean isShowRoot() {
    return showRoot;
  }
  
  public void setShowRoot(boolean showRoot) {
    this.showRoot = showRoot;
    repaintNodes();
    recalcVisible(true);
  }
  
  public GraphViewManager getViewManager() {
    return viewManager;
  }
  
  public void setViewManager(GraphViewManager newManager) {
    viewManager = newManager;
    repaintNodes();
  }
  
  public GraphLocation getSubfocus() {
    return subfocus != null ? subfocus.node : null;
  }
  
  public void setSubfocus(GraphLocation subfocus) {
    if (editor != null) {
      if (lockSubfocus)
        return;
      else
        safeEndEdit();
    }
    if (lockSubfocus || editor != null)
      return;
    NodeState sf = getViewState(subfocus);
    if (sf == null)
      return;
    
    if (!preProcessSubfocusEvent(new GraphSubfocusEvent(this,
        SubfocusEvent.SUBFOCUS_CHANGING, subfocus)))
      return;
    
    GraphLocation mom = sf.node.getParent();
    if (mom != null) {
      NodeState momnode = getViewState(mom);
      if (makeExpanded(momnode)) {
        updateNodeRects(momnode, getSiteGraphics());
        recalcVisible(true);
      }
    }
    subfocusChecked = true;
    setSubfocus(sf, SelectFlags.CLEAR | SelectFlags.ADD_ITEM
        | SelectFlags.RESET_ANCHOR);
  }
  
  protected void setSubfocus(NodeState nState, int flags) {
    if (nState == null || subfocus == nState)
      return;
    /*
     * System.err.print(++Diagnostic.count+"\tTreeCore.setSubfocus(\""+model.get(
     * nState.node)+"\") flags="); if ((flags & SelectFlags.CLEAR) != 0)
     * System.err.print(" CLEAR"); if ((flags & SelectFlags.ADD_ITEM) != 0)
     * System.err.print(" ADD_ITEM"); if ((flags & SelectFlags.TOGGLE_ITEM) !=
     * 0) System.err.print(" TOGGLE_ITEM"); if ((flags & SelectFlags.ADD_RANGE)
     * != 0) System.err.print(" ADD_RANGE"); if ((flags &
     * SelectFlags.RESET_ANCHOR) != 0) System.err.print(" RESET_ANCHOR");
     * System.err.println();
     */
    if (editor != null) {
      if (lockSubfocus)
        return;
      else
        safeEndEdit();
    }
    if (lockSubfocus || editor != null)
      return;
    
    if (subfocusChecked)
      subfocusChecked = false;
    else {
      if (!preProcessSubfocusEvent(new GraphSubfocusEvent(this,
          SubfocusEvent.SUBFOCUS_CHANGING, nState.node)))
        return;
    }
    
    if (subfocus == null) {
      subfocus = nState;
      selectAnchor = nState;
      if ((flags & SelectFlags.ADD_ITEM) != 0)
        selection.add(subfocus.node);
      scrollView();
      processSubfocusEvent(new GraphSubfocusEvent(this,
          SubfocusEvent.SUBFOCUS_CHANGED, subfocus.node));
      return;
    }
    
    if (selectAnchor == null)
      selectAnchor = nState;
    NodeState oldFocus = subfocus;
    subfocus = nState;
    
    boolean eventsOff = false;
    if ((flags & SelectFlags.CLEAR) != 0) {
      eventsOff = true;
      selection.enableSelectionEvents(false);
      selection.removeAll();
    }
    if ((flags & SelectFlags.ADD_ITEM) != 0)
      selection.add(subfocus.node);
    if ((flags & SelectFlags.TOGGLE_ITEM) != 0) {
      if (selection.contains(subfocus.node))
        selection.remove(subfocus.node);
      else
        selection.add(subfocus.node);
    }
    if ((flags & SelectFlags.RESET_ANCHOR) != 0)
      selectAnchor = subfocus;
    
    if (eventsOff)
      selection.enableSelectionEvents(true);
    
    scrollView();
    
    // repaint the old and new subfocus items (redraw focus rectangle)
    repaintNode(oldFocus);
    repaintNode(subfocus);
    processSubfocusEvent(new GraphSubfocusEvent(this,
        SubfocusEvent.SUBFOCUS_CHANGED, nState.node));
  }
  
  private void scrollView() {
    if (subfocus == null)
      return;
    Rectangle vRect = scroller.getViewport().getViewRect();
    Rectangle fRect = subfocus.itemRect;
    int x = vRect.x;
    int y = vRect.y;
    if (fRect != null) {
      if (fRect.width > vRect.width || fRect.x < vRect.x)
        x = fRect.x;
      else if (fRect.x + fRect.width > vRect.x + vRect.width)
        x = (getSize().width - vRect.width) < (fRect.x + fRect.width - vRect.width) ? getSize().width
            - vRect.width
            : fRect.x + fRect.width - vRect.width;
      if (fRect.y < vRect.y)
        y = fRect.y;
      else if (fRect.y + fRect.height > vRect.y + vRect.height) {
        y = (getSize().height - vRect.height) < (fRect.y + fRect.height - vRect.height) ? getSize().height
            - vRect.height
            : fRect.y + fRect.height - vRect.height;
        if (snapOrigin) {
          NodeState o = hitTestAbsY(y);
          if (o != null && o.nextVisible != null)
            y = o.nextVisible.itemRect.y;
        }
      }
      // push scroll as far left as possible
      if (x > 0 && fRect.width < vRect.width) {
        x = 0 > (fRect.x + fRect.width - vRect.width) ? 0 : fRect.x
            + fRect.width - vRect.width;
      }
      if (hSnap) {
        if (vRect.x != x || vRect.y != y)
          scroller.getViewport().setViewPosition(new Point(x, y));
      } else {
        if (vRect.y != y)
          scroller.getViewport().setViewPosition(new Point(vRect.x, y));
      }
      scroller.getHorizontalScrollBar().setUnitIncrement(fRect.width);
      scroller.getVerticalScrollBar().setUnitIncrement(fRect.height);
    }
  }
  
  public GraphLocation hitTest(int xPos, int yPos) {
    NodeState nState = hitTestY(yPos);
    if (nState != null && nState.hitTest(xPos, yPos) == 1)
      return nState.node;
    else
      return null;
  }
  
  NodeState hitTestXY(int xPos, int yPos) {
    NodeState nState = hitTestY(yPos);
    if (nState != null && nState.hitTest(xPos, yPos) == 1)
      return nState;
    else
      return null;
  }
  
  NodeState hitTestY(int y) {
    NodeState nState = firstNode;
    while (nState != null && nState.itemRect.y + nState.itemRect.height <= y)
      nState = nState.nextVisible;
    if (nState == null)
      nState = lastCalc;
    return nState;
  }
  
  NodeState hitTestAbsY(int y) {
    NodeState nState = firstNode;
    while (nState != null && nState.itemRect.y + nState.itemRect.height <= y)
      nState = nState.nextVisible;
    return nState;
  }
  
  public void addNotify() {
    super.addNotify();
    Graphics g = getSiteGraphics();
    if (g != null) {
      updateViewState();
      updateNodeRects(firstNode, g);
      recalcVisible(false); // don't prematurely validate
    }
  }
  
  NodeState hitTestLine(int lineNumber) {
    NodeState nState = firstNode;
    while (nState != null && nState.lineNumber < lineNumber)
      nState = nState.nextVisible;
    return nState;
  }
  
  void initialize(Graphics g) {
    if (!initialized) {
      initialized = true;
      updateViewState();
      // Expand nodes in presetExpandList
      for (Enumeration<GraphLocation> i = presetExpandList.elements(); i
          .hasMoreElements();) {
        NodeState nState = getViewState((GraphLocation) i.nextElement());
        if (nState != null)
          expandNode(nState, true);
      }
      // Collapse nodes in presetCollapseList
      for (Enumeration<GraphLocation> i = presetCollapseList.elements(); i
          .hasMoreElements();) {
        NodeState nState = getViewState((GraphLocation) i.nextElement());
        if (nState != null)
          expandNode(nState, false);
      }
      updateViewState();
      updateNodeRects(firstNode, g);
      recalcVisible(false);
    }
  }
  
  public void paintComponent(Graphics g) {
    if (batchMode)
      return;
    super.paintComponent(g);
    g.clipRect(0, 0, getSize().width, getSize().height);
    Rectangle vRect = scroller.getViewport().getViewRect();
    Rectangle c = g.getClipBounds();
    if (c == null)
      return;
    Rectangle clip;
    if (c.width > vRect.width || c.height > vRect.height)
      clip = c.intersection(vRect);
    else
      clip = c;
    if (clip.width <= 0 || clip.height <= 0 || vRect.width <= 0
        || vRect.height <= 0) {
      return;
    }
    g.setClip(clip.x, clip.y, clip.width, clip.height);
    NodeState first = hitTestY(clip.y);
    NodeState last = hitTestY(clip.y + clip.height - 1);
    NodeState nState = first;
    if (first == last)
      paintNode(g, first);
    else {
      for (nState = first; last != null && nState != last.nextVisible;) {
        paintNode(g, nState);
        if (nState.nextVisible == null)
          break; // quit before we step & fall out, in order to remember last
                 // nState
        nState = nState.nextVisible;
      }
    }
    
    // make sure that all links are drawn--even for nStates that were clipped
    // out
    if (style == TreeView.STYLE_PLUSES) {
      // for (nState = first; nState != last;) {
      for (nState = first; nState != null;) {
        NodeState next = getViewState(nState.getNextSibling());
        if (next != null)
          paintLink(g, next);
        if (nState.nextVisible == null)
          break; // quit before we step & fall out, in order to remember last
                 // nState
        nState = nState.nextVisible;
      }
    }
    
    // erase from last nState to bottom of visible area
    int y = (nState != null) ? nState.itemRect.y + nState.itemRect.height : 0;
    if (y < vRect.y + vRect.height) {
      if (texture != null)
        ImageTexture.texture(texture, g, vRect.x, y, vRect.width, vRect.height
            - y);
      else if (isOpaque()) {
        g.setColor(getBackground());
        g.fillRect(vRect.x, y, vRect.width, vRect.height - y);
      }
    }
    
    // For debugging purposes only
    // draws a colored rectangle around the clip rectangle
    // with diagonal hash lines (for tracing paint calls)
    if (debugPaint) {
      GridCore.debugRect(g, clip.x, clip.y, clip.width, clip.height);
    }
  }
  
  protected void paintNode(Graphics g, NodeState nState) {
    if (g != null && nState != null) {
      Dimension vp = scroller.getViewport().getExtentSize();
      if (texture != null)
        ImageTexture.texture(texture, g, 0, nState.itemRect.y, vp.width,
            nState.itemRect.height + vgap);
      else if (isOpaque()) {
        g.setColor(getBackground());
        g.fillRect(0, nState.itemRect.y, vp.width, nState.itemRect.height
            + vgap);
      }
      if (style == TreeView.STYLE_PLUSES) {
        paintBox(g, nState);
        paintItem(g, nState);
        paintLink(g, nState);
        paintVLines(g, nState);
      } else if (style == TreeView.STYLE_ARROWS) {
        paintArrow(g, nState);
        paintItem(g, nState);
      }
    }
  }
  
  protected void paintBox(Graphics g, NodeState nState) {
    if (nState.node != null && nState.node.hasChildren() != TriState.NO) {
      int x = nState.boxRect.x;
      int y = nState.boxRect.y;
      if (texture != null)
        ImageTexture.texture(texture, g, x, y, boxSize.width - 1,
            boxSize.height - 1);
      else if (isOpaque()) {
        g.setColor(SystemColor.window);
        g.fillRect(x, y, boxSize.width - 1, boxSize.height - 1);
      }
      g.setColor(isEnabled() ? SystemColor.controlShadow
          : SystemColor.controlShadow.brighter());
      DottedLine.drawHLine(g, x + boxSize.width + 1, x + itemOffset, y
          + boxSize.width / 2);
      g.drawRect(x, y, boxSize.width - 1, boxSize.height - 1);
      g.setColor(isEnabled() ? SystemColor.controlText
          : SystemColor.controlText.brighter());
      if (!nState.expanded)
        // paint a "plus" sign if not expanded
        g.drawLine(x + boxSize.width / 2, y + 2, x + boxSize.width / 2, y
            + boxSize.height - 3);
      // Otherwise this is a "minus" sign
      g.drawLine(x + 2, y + boxSize.height / 2, x + boxSize.width - 3, y
          + boxSize.height / 2);
    }
  }
  
  protected void paintArrow(Graphics g, NodeState nState) {
    if (nState.node != null && nState.node.hasChildren() != TriState.NO) {
      Image arrow = nState.expanded ? expandedArrow : contractedArrow;
      if (arrow != null)
        g.drawImage(arrow, nState.boxRect.x, nState.boxRect.y, this);
    }
  }
  
  protected void paintItem(Graphics g, NodeState nState) {
    if (viewManager != null) {
      int state = getState(nState);
      Object data = model.get(nState.node);
      ItemPainter itemPainter = getPainter(nState, data, state);
      if (itemPainter != null) {
        Dimension dim = itemPainter.getPreferredSize(data, g, state, this);
        nState.itemRect.width = dim.width;
        nState.itemRect.height = dim.height;
        g.setColor(getBackground());
        Rectangle itemRect = new Rectangle(nState.itemRect.x,
            nState.itemRect.y, dim.width, dim.height);
        itemPainter.paint(data, g, itemRect, state, this);
      }
    }
  }
  
  protected void paintLink(Graphics g, NodeState nState) {
    int x = boxSize.width / 2 + hIndent * nState.level + leftMargin;
    int y1 = nState.node.hasChildren() != TriState.NO ? nState.boxRect.y
        : nState.itemRect.y + nState.itemRect.height / 2;
    int y0 = y1;
    NodeState prev = nState.prevVisible;
    // System.err.println("painting nState=" + nState + " prev=" + prev);
    // System.err.println("  y1=" + y1);
    if (prev != null) {
      // Previous item is a sibling or a sibling's child
      if (prev.level >= nState.level) {
        // walk up until we hit the sibling
        while (prev.level > nState.level && prev != null)
          prev = prev.prevVisible;
        
        if (prev.node.hasChildren() == TriState.NO) {
          y0 = prev.itemRect.y + prev.itemRect.height / 2;
          // System.err.println("  y0=" + y0 + " ==,no");
        } else {
          y0 = prev.boxRect.y + prev.boxRect.height;
          // System.err.println("  y0=" + y0 + " ==,!no");
        }
      } else if (prev.expanded || prev.node.hasChildren() != TriState.UNKNOWN) {
        // Previous item is our parent
        GraphLocation[] kids = prev.node.getChildren();
        if (kids.length > 0 && kids[0] == nState.node) {
          y0 = prev.itemRect.y + prev.itemRect.height;
          // System.err.println("  y0=" + y0 + " !=,first kid");
        } else {
          if (nState.node.getParent() == null) {
            // No such case (we are now a single root tree)
            return;
          } else {
            // Connecting with the parent
            // GraphLocation[] children = nState.node.getParent().getChildren();
            // GraphLocation location = null;
            // while (it.hasMoreElements() && (location =
            // (GraphLocation)it.nextElement()) != null &&
            // location.getNextSibling() != nState.node)
            // ;
            NodeState parent = getViewState(nState.node.getParent());
            if (parent == null)
              // This can happen if property change is not setup properly
              return;
            y0 = parent.boxRect.y + parent.boxRect.height;
            // System.err.println("  y0=" + y0 + " !=,!first kid");
          }
        }
      }
    } else
      y0 = y1;
    g.setColor(isEnabled() ? SystemColor.controlShadow
        : SystemColor.controlShadow.brighter());
    DottedLine.drawVLine(g, x, y0, y1);
    if (nState.node.hasChildren() == TriState.NO) {
      int x2 = nState.itemRect.x;
      int y = nState.itemRect.y + nState.itemRect.height / 2;
      DottedLine.drawHLine(g, x, x2, y);
    }
  }
  
  // Paints vertical lines that connect this node's parent (all the way up the
  // chain)
  // with it's siblings.
  //
  protected void paintVLines(Graphics g, NodeState nState) {
    if (g == null || nState == null)
      return;
    NodeState parent = nState;
    for (int lev = nState.level - 1; lev > 0 && parent != null; lev--) {
      parent = getViewState(parent.node.getParent());
      if (parent != null && parent.getNextSibling() != null) {
        int x = boxSize.width / 2 + hIndent * lev + leftMargin;
        g.setColor(isEnabled() ? SystemColor.controlShadow
            : SystemColor.controlShadow.brighter());
        DottedLine.drawVLine(g, x, nState.itemRect.y, nState.itemRect.y
            + nState.itemRect.height + 1);
      }
    }
  }
  
  /**
   * The painter maintains an internal data structure to hold the UI state for
   * each nState.
   */
  protected NodeState getViewState(GraphLocation gl) {
    if (gl == null)
      return null;
    NodeState nState = null;
    try {
      nState = (NodeState) cache.get(gl);
      return nState;
    } catch (Exception e) {
      /*
       * NodeState nState = null; NodeState pnState = null; GraphLocation
       * parent; if (gl != null && (parent = gl.getParent()) != null) { try {
       * pnState = (NodeState)cache.get(parent); } catch (Exception x) {
       * Diagnostic.println("FAILED: pnState = (NodeState)cache.get(parent);");
       * return null; } nState = new NodeState(this, gl, pnState.level + 1,
       * expandByDefault && gl.hasChildren() == TriState.YES); putViewState(gl,
       * nState); } Diagnostic.println("nState=" + nState);
       */
      updateViewState();
      
      try {
        nState = (NodeState) cache.get(gl);
        return nState;
      } catch (Exception x) {
        return null;
      }
    }
  }
  
  protected void putViewState(GraphLocation gl, NodeState state) {
    if (gl != null && cache.get(gl) == null) {
      cache.put(gl, state);
    }
  }
  
  /**
   * Make sure the view state cache is synced with the model's graph update
   * firstNode, remove obsolete nStates, create nStates as needed nStates will
   * end up with accurate graph information, new nStates get default view info -
   * nState will be accurate - level will be accurate
   */
  protected void updateViewState() {
    if (initialized && model != null) {
      GraphLocation root = model.getRoot();
      if (root != null) {
        markViewState(root);
        updateViewState(root, showRoot ? 0 : -1);
        cleanViewState(root);
        if (firstNode == null || !firstNode.inUse)
          firstNode = getViewState(root);
      }
    }
  }
  
  /**
   * Clear all the in-use marks for view state nStates in preparation for an
   * update & clean
   */
  protected void markViewState(GraphLocation gl) {
    for (Enumeration<NodeState> i = cache.elements(); i.hasMoreElements();)
      ((NodeState) i.nextElement()).inUse = false;
  }
  
  /**
   * Remove viewState nStates that haven't been updated & who's data nStates are
   * gone
   */
  protected void cleanViewState(GraphLocation gl) {
    Object key = null;
    for (Enumeration<GraphLocation> i = cache.keys(); i.hasMoreElements();) {
      key = i.nextElement();
      NodeState viewState = (NodeState) cache.get(key);
      if (!viewState.inUse && model.get(viewState.node) == null) {
        // System.err.println("sweepViewState removing " + viewState);
        cache.remove(key);
      }
    }
  }
  
  /**
   * Inner recursion method for the outer updateViewState()
   */
  protected void updateViewState(GraphLocation gl, int level) {
    if (gl != null) {
      NodeState nState = getViewState(gl);
      // System.err.println("updateViewState " + gl + "(" + gl.hasChildren() +
      // ")," + level + " : " + nState);
      if (nState == null || !nState.isDescendentOf(model.getRoot())) {
        // TriState.UNKNOWN to GraphLocation.hasChildren(). Those nStates
        // will not be expanded by default.
        nState = new NodeState(this, gl, level, expandByDefault
            && gl.hasChildren() == TriState.YES);
        putViewState(gl, nState);
      } else {
        nState.update(level);
      }
      
      // System.err.println("updateViewState " + gl + "(" + gl.hasChildren() +
      // ")," + level + " : " + nState);
      if (!nState.expanded)
        return;
      
      GraphLocation[] children = nState.node.getChildren();
      for (int i = 0; i < children.length; i++) {
        if (children[i] == null) {
          continue;
        }
        // System.err.println("                    ".substring(0,2+level*2) +
        // "child " + cgl);
        NodeState childNodeState = getViewState(children[i]);
        if (childNodeState == null) {
          // TriState.UNKNOWN to GraphLocation.hasChildren(). Those nStates
          // will not be expanded by default.
          putViewState(children[i], childNodeState = new NodeState(this,
              children[i], level + 1, expandByDefault
                  && children[i].hasChildren() == TriState.YES));
        } else {
          childNodeState.update(level + 1);
        }
        if (nState.expanded) {
          updateViewState(children[i], level + 1);
        }
      }
    }
  }
  
  /**
   * Update the rectangle dimension information for a give nStatestate
   */
  protected void updateNodeRects(NodeState nState, Graphics g) {
    if (nState != null) {
      updateNodeRect(nState, g);
      if (!nState.expanded)
        return;
      GraphLocation[] children = nState.node.getChildren();
      for (int i = 0; i < children.length; i++) {
        if (children[i] == null)
          continue;
        NodeState childNodeState = getViewState(children[i]);
        if (childNodeState != null)
          updateNodeRects(childNodeState, g);
      }
    }
  }
  
  protected void updateNodeRect(NodeState nState, Graphics g) {
    if (nState == null || model == null || viewManager == null)
      return;
    int state = getState(nState);
    Object data = model.get(nState.node);
    ItemPainter itemPainter = getPainter(nState, data, state);
    if (itemPainter != null) {
      Dimension dim = itemPainter.getPreferredSize(data, g, state, this);
      nState.itemRect.width = dim.width;
      nState.itemRect.height = dim.height;
    }
  }
  
  /**
   * Repaint the visible rectangle for a nState
   */
  protected void repaintNode(NodeState nState) {
    if (!batchMode && nState != null && isVisible(nState)) {
      int x = nState.node.hasChildren() == TriState.NO ? nState.itemRect.x
          : nState.boxRect.x;
      repaint(x, nState.itemRect.y, getSize().width - x, nState.itemRect.height);
    }
  }
  
  public void repaintNode(GraphLocation location) {
    repaintNode(getViewState(location));
  }
  
  public void repaintNodes() {
    if (batchMode)
      return;
    // Graphics g = getGraphics();
    // if (g != null) {
    // Rectangle vRect = scroller.getViewport().getViewRect();
    // g.setClip(vRect.x, vRect.y, vRect.width, vRect.height);
    // paint(g);
    // }
    repaint(100);
  }
  
  protected void repaintToEnd(NodeState nState) {
    if (batchMode || nState == null)
      return;
    int y = nState.itemRect.y;
    repaint(0, y, getSize().width, getSize().height - y);
  }
  
  public Dimension getPreferredSize() {
    if (!initialized)
      initialize(getGraphics());
    return new Dimension(leftMargin + canvasWidth, topMargin + canvasHeight);
  }
  
  public Dimension getMinimumSize() {
    return new Dimension(20, 20);
  }
  
  private boolean canSet(GraphLocation node) {
    return isReadOnly() ? false : writeModel.canSet(node);
  }
  
  // MouseEvents
  
  protected void processMousePressed(MouseEvent e) {
    boolean hadFocus = hasFocus;
    hasFocus = true;
    super.processMousePressed(e);
    int x = e.getX();
    int y = e.getY();
    boolean shift = e.isShiftDown();
    boolean control = e.isControlDown();
    boolean alt = e.isAltDown();
    boolean right = e.isMetaDown();
    int flags;
    NodeState current = hitTestY(y);
    if (current == null)
      return;
    rollover = null;
    if (subfocus == null)
      setSubfocus(current.node);
    
    lastClickPosition = current.hitTest(x, y);
    switch (lastClickPosition) {
    case 2: // HIT_BOX
      mouseDown = null;
      boolean expand = !current.expanded;
      Point sp = scroller.getViewport().getViewPosition();
      expandNode(current, expand);
      recalcVisible(true);
      repaintToEnd(current);
      scroller.getViewport().setViewPosition(sp);
      if (!expand) { // the nState was collapsed
        if (subfocus != null && subfocus.isDescendentOf(current.node))
          setSubfocus(current, SelectFlags.CLEAR | SelectFlags.ADD_ITEM
              | SelectFlags.RESET_ANCHOR);
      }
      break;
    case 1: // HIT_ITEM
      mouseDown = current;
      if (editor != null) {
        if (editorNode.equals(current))
          return;
        else
          safeEndEdit();
      }
      if (current == subfocus) {
        if (!right && e.getClickCount() == 2)
          fireActionEvent();
        if (!control && !shift && !alt) {
          if (hadFocus && !selection.contains(current.node)) {
            selection.removeAll();
            selection.add(current.node);
          } else if (!hadFocus) {
            selection.removeAll();
            selection.add(current.node);
          }
        } else if (control && !shift) {
          if (selection.contains(current.node))
            selection.remove(current.node);
          else
            selection.add(current.node);
        }
        if (hadFocus && !right && !control && !shift && !isToggleItem(current)
            && canSet(current.node)) {
          doStartEdit = true;
          return;
        }
      }
      if (shift && control)
        flags = SelectFlags.ADD_ITEM;
      else if (shift)
        flags = SelectFlags.ADD_ITEM;
      else if (control)
        flags = SelectFlags.TOGGLE_ITEM | SelectFlags.RESET_ANCHOR;
      else
        flags = SelectFlags.CLEAR | SelectFlags.ADD_ITEM
            | SelectFlags.RESET_ANCHOR;
      setSubfocus(current, flags);
      break;
    }
  }
  
  protected void processMouseDragged(MouseEvent e) {
    rollover = null;
    if (dragSubfocus && !e.isMetaDown()) {
      NodeState current = hitTestY(e.getY());
      if (current != null) {
        int flags = e.isControlDown() ? SelectFlags.RESET_ANCHOR
            : SelectFlags.CLEAR | SelectFlags.ADD_ITEM;
        setSubfocus(current, flags);
      }
    }
  }
  
  boolean doStartEdit = false;
  
  protected void processMouseReleased(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();
    boolean shift = e.isShiftDown();
    boolean control = e.isControlDown();
    boolean right = e.isMetaDown();
    NodeState current = hitTestY(y);
    if (current != null && current == mouseDown && current.hitTest(x, y) == 1) {
      editClickPoint = new Point(x, y);
      if (!right && (doStartEdit || isToggleItem(current))) {
        startEdit(current);
        doStartEdit = false;
      } else if (!right && !shift && !control && selection.getCount() > 1) {
        selection.removeAll();
        selection.add(current.node);
      }
      editClickPoint = null;
    }
    mouseDown = null;
  }
  
  protected void processMouseMoved(MouseEvent e) {
    if (showRollover) {
      NodeState hit = hitTestXY(e.getX(), e.getY());
      if (hit != rollover) {
        NodeState oldRollover = rollover;
        rollover = hit;
        repaintNode(oldRollover);
        repaintNode(rollover);
      }
    }
  }
  
  public JToolTip createToolTip() {
    return toolTip;
  }
  
  public String getToolTipText(MouseEvent e) {
    if (toolTip.active) {
      NodeState hit = hitTestXY(e.getX(), e.getY());
      if (hit != null && model != null && viewManager != null) {
        Object data = model.get(hit.node);
        if (data != null) {
          Rectangle r = getNodeRect(hit.node);
          Rectangle vp = scroller.getViewport().getViewRect();
          if (r != null
              && (!vp.contains(r.x, r.y) || !vp.contains(r.x + r.width - 1, r.y
                  + r.width - 1))) {
            int state = getState(hit);
            toolTip.data = data;
            toolTip.painter = getPainter(hit, data, state);
            toolTip.state = state;
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
      NodeState hit = hitTestXY(e.getX(), e.getY());
      if (hit != null) {
        Rectangle r = getNodeRect(hit.node);
        if (r != null && model != null && viewManager != null) {
          Object data = model.get(hit.node);
          int state = getState(hit);
          toolTip.data = data;
          toolTip.painter = getPainter(hit, data, state);
          toolTip.state = state;
          return new Point(r.x - 1, r.y - 1);
        }
      }
    }
    toolTip.painter = null;
    return null;
  }
  
  protected void processMouseExited(MouseEvent e) {
    if (showRollover) {
      NodeState oldRollover = rollover;
      rollover = null;
      repaintNode(oldRollover);
    }
  }
  
  // Key events
  
  // keyPressed on embedded editor
  //
  public void keyPressed(KeyEvent e) {
    if (editor == null || e.isConsumed())
      return;
    switch (e.getKeyCode()) {
    case KeyEvent.VK_ENTER:
      safeEndEdit(true);
      if (!lockSubfocus) {
        e.consume();
        // fireActionEvent();
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
      safeEndEdit();
      processKeyPressed(e); // pass it on
      break;
    case KeyEvent.VK_LEFT:
    case KeyEvent.VK_RIGHT:
      if (e.isControlDown()) {
        safeEndEdit();
        processKeyPressed(e); // pass it on
      }
    }
  }
  
  public void keyReleased(KeyEvent e) {
  }
  
  public void keyTyped(KeyEvent e) {
  }
  
  // keyPressed on TreeCore (not embedded editor)
  //
  protected void processKeyPressed(KeyEvent e) {
    int key = e.getKeyCode();
    boolean control = e.isControlDown();
    boolean shift = e.isShiftDown();
    boolean alt = e.isAltDown();
    int flags;
    
    if (subfocus == null && model != null)
      setSubfocus(model.getRoot());
    
    if (shift && control)
      // flags = SelectFlags.ADD_RANGE;
      flags = SelectFlags.ADD_ITEM;
    else if (shift)
      // flags = SelectFlags.ADD_RANGE;
      flags = SelectFlags.ADD_ITEM;
    else if (control)
      flags = SelectFlags.RESET_ANCHOR;
    else
      flags = SelectFlags.CLEAR | SelectFlags.ADD_ITEM
          | SelectFlags.RESET_ANCHOR;
    
    if (subfocus == null)
      subfocus = firstNode;
    
    switch (key) {
    case KeyEvent.VK_HOME:
      setSubfocus(hitTestLine(0), flags);
      e.consume();
      break;
    case KeyEvent.VK_END:
      setSubfocus(hitTestLine(lineCount - 1), flags);
      e.consume();
      break;
    case KeyEvent.VK_UP:
      if (subfocus != null) {
        setSubfocus(subfocus.prevVisible, flags);
        e.consume();
      }
      break;
    case KeyEvent.VK_DOWN:
      if (subfocus != null) {
        setSubfocus(subfocus.nextVisible, flags);
        e.consume();
      }
      break;
    case KeyEvent.VK_PAGE_UP:
      if (subfocus != null && lineCount > 0 && canvasHeight > 0) {
        int avgheight = canvasHeight / lineCount;
        int pageHeight = scroller.getViewport().getExtentSize().height
            / avgheight;
        NodeState item = subfocus;
        while (item.prevVisible != null && --pageHeight > 0)
          item = item.prevVisible;
        setSubfocus(item, flags);
        e.consume();
      }
      break;
    case KeyEvent.VK_PAGE_DOWN:
      if (subfocus != null && lineCount > 0 && canvasHeight > 0) {
        int avgheight = canvasHeight / lineCount;
        int pageHeight = scroller.getViewport().getExtentSize().height
            / avgheight;
        NodeState item = subfocus;
        while (item.nextVisible != null && --pageHeight > 0)
          item = item.nextVisible;
        setSubfocus(item, flags);
        e.consume();
      }
      break;
    case KeyEvent.VK_LEFT:
      if (subfocus != null
          && (!subfocus.expanded || subfocus.node.hasChildren() == TriState.NO)) {
        GraphLocation n = subfocus.node.getParent();
        setSubfocus(getViewState(n), flags);
        e.consume();
        break;
      }
      // fall thru
    case KeyEvent.VK_SUBTRACT:
      if (!alt && !shift && !control && subfocus != null && subfocus.expanded) {
        expandNode(subfocus, false);
        recalcVisible(true);
        repaintToEnd(subfocus);
        e.consume();
      }
      break;
    case KeyEvent.VK_RIGHT:
      if (subfocus != null && subfocus.expanded
          && subfocus.node.hasChildren() != TriState.NO) {
        setSubfocus(subfocus.nextVisible, flags);
        e.consume();
        break;
      }
      // fall thru
    case KeyEvent.VK_ADD:
      if (!alt && !shift && !control && subfocus != null && !subfocus.expanded
          && subfocus.node.hasChildren() != TriState.NO) {
        expandNode(subfocus, true);
        recalcVisible(true);
        repaintToEnd(subfocus);
        e.consume();
      }
      break;
    /*
     * case KeyEvent.VK_MULTIPLY: if (!alt && !control && subfocus != null &&
     * !subfocus.expanded && subfocus.node.hasChildren() != TriState.NO) { if
     * (shift) collapseAll(subfocus.node); else expandAll(subfocus.node);
     * recalcVisible(true); repaintToEnd(subfocus); e.consume(); } break;
     */
    case KeyEvent.VK_SPACE:
      if (subfocus == null)
        break;
      if (!shift && !alt) {
        if (control && selection.contains(subfocus.node))
          selection.remove(subfocus.node);
        else
          selection.add(subfocus.node);
        if (isToggleItem(subfocus))
          startEdit(subfocus);
        e.consume();
      }
      break;
    case KeyEvent.VK_ENTER:
      if (control && editor == null && canSet(subfocus.node))
        startEdit(subfocus);
      else
        fireActionEvent();
      e.consume();
      break;
    case KeyEvent.VK_F2:
      if (editor == null && !isToggleItem(subfocus) && canSet(subfocus.node)) {
        startEdit(subfocus);
        e.consume();
      }
      break;
    case KeyEvent.VK_J: // debug painting
      if (shift && control && alt)
        debugPaint = !debugPaint;
      break;
    default:
      return;
    }
  }
  
  // keyTyped on TreeCore (not embedded editor)
  // This should only be printable characters...
  //
  protected void processKeyTyped(KeyEvent e) {
    char kChar = e.getKeyChar();
    if (editor != null || !autoEdit || e.isConsumed() || isReadOnly()
        || kChar == 0 || kChar == '\t' || kChar == '\r' || kChar == '\n'
        || kChar == ' ' || kChar == 27
        || // ESCAPE
        kChar == '+' || kChar == '-' || isToggleItem(subfocus) || e.isAltDown()
        || e.isControlDown() || !writeModel.canSet(subfocus.node))
      return;
    startEdit(subfocus);
    Component eComp = null;
    if (editor != null && (eComp = editor.getComponent()) != null) {
      eComp.dispatchEvent(e);
    }
  }
  
  // Tree events
  
  public void addTreeListener(TreeListener l) {
    treeListeners.add(l);
  }
  
  public void removeTreeListener(TreeListener l) {
    treeListeners.remove(l);
  }
  
  protected void processTreeEvent(TreeEvent e) {
    // System.err.println("TreeEvent=" + e);
    if (treeListeners.hasListeners())
      treeListeners.dispatch(e);
  }
  
  public void windowActiveChanged(boolean active) {
    super.windowActiveChanged(active);
    repaintSelection();
  }
  
  // Focus events (on embedded editor)
  
  public void focusGained(FocusEvent e) {
  }
  
  public void focusLost(FocusEvent e) {
    // safeEndEdit();
    if (hasFocus) {
      hasFocus = false;
      repaintNode(subfocus);
    }
  }
  
  // Focus events (on TreeCore itself)
  
  protected void processFocusEvent(FocusEvent e) {
    super.processFocusEvent(e);
    switch (e.getID()) {
    case FocusEvent.FOCUS_GAINED:
      if (editor != null && editor.getComponent() != null)
        editor.getComponent().requestFocus();
      if (!hasFocus)
        hasFocus = true;
      break;
    case FocusEvent.FOCUS_LOST:
      if (editor == null)
        hasFocus = false;
      break;
    }
    if (model != null && subfocus == null)
      setSubfocus(model.getRoot());
    repaintNode(subfocus);
  }
  
  // Subfocus events
  
  public void addSubfocusListener(GraphSubfocusListener l) {
    subfocusListeners.add(l);
  }
  
  public void removeSubfocusListener(GraphSubfocusListener l) {
    subfocusListeners.remove(l);
  }
  
  protected boolean preProcessSubfocusEvent(GraphSubfocusEvent e) {
    if (subfocusListeners.hasListeners())
      return subfocusListeners.vetoableDispatch(e);
    return true;
  }
  
  protected void processSubfocusEvent(GraphSubfocusEvent e) {
    if (subfocusListeners.hasListeners())
      subfocusListeners.dispatch(e);
  }
  
  private ItemPainter getPainter(NodeState ns, Object data, int state) {
    ItemPainter painter = viewManager != null ? viewManager.getPainter(ns.node,
        data, state) : null;
    if (painter != null && customizeListeners != null) {
      customPainter.setPainter(painter);
      fireCustomizeItemEvent(ns.node, data, state, customPainter);
      return customPainter;
    }
    return painter;
  }
  
  private ItemEditor getEditor(NodeState ns, Object data, int state) {
    ItemEditor editor = viewManager != null ? viewManager.getEditor(ns.node,
        data, state) : null;
    if (editor != null && customizeEditors && customizeListeners != null) {
      customEditor.setEditor(editor);
      fireCustomizeItemEvent(ns.node, data, state, customEditor);
      return customEditor;
    }
    return editor;
  }
  
  protected void repaintSelection() {
    for (Enumeration<NodeState> i = cache.elements(); i.hasMoreElements();) {
      NodeState n = (NodeState) i.nextElement();
      boolean selected = selection.contains(n.node);
      if (n.selected != selected) {
        n.selected = selected;
        repaintNode(n);
      } else if (n.selected)
        repaintNode(n);
    }
    // GraphLocation[] selections = selection.getAll();
    // for (int i = 0; i < selections.length; i++)
    // repaintNode(selections[i]);
  }
  
  // Selection change event
  
  public void selectionItemChanged(GraphSelectionEvent e) {
    // System.err.println("selectionItemChanged " + e);
    NodeState n = getViewState(e.getLocation());
    if (n != null) {
      if (subfocus == null)
        subfocus = n;
      n.selected = selection.contains(n.node);
      repaintNode(n);
    }
  }
  
  public void selectionChanged(GraphSelectionEvent e) {
    // System.err.println("selectionChanged " + e);
    repaintSelection();
  }
  
  // Internal functions
  
  void recalcVisible(boolean shouldValidate) {
    int oldCanvasWidth = canvasWidth;
    int oldCanvasHeight = canvasHeight;
    lastCalc = null;
    lineCount = 0;
    canvasWidth = 0;
    canvasHeight = 0;
    if (model != null) {
      NodeState root = getViewState(model.getRoot());
      if (root != null) {
        if (showRoot)
          recalcVisible(root.node, topMargin);
        else if (root.nextVisible != null)
          recalcVisible(root.nextVisible.node, topMargin);
      }
    }
    scroller.getVerticalScrollBar().setUnitIncrement(
        lineCount > 0 ? canvasHeight / lineCount : 0);
    if (canvasWidth != oldCanvasWidth || canvasHeight != oldCanvasHeight) {
      invalidate();
      repaintNodes();
      if (shouldValidate && isShowing() && !batchMode) {
        scroller.validate();
        scrollView();
      }
    }
  }
  
  /**
   * recursively updates the positional info for all expanded nodes & children
   * starting at a given node assumes that each node height has already been
   * calculated
   */
  int recalcVisible(GraphLocation location, int y) {
    while (location != null) {
      NodeState nState = getViewState(location);
      if (nState == null)
        break;
      int x = leftMargin + nState.level * hIndent;
      // Establish the display chain
      nState.prevVisible = lastCalc;
      nState.nextVisible = null;
      nState.lineNumber = lineCount++;
      if (lastCalc != null)
        lastCalc.nextVisible = nState;
      lastCalc = nState;
      // height & width calculated in updateNodeRects()
      if (location.hasChildren() != TriState.NO) {
        nState.boxRect.x = x;
        nState.boxRect.y = y + (nState.itemRect.height - nState.boxRect.height)
            / 2;
      }
      nState.boxRect.width = boxSize.width;
      nState.boxRect.height = boxSize.height;
      x += itemOffset;
      nState.itemRect.x = x;
      nState.itemRect.y = y;
      // height & width calculated in updateNodeRects()
      canvasHeight += nState.itemRect.height + vgap;
      if (nState.itemRect.x + nState.itemRect.width > canvasWidth) {
        canvasWidth = nState.itemRect.x + nState.itemRect.width;
      }
      y += nState.itemRect.height + vgap;
      if (nState.expanded && nState.node.hasChildren() != TriState.NO) {
        GraphLocation[] ns = nState.node.getChildren();
        if (ns.length > 0)
          y = recalcVisible(ns[0], y);
      }
      location = nState.getNextSibling();
    }
    return y;
  }
  
  public void setSelection(WritableGraphSelection s) {
    if (selection != null) {
      selection.removeSelectionListener(this);
      selection.removeSelectionListener(selectionMulticaster);
    }
    selection = s;
    if (selection != null) {
      selection.addSelectionListener(this);
      selection.addSelectionListener(selectionMulticaster);
    }
  }
  
  public WritableGraphSelection getSelection() {
    return selection;
  }
  
  public void addSelectionListener(GraphSelectionListener l) {
    selectionMulticaster.add(l);
  }
  
  public void removeSelectionListener(GraphSelectionListener l) {
    selectionMulticaster.remove(l);
  }
  
  /*
   * protected void rangeSelect(NodeState start, NodeState end) { Vector sels =
   * new Vector();
   * 
   * if (start.lineNumber > end.lineNumber) for (NodeState d = start; d != null
   * && d.lineNumber >= end.lineNumber; d = d.prevVisible)
   * sels.addElement(d.node); else for (NodeState d = start; d != null &&
   * d.lineNumber <= end.lineNumber; d = d.nextVisible) sels.addElement(d.node);
   * 
   * GraphLocation[] sa = new GraphLocation[sels.size()]; sels.copyInto(sa);
   * selection.removeAll(); selection.add(sa); }
   */

  /**
   * Expands (or collapses) a node.
   * 
   * @param node
   *          The NodeState to expand or collapse.
   * @param expand
   *          True to expand, false to collapse.
   * @return True if the node state was changed, false if not.
   */
  protected boolean expandNode(NodeState nState, boolean expand) {
    if (nState.node.hasChildren() == TriState.NO)
      return false;
    // Only fire the event if the expanded state would change.
    if (nState.expanded != expand) {
      processTreeEvent(new TreeEvent(this, expand ? TreeEvent.NODE_EXPANDED
          : TreeEvent.NODE_COLLAPSED, nState.node));
      // If to be expanded, retrieve the children
      nState.expanded = expand;
      if (expand) {
        updateViewState(nState.node, nState.level);
        updateNodeRects(nState, getSiteGraphics());
      }
      return true;
    } else
      return false;
  }
  
  public boolean isExpanded(GraphLocation location) {
    NodeState nState = getViewState(location);
    if (nState != null)
      return nState.expanded;
    else
      return false;
  }
  
  public void expand(GraphLocation location) {
    if (!initialized) {
      presetExpandList.addElement(location);
      return;
    }
    NodeState nState = getViewState(location);
    if (nState != null) {
      if (expandNode(nState, true)) {
        recalcVisible(true);
        repaintToEnd(nState);
      }
    }
  }
  
  public void collapse(GraphLocation location) {
    if (!initialized) {
      presetCollapseList.addElement(location);
      return;
    }
    NodeState nState = getViewState(location);
    if (nState != null) {
      if (expandNode(nState, false)) {
        recalcVisible(true);
        repaintToEnd(nState);
      }
      if (subfocus != null && subfocus.isDescendentOf(location))
        setSubfocus(nState, SelectFlags.ADD_ITEM | SelectFlags.RESET_ANCHOR);
    }
    
  }
  
  public void toggleExpanded(GraphLocation location) {
    if (!initialized || location == null)
      return;
    NodeState nState = getViewState(location);
    if (nState != null && nState.node.hasChildren() != TriState.NO) {
      boolean wasExpanded = false;
      if (!nState.expanded)
        wasExpanded = expandNode(nState, true);
      else
        wasExpanded = expandNode(nState, false);
      if (wasExpanded) {
        recalcVisible(true);
        repaintToEnd(nState);
      }
    }
  }
  
  public void expandAll(GraphLocation location) {
    if (location == null)
      return;
    expandChildNodes(location);
    NodeState nState = getViewState(location);
    if (nState != null) {
      recalcVisible(true);
      repaintToEnd(nState);
    }
  }
  
  public void collapseAll(GraphLocation location) {
    if (location == null)
      return;
    collapseChildNodes(location);
    NodeState nState = getViewState(location);
    if (nState != null) {
      recalcVisible(true);
      repaintToEnd(nState);
    }
    if (subfocus != null && subfocus.isDescendentOf(location))
      setSubfocus(nState, SelectFlags.ADD_ITEM | SelectFlags.RESET_ANCHOR);
  }
  
  private void expandChildNodes(GraphLocation location) {
    NodeState nState = getViewState(location);
    if (nState != null && nState.node.hasChildren() != TriState.NO) {
      expandNode(nState, true);
      GraphLocation[] children = location.getChildren();
      for (int i = 0; i < children.length; i++)
        expandChildNodes(children[i]);
    }
  }
  
  private void collapseChildNodes(GraphLocation location) {
    NodeState nState = getViewState(location);
    if (nState != null && nState.node.hasChildren() != TriState.NO) {
      expandNode(nState, false);
      GraphLocation[] children = location.getChildren();
      for (int i = 0; i < children.length; i++)
        collapseChildNodes(children[i]);
    }
  }
  
  public boolean isTransparent() {
    return texture != null ? true : !isOpaque();
  }
  
  public Graphics getSiteGraphics() {
    Graphics g = getGraphics();
    if (g != null) {
      g.setFont(getFont());
    }
    return g;
  }
  
  public Component getSiteComponent() {
    return this;
  }
  
  private int getState(NodeState nState) {
    int state = 0;
    if (nState == null)
      return 0;
    if (selection != null && selection.contains(nState.node))
      state |= ItemPainter.SELECTED;
    if (!isEnabled())
      state |= ItemPainter.DISABLED | ItemPainter.INACTIVE;
    else {
      if (showFocus && hasFocus && subfocus == nState)
        state |= ItemPainter.FOCUSED;
      if ((focusState & ItemPainter.INACTIVE) != 0)
        state |= ItemPainter.INACTIVE;
      if (nState.expanded)
        state |= ItemPainter.OPENED;
      if (showRollover && rollover != null && rollover == nState)
        state |= ItemPainter.ROLLOVER;
    }
    if (!hasFocus)
      state |= ItemPainter.NOT_FOCUS_OWNER;
    return state;
  }
  
  void dump(NodeState nState) {
    Diagnostic.println("");
    Diagnostic.println("canvas  :" + getPreferredSize());
    Diagnostic.println("current :" + nState);
  }
  
  /**
   * See whether a given node state is visible
   */
  boolean isVisible(NodeState s) {
    if (!isVisible())
      return false;
    Rectangle vRect = scroller.getViewport().getViewRect();
    if (s == null || s.itemRect.y < vRect.y
        || s.itemRect.y - vRect.y > vRect.height)
      return false;
    do {
      NodeState ps = getViewState(s.node.getParent());
      if (ps == s) {
        // System.err.println("TreeView NodeState tree is circular!!!");
        return false;
      }
      s = ps;
    } while (s != null && s.expanded);
    return s == null;
  }
  
  /**
   * See whether a given node is completly visible (last returns false if only
   * partial)
   */
  /*
   * protected boolean isFullyVisible(NodeState s) { Dimension vp =
   * scroller.getViewport().getExtentSize(); Point sp =
   * scroller.getViewport().getViewPosition(); return isVisible(s) &&
   * s.itemRect.y + s.itemRect.height - 1 - sp.y < vp.height; }
   * 
   * protected void makeVisible(NodeState s, boolean shouldValidate) { if (s ==
   * null || isFullyVisible(s)) return; if (makeExpanded(s))
   * recalcVisible(false); if (!isFullyVisible(s)) { int newY = s.itemRect.y -
   * scroller.getViewport().getExtentSize().height/2 + s.itemRect.height/2; s =
   * hitTestY(newY); setSubfocus(s, SelectFlags.CLEAR | SelectFlags.ADD_ITEM |
   * SelectFlags.RESET_ANCHOR); } if (shouldValidate && !batchMode) {
   * scroller.validate(); repaintToEnd(s); } else repaintToEnd(s); }
   */
  /**
   * expand a node & all of its parents as needed
   */
  protected boolean makeExpanded(NodeState s) {
    boolean didExpand = false;
    do {
      if (!s.expanded)
        s.expanded = didExpand = true;
      s = getViewState(s.node.getParent());
    } while (s != null);
    return didExpand;
  }
  
  //----------------------------------------------------------------------------
  // --------------------
  // GraphModelListener interface
  //----------------------------------------------------------------------------
  // --------------------
  
  public void modelContentChanged(GraphModelEvent e) {
    GraphLocation gl = e.getLocation();
    NodeState nState = getViewState(gl);
    if (!initialized || gl == null || nState == null)
      return;
    updateNodeRects(nState, getSiteGraphics());
    switch (e.getChange()) {
    case GraphModelEvent.ITEM_TOUCHED:
      if (editor != null && editorNode != null
          && editorNode.equals(e.getLocation()))
        safeEndEdit(false);
      repaintToEnd(nState);
      break;
    default:
      if (editor != null && editorNode != null
          && editorNode.equals(e.getLocation()))
        safeEndEdit(false);
      repaintNode(nState);
      break;
    }
    recalcVisible(true);
  }
  
  public void modelStructureChanged(GraphModelEvent e) {
    if (!initialized)
      return;
    GraphLocation loc = e.getLocation();
    switch (e.getChange()) {
    case GraphModelEvent.NODE_ADDED:
      updateViewState();
      NodeState nState = getViewState(loc.getParent());
      if (nState == null)
        nState = firstNode;
      // JPN NOTE: The expandByDefault property is ignored if the node returns
      // TriState.UNKNOWN to GraphLocation.hasChildren(). Those nodes
      // will not be expanded by default.
      if (expandByDefault && !nState.expanded
          && nState.node.hasChildren() == TriState.YES) {
        expandNode(nState, true);
      } else {
        updateNodeRects(nState, getSiteGraphics());
        repaintToEnd(nState);
      }
      break;
    
    case GraphModelEvent.NODE_REMOVED:
      NodeState ns = getViewState(loc);
      NodeState jump;
      if (ns != null && ns.nextVisible != null
          && ns.nextVisible.level == ns.level)
        jump = ns.nextVisible;
      else if (ns != null && ns.prevVisible != null)
        jump = ns.prevVisible;
      else if (ns != firstNode)
        jump = firstNode;
      else {
        jump = null;
        subfocus = null;
      }
      if (selection != null && ns != null && selection.contains(ns.node))
        selection.remove(ns.node);
      if (ns == subfocus || subfocus != null && subfocus.isDescendentOf(loc))
        setSubfocus(jump, SelectFlags.ADD_ITEM | SelectFlags.RESET_ANCHOR);
      updateViewState();
      updateNodeRects(jump, getSiteGraphics());
      break;
    
    case GraphModelEvent.NODE_REPLACED:
      updateViewState();
      NodeState rn = getViewState(loc);
      updateNodeRects(rn, getSiteGraphics());
      break;
    
    case GraphModelEvent.STRUCTURE_CHANGED:
      updateViewState();
      updateNodeRects(firstNode, getSiteGraphics());
      break;
    
    default:
      updateViewState();
      updateNodeRects(firstNode, getSiteGraphics());
    }
    recalcVisible(true);
  }
  
  // Editor Functionality
  
  public void startEdit(GraphLocation node) {
    startEdit(getViewState(node));
  }
  
  protected void startEdit(NodeState newEditorNode) {
    if (model == null || viewManager == null || !editInPlace || batchMode)
      return;
    rollover = null;
    editorNode = newEditorNode;
    if (newEditorNode == subfocus) {
      if (selection.getCount() != 1 || !selection.contains(newEditorNode.node)) {
        selection.removeAll();
        selection.add(newEditorNode.node);
      }
    } else {
      setSubfocus(editorNode, SelectFlags.CLEAR | SelectFlags.ADD_ITEM
          | SelectFlags.RESET_ANCHOR);
    }
    Object data = model.get(editorNode.node);
    int state = getState(editorNode);
    editor = getEditor(editorNode, data, state);
    if (editor == null) {
      editorNode = null;
      return;
    } else {
      Component editorComponent = editor.getComponent();
      if (editorComponent != null) {
        editorComponent.setVisible(false);
        add(editorComponent);
      }
      Rectangle r = getEditorRect();
      // r.width = scroller.getViewport().getViewSize().width - r.x;
      editor.addKeyListener(this);
      editor.addKeyListener(keyMulticaster);
      editor.startEdit(data, r, this);
      resyncEditor();
      if (editor != null && editor.getComponent() != null)
        editor.getComponent().addFocusListener(this);
      editClickPoint = null;
    }
  }
  
  protected Rectangle getEditorRect() {
    Rectangle rect = null;
    if (editorNode != null && editor != null) {
      rect = editorNode.itemRect;
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
    if (editorNode != null && editor != null) {
      Rectangle er = getEditorRect();
      editor.changeBounds(er != null ? er : new Rectangle());
    }
  }
  
  private boolean isToggleItem(NodeState item) {
    if (model == null || viewManager == null || !editInPlace || batchMode)
      return false;
    Object data = model.get(item.node);
    int state = getState(item);
    ItemEditor ie = getEditor(item, data, state);
    if (ie instanceof ToggleItemEditor) {
      Rectangle rect = new Rectangle(item.itemRect);
      return ((ToggleItemEditor) ie).isToggle(data, rect, this)
          && canSet(item.node);
    } else
      return false;
  }
  
  public void endEdit() throws Exception {
    endEdit(postOnEndEdit);
  }
  
  public void endEdit(boolean post) throws Exception {
    lockSubfocus = false;
    ItemEditor editor = this.editor; // keep in local in case of reentrancy
    this.editor = null;
    if (editor != null) {
      boolean okToEnd = true;
      try {
        if (!post || (okToEnd = editor.canPost())) {
          if (post && okToEnd) {
            writeModel.set(editorNode.node, editor.getValue());
          }
          repaintNode(editorNode);
          if (okToEnd) {
            Component editorComponent = editor.getComponent();
            editor.endEdit(post);
            editor.removeKeyListener(this);
            editor.removeKeyListener(keyMulticaster);
            if (editorComponent != null) {
              remove(editorComponent);
              editorComponent.removeFocusListener(this);
            }
            updateViewState(editorNode.node, editorNode.level);
            editClickPoint = null;
            editorNode = null;
            editor = null;
            requestFocus();
          }
        }
      } catch (Exception x) {
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
    } catch (Exception x) {
      // if (editor instanceof ExceptionHandler)
      // ((ExceptionHandler)editor).handleException(x);
      // else if (model instanceof ExceptionHandler)
      // ((ExceptionHandler)model).handleException(x);
    }
  }
  
  public void doLayout() {
    if (editorNode != null && editor != null) {
      Rectangle r = editorNode.itemRect;
      if (r != null) {
        editor.changeBounds(new Rectangle(r.x, r.y, r.width, r.height));
      } else
        editor.changeBounds(new Rectangle(0, 0, 0, 0));
    }
  }
  
  /**
   * Used by editors which wish to set the insertion point at the clicked
   * position. This method returns the mouse click position, or null if no mouse
   * click initiated the editing
   */
  public Point getEditClickPoint() {
    return editClickPoint;
  }
  
  public void addKeyListener(KeyListener l) {
    keyMulticaster.add(l);
  }
  
  public void removeKeyListener(KeyListener l) {
    keyMulticaster.remove(l);
  }
  
  public void checkParentWindow() {
    findParentWindow();
  }
  
  private void fireActionEvent() {
    Object item = model != null ? model.get(subfocus.node) : null;
    String action = item != null ? item.toString() : "";
    processActionEvent(new ActionEvent(scroller, ActionEvent.ACTION_PERFORMED,
        action));
  }
  
  protected void fireCustomizeItemEvent(Object address, Object data, int state,
      CustomPaintSite cps) {
    if (customizeListeners != null) {
      cps.reset();
      for (int i = 0; i < customizeListeners.size(); i++)
        ((CustomItemListener) customizeListeners.elementAt(i)).customizeItem(
            address, data, state, cps);
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
    Hashtable<String, Object> hash = new Hashtable<String, Object>(5);
    if (model instanceof Serializable)
      hash.put("m", model);
    if (viewManager instanceof Serializable)
      hash.put("v", viewManager);
    if (selection instanceof Serializable)
      hash.put("s", selection);
    if (expandedArrow != null)
      hash.put("e", SerializableImage.create(expandedArrow));
    if (contractedArrow != null)
      hash.put("c", SerializableImage.create(contractedArrow));
    s.writeObject(hash);
  }
  
  private void readObject(ObjectInputStream s) throws IOException,
      ClassNotFoundException {
    s.defaultReadObject();
    Hashtable<Object, Object> hash = (Hashtable<Object, Object>) s.readObject();
    Object data = hash.get("m");
    if (data instanceof GraphModel)
      model = (GraphModel) data;
    if (model instanceof WritableGraphModel)
      writeModel = (WritableGraphModel) model;
    data = hash.get("v");
    if (data instanceof GraphViewManager)
      viewManager = (GraphViewManager) data;
    data = hash.get("s");
    if (data instanceof WritableGraphSelection)
      selection = (WritableGraphSelection) data;
    data = hash.get("e");
    if (data instanceof SerializableImage)
      expandedArrow = ((SerializableImage) data).getImage();
    data = hash.get("c");
    if (data instanceof SerializableImage)
      contractedArrow = ((SerializableImage) data).getImage();
  }
  
  private transient GraphModel model;
  private transient WritableGraphModel writeModel;
  private transient GraphViewManager viewManager;
  private transient WritableGraphSelection selection = new NullGraphSelection();
  private transient Image expandedArrow;
  private transient Image contractedArrow;
  
  private boolean readOnly;
  private NodeState selectAnchor;
  private NodeState subfocus;
  private NodeState rollover;
  private NodeState mouseDown;
  private boolean subfocusChecked = false; // flag for programmatic subfocus
                                           // changes
  private transient ItemEditor editor; // ignore for serialization
  private NodeState editorNode;
  private Point editClickPoint;
  private boolean snapOrigin = true;
  private boolean postOnEndEdit = true;
  private boolean expandByDefault = false;
  private boolean hasFocus = false;
  private boolean showFocus = true;
  private boolean showRoot = true;
  private transient Hashtable<GraphLocation, NodeState> cache = new Hashtable<GraphLocation, NodeState>(); // ignore
                                                                                                           // for
                                                                                                           // serialization
  private NodeState firstNode; // view state for root node
  private NodeState lastCalc; // temporary used when calculating visual
                              // positions
  private int style = TreeView.STYLE_PLUSES;
  private JScrollPane scroller;
  private int canvasWidth;
  private int canvasHeight;
  private int lineCount;
  private int lastClickPosition;
  private boolean initialized = false;
  private Vector<GraphLocation> presetExpandList = new Vector<GraphLocation>();
  private Vector<GraphLocation> presetCollapseList = new Vector<GraphLocation>();
  private boolean editInPlace = true;
  private boolean autoEdit = true;
  private boolean growEditor = true;
  private boolean hSnap = false;
  private boolean lockSubfocus = false;
  private boolean dragSubfocus = true;
  private boolean debugPaint = false;
  private boolean batchMode = false;
  private boolean showRollover = false;
  private transient DataToolTip toolTip = new DataToolTip(this);
  
  private Insets margins = new Insets(2, 2, 2, 2);
  private int leftMargin = 2;
  private int topMargin = 0;
  private int alignment = Alignment.LEFT | Alignment.MIDDLE;
  private Dimension boxSize = new Dimension(9, 9);
  private int vgap = 0; // inter-item vertical gap
  private int hIndent = 19; // indentation level (pixels)
  private int itemOffset = 14; // offset of itemRect from left of box
  
  private transient CustomItemPainter customPainter = new CustomItemPainter();
  private transient CustomItemEditor customEditor = new CustomItemEditor();
  private transient Vector<CustomItemListener> customizeListeners;
  private boolean customizeEditors = false;
  
  transient com.borland.jb.util.EventMulticaster treeListeners = new com.borland.jb.util.EventMulticaster();
  transient com.borland.jb.util.EventMulticaster subfocusListeners = new com.borland.jb.util.EventMulticaster();
  private transient KeyMulticaster keyMulticaster = new KeyMulticaster();
  private transient GraphModelMulticaster modelMulticaster = new GraphModelMulticaster();
  private transient GraphSelectionMulticaster selectionMulticaster = new GraphSelectionMulticaster();
}

@SuppressWarnings("serial")
class NodeState implements Serializable {
  NodeState(TreeCore core, GraphLocation node, int level, boolean expanded) {
    this.core = core;
    this.node = node;
    this.level = level;
    this.expanded = (node.hasChildren() != TriState.NO) ? expanded : false;
    boxRect.width = core.getBoxSize().width;
    boxRect.height = core.getBoxSize().height;
    inUse = true;
  }
  
  GraphLocation getNextSibling() {
    if (node instanceof LinkedTreeNode)
      return ((LinkedTreeNode) node).getNextSibling();
    GraphLocation p = node.getParent();
    if (p != null) {
      GraphLocation[] children = p.getChildren();
      for (int i = 0; i < children.length; i++) {
        if (children[i] == node && i < (children.length - 1)) {
          return children[i + 1];
        }
      }
    }
    return null;
  }
  
  void update(int level) {
    this.level = level;
    inUse = true;
  }
  
  boolean isDescendentOf(GraphLocation root) {
    for (GraphLocation gl = node; gl != null; gl = gl.getParent())
      if (gl == root)
        return true;
    return false;
  }
  
  // return the visible node at an (x,y), setting
  // 0 = hit nothing (miss!)
  // 1 = hit item
  // 2 = hit box
  int hitTest(int x, int y) {
    if (itemRect.contains(x, y))
      return 1;
    if (boxRect.contains(x, y)) {
      if (node.hasChildren() != TriState.NO)
        return 2;
      else
        return 0;
    }
    return 0;
  }
  
  public String toString() {
    if (node == null)
      return "null";
    String prev = prevVisible == null ? "null" : prevVisible.node.toString();
    String next = nextVisible == null ? "null" : nextVisible.node.toString();
    return "#" + lineNumber + " " + itemRect + " " + node.toString()
        + "  prev:" + prev + ", next:" + next + (expanded ? " exp" : "")
        + (selected ? " sel" : "");
  }
  
  // Serialization support
  
  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    s.writeObject(node instanceof Serializable ? node : null);
  }
  
  private void readObject(ObjectInputStream s) throws IOException,
      ClassNotFoundException {
    s.defaultReadObject();
    Object data = s.readObject();
    if (data instanceof GraphLocation)
      node = (GraphLocation) data;
  }
  
  transient GraphLocation node;
  
  TreeCore core;
  boolean expanded;
  boolean selected; // this is ONLY used to optimize selectionChanged Events
  int level;
  int lineNumber;
  Rectangle boxRect = new Rectangle(); // rectange of +/- or v/> box for parent
                                       // nodes
  Rectangle itemRect = new Rectangle(); // rectangle of item area
  NodeState prevVisible;
  NodeState nextVisible;
  boolean inUse = true;
}
