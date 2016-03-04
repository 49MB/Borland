package com.borland.dx.dataset;

import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
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
public class DataSetBeanInfo extends SimpleBeanInfo {
  private final Class beanClass = DataSet.class;
  private String iconColor16x16Filename;
  private String iconColor32x32Filename;
  private String iconMono16x16Filename;
  private String iconMono32x32Filename;

  public DataSetBeanInfo() {
  }

  @Override
  public PropertyDescriptor[] getPropertyDescriptors() {
    try {
      PropertyDescriptor _defaultValues = new PropertyDescriptor(
	  "defaultValues", beanClass, null, "setDefaultValues");

      PropertyDescriptor _detailDataSetWithFetchAsNeeded = new
	  PropertyDescriptor("detailDataSetWithFetchAsNeeded", beanClass,
	      "isDetailDataSetWithFetchAsNeeded", null);

      PropertyDescriptor _details = new PropertyDescriptor("details", beanClass,
	  "getDetails", null);

      PropertyDescriptor _displayErrors = new PropertyDescriptor(
	  "displayErrors", beanClass, "isDisplayErrors", "setDisplayErrors");

      PropertyDescriptor _displayVariant = new IndexedPropertyDescriptor(
	  "displayVariant", beanClass, null, null, null, "setDisplayVariant");

      PropertyDescriptor _editable = new PropertyDescriptor("editable",
	  beanClass, "isEditable", "setEditable");

      PropertyDescriptor _editing = new PropertyDescriptor("editing", beanClass,
	  "isEditing", null);

      PropertyDescriptor _editingNewRow = new PropertyDescriptor(
	  "editingNewRow", beanClass, "isEditingNewRow", null);

      PropertyDescriptor _empty = new PropertyDescriptor("empty", beanClass,
	  "isEmpty", null);

      PropertyDescriptor _enableDataSetEvents = new PropertyDescriptor(
	  "enableDataSetEvents", beanClass, "isEnableDataSetEvents", null);

      PropertyDescriptor _enableDelete = new PropertyDescriptor("enableDelete",
	  beanClass, "isEnableDelete", "setEnableDelete");

      PropertyDescriptor _enableInsert = new PropertyDescriptor("enableInsert",
	  beanClass, "isEnableInsert", "setEnableInsert");

      PropertyDescriptor _enableUpdate = new PropertyDescriptor("enableUpdate",
	  beanClass, "isEnableUpdate", "setEnableUpdate");

      PropertyDescriptor _internalRow = new PropertyDescriptor("internalRow",
	  beanClass, "getInternalRow", null);

      PropertyDescriptor _lastColumnVisited = new PropertyDescriptor(
	  "lastColumnVisited", beanClass, "getLastColumnVisited",
	  "setLastColumnVisited");

      PropertyDescriptor _masterLink = new PropertyDescriptor("masterLink",
	  beanClass, "getMasterLink", "setMasterLink");

      PropertyDescriptor _open = new PropertyDescriptor("open", beanClass,
	  "isOpen", null);

      PropertyDescriptor _openMonitor = new PropertyDescriptor("openMonitor",
	  beanClass, "getOpenMonitor", null);

      PropertyDescriptor _postUnmodifiedRow = new PropertyDescriptor(
	  "postUnmodifiedRow", beanClass, "isPostUnmodifiedRow",
	  "setPostUnmodifiedRow");

      PropertyDescriptor _row = new PropertyDescriptor("row", beanClass,
	  "getRow", null);

      PropertyDescriptor _rowCount = new PropertyDescriptor("rowCount",
	  beanClass, "getRowCount", null);

      PropertyDescriptor _rowDirty = new PropertyDescriptor("rowDirty",
	  beanClass, "isRowDirty", "setRowDirty");

      PropertyDescriptor _rowFilterListeners = new PropertyDescriptor(
	  "rowFilterListeners", beanClass, "getRowFilterListeners", null);

      PropertyDescriptor _schemaName = new PropertyDescriptor("schemaName",
	  beanClass, "getSchemaName", null);

      PropertyDescriptor _sort = new PropertyDescriptor("sort", beanClass,
	  "getSort", "setSort");

      PropertyDescriptor _status = new PropertyDescriptor("status", beanClass,
	  "getStatus", null);

      PropertyDescriptor _storageDataSet = new PropertyDescriptor(
	  "storageDataSet", beanClass, "getStorageDataSet", null);

      PropertyDescriptor _tableName = new PropertyDescriptor("tableName",
	  beanClass, "getTableName", null);

      PropertyDescriptor[] pds = new PropertyDescriptor[] {
				 _defaultValues,
				 _detailDataSetWithFetchAsNeeded, _details,
				 _displayErrors, _displayVariant, _editable,
				 _editing, _editingNewRow, _empty,
				 _enableDataSetEvents, _enableDelete,
				 _enableInsert, _enableUpdate, _internalRow,
				 _lastColumnVisited, 
				 _masterLink, _open, _openMonitor,
				 _postUnmodifiedRow, _row, _rowCount, _rowDirty,
				 _rowFilterListeners, _schemaName, _sort,
				 _status, _storageDataSet, _tableName
      };
      return pds;
    }
    catch (Exception exception) {
      exception.printStackTrace();
      return null;
    }
  }

  @Override
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

  @Override
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