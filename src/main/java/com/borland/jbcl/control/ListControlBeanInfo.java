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

public class ListControlBeanInfo extends BasicBeanInfo implements java.io.Serializable
{
  public ListControlBeanInfo() {
    beanClass = ListControl.class;
    namedAttributes = new Object[][] {
      {"isContainer", Boolean.FALSE}} ;
    propertyDescriptors = new String[][] {
      {"alignment",     "Alignment setting", "getAlignment", "setAlignment", com.borland.jbcl.editors.AlignmentEditor.class.getName()}, // int     //RES NORES,BI_alignment,NORES,NORES
      {"autoAppend",    "Automatically append items to end of list", "isAutoAppend", "setAutoAppend"},     //RES NORES,BI_List_autoAppend,NORES,NORES
      {"autoEdit",      "Automatically start edit when a key is typed", "isAutoEdit", "setAutoEdit"},     //RES NORES,BI_autoEdit,NORES,NORES
      {"autoInsert",    "Insert blank data object when component is empty", "isAutoInsert", "setAutoInsert"},     //RES NORES,BI_autoInsert,NORES,NORES
      {"background",    "Background color", "getBackground", "setBackground"},     //RES NORES,BI_background,NORES,NORES
      {"columnName",    "Column name from DataSet", "getColumnName", "setColumnName", "com.borland.jbuilder.cmt.editors.ColumnNameEditor"}, 
      {"dataSet",       "DataSet data source", "getDataSet", "setDataSet"}, // com.borland.dx.dataset.DataSet     //RES NORES,BI_dataSet,NORES,NORES
      {"dataToolTip",   "Automatic toolTipText from model", "isDataToolTip", "setDataToolTip"},     //RES NORES,BI_dataToolTip,NORES,NORES
      {"doubleBuffered",  "Double buffered painting", "isDoubleBuffered", "setDoubleBuffered"},     //RES NORES,BI_doubleBuffered,NORES,NORES
      {"dragSubfocus",  "Change the subfocus item when mouse is dragged", "isDragSubfocus", "setDragSubfocus"},     //RES NORES,BI_dragSubfocus,NORES,NORES
      {"editInPlace",   "Allow in-place editing", "isEditInPlace", "setEditInPlace"},     //RES NORES,BI_editInPlace,NORES,NORES
      {"enabled",       "Enabled state", "isEnabled", "setEnabled"},     //RES NORES,BI_enabled,NORES,NORES
      {"font",          "Default font", "getFont", "setFont"},     //RES NORES,BI_font,NORES,NORES
      {"foreground",    "Foreground color", "getForeground", "setForeground"},     //RES NORES,BI_foreground,NORES,NORES
      {"growEditor",    "Auto-size ItemEditors", "isGrowEditor", "setGrowEditor"},     //RES NORES,BI_growEditor,NORES,NORES
      {"horizontalScrollBarPolicy", "ScrollBar display policy", "getHorizontalScrollBarPolicy", "setHorizontalScrollBarPolicy", com.borland.jbcl.editors.HorizontalScrollBarPolicyEditor.class.getName()},     //RES NORES,BI_SBPolicy,NORES,NORES
      {"itemHeight",    "Item height (pixels)", "getItemHeight", "setItemHeight"},     //RES NORES,BI_List_itemHeight,NORES,NORES
      {"itemMargins",   "Item margins [top, left, bottom, right] (pixels)", "getItemMargins", "setItemMargins"}, // Insets     //RES NORES,BI_itemMargins,NORES,NORES
      {"items",         "Item string array", "getItems", "setItems"}, // String[]     //RES NORES,BI_items,NORES,NORES
      {"itemWidth",     "Item width (pixels)", "getItemWidth", "setItemWidth"},     //RES NORES,BI_List_itemWidth,NORES,NORES
      {"multiSelect",   "Allow multiple selection", "isMultiSelect", "setMultiSelect"},     //RES NORES,BI_multiSelect,NORES,NORES
      {"opaque",        "Opaque setting (false == transparent)", "isOpaque", "setOpaque"},     //RES NORES,BI_opaque,NORES,NORES
      {"postOnEndEdit", "Auto-post after editing", "isPostOnEndEdit", "setPostOnEndEdit"},     //RES NORES,BI_postOnEndEdit,NORES,NORES
      {"readOnly",      "Read only state", "isReadOnly", "setReadOnly"},     //RES NORES,BI_readOnly,NORES,NORES
      {"showFocus",     "Show focus rectangle", "isShowFocus", "setShowFocus"},     //RES NORES,BI_showFocus,NORES,NORES
      {"showRollover",  "Show rollover item", "isShowRollover", "setShowRollover"},     //RES NORES,BI_showRollover,NORES,NORES
      {"snapOrigin",    "Snap scroll to origin", "isSnapOrigin", "setSnapOrigin"},     //RES NORES,BI_snapOrigin,NORES,NORES
      {"subfocus",      "Current subfocus index", "getSubfocus", "setSubfocus"}, // int     //RES NORES,BI_List_subfocus,NORES,NORES
      {"textureName",   "Background tiled texture", "getTextureName", "setTextureName", com.borland.jbcl.editors.FileNameEditor.class.getName()},     //RES NORES,BI_texture,NORES,NORES
      {"toolTipText",   "ToolTip help text", "getToolTipText", "setToolTipText"},     //RES NORES,BI_toolTipText,NORES,NORES
      {"uniformHeight", "Rows all have the same height", "isUniformHeight", "setUniformHeight"},     //RES NORES,BI_List_uniformHeight,NORES,NORES
      {"uniformWidth",  "Rows all have the same width", "isUniformWidth", "setUniformWidth"},     //RES NORES,BI_List_uniformWidth,NORES,NORES
      {"verticalScrollBarPolicy", "ScrollBar display policy", "getVerticalScrollBarPolicy", "setVerticalScrollBarPolicy", com.borland.jbcl.editors.VerticalScrollBarPolicyEditor.class.getName()},     //RES NORES,BI_SBPolicy,NORES,NORES
      {"visible",       "Visible state", "isVisible", "setVisible"},     //RES NORES,BI_visible,NORES,NORES
    };
  }
}
