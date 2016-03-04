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

public class CheckboxPanelBeanInfo extends BasicBeanInfo implements java.io.Serializable
{
  public CheckboxPanelBeanInfo() {
    beanClass = CheckboxPanel.class;
    namedAttributes = new Object[][] {
      {"isContainer", Boolean.FALSE}} ;
    propertyDescriptors = new String[][] {
      {"background",    "Background color", "getBackground", "setBackground"},     //RES NORES,BI_background,NORES,NORES
      {"bevelInner",    "Inner edge style", "getBevelInner", "setBevelInner", com.borland.jbcl.editors.BevelTypeEditor.class.getName()},     //RES NORES,BI_BevelPanel_bevelInner,NORES,NORES
      {"bevelOuter",    "Outer edge style", "getBevelOuter", "setBevelOuter", com.borland.jbcl.editors.BevelTypeEditor.class.getName()},     //RES NORES,BI_BevelPanel_bevelOuter,NORES,NORES
      {"enabled",       "Enabled state", "isEnabled", "setEnabled"},     //RES NORES,BI_enabled,NORES,NORES
      {"font",          "Default font", "getFont", "setFont"},     //RES NORES,BI_font,NORES,NORES
      {"foreground",    "Foreground color", "getForeground", "setForeground"},     //RES NORES,BI_foreground,NORES,NORES
      {"grouped",       "Allow only 1 selected checkbox at a time", "isGrouped", "setGrouped"},     //RES NORES,BI_CheckboxPanel_grouped,NORES,NORES
      {"labels",        "Label string array", "getLabels", "setLabels"},     //RES NORES,BI_labels,NORES,NORES
      {"margins",       "Border spacing [top, left, bottom, right] (pixels)", "getMargins", "setMargins"},     //RES NORES,BI_margins,NORES,NORES
      {"orientation",   "Orientation setting", "getOrientation", "setOrientation", com.borland.jbcl.editors.OrientationEditor.class.getName()},     //RES NORES,BI_orientation,NORES,NORES
      {"selectedIndex", "Currenly selected checkbox (if grouped)", "getSelectedIndex", "setSelectedIndex"},     //RES NORES,BI_CheckboxPanel_selected,NORES,NORES
      {"selectedLabel", "Currenly selected checkbox (if grouped)", "getSelectedLabel", "setSelectedLabel"},     //RES NORES,BI_CheckboxPanel_selected,NORES,NORES
      {"selectedLabels", "Currenly selected checkboxes (non-grouped)", "getSelectedLabels", "setSelectedLabels"},     //RES NORES,BI_CheckboxPanel_selectedLabels,NORES,NORES
      {"soft",          "Use soft edge colors", "isSoft", "setSoft"},     //RES NORES,BI_BevelPanel_soft,NORES,NORES
      {"textureName",   "Background tiled texture", "getTextureName", "setTextureName", com.borland.jbcl.editors.FileNameEditor.class.getName()},     //RES NORES,BI_texture,NORES,NORES
      {"toolTipText",   "ToolTip help text", "getToolTipText", "setToolTipText"},     //RES NORES,BI_toolTipText,NORES,NORES
      {"visible",       "Visible state", "isVisible", "setVisible"},     //RES NORES,BI_visible,NORES,NORES
    };
  }
}
