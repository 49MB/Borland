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

public class PopupPickListItemEditorBeanInfo extends BasicBeanInfo implements java.io.Serializable
{
  public PopupPickListItemEditorBeanInfo() {
    beanClass = PopupPickListItemEditor.class;
    namedAttributes = new Object[][] {
      {"isContainer", Boolean.FALSE}} ;
    propertyDescriptors = new String[][] {
      {"allowSearch",    "Allow incremental searching", "isAllowSearch", "setAllowSearch"},     //RES NORES,BI_allowSearch,NORES,NORES
      {"alwaysCenter",    "Always center picklist window", "isAlwaysCenter", "setAlwaysCenter"},     //RES NORES,BI_alwaysCenter,NORES,NORES
//      {"background", Res._BI_background, "getBackground", "setBackground"},
      {"displayOKCancel",    "Display an OK/Cancel button bar", "isDisplayOKCancel", "setDisplayOKCancel"},     //RES NORES,BI_displayOKCancel,NORES,NORES
//      {"enabled",    Res._BI_enabled, "isEnabled", "setEnabled"},
//      {"font",       Res._BI_font, "getFont", "setFont"},
//      {"foreground", Res._BI_foreground, "getForeground", "setForeground"},
      {"title",    "Title string", "getTitle", "setTitle"},     //RES NORES,BI_title,NORES,NORES
//      {"visible",    Res._BI_visible, "isVisible", "setVisible"},
    };

    eventSetDescriptors = new String [][] {
    };
  }

}
