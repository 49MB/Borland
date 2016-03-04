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
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.borland.jbcl.model.BasicMatrixContainer;
import com.borland.jbcl.model.BasicViewManager;
import com.borland.jbcl.model.ItemEditor;
import com.borland.jbcl.model.MatrixLocation;
import com.borland.jbcl.model.MatrixModel;
import com.borland.jbcl.model.MatrixModelListener;
import com.borland.jbcl.model.MatrixSelectionListener;
import com.borland.jbcl.model.MatrixSubfocusListener;
import com.borland.jbcl.model.MatrixViewManager;
import com.borland.jbcl.model.SingleMatrixSelection;
import com.borland.jbcl.model.WritableMatrixModel;
import com.borland.jbcl.model.WritableMatrixSelection;
import com.borland.jbcl.util.ImageLoader;
import com.borland.jbcl.util.Orientation;

/**
 * GridView is a scrollable view of matrix data with a row and column header.  It is
 * composed of a Panel that contains a GridCore, two HeaderViews (row and column),
 * two Scrollbars, a ButtonControl (top-left between the headers), and another Panel (paints
 * gap on lower right between scrollbars).  This class delegates all the necessary methods
 * to the non-public GridCore class, the Scrollbars, and the two public HeaderView classes
 * for external users and its sub-classes.
 */
