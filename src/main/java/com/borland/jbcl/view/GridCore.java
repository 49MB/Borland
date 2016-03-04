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

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
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
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JToolTip;
import javax.swing.JViewport;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import com.borland.dx.dataset.CustomPaintSite;
import com.borland.dx.dataset.Variant;
import com.borland.dx.text.Alignment;
import com.borland.jb.util.EventMulticaster;
import com.borland.jbcl.model.BasicMatrixSelection;
import com.borland.jbcl.model.ItemEditSite;
import com.borland.jbcl.model.ItemEditor;
import com.borland.jbcl.model.ItemPainter;
import com.borland.jbcl.model.MatrixLocation;
import com.borland.jbcl.model.MatrixModel;
import com.borland.jbcl.model.MatrixModelEvent;
import com.borland.jbcl.model.MatrixModelListener;
import com.borland.jbcl.model.MatrixModelMulticaster;
import com.borland.jbcl.model.MatrixSelectionEvent;
import com.borland.jbcl.model.MatrixSelectionListener;
import com.borland.jbcl.model.MatrixSelectionMulticaster;
import com.borland.jbcl.model.MatrixSubfocusEvent;
import com.borland.jbcl.model.MatrixSubfocusListener;
import com.borland.jbcl.model.MatrixViewManager;
import com.borland.jbcl.model.NullMatrixSelection;
import com.borland.jbcl.model.SelectionEvent;
import com.borland.jbcl.model.SubfocusEvent;
import com.borland.jbcl.model.ToggleItemEditor;
import com.borland.jbcl.model.WritableMatrixModel;
import com.borland.jbcl.model.WritableMatrixSelection;
import com.borland.jbcl.util.ColorWheel;
import com.borland.jbcl.util.ImageTexture;
import com.borland.jbcl.util.KeyMulticaster;
import com.borland.jbcl.util.SelectFlags;

