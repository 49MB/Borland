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

package com.borland.jbcl.layout;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class BoxLayout2BeanInfo extends SimpleBeanInfo {
  Class beanClass = BoxLayout2.class;
  String iconColor16x16Filename;
  String iconColor32x32Filename;
  String iconMono16x16Filename;
  String iconMono32x32Filename;

  public BoxLayout2BeanInfo() {
  }
  public PropertyDescriptor[] getPropertyDescriptors() {
      try {
      PropertyDescriptor _axis = new PropertyDescriptor("axis", beanClass, "getAxis", "setAxis");
      _axis.setDisplayName("axis");
      _axis.setShortDescription("major axis");
      _axis.setPropertyEditorClass(com.borland.jbcl.editors.BoxAxisEditor.class);
      PropertyDescriptor[] pds = new PropertyDescriptor[] {
      _axis,
      };
      return pds;
      }
      catch (IntrospectionException e) {
      e.printStackTrace();
      return null;
      }
  }
  public java.awt.Image getIcon(int iconKind) {
      switch (iconKind) {
      case BeanInfo.ICON_COLOR_16x16:
      return iconColor16x16Filename != null ? loadImage(iconColor16x16Filename) : null;
      case BeanInfo.ICON_COLOR_32x32:
      return iconColor32x32Filename != null ? loadImage(iconColor32x32Filename) : null;
      case BeanInfo.ICON_MONO_16x16:
      return iconMono16x16Filename != null ? loadImage(iconMono16x16Filename) : null;
      case BeanInfo.ICON_MONO_32x32:
      return iconMono32x32Filename != null ? loadImage(iconMono32x32Filename) : null;
      }
      return null;
  }
}


