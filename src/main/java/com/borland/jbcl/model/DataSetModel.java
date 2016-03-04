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
package com.borland.jbcl.model;

import java.awt.Component;
import java.awt.Frame;
import java.io.InputStream;

import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.ColumnPaintListener;
import com.borland.dx.dataset.ColumnVariant;
import com.borland.dx.dataset.DataChangeListener;
import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.DataSetException;
import com.borland.dx.dataset.ExceptionEvent;
import com.borland.dx.dataset.MatrixData;
import com.borland.dx.dataset.NavigationListener;
import com.borland.dx.dataset.PickListDescriptor;
import com.borland.dx.dataset.ReadRow;
import com.borland.dx.dataset.Variant;
import com.borland.dx.text.ItemEditMask;
import com.borland.jbcl.control.ExceptionDialog;
import com.borland.jbcl.control.MaskableTextItemEditor;
import com.borland.jbcl.control.PickListItemEditor;
import com.borland.jbcl.view.BooleanToggleItemEditor;
import com.borland.jbcl.view.CheckboxItemPainter;
import com.borland.jbcl.view.CustomItemEditor;
import com.borland.jbcl.view.CustomItemPainter;
import com.borland.jbcl.view.FocusableItemPainter;
import com.borland.jbcl.view.ImageItemEditor;
import com.borland.jbcl.view.ImageItemPainter;
import com.borland.jbcl.view.SelectableItemPainter;
import com.borland.jbcl.view.TextItemPainter;

/**
 */
public class DataSetModel
{
  DataSetModel(DataSet dataSet, Column column, Component component)
  {
    this.dataSet   = dataSet;
    this.column    = column;
    this.component = component;
    this.value     = new ColumnVariant(column, dataSet);

    editor  = (ItemEditor)column.getItemEditor();
    painter = (ItemPainter)column.getItemPainter();
  }

  DataSet getDataSet() {
    return dataSet;
  }

  Column getColumn() {
    return column;
  }

  ItemPainter getPainter(Object value) {
    return getPainter(dataSet.getRow(), value);
  }

  ItemPainter getPainter(int row, Object object) {
    if (painter == null) {
      switch (column.getDataType()) {
        case Variant.BOOLEAN:
          painter = new CheckboxItemPainter();
          break;
        case Variant.INPUTSTREAM: 
          painter = new ImageItemPainter();
          break;
        case Variant.OBJECT:  
          painter = new TextItemPainter(column.getFormatter());
//          Diagnostic.fail();
          break;
        default: 
          painter = new TextItemPainter(column.getFormatter());
          break;
      }
      paintListener = column.getColumnPaintListener();
      if (paintListener != null) {
        customPainter = new CustomItemPainter(new SelectableItemPainter(painter));
        painter       = new FocusableItemPainter(customPainter);
      }
      else
        painter = new FocusableItemPainter(new SelectableItemPainter(painter));
    }

    if (paintListener != null) {
      if (customPainter == null) {
        customPainter = new CustomItemPainter(new SelectableItemPainter(painter));
        painter       = new FocusableItemPainter(customPainter);
      }
      customPainter.reset();
      paintListener.painting(dataSet, column, row, (Variant) object, customPainter);
    }

    return painter;
  }

  ItemEditor getEditor() {
    if (editor == null) {
      switch (column.getDataType()) {
        case Variant.BOOLEAN:
          editor = new BooleanToggleItemEditor();
          break;
        case Variant.INPUTSTREAM: 
          editor  = new ImageItemEditor();
          break;
        default: 
          PickListDescriptor pickList = column.getPickList();
          if (pickList != null) {
            PickListItemEditor pickListEditor = new PickListItemEditor();
            pickListEditor.setCachePickList(true);
            editor  = pickListEditor;
          }
          else {
            int precision = -1;
            ItemEditMask  mask  = column.getEditMasker();
            if (column.getDataType() == Variant.STRING && mask == null)
              precision = column.getPrecision();
            editor = new MaskableTextItemEditor(column.getFormatter(), mask);
          }
          break;
      }
      paintListener = column.getColumnPaintListener();
      if (paintListener != null) {
        customEditor  = new CustomItemEditor(editor);
        editor        = customEditor;
      }
    }
    if (paintListener != null) {
      if (customEditor == null) {
        customEditor  = new CustomItemEditor(editor);
        editor        = customEditor;
      }
      customEditor.reset();
      paintListener.editing(dataSet, column, customEditor);
    }
    return editor;
  }

  final void addDataChangeListener(DataChangeListener listener) {
    dataSet.addDataChangeListener(listener);
  }

  final void addNavigationListener(NavigationListener listener) {
    this.dataSet.addNavigationListener(listener);
  }

  final void handleThisException(Exception ex) {
    handleException(dataSet, component, ex);
  }

  final Variant get() {
    try {
      dataSet.getDisplayVariant(column.getOrdinal(), dataSet.getRow(), value);
      return value;
    }
    catch (Exception ex) {
      handleThisException(ex);
      value.setUnassignedNull();
      return value;
    }
  }

