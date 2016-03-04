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

public class TextFieldControlBeanInfo extends BasicBeanInfo implements java.io.Serializable
{
  public TextFieldControlBeanInfo() {
    beanClass = TextFieldControl.class;
    namedAttributes = new Object[][] {
      {"isContainer", Boolean.FALSE}} ;
    propertyDescriptors = new String[][] {
      {"background",      "Background color", "getBackground", "setBackground"},     //RES NORES,BI_background,NORES,NORES
      {"caretPosition",   "Caret position", "getCaretPosition", "setCaretPosition"},     //RES NORES,BI_Text_caretPosition,NORES,NORES
      {"columnName",      "Column name from DataSet", "getColumnName", "setColumnName", "com.borland.jbuilder.cmt.editors.ColumnNameEditor"}, 
      {"columns",         "Number of columns", "getColumns", "setColumns"},     //RES NORES,BI_Text_columns,NORES,NORES
      {"dataSet",         "DataSet data source", "getDataSet", "setDataSet"},     //RES NORES,BI_dataSet,NORES,NORES
      {"echoChar",        "Echo character", "getEchoChar", "setEchoChar"},     //RES NORES,BI_Text_echoChar,NORES,NORES
      {"editable",        "Allow editing", "isEditable", "setEditable"},     //RES NORES,BI_Text_editable,NORES,NORES
      {"enabled",         "Enabled state", "isEnabled", "setEnabled"},     //RES NORES,BI_enabled,NORES,NORES
      {"font",            "Default font", "getFont", "setFont"},     //RES NORES,BI_font,NORES,NORES
      {"foreground",      "Foreground color", "getForeground", "setForeground"},     //RES NORES,BI_foreground,NORES,NORES
      {"postOnFocusLost", "Auto-post after losing focus", "isPostOnFocusLost", "setPostOnFocusLost"},     //RES NORES,BI_postOnFocusLost,NORES,NORES
      {"readOnly",        "Read only state", "isReadOnly", "setReadOnly"},     //RES NORES,BI_readOnly,NORES,NORES
      {"selectionEnd",    "End of selection", "getSelectionEnd", "setSelectionEnd"},     //RES NORES,BI_Text_selectionEnd,NORES,NORES
      {"selectionStart",  "Start of selection", "getSelectionStart", "setSelectionStart"},     //RES NORES,BI_Text_selectionStart,NORES,NORES
      {"text",            "Text string", "getText", "setText"},     //RES NORES,BI_text,NORES,NORES
      {"visible",         "Visible state", "isVisible", "setVisible"},     //RES NORES,BI_visible,NORES,NORES
    };
  }
}
