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

//NOTTRANSLATABLE

package com.borland.jbcl.control;

import com.borland.jb.util.BasicBeanInfo;

public class GridControlBeanInfo extends BasicBeanInfo implements java.io.Serializable
{
  public GridControlBeanInfo() {
    beanClass = GridControl.class;
    namedAttributes = new Object[][] {
      {"isContainer", Boolean.FALSE}} ;
    propertyDescriptors = new String[][] {
      {"autoAppend",          "Automatically append rows to end of grid", "isAutoAppend", "setAutoAppend"},     //RES NORES,BI_Grid_autoAppend,NORES,NORES
      {"autoEdit",            "Automatically start edit when a key is typed", "isAutoEdit", "setAutoEdit"},     //RES NORES,BI_autoEdit,NORES,NORES
      {"autoInsert",          "Insert blank data object when component is empty", "isAutoInsert", "setAutoInsert"},     //RES NORES,BI_autoInsert,NORES,NORES
      {"background",          "Background color", "getBackground", "setBackground"},     //RES NORES,BI_background,NORES,NORES
      {"columnHeaderHeight",  "Height of column header (pixels)", "getColumnHeaderHeight", "setColumnHeaderHeight"},     //RES NORES,BI_Grid_columnHeaderHeight,NORES,NORES
      {"columnHeaderVisible", "Show column header", "isColumnHeaderVisible", "setColumnHeaderVisible"},     //RES NORES,BI_Grid_columnHeaderVisible,NORES,NORES
      {"columnCaptions",      "Column caption text (String[])", "getColumnCaptions", "setColumnCaptions"}, // String[]     //RES NORES,BI_Grid_columnCaptions,NORES,NORES
      {"dataSet",             "DataSet data source", "getDataSet", "setDataSet"},     //RES NORES,BI_dataSet,NORES,NORES
      {"dataToolTip",         "Automatic toolTipText from model", "isDataToolTip", "setDataToolTip"},     //RES NORES,BI_dataToolTip,NORES,NORES
      {"defaultColumnWidth",  "Default column width (pixels)", "getDefaultColumnWidth", "setDefaultColumnWidth"},     //RES NORES,BI_Grid_defaultColumnWidth,NORES,NORES
      {"doubleBuffered",      "Double buffered painting", "isDoubleBuffered", "setDoubleBuffered"},     //RES NORES,BI_doubleBuffered,NORES,NORES
      {"dragSubfocus",        "Change the subfocus item when mouse is dragged", "isDragSubfocus", "setDragSubfocus"},     //RES NORES,BI_dragSubfocus,NORES,NORES
      {"editInPlace",         "Allow in-place editing", "isEditInPlace", "setEditInPlace"},     //RES NORES,BI_editInPlace,NORES,NORES
      {"enabled",             "Enabled state", "isEnabled", "setEnabled"},     //RES NORES,BI_enabled,NORES,NORES
      {"font",                "Default font", "getFont", "setFont"},     //RES NORES,BI_font,NORES,NORES
      {"foreground",          "Foreground color", "getForeground", "setForeground"},     //RES NORES,BI_foreground,NORES,NORES
      {"growEditor",          "Auto-size ItemEditors", "isGrowEditor", "setGrowEditor"},     //RES NORES,BI_growEditor,NORES,NORES
      {"gridLineColor",       "Grid line color", "getGridLineColor", "setGridLineColor"},     //RES NORES,BI_Grid_gridLineColor,NORES,NORES
      {"gridVisible",         "Show grid lines", "isGridVisible", "setGridVisible"},     //RES NORES,BI_Grid_gridVisible,NORES,NORES
      {"horizontalLines",     "Show horizontal grid lines", "isHorizontalLines", "setHorizontalLines"},     //RES NORES,BI_Grid_horizontalLines,NORES,NORES
      {"horizontalScrollBarPolicy", "ScrollBar display policy", "getHorizontalScrollBarPolicy", "setHorizontalScrollBarPolicy", com.borland.jbcl.editors.HorizontalScrollBarPolicyEditor.class.getName()},     //RES NORES,BI_SBPolicy,NORES,NORES
      {"items",               "Item string array", "getItems", "setItems"},     //RES NORES,BI_items,NORES,NORES
      {"moveableColumns",     "Allow column reordering", "isMoveableColumns", "setMoveableColumns"},     //RES NORES,BI_Grid_moveableColumns,NORES,NORES
      {"multiSelect",         "Allow multiple selection", "isMultiSelect", "setMultiSelect"},     //RES NORES,BI_multiSelect,NORES,NORES
      {"navigateOnEnter",     "Navigate when ENTER key is pressed", "isNavigateOnEnter", "setNavigateOnEnter"},     //RES NORES,BI_Grid_navigateOnEnter,NORES,NORES
      {"navigateOnTab",       "Navigate in grid when TAB key is pressed", "isNavigateOnTab", "setNavigateOnTab"},     //RES NORES,BI_Grid_navigateOnTab,NORES,NORES
      {"navigateWithDataSet", "Navigate with DataSet (row cursor)", "isNavigateWithDataSet", "setNavigateWithDataSet"},     //RES NORES,BI_navigateWithDataSet,NORES,NORES
      {"opaque",              "Opaque setting (false == transparent)", "isOpaque", "setOpaque"},     //RES NORES,BI_opaque,NORES,NORES
      {"postOnEndEdit",       "Auto-post after editing", "isPostOnEndEdit", "setPostOnEndEdit"},     //RES NORES,BI_postOnEndEdit,NORES,NORES
      {"preferredSize",       "Preferred layout size (pixels)", "getPreferredSize", "setPreferredSize"},     //RES NORES,BI_preferredSize,NORES,NORES
      {"readOnly",            "Read only state", "isReadOnly", "setReadOnly"},     //RES NORES,BI_readOnly,NORES,NORES
      {"resizableColumns",    "Allow column resizing", "isResizableColumns", "setResizableColumns"},     //RES NORES,BI_Grid_resizableColumns,NORES,NORES
      {"resizableRows",       "Allow row resizing", "isResizableRows", "setResizableRows"},     //RES NORES,BI_Grid_resizableRows,NORES,NORES
      {"rowHeaderVisible",    "Show row header", "isRowHeaderVisible", "setRowHeaderVisible"},     //RES NORES,BI_Grid_rowHeaderVisible,NORES,NORES
      {"rowHeaderWidth",      "Width of row header (pixels)", "getRowHeaderWidth", "setRowHeaderWidth"},     //RES NORES,BI_Grid_rowHeaderWidth,NORES,NORES
      {"selectColumn",        "Select entire column", "isSelectColumn", "setSelectColumn"},     //RES NORES,BI_Grid_selectColumn,NORES,NORES
      {"selectRow",           "Select entire row", "isSelectRow", "setSelectRow"},     //RES NORES,BI_Grid_selectRow,NORES,NORES
      {"showFocus",           "Show focus rectangle", "isShowFocus", "setShowFocus"},     //RES NORES,BI_showFocus,NORES,NORES
      {"showPopup",           "Show popup menu", "isShowPopup", "setShowPopup"},     //RES NORES,BI_showPopup,NORES,NORES
      {"showRollover",        "Show rollover item", "isShowRollover", "setShowRollover"},     //RES NORES,BI_showRollover,NORES,NORES
      {"sortOnHeaderClick",   "Sort column on header click (requires DataSet)", "isSortOnHeaderClick", "setSortOnHeaderClick"},     //RES NORES,BI_Grid_sortOnHeaderClick,NORES,NORES
      {"subfocus",            "Current subfocus cell [row, column]", "getSubfocus", "setSubfocus", com.borland.jbcl.editors.MatrixLocationEditor.class.getName()},     //RES NORES,BI_Grid_subfocus,NORES,NORES
      {"textureName",         "Background tiled texture", "getTextureName", "setTextureName", com.borland.jbcl.editors.FileNameEditor.class.getName()},     //RES NORES,BI_texture,NORES,NORES
      {"toolTipText",         "ToolTip help text", "getToolTipText", "setToolTipText"},     //RES NORES,BI_toolTipText,NORES,NORES
      {"verticalLines",       "Show vertical grid lines", "isVerticalLines", "setVerticalLines"},     //RES NORES,BI_Grid_verticalLines,NORES,NORES
      {"verticalScrollBarPolicy", "ScrollBar display policy", "getVerticalScrollBarPolicy", "setVerticalScrollBarPolicy", com.borland.jbcl.editors.VerticalScrollBarPolicyEditor.class.getName()},     //RES NORES,BI_SBPolicy,NORES,NORES
      {"visible",             "Visible state", "isVisible", "setVisible"},     //RES NORES,BI_visible,NORES,NORES
    };
  }
}
