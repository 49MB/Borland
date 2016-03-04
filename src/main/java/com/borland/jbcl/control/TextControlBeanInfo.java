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

public class TextControlBeanInfo extends BasicBeanInfo implements java.io.Serializable
{
  public TextControlBeanInfo() {
    beanClass = TextControl.class;
    namedAttributes = new Object[][] {
      {"isContainer", Boolean.FALSE}} ;
    propertyDescriptors = new String[][] {
      {"alignment",   "Alignment setting", "getAlignment", "setAlignment", com.borland.jbcl.editors.AlignmentEditor.class.getName()},     //RES NORES,BI_alignment,NORES,NORES
      {"background",  "Background color", "getBackground", "setBackground"},     //RES NORES,BI_background,NORES,NORES
      {"drawEdge",    "Show edge", "isDrawEdge", "setDrawEdge"},     //RES NORES,BI_Shape_drawEdge,NORES,NORES
      {"edgeColor",   "Edge color", "getEdgeColor", "setEdgeColor"},     //RES NORES,BI_Shape_edgeColor,NORES,NORES
      {"font",        "Default font", "getFont", "setFont"},     //RES NORES,BI_font,NORES,NORES
      {"foreground",  "Foreground color", "getForeground", "setForeground"},     //RES NORES,BI_foreground,NORES,NORES
      {"margins",     "Border spacing [top, left, bottom, right] (pixels)", "getMargins", "setMargins"},     //RES NORES,BI_margins,NORES,NORES
      {"text",        "Text string", "getText", "setText"},     //RES NORES,BI_text,NORES,NORES
      {"toolTipText", "ToolTip help text", "getToolTipText", "setToolTipText"},     //RES NORES,BI_toolTipText,NORES,NORES
      {"transparent", "Transparent painting", "isTransparent", "setTransparent"},     //RES NORES,BI_transparent,NORES,NORES
      {"visible",     "Visible state", "isVisible", "setVisible"},     //RES NORES,BI_visible,NORES,NORES
    };
  }
}
