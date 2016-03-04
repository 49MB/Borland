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

public class ChoiceControlBeanInfo extends BasicBeanInfo implements java.io.Serializable
{
  public ChoiceControlBeanInfo() {
    beanClass = ChoiceControl.class;
    namedAttributes = new Object[][] {
      {"isContainer", Boolean.FALSE}} ;
    propertyDescriptors = new String[][] {
      {"autoAdd",    "Automatically add set items to list", "isAutoAdd", "setAutoAdd"},     //RES NORES,BI_Choice_autoAdd,NORES,NORES
      {"background", "Background color", "getBackground", "setBackground"},     //RES NORES,BI_background,NORES,NORES
      {"columnName", "Column name from DataSet", "getColumnName", "setColumnName", "com.borland.jbuilder.cmt.editors.ColumnNameEditor"}, 
      {"dataSet",    "DataSet data source", "getDataSet", "setDataSet"},     //RES NORES,BI_dataSet,NORES,NORES
      {"enabled",    "Enabled state", "isEnabled", "setEnabled"},     //RES NORES,BI_enabled,NORES,NORES
      {"font",       "Default font", "getFont", "setFont"},     //RES NORES,BI_font,NORES,NORES
      {"foreground", "Foreground color", "getForeground", "setForeground"},     //RES NORES,BI_foreground,NORES,NORES
      {"items",      "Item string array", "getItems", "setItems"},     //RES NORES,BI_items,NORES,NORES
      {"readOnly",   "Read only state", "isReadOnly", "setReadOnly"},     //RES NORES,BI_readOnly,NORES,NORES
      {"visible",    "Visible state", "isVisible", "setVisible"},     //RES NORES,BI_visible,NORES,NORES
    };
  }
}
