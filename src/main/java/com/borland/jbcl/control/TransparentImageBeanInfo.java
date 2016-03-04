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

public class TransparentImageBeanInfo extends BasicBeanInfo implements java.io.Serializable
{
  public TransparentImageBeanInfo() {
    beanClass = TransparentImage.class;
    namedAttributes = new Object[][] {
      {"isContainer", Boolean.FALSE}} ;
    propertyDescriptors = new String[][] {
      {"alignment",    "Alignment setting", "getAlignment", "setAlignment", com.borland.jbcl.editors.AlignmentEditor.class.getName()},     //RES NORES,BI_alignment,NORES,NORES
      {"background",   "Background color", "getBackground", "setBackground"},     //RES NORES,BI_background,NORES,NORES
      {"drawEdge",     "Show edge", "isDrawEdge", "setDrawEdge"},     //RES NORES,BI_Shape_drawEdge,NORES,NORES
      {"edgeColor",    "Edge color", "getEdgeColor", "setEdgeColor"},     //RES NORES,BI_Shape_edgeColor,NORES,NORES
      {"transparent",  "Transparent painting", "isTransparent", "setTransparent"},     //RES NORES,BI_transparent,NORES,NORES
//      {"image",        Res._BI_image, "getImage", "setImage"},
      {"imageName",    "Image Name", "getImageName", "setImageName", com.borland.jbcl.editors.FileNameEditor.class.getName()},     //RES NORES,BI_imageName,NORES,NORES
//      {"imageURL",     Res._BI_imageURL, "getImageURL", "setImageURL"},
      {"visible",      "Visible state", "isVisible", "setVisible"},     //RES NORES,BI_visible,NORES,NORES
    };
  }
}