  final Variant get(int row) {
    if (column == null)
      return null;
    try {
      dataSet.getDisplayVariant(column.getOrdinal(), row, value);
      return value;
    }
    catch (Exception ex) {
      handleThisException(ex);
      value.setUnassignedNull();
      return value;
    }
  }

  final int getRowCount() {
    try {
      return dataSet.getRowCount();
    }
    catch (Exception ex) {
      handleThisException(ex);
      return 0;
    }
  }


  final boolean canSet(boolean startEdit) {
    return canSet(dataSet.getRow(), startEdit);
  }

  final boolean canSet(int row, boolean startEdit) {
    if (!column.isEditable())
      return false;
    try {
      if (startEdit)
        dataSet.startEdit(column);
      else
        dataSet.startEditCheck(column);
      return true;
    }
    catch(DataSetException ex) {
      handleThisException(ex);
    }
    return false;
  }

  private final void setVariant(Variant value)
    throws DataSetException
  {

    int ordinal = column.getOrdinal();

//    if (column.lookup != null && value.getType() != column.getDataType())
      dataSet.setDisplayVariant(ordinal, value);
//    else
//      dataSet.setVariant(ordinal, value);
  }

  final void set(Object data) {
    try {
      if (data instanceof Variant) {
        setVariant((Variant)data);
      }
      else if (data instanceof InputStream) {
        dataSet.setInputStream(column.getOrdinal(), (InputStream)data);
      }
      else if (data instanceof ReadRow && column.getPickList() != null) {
        // In this case, assume that the pickList DataSet has been positioned by the
        // caller to the row to copy into dataSet.
        //
        PickListDescriptor pickList = column.getPickList();
        ReadRow.copyTo(pickList.getPickListColumns(), (ReadRow)data, pickList.getDestinationColumns(), dataSet);
      }
      else if (data != null) {
        column.getFormatter().parse(data.toString(), value);
        setVariant((Variant) value);
      }
      else {
        value.setAssignedNull();
        dataSet.setVariant(column.getOrdinal(), value);
      }
    }
    catch (Exception ex) {
      handleThisException(ex);                    // this logs exception to listeners
      throw new IllegalArgumentException();   // and this tells client the set() failed
    }
  }

  final void set(int row, Object data) {
      if (row != dataSet.getRow()) {
        try {
          dataSet.goToRow(row);
        }
        catch (DataSetException ex) {
          handleThisException(ex);
          return;
        }
      }
      set(data);
  }

  void addRow(int aheadOf) {
    try {
      if (aheadOf != dataSet.getRow())
        dataSet.goToRow(aheadOf);
      dataSet.insertRow(true);
    }
    catch (Exception ex) {
      handleThisException(ex);
    }
  }

  final void addRow() {
    try {
      if (!dataSet.atLast()) {
        if (dataSet.getStorageDataSet().provideMoreData())
          return;
        else
          dataSet.last();
      }
      dataSet.insertRow(false);
    }
    catch (Exception ex) {
      handleThisException(ex);
    }
  }

  final void removeRow(int row) {
    try {
      if (row != dataSet.getRow())
        dataSet.goToRow(row);
      dataSet.deleteRow();
    }
    catch (Exception ex) {
      handleThisException(ex);
    }
  }

  private static final Frame getFrame(Component component) {
    Component parent  = component;
    while (parent != null) {
      if (parent instanceof Frame)
        return (Frame) parent;
      parent  =  parent.getParent();
    }
    return new Frame();
  }

  public static final void handleException(DataSet dataSet, Component component, Exception ex, boolean modal) {
    Frame frame = getFrame(component);

    //Diagnostic.println("showCount:  "+ExceptionDialog.getShowCount());
    //Diagnostic.println("displayError:  "+dataSet.displayError(ex));
    //Diagnostic.println("Frame:  "+getFrame(component));

    if (DataSetException.getExceptionListeners() != null) {
      DataSetException.getExceptionListeners().dispatch(new ExceptionEvent(dataSet, component, ex));
    }
    else if ((dataSet == null || MatrixData.displayError(dataSet, ex)) && frame != null && ExceptionDialog.getShowCount() < 1) {
      new ExceptionDialog(frame, Res._Error, ex, modal).setVisible(true);     
    }
  }

  public static final void handleException(DataSet dataSet, Component component, Exception ex) {
    handleException(dataSet, component, ex, false);
  }

  public static final void handleException(Component component, Exception ex, boolean modal) {
    handleException(null, component, ex, modal);
  }

  public static final void handleException(Exception ex) {
    handleException(null, null, ex);
  }

  public static final void handleException(Exception ex, boolean modal) {
    handleException(null, null, ex, modal);
  }

  private DataSet             dataSet;
  private Column              column;
  private Component           component;
  private ItemPainter         painter;
  private CustomItemPainter   customPainter;
  private CustomItemEditor    customEditor;
  private ColumnPaintListener paintListener;
  private ItemEditor          editor;
  private ColumnVariant       value;
}
