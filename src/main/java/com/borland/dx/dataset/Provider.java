//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

/**
 * The Provider class is an abstract base class that "provides" (or populates) a
 * DataSet with data. Extend this class if you want to create a custom provider.
 * The instantiable subclasses (technically, subclasses of its JDBCProvider
 * subclass) QueryProvider and ProcedureProvider are included. These classes
 * collect provider functionality using queries and stored procedures on JBDC
 * data sources.
 * <p>
 * For an example of a custom provider that extends this class, see the
 * ProviderBean class in the sample project CustomProviderResolver.jpr. This
 * sample is located in the providers sample folder of your JBuilder
 * installation. For another example of writing a custom provider, see the
 * sample project StreamableDataSets.jpr located in the
 * DataExpress/StreamableDataSets samples folder of 1your JBuilder installation.
 * The ClientProvider.java class in this sample application discusses building a
 * custom provider that uses RMI to make a remote method call to load data into
 * a DataSet. (These samples only run with JBuilder Enterprise.)
 */
public abstract class Provider implements java.io.Serializable, Designable {
  
  /**
   * Provides the data for a DataSet. The source of the data, and the method of
   * retrieving the data is up to the implementation of this abstract method.
   * The toOpen parameter indicates whether this method is called as part of
   * opening this StorageDataSet.
   * 
   * @param dataSet
   *          StorageDataSet
   * @param toOpen
   *          boolean
   */
  abstract public void provideData(StorageDataSet dataSet, boolean toOpen) /*-throws DataSetException-*/;
  
  /**
   * (SS)Provides the data for a DataSet. The source of the data, and the method
   * of retrieving the data is up to the implementation of this abstract method.
   * The fetchAsNeededData parameter contains the 'where' parameter for
   * fetchAsNeeded-Query opening this StorageDataSet.
   * 
   * @param dataSet
   *          StorageDataSet
   * @param fetchAsNeededData
   *          ReadRow
   */
  abstract public void provideData(StorageDataSet dataSet,
      ReadRow fetchAsNeededData) /*-throws DataSetException-*/;
  
  /**
   * Some implementations of the provideData method may optionally provide the
   * data asynchronously. A StorageDataSet has to block actions such as
   * resolving until the asynchronous data is present. This method allows an
   * implementation to give an appropriate error message by raising a
   * DataSetException. The default action is to do nothing, i.e. no asynchronous
   * providing.
   * 
   * @param dataSet
   *          StorageDataSet
   */
  public void checkIfBusy(StorageDataSet dataSet) /*-throws DataSetException-*/{
  }
  
  /**
   * Called to validate the masterLink property. When the MasterLinkDescriptor's
   * fetchAsNeeded property is enabled (true), the QueryProvider uses this
   * method to check if there is a WHERE clause in the query. If no WHERE clause
   * is specified, the QueryProvider throws a DataSetException.
   * 
   * @param dataSet
   *          StorageDataSet
   * @param masterLink
   *          MasterLinkDescriptor
   */
  public void checkMasterLink(StorageDataSet dataSet,
      MasterLinkDescriptor masterLink)
  /*-throws DataSetException-*/
  {
  }
  
  /**
   * Some implementations of a Provider may allow to provide part of the data,
   * then load more data on demand. This method should return <b>true</b> if
   * there is more data to be loaded. To load the data, call the provideMoreData
   * method.
   * 
   * <p>
   * <b>Note:</b> StorageDataSet will attempt to retrieve more data in the
   * following three cases:
   * <ol>
   * <li>Navigation with dataSet.next() goes past the last record.</li>
   * <li>dataSet.last() is called.</li>
   * <li>Whenever any UI component gets close to the last record.</li>
   * </ol>
   * 
   * <p>
   * Then, Provider.hasMoreData() is called. If it returns <b>true</b>,
   * {@link com.borland.dx.dataset.Provider#provideMoreData(StorageDataSet)} is
   * called.
   * 
   * @param dataSet
   *          StorageDataSet
   * @return boolean
   */
  public boolean hasMoreData(StorageDataSet dataSet) {
    return false;
  }
  
  public boolean isAsyncLoad(StorageDataSet dataSet) {
    return false;
  }
  
  public void setMaxLoadRows(int maxLoadRows) {
    
  }
  
  public int getMaxLoadRows() {
    return -1;
  }
  
  public void setMaxRowCount(int maxRowCounts) {
    
  }
  
  public int getMaxRowCount() {
    return -1;
  }
  
  /**
   * Some implementations of a Provider may allow to provide part of the data
   * and then load more data on demand. This method provides more data if there
   * is more data to be loaded. If no more data is available, this method simply
   * returns.
   * 
   * <p>
   * <b>Note:</b> StorageDataSet will attempt to retrieve more data in the
   * following three cases:
   * 
   * <p>
   * <ol>
   * <liNavigation with dataSet.next() goes past the last record.</li>
   * <li>dataSet.last() is called.</li>
   * <li>Whenever any UI component gets close to the last record.</li>
   * </ol>
   * 
   * <p>
   * Then, {@link Provider#hasMoreData(StorageDataSet)} is called. If it returns
   * <b>true</b>, Provider.provideMoreData() is called.
   * 
   * @param dataSet
   *          StorageDataSet
   */
  public void provideMoreData(StorageDataSet dataSet) /*-throws DataSetException-*/{
  }
  
  /**
   * Releases resources kept for loading data on demand. StorageDataSet calls
   * this method when the storage is being closed and when calling
   * StorageDataSet.closeProvider. The loadRemainingRows parameter controls
   * whether the rest of the data (if more data is available) is loaded or not.
   * 
   * @param dataSet
   *          StorageDataSet
   * @param loadRemainingRows
   *          boolean
   */
  public void close(StorageDataSet dataSet, boolean loadRemainingRows) /*-throws DataSetException-*/
  {
  }
  
  public void closeResources() {
  }
  
  /**
   * Returns <b>false</b> if new data provide requests should empty the
   * associated StorageDataSet. Returns <b>true</b> if new data provide requests
   * should leave existing rows in the associated StorageDataSet.
   * 
   * @return <b>false</b> if new data provide requests should empty the
   *         associated StorageDataSet. Returns <b>true</b> if new data provide
   *         requests should leave existing rows in the associated
   *         StorageDataSet.
   */
  public boolean isAccumulateResults() {
    return false;
  }
  
  /**
   * This is the parameterRow that will be used by extensions of StorageDataSet,
   * like QueryDataSet and ProcedureDataSet, to fill in parameter values for
   * parameterized queries or stored procedures. If a TableDataSet extension of
   * StorageDataSet has a QueryProvider or ProcedureProvider, this property will
   * also be used by those providers to fill in parameter values for
   * parameterized queries or stored procedures.
   * 
   * @return com.borland.dx.dataset.ReadWriteRow
   */
  public ReadWriteRow getParameterRow() {
    return parameterRow;
  }
  
  public void setParameterRow(ReadWriteRow value) {
    parameterRow = value;
  }
  
  private ReadWriteRow parameterRow;
  
  private static final long serialVersionUID = 1L;
}
