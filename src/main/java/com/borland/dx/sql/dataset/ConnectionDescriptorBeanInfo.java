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
public class ConnectionDescriptorBeanInfo extends SimpleBeanInfo {
  private Class beanClass = ConnectionDescriptor.class;
  private String iconColor16x16Filename;
  private String iconColor32x32Filename;
  private String iconMono16x16Filename;
  private String iconMono32x32Filename;

  public ConnectionDescriptorBeanInfo() {
  }

  public PropertyDescriptor[] getPropertyDescriptors() {
    try {
      PropertyDescriptor _complete = new PropertyDescriptor("complete",
	  beanClass, "isComplete", null);

      PropertyDescriptor _connectionURL = new PropertyDescriptor(
	  "connectionURL", beanClass, "getConnectionURL", "setConnectionURL");

      PropertyDescriptor _driver = new PropertyDescriptor("driver", beanClass,
	  "getDriver", "setDriver");

      PropertyDescriptor _password = new PropertyDescriptor("password",
	  beanClass, "getPassword", "setPassword");

      PropertyDescriptor _promptPassword = new PropertyDescriptor(
	  "promptPassword", beanClass, "isPromptPassword", "setPromptPassword");

      PropertyDescriptor _properties = new PropertyDescriptor("properties",
	  beanClass, "getProperties", "setProperties");

      PropertyDescriptor _userName = new PropertyDescriptor("userName",
	  beanClass, "getUserName", "setUserName");

      PropertyDescriptor[] pds = new PropertyDescriptor[] {
				 _complete, _connectionURL, _driver, _password,
				 _promptPassword, _properties, _userName
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