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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

import com.borland.dx.text.Alignment;
import com.borland.jb.util.Diagnostic;
import com.borland.jb.util.EventMulticaster;
import com.borland.jb.util.Trace;
import com.borland.jbcl.model.ItemPaintSite;
import com.borland.jbcl.model.ItemPainter;
import com.borland.jbcl.model.SingleVectorSelection;
import com.borland.jbcl.model.SubfocusEvent;
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

public class TabsetView
     extends BeanPanel
  implements ItemPaintSite, VectorModelListener, VectorSelectionListener, VectorView, Serializable
{
  private static final long serialVersionUID = 200L;

  // fixed properties
  static final int SEL_HEIGHT_INCREASE  = 2;  // selected tabs are this much taller
  static final int SLOPE                = 1;  // Number of pixels in the corners
  static final int EDGE                 = 2;  // thickness of beveled edges
  static final int LINE                 = 2;  // accounted thickness of main line
  static final int SCROLLER_WIDTH       = 28; // width of *both* scroll arrows
  static final int SCROLLER_HEIGHT      = 14; // height of scroll arrows

  public TabsetView() {
    super(null);
    setBackground(SystemColor.control);
    selection.addSelectionListener(this);
    selection.addSelectionListener(selectionMulticaster);

    TextItemPainter textPainter = new TextItemPainter(Alignment.LEFT | Alignment.MIDDLE, new Insets(1, 1, 1, 1));
    defaultPainter  = new FocusableItemPainter(textPainter);
  }

  //------------------------------------------------------------------------------------------------
  // properties
  //------------------------------------------------------------------------------------------------

  public void setLabels(String[] labels) {
    if (isReadOnly())
      return;
    if (labels != null) {
      writeModel.enableModelEvents(false);
      writeModel.removeAll();
      for (int i = 0; i < labels.length; i++) {
        writeModel.addItem(labels[i]);
      }
      writeModel.enableModelEvents(true);
    }
    else
      writeModel.removeAll();
  }

  public String[] getLabels() {
    String[] labels = new String[model.getCount()];
    for (int i = 0; i < model.getCount(); i++)
      labels[i] = model.get(i).toString();
    return labels;
  }

  public void setLabel(int index, Object item) {
    if (isReadOnly())
      return;
    writeModel.set(index, item);
  }

  public String getLabel(int index) {
    return model.get(index).toString();
  }

  /**
   *
   */
  public void addTab(Object item) {
    if (isReadOnly())
      return;
    writeModel.addItem(item);
  }

  public void removeTab(Object item) {
    if (isReadOnly())
      return;
    int index = writeModel.find(item);
    if (index != -1) {
      writeModel.remove(index);
    }
  }

  public void addTab(int aheadOf, Object item) {
    if (isReadOnly())
      return;
    if (aheadOf == -1)
      writeModel.addItem(item);
    else
      writeModel.addItem(aheadOf, item);
  }

  public void renameTab(String oldName, String newName) {
    if (isReadOnly())
      return;
    int index = writeModel.find(oldName);
    if (index != -1) {
      writeModel.set(index, newName);
    }
  }

  /**
   * model to use for tab labels - mandatory
   */
  public VectorModel getModel() { return model; }
  public WritableVectorModel getWriteModel() {
    return readOnly ? null : writeModel;
  }
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
      writeModel = (WritableVectorModel) model;
    else
      writeModel = null;
    updateTabInfo();
  }

  /**
   * manager of ItemPainters - mandatory
   */
  public VectorViewManager getViewManager() { return viewManager; }
  public void setViewManager(VectorViewManager viewManager) {
    this.viewManager = viewManager;
    invalidate();
    repaint(100);
  }

  /**
   * Subfocus for tabs always corresponds directly with the single selection
   */
  public int getSubfocus() { return getSelectedIndex(); }
  public void setSubfocus(int subfocus) { selectTab(subfocus); }

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

    int newIndex = getSelectedIndex();

    repaintTab(newIndex);
    repaintTab(oldIndex);

    oldIndex = newIndex;
  }

  /**
   * The showRollover property enables/disables the repainting of the rollover item.  The
   * rollover item is the item that currently has the mouse floating over it.
   * If an ItemPainter plugged into the tabset ignores the ROLLOVER bit, this property will
   * have no effect.  By default, showRollover is false.
   */
  public void setShowRollover(boolean showRollover) { this.showRollover = showRollover; }
  public boolean isShowRollover() { return showRollover; }

  /**
   * Single selection selectedIndex property
   */
  public int getSelectedIndex() {
    int[] sels = selection.getAll();
    return sels.length > 0 ? sels[0] : -1;
  }
  public void setSelectedIndex(int index) {
    selectTab(index);
  }

  public String getSelectedTab() {
    int sel = getSelectedIndex();
    if (sel >= 0)
      return model.get(sel).toString();
    return null;
  }
  public void setSelectedTab(Object item) {
    selectTab(model.find(item));
  }

  public int getAlignment() { return alignment; }
  public void setAlignment(int align) {
    alignment = align;
    repaint(100);
  }

  /**
   * when true, tabset draws a raised border around client (non-tab) area
   */
  public boolean isClientBordered() { return clientBordered; }
  public void setClientBordered(boolean bordered) {
    clientBordered = bordered;
    repaint(100);
  }

  public boolean isFocusAware() { return super.isFocusAware(); }
  public void setFocusAware(boolean focusAware) {
    super.setFocusAware(focusAware);
    repaintTab(getSelectedIndex());
  }

  public Insets getItemMargins() { return itemMargins; }
  public void setItemMargins(Insets margins) {
    itemMargins = margins;
    invalidate();
    repaint(100);
  }

  public Component getSiteComponent() { return this; }

  public boolean isTransparent() { return texture != null ? true : !isOpaque(); }

  public boolean isReadOnly() {
    return readOnly ? true : writeModel == null;
  }
  public void setReadOnly(boolean ro) {
    readOnly = ro;
  }

  public boolean isTabsOnTop() { return tabsOnTop; }
  public void setTabsOnTop(boolean tabsOnTop) {
    this.tabsOnTop = tabsOnTop;
    invalidate();
    repaint(100);
  }

  /**
   * when true, tabset draws a raised border around client (non-tab) area
   */
  public void setThickBorder(boolean thickBorder) {
    this.thickBorder = thickBorder;
    repaint(100);
  }

  public boolean isThickBorder() {
    return thickBorder;
  }


  public int hitTest(int x, int y) {
    // Make sure that the mouse was not clicked in the scroll arrow region.
    if (needScroller && x > leftArrowRect.x)
      return -1;

    // Make sure that the mouse was clicked within the tab area.
    if (tabsOnTop) {
      if (y < border || y >= border + tabBarHeight)
        return -1;
    }
    else {
      if (y < getSize().height - border - tabBarHeight || y >= getSize().height - border)
        return -1;
    }

    // Find out which tab was actually clicked on.
    return hitTestX(x);
  }

  int hitTestX(int x) {
    try {
      for (int i = firstVisible; i <= lastVisible; i++) { //JPBS-INDEX OUT OF RANGE
        TabInfo t = (TabInfo) tabInfo.elementAt(i);
        if (t.hitTestX(x + xOffset)) {
          return i;
        }
      }
      return -1;
    } catch(Exception e) {
      return -1;
    }
  }

  //------------------------------------------------------------------------------------------------
  // Event notification
  //------------------------------------------------------------------------------------------------

  // Event Listeners

  public void addModelListener(VectorModelListener l) { modelMulticaster.add(l); }
  public void removeModelListener(VectorModelListener l) { modelMulticaster.remove(l); }

  public void addSelectionListener(VectorSelectionListener l) { selectionMulticaster.add(l); }
  public void removeSelectionListener(VectorSelectionListener l) { selectionMulticaster.remove(l); }

  public void addSubfocusListener(VectorSubfocusListener l) { subfocusListeners.add(l);}
  public void removeSubfocusListener(VectorSubfocusListener l) { subfocusListeners.remove(l);}

  // VectorModelListener events...

  public void modelContentChanged(VectorModelEvent e) {
    Diagnostic.trace(Trace.ModelEvents, "TabsetView.modelContentChanged(" + e + ")"); 
    switch (e.getChange()) {
      case (VectorModelEvent.CONTENT_CHANGED):
        updateTabInfo();
        break;
      case (VectorModelEvent.ITEM_CHANGED):
      case (VectorModelEvent.ITEM_TOUCHED):
        TabInfo t = (TabInfo)tabInfo.elementAt(e.getLocation());
        t.item = model.get(e.getLocation());
        updateTabRects();
        adjustScrollbar();
        repaint(100);
        break;
    }
  }

  public void modelStructureChanged(VectorModelEvent e) {
    Diagnostic.trace(Trace.ModelEvents, "TabsetView.modelStructureChanged(" + e + ")"); 
    updateTabInfo();
  }

  // VectorSelectionListener events...

  public void selectionItemChanged(VectorSelectionEvent e) {}
  public void selectionRangeChanged(VectorSelectionEvent e) {}

  public void selectionChanged(VectorSelectionEvent e) {
    int newIndex = getSelectedIndex();

    repaintTab(newIndex);
    repaintTab(oldIndex);

    oldIndex = newIndex;
  }

  protected void processFocusEvent(FocusEvent e) {
    super.processFocusEvent(e);
    repaintTab(getSelectedIndex());
  }

  // Keyboard events

  /**
   * handle selection keys
   */
  protected void processKeyPressed(KeyEvent e) {
    switch (e.getKeyCode()) {
      case KeyEvent.VK_HOME:
        selectTab(0);
        break;
      case KeyEvent.VK_LEFT: {
        int sel = getSelectedIndex();
        if (sel > 0)
          selectTab(sel - 1);
        break;
      }
      case KeyEvent.VK_RIGHT: {
        int sel = getSelectedIndex();
        if (sel < tabInfo.size() - 1)
          selectTab(sel + 1);
        break;
      }
      case KeyEvent.VK_END:
        if (tabInfo.size() > 0)
          selectTab(tabInfo.size() - 1);
        break;
    }
  }

  // Subfocus Events

  protected boolean preProcessSubfocusEvent(VectorSubfocusEvent e) {
    if (subfocusListeners.hasListeners())
      return subfocusListeners.vetoableDispatch(e);
    return true;
  }

  protected void processSubfocusEvent(VectorSubfocusEvent e) {
    if (subfocusListeners.hasListeners()) {
      subfocusListeners.dispatch(e);
    }
  }

  // Mouse Events

  protected void processMousePressed(MouseEvent e) {
    requestFocus();
    rollover = -1;
    if (e.isMetaDown())
      return;
    if (scrollCheck(e.getX(), e.getY()))
      return;
    int hit = hitTest(e.getX(), e.getY());
    if (hit >= 0) {
      selectTab(hit);
    }
  }

  protected void processMouseMoved(MouseEvent e) {
    if (showRollover) {
      int hit = hitTest(e.getX(), e.getY());
      if (hit >= 0 && hit != rollover) {
        int oldRollover = rollover;
        rollover = hit;
        repaintTab(oldRollover);
        repaintTab(rollover);
      }
    }
  }

  protected void processMouseExited(MouseEvent e) {
    if (showRollover) {
      int oldRollover = rollover;
      rollover = -1;
      repaintTab(oldRollover);
    }
  }

  //------------------------------------------------------------------------------------------------
  // internal
  //------------------------------------------------------------------------------------------------

  protected void setBorderHeight(int height) {
    border = height;
  }

  protected void updateTabInfo() {
    tabInfo.removeAllElements();

    for (int i = 0; i < model.getCount(); i++) {
      tabInfo.addElement(new TabInfo(model.get(i)));
    }

    int[] sels = selection.getAll();
    if (sels.length > 0 && sels[0] >= tabInfo.size()) {
      int[] nosels = { };
      selection.removeAll();
      selection.add(nosels);
    }
    updateTabRects();
    adjustScrollbar();
    invalidate();
    repaint(100);
  }

  protected void selectTab(int index) {
    if (index != getSelectedIndex() && index < tabInfo.size()) {

      if (!preProcessSubfocusEvent(new VectorSubfocusEvent(this, SubfocusEvent.SUBFOCUS_CHANGING, index)))
        return;

      int previous = getSelectedIndex();
      selection.enableSelectionEvents(false);
      selection.removeAll();
      if (index >= 0)
        selection.add(index);
      selection.enableSelectionEvents(true);

      processSubfocusEvent(new VectorSubfocusEvent(this, SubfocusEvent.SUBFOCUS_CHANGED, index));
    }
  }

  protected void scrollLeft() {
    if (firstVisible > 0) {
      firstVisible--;
      adjustScrollbar();
      repaint(100);
    }
  }

  protected void scrollRight() {
    if ((lastClipped || lastVisible < tabInfo.size() - 1) && firstVisible < tabInfo.size() - 1) {
      firstVisible++;
      adjustScrollbar();
      repaint(100);
    }
  }

  protected boolean scrollCheck(int x, int y) {
    if (needScroller) {
      if (leftArrowRect.contains(x, y)) {
        scrollLeft();
        return true;
      }
      else if (rightArrowRect.contains(x, y)) {
        scrollRight();
        return true;
      }
    }
    return false;
  }

  // This function initializes the positioning of the items int the
  // Tabinfo array.
  void updateTabRects() {
    Graphics g = getGraphics();

    tabBarWidth = 0;
    if (g != null) {
      int xCurrent = EDGE;
      int maxHeight = 0;
      g.setFont(getFont());

      for (int i = 0; i < tabInfo.size(); i++) {
        TabInfo t = (TabInfo) tabInfo.elementAt(i);
        int state = 0;
        if (i == getSelectedIndex()) {
          state |= ItemPainter.SELECTED;
          state |= focusState;
        }
        Dimension dimension;
        if (t.item != null) {
          ItemPainter painter = getPainter(i, state);
          dimension = painter.getPreferredSize(t.item, g, state, this);
        }
        else {
          dimension = new Dimension(10, 10);
        }

        t.x = xCurrent;
        t.itemWidth = dimension.width;
        t.width = EDGE + t.itemWidth + EDGE;

        t.itemHeight = dimension.height;
        int height = SEL_HEIGHT_INCREASE + EDGE + t.itemHeight + LINE - 3;

        xCurrent += t.width;
        if (height > maxHeight)
          maxHeight = height;
      }
      tabBarWidth = xCurrent + EDGE;
      tabBarHeight = maxHeight;
    }
  }

  // This function determines whether or not the control needs scroll
  // arrows and, if so, which tabs are visible.
  protected void adjustScrollbar() {
    // Figure out where the tab bar is.
    tabBarTop = tabsOnTop ? border : getSize().height - border - tabBarHeight;

    xOffset = 0;
    needScroller = false;
    lastClipped = false;

    // If the entire tab bar fits, we don't need to go on.
    if (tabBarWidth <= getSize().width) {
      firstVisible = 0;
      lastVisible = tabInfo.size() - 1;
      //System.out.println("LAST VISIBLE (1):"+lastVisible); //JPBS
      return;
    }

    // We need scrollers, so figure out where they need to be.
    int xS = getSize().width - SCROLLER_WIDTH - EDGE - itemMargins.right;
    int yS = (tabBarHeight - SCROLLER_HEIGHT) / 2;

    if (tabsOnTop)
      yS += border;
    else
      yS += getSize().height - border - tabBarHeight;

    if (tabInfo.size() > 0) {
      if (firstVisible >= tabInfo.size()) {
        firstVisible--;
      }
      xOffset = ((TabInfo)tabInfo.elementAt(firstVisible)).x - EDGE;
      lastVisible = firstVisible;
      for (int i = firstVisible; i < tabInfo.size(); i++) {
        TabInfo t = (TabInfo) tabInfo.elementAt(i);
        lastVisible = i;
        if (t.x - xOffset + t.width <= xS - EDGE - itemMargins.right) {
          lastVisible = i;
        }
        else {
          lastClipped = true;
          needScroller = true;
          break;
        }
      }
    }
    if (firstVisible != 0)
      needScroller = true;

    if (needScroller) {
      leftArrowRect  = new Rectangle(xS, yS, SCROLLER_WIDTH / 2, SCROLLER_HEIGHT);
      rightArrowRect = new Rectangle(xS + SCROLLER_WIDTH / 2 + 1, yS, SCROLLER_WIDTH / 2, SCROLLER_HEIGHT);
    }
    //System.out.println("LAST VISIBLE (2):"+lastVisible); //JPBS
  }

  ItemPainter getPainter(int index, int state) {
    return viewManager != null ? viewManager.getPainter(index, ((TabInfo) tabInfo.elementAt(index)).item, state)
                               : defaultPainter;
  }

  //------------------------------------------------------------------------------------------------
  // AWT overrides
  //------------------------------------------------------------------------------------------------

  /** @DEPRECATED */
  public Insets insets() {
    updateTabRects();
    if (tabsOnTop)
      return new Insets(tabBarHeight + border, EDGE, clientBordered ? EDGE : 0, EDGE);
    else
      return new Insets(clientBordered ? EDGE : 0, EDGE, tabBarHeight + border, EDGE);
  }

  public Dimension getPreferredSize() {
    Insets in = insets();
    return new Dimension(tabBarWidth, in.top + in.bottom);
  }

  public void setFont(Font font) {
    super.setFont(font);
    doLayout();
    repaint(100);
  }

  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    repaint(100);
  }

  public void doLayout() {
    super.doLayout();
    updateTabRects();
    adjustScrollbar();
  }

/*
  public boolean contains(int x, int y) {
    if (!super.contains(x, y))
      return false;

    Dimension dim = getSize();

    if (tabsOnTop) {
      if (y > tabBarHeight) {
        return true;
      }
      int leftStart = 0;

      if (tabInfo.size() > 0) {
        try {
          TabInfo lastTab = (TabInfo) tabInfo.elementAt(lastVisible);
          leftStart = lastTab.x + lastTab.width - 1;
        }
        catch (Exception ex) {
        }
      }
      if (x > EDGE && x < leftStart)
        return true;
      return false;
    }
    else {
      if (y < dim.height - tabBarHeight) {
        return true;
      }
      int leftStart = 0;

      if (tabInfo.size() > 0) {
        TabInfo lastTab = (TabInfo) tabInfo.elementAt(lastVisible);
        leftStart = lastTab.x + lastTab.width - 1;
      }
      if (x > EDGE && x < leftStart)
        return true;
      return false;
    }
  }
*/

  public void paintComponent(Graphics g) {
    Rectangle clip = g.getClipBounds();
    if (clip == null)
      return;
    if (clip.width <= 0 || clip.height <= 0 || getSize().width <= 0 || getSize().height <= 0)
      return;

    if (isOpaque()) {
      g.setColor(getBackground());
      g.fillRect(0, 0, getSize().width, getSize().height);
      g.fillRect(clip.x, clip.y, clip.width, clip.height);
    }

    // Paint the texture, if we have one.  Erase the client rect if we're opaque.
    if (texture != null) {
      if (tabsOnTop)
        ImageTexture.texture(texture, g, EDGE, tabBarHeight, getSize().width - EDGE, getSize().height - EDGE);
      else
        ImageTexture.texture(texture, g, EDGE, EDGE, getSize().width - EDGE, getSize().height - tabBarHeight - EDGE);
    }
    else if (isOpaque()) {
      g.setColor(getBackground());
      g.fillRect(0, 0, getSize().width, getSize().height);
      g.fillRect(clip.x, clip.y, clip.width, clip.height);
    }

//    super.paintComponent(g);

    // Paint the borders
    paintBorderLines(g);

    // Make sure we have some tabs before bothering to paint them.
    if (tabInfo.size() > 0) {
      // Make sure that the clipRect intersects the tabBar area.
      if (clip.y < tabBarTop + tabBarHeight && clip.y + clip.height > tabBarTop) {

        int first = hitTestX(clip.x);
        int last = hitTestX(clip.x + clip.width - 1);

        // If the clipRect starts to the left of the first visible tab, start
        // with the first visible tab.
        if (first == -1) {
          TabInfo firstTab = (TabInfo) tabInfo.elementAt(firstVisible);
          if (clip.x + xOffset < firstTab.x)
            first = firstVisible;
        }

        // If the clipRect ends to the right of the last visible tab, end
        // with the last visible tab.
        if (last == -1) {
          try {
            TabInfo lastTab = (TabInfo) tabInfo.elementAt(lastVisible);
            if (clip.x + clip.width + xOffset > lastTab.x + lastTab.width)
              last = lastVisible;
          }
          catch (Exception e) {} //JPBS
        }

//System.err.println("first is " + first + ", last is " + last);

        // If first is -1, then the clipRect starts after the last visible tab.
        // If last is -1, then the clipRect ends before the first visible tab.
        // In either case, don't bother painting any tabs.
        if (first != -1 && last != -1) {
/*
          TabInfo firstTab = (TabInfo) tabInfo.elementAt(first);
          TabInfo lastTab = (TabInfo) tabInfo.elementAt(last);

          System.err.println("firstTab is " + new Rectangle(firstTab.x, tabBarTop, firstTab.width, tabBarHeight));
          System.err.println("lastTab is " + new Rectangle(lastTab.x, tabBarTop, lastTab.width, tabBarHeight));
*/
          // Paint each tab that needs to be painted.
//          for (int i = firstVisible; i <= lastVisible; i++) {
          for (int i = first; i <= last; i++) {
            paintTab(g, i);
          }
        }
      }

      if (needScroller)
        paintScrollArrows(g);
    }
  }

  // This function paints the border around the client area (if needed),
  // and paints the main separator line.
  void paintBorderLines(Graphics g) {
    int left    = 0;
    int right   = getSize().width - 1;
    int top     = 0;
    int bottom  = getSize().height - 1;

    int leftStart = left;

    if (tabInfo.size() > 0) {
      try {
        TabInfo lastTab = (TabInfo) tabInfo.elementAt(lastVisible);
        leftStart = lastTab.x + lastTab.width - 1;
      }
      catch (Exception ex) {}
    }

    if (tabsOnTop) {
      top = border + tabBarHeight - LINE;

      if (clientBordered) {
        g.setColor(SystemColor.controlLtHighlight);
        g.drawLine(left, top, left, bottom - 1);

        g.setColor(SystemColor.controlDkShadow);
        g.drawLine(left, bottom, right, bottom);
        g.drawLine(right, top, right, bottom);

        if (thickBorder) {
          g.setColor(SystemColor.controlHighlight);
          g.drawLine(left + 1, top + 1, left + 1, bottom - 2);

          g.setColor(SystemColor.controlShadow);
          g.drawLine(left + 1, bottom - 1, right - 2, bottom - 1);
          g.drawLine(right - 1, top + 1, right - 1, bottom - 1);
        }
      }

      // Draw main separator line
      g.setColor(SystemColor.controlLtHighlight);
      g.drawLine(left, top, left + EDGE, top);
      g.drawLine(leftStart, top, right - 1, top);

      g.setColor(SystemColor.controlHighlight);
      g.drawLine(left + 1, top + 1, left + EDGE - 1, top + 1);
      g.drawLine(leftStart + 1, top + 1, right - 2, top + 1);
    }
    else {
      bottom = getSize().height - border - tabBarHeight + 1;

      if (clientBordered) {
        g.setColor(SystemColor.controlLtHighlight);
        g.drawLine(left, top, right - 1, top);
        g.drawLine(left, top, left, bottom - 1);

        g.setColor(SystemColor.controlDkShadow);
        g.drawLine(right, top, right, bottom);

        if (thickBorder) {
          g.setColor(SystemColor.controlHighlight);
          g.drawLine(left + 1, top + 1, right - 2, top + 1);
          g.drawLine(left + 1, top + 1, left + 1, bottom - 2);

          g.setColor(SystemColor.controlShadow);
          g.drawLine(right - 1, top + 1, right - 1, bottom - 1);
        }
      }

      // Draw main separator line
      g.setColor(SystemColor.controlDkShadow);
      g.drawLine(left, bottom, left + EDGE, bottom);
      g.drawLine(leftStart, bottom, right, bottom);

      g.setColor(SystemColor.controlShadow);
      g.drawLine(left + 1, bottom - 1, left + EDGE - 1, bottom - 1);
      g.drawLine(leftStart + 1, bottom - 1, right - 2, bottom - 1);
    }
  }

  public void repaintTab(int index) {
    if (index == -1 || index > tabInfo.size() - 1)
      return;

    TabInfo t = (TabInfo) tabInfo.elementAt(index);
    repaint(100, t.x - xOffset, tabBarTop, t.width, tabBarHeight);
//System.err.println("repainting " + new Rectangle(t.x - xOffset, tabBarTop, t.width, tabBarHeight));
  }

  private void paintTab(Graphics g, int index) {
    if (tabsOnTop)
      paintTopTab(g, index);
    else
      paintBottomTab(g, index);
  }

  private void paintTopTab(Graphics g, int index) {
    TabInfo t = (TabInfo) tabInfo.elementAt(index);
    int selectedIndex = getSelectedIndex();
    if (selectedIndex == -1) { selectedIndex = -2; }
    boolean isSelected = index == selectedIndex;
    if (model.getCount() == 1) { selectedIndex = -2; }
    int state = 0;

    // Paint the client area, or fill it with a texture.
    if (texture != null)
      ImageTexture.texture(texture, g, t.x - xOffset + EDGE, tabBarTop + SEL_HEIGHT_INCREASE, t.width - EDGE - EDGE, tabBarHeight);
    else if (isOpaque()) {
      g.setColor(getBackground());
      g.fillRect(t.x - xOffset, tabBarTop, t.width, tabBarHeight);
    }

    int left = t.x - xOffset;
    int right = left + t.width - 1;
    int top = border + SEL_HEIGHT_INCREASE;
    int bottom = border + tabBarHeight - LINE;

    // Adjust width to account for a 2 pixel expansion of the selected tab.
    if (isSelected) {
      top -= SEL_HEIGHT_INCREASE;
    }

    ItemPainter painter = null;
    if (t.item != null) {
      painter = getPainter(index, state);
    }

    Font normalFont = getFont();
    Font selectedFont = getFont();

    if (isSelected) {
      state |= ItemPainter.SELECTED;
      state |= focusState;
      g.setFont(selectedFont);
    } else {
      g.setFont(normalFont);
    }

    Rectangle itemRect = new Rectangle(left + EDGE, top + EDGE - 2/*!RR*/, t.itemWidth, t.itemHeight);
    if (!isEnabled())
      state |= ItemPainter.DISABLED;
    state |= (index == rollover) ? ItemPainter.ROLLOVER : 0;
    g.setColor(getBackground());

    if (painter != null) {
      painter.paint(t.item, g, itemRect, state, this);
    }

    if (index == selectedIndex + 1) {
      // Outer top right corner, right edge
      g.setColor(SystemColor.controlDkShadow);
      g.drawLine(left, top + 1 - SEL_HEIGHT_INCREASE, left + SLOPE, top + SLOPE + 1 - SEL_HEIGHT_INCREASE);
      g.drawLine(left + SLOPE, top + SLOPE + 2 - SEL_HEIGHT_INCREASE, left + SLOPE, bottom);
      // Inner top right corner, right edge
      g.setColor(SystemColor.controlShadow);
      g.drawLine(left, top + 2 - SEL_HEIGHT_INCREASE, left + SLOPE - 1, top + SLOPE + 2 - SEL_HEIGHT_INCREASE);
      g.drawLine(left + SLOPE - 1, top + SLOPE + 3 - SEL_HEIGHT_INCREASE, left + SLOPE - 1, bottom);
    } else if (index != selectedIndex || index == 0) {
      // Outer left edge, top left corner
      g.setColor(SystemColor.controlLtHighlight);
      g.drawLine(left, top + SLOPE + 1, left, bottom);
      g.drawLine(left + 1, top + SLOPE, left + SLOPE, top + 1);
      // Inner left edge, top left corner
      g.setColor(SystemColor.controlHighlight);
      g.drawLine(left + 1, top + SLOPE + 2, left + 1, bottom);
      g.drawLine(left + 1, top + SLOPE + 1, left + 1 + SLOPE, top + 1);
    }

    int leftAdjust = 0;
    int rightAdjust = 0;
    if (index == selectedIndex) {
      if (index == 0) {
        leftAdjust = EDGE;
      }
      if (index == model.getCount() - 1) {
        rightAdjust = EDGE;
      }
    } else if (index >= 0) {
      leftAdjust = EDGE;
      rightAdjust = EDGE;
    }

    // Outer top edge
    g.setColor(SystemColor.controlLtHighlight);
    g.drawLine(left + leftAdjust, top, right - rightAdjust, top);
    // Inner top edge
    g.setColor(SystemColor.controlHighlight);
    g.drawLine(left + leftAdjust, top + 1, right - rightAdjust, top + 1);

    if (index == selectedIndex - 1) {
      g.setColor(SystemColor.controlLtHighlight);
      g.drawLine(right - SLOPE, top + SLOPE + 1 - SEL_HEIGHT_INCREASE, right - SLOPE, bottom);
      g.drawLine(right - SLOPE + 1, top + SLOPE - SEL_HEIGHT_INCREASE, right, top + 1 - SEL_HEIGHT_INCREASE);
      // Inner left edge, top left corner
      g.setColor(SystemColor.controlHighlight);
      g.drawLine(right - SLOPE + 1, top + SLOPE + 2 - SEL_HEIGHT_INCREASE, right - SLOPE + 1, bottom);
      g.drawLine(right - SLOPE + 1, top + SLOPE + 1, right + 1, top + 1 - SEL_HEIGHT_INCREASE);
    } else if (index != selectedIndex || index == model.getCount() - 1) {
      // Outer top right corner, right edge
      g.setColor(SystemColor.controlDkShadow);
      g.drawLine(right - SLOPE, top + 1, right, top + SLOPE + 1);
      g.drawLine(right, top + SLOPE + 2, right, bottom);
      // Inner top right corner, right edge
      g.setColor(SystemColor.controlShadow);
      g.drawLine(right - SLOPE, top + 2, right - 1, top + SLOPE + 2);
      g.drawLine(right - 1, top + SLOPE + 3, right - 1, bottom);
    }

    if (!isSelected) {

      leftAdjust = 0;
      rightAdjust = 0;
      if (index == selectedIndex - 1) {
        rightAdjust = EDGE;
      } else if (index == selectedIndex + 1) {
        leftAdjust = EDGE;
      };
      // paint main separator line below tab
      int lineY = border + tabBarHeight - LINE;
      g.setColor(SystemColor.controlLtHighlight);
      g.drawLine(left + leftAdjust, lineY, right - rightAdjust, lineY);
      g.setColor(SystemColor.controlHighlight);
      g.drawLine(left + leftAdjust, lineY + 1, right - rightAdjust, lineY + 1);
    }
  }

  private void paintBottomTab(Graphics g, int index) {
    TabInfo t = (TabInfo) tabInfo.elementAt(index);
    boolean isSelected = index == getSelectedIndex();
    int state = 0;

    // Paint the client area, or fill it with a texture.
    if (texture != null)
      ImageTexture.texture(texture, g, t.x - xOffset + EDGE, tabBarTop, t.width - EDGE - EDGE, tabBarHeight - SEL_HEIGHT_INCREASE);
    else if (isOpaque()) {
      g.setColor(getBackground());
      g.fillRect(t.x - xOffset, tabBarTop, t.width, tabBarHeight);
    }

    int left = t.x - xOffset;
    int right = left + t.width - 1;
    int top = getSize().height - border - tabBarHeight + LINE - 1;
    int bottom = getSize().height - border - (isSelected ? 0 : SEL_HEIGHT_INCREASE) - 1;

    ItemPainter painter = null;
    if (t.item != null) {
      painter = getPainter(index, state);
    }

    Font normalFont = getFont();
    Font selectedFont = getFont();

    // Outer right edge, bottom right corner, bottom edge
    g.setColor(SystemColor.controlDkShadow);
    g.drawLine(right, top, right, bottom - 1 - SLOPE);
    g.drawLine(right, bottom - 1 - SLOPE, right - SLOPE, bottom - 1);
    g.drawLine(right - SLOPE - 1, bottom, left + SLOPE + 1, bottom);

    // Inner right edge, bottom right corner, bottom edge
    g.setColor(SystemColor.controlShadow);
    g.drawLine(right - 1, top, right - 1, bottom - 1 - SLOPE);
    g.drawLine(right - 1, bottom - 2 - SLOPE, right - 1 - SLOPE, bottom - 2);
    g.drawLine(right - 1 - SLOPE, bottom - 1, left + SLOPE, bottom - 1);

    // Outer bottom left corner, left edge
    g.setColor(SystemColor.controlLtHighlight);
    g.drawLine(left + SLOPE, bottom - 1, left, bottom - 1 - SLOPE);
    g.drawLine(left, bottom - 1 - SLOPE, left, top);

    // Inner bottom left corner, left edge
    g.setColor(SystemColor.controlHighlight);
    g.drawLine(left + SLOPE, bottom - 2, left + 1, bottom - 2 - SLOPE);
    g.drawLine(left + 1, bottom - 2 - SLOPE, left + 1, top);

    if (isSelected) {
      state |= ItemPainter.SELECTED;
      state |= focusState;
      g.setFont(selectedFont);

      // Paint just a tiny bit to get this just right.
      int lineY = getSize().height - tabBarHeight - border;
      g.setColor(SystemColor.controlShadow);
      g.drawLine(left, lineY, left, lineY);
      g.drawLine(right - 1, lineY, right, lineY);
    }
    else {
      // paint main separator line above tab
      int lineY = getSize().height - tabBarHeight - border;
      g.setColor(SystemColor.controlDkShadow);
      g.drawLine(left, lineY + 1, right, lineY + 1);
      g.setColor(SystemColor.controlShadow);
      g.drawLine(left, lineY, right, lineY);
      g.setFont(normalFont);
    }

    Rectangle itemRect = new Rectangle(left + EDGE, bottom - EDGE - t.itemHeight + 1, t.itemWidth, t.itemHeight);
    if (!isEnabled())
      state |= ItemPainter.DISABLED;
    state |= (index == rollover) ? ItemPainter.ROLLOVER : 0;
    g.setColor(getBackground());
    if (t.item != null) {
      painter.paint(t.item, g, itemRect, state, this);
    }
  }

  private void paintScrollArrows(Graphics g) {
    if (lastClipped) {
      int lineY   = 0;

      int top     = tabBarTop;
      int bottom  = tabBarTop + tabBarHeight - 1;
      int left    = leftArrowRect.x - EDGE - itemMargins.left + 1;
      int right   = getSize().width - 1;

      // Clear the background area where we'll paint the tabs.
      g.setColor(getBackground());
      g.fillRect(left, top, right - left + 1, tabBarHeight);

      if (tabsOnTop) {
        // paint main separator line below tab
        lineY = border + tabBarHeight - LINE;
        g.setColor(SystemColor.controlLtHighlight);
        g.drawLine(left, lineY, right, lineY);
        g.setColor(SystemColor.controlHighlight);
        g.drawLine(left, lineY + 1, right, lineY + 1);

        // We've blown away a bit of the client border here, so repaint
        // it if necessary.
        if (clientBordered) {
          g.setColor(SystemColor.controlDkShadow);
          g.drawLine(right, lineY, right, lineY + 1);
          if (thickBorder) {
            g.setColor(SystemColor.controlShadow);
            g.drawLine(right - 1, lineY + 1, right - 1, lineY + 1);
          }
        }
      }
      else {
        // paint main separator line above tab
        lineY = getSize().height - tabBarHeight - border;
        g.setColor(SystemColor.controlDkShadow);
        g.drawLine(left, lineY + 1, right, lineY + 1);
        g.setColor(SystemColor.controlShadow);
        g.drawLine(left, lineY, right, lineY);

        // We've blown away a bit of the client border here, so repaint
        // it if necessary.
        if (clientBordered) {
          g.setColor(SystemColor.controlDkShadow);
          g.drawLine(right, lineY, right, lineY);
        }
      }

      int sel = getSelectedIndex();

      // If the tab at the cutoff point is not selected, adjust the height of the
      // jagged line to take that into account.
      if (sel == -1 || sel != hitTestX(left)) {
        if (tabsOnTop) {
          top = top + SEL_HEIGHT_INCREASE;
        }
        else {
          bottom = bottom - SEL_HEIGHT_INCREASE;
        }
      }

      g.setColor(SystemColor.controlShadow);

      int horizAdj = 0;
      int horizIncr = 1;
      int lineLength = 0;
      int numPoints = bottom - top;
      int[] xPoints = new int[numPoints];
      int[] yPoints = new int[numPoints];
      int pointCounter = 0;

      for (int i = top; i < bottom; i++) {
        xPoints[pointCounter] = left + horizAdj;
        yPoints[pointCounter] = i;
        pointCounter++;

        if (lineLength == 1) {
          lineLength = 0;
          horizAdj += horizIncr;
          if (horizAdj == 2)
            horizIncr = -1;
          else if (horizAdj == 0)
            horizIncr = 1;
        }
        else
          lineLength++;
      }
      g.drawPolyline(xPoints, yPoints, numPoints);
    }

/*
    if (lastClipped) {
      int lineY   = 0;
      int top     = 0;
      int bottom  = 0;
      int left    = leftArrowRect.x - 3 * EDGE + 1;
      int right   = getSize().width;

      if (tabsOnTop) {
        lineY = border + tabBarHeight - LINE;
        top     = border + SEL_HEIGHT_INCREASE;
        bottom  = lineY - 1;

        g.setColor(getBackground());
        g.fillRect(left, border, right, tabBarHeight - LINE);
        if (getSelectedIndex() == lastVisible) {
          top = top - SEL_HEIGHT_INCREASE;
          g.setColor(SystemColor.controlLtHighlight);
          g.drawLine(left, lineY, right, lineY);
          g.setColor(SystemColor.controlHighlight);
          g.drawLine(left, lineY + 1, right, lineY + 1);
        }
      }
      else {
        lineY = getSize().height - tabBarHeight - border;
        top = lineY + LINE;
        bottom = lineY + tabBarHeight - SEL_HEIGHT_INCREASE - 1;

        g.setColor(getBackground());
        g.fillRect(left, top, right, tabBarHeight - LINE);
        if (getSelectedIndex() == lastVisible) {
          bottom = bottom + SEL_HEIGHT_INCREASE;

          g.setColor(SystemColor.controlDkShadow);
          g.drawLine(left, lineY + 1, right, lineY + 1);
          g.setColor(SystemColor.controlShadow);
          g.drawLine(left, lineY, right, lineY);
        }
      }

      g.setColor(SystemColor.controlShadow);

      int horizAdj = 0;
      int horizIncr = 1;
      int lineLength = 0;
      int numPoints = bottom - top;
      int[] xPoints = new int[numPoints];
      int[] yPoints = new int[numPoints];
      int pointCounter = 0;

      for (int i = top; i < bottom; i++) {
        xPoints[pointCounter] = left + horizAdj;
        yPoints[pointCounter] = i;
        pointCounter++;

        if (lineLength == 1) {
          lineLength = 0;
          horizAdj += horizIncr;
          if (horizAdj == 2)
            horizIncr = -1;
          else if (horizAdj == 0)
            horizIncr = 1;
        }
        else
          lineLength++;
      }
      g.drawPolyline(xPoints, yPoints, numPoints);
    }
*/
    paintScrollArrow(g, true);
    paintScrollArrow(g, false);
  }

  private void paintScrollArrow(Graphics g, boolean left) {
    Rectangle r = left ? leftArrowRect : rightArrowRect;

    // Paint the box around the arrow glyphs.
    g.setColor(SystemColor.controlLtHighlight);
    g.drawLine(r.x, r.y, r.x + r.width - 1, r.y);
    g.drawLine(r.x, r.y, r.x, r.y + r.height - 1);

    g.setColor(SystemColor.controlHighlight);
    g.drawLine(r.x + 1, r.y + 1, r.x + r.width - 2, r.y + 1);
    g.drawLine(r.x + 1, r.y + 1, r.x + 1, r.y + r.height - 2);

    g.setColor(SystemColor.controlShadow);
    g.drawLine(r.x + 1, r.y + r.height - 1, r.x + r.width - 1, r.y + r.height - 1);
    g.drawLine(r.x + r.width - 1, r.y + 1, r.x + r.width - 1, r.y + r.height - 1);

    g.setColor(SystemColor.controlDkShadow);
    g.drawLine(r.x, r.y + r.height, r.x + r.width, r.y + r.height);
    g.drawLine(r.x + r.width, r.y, r.x + r.width, r.y + r.height);

    // Here we draw the actual arrow glyphs based on their state.
    if (left) {
      if (firstVisible > 0)
        g.setColor(SystemColor.controlDkShadow);
      else
        g.setColor(SystemColor.controlShadow);

      g.drawLine(r.x + r.width / 2 - 1, r.y + r.height / 2, r.x + r.width / 2 - 1, r.y + r.height / 2);
      g.drawLine(r.x + r.width / 2, r.y + r.height / 2 - 1, r.x + r.width / 2, r.y + r.height / 2 + 1);
      g.drawLine(r.x + r.width / 2 + 1, r.y + r.height / 2 - 2, r.x + r.width / 2 + 1, r.y + r.height / 2 + 2);
    }
    else {
      if (lastClipped)
        g.setColor(SystemColor.controlDkShadow);
      else
        g.setColor(SystemColor.controlShadow);

      g.drawLine(r.x + r.width / 2 + 1, r.y + r.height / 2, r.x + r.width / 2 + 1, r.y + r.height / 2);
      g.drawLine(r.x + r.width / 2, r.y + r.height / 2 - 1, r.x + r.width / 2, r.y + r.height / 2 + 1);
      g.drawLine(r.x + r.width / 2 - 1, r.y + r.height / 2 - 2, r.x + r.width / 2 - 1, r.y + r.height / 2 + 2);
    }
  }

  /** @DEPRECATED - Use setDoubleBuffered(boolean buffer) method */
  public void setDoubleBuffer(boolean buffer) {
    setDoubleBuffered(buffer);
  }
  /** @DEPRECATED - Use isDoubleBuffered() method */
  public boolean isDoubleBuffer() {
    return isDoubleBuffered();
  }

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    Hashtable hash = new Hashtable(4);
    if (model instanceof Serializable)
      hash.put("m", model); 
    if (viewManager instanceof Serializable)
      hash.put("v", viewManager); 
    if (selection instanceof Serializable)
      hash.put("s", selection); 
    if (defaultPainter instanceof Serializable)
      hash.put("p", defaultPainter); 
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
    data = hash.get("p"); 
    if (data instanceof ItemPainter)
      defaultPainter = (ItemPainter)data;
  }

  // fields

  protected transient VectorModel           model;
  protected transient WritableVectorModel   writeModel;
  protected transient VectorViewManager     viewManager;
  protected transient WritableVectorSelection selection = new SingleVectorSelection();   // selection pool
  protected transient ItemPainter defaultPainter;

  private int           alignment       = Alignment.CENTER | Alignment.MIDDLE;
  private boolean       clientBordered  = false;
  private Insets        itemMargins     = new Insets(1, 4, 1, 4);
  private boolean       readOnly        = false;
  private boolean       tabsOnTop       = true;
  private boolean       showRollover    = false;
  private int           rollover        = -1;
  boolean               thickBorder     = true;
  int                   border  = 2;    // Number of pixels between the tallest tab & the edge
  private int           tabBarTop;      // Top of the tab area including lines & margins
  protected int         tabBarWidth;    // width of the whole tab area including lines & margins
  protected int         tabBarHeight;   // height of the whole tab area including lines & margins
  private int           oldIndex = -1;

  // Scrollbar state
  int                   firstVisible    = 0;
  int                   lastVisible   = -1;
  int                   xOffset         = 0;
  Rectangle             leftArrowRect   = new Rectangle();
  Rectangle             rightArrowRect  = new Rectangle();
  boolean               needScroller    = false;
  boolean               lastClipped     = false;

  // Listeners
  transient EventMulticaster subfocusListeners = new com.borland.jb.util.EventMulticaster();
  private transient VectorModelMulticaster modelMulticaster = new VectorModelMulticaster();
  private transient VectorSelectionMulticaster selectionMulticaster = new VectorSelectionMulticaster();

  Vector tabInfo = new Vector();
}

class TabInfo implements Serializable
{
  transient Object item;
  int     x;
  int     width;
  int     itemWidth;
  int     itemHeight;

  TabInfo(Object item) { this.item = item; }
  boolean hitTestX(int xPoint) { return (xPoint >= x && xPoint < x + width); }
  public String toString() {
    return "[TabInfo x=" + x + " width=" + width + " item=" + item + " itemWidth=" + itemWidth + " itemHeight=" + itemHeight + "]";  
  }

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    s.writeObject(item instanceof Serializable ? item : null);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    item = s.readObject();
  }
}
