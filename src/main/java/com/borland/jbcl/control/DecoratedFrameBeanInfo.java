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

public class DecoratedFrameBeanInfo extends BasicBeanInfo implements java.io.Serializable
{
  public DecoratedFrameBeanInfo() {
    beanClass = DecoratedFrame.class;
    propertyDescriptors = new String[][] {
      {"background",    "Background color", "getBackground", "setBackground"},     //RES NORES,BI_background,NORES,NORES
      {"disposeOnClose", "Call dispose() on window closing", "isDisposeOnClose", "setDisposeOnClose"},     //RES NORES,BI_DecFrame_disposeOnClose,NORES,NORES
      {"enabled",       "Enabled state", "isEnabled", "setEnabled"},     //RES NORES,BI_enabled,NORES,NORES
      {"exitOnClose",   "Call System.exit() when window closed", "isExitOnClose", "setExitOnClose"},     //RES NORES,BI_DecFrame_exitOnClose,NORES,NORES
      {"font",          "Default font", "getFont", "setFont"},     //RES NORES,BI_font,NORES,NORES
      {"foreground",    "Foreground color", "getForeground", "setForeground"},     //RES NORES,BI_foreground,NORES,NORES
      {"iconImageName", "Image Name", "getIconImageName", "setIconImageName", com.borland.jbcl.editors.FileNameEditor.class.getName()},     //RES NORES,BI_imageName,NORES,NORES
      {"iconImageURL",  "Image URL", "getIconImageURL", "setIconImageURL"},     //RES NORES,BI_imageURL,NORES,NORES
      {"layout",        "Layout manager for contained components", "getLayout", "setLayout"},     //RES NORES,BI_layout,NORES,NORES
      {"menuBar",       "Menu bar", "getMenuBar", "setMenuBar"},     //RES NORES,BI_DecFrame_menuBar,NORES,NORES
      {"resizable",     "Resizable state", "isResizable", "setResizable"},     //RES NORES,BI_resizable,NORES,NORES
      {"size",          "Initial size", "getSize", "setSize"},     //RES NORES,BI_size,NORES,NORES
      {"title",         "Title string", "getTitle", "setTitle"},     //RES NORES,BI_title,NORES,NORES
      {"visible",       "Visible state", "isVisible", "setVisible"},     //RES NORES,BI_visible,NORES,NORES
    };
  }
}
