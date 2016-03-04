package com.borland.dbswing;

import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: Softwareschmiede Höffl GmbH
 * </p>
 * 
 * @author unbekannt
 * @version 1.0
 */
public class JdbSpinnerBeanInfo extends SimpleBeanInfo {
  Class<JdbSpinner> beanClass = JdbSpinner.class;
  String iconColor16x16Filename;
  String iconColor32x32Filename;
  String iconMono16x16Filename;
  String iconMono32x32Filename;
  
  public JdbSpinnerBeanInfo() {
  }
  
  public PropertyDescriptor[] getPropertyDescriptors() {
    try {
      PropertyDescriptor _columnName = new PropertyDescriptor("columnName",
          beanClass, "getColumnName", "setColumnName");
      _columnName.setPropertyEditorClass(getClass().forName(
          "com.borland.jbuilder.cmt.editors.ColumnNameEditor"));
      
      PropertyDescriptor _dataSet = new PropertyDescriptor("dataSet",
          beanClass, "getDataSet", "setDataSet");
      
      PropertyDescriptor[] pds = new PropertyDescriptor[] { _columnName,
          _dataSet };
      return pds;
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    }
  }
  
  public Image getIcon(int iconKind) {
    switch (iconKind) {
    case BeanInfo.ICON_COLOR_16x16:
      return ((iconColor16x16Filename != null) ? loadImage(iconColor16x16Filename)
          : null);
      
    case BeanInfo.ICON_COLOR_32x32:
      return ((iconColor32x32Filename != null) ? loadImage(iconColor32x32Filename)
          : null);
      
    case BeanInfo.ICON_MONO_16x16:
      return ((iconMono16x16Filename != null) ? loadImage(iconMono16x16Filename)
          : null);
      
    case BeanInfo.ICON_MONO_32x32:
      return ((iconMono32x32Filename != null) ? loadImage(iconMono32x32Filename)
          : null);
    }
    
    return null;
  }
  
  public BeanInfo[] getAdditionalBeanInfo() {
    Class<?> superclass = beanClass.getSuperclass();
    try {
      BeanInfo superBeanInfo = Introspector.getBeanInfo(superclass);
      return (new BeanInfo[] { superBeanInfo });
    } catch (IntrospectionException introspectionException) {
      introspectionException.printStackTrace();
      return null;
    }
  }
}
