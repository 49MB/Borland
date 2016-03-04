//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.Arrays;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Set;

/**
 * The DataChangeEvent is the internal event generated when the data in a
 * DataSet is changed. It is passed to DataSet components and listeners of the
 * DataSet. The event ID (see the ID property) indicates the type of data
 * update. Other members provide additional information on the change of the
 * data.
 * <p>
 * The dbSwing components contain some examples for using the DataChangeEvent
 * class.
 * <p>
 * The {@link com.borland.dx.dataset.DataChangeEvent} class may be useful for
 * component writers, however, is not recommended for general usage. The
 * DataChangeListener responds to the DataChangeEvent class.
 */
public class DataChangeEvent extends com.borland.jb.util.DispatchableEvent
    implements com.borland.jb.util.ExceptionDispatch {
  /**
   * Row added. getRowAffected() will return the new position.
   */
  public static final int ROW_ADDED = 1;
  /**
   * Row Deleted. getRowAffected() will return the new position.
   */
  public static final int ROW_DELETED = 2;
  /**
   * Only a cell changed, row did not post. getRowAffected() will return the new
   * position.
   */
  public static final int ROW_CHANGED = 3;
  /**
   * Row changed and posted. getRowAffected() will return the new position.
   */
  public static final int ROW_CHANGE_POSTED = 4;
  /**
   * More than one row of data has changed.
   */
  public static final int DATA_CHANGED = 5;
  /**
   * Notification to listeners that a row is posting. This allows a listener to
   * post unposted field values just before the row is going to be posted.
   */
  public static final int POST_ROW = 6;
  /**
   * (SS) Editing canceled: Old: ROW_DELETED was called, this had problems with
   * JDBTable, because java deletes this row
   */
  public static final int ROW_CANCELED = 7;
  
  /**
   * Data in more than one row is affected. Useful for repaint strategies.
   * 
   * @return boolean
   */
  public final boolean multiRowChange() {
    return affectedRow == -1;
  }
  
  /**
   * Constructs a DataChangeEvent object.
   * 
   * @param source
   *          Object
   * @param id
   *          int
   */
  public DataChangeEvent(Object source, int id) {
    this(source, id, -1, -1);
  }
  
  /**
   * Constructs a DataChangeEvent object.
   * 
   * @param source
   *          Object
   * @param id
   *          int
   * @param affectedRow
   *          long
   */
  public DataChangeEvent(Object source, int id, int affectedRow,
      long internalRow) {
    super(source);
    this.id = id;
    this.affectedRow = affectedRow;
    this.internalRow = internalRow;
  }
  
  /**
   * Special Event Handling for provideMoreData (=loadAsNeeded)
   * 
   * @param source
   *          Object
   * @param loaded
   *          boolean
   * @param fromRow
   *          long
   * @param toRow
   *          long
   */
  public DataChangeEvent(Object source, boolean loaded, int fromRow, int toRow) {
    super(source);
    this.id = ROW_ADDED;
    this.affectedRow = -1;
    this.internalRow = -1;
    this.loaded = loaded;
    this.fromRow = fromRow;
    this.toRow = toRow;
  }
  
  // SS: 06/2006 dass ersichtlich ist, welche Column geändert wurde
  public DataChangeEvent(Object source, int id, int affectedRow,
      long internalRow, Column affectedColumn) {
    this(source, id, affectedRow, internalRow);
    this.affectedColumns.add(affectedColumn.getColumnName());
  }
  
  // SS: 04/2014 dass ersichtlich ist, welche Columns geändert wurde
  public DataChangeEvent(Object source, int id, int affectedRow,
      long internalRow, String[] affectedColumns) {
    this(source, id, affectedRow, internalRow);
    this.affectedColumns.addAll(Arrays.asList(affectedColumns));
  }
  
  /**
   * This method is used internally by other com.borland classes. You should
   * never use this method directly.
   * 
   * @param listener
   *          EventListener
   */
  @Override
  public void dispatch(final EventListener listener) {
    ((DataChangeListener) listener).dataChanged(this);
  }
  
  public void exceptionDispatch(EventListener listener) throws Exception {
    ((DataChangeListener) listener).postRow(this);
  }
  
  /**
   * If multiRowChange is false, this returns the row affected. Otherwise -1 is
   * returned.
   * 
   * @return int
   */
  public final int getRowAffected() {
    return affectedRow;
  }
  
  public final boolean isRowAffected(DataSet dataSet) {
    return affectedRow == -1 || dataSet.getRow() == affectedRow;
  }
  
  public final long getInternalRow() {
    return internalRow;
  }
  
  public final boolean isColumnAffected(Column column) {
    if (column == null) {
      return false;
    } else {
      return isColumnAffected(column.getColumnName());
    }
  }
  
  public final boolean isColumnAffected(String columnName) {
    if (affectedColumns.isEmpty())
      return true;
    
    for (String c : affectedColumns) {
      if (c.equalsIgnoreCase(columnName))
        return true;
    }
    if (source instanceof DataSet) {
      DataSet dataSet = (DataSet) source;
      Column lookupCol = dataSet.hasColumn(columnName);
      if (lookupCol != null && lookupCol.getPickList() != null) {
        PickListDescriptor pl = lookupCol.getPickList();
        for (String destCol : pl.getDestinationColumns()) {
          for (String c : affectedColumns) {
            if (c.equalsIgnoreCase(destCol))
              return true;
          }
        }
      }
    }
    return false;
  }
  
  // SS 08/2006: Gibt true zurück wenn mindestens einer der Spalten betroffen
  // ist
  public final boolean isColumnsAffected(String... columnNames) {
    if (affectedColumns.isEmpty())
      return true;
    for (String column : columnNames) {
      if (isColumnAffected(column)) {
        return true;
      }
    }
    return false;
  }
  
  public final boolean isColumnsAffected(Column... columns) {
    if (affectedColumns.isEmpty())
      return true;
    for (Column column : columns) {
      if (isColumnAffected(column)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * @return The type of data change. Return values for this property are
   *         constants defined in this class.
   */
  public final int getID() {
    return id;
  }
  
  /**
   * @return The concatenation of super.toString and the value of the ID
   *         property.
   */
  @Override
  public String toString() {
    return super.toString() + " " + id;
  }
  
  /**
   * @return boolean true, wenn die Daten von provideMoreData (=loadAsNeeded)
   *         geladen wurden
   */
  public boolean isLoaded() {
    return loaded;
  }
  
  public int getFromLoadRow() {
    return fromRow;
  }
  
  public int getToLoadRow() {
    return toRow;
  }
  
  private final int id;
  private int affectedRow;
  private long internalRow;
  private final HashSet<String> affectedColumns = new HashSet<String>();
  // SS: 04/2014 dass ersichtlich ist, welche Column geändert wurde
  private boolean loaded; // SS: 06/2007: true, wenn von provideMoreData geladen
                          // wurde
  int fromRow;
  int toRow;
  
  public void merge(DataChangeEvent event) {
    affectedColumns.addAll(event.affectedColumns);
    
    if (affectedRow != event.affectedRow)
      affectedRow = -1;
    if (internalRow != event.internalRow)
      internalRow = -1;
  }
  
  public Set<String> getAffectedColumns() {
    return affectedColumns;
  }
}
