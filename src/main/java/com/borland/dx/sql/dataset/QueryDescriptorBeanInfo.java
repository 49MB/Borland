package com.borland.dx.sql.dataset;

import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: Softwareschmiede Hoeffl GmbH</p>
 *
 * @author unbekannt
 * @version 1.0
 */
public class QueryDescriptorBeanInfo extends SimpleBeanInfo {
  private Class beanClass = QueryDescriptor.class;
  private String iconColor16x16Filename;
  private String iconColor32x32Filename;
  private String iconMono16x16Filename;
  private String iconMono32x32Filename;

  public QueryDescriptorBeanInfo() {
  }

  public PropertyDescriptor[] getPropertyDescriptors() {
    try {
      PropertyDescriptor _asynchronousExecution = new PropertyDescriptor(
	  "asynchronousExecution", beanClass, "isAsynchronousExecution",
	  "setAsynchronousExecution");

      PropertyDescriptor _database = new PropertyDescriptor("database",
	  beanClass, "getDatabase", "setDatabase");

      PropertyDescriptor _executeOnOpen = new PropertyDescriptor(
	  "executeOnOpen", beanClass, "isExecuteOnOpen", "setExecuteOnOpen");

      PropertyDescriptor _loadOption = new PropertyDescriptor("loadOption",
	  beanClass, "getLoadOption", "setLoadOption");

      PropertyDescriptor _parameterRow = new PropertyDescriptor("parameterRow",
	  beanClass, "getParameterRow", null);

      PropertyDescriptor _query = new PropertyDescriptor("query", beanClass,
	  "getQuery", "setQuery");

      PropertyDescriptor _queryString = new PropertyDescriptor("queryString",
	  beanClass, "getQueryString", null);

      PropertyDescriptor[] pds = new PropertyDescriptor[] {
				 _asynchronousExecution, _database,
				 _executeOnOpen, _loadOption, _parameterRow,
				 _query, _queryString
      };
      return pds;
    }
    catch (Exception exception) {
      exception.printStackTrace();
      return null;
    }
  }

  public Image getIcon(int iconKind) {
    switch (iconKind) {
      case BeanInfo.ICON_COLOR_16x16:
	return ((iconColor16x16Filename != null) ?
		loadImage(iconColor16x16Filename) : null);

      case BeanInfo.ICON_COLOR_32x32:
	return ((iconColor32x32Filename != null) ?
		loadImage(iconColor32x32Filename) : null);

      case BeanInfo.ICON_MONO_16x16:
	return ((iconMono16x16Filename != null) ?
		loadImage(iconMono16x16Filename) : null);

      case BeanInfo.ICON_MONO_32x32:
	return ((iconMono32x32Filename != null) ?
		loadImage(iconMono32x32Filename) : null);
    }

    return null;
  }

  public BeanInfo[] getAdditionalBeanInfo() {
    Class superclass = beanClass.getSuperclass();
    try {
      BeanInfo superBeanInfo = Introspector.getBeanInfo(superclass);
      return (new BeanInfo[] {superBeanInfo});
    }
    catch (IntrospectionException introspectionException) {
      introspectionException.printStackTrace();
      return null;
    }
  }
}