//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.HashMap;

import com.borland.jb.util.Diagnostic;
import com.borland.jb.util.ErrorResponse;

public class LookupDefault implements Lookup, AccessListener, EditListener {

  public LookupDefault(StorageDataSet dataSet, Column column,
      PickListDescriptor pickList)
  /*-throws DataSetException-*/
  {
    this.pickList = pickList;
    this.column = column;
    this.dataSet = dataSet;
    this.calcField = (column.getCalcType() == CalcType.LOOKUP);
  }

  @Override
  public boolean isCalcField() {
    return calcField;
  }

  private final boolean _init() {
    try {
      destinationColumns = pickList.getDestinationColumns();
      pickListColumns = pickList.getPickListColumns();
      pickListDataSet = pickList.getPickListDataSet();
      if ((pickListDataSet.isOpen() && !pickListDataSet.openComplete))
        return false;

      if (pickListDataSet.getStorageDataSet() != null && pickListDataSet.getStorageDataSet().isLoading()
          && pickListDataSet.getColumnCount() == 0)
        return false;
      pickListDataSet.open();
      if (pickListDataSet.getColumnCount() == 0) {
        pickListDataSet.close();
        pickListDataSet.open();
      }
      if (pickListDataSet.getColumnCount() == 0) {
        System.out.println("Dataset ohne Columns?!? "
            + pickListDataSet.getTableName());
        return false;
      }
      pickListRow = new DataRow(pickListDataSet, pickListColumns);
      destRow = new DataRow(dataSet, destinationColumns);

      Column t = pickListDataSet.getColumn(pickList.getLookupDisplayColumn());
      displayOrdinal = t.getOrdinal();
      displayDataType = t.getDataType();
      return true;
    } catch (Throwable ex) {
      ex.printStackTrace();
      return false;
    }
  }

  private final synchronized boolean init()
  /*-throws DataSetException-*/
  {
    if (destinationColumns == null || pickListRow == null) {
      if (!_init())
        return false;
    }

    pickListDataSet.open();

    if (!pickListRow.isCompatibleList(pickListDataSet)
        || !destRow.isCompatibleList(dataSet)) {
      if (!_init())
        return false;
    }
    // SS: 17.4.07: loadRow/CalcFields Problem !!
    // pickListDataSet.dataSetStore.closeProvider(true);
    setupCache();
    return true;
  }

  @Override
  public void close() {
    if (lookupCache != null) {
      lookupCache.clear();
      lookupCache = null;
      pickListDataSet.removeAccessListener(this);
      if (pickListDataSet instanceof TableDataSet) {
        ((TableDataSet) pickListDataSet).removeEditListener(this);
      }
      // System.out.println("Lookup removed: " + pickListDataSet);
    }
  }

  @Override
  public void lookup(DataSet dataSet, int row, Variant value)
  /*-throws DataSetException-*/
  {
    if (!init())
      return;
    try {
      // SS: Diagnostic.println("lookup: "+dataSet.getTableName()+" row:"+row);
      // SS: Zusätzliches Check bei leren Dataset
      value.setUnassignedNull();
      if (row <= dataSet.index.lastRow()) {
        dataSet.getDataRow(row, destRow);

        CacheItem key = new CacheItem(destinationColumns, destRow); // SS: Ge
        // 'cashed'tes
        // Lookup!

        if (key.isNull()) {
          value.setAssignedNull();
          return;
        }

        Variant valueCache = lookupCache.get(key); // SS: Ge'cashed'tes Lookup!
        if (valueCache == null) { // SS: Ge'cashed'tes Lookup!
          Diagnostic.check(pickListColumns != null);
          Diagnostic.check(destinationColumns != null);
          ReadRow.copyTo(destinationColumns, destRow, pickListColumns,
              pickListRow);

          // (SS) LookupFallback:
          LookupFallback lfb = getLookupFallback();
          if (lfb != null) {
            valueCache = new Variant(displayDataType);
            if (lfb.lookupFallback(pickListDataSet, pickListRow,
                pickListColumns, pickListDataSet.getColumn(displayOrdinal),
                valueCache)) {
              lookupCache.put(key, valueCache);
              value.setVariant(valueCache);
              return;
            }
          }

          // Suche in der aktuellen picklist:
          if (pickListDataSet.dataSetStore.lookup(pickListDataSet,
              pickListRow.columnList.getScopedArray(), pickListRow,
              displayOrdinal, value, Locate.FIRST)) {
            valueCache = new Variant();
            valueCache.setVariant(value);
            lookupCache.put(key, valueCache); // SS: Ge'cashed'tes Lookup!
            return;
          }
          // (SS) Wenn nicht gefunden und das Dataset ist "FetchAsNeeded", dann
          // müssen wir
          // zu dem Masterlink entsprechenen Daten navigieren (temporär) und
          // erneut ein
          // Lookup machen. Diese art des Lookup passiert in Tabellen, wenn eine
          // Zeile
          // mit einer Picklist dargestellt werden soll, welche jedoch nicht die
          // aktuelle
          // Zeile ist.
          if (pickListDataSet.isDetailDataSetWithFetchAsNeeded()
              && pickListDataSet.index != null) {
            pickListDataSet.index.loadDetails(destRow);
            if (init())
              ReadRow.copyTo(destinationColumns, destRow, pickListColumns,
                  pickListRow);
            if (pickListDataSet.dataSetStore.lookup(pickListDataSet,
                pickListRow.columnList.getScopedArray(), pickListRow,
                displayOrdinal, value, Locate.FIRST)) {
              valueCache = new Variant();
              valueCache.setVariant(value);
              lookupCache.put(key, valueCache); // SS: Ge'cashed'tes Lookup!
              return;
            }
          }
          /*
           * // (SS) Parametrisierte Abfrage ausführen: else if
           * (pickListDataSet.getRowCount() == 0 && pickListDataSet instanceof
           * QueryDataSet && ((QueryDataSet) pickListDataSet).getParameterRow()
           * != null && pickListDataSet.refreshSupported()) {
           * pickListDataSet.refresh(); if (init())
           * ReadRow.copyTo(destinationColumns, destRow, pickListColumns,
           * pickListRow); if
           * (pickListDataSet.dataSetStore.lookup(pickListDataSet,
           * pickListRow.columnList.getScopedArray(), pickListRow,
           * displayOrdinal, value, Locate.FIRST)) { valueCache = new Variant();
           * valueCache.setVariant(value); lookupCache.put(key, valueCache);
           * return; } }
           */

        } else {
          value.setVariant(valueCache); // SS: Ge'cashed'tes Lookup!
        }
      }
    } catch (VariantException ex) {
      System.err.println(pickList.toString());
      throw ex;
    }
  }

  @Override
  public void lookup(ReadRow readRow, Variant value)
  /*-throws DataSetException-*/
  {
    if (!init())
      return;

    try {
      value.setUnassignedNull();

      // SS: Ge'cashed'tes Lookup!
      CacheItem key = new CacheItem(destinationColumns, readRow); // SS:
      // Ge'cashed'tes
      // Lookup!
      // if (key.items[0].getType() == Variant.INT && key.items[0].getInt() ==
      // 150)
      // key = key;
      // if (pickListDataSet.getTableName() != null &&
      // pickListDataSet.getTableName().equals("ADDRESSDATA"))
      // key = key;

      if (key.isNull()) {
        value.setAssignedNull();
        return;
      }

      Variant valueCache = lookupCache.get(key); // SS: Ge'cashed'tes Lookup!
      if (valueCache == null) { // SS: Ge'cashed'tes Lookup!
        ReadRow.copyTo(destinationColumns, readRow, pickListColumns,
            pickListRow);

        if (pickListDataSet.dataSetStore.lookup(pickListDataSet,
            pickListRow.columnList.getScopedArray(), pickListRow,
            displayOrdinal, value, Locate.FIRST)) {
          valueCache = new Variant();
          valueCache.setVariant(value);
          lookupCache.put(key, valueCache); // SS: Ge'cashed'tes Lookup!
          return;
        }
        // (SS) Wenn nicht gefunden und das Dataset ist "FetchAsNeeded", dann
        // müssen wir
        // zu dem Masterlink entsprechenen Daten navigieren (temporär) und
        // erneut ein
        // Lookup machen. Diese art des Lookup passiert in Tabellen, wenn eine
        // Zeile
        // mit einer Picklist dargestellt werden soll, welche jedoch nicht die
        // aktuelle
        // Zeile ist.
        if (pickListDataSet.getMasterLink() != null
            && pickListDataSet.index != null) {
          pickListDataSet.open();
          pickListDataSet.index.loadDetails(pickListDataSet.getMasterLink()
              .getMasterDataSet());
          if (init())
            ReadRow.copyTo(destinationColumns, readRow, pickListColumns,
                pickListRow);
          if (pickListDataSet.dataSetStore.lookup(pickListDataSet,
              pickListRow.columnList.getScopedArray(), pickListRow,
              displayOrdinal, value, Locate.FIRST)) {
            valueCache = new Variant();
            valueCache.setVariant(value);
            lookupCache.put(key, valueCache); // SS: Ge'cashed'tes Lookup!
            return;
          }
        }
        /*
         * // (SS) Parametrisierte Abfrage ausführen: else if
         * (pickListDataSet.getRowCount() == 0 && pickListDataSet instanceof
         * QueryDataSet && ((QueryDataSet) pickListDataSet).getParameterRow() !=
         * null && pickListDataSet.refreshSupported()) {
         * pickListDataSet.refresh(); if (init())
         * ReadRow.copyTo(destinationColumns, readRow, pickListColumns,
         * pickListRow); if
         * (pickListDataSet.dataSetStore.lookup(pickListDataSet,
         * pickListRow.columnList.getScopedArray(), pickListRow, displayOrdinal,
         * value, Locate.FIRST)) { valueCache = new Variant();
         * valueCache.setVariant(value); lookupCache.put(key, valueCache); //
         * SS: Ge'cashed'tes Lookup! return; } }
         */

        // (SS)Ein nicht gefundenes Element als Null zu speichern führt nicht
        // zum gewünschten Ergebnis!
        // valueCache = new Variant();
        // valueCache.setUnassignedNull();
        // lookupCache.put(key, valueCache);
        // (SS) LookupFallback:
        LookupFallback lfb = getLookupFallback();
        if (lfb != null) {
          valueCache = new Variant(displayDataType);
          if (lfb.lookupFallback(pickListDataSet, pickListRow, pickListColumns,
              pickListDataSet.getColumn(displayOrdinal), valueCache)) {
            lookupCache.put(key, valueCache);
            value.setVariant(valueCache);
          }
        }
      } else {
        value.setVariant(valueCache); // SS: Ge'cashed'tes Lookup!
      }
    } catch (VariantException ex) {
      System.err.println(pickList.toString());
      throw ex;
    }
  }

  LookupFallback getLookupFallback() {
    if (pickListDataSet instanceof LookupFallback)
      return (LookupFallback) pickListDataSet;
    DataSet ds = pickListDataSet.getStorageDataSet();
    if (ds instanceof LookupFallback)
      return (LookupFallback) ds;
    return null;
  }

  @Override
  public void fillIn(DataSet dataSet, Variant value)
  /*-throws DataSetException-*/
  {
    // if (value == null && !pickListDataSet.isOpen())
    // return;

    if (!init())
      return;

    if (fillInRow == null || !fillInRow.isCompatibleList(dataSet)) {
      fillInRow = new DataRow(pickListDataSet, pickListDataSet.getColumn(
          displayOrdinal).getColumnName());
      fillInDestRow = new DataRow(pickListDataSet, pickListColumns);
    }

    fillInRow.setVariant(0, value);
    if (pickListDataSet.dataSetStore.isOpen()
        && pickListDataSet.dataSetStore.lookup(fillInRow, fillInDestRow,
            Locate.FIRST)) {
      ReadRow.copyTo(pickListColumns, fillInDestRow, destinationColumns,
          dataSet);
    }
  }

  private final DataSet dataSet;
  private final PickListDescriptor pickList;
  private final Column column;

  private DataSet pickListDataSet;
  private String[] destinationColumns;
  private String[] pickListColumns;
  private int displayOrdinal;
  private int displayDataType;
  private DataRow fillInRow;
  private DataRow fillInDestRow;
  private DataRow destRow;
  private DataRow pickListRow;
  boolean calcField;

  private HashMap<CacheItem, Variant> lookupCache = null;

  class CacheItem {
    Variant[] items;

    public CacheItem(Variant[] items) {
      this.items = items;
    }

    public CacheItem(String[] columns, ReadRow row) {
      items = new Variant[columns.length];
      for (int i = 0; i < columns.length; i++) {
        items[i] = new Variant();
        row.getVariant(columns[i], items[i]);
      }
    }

    public boolean isNull() {
      for (Variant v : items)
        if (v.isNull())
          return true;
      return false;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj
     *          the reference object with which to compare.
     * @return <code>true</code> if this object is the same as the obj argument;
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;

      if (!(obj instanceof CacheItem))
        return false;

      CacheItem c = (CacheItem) obj;
      if (items.length != c.items.length)
        return false;

      for (int i = 0; i < items.length; i++) {
        if (!items[i].equals(c.items[i]))
          return false;
      }
      return true;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
      int h = 19;
      for (Variant item : items) {
        h = (h + item.hashCode()) % 4125453;
      }
      return h;
    }

    /**
     * toString
     *
     * @return String
     */
    @Override
    public String toString() {
      String s = "";
      for (Object item : items) {
        s += item.toString() + '|';
      }
      return s;
    }

  }

  public void dropCache() {
    lookupCache.clear();
  }

  public void setupCache() {
    if (lookupCache == null) {
      lookupCache = new HashMap<CacheItem, Variant>();
      pickListDataSet.addAccessListener(this);
      if (pickListDataSet instanceof TableDataSet) {
        ((TableDataSet) pickListDataSet).addEditListener(this);
      }
      // if (pickListDataSet.getTableName() != null
      // && pickListDataSet.getTableName().equals("ADDRESSDATA"))
      // System.out.println("Lookup init: " + pickListDataSet);
    }
  }

  @Override
  public void accessChange(AccessEvent event) {
    if (event.getID() == AccessEvent.CLOSE) {
      if (event.getReason() == AccessEvent.UNKNOWN) {
        if (lookupCache != null)
          lookupCache.clear();
      }
    }
  }

  @Override
  public void canceling(DataSet dataSet) throws Exception {
  }

  @Override
  public void updating(DataSet dataSet, ReadWriteRow newRow, ReadRow oldRow)
      throws Exception {
    CacheItem key = new CacheItem(pickListColumns, oldRow);
    lookupCache.remove(key);
  }

  @Override
  public void updated(DataSet dataSet) {
  }

  @Override
  public void adding(DataSet dataSet, ReadWriteRow newRow) throws Exception {
  }

  @Override
  public void added(DataSet dataSet) {
  }

  @Override
  public void deleting(DataSet dataSet) throws Exception {
    CacheItem key = new CacheItem(pickListColumns, dataSet);
    lookupCache.remove(key);
  }

  @Override
  public void deleted(DataSet dataSet) {
  }

  @Override
  public void modifying(DataSet dataSet) throws Exception {
  }

  @Override
  public void inserting(DataSet dataSet) throws Exception {
  }

  @Override
  public void inserted(DataSet dataSet) {
  }

  @Override
  public void editError(DataSet dataSet, Column column, Variant value,
      DataSetException ex, ErrorResponse response) {
  }

  @Override
  public void addError(DataSet dataSet, ReadWriteRow row, DataSetException ex,
      ErrorResponse response) {
  }

  @Override
  public void updateError(DataSet dataSet, ReadWriteRow row,
      DataSetException ex, ErrorResponse response) {
  }

  @Override
  public void deleteError(DataSet dataSet, DataSetException ex,
      ErrorResponse response) {
  }

  @Override
  public void canceled(DataSet dataSet) {
  }

}
