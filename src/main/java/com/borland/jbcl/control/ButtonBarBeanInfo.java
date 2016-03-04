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

public class ButtonBarBeanInfo extends BasicBeanInfo implements java.io.Serializable
{
  public ButtonBarBeanInfo() {
    beanClass = ButtonBar.class;
    namedAttributes = new Object[][] {
      {"isContainer", Boolean.FALSE}} ;
    propertyDescriptors = new String[][] {
      {"alignment",         "Button alignment", "getAlignment", "setAlignment", com.borland.jbcl.editors.FlowAlignmentEditor.class.getName()}, // int     //RES NORES,BI_ButtonBar_alignment,NORES,NORES
      {"background",        "Background color", "getBackground", "setBackground"},     //RES NORES,BI_background,NORES,NORES
      {"bevelInner",        "Inner edge style", "getBevelInner", "setBevelInner", com.borland.jbcl.editors.BevelTypeEditor.class.getName()}, // int     //RES NORES,BI_BevelPanel_bevelInner,NORES,NORES
      {"bevelOuter",        "Outer edge style", "getBevelOuter", "setBevelOuter", com.borland.jbcl.editors.BevelTypeEditor.class.getName()}, // int     //RES NORES,BI_BevelPanel_bevelOuter,NORES,NORES
      {"buttonAlignment",   "Alignment setting", "getButtonAlignment", "setButtonAlignment", com.borland.jbcl.editors.AlignmentEditor.class.getName()}, // int     //RES NORES,BI_alignment,NORES,NORES
      {"buttonOrientation", "Orientation setting", "getButtonOrientation", "setButtonOrientation", com.borland.jbcl.editors.OrientationEditor.class.getName()}, // int     //RES NORES,BI_orientation,NORES,NORES
      {"buttonType",        "Button type", "getButtonType", "setButtonType", com.borland.jbcl.editors.ButtonBarButtonTypeEditor.class.getName()}, // int     //RES NORES,BI_ButtonBar_buttonType,NORES,NORES
      {"enabled",           "Enabled state", "isEnabled", "setEnabled"},     //RES NORES,BI_enabled,NORES,NORES
      {"font",              "Default font", "getFont", "setFont"},     //RES NORES,BI_font,NORES,NORES
      {"foreground",        "Foreground color", "getForeground", "setForeground"},     //RES NORES,BI_foreground,NORES,NORES
      {"hgap",              "Horizontal gap (pixels)", "getHgap", "setHgap"},     //RES NORES,BI_hgap,NORES,NORES
      {"imageBase",         "Image base", "getImageBase", "setImageBase", com.borland.jbcl.editors.DirectoryEditor.class.getName()}, // String     //RES NORES,BI_ButtonBar_imageBase,NORES,NORES
      {"imageFirst",        "Image left/above label", "isImageFirst", "setImageFirst"},     //RES NORES,BI_ButtonControl_imageFirst,NORES,NORES
      {"imageNames",        "Image name string array", "getImageNames", "setImageNames"}, // String[]     //RES NORES,BI_ButtonBar_imageNames,NORES,NORES
      {"labels",            "Button label string array", "getLabels", "setLabels"}, // String[]     //RES NORES,BI_ButtonBar_labels,NORES,NORES
//      {"layout",            Res._BI_layout, "getLayout", "setLayout"},
      {"margins",           "Border spacing [top, left, bottom, right] (pixels)", "getMargins", "setMargins"}, // Insets     //RES NORES,BI_margins,NORES,NORES
      {"opaque",            "Opaque setting (false == transparent)", "isOpaque", "setOpaque"},     //RES NORES,BI_opaque,NORES,NORES
      {"showRollover",      "Show rollover item", "isShowRollover", "setShowRollover"},     //RES NORES,BI_showRollover,NORES,NORES
      {"soft",              "Use soft edge colors", "isSoft", "setSoft"},     //RES NORES,BI_BevelPanel_soft,NORES,NORES
      {"textureName",       "Background tiled texture", "getTextureName", "setTextureName", com.borland.jbcl.editors.FileNameEditor.class.getName()},     //RES NORES,BI_texture,NORES,NORES
      {"toolTipText",       "ToolTip help text", "getToolTipText", "setToolTipText"},     //RES NORES,BI_toolTipText,NORES,NORES
      {"visible",           "Visible state", "isVisible", "setVisible"},     //RES NORES,BI_visible,NORES,NORES
      {"vgap",              "Vertical gap (pixels)", "getVgap", "setVgap"},     //RES NORES,BI_vgap,NORES,NORES
    };
  }
}