public class GridView
     extends JScrollPane
  implements HeaderListener, MatrixView, Serializable
{
  public GridView() {
    super();
    init();
  }

  protected void init() {
    setDoubleBuffered(true);
    setBorder(UIManager.getBorder("Table.scrollPaneBorder")); 

    setColumnSizes(createDefaultColumnSizes());
    columnHeader = createDefaultColumnHeaderView();
    columnHeader.addHeaderListener(this);
    if (columnHeaderVisible)
      setColumnHeaderView(columnHeader);

    setRowSizes(createDefaultRowSizes());
    rowHeader = createDefaultRowHeaderView();
    rowHeader.addHeaderListener(this);
    if (rowHeaderVisible)
      setRowHeaderView(rowHeader);
    if (columnHeaderVisible && rowHeaderVisible)
      setCorner(UPPER_LEFT_CORNER, butUL);

    core.setActionSource(this);
    core.addActionListener(new GridView_ColumnViewActionAdapter(core, columnHeader));
    getViewport().setView(core);

    butUL.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setSubfocus(0, 0);
      }
    });
    butUR.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        MatrixModel m = getModel();
        if (m != null)
          setSubfocus(0, m.getColumnCount() - 1);
      }
    });
    butLL.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        MatrixModel m = getModel();
        if (m != null)
          setSubfocus(m.getRowCount() - 1, 0);
      }
    });
    butLR.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        MatrixModel m = getModel();
        if (m != null)
          setSubfocus(m.getRowCount() - 1, m.getColumnCount() - 1);
      }
    });
    setCorner(UPPER_LEFT_CORNER,  butUL);
    setCorner(UPPER_RIGHT_CORNER, butUR);
    setCorner(LOWER_LEFT_CORNER,  butLL);
    setCorner(LOWER_RIGHT_CORNER, butLR);
    setModel(createDefaultModel());
    setViewManager(createDefaultViewManager());
    setSelection(createDefaultSelection());
    // for JDK 1.3, remove JScrollPane's default key action map
    // so GridCore can process its own keystrokes
    if (BeanPanel.is1dot3) {
      SwingUtilities.replaceUIActionMap(this, null);
    }
  }

  public void updateUI() {
    super.updateUI();
    super.setBorder(UIManager.getBorder("Table.scrollPaneBorder")); 
  }

  protected MatrixModel createDefaultModel() {
    return new BasicMatrixContainer();
  }

  protected MatrixViewManager createDefaultViewManager() {
    return new BasicViewManager(
      new FocusableItemPainter(new SelectableItemPainter(new TextItemPainter())),
      new TextItemEditor());
  }

  protected WritableMatrixSelection createDefaultSelection() {
    return new SingleMatrixSelection();
  }

  protected HeaderView createDefaultColumnHeaderView() {
    HeaderView hv = new HeaderView(Orientation.HORIZONTAL);
    DefaultColumnHeaderManager manager = new DefaultColumnHeaderManager(this);
    hv.setModel(manager);
    hv.setViewManager(manager);
    hv.setItemSizes(getColumnSizes());
    hv.setThickness(20);
    return hv;
  }

  protected HeaderView createDefaultRowHeaderView() {
    HeaderView hv = new HeaderView(Orientation.VERTICAL);
    DefaultRowHeaderManager manager = new DefaultRowHeaderManager(this);
    hv.setModel(manager);
    hv.setViewManager(manager);
    hv.setItemSizes(getRowSizes());
    return hv;
  }

  protected SizeVector createDefaultColumnSizes() {
    return new VariableSizeVector();
  }

  protected SizeVector createDefaultRowSizes() {
    return new FixedSizeVector(20);
  }

  // Properties

  /**
   * Returns the contained GridCore (non-public) class instance as a Component.
   */
  public Component getCoreComponent() { return core; }

  /**
   * Returns the contained HeaderView class instance.
   */
  public HeaderView getColumnHeaderView() { return columnHeader; }

  /**
   * Returns the contained HeaderView class instance.
   */
  public HeaderView getRowHeaderView() { return rowHeader; }

  /**
   * The model property defines the MatrixModel that this grid is displaying data from.  If the
   * current model is an instance of WritableMatrixModel, an external user can get access to it
   * using getWriteModel().
   */
  public void setModel(MatrixModel model) {
    invalidate();
    core.setModel(model);
  }
  public MatrixModel getModel() { return core.getModel(); }
  public WritableMatrixModel getWriteModel() { return core.getWriteModel(); }

  /**
   * The viewManager property defines the MatrixViewManager that will 'broker' ItemPainters and
   * ItemEditors to this grid.  If a ColumnView has a defined ItemPainter and/or ItemEditor for
   * a particular column, those will be used.  If not (typical), the ItemPainters and ItemViewers
   * are obtained from this viewManager.  The viewManager is esentially a back-stop for the grid.
   */
  public void setViewManager(MatrixViewManager viewManager) {
    invalidate();
    core.setViewManager(viewManager);
  }
  public MatrixViewManager getViewManager() { return core.getViewManager(); }

  /**
   * The readOnly property is used when the model property is actually a WritableMatrixModel, but
   * the user wishes it to be treated as a read-only model.  This is commonly used to allow users
   * to browse, but not edit normally writable matricies of data.
   */
  public void setReadOnly(boolean ro) { core.setReadOnly(ro); }
  public boolean isReadOnly() { return core.isReadOnly(); }

  /**
   * The selection property defines a WritableMatrixSelection manager.  This allows multiple
   * matrix viewers to share the same selection pool - and for the grid to handle selection in
   * a generic manner.  This allows users to plug in their own implemention of a selection
   * manager to have custom selection behavior.
   */
  public void setSelection(WritableMatrixSelection wms) { core.setSelection(wms); }
  public WritableMatrixSelection getSelection() { return core.getSelection(); }

  /**
   * The subfocus property defines the 'current' cell (defined by a MatrixLocation) in the grid.
   * This is the cell that is receiving keyboard input.  The subfocus can be set using a MatrixLocation,
   * or by specifying a row and column pair.
   */
  public void setSubfocus(MatrixLocation newSubfocus) { core.setSubfocus(newSubfocus); }
  public MatrixLocation getSubfocus() { return core.getSubfocus(); }
  public void setSubfocus(int row, int column) { core.setSubfocus(row, column); }

  /**
   * The dragFocus property enables/disables dragging of the subfocus cell when dragging the mouse
   * pointer over the grid (with the button depressed).  By default this property is true.
   */
  public void setDragSubfocus(boolean dragSubfocus) { core.setDragSubfocus(dragSubfocus); }
  public boolean isDragSubfocus() { return core.isDragSubfocus(); }

  /**
   * The postOnEndEdit property controls wether or not the grid will post changes to a cell back
   * to the model when the user clicks or tabs off of the cell.  If false, editing a cell's value
   * will not 'stick' unless committed by hitting ENTER - clicking off the cell being edited will
   * revert the value back to its original state.  By default, this property is set to true.
   */
  public void setPostOnEndEdit(boolean post) { core.setPostOnEndEdit(post); }
  public boolean isPostOnEndEdit() { return core.isPostOnEndEdit(); }

  /**
   * The editInPlace property enables/disables cell editing in the grid.  By default, this
   * property is true, and a user can edit values in any cell on the grid.  If set to false,
   * the matrix data cannot by modified by the user - except through programmatic access to
   * the grid's model.
   */
  public void setEditInPlace(boolean editInPlace) { core.setEditInPlace(editInPlace); }
  public boolean isEditInPlace() { return core.isEditInPlace(); }

  /**
   * The autoEdit property enables/disables automatic cell editing in the grid.  By default, this
   * property is true, and a user can edit values in any cell on the grid by typing a character on
   * the keyboard.  If set to false, the user must hit F2, Ctrl+Enter, or double click to start an
   * edit session.
   */
  public void setAutoEdit(boolean autoEdit) { core.setAutoEdit(autoEdit); }
  public boolean isAutoEdit() { return core.isAutoEdit(); }

  /**
   * The growEditor property enables/disables automatic sizing of a cell's ItemEditor.  In some
   * look & feel settings, the ItemEditors will need to grow vertically in order to property edit
   * the data.  By default, this property is set to true.
   */
  public void setGrowEditor(boolean growEditor) { core.setGrowEditor(growEditor); }
  public boolean isGrowEditor() { return core.isGrowEditor(); }

  /**
   * The autoAppend property enables/disables automatic row appending at the end of the grid.  By
   * default, this property is false, and a user must insert rows with the Insert key or by navigating
   * to the end of the grid and pressing Ctrl+Down.  Setting this property to true allows the user
   * to append new rows by navigating past the last row.
   */
  public void setAutoAppend(boolean autoAppend) { core.setAutoAppend(autoAppend); }
  public boolean isAutoAppend() { return core.isAutoAppend(); }

  /**
   * The navigateOnEnter property enables/disables automatic navigation when hitting the ENTER key.
   * By default, this property is true, hitting ENTER will navigate the grid to the next column (or
   * first column in the next row).  If false, the user must use the arrow keys (or mouse) to navigate.
   */
  public void setNavigateOnEnter(boolean navigateOnEnter) { core.setNavigateOnEnter(navigateOnEnter); }
  public boolean isNavigateOnEnter() { return core.isNavigateOnEnter(); }

  /**
   * The navigateOnTab property enables/disables automatic navigation within the grid when hitting
   * the TAB key.  By default, this property is true, hitting TAB will navigate the grid to the next
   * column (or first column in the next row).  If false, the user must use the arrow keys (or mouse)
   * to navigate.
   */
  public void setNavigateOnTab(boolean navigateOnTab) { core.setNavigateOnTab(navigateOnTab); }
  public boolean isNavigateOnTab() { return core.isNavigateOnTab(); }

  /**
   * The editing property (read only) returns true if a cell is currently being edited in the grid.
   */
  public boolean isEditing() { return core.isEditing(); }

  /**
   * If 'editor' property (read-only) returns the current ItemEditor being used in the grid - or null
   * if the grid is not currently editing.
   */
  public ItemEditor getEditor() { return core.getEditor(); }

  /**
   * The batchMode property enables/disables all painting in the grid.  This is used for programmatic
   * mass updates to the grid's model, selection, or whatever - without triggering repaint messages.
   */
  public void setBatchMode(boolean batchMode) {
    core.setBatchMode(batchMode);
    columnHeader.setBatchMode(batchMode);
    rowHeader.setBatchMode(batchMode);
  }
  public boolean isBatchMode() { return core.isBatchMode(); }

  /**
   * The resizableColumns property controls wether or not the grid columns are resizable
   * by the user.  The default is true, and the columns can be resized using the mouse.
   */
  public void setResizableColumns(boolean resizable) { columnHeader.setResizable(resizable); }
  public boolean isResizableColumns() { return columnHeader.isResizable(); }

  /**
   * The resizableRows property controls wether or not the grid rows are resizable
   * by the user.  The default is true, and the rows can be resized using the mouse.
   */
  public void setResizableRows(boolean resizable) { rowHeader.setResizable(resizable); }
  public boolean isResizableRows() { return rowHeader.isResizable(); }

  /**
   * The moveableColumns property controls wether or not the grid columns can be re-arranged
   * by the user.  The default is true, and the columns can be moved with the mouse.
   */
  public void setMoveableColumns(boolean moveable) { columnHeader.setMoveable(moveable); }
  public boolean isMoveableColumns() { return columnHeader.isMoveable(); }

  /**
   * Individual rowHeights can be modified using the rowHeight (indexed) property.  Row
   * heights are controlled by a SizeVector, which, by default, is a FixedSizeVector.  A
   * FixedSizeVector will set ALL the row heights to the setting that is passed regardless
   * of the row index passed.  See the rowSizes property to plug in a VariableSizeVector if
   * you wish to modify individual row heights.
   */
  public void setRowHeight(int row, int height) {
    if (height > 0) {
      invalidate();
      if (rowHeader != null)
        rowHeader.getItemSizes().setSize(row, height);
      core.getRowSizes().setSize(row, height);
    }
  }
  public int getRowHeight(int row) { return core.getRowSizes().getSize(row); }

  /**
   * The rowSizes property defines a SizeVector to manage the row heights in the grid.
   * SizeVector is an interface that has two 'common' implementations: FixedSizeVector,
   * and VariableSizeVector.  FixedSizeVector (the default for rowSizes) keeps all the
   * row sizes identical.  VariableSizeVector allows a different size for each row.
   */
  public void setRowSizes(SizeVector newSizes) {
    invalidate();
    if (rowHeader != null)
      rowHeader.setItemSizes(newSizes);
    core.setRowSizes(newSizes);
  }
  public SizeVector getRowSizes() { return core.getRowSizes(); }

  /**
   * Individual columnWidths can be modified using the columnWidth (indexed) property.
   * Column widths are controlled by a SizeVector, which, by default, is a VariableSizeVector.
   * If you wish the columns to all match widths, see the columnSizes property to plug in a
   * FixedSizeVector.
   */
  public void setColumnWidth(int column, int width) {
    if (width > 0) {
      invalidate();
      if (columnHeader != null)
        columnHeader.getItemSizes().setSize(column, width);
      core.getColumnSizes().setSize(column, width);
    }
  }
  public int getColumnWidth(int row) { return core.getColumnSizes().getSize(row); }

  /**
   * The columnSizes property defines a SizeVector to manage the column widths in the grid.
   * SizeVector is an interface that has two 'common' implementations: FixedSizeVector,
   * and VariableSizeVector.  FixedSizeVector keeps all the column widths identical.  A
   * VariableSizeVector (the default for columnSizes) allows a different size for each
   * column.
   */
  public void setColumnSizes(SizeVector newSizes) {
    invalidate();
    if (columnHeader != null)
      columnHeader.setItemSizes(newSizes);
    core.setColumnSizes(newSizes);
  }
  public SizeVector getColumnSizes() { return core.getColumnSizes(); }

  /**
   * The defaultColumnWidth property is the columnWidth used when a new column is inserted
   * into the grid.  The default is 100 pixels.
   */
  public void setDefaultColumnWidth(int defaultWidth) { core.setDefaultColumnWidth(defaultWidth); }
  public int getDefaultColumnWidth() { return core.getDefaultColumnWidth(); }

  /**
   * The gridVisible property shows/hides the grid lines.  The default is true.
   */
  public void setGridVisible(boolean visible) { core.setGridVisible(visible); }
  public boolean isGridVisible() { return core.isGridVisible(); }

  /**
   * The horizontalLines property shows/hides the horizontal grid lines.  The default is true.
   */
  public boolean isHorizontalLines() { return core.isHorizontalLines(); }
  public void setHorizontalLines(boolean visible) { core.setHorizontalLines(visible); }

  /**
   * The verticalLines property shows/hides the vertical grid lines.  The default is true.
   */
  public boolean isVerticalLines() { return core.isVerticalLines(); }
  public void setVerticalLines(boolean visible) { core.setVerticalLines(visible); }

  /**
   * The gridLineColor property defines the line color for the grid.  The default color
   * for the grid lines is SystemColor.control.
   */
  public void setGridLineColor(Color gridLineColor) { core.setGridLineColor(gridLineColor); }
  public Color getGridLineColor() { return core.getGridLineColor(); }

  /**
   * The showFocus property enables/disables the painting of the focus rectangle on the
   * current subfocus cell.  In reality, the showFocus property turns on/off the FOCUSED
   * bit in the state information that is passed to the ItemPainter when a cell is painted.
   * If an ItemPainter plugged into the grid ignores the FOCUSED bit, this property will
   * have no effect.  By default, showFocus is true.
   */
  public void setShowFocus(boolean visible) { core.setShowFocus(visible); }
  public boolean isShowFocus() { return core.isShowFocus(); }

  /**
   * The showRollover property enables/disables the repainting of the rollover cell.  The
   * rollover cell is the cell that currently has the mouse floating over it.
   * If an ItemPainter plugged into the grid ignores the ROLLOVER bit, this property will
   * have no effect.  By default, showRollover is false.
   */
  public void setShowRollover(boolean showRollover) {
//    rowHeader.setShowRollover(showRollover);
//    columnHeader.setShowRollover(showRollover);
    core.setShowRollover(showRollover);
  }
  public boolean isShowRollover() { return core.isShowRollover(); }

  /**
   * The dataToolTip property enables/disables the automatic tooltip mechanism
   * to display the contents of the model (as text) in a tooltip window when
   * the mouse is floating over a cell.  By default, this property is false.
   * If set to true, the text stored in the toolTipText property is discarded.
   */
  public void setDataToolTip(boolean dataToolTip) { core.setDataToolTip(dataToolTip); }
  public boolean isDataToolTip() { return core.isDataToolTip(); }

  public void setToolTipText(String text) { core.setToolTipText(text); }
  public String getToolTipText() { return core.getToolTipText(); }

  /**
   * The rowHeaderWidth property defines the width of the row header in pixels.
   */
  public void setRowHeaderWidth(int width) {
    rowHeaderWidth = width;
    invalidate();
    if (rowHeader != null)
      rowHeader.setThickness(rowHeaderWidth);
    core.repaintCells();
  }
  public int getRowHeaderWidth() { return rowHeaderWidth; }

  /**
   * The rowHeaderVisible property controls wether or not the rowHeader is visible.
   * The default is true.
   */
  public void setRowHeaderVisible(boolean visible) {
    if (visible != rowHeaderVisible) {
      rowHeaderVisible = visible;
      if (visible)
        setRowHeaderView(rowHeader);
      else
        setRowHeaderView(null);
      if (isShowing())
        validate();
      core.repaintCells();
    }
  }
  public boolean isRowHeaderVisible() { return rowHeaderVisible; }

  /**
   * The columnHeaderHeight property defines the height of the column header in pixels.
   */
  public void setColumnHeaderHeight(int height) {
    columnHeaderHeight = height;
    invalidate();
    if (columnHeader != null)
      columnHeader.setThickness(columnHeaderHeight);
    core.repaintCells();
  }
  public int getColumnHeaderHeight() { return columnHeaderHeight; }

  public void addHeaderListener(HeaderListener l) {
    rowHeader.addHeaderListener(l);
    columnHeader.addHeaderListener(l);
  }
  public void removeHeaderListener(HeaderListener l) {
    rowHeader.removeHeaderListener(l);
    columnHeader.removeHeaderListener(l);
  }

  /**
   * The columnHeaderVisible property controls wether or not the columnHeader is visible.
   * The default is true.
   */
  public void setColumnHeaderVisible(boolean visible) {
    if (visible != columnHeaderVisible) {
      columnHeaderVisible = visible;
      if (visible)
        setColumnHeaderView(columnHeader);
      else
        setColumnHeaderView(null);
      if (isShowing())
        validate();
      core.repaintCells();
    }
  }
  public boolean isColumnHeaderVisible() { return columnHeaderVisible; }

  /**
   * A ColumnView defines the view state information for a particular column in the grid.
   * The ColumnView class defines such properties as background, foreground, font, etc...
   * Every column displayed in the grid has a ColumnView to go with it.  Individual columnViews
   * can be set using this indexed columnView property. The entire set of columnViews can be set
   * using the columnViews property.
   */
  public void setColumnView(int index, ColumnView col) {
    invalidate();
    core.setColumnView(index, col);
  }
  public ColumnView getColumnView(int index) { return core.getColumnView(index); }

  /**
   * A ColumnView defines the view state information for a particular column in the grid.
   * The ColumnView class defines such properties as background, foreground, font, etc...
   * Every column displayed in the grid has a ColumnView to go with it.  Individual columnViews
   * can be set using this indexed columnView property. The entire set of columnViews can be set
   * using the columnViews property.
   */
  public void setColumnViews(ColumnView[] columnViews) {
    invalidate();
    core.setColumnViews(columnViews);
  }
  public ColumnView[] getColumnViews() { return core.getColumnViews(); }

  /**
   * A ColumnView may not appear in the columnViews array at the same ordinal as its data
   * does in the model.  Use getColumnOrdinal to determine the MatrixModel column ordinal
   * for a display column.
   */
  public int getColumnOrdinal(int displayColumn) {
    return core.getColumnOrdinal(displayColumn);
  }

  /**
   * The opaque property controls the grid's opacity.  If a texture is set,
   * the opaque property will automatically be 'true'.  By default, it is true.
   */
  public void setOpaque(boolean opaque) {
    // workaround for new JScrollPane behavior in JDK 1.3, which invokes
    // setOpaque from within the default constructor.
    if (core != null) {
      core.setOpaque(opaque);
      columnHeader.setOpaque(opaque);
      rowHeader.setOpaque(opaque);
      butUL.setOpaque(opaque);
      butUR.setOpaque(opaque);
      butLL.setOpaque(opaque);
      butLR.setOpaque(opaque);
    }
  }
  public boolean isOpaque() { return core.isOpaque(); }

  /**
   * The texture property defines an image to fill the background of the grid.
   */
  public void setTexture(Image texture) { core.setTexture(texture); }
  public Image getTexture() { return core.getTexture(); }

  // Methods

  /**
   * Starts an edit session at the cell location defined by 'cell'.
   * If editInPlace is false or if readOnly is true, this method is a no-op.
   * @param cell The MatrixLocation to start the edit session at.
   */
  public void startEdit(MatrixLocation cell) {
    core.startEdit(new MatrixLocation(cell));
  }

  /**
   * Resets the scroll position, sets the subfocus back to (0,0), and makes sure the grid
   * is properly sized and validated.
   */
  public void reset() { core.reset(); }

  /**
   * Repaints the cell at the passed location.
   * @param cell The MatrixLocation that you want repainted.
   */
  public void repaintCell(MatrixLocation cell) { core.repaintCell(cell); }

  /**
   * Repaints all the visible cells in the grid.
   */
  public void repaintCells() { core.repaintCells(); }

  /**
   * Repaints all the visible cells in the range between the start and end cell location.
   * @param start The top-left cell location to repaint.
   * @param end The bottom-right cell location to repaint.
   */
  public void repaintCells(MatrixLocation start, MatrixLocation end) { core.repaintCells(start, end); }

  /**
   * Returns the address (MatrixLocation) of the cell at the coordinates specified.  Coordinates
   * are relative to the entire scrollable region inside of the GridView.  Use getViewport().getViewPosition()
   * and getViewport().getExtentSize() to calculate relative point positions to external components.
   * @param x The x location.
   * @param y The y location.
   * @return The hit MatrixLocation (row,column), or null if nothing was hit.
   */
  public MatrixLocation hitTest(int x, int y) { return core.hitTest(x, y); }

  /**
   * Returns the Rectangle (in pixels) that bounds the cell at the specified location.  Coordinates
   * are relative to the entire scrollable region inside of the GridView.  Use getViewport().getViewPosition()
   * and getViewport().getExtentSize() to calculate relative point positions to external components.
   * @param cell The cell that you want the rectangle for.
   * @return The bounding rectangle of the cell.
   */
  public Rectangle getCellRect(MatrixLocation cell) { return core.getCellRect(cell); }

  /**
   * Returns the Rectangle (in pixels) that bounds the range of cells at the specified locations.
   * Coordinates are relative to the entire scrollable region inside of the GridView.  Use
   * getViewport().getViewPosition() and getViewport().getExtentSize() to calculate relative point positions to external
   * components.
   * @param start The top-left cell location to calculate.
   * @param end The bottom-right cell location to calculate.
   * @return The bounding rectangle of the cell range.
   */
  public Rectangle getCellRangeRect(MatrixLocation start, MatrixLocation end) { return core.getCellRangeRect(start, end); }

  /**
   * Moves any embedded editor to its proper position (and size) if the GridView has been externally
   * manipulated. This method is used by subclasses or consumers that are manipulating the rowSizes
   * and columnSizes while the grid is in an edit session.
   */
  public void resyncEditor() { core.resyncEditor(); }

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

  public void checkParentWindow() { core.checkParentWindow(); }

  // Events

  /**
   * A MatrixModelListener will get notifications about changes to this grid's data structure.
   */
  public void addModelListener(MatrixModelListener listener) { core.addModelListener(listener); }
  public void removeModelListener(MatrixModelListener listener) { core.removeModelListener(listener); }

  /**
   * A MatrixSelectionListener will get notifications about changes to this grid's selection pool.
   */
  public void addSelectionListener(MatrixSelectionListener listener) { core.addSelectionListener(listener); }
  public void removeSelectionListener(MatrixSelectionListener listener) { core.removeSelectionListener(listener); }

  /**
   * A MatrixSubfocusListener will get notifications when the grid's subfocus location changes.
   */
  public void addSubfocusListener(MatrixSubfocusListener listener) { core.addSubfocusListener(listener); }
  public void removeSubfocusListener(MatrixSubfocusListener listener) { core.removeSubfocusListener(listener); }

  /**
   * An ActionListener will get notifications when a user double-clicks a cell, or hits ENTER to
   * post a change to a cell.
   */
  public void addActionListener(ActionListener l) { core.addActionListener(l); }
  public void removeActionListener(ActionListener l) { core.removeActionListener(l); }

  public void addKeyListener(KeyListener l) { core.addKeyListener(l); }
  public void removeKeyListener(KeyListener l) { core.removeKeyListener(l); }

  public void addMouseListener(MouseListener l) { core.addMouseListener(l); }
  public void removeMouseListener(MouseListener l) { core.removeMouseListener(l); }

  public void addMouseMotionListener(MouseMotionListener l) { core.addMouseMotionListener(l); }
  public void removeMouseMotionListener(MouseMotionListener l) { core.removeMouseMotionListener(l); }

  public void addFocusListener(FocusListener listener) {
    if (core != null) {
      core.addFocusListener(listener);
    } else {
      super.addFocusListener(listener);
    }
  }
  public void removeFocusListener(FocusListener listener) { core.removeFocusListener(listener); }

  public void addCustomItemListener(CustomItemListener l) { core.addCustomItemListener(l); }
  public void removeCustomItemListener(CustomItemListener l) { core.removeCustomItemListener(l); }

  public void requestFocus() {
    core.requestFocus();
  }
  public boolean hasFocus() {
    return core.hasFocus();
  }

  // Deprecated methods, properties, etc.

  /** @DEPRECATED - Use autoEdit property */
  public void setAlwaysEdit(boolean autoEdit) { core.setAutoEdit(autoEdit); }
  /** @DEPRECATED - Use autoEdit property */
  public boolean isAlwaysEdit() { return core.isAutoEdit(); }

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
  /** @DEPRECATED - Use setHorizontalScrollBarPolicy(int policy) method */
  public void setShowHScroll(boolean show) {
    setHorizontalScrollBarPolicy(show ? HORIZONTAL_SCROLLBAR_AS_NEEDED : HORIZONTAL_SCROLLBAR_NEVER);
  }
  /** @DEPRECATED - Use getHorizontalScrollBarPolicy() method */
  public boolean isShowHScroll() {
    return getHorizontalScrollBarPolicy() == HORIZONTAL_SCROLLBAR_AS_NEEDED;
  }
  /** @DEPRECATED - Use getVerticalScrollBarPolicy() method */
  public boolean isShowVScroll() {
    return getVerticalScrollBarPolicy() == VERTICAL_SCROLLBAR_AS_NEEDED;
  }
  /** @DEPRECATED - Use setHorizontalScrollBarPolicy(int policy) method */
  public void setShowVScroll(boolean show) {
    setVerticalScrollBarPolicy(show ? VERTICAL_SCROLLBAR_AS_NEEDED : VERTICAL_SCROLLBAR_NEVER);
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

  public void setBackground(Color color) {
    super.setBackground(color);
    if (core != null)
      core.setBackground(color);
    if (columnHeader != null)
      columnHeader.setBackground(color);
    if (rowHeader != null)
      rowHeader.setBackground(color);
  }

  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    core.setEnabled(enabled);
    columnHeader.setEnabled(enabled);
    rowHeader.setEnabled(enabled);
    butUL.setEnabled(enabled);
    butUR.setEnabled(enabled);
    butLL.setEnabled(enabled);
    butLR.setEnabled(enabled);
    getHorizontalScrollBar().setEnabled(enabled);
    getVerticalScrollBar().setEnabled(enabled);
    repaint();
  }

  public void setPreferredSize(Dimension prefSize) {
    this.prefSize = prefSize;
    invalidate();
  }
  public Dimension getPreferredSize() {
    if (prefSize != null)
      return prefSize;
    else
      return super.getPreferredSize();
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

  // default: navigate to the clicked column
  protected void columnHeaderClicked(int index) {
    core.setSubfocus(core.getSubfocus().row, index);
  }

  // default: navigate to the clicked row
  protected void rowHeaderClicked(int index) {
    core.setSubfocus(index, core.getSubfocus().column);
  }

  // HeaderListener implementation

  // a row or column header was clicked.
  public void headerItemClicked(HeaderEvent e) {
    Object source = e.getSource();
    int index = e.getIndex();
    if (source == columnHeader)
      columnHeaderClicked(index);
    else
      rowHeaderClicked(index);
  }

  // a row or column is being resized.
  public void headerItemResizing(HeaderEvent e) {
    Object source = e.getSource();
    int id = e.getID();
    int index = e.getIndex();
    int x = e.getX();
    int y = e.getY();
    switch (id) {
      case HeaderEvent.START_RESIZE:
        core.startResize(source == columnHeader, index, x, y);
        break;
      case HeaderEvent.WHILE_RESIZE:
        core.whileResize(source == columnHeader, index, x, y);
        break;
      case HeaderEvent.STOP_RESIZE:
        core.stopResize(source == columnHeader, index, x, y);
        break;
    }
  }

  public void headerItemMoving(HeaderEvent e) {
    Object source = e.getSource();
    int id = e.getID();
    int index = e.getIndex();
    int x = e.getX();
    int y = e.getY();
    switch (id) {
      case HeaderEvent.START_MOVE:
        core.startMove(source == columnHeader, index, x, y);
        break;
      case HeaderEvent.WHILE_MOVE:
        core.whileMove(source == columnHeader, index, x, y);
        break;
      case HeaderEvent.STOP_MOVE:
        core.stopMove(source == columnHeader, index, x, y);
        break;
    }
  }

  protected HeaderView columnHeader;
  protected boolean    columnHeaderVisible = true;
  protected int        columnHeaderHeight;
  protected HeaderView rowHeader;
  protected boolean    rowHeaderVisible    = true;
  protected int        rowHeaderWidth;
            GridCore   core                = new GridCore(this);
  protected Dimension  prefSize;

  protected GridView_Button butUL =
    new GridView_Button(ImageLoader.loadFromResource("image/flatUL.gif", GridView.class), 
                        ImageLoader.loadFromResource("image/rollUL.gif", GridView.class)); 
  protected GridView_Button butUR =
    new GridView_Button(ImageLoader.loadFromResource("image/flatUR.gif", GridView.class), 
                        ImageLoader.loadFromResource("image/rollUR.gif", GridView.class)); 
  protected GridView_Button butLL =
    new GridView_Button(ImageLoader.loadFromResource("image/flatLL.gif", GridView.class), 
                        ImageLoader.loadFromResource("image/rollLL.gif", GridView.class)); 
  protected GridView_Button butLR =
    new GridView_Button(ImageLoader.loadFromResource("image/flatLR.gif", GridView.class), 
                        ImageLoader.loadFromResource("image/rollLR.gif", GridView.class)); 
}

class GridView_ColumnViewActionAdapter implements ActionListener, java.io.Serializable {
  public GridView_ColumnViewActionAdapter(GridCore core, HeaderView header) {
    this.core = core;
    this.header = header;
  }
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == core) {
      if (e.getModifiers() == ColumnView.PROP_WIDTH)
        header.invalidate();
      header.repaint(100);
    }
  }
  private GridCore core;
  private HeaderView header;
}

class GridView_Button extends JButton {
  public GridView_Button(Image flat, Image roll) {
    super();
    if (flat != null)
      setIcon(new ImageIcon(flat));
    if (roll != null)
      setRolloverIcon(new ImageIcon(roll));
    setFocusPainted(false);
    setRequestFocusEnabled(false);
    setBorder(BorderFactory.createRaisedBevelBorder());
  }

  public boolean isFocusTraversable() {
    return false;
  }
}

