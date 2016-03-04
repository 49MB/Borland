package com.borland.dx.dataset;

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
public class ColumnBeanInfo extends SimpleBeanInfo {
  private Class beanClass = Column.class;
  private String iconColor16x16Filename;
  private String iconColor32x32Filename;
  private String iconMono16x16Filename;
  private String iconMono32x32Filename;

  public ColumnBeanInfo() {
  }

  public PropertyDescriptor[] getPropertyDescriptors() {
    try {
      PropertyDescriptor _agg = new PropertyDescriptor("agg", beanClass,
          "getAgg", "setAgg");

      PropertyDescriptor _alignment = new PropertyDescriptor("alignment",
          beanClass, "getAlignment", "setAlignment");

      PropertyDescriptor _autoIncrement = new PropertyDescriptor(
          "autoIncrement", beanClass, "isAutoIncrement", "setAutoIncrement");

      PropertyDescriptor _background = new PropertyDescriptor("background",
          beanClass, "getBackground", "setBackground");

      PropertyDescriptor _calcType = new PropertyDescriptor("calcType",
          beanClass, "getCalcType", "setCalcType");

      PropertyDescriptor _caption = new PropertyDescriptor("caption", beanClass,
          "getCaption", "setCaption");

      PropertyDescriptor _clusteredOrAutoIncrement = new PropertyDescriptor(
          "clusteredOrAutoIncrement", beanClass, "isClusteredOrAutoIncrement", null);

      PropertyDescriptor _columnChangeListeners = new PropertyDescriptor(
          "columnChangeListeners", beanClass, "getColumnChangeListeners",
          "setColumnChangeListeners");

      PropertyDescriptor _columnName = new PropertyDescriptor("columnName",
          beanClass, "getColumnName", "setColumnName");

      PropertyDescriptor _columnPaintListener = new PropertyDescriptor(
          "columnPaintListener", beanClass, "getColumnPaintListener", null);

      PropertyDescriptor _currency = new PropertyDescriptor("currency",
          beanClass, "isCurrency", "setCurrency");

      PropertyDescriptor _dataSet = new PropertyDescriptor("dataSet", beanClass,
          "getDataSet", null);

      PropertyDescriptor _dataType = new PropertyDescriptor("dataType",
          beanClass, "getDataType", "setDataType");

      PropertyDescriptor _default = new PropertyDescriptor("default", beanClass,
          "getDefault", "setDefault");

      PropertyDescriptor _defaultValue = new PropertyDescriptor("defaultValue",
          beanClass, "getDefaultValue", "setDefaultValue");

      PropertyDescriptor _displayMask = new PropertyDescriptor("displayMask",
          beanClass, "getDisplayMask", "setDisplayMask");

      PropertyDescriptor _editable = new PropertyDescriptor("editable",
          beanClass, "isEditable", "setEditable");

      PropertyDescriptor _editMask = new PropertyDescriptor("editMask",
          beanClass, "getEditMask", "setEditMask");

      PropertyDescriptor _editMasker = new PropertyDescriptor("editMasker",
          beanClass, "getEditMasker", "setEditMasker");

      PropertyDescriptor _exportDisplayMask = new PropertyDescriptor(
          "exportDisplayMask", beanClass, "getExportDisplayMask",
          "setExportDisplayMask");

      PropertyDescriptor _exportFormatter = new PropertyDescriptor(
          "exportFormatter", beanClass, "getExportFormatter",
          "setExportFormatter");

      PropertyDescriptor _fixedPrecision = new PropertyDescriptor(
          "fixedPrecision", beanClass, "isFixedPrecision", "setFixedPrecision");

      PropertyDescriptor _font = new PropertyDescriptor("font", beanClass,
          "getFont", "setFont");

      PropertyDescriptor _foreground = new PropertyDescriptor("foreground",
          beanClass, "getForeground", "setForeground");

      PropertyDescriptor _formatter = new PropertyDescriptor("formatter",
          beanClass, "getFormatter", "setFormatter");

      PropertyDescriptor _hash = new PropertyDescriptor("hash", beanClass,
          "getHash", null);

      PropertyDescriptor _hidden = new PropertyDescriptor("hidden", beanClass,
          "isHidden", "setHidden");

      PropertyDescriptor _itemEditor = new PropertyDescriptor("itemEditor",
          beanClass, "getItemEditor", "setItemEditor");

      PropertyDescriptor _itemPainter = new PropertyDescriptor("itemPainter",
          beanClass, "getItemPainter", "setItemPainter");

      PropertyDescriptor _javaClass = new PropertyDescriptor("javaClass",
          beanClass, "getJavaClass", "setJavaClass");

      PropertyDescriptor _locale = new PropertyDescriptor("locale", beanClass,
          "getLocale", "setLocale");

      PropertyDescriptor _max = new PropertyDescriptor("max", beanClass,
          "getMax", "setMax");

      PropertyDescriptor _maxInline = new PropertyDescriptor("maxInline",
          beanClass, "getMaxInline", "setMaxInline");

      PropertyDescriptor _maxValue = new PropertyDescriptor("maxValue",
          beanClass, "getMaxValue", "setMaxValue");

      PropertyDescriptor _min = new PropertyDescriptor("min", beanClass,
          "getMin", "setMin");

      PropertyDescriptor _minValue = new PropertyDescriptor("minValue",
          beanClass, "getMinValue", "setMinValue");

      PropertyDescriptor _ordinal = new PropertyDescriptor("ordinal", beanClass,
          "getOrdinal", null);

      PropertyDescriptor _parameterType = new PropertyDescriptor(
          "parameterType", beanClass, "getParameterType", "setParameterType");

      PropertyDescriptor _persist = new PropertyDescriptor("persist", beanClass,
          "isPersist", "setPersist");

      PropertyDescriptor _pickList = new PropertyDescriptor("pickList",
          beanClass, "getPickList", "setPickList");

      PropertyDescriptor _precision = new PropertyDescriptor("precision",
          beanClass, "getPrecision", "setPrecision");

      PropertyDescriptor _precisionSet = new PropertyDescriptor("precisionSet",
          beanClass, "isPrecisionSet", null);

      PropertyDescriptor _preferredOrdinal = new PropertyDescriptor(
          "preferredOrdinal", beanClass, "getPreferredOrdinal",
          "setPreferredOrdinal");

      PropertyDescriptor _primaryKey = new PropertyDescriptor("primaryKey",
          beanClass, "isPrimaryKey", null);

      PropertyDescriptor _readOnly = new PropertyDescriptor("readOnly",
          beanClass, "isReadOnly", "setReadOnly");

      PropertyDescriptor _required = new PropertyDescriptor("required",
          beanClass, "isRequired", "setRequired");

      PropertyDescriptor _resolvable = new PropertyDescriptor("resolvable",
          beanClass, "isResolvable", "setResolvable");

      PropertyDescriptor _rowId = new PropertyDescriptor("rowId", beanClass,
          "isRowId", "setRowId");

      PropertyDescriptor _scale = new PropertyDescriptor("scale", beanClass,
          "getScale", "setScale");

      PropertyDescriptor _scaleSet = new PropertyDescriptor("scaleSet",
          beanClass, "isScaleSet", null);

      PropertyDescriptor _schemaName = new PropertyDescriptor("schemaName",
          beanClass, "getSchemaName", "setSchemaName");

      PropertyDescriptor _searchable = new PropertyDescriptor("searchable",
          beanClass, "isSearchable", "setSearchable");

      PropertyDescriptor _serverColumnName = new PropertyDescriptor(
          "serverColumnName", beanClass, "getServerColumnName",
          "setServerColumnName");

      PropertyDescriptor _sortable = new PropertyDescriptor("sortable",
          beanClass, "isSortable", null);

      PropertyDescriptor _sortPrecision = new PropertyDescriptor(
          "sortPrecision", beanClass, "getSortPrecision", "setSortPrecision");

      PropertyDescriptor _sqlType = new PropertyDescriptor("sqlType", beanClass,
          "getSqlType", "setSqlType");

      PropertyDescriptor _tableName = new PropertyDescriptor("tableName",
          beanClass, "getTableName", "setTableName");

      PropertyDescriptor _textual = new PropertyDescriptor("textual", beanClass,
          "isTextual", null);

      PropertyDescriptor _visible = new PropertyDescriptor("visible", beanClass,
          "isVisible", "setVisible");

      PropertyDescriptor _width = new PropertyDescriptor("width", beanClass,
          "getWidth", "setWidth");

      PropertyDescriptor _widthSet = new PropertyDescriptor("widthSet",
          beanClass, "isWidthSet", null);

      PropertyDescriptor[] pds = new PropertyDescriptor[] {
                                 _agg, _alignment, _autoIncrement, _background,
                                 _calcType, _caption, _clusteredOrAutoIncrement,
                                 _columnChangeListeners, _columnName,
                                 _columnPaintListener, _currency, _dataSet,
                                 _dataType, _default, _defaultValue,
                                 _displayMask, _editable, _editMask,
                                 _editMasker, _exportDisplayMask,
                                 _exportFormatter, _fixedPrecision, _font,
                                 _foreground, _formatter, _hash, _hidden,
                                 _itemEditor, _itemPainter, _javaClass, _locale,
                                 _max, _maxInline, _maxValue, _min, _minValue,
                                 _ordinal, _parameterType, _persist, _pickList,
                                 _precision, _precisionSet, _preferredOrdinal,
                                 _primaryKey, _readOnly, _required, _resolvable,
                                 _rowId, _scale, _scaleSet, _schemaName,
                                 _searchable, _serverColumnName, _sortable,
                                 _sortPrecision, _sqlType, _tableName, _textual,
                                 _visible, _visible, _width, _widthSet
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