class GridCore extends BeanPanel implements ItemEditSite, KeyListener,
    FocusListener, MatrixModelListener, MatrixSelectionListener, MatrixView,
    Serializable {
  private static final long serialVersionUID = 200L;
  
  public GridCore(JScrollPane scroller) {
    this.scroller = scroller;
    actionSource = scroller;
    divider.setVisible(false);
    add(divider);
    super.addKeyListener(keyMulticaster);
    scroller.getVerticalScrollBar().setUnitIncrement(20);
    scroller.getHorizontalScrollBar().setUnitIncrement(20);
    scroller.getViewport().setScrollMode(
        useBackingStore ? JViewport.BACKINGSTORE_SCROLL_MODE
            : JViewport.BLIT_SCROLL_MODE);
    super.setBackground(UIManager.getColor("Table.background"));
    super.setForeground(UIManager.getColor("Table.foreground"));
    setGridLineColor(UIManager.getColor("Table.gridColor"));
  }
  
  public void updateUI() {
    super.updateUI();
    super.setBackground(UIManager.getColor("Table.background"));
    super.setForeground(UIManager.getColor("Table.foreground"));
    setGridLineColor(UIManager.getColor("Table.gridColor"));
  }
  
  // MatrixView Implementation
  
  public MatrixModel getModel() {
    return model;
  }
  
  public void setModel(MatrixModel mm) {
    if (editor != null)
      safeEndEdit();
    if (model != null) {
      model.removeModelListener(this);
      model.removeModelListener(modelMulticaster);
    }
    model = mm;
    if (model != null) {
      model.addModelListener(this);
      model.addModelListener(modelMulticaster);
    }
    if (model instanceof WritableMatrixModel)
      writeModel = (WritableMatrixModel) model;
    else
      writeModel = null;
    if (model != null) {
      invalidate();
      reset();
    }
  }
  
  public WritableMatrixModel getWriteModel() {
    return readOnly ? null : writeModel;
  }
  
  public boolean isReadOnly() {
    return readOnly ? true : writeModel == null;
  }
  
  public void setReadOnly(boolean ro) {
    readOnly = ro;
  }
  
  public MatrixViewManager getViewManager() {
    return viewManager;
  }
  
  public void setViewManager(MatrixViewManager newViewManager) {
    if (editor != null)
      safeEndEdit();
    viewManager = newViewManager;
    invalidate();
    reset();
  }
  
  public WritableMatrixSelection getSelection() {
    return selection;
  }
  
  public void setSelection(WritableMatrixSelection wms) {
    if (selection != null) {
      selection.removeSelectionListener(this);
      selection.removeSelectionListener(selectionMulticaster);
    }
    selection = wms;
    if (selection != null) {
      selection.addSelectionListener(this);
      selection.addSelectionListener(selectionMulticaster);
    }
    oldSelected = selection.getAll();
    repaintCells();
  }
  
  public void setActionSource(Object actionSource) {
    this.actionSource = actionSource;
  }
  
  protected void registerColumnViews() {
    if (columnViews == null)
      return;
    for (int i = 0; i < columnViews.length; i++)
      columnViews[i].setGridCore(this);
  }
  
  public void columnViewChanged(ColumnView cv, int prop) {
    int cvIndex = -1;
    for (int i = 0; i < columnViews.length; i++) {
      if (cv == columnViews[i]) {
        cvIndex = i;
        break;
      }
    }
    if (cvIndex == -1)
      return;
    if (prop == ColumnView.PROP_FONT || prop == ColumnView.PROP_ALIGNMENT
        || prop == ColumnView.PROP_BACKGROUND || prop == ColumnView.PROP_FOREGROUND
        || prop == ColumnView.PROP_MARGINS || prop == ColumnView.PROP_ITEMPAINTER) {
      repaintCells(new MatrixLocation(0, cvIndex), new MatrixLocation(
          getRowCount() - 1, cvIndex));
      return;
    } else if (prop == ColumnView.PROP_WIDTH) {
      int width = cv.getWidth();
      columnSizes.setSize(cvIndex, width > MIN_CELL_SIZE ? width
          : MIN_CELL_SIZE);
      invalidate();
      processActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, cv
          .getName(), ColumnView.PROP_WIDTH));
      scroller.validate();
      repaintCells();
    } else if (prop == ColumnView.PROP_CAPTION) {
      processActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, cv
          .getName(), ColumnView.PROP_CAPTION));
    }
  }
  
  public ColumnView[] getColumnViews() {
    return columnViews;
  }
  
  public void setColumnViews(ColumnView[] columnViews) {
    if (columnViews == null) {
      this.columnViews = new ColumnView[0];
    } else
      this.columnViews = columnViews;
    registerColumnViews();
    resetColumnSizes();
  }
  
  public ColumnView getColumnView(int index) {
    return columnViews != null && columnViews.length > index ? columnViews[index]
        : null;
  }
  
  public void setColumnView(int index, ColumnView col) {
    if (columnViews.length > index) {
      columnViews[index] = col;
      columnViews[index].setGridCore(this);
      int width = col.getWidth();
      if (width == 0)
        width = defaultColWidth;
      columnSizes.setSize(index, width > MIN_CELL_SIZE ? width : MIN_CELL_SIZE);
      col.setWidth(columnSizes.getSize(index));
    } else
      throw new IllegalArgumentException();
  }
  
  public Insets getItemMargins() {
    if (subfocus != null && columnViews != null
        && columnViews.length > subfocus.column)
      return columnViews[subfocus.column].getItemMargins();
    else
      return null;
  }
  
  public int getAlignment() {
    if (subfocus != null && columnViews != null
        && columnViews.length > subfocus.column)
      return columnViews[subfocus.column].getAlignment();
    else
      return 0;
  }
  
  // Events
  
  public void addModelListener(MatrixModelListener l) {
    modelMulticaster.add(l);
  }
  
  public void removeModelListener(MatrixModelListener l) {
    modelMulticaster.remove(l);
  }
  
  public void addSelectionListener(MatrixSelectionListener l) {
    selectionMulticaster.add(l);
  }
  
  public void removeSelectionListener(MatrixSelectionListener l) {
    selectionMulticaster.remove(l);
  }
  
  public void addSubfocusListener(MatrixSubfocusListener l) {
    subfocusMulticaster.add(l);
  }
  
  public void removeSubfocusListener(MatrixSubfocusListener l) {
    subfocusMulticaster.remove(l);
  }
  
  public void addKeyListener(KeyListener l) {
    keyMulticaster.add(l);
  }
  
  public void removeKeyListener(KeyListener l) {
    keyMulticaster.remove(l);
  }
  
  // MatrixModelListener implementation
  
  public void modelContentChanged(MatrixModelEvent e) {
    MatrixLocation ml = e.getLocation();
    switch (e.getChange()) {
    case MatrixModelEvent.CONTENT_CHANGED:
      repaintCells();
      break;
    case MatrixModelEvent.ITEM_CHANGED:
    case MatrixModelEvent.ITEM_TOUCHED:
      if (editor != null && editorLocation != null && editorLocation.equals(ml))
        safeEndEdit(false);
      repaintCell(ml);
      break;
    case MatrixModelEvent.ROW_CHANGED:
      if (editor != null && editorLocation != null
          && editorLocation.row == ml.row)
        safeEndEdit(false);
      repaintCells(new MatrixLocation(ml.row, 0), new MatrixLocation(
          getRowCount() - 1, getColumnCount() - 1));
      break;
    case MatrixModelEvent.COLUMN_CHANGED:
      if (editor != null && editorLocation != null
          && editorLocation.column == ml.column)
        safeEndEdit(false);
      repaintCells(new MatrixLocation(0, ml.column), new MatrixLocation(
          getRowCount() - 1, getColumnCount() - 1));
      break;
    }
  }
  
  public void modelStructureChanged(MatrixModelEvent e) {
    MatrixLocation ml = e.getLocation();
    switch (e.getChange()) {
    case MatrixModelEvent.STRUCTURE_CHANGED:
      if (editor != null)
        safeEndEdit(false);
      invalidate();
      repaintCells();
      if (isShowing() && !batchMode) {
        scroller.validate();
        scrollView();
      }
      break;
    case MatrixModelEvent.ROW_ADDED:
    case MatrixModelEvent.ROW_REMOVED:
      int rows = e.getModel().getRowCount();
      if (editor != null
          && editorLocation != null
          && (ml != null && ml.row == editorLocation.row || editorLocation.row >= rows)) {
        safeEndEdit(false);
      }
      invalidate();
      if (e.getChange() == MatrixModelEvent.ROW_REMOVED && subfocus != null
          && rows <= subfocus.row && rows > 0) {
        setSubfocus(rows - 1, subfocus.column);
      }
      repaintCells();
      if (isShowing() && !batchMode) {
        scroller.validate();
        scrollView();
      }
      break;
    case MatrixModelEvent.COLUMN_ADDED:
      addColumnView(ml.column);
      invalidate();
      repaintCells();
      if (isShowing() && !batchMode) {
        scroller.validate();
        scrollView();
      }
      break;
    case MatrixModelEvent.COLUMN_REMOVED:
      int cols = e.getModel().getColumnCount();
      if (subfocus != null && cols <= subfocus.column && cols > 0) {
        setSubfocus(subfocus.row, cols - 1);
      }
      dropColumnView(ml.column);
      invalidate();
      repaintCells();
      if (isShowing() && !batchMode) {
        scroller.validate();
        scrollView();
      }
      break;
    }
  }
  
  protected void addColumnView(int newColumn) {
    ColumnView[] newViews = new ColumnView[columnViews.length + 1];
    if (newColumn == 0) {
      System.arraycopy(columnViews, 0, newViews, 1, columnViews.length);
    } else if (newColumn >= columnViews.length) {
      System.arraycopy(columnViews, 0, newViews, 0, columnViews.length);
    } else {
      System.arraycopy(columnViews, 0, newViews, 0, newColumn);
      System.arraycopy(columnViews, newColumn, newViews, newColumn + 1,
          columnViews.length - newColumn);
    }
    newViews[newColumn] = new ColumnView();
    defaultColumnView(newColumn, newViews[newColumn]);
    for (int i = 0; i < newViews.length; i++) {
      int w = newViews[i].getWidth();
      columnSizes.setSize(i, w > MIN_CELL_SIZE ? w : MIN_CELL_SIZE);
      newViews[i].setWidth(columnSizes.getSize(i));
    }
    columnViews = newViews;
    registerColumnViews();
  }
  
  protected void defaultColumnView(int col, ColumnView c) {
    String name = java.text.MessageFormat.format(Res._ColumnName,
        new Object[] { String.valueOf(col) });
    c.setName(name);
    c.setAlignment(Alignment.LEFT | Alignment.MIDDLE);
    c.setItemMargins(new Insets(0, 2, 0, 2));
    c.setWidth(defaultColWidth);
  }
  
  protected void dropColumnView(int dropColumn) {
    ColumnView[] newViews = new ColumnView[columnViews.length - 1];
    if (dropColumn == 0)
      System.arraycopy(columnViews, 1, newViews, 0, columnViews.length - 1);
    else if (dropColumn == newViews.length) {
      System.arraycopy(columnViews, 0, newViews, 0, newViews.length);
    } else {
      System.arraycopy(columnViews, 0, newViews, 0, dropColumn);
      System.arraycopy(columnViews, dropColumn + 1, newViews, dropColumn,
          newViews.length - dropColumn);
    }
    for (int i = dropColumn; i < newViews.length; i++) {
      int w = newViews[i].getWidth();
      columnSizes.setSize(i, w > MIN_CELL_SIZE ? w : MIN_CELL_SIZE);
      newViews[i].setWidth(columnSizes.getSize(i));
    }
    columnViews = newViews;
    registerColumnViews();
  }
  
  // MatrixSelectionListener implementation
  
  public void selectionItemChanged(MatrixSelectionEvent e) {
    // Diagnostic.trace(Trace.SelectionEvents,
    // "GridCore.ITEM_ADDED or ITEM_REMOVED");
    repaintCell(e.getLocation());
    oldSelected = e.getSelection().getAll();
  }
  
  public void selectionRangeChanged(MatrixSelectionEvent e) {
    switch (e.getChange()) {
    case SelectionEvent.RANGE_ADDED:
      if (dumpingRange) {
        selectionChanged(e);
        dumpingRange = false;
      } else {
        repaintCells(e.getRangeStart(), e.getRangeEnd());
        oldSelected = e.getSelection().getAll();
      }
      break;
    case SelectionEvent.RANGE_REMOVED:
      if (!dumpingRange) {
        repaintCells(e.getRangeStart(), e.getRangeEnd());
        oldSelected = e.getSelection().getAll();
      }
      break;
    }
  }
  
  public void selectionChanged(MatrixSelectionEvent e) {
    switch (e.getChange()) {
    case SelectionEvent.SELECTION_CLEARED:
      repaintSelection();
      break;
    case SelectionEvent.SELECTION_CHANGED:
    default:
      MatrixLocation[] selected = e.getSelection().getAll();
      WritableMatrixSelection old = new BasicMatrixSelection(oldSelected);
      for (int i = 0; i < selected.length; i++) {
        if (!old.contains(selected[i]))
          repaintCell(selected[i]);
        else
          old.remove(selected[i]);
      }
      oldSelected = old.getAll();
      for (int i = 0; i < oldSelected.length; i++)
        repaintCell(oldSelected[i]);
      break;
    }
    oldSelected = e.getSelection().getAll();
  }
  
  public void windowActiveChanged(boolean active) {
    super.windowActiveChanged(active);
    repaintSelection();
  }
  
  // Focus Events
  
  public void focusGained(FocusEvent e) {
  }
  
  public void focusLost(FocusEvent e) {
    if (hasFocus) {
      hasFocus = false;
      repaintCell(subfocus);
    }
  }
  
  protected void processFocusEvent(FocusEvent e) {
    switch (e.getID()) {
    case FocusEvent.FOCUS_GAINED:
      if (editor != null && editor.getComponent() != null)
        editor.getComponent().requestFocus();
      if (!hasFocus) {
        hasFocus = true;
        repaintCell(subfocus);
      }
      break;
    case FocusEvent.FOCUS_LOST:
      if (editor == null) {
        hasFocus = false;
        repaintCell(subfocus);
      }
      break;
    }
    super.processFocusEvent(e);
  }
  
  // Properties
  
  public int getVisibleRows() {
    return getVisibleCount(true);
  }
  
  public int getVisibleColumns() {
    return getVisibleCount(false);
  }
  
  int getVisibleCount(boolean rows) {
    MatrixLocation first = hitTest(1, 1);
    if (first == null)
      first = new MatrixLocation();
    MatrixLocation last = hitTest(getSize().width, getSize().height);
    if (last == null)
      last = new MatrixLocation(getRowCount() - 1, getColumnCount() - 1);
    return (rows) ? last.row - first.row + 1 : last.column - first.column + 1;
  }
  
  public boolean isGridVisible() {
    return visibleGrid;
  }
  
  public void setGridVisible(boolean visible) {
    if (visibleGrid != visible) {
      visibleGrid = visible;
      repaintCells();
    }
  }
  
  public boolean isHorizontalLines() {
    return hGridLines;
  }
  
  public void setHorizontalLines(boolean visible) {
    if (hGridLines != visible) {
      hGridLines = visible;
      repaintCells();
    }
  }
  
  public boolean isVerticalLines() {
    return vGridLines;
  }
  
  public void setVerticalLines(boolean visible) {
    if (vGridLines != visible) {
      vGridLines = visible;
      repaintCells();
    }
  }
  
  public Color getGridLineColor() {
    return lineColor;
  }
  
  public void setGridLineColor(Color gridLineColor) {
    if (gridLineColor != null) {
      lineColor = gridLineColor;
      repaintCells();
    } else
      throw new IllegalArgumentException();
  }
  
  public boolean isShowFocus() {
    return showFocus;
  }
  
  public void setShowFocus(boolean visible) {
    if (showFocus != visible) {
      showFocus = visible;
      repaintCell(getSubfocus());
    }
  }
  
  public int getDefaultColumnWidth() {
    return defaultColWidth;
  }
  
  public void setDefaultColumnWidth(int defaultWidth) {
    if (defaultWidth > 0)
      defaultColWidth = defaultWidth;
    else
      throw new IllegalArgumentException();
  }
  
  public boolean isPostOnEndEdit() {
    return postOnEndEdit;
  }
  
  public void setPostOnEndEdit(boolean post) {
    postOnEndEdit = post;
  }
  
  public boolean isEditing() {
    return editor != null;
  }
  
  public ItemEditor getEditor() {
    return editor;
  }
  
  public void setAutoEdit(boolean autoEdit) {
    this.autoEdit = autoEdit;
  }
  
  public boolean isAutoEdit() {
    return autoEdit;
  }
  
  public void setGrowEditor(boolean growEditor) {
    this.growEditor = growEditor;
  }
  
  public boolean isGrowEditor() {
    return growEditor;
  }
  
  public void setAutoAppend(boolean autoAppend) {
    this.autoAppend = autoAppend;
  }
  
  public boolean isAutoAppend() {
    return autoAppend;
  }
  
  public void setNavigateOnEnter(boolean navigateOnEnter) {
    this.navigateOnEnter = navigateOnEnter;
  }
  
  public boolean isNavigateOnEnter() {
    return navigateOnEnter;
  }
  
  public void setNavigateOnTab(boolean navigateOnTab) {
    this.navigateOnTab = navigateOnTab;
  }
  
  public boolean isNavigateOnTab() {
    return navigateOnTab;
  }
  
  public void setDragSubfocus(boolean dragSubfocus) {
    this.dragSubfocus = dragSubfocus;
  }
  
  public boolean isDragSubfocus() {
    return dragSubfocus;
  }
  
  public void setSnapOrigin(boolean snapOrigin) {
    this.snapOrigin = snapOrigin;
  }
  
  public boolean isSnapOrigin() {
    return snapOrigin;
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
  
  public void setEditInPlace(boolean editInPlace) {
    this.editInPlace = editInPlace;
  }
  
  public boolean isEditInPlace() {
    return editInPlace;
  }
  
  public void setBatchMode(boolean batchMode) {
    if (this.batchMode != batchMode) {
      this.batchMode = batchMode;
      if (!this.batchMode) {
        repaintCells();
        if (isShowing())
          scroller.validate();
      }
    }
  }
  
  public boolean isBatchMode() {
    return batchMode;
  }
  
  /**
   * Used by editors which wish to set the insertion point at the clicked
   * position. This method returns the mouse click position, or null if no mouse
   * click initiated the editing
   */
  public Point getEditClickPoint() {
    return editClickPoint;
  }
  
  public boolean isTransparent() {
    return texture != null ? true : !isOpaque();
  }
  
  public Graphics getSiteGraphics() {
    return getGraphics();
  }
  
  public Component getSiteComponent() {
    return this;
  }
  
  public SizeVector getRowSizes() {
    return rowSizes;
  }
  
  public void setRowSizes(SizeVector newRowSizes) {
    rowSizes = newRowSizes;
    invalidate();
    repaintCells();
  }
  
  public SizeVector getColumnSizes() {
    return columnSizes;
  }
  
  public void setColumnSizes(SizeVector newColumnSizes) {
    columnSizes = newColumnSizes;
    invalidate();
    repaintCells();
  }
  
  private void scrollView() {
    Rectangle fRect = getCellRect(subfocus);
    Rectangle vRect = scroller.getViewport().getViewRect();
    int x = vRect.x;
    int y = vRect.y;
    if (fRect != null) {
      boolean offRight = false;
      boolean offBottom = false;
      if (fRect.width > vRect.width || fRect.x < vRect.x)
        x = fRect.x;
      else if (fRect.x + fRect.width > vRect.x + vRect.width) {
        x = (getSize().width - vRect.width) < (fRect.x + fRect.width - vRect.width) ? getSize().width
            - vRect.width
            : fRect.x + fRect.width - vRect.width;
        offRight = true;
      }
      if (fRect.y < vRect.y)
        y = fRect.y;
      else if (fRect.y + fRect.height > vRect.y + vRect.height) {
        y = (getSize().height - vRect.height) < (fRect.y + fRect.height - vRect.height) ? getSize().height
            - vRect.height
            : fRect.y + fRect.height - vRect.height;
        offBottom = true;
      }
      if (snapOrigin) {
        MatrixLocation o = hitTest(x, y);
        if (o != null) {
          if (offRight && offBottom) {
            o.row++;
            o.column++;
            Rectangle oRect = getCellRect(o);
            x = oRect.x;
            y = oRect.y;
          } else if (offRight) {
            o.column++;
            Rectangle oRect = getCellRect(o);
            x = oRect.x;
          } else if (offBottom) {
            o.row++;
            Rectangle oRect = getCellRect(o);
            y = oRect.y;
          }
        }
      }
      if (vRect.x != x || vRect.y != y) {
        JViewport jvp = scroller.getViewport();
        // jvp.setBackingStoreEnabled(false);
        jvp.setViewPosition(new Point(x, y));
        // jvp.setBackingStoreEnabled(true);
        repaint();
      }
      scroller.getHorizontalScrollBar().setUnitIncrement(fRect.width);
      scroller.getVerticalScrollBar().setUnitIncrement(fRect.height);
    }
  }
  
  private int getState(int row, int column) {
    int state = 0;
    if (selection.contains(row, column))
      state |= ItemPainter.SELECTED;
    if (!isEnabled())
      state |= ItemPainter.DISABLED | ItemPainter.INACTIVE;
    else {
      if (showFocus && hasFocus && subfocus.row == row
          && subfocus.column == column)
        state |= ItemPainter.FOCUSED;
      if ((focusState & ItemPainter.INACTIVE) != 0)
        state |= ItemPainter.INACTIVE;
      if (showRollover && rollover != null && rollover.row == row
          && rollover.column == column)
        state |= ItemPainter.ROLLOVER;
    }
    if (!hasFocus)
      state |= ItemPainter.NOT_FOCUS_OWNER;
    return state;
  }
  
  public int getColumnOrdinal(int column) {
    if (columnViews != null && columnViews.length > column) {
      int ordinal = columnViews[column].getOrdinal();
      if (ordinal >= 0)
        return ordinal;
    }
    return column;
  }
  
  private ItemPainter getPainter(int row, int column, Object data, int state) {
    ItemPainter painter = (columnViews != null && column < columnViews.length) ? columnViews[column] != null ? columnViews[column]
        .getItemPainter()
        : null
        : null;
    column = getColumnOrdinal(column);
    if (painter == null)
      painter = viewManager.getPainter(row, column, data, state);
    if (painter != null && customizeListeners != null) {
      customPainter.setPainter(painter);
      fireCustomizeItemEvent(new MatrixLocation(row, column), data, state,
          customPainter);
      return customPainter;
    }
    return painter;
  }
  
  private ItemEditor getEditor(int row, int column, Object data, int state) {
    ItemEditor editor = (columnViews != null && column < columnViews.length) ? columnViews[column]
        .getItemEditor()
        : null;
    column = getColumnOrdinal(column);
    if (editor == null)
      editor = viewManager.getEditor(row, column, data, state);
    if (editor != null && customizeListeners != null) {
      customEditor.setEditor(editor);
      fireCustomizeItemEvent(new MatrixLocation(row, column), data, state,
          customEditor);
      return customEditor;
    }
    return editor;
  }
  
  public void repaintSelection() {
    for (int i = 0; i < oldSelected.length; i++)
      repaintCell(oldSelected[i]);
  }
  
  public void repaintCell(MatrixLocation cell) {
    if (batchMode || cell == null)
      return;
    if (cell.row < getRowCount() && cell.column < getColumnCount()) {
      Rectangle rect = getCellRect(cell);
      if (rect != null)
        repaint(rect.x, rect.y, rect.width, rect.height);
    }
  }
  
  public void repaintCells() {
    if (batchMode)
      return;
    repaint(100);
  }
  
  public void repaintCells(MatrixLocation s, MatrixLocation e) {
    if (batchMode)
      return;
    Rectangle rect = getCellRangeRect(s, e);
    if (rect != null)
      repaint(rect.x, rect.y, rect.width, rect.height);
  }
  
  public void update(Graphics g) {
    paint(g);
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
    int lastX = clip.x;
    int lastY = clip.y;
    MatrixLocation hit = hitTest(clip.x, clip.y);
    if (hit != null) {
      g.setFont(getFont());
      MatrixLocation ur = hitTest(clip.x + clip.width - 1, clip.y);
      int lastColumn = ur != null ? ur.column : getColumnCount() - 1;
      MatrixLocation ll = hitTest(clip.x, clip.y + clip.height - 1);
      int lastRow = ll != null ? ll.row : getRowCount() - 1;
      Rectangle r = getCellRect(hit);
      int xStart = r.x;
      for (int row = hit.row; row <= lastRow; row++) {
        r.x = xStart;
        r.height = rowSizes.getSize(row);
        if (r.height > 0) {
          for (int column = hit.column; column <= lastColumn; column++) {
            r.width = columnSizes.getSize(column);
            if (r.width > 0) {
              Object value = model.get(row, getColumnOrdinal(column));
              if (value instanceof Variant)
                value = ((Variant) value).clone();
              int state = getState(row, column);
              ItemPainter painter = getPainter(row, column, value, state);
              ColumnView cview = columnViews != null
                  && columnViews.length > column ? columnViews[column] : null;
              if (texture != null) {
                ImageTexture.texture(texture, g, r.x, r.y, r.width, r.height);
              }
              if (painter != null) {
                g.setColor(getBackground());
                if (visibleGrid) {
                  Rectangle rect = new Rectangle(r);
                  if (vGridLines)
                    rect.width--;
                  if (hGridLines)
                    rect.height--;
                  g.clipRect(rect.x, rect.y, rect.width, rect.height);
                  painter.paint(value, g, rect, state, cview);
                } else {
                  g.clipRect(r.x, r.y, r.width, r.height);
                  painter.paint(value, g, r, state, cview);
                }
                g.setClip(clip.x, clip.y, clip.width, clip.height);
              }
              r.x += r.width;
            }
          }
          r.y += r.height;
        }
      }
      lastX = r.x;
      lastY = r.y;
      
      if (lastX < clip.x + clip.width) {
        if (texture != null)
          ImageTexture.texture(texture, g, lastX, clip.y, clip.x + clip.width
              - lastX, clip.height);
        else if (isOpaque()) {
          g.setColor(getBackground());
          g.fillRect(lastX, clip.y, clip.x + clip.width - lastX, clip.height);
        }
      }
      if (lastY < clip.y + clip.height) {
        if (texture != null)
          ImageTexture.texture(texture, g, clip.x, lastY, clip.width, clip.y
              + clip.height - lastY);
        else if (isOpaque()) {
          g.setColor(getBackground());
          g.fillRect(clip.x, lastY, clip.width, clip.y + clip.height - lastY);
        }
      }
      
      if (visibleGrid) {
        g.setColor(isEnabled() ? lineColor : lineColor.brighter());
        r = getCellRect(hit);
        r.x--;
        r.y--;
        lastX--;
        lastY--;
        Dimension d = new Dimension(r.x, r.y);
        if (vGridLines) {
          g.drawLine(d.width, d.height, d.width, lastY);
          for (int column = hit.column; column <= lastColumn; column++) {
            d.width += columnSizes.getSize(column);
            g.drawLine(d.width, d.height, d.width, lastY);
          }
        }
        if (hGridLines) {
          d.width = r.x;
          d.height = r.y;
          g.drawLine(d.width, d.height, lastX, d.height);
          for (int row = hit.row; row <= lastRow; row++) {
            d.height += rowSizes.getSize(row);
            g.drawLine(d.width, d.height, lastX, d.height);
          }
        }
      }
    } else if (texture != null)
      ImageTexture.texture(texture, g, clip.x, clip.y, clip.width, clip.height);
    else if (isOpaque()) {
      g.setColor(getBackground());
      g.fillRect(clip.x, clip.y, clip.width, clip.height);
    }
    
    // For debugging purposes only
    // draws a colored rectangle around the clip rectangle
    // with diagonal hash lines (for tracing paint calls)
    if (debugPaint) {
      GridCore.debugRect(g, clip.x, clip.y, clip.width, clip.height);
    }
  }
  
  public Dimension getPreferredSize() {
    Dimension d = new Dimension(10, 10);
    if (columnSizes != null)
      d.width = columnSizes.getSizeUpTo(getColumnCount());
    if (rowSizes != null)
      d.height = rowSizes.getSizeUpTo(getRowCount());
    return d;
  }
  
  public Dimension getMinimumSize() {
    Dimension d = new Dimension(10, 10);
    if (columnSizes != null)
      d.width = columnSizes.getSize(0);
    if (rowSizes != null)
      d.height = rowSizes.getSize(0);
    return d;
  }
  
  // returns the MatrixLocation containing mouse coordinates x, y
  public MatrixLocation hitTest(Point p) {
    return hitTest(p.x, p.y);
  }
  
  public MatrixLocation hitTest(int x, int y) {
    if (getRowCount() <= 0 || getColumnCount() <= 0)
      return null;
    
    // Find column hit
    int c = 0;
    int w = columnSizes.getSize(0);
    if (columnSizes instanceof FixedSizeVector) {
      c = x / w;
      if (c >= getColumnCount())
        c = getColumnCount() - 1;
      w = w * (c + 1);
    } else {
      while (x >= w && ++c < getColumnCount())
        w += columnSizes.getSize(c);
    }
    
    // Find row hit
    int r = 0;
    int h = rowSizes.getSize(0);
    if (rowSizes instanceof FixedSizeVector) {
      r = y / h;
      if (r >= getRowCount())
        r = getRowCount() - 1;
      h = h * (r + 1);
    } else {
      while (y >= h && ++r < getRowCount())
        h += rowSizes.getSize(r);
    }
    MatrixLocation hit = null;
    if (x >= 0 && x < w && y >= 0 && y < h)
      hit = new MatrixLocation(r, c);
    return hit;
  }
  
  /**
   * Returns the screen location of a single cell.
   */
  public Rectangle getCellRect(int row, int column) {
    return getCellRect(new MatrixLocation(row, column));
  }
  
  public Rectangle getCellRect(MatrixLocation cell) {
    Rectangle rect = new Rectangle();
    for (int c = 0; c < cell.column; c++)
      rect.x += columnSizes.getSize(c);
    for (int r = 0; r < cell.row; r++)
      rect.y += rowSizes.getSize(r);
    rect.width = columnSizes.getSize(cell.column);
    rect.height = rowSizes.getSize(cell.row);
    // Diagnostic.printlnc("GridCore.getCellRect([r"+cell.row+",c"+cell.column+
    // "]) = (x"+rect.x+",y"+rect.y+",w"+rect.width+",h"+rect.height+")");
    return rect;
  }
  
  private boolean canSet(int row, int column, boolean startingEdit) {
    return isReadOnly() ? false : writeModel.canSet(row,
        getColumnOrdinal(column), startingEdit);
  }
  
  public int getRowCount() {
    return model == null ? 0 : model.getRowCount();
  }
  
  public int getColumnCount() {
    return model == null ? 0 : model.getColumnCount();
  }
  
  public MatrixLocation getSubfocus() {
    return subfocus;
  }
  
  public void setSubfocus(MatrixLocation newSubfocus) {
    setSubfocus(newSubfocus.row, newSubfocus.column);
  }
  
  public void setSubfocus(int row, int column) {
    setSubfocus(row, column, SelectFlags.CLEAR | SelectFlags.ADD_ITEM
        | SelectFlags.RESET_ANCHOR);
  }
  
  protected void setSubfocus(int row, int column, int flags) {
    /*
     * System.err.print(++Diagnostic.count+"\tsetSubfocus(r"+row+",c"+column+") flags="
     * ); if ((flags & SelectFlags.CLEAR) != 0) System.err.print(" CLEAR"); if
     * ((flags & SelectFlags.ADD_ITEM) != 0) System.err.print(" ADD_ITEM"); if
     * ((flags & SelectFlags.TOGGLE_ITEM) != 0)
     * System.err.print(" TOGGLE_ITEM"); if ((flags & SelectFlags.ADD_RANGE) !=
     * 0) System.err.print(" ADD_RANGE"); if ((flags & SelectFlags.RESET_ANCHOR)
     * != 0) System.err.print(" RESET_ANCHOR"); System.err.println();
     */
    if (getRowCount() < 1 || getColumnCount() < 1)
      return;
    
    // allowable subfocus range: (0,0) to (maxRow + 1, maxColumn + 1)
    if (row < 0 || column < 0 || row > getRowCount()
        || column > getColumnCount()) {
      if (batchMode)
        return;
      else
        throw new IllegalArgumentException(Res._IllegalSubfocus);
    }
    
    if (editor != null) {
      if (lockSubfocus)
        return; // if editor says stay put, then stay put.
      else {
        safeEndEdit(); // try to end edit session
        if (lockSubfocus) // if it fails, drop out!
          return;
      }
    }
    
    if ((row > getRowCount() - 1)
        || (row < 0)
        || (column > getColumnCount() - 1)
        || (column < 0)
        || (subfocus != null && subfocus.row == row && subfocus.column == column)) {
      if (row == getRowCount()) {
        if (!isReadOnly() && writeModel.isVariableRows()) {
          writeModel.addRow();
        }
      } else if (column == getColumnCount()) {
        if (!isReadOnly() && writeModel.isVariableColumns()) {
          writeModel.addColumn();
        }
      } else
        return;
    }
    
    if (!preprocessSubfocusEvent(new MatrixSubfocusEvent(this,
        SubfocusEvent.SUBFOCUS_CHANGING, new MatrixLocation(row, column))))
      return;
    
    if (subfocus == null) {
      subfocus = new MatrixLocation(row, column);
      selectAnchor = new MatrixLocation(row, column);
      if ((flags & SelectFlags.ADD_ITEM) != 0)
        selection.add(subfocus);
      scrollView();
      processSubfocusEvent(new MatrixSubfocusEvent(this,
          SubfocusEvent.SUBFOCUS_CHANGED, new MatrixLocation(subfocus)));
      return;
    }
    
    if (row != subfocus.row || column != subfocus.column) {
      if (selectAnchor == null)
        selectAnchor = new MatrixLocation(row, column);
      MatrixLocation oldFocus = new MatrixLocation(subfocus);
      subfocus = new MatrixLocation(row, column);
      
      if ((flags & SelectFlags.CLEAR) != 0)
        selection.removeAll();
      if ((flags & SelectFlags.ADD_ITEM) != 0)
        selection.add(new MatrixLocation(subfocus));
      if ((flags & SelectFlags.TOGGLE_ITEM) != 0) {
        if (selection.contains(subfocus))
          selection.remove(subfocus);
        else
          selection.add(new MatrixLocation(subfocus));
      }
      if ((flags & SelectFlags.ADD_RANGE) != 0) {
        dumpingRange = true;
        selection.removeRange(selectAnchor, oldFocus);
        selection.addRange(selectAnchor, subfocus);
      }
      if ((flags & SelectFlags.RESET_ANCHOR) != 0)
        selectAnchor = new MatrixLocation(subfocus);
      
      repaintCell(oldFocus);
      scrollView();
      repaintCell(subfocus);
      
      processSubfocusEvent(new MatrixSubfocusEvent(this,
          SubfocusEvent.SUBFOCUS_CHANGED, new MatrixLocation(subfocus)));
    }
  }
  
  // moves the subfocus point down deltaRows and right deltaColumns
  protected void moveFocus(int deltaRows, int deltaColumns, int flags) {
    int rows = getRowCount() - 1;
    int cols = getColumnCount() - 1;
    int minRows = (subfocus.row + deltaRows) < rows ? subfocus.row + deltaRows
        : rows;
    int minCols = (subfocus.column + deltaColumns) < cols ? subfocus.column
        + deltaColumns : cols;
    int row = 0 > minRows ? 0 : minRows;
    int column = 0 > minCols ? 0 : minCols;
    setSubfocus(row, column, flags);
  }
  
  // keyPressed on embedded editor
  //
  public void keyPressed(KeyEvent e) {
    boolean alt = e.isAltDown();
    boolean control = e.isControlDown();
    boolean shift = e.isShiftDown();
    if (editor == null || e.isConsumed())
      return;
    switch (e.getKeyCode()) {
    case KeyEvent.VK_ENTER:
      if (!control && !alt && !shift) {
        safeEndEdit(true);
        if (!lockSubfocus) {
          e.consume();
          fireActionEvent();
          processKeyPressed(e); // pass it on
        }
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
    case KeyEvent.VK_LEFT:
    case KeyEvent.VK_RIGHT:
      if (control && !alt && !shift) {
        safeEndEdit();
        processKeyPressed(e); // pass it on
      }
      break;
    case KeyEvent.VK_TAB:
      if (!alt) {
        safeEndEdit();
        doTabKey(e);
      }
    }
  }
  
  public void keyReleased(KeyEvent e) {
  }
  
  public void keyTyped(KeyEvent e) {
  }
  
  // keyPressed on GridCore (not embedded editor)
  //
  protected void processKeyPressed(KeyEvent e) {
    int key = e.getKeyCode();
    boolean control = e.isControlDown();
    boolean shift = e.isShiftDown();
    boolean alt = e.isAltDown();
    int flags;
    
    if (shift && control)
      flags = SelectFlags.ADD_RANGE;
    else if (shift)
      flags = SelectFlags.ADD_RANGE;
    else if (control)
      flags = SelectFlags.RESET_ANCHOR;
    else
      flags = SelectFlags.CLEAR | SelectFlags.ADD_ITEM
          | SelectFlags.RESET_ANCHOR;
    
    switch (key) {
    case KeyEvent.VK_DOWN:
      if ((autoAppend || control) && subfocus.row == getRowCount() - 1
          && !isReadOnly() && writeModel.isVariableRows()) {
        writeModel.addRow();
        setSubfocus(getRowCount() - 1, subfocus.column, flags);
        e.consume();
      } else if (subfocus.row < getRowCount() - 1 && !alt) {
        moveFocus(1, 0, flags);
        e.consume();
      }
      break;
    case KeyEvent.VK_RIGHT:
      if (control && subfocus.column == getColumnCount() - 1 && !isReadOnly()
          && writeModel.isVariableColumns()) {
        writeModel.addColumn();
        setSubfocus(subfocus.row, getColumnCount() - 1, flags);
        e.consume();
      } else if (subfocus.column < getColumnCount() - 1 && !alt) {
        moveFocus(0, 1, flags);
        e.consume();
      }
      break;
    case KeyEvent.VK_UP:
      if (subfocus.row > 0 && !alt) {
        moveFocus(-1, 0, flags);
        e.consume();
      }
      break;
    case KeyEvent.VK_LEFT:
      if (subfocus.column > 0 && !alt) {
        moveFocus(0, -1, flags);
        e.consume();
      }
      break;
    case KeyEvent.VK_PAGE_UP:
      if (subfocus.row > 0 && !alt) {
        pageJump(false, flags);
        e.consume();
      }
      break;
    case KeyEvent.VK_PAGE_DOWN:
      if (subfocus.row < getRowCount() - 1 && !alt) {
        pageJump(true, flags);
        e.consume();
      }
      break;
    case KeyEvent.VK_SPACE:
      if (!alt && !shift) {
        if (control && selection.contains(subfocus))
          selection.remove(subfocus);
        else
          selection.add(subfocus);
        e.consume();
        if (isToggleItem(subfocus.row, subfocus.column))
          startEdit(subfocus);
      }
      break;
    case KeyEvent.VK_HOME:
      if (alt)
        break;
      if (control && shift) {
        if (subfocus.row != 0 || subfocus.column != 0) {
          setSubfocus(0, 0, SelectFlags.ADD_RANGE);
          e.consume();
        }
      } else if (control) {
        if (subfocus.row != 0 || subfocus.column != 0) {
          setSubfocus(0, 0, SelectFlags.CLEAR | SelectFlags.ADD_ITEM
              | SelectFlags.RESET_ANCHOR);
          e.consume();
        }
      } else if (subfocus.column != 0) {
        moveFocus(0, -subfocus.column, flags);
        e.consume();
      }
      break;
    case KeyEvent.VK_END:
      if (alt)
        break;
      if (control && shift) {
        if (subfocus.row != getRowCount() - 1
            || subfocus.column != getColumnCount() - 1) {
          setSubfocus(getRowCount() - 1, getColumnCount() - 1,
              SelectFlags.ADD_RANGE);
          e.consume();
        }
      } else if (control) {
        if (subfocus.row != getRowCount() - 1
            || subfocus.column != getColumnCount() - 1) {
          setSubfocus(getRowCount() - 1, getColumnCount() - 1,
              SelectFlags.CLEAR | SelectFlags.ADD_ITEM
                  | SelectFlags.RESET_ANCHOR);
          e.consume();
        }
      } else if (subfocus.column != getColumnCount() - 1) {
        setSubfocus(subfocus.row, getColumnCount() - 1, flags);
        e.consume();
      }
      break;
    case KeyEvent.VK_INSERT:
      if (shift && control) {
        if (!isReadOnly() && writeModel.isVariableColumns()) {
          writeModel.addColumn(getColumnOrdinal(subfocus.column));
          e.consume();
        }
      } else {
        if (!isReadOnly() && writeModel.isVariableRows()) {
          writeModel.addRow(subfocus.row);
          e.consume();
        }
      }
      break;
    case KeyEvent.VK_DELETE:
      if (shift && control) {
        if (!isReadOnly() && writeModel.isVariableColumns()
            && writeModel.getColumnCount() > 0) {
          writeModel.removeColumn(getColumnOrdinal(subfocus.column));
          e.consume();
          if (subfocus.column == getColumnCount())
            moveFocus(0, -1, SelectFlags.CLEAR | SelectFlags.ADD_ITEM
                | SelectFlags.RESET_ANCHOR);
        }
      } else if (control) {
        if (!isReadOnly() && writeModel.isVariableRows()
            && writeModel.getRowCount() > 0) {
          writeModel.removeRow(subfocus.row);
          e.consume();
          if (subfocus.row == getRowCount())
            moveFocus(-1, 0, SelectFlags.CLEAR | SelectFlags.ADD_ITEM
                | SelectFlags.RESET_ANCHOR);
        }
      }
      break;
    case KeyEvent.VK_ENTER:
      if (control && !shift && !alt && editor == null
          && canSet(subfocus.row, subfocus.column, false)) {
        startEdit(subfocus);
        e.consume();
      } else if (navigateOnEnter && subfocus.column < getColumnCount() - 1) {
        moveFocus(0, 1, SelectFlags.CLEAR | SelectFlags.ADD_ITEM
            | SelectFlags.RESET_ANCHOR);
        e.consume();
      } else if (navigateOnEnter && subfocus.row < getRowCount() - 1) {
        moveFocus(1, -subfocus.column, SelectFlags.CLEAR | SelectFlags.ADD_ITEM
            | SelectFlags.RESET_ANCHOR);
        e.consume();
      }
      break;
    case KeyEvent.VK_F2:
      if (editor == null && !isToggleItem(subfocus.row, subfocus.column)
          && canSet(subfocus.row, subfocus.column, false)) {
        startEdit(subfocus);
        e.consume();
      }
      break;
    case KeyEvent.VK_D: // duplicate item
      if (control && editor == null && subfocus.row > 0
          && canSet(subfocus.row, subfocus.column, true)) {
        writeModel.set(subfocus.row, getColumnOrdinal(subfocus.column), model
            .get(subfocus.row - 1, getColumnOrdinal(subfocus.column)));
        e.consume();
      }
      break;
    case KeyEvent.VK_J: // debug painting
      if (shift && control && alt)
        debugPaint = !debugPaint;
      // do not consume event (debugging)
      break;
    case KeyEvent.VK_KANJI:
    case 0xE5: // VK_PROCESSKEY
      // For Asian keyboards: activate the edit, so that the IME
      // entry will take place in the edit window.
      if (editor == null && canSet(subfocus.row, subfocus.column, false)) {
        startEdit(subfocus);
        e.consume();
      }
      break;
    }
  }
  
  protected void doTabKey(KeyEvent e) {
    if (!e.isShiftDown()) {
      if (subfocus.column < getColumnCount() - 1) {
        moveFocus(0, 1, SelectFlags.CLEAR | SelectFlags.ADD_ITEM
            | SelectFlags.RESET_ANCHOR);
        e.consume();
      } else if (subfocus.row < getRowCount() - 1) {
        moveFocus(1, -subfocus.column, SelectFlags.CLEAR | SelectFlags.ADD_ITEM
            | SelectFlags.RESET_ANCHOR);
        e.consume();
      } else if (subfocus.row == getRowCount() - 1
          && subfocus.column == getColumnCount() - 1) {
        setSubfocus(0, 0, SelectFlags.CLEAR | SelectFlags.ADD_ITEM
            | SelectFlags.RESET_ANCHOR);
        e.consume();
      }
    } else {
      if (subfocus.column > 0) {
        moveFocus(0, -1, SelectFlags.CLEAR | SelectFlags.ADD_ITEM
            | SelectFlags.RESET_ANCHOR);
        e.consume();
      } else if (subfocus.row > 0) {
        moveFocus(-1, getColumnCount() - 1, SelectFlags.CLEAR
            | SelectFlags.ADD_ITEM | SelectFlags.RESET_ANCHOR);
        e.consume();
      } else if (subfocus.row == 0 && subfocus.column == 0) {
        setSubfocus(getRowCount() - 1, getColumnCount() - 1, SelectFlags.CLEAR
            | SelectFlags.ADD_ITEM | SelectFlags.RESET_ANCHOR);
        e.consume();
      }
    }
  }
  
  protected void processKeyEvent(KeyEvent e) {
    if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_TAB
        && navigateOnTab && !e.isControlDown() && !e.isAltDown()
        && editor == null) {
      doTabKey(e);
      return;
    }
    super.processKeyEvent(e);
  }
  
  // keyTyped on GridCore (not embedded editor)
  // This should only be printable characters...
  // ktien: Ctrl+Alt is Alt-GR: used on European
  // keyboards for printable accented characters,
  // so the test should be Alt XOR Ctrl.
  protected void processKeyTyped(KeyEvent e) {
    char kChar = e.getKeyChar();
    if (editor != null
        || !autoEdit
        || e.isConsumed()
        || isReadOnly()
        || kChar == 0
        || kChar == '\t'
        || kChar == '\r'
        || kChar == '\n'
        || kChar == ' '
        || kChar == 27
        || // ESCAPE
        isToggleItem(subfocus.row, subfocus.column)
        || (e.isAltDown() ^ e.isControlDown())
        || !writeModel.canSet(subfocus.row, getColumnOrdinal(subfocus.column),
            false))
      return;
    startEdit(subfocus);
    Component eComp = null;
    if (editor != null && (eComp = editor.getComponent()) != null) {
      eComp.dispatchEvent(e);
    }
  }
  
  private void pageJump(boolean pageDown, int flags) {
    int jump = 10;
    if (rowSizes instanceof FixedSizeVector) {
      int count = getRowCount();
      if (count == 0)
        count++;
      int avHeight = getPreferredSize().height / count;
      if (avHeight == 0)
        avHeight++;
      jump = (scroller.getViewport().getExtentSize().height / avHeight) - 1;
    }
    jump = pageDown ? jump : -jump;
    moveFocus(jump, 0, flags);
  }
  
  // Subfocus Events
  
  protected boolean preprocessSubfocusEvent(MatrixSubfocusEvent e) {
    return subfocusMulticaster.hasListeners() ? subfocusMulticaster
        .vetoableDispatch(e) : true;
  }
  
  protected void processSubfocusEvent(MatrixSubfocusEvent e) {
    if (subfocusMulticaster.hasListeners())
      subfocusMulticaster.dispatch(e);
  }
  
  // Mouse Events
  
  protected void processMousePressed(MouseEvent e) {
    super.processMousePressed(e);
    int x = e.getX();
    int y = e.getY();
    boolean shift = e.isShiftDown();
    boolean control = e.isControlDown();
    boolean right = e.isMetaDown();
    int flags;
    
    rollover = null;
    
    if (shift && control)
      flags = SelectFlags.ADD_RANGE;
    else if (shift)
      flags = SelectFlags.CLEAR | SelectFlags.ADD_RANGE;
    else if (control)
      flags = SelectFlags.TOGGLE_ITEM | SelectFlags.RESET_ANCHOR;
    else
      flags = SelectFlags.CLEAR | SelectFlags.ADD_ITEM
          | SelectFlags.RESET_ANCHOR;
    
    MatrixLocation current = hitTest(x, y);
    mouseDown = current != null ? new MatrixLocation(current) : null;
    if (editor != null) {
      if (editorLocation.equals(current))
        return;
      else
        safeEndEdit();
    }
    resize = null;
    if (current != null) {
      if (current.equals(subfocus)) {
        if (!right && e.getClickCount() == 2)
          fireActionEvent();
        // System.err.println("hasFocus=" + hasFocus);
        if (/* hasFocus && */!right && !control && !shift
            && !isToggleItem(current.row, current.column)
            && canSet(current.row, current.column, false)) {
          doStartEdit = true;
          return;
        } else if (control && !shift) {
          if (selection.contains(current))
            selection.remove(current);
          else
            selection.add(new MatrixLocation(current));
        }
      }
      rangeSelecting = true;
      setSubfocus(current.row, current.column, flags);
    }
    if (resizingGrid && !liveResize)
      startResize(resizeColumn, resize1 = resize, x, y);
  }
  
  protected void processMouseDragged(MouseEvent e) {
    rollover = null;
    if (dragSubfocus && !e.isMetaDown() && rangeSelecting) {
      int x = e.getX();
      int y = e.getY();
      int flags = e.isControlDown() ? SelectFlags.RESET_ANCHOR
          : SelectFlags.ADD_RANGE;
      MatrixLocation current = hitTest(x, y);
      if (current != null)
        setSubfocus(current.row, current.column, flags);
    }
  }
  
  boolean doStartEdit = false;
  
  protected void processMouseReleased(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();
    boolean shift = e.isShiftDown();
    boolean control = e.isControlDown();
    boolean right = e.isMetaDown();
    MatrixLocation current = hitTest(x, y);
    if (current != null && current.equals(mouseDown)) {
      editClickPoint = new Point(x, y);
      if (!right && (doStartEdit || isToggleItem(current.row, current.column))) {
        startEdit(current);
        doStartEdit = false;
      }
      editClickPoint = null;
    }
    
    if (resizingGrid && !liveResize) {
      stopResize(resizeColumn, resize, x, y);
      resize = null;
    }
    
    // if (editorLocation != null && editor != null && editor.getComponent() !=
    // null)
    // editor.getComponent().requestFocus();
    rangeSelecting = false;
    resizingGrid = false;
    mouseDown = null;
  }
  
  protected Dimension getPreferredCellSize(MatrixLocation cell, Object data) {
    int state = getState(cell.row, cell.column);
    ColumnView cv = getColumnView(cell.column);
    ItemPainter painter = getPainter(cell.row, cell.column, data, state);
    Dimension size = new Dimension(0, 0);
    if (painter != null)
      size = painter.getPreferredSize(data, getSiteGraphics(), state, cv);
    return size;
  }
  
  protected void processMouseMoved(MouseEvent e) {
    if (showRollover) {
      MatrixLocation hit = hitTest(e.getX(), e.getY());
      if (hit != null && !hit.equals(rollover)) {
        MatrixLocation oldRollover = rollover;
        rollover = hit;
        repaintCell(oldRollover);
        repaintCell(rollover);
      }
    }
  }
  
  public JToolTip createToolTip() {
    return toolTip;
  }
  
  public String getToolTipText(MouseEvent e) {
    if (toolTip.active) {
      MatrixLocation hit = hitTest(e.getX(), e.getY());
      if (hit != null && model != null && viewManager != null) {
        Object data = model.get(hit.row, getColumnOrdinal(hit.column));
        if (data != null) {
          Rectangle r = getCellRect(hit);
          Dimension size = getPreferredCellSize(hit, data);
          Rectangle vp = scroller.getViewport().getViewRect();
          if (r != null
              && (!r.contains(r.x + size.width - 1, r.y + size.height - 1)
                  || !vp.contains(r.x, r.y) || !vp.contains(r.x + size.width
                  - 1, r.y + size.width - 1))) {
            int state = getState(hit.row, hit.column);
            toolTip.data = data;
            toolTip.painter = getPainter(hit.row, hit.column, data, state);
            toolTip.state = state;
            /*
             * if (columnViews.length > hit.column) toolTip.site =
             * columnViews[hit.column]; else toolTip.site = this;
             */
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
      MatrixLocation hit = hitTest(e.getX(), e.getY());
      if (hit != null) {
        Rectangle r = getCellRect(hit);
        if (r != null && model != null) {
          Object data = model.get(hit.row, hit.column);
          int state = getState(hit.row, hit.column);
          toolTip.data = data;
          toolTip.painter = getPainter(hit.row, hit.column, data, state);
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
      MatrixLocation oldRollover = rollover;
      rollover = null;
      repaintCell(oldRollover);
    }
  }
  
  protected void startEdit(MatrixLocation newEditorLocation) {
    // System.err.println("GridCore.startEdit(" + newEditorLocation + ")");
    if (model == null || viewManager == null || !editInPlace
        || !canSet(newEditorLocation.row, newEditorLocation.column, true)
        || batchMode)
      return;
    rollover = null;
    editorLocation = new MatrixLocation(newEditorLocation);
    selection.removeAll();
    selection.add(new MatrixLocation(editorLocation));
    Object data = model.get(editorLocation.row,
        getColumnOrdinal(editorLocation.column));
    int state = getState(editorLocation.row, editorLocation.column);
    editor = getEditor(editorLocation.row, editorLocation.column, data, state);
    if (editor != null) {
      Component editorComponent = editor.getComponent();
      if (editorComponent != null) {
        editorComponent.setVisible(false);
        add(editorComponent);
      }
      Rectangle r = getEditorRect();
      ItemEditSite site = this;
      if (columnViews != null && columnViews.length > editorLocation.column)
        site = columnViews[editorLocation.column];
      editor.addKeyListener(this);
      editor.addKeyListener(keyMulticaster);
      editor.startEdit(data, r, site);
      // in case it needs to grow!
      resyncEditor();
      // JPN: this must re-check, as the startEdit call could have terminted
      // the edit session before the next line of code executes...
      if (editor != null && editor.getComponent() != null)
        editor.getComponent().addFocusListener(this);
      editClickPoint = null;
    }
  }
  
  private boolean isToggleItem(int row, int column) {
    if (model == null || viewManager == null || !editInPlace || batchMode)
      return false;
    Object data = model.get(row, getColumnOrdinal(column));
    int state = getState(row, column);
    ItemEditor ie = getEditor(row, column, data, state);
    if (ie instanceof ToggleItemEditor) {
      Rectangle rect = getCellRect(row, column);
      ItemEditSite site = this;
      if (columnViews != null && column < columnViews.length)
        site = columnViews[column];
      return ((ToggleItemEditor) ie).isToggle(data, rect, site)
          && canSet(row, column, false);
    } else
      return false;
  }
  
  private boolean lockSubfocus = false;
  
  public void endEdit() throws Exception {
    endEdit(postOnEndEdit);
  }
  
  public void endEdit(boolean post) throws Exception {
    ItemEditor editor = this.editor; // keep in local in case of reentrancy
    this.editor = null;
    if (editor != null) {
      Component editorComponent = editor.getComponent();
      if (post) {
        try {
          boolean okToEnd = editor.canPost();
          if (!okToEnd) {
            throw new IllegalStateException(Res._EditorCannotPost);
          }
          if (post && okToEnd
              && canSet(editorLocation.row, editorLocation.column, true)) {
            writeModel.set(editorLocation.row,
                getColumnOrdinal(editorLocation.column), editor.getValue());
          }
          editor.endEdit(post);
        } catch (Exception x) {
          lockSubfocus = true;
          this.editor = editor;
          throw x;
        }
      }
      editor.removeKeyListener(this);
      editor.removeKeyListener(keyMulticaster);
      if (editorComponent != null) {
        remove(editorComponent);
        editorComponent.removeFocusListener(this);
      }
      lockSubfocus = false;
      repaintCell(editorLocation);
      editorLocation = null;
      editClickPoint = null;
      requestFocus();
    }
  }
  
  public void safeEndEdit() {
    safeEndEdit(postOnEndEdit);
  }
  
  public void safeEndEdit(boolean post) {
    try {
      endEdit(post);
    } catch (Exception x) {
    }
  }
  
  // Returns the clip rectangle for the range (start-end) of cells
  public Rectangle getCellRangeRect(MatrixLocation start, MatrixLocation end) {
    MatrixLocation s = new MatrixLocation(start.row < end.row ? start.row
        : end.row, start.column < end.column ? start.column : end.column);
    MatrixLocation e = new MatrixLocation(start.row > end.row ? start.row
        : end.row, start.column > end.column ? start.column : end.column);
    if (s.row <= e.row && s.column <= e.column) {
      Rectangle sr = getCellRect(s);
      if (sr != null) {
        Rectangle er = getCellRect(e);
        if (er != null)
          return sr.union(er);
        else {
          Dimension sz = getSize();
          Rectangle top = getCellRect(new MatrixLocation(0, e.column));
          int xmax = top != null ? top.x + top.width - 1 : sz.width - 1;
          Rectangle left = getCellRect(new MatrixLocation(e.row, 0));
          int ymax = left != null ? left.y + left.height - 1 : sz.height - 1;
          sr.add(xmax, ymax);
          return sr;
        }
      }
    }
    return null;
  }
  
  protected Rectangle getEditorRect() {
    Rectangle rect = null;
    if (editorLocation != null && editor != null) {
      rect = getCellRect(editorLocation);
      if (rect != null) {
        if (visibleGrid) {
          rect.x--;
          rect.width++;
          rect.y--;
          rect.height++;
          // System.err.println("visible grid cell rect=" + r);
        }
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
  
  public void doLayout() {
    resyncEditor();
  }
  
  protected void resyncEditor() {
    if (editorLocation != null && editor != null) {
      Rectangle er = getEditorRect();
      editor.changeBounds(er != null ? er : new Rectangle());
    }
  }
  
  public void reset() {
    safeEndEdit(false);
    resize = null;
    setSubfocus(0, 0, SelectFlags.CLEAR | SelectFlags.ADD_ITEM
        | SelectFlags.RESET_ANCHOR);
    scroller.getViewport().setViewPosition(new Point());
    repaintCells();
  }
  
  public boolean startResize(boolean column, int index, int mouseX, int mouseY) {
    resize1 = column ? new MatrixLocation(0, index) : new MatrixLocation(index,
        0);
    return startResize(column, resize1, mouseX, mouseY);
  }
  
  boolean startResize(boolean column, MatrixLocation location, int mouseX,
      int mouseY) {
    if (editorLocation != null)
      safeEndEdit();
    // setCursor(column ? CURSOR_SIZE_H : CURSOR_SIZE_V);
    Rectangle outerRect = new Rectangle(0, 0, getSize().width, getSize().height);
    if (column) {
      Rectangle r = getCellRect(location);
      dividerDelta = mouseX - (r.x + r.width);
      // divider.setBounds(r.x + r.width, 0, dividerWidth, outerRect.height);
      // divider.setVisible(true);
    } else {
      Rectangle r = getCellRect(location);
      dividerDelta = mouseY - (r.y + r.height);
      // divider.setBounds(0, r.y + r.height, outerRect.width, dividerHeight);
      // divider.setVisible(true);
    }
    // setCursor(column ? CURSOR_SIZE_H : CURSOR_SIZE_V);
    resizingGrid = true;
    return true;
  }
  
  public boolean stopResize(boolean column, int index, int mouseX, int mouseY) {
    boolean retVal = stopResize(column, resize1, mouseX, mouseY);
    resize1 = null;
    invalidate();
    repaintCells();
    if (isShowing() && !batchMode) {
      Point vp = scroller.getViewport().getViewPosition();
      scroller.validate();
      scroller.getViewport().setViewPosition(vp);
    }
    return retVal;
  }
  
  public boolean stopResize(boolean column, MatrixLocation location,
      int mouseX, int mouseY) {
    // divider.setVisible(false);
    // setCursor(CURSOR_DEFAULT);
    if (location != null) {
      Rectangle r = getCellRect(location);
      if (column) {
        if (mouseX - dividerDelta - r.x > 1) {
          columnSizes.setSize(location.column, mouseX - dividerDelta - r.x);
          if (columnViews != null && columnViews.length > location.column)
            columnViews[location.column].setWidth(columnSizes
                .getSize(location.column));
        }
      } else {
        if (mouseY - dividerDelta - r.y > 1)
          rowSizes.setSize(location.row, mouseY - dividerDelta - r.y);
      }
    }
    resizingGrid = false;
    return true;
  }
  
  public boolean whileResize(boolean column, int index, int mouseX, int mouseY) {
    if (column) {
      // divider.setLocation(mouseX - dividerDelta, 0);
      // setCursor(CURSOR_SIZE_H);
    } else {
      // divider.setLocation(0, mouseY - dividerDelta);
      // setCursor(CURSOR_SIZE_V);
    }
    invalidate();
    Point vp = scroller.getViewport().getViewPosition();
    scroller.validate();
    scroller.getViewport().setViewPosition(vp);
    repaintCells();
    return true;
  }
  
  public void startMove(boolean column, int index, int mouseX, int mouseY) {
    if (column) {
      moveIndex = index;
      movingLocation = hitTest(mouseX, 0);
      resize1 = movingLocation;
      Rectangle r = getCellRect(0, index);
      // dividerDelta = mouseX - (r.x + r.width);
      Rectangle vRect = scroller.getViewport().getViewRect();
      divider.setBounds(r.x - dividerWidth / 2, vRect.y, dividerWidth,
          vRect.height);
      // setCursor(CURSOR_SIZE_H);
    } else {
      // no move support on rows
    }
  }
  
  public void whileMove(boolean column, int index, int mouseX, int mouseY) {
    divider.setVisible(true);
    // setCursor(CURSOR_SIZE_H);
    if (column) {
      MatrixLocation hit = hitTest(mouseX, 0);
      if (hit != resize1 && hit != null) {
        Rectangle r = getCellRect(hit);
        // dividerDelta = mouseX - (r.x + r.width);
        Rectangle vRect = scroller.getViewport().getViewRect();
        if ((mouseX - r.x) > r.width / 2)
          divider.setLocation(r.x + r.width - dividerWidth / 2, vRect.y);
        else
          divider.setLocation(r.x - dividerWidth / 2, vRect.y);
        resize1 = hit;
      } else if (hit == null) {
        int lastCol = getColumnCount() - 1;
        if (mouseX >= columnSizes.getSizeUpTo(lastCol)) {
          Rectangle r = getCellRect(0, lastCol);
          // dividerDelta = mouseX - (r.x + r.width);
          divider.setLocation(r.x + r.width - dividerWidth / 2, scroller
              .getViewport().getViewPosition().y);
          resize1 = hit;
        }
      }
    } else {
      // no move support on rows
    }
  }
  
  // index = old ColumnView index
  // target = left of new ColumnView index (without having removed old one)
  private void moveColumnView(int index, int target) {
    if (index >= columnViews.length || target > columnViews.length) {
      return;
    }
    // make sure the ordinals are set on the columns
    for (int i = 0; i < columnViews.length; i++)
      columnViews[i].setOrdinal(getColumnOrdinal(i));
    // calculate the new index location
    int newIndex = index > target ? target : target - 1;
    // store off the moved column
    ColumnView indexView = columnViews[index];
    int indexSize = columnSizes.getSize(index);
    // move left
    if (newIndex < index) {
      for (int i = index; i > newIndex; i--) {
        columnViews[i] = columnViews[i - 1];
        columnSizes.setSize(i, columnSizes.getSize(i - 1));
      }
    }
    // move right
    else {
      for (int i = index; i < newIndex; i++) {
        columnViews[i] = columnViews[i + 1];
        columnSizes.setSize(i, columnSizes.getSize(i + 1));
      }
    }
    // update the moved column
    columnViews[newIndex] = indexView;
    columnSizes.setSize(newIndex, indexSize);
  }
  
  public void stopMove(boolean column, int index, int mouseX, int mouseY) {
    // setCursor(CURSOR_DEFAULT);
    divider.setVisible(false);
    if (column) {
      MatrixLocation hit = hitTest(mouseX, 0);
      int target = -1;
      if (hit != null) {
        if (hit.column != movingLocation.column) {
          Rectangle r = getCellRect(hit);
          target = hit.column;
          if ((mouseX - r.x) > r.width / 2)
            target++;
        }
      } else {
        if (mouseX >= columnSizes.getSizeUpTo(columnViews.length - 1))
          target = columnViews.length;
        else if (mouseX < 0)
          target = 0;
      }
      if (target != moveIndex && (target - 1) != moveIndex && target != -1) {
        moveColumnView(moveIndex, target);
        repaintCells();
      }
    } else {
      // For now: no move support on rows
    }
    movingLocation = null;
  }
  
  public void checkParentWindow() {
    findParentWindow();
  }
  
  private void fireActionEvent() {
    Object item = model != null ? model.get(subfocus.row,
        getColumnOrdinal(subfocus.column)) : null;
    String action = item != null ? item.toString() : "";
    processActionEvent(new ActionEvent(actionSource,
        ActionEvent.ACTION_PERFORMED, action));
  }
  
  private void resetColumnSizes() {
    for (int i = 0; i < columnViews.length; i++) {
      int width = columnViews[i].getWidth();
      if (width == 0)
        width = defaultColWidth;
      columnSizes.setSize(i, width > MIN_CELL_SIZE ? width : MIN_CELL_SIZE);
      columnViews[i].setWidth(columnSizes.getSize(i));
    }
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
    Hashtable<String, Object> hash = new Hashtable<String, Object>(8);
    if (model instanceof Serializable)
      hash.put("mo", model);
    if (viewManager instanceof Serializable)
      hash.put("vm", viewManager);
    if (selection instanceof Serializable)
      hash.put("se", selection);
    if (columnSizes instanceof Serializable)
      hash.put("cs", columnSizes);
    if (rowSizes instanceof Serializable)
      hash.put("rs", rowSizes);
    if (actionSource instanceof Serializable)
      hash.put("as", actionSource);
    s.writeObject(hash);
  }
  
  private void readObject(ObjectInputStream s) throws IOException,
      ClassNotFoundException {
    s.defaultReadObject();
    Hashtable<?, ?> hash = (Hashtable<?, ?>) s.readObject();
    // model / writeModel
    Object data = hash.get("mo");
    if (data != null)
      model = (MatrixModel) data;
    if (model instanceof WritableMatrixModel)
      writeModel = (WritableMatrixModel) model;
    // viewManager
    data = hash.get("vm");
    if (data instanceof MatrixViewManager)
      viewManager = (MatrixViewManager) data;
    // selection
    data = hash.get("se");
    if (data instanceof WritableMatrixSelection)
      selection = (WritableMatrixSelection) data;
    // columnSizes
    data = hash.get("cs");
    if (data instanceof SizeVector)
      columnSizes = (SizeVector) data;
    // rowSizes
    data = hash.get("rs");
    if (data instanceof SizeVector)
      rowSizes = (SizeVector) data;
    // actionSource
    actionSource = hash.get("as");
  }
  
  /**
   * Paints a color-cycled hashmarked rectangle in the passed bounds. Used for
   * debugging paint messages.
   */
  
  public static void debugRect(Graphics g, int x, int y, int width, int height) {
    if (g == null)
      return;
    Rectangle clip = g.getClipBounds();
    if (clip == null)
      return;
    g.setClip(x, y, width, height);
    Color c = g.getColor();
    g.setColor(colorWheel.next());
    g.drawRect(x, y, width - 1, height - 1);
    g.drawRect(x + 1, y + 1, width - 3, height - 3);
    // alternate hash directions
    if (debugRectHashLeft) {
      for (int i = 0; i < x + width + height; i += debugRectInc)
        g.drawLine(x, y + i, x + i, y);
    } else {
      for (int i = 0; i < x + width + height; i += debugRectInc)
        g.drawLine(x + width, y + i, x + width - i, y);
    }
    debugRectHashLeft = !debugRectHashLeft;
    if (debugRectInc > 15)
      debugRectInc = 5;
    else
      debugRectInc += 2;
    g.setColor(c);
    if (clip != null)
      g.setClip(clip.x, clip.y, clip.width, clip.height);
  }
  
  // private members
  
  private static ColorWheel colorWheel = new ColorWheel(Color.red,
      ColorWheel.RED_TO_YELLOW, 100);
  private static int debugRectInc = 10;
  private static boolean debugRectHashLeft = true;
  
  // Objects that need custom serialization
  
  private transient MatrixModel model;
  private transient WritableMatrixModel writeModel; // JPN : do not serialize
                                                    // this object (model is
                                                    // same)
  private transient MatrixViewManager viewManager;
  private transient WritableMatrixSelection selection = new NullMatrixSelection();
  private transient SizeVector columnSizes = new VariableSizeVector();
  private transient SizeVector rowSizes = new VariableSizeVector();
  private transient JScrollPane scroller;
  private transient Object actionSource;
  
  // Serializable data
  
  private ColumnView[] columnViews;
  private MatrixLocation[] oldSelected = new MatrixLocation[0];
  private boolean readOnly = false;
  private boolean liveResize = false;
  private boolean visibleGrid = true;
  private boolean hGridLines = true;
  private boolean vGridLines = true;
  private boolean postOnEndEdit = true;
  private boolean hasFocus = false;
  private boolean showFocus = true;
  private GridCore_Divider divider = new GridCore_Divider();
  private int dividerWidth = 4;
  private int dividerDelta;
  private Point editClickPoint;
  private ItemEditor editor;
  private MatrixLocation subfocus = new MatrixLocation(0, 0);
  private MatrixLocation editorLocation;
  private boolean growEditor = true;
  private MatrixLocation selectAnchor = new MatrixLocation(0, 0);
  private MatrixLocation resize;
  private MatrixLocation resize1;
  private MatrixLocation movingLocation;
  private MatrixLocation rollover;
  private MatrixLocation mouseDown;
  private int moveIndex;
  private boolean rangeSelecting;
  private boolean resizingGrid;
  private boolean resizeColumn;
  private boolean dumpingRange = false; // internal flag to only paint once on a
                                        // range select
  private boolean snapOrigin = true;
  private Insets margins = new Insets(2, 2, 2, 2);
  private int defaultColWidth = 100;
  private Color lineColor = SystemColor.control;
  private boolean editInPlace = true;
  private boolean autoEdit = true;
  private boolean autoAppend = false;
  private boolean navigateOnEnter = true;
  private boolean navigateOnTab = true;
  private boolean dragSubfocus = true;
  private boolean debugPaint = false;
  private boolean batchMode = false;
  private boolean showRollover = false;
  private DataToolTip toolTip = new DataToolTip(this);
  private boolean useBackingStore = false;
  
  public static final int MIN_CELL_SIZE = 4;
  
  private transient CustomItemPainter customPainter = new CustomItemPainter();
  private transient CustomItemEditor customEditor = new CustomItemEditor();
  private transient Vector<CustomItemListener> customizeListeners;
  
  // Event Multicaster classes
  
  private transient KeyMulticaster keyMulticaster = new KeyMulticaster();
  private transient MatrixModelMulticaster modelMulticaster = new MatrixModelMulticaster();
  private transient MatrixSelectionMulticaster selectionMulticaster = new MatrixSelectionMulticaster();
  private transient EventMulticaster subfocusMulticaster = new EventMulticaster();
  
  // Static data
  
  private static int TRACE_MOUSE = 23;
  
  private static Cursor CURSOR_DEFAULT = Cursor.getDefaultCursor();
  private static Cursor CURSOR_MOVE = new Cursor(Cursor.MOVE_CURSOR);
  private static Cursor CURSOR_SIZE_V = new Cursor(Cursor.N_RESIZE_CURSOR);
  private static Cursor CURSOR_SIZE_H = new Cursor(Cursor.W_RESIZE_CURSOR);
  // private static Cursor CURSOR_EDIT = new Cursor(Cursor.TEXT_CURSOR);
}

@SuppressWarnings("serial")
class GridCore_Divider extends Canvas implements Serializable {
  public GridCore_Divider() {
    setBackground(SystemColor.controlShadow);
  }
}
