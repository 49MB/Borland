package com.borland.dbswing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;

import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.ColumnAware;
import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.Variant;

/**
 * <p>
 * Title:
 * </p>
 * Jdb Spinner
 * 
 * <p>
 * Description:
 * </p>
 * Erweiterung von JSpinner um Datenbanksensitivität
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
public class JdbSpinner extends JSpinner implements ColumnAware,
    java.io.Serializable, PropertyChangeListener {
  JFormattedTextField textControl;
  boolean ignoreStateChange;
  int modelType = -1;
  boolean addCalled = false;
  /**
   * <p>
   * Returns the <code>DBTextDataBinder</code> that makes this a data-aware
   * component.
   * </p>
   */
  protected DBTextDataBinder dataBinder;
  
  public JdbSpinner() {
    super();
    commonInit();
  }
  
  protected Integer variantToInt(Variant v) {
    if (v == null || v.isNull()) {
      return null;
    } else {
      return new Integer(v.getAsInt());
    }
  }
  
  protected Double variantToDouble(Variant d) {
    if (d == null || d.isNull()) {
      return null;
    } else {
      return new Double(d.getDouble());
    }
  }
  
  public void setupModel() {
    DataSet dataSet = getDataSet();
    if (dataSet != null) {
      Column col = dataSet.hasColumn(getColumnName());
      if (col != null) {
        int oldModelType = modelType;
        int newModelType = col.getDataType();
        if (oldModelType != newModelType) {
          modelType = newModelType;
          setupModel(col, newModelType);
        }
      }
    }
  }
  
  public void setupModel(Column col, int newModelType) {
    Variant min = col.getMinValue();
    Variant max = col.getMaxValue();
    switch (newModelType) {
    case Variant.INT:
    case Variant.BYTE:
    case Variant.SHORT:
    case Variant.LONG:
      setModel(new SpinnerNumberModel(new Integer(0), variantToInt(min),
          variantToInt(max), new Integer(1)));
      break;
    
    case Variant.DATE:
    case Variant.TIMESTAMP:
      setModel(new SpinnerDateModel(null, null, null, 1));
      break;
    
    case Variant.FLOAT:
    case Variant.DOUBLE:
    case Variant.BIGDECIMAL:
      setModel(new SpinnerNumberModel(new Double(0), variantToDouble(min),
          variantToDouble(max), new Double(1)));
      break;
    
    // Not Supportet: case Variant.STRING:
    // setModel(new SpinnerListModel());
    
    default:
      throw new IllegalStateException("JdbSpinner: DataType "
          + Variant.typeName(col.getDataType()) + " is not supportet!");
      
      /*
       * case Variant.TIME: case Variant.BOOLEAN: case Variant.BINARY_STREAM:
       * case Variant.INPUTSTREAM: case Variant.OBJECT: case Variant.BYTE_ARRAY:
       */
    }
  }
  
  @Override
  public void addNotify() {
    super.addNotify();
    addCalled = true;
    modelSetupInd();
  }
  
  @Override
  public void removeNotify() {
    super.removeNotify();
    addCalled = false;
  }
  
  private void modelSetupInd() {
    if (addCalled) {
      ignoreStateChange = true;
      try {
        setupModel();
      } finally {
        ignoreStateChange = false;
      }
    }
  }
  
  /**
   * <p>
   * Used to initialize <code>JdbTextPane</code> with the same defaults,
   * regardless of the constructor used. A newly instantiated
   * <code>JdbTextPane</code> differs from a <code>JTextPane</code> in that it
   * has a non-blinking cursor.
   * </p>
   */
  protected void commonInit() {
    textControl = ((DefaultEditor) getEditor()).getTextField();
    dataBinder = new DBTextDataBinder(textControl);
    addPropertyChangeListener(this);
  }
  
  /**
   * <p>
   * Sets the <code>DataSet</code> from which values are read.
   * </p>
   * 
   * @param dataSet
   *          The <code>DataSet</code>.
   * @see #getDataSet
   */
  public void setDataSet(DataSet dataSet) {
    dataBinder.setDataSet(dataSet);
  }
  
  /**
   * <p>
   * Returns the <code>DataSet</code> from which values are read.
   * </p>
   * 
   * @return The <code>DataSet</code>.
   * @see #setDataSet
   */
  public DataSet getDataSet() {
    if (dataBinder == null) {
      return null;
    }
    return dataBinder.getDataSet();
  }
  
  /**
   * <p>
   * Sets the column name of the <code>DataSet</code> from which values are
   * read.
   * </p>
   * 
   * @param columnName
   *          The column name.
   * @see #getColumnName
   * @see #setDataSet
   */
  public void setColumnName(String columnName) {
    dataBinder.setColumnName(columnName);
  }
  
  /**
   * <p>
   * Returns the column name of the <code>DataSet</code> from which values are
   * read.
   * </p>
   * 
   * @return The column name.
   * @see #setColumnName
   * @see #getDataSet
   */
  public String getColumnName() {
    if (dataBinder == null) {
      return null;
    }
    return dataBinder.getColumnName();
  }
  
  // used by DBTextDataBinder actions to get the dataBinder
  /**
   * <p>
   * Returns the <code>DBTextDataBinder</code> that makes this a data-aware
   * component.
   * </p>
   * 
   * @return the <code>DBTextDataBinder</code> that makes this a data-aware
   *         component.
   */
  DBTextDataBinder getDataBinder() {
    return dataBinder;
  }
  
  public JFormattedTextField getTextControl() {
    return textControl;
  }
  
  public void setTextControl(JFormattedTextField textControl) {
    this.textControl = textControl;
    dataBinder.setJTextComponent(textControl);
  }
  
  @Override
  public void setEditor(JComponent editor) {
    super.setEditor(editor);
    setTextControl(((DefaultEditor) editor).getTextField());
  }
  
  @Override
  protected void fireStateChanged() {
    if (!ignoreStateChange) {
      super.fireStateChanged();
    }
  }
  
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals("editor")) {
      Object editor = evt.getNewValue();
      if (editor instanceof DefaultEditor)
        setTextControl(((DefaultEditor) editor).getTextField());
    }
  }
  
  /**
   * <p>
   * Sets whether pressing <kbd>Enter</kbd> automatically moves focus to the
   * next focusable field. The default value is <code>true</code>.
   * </p>
   * 
   * @param nextFocusOnEnter
   *          If <code>true</code>, pressing <kbd>Enter</kbd> automatically
   *          moves focus to the next focusable field.
   * @see #isNextFocusOnEnter
   */
  public void setNextFocusOnEnter(boolean nextFocusOnEnter) {
    dataBinder.setNextFocusOnEnter(nextFocusOnEnter);
  }
  
  /**
   * <p>
   * Returns whether pressing <kbd>Enter</kbd> automatically moves focus to the
   * next focusable field.
   * </p>
   * 
   * @return If <code>true</code>, pressing <kbd>Enter</kbd> automatically moves
   *         focus to the next focusable field.
   * @see #isNextFocusOnEnter
   */
  public boolean isNextFocusOnEnter() {
    return dataBinder.isNextFocusOnEnter();
  }
  
}
