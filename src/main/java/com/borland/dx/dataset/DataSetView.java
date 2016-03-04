//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

//import java.beans.Beans;

/**
 * 
 * The <CODE>DataSetView</CODE> component extends <CODE>DataSet</CODE>
 * functionality by presenting an alternate view of the data in the
 * <CODE>DataSet</CODE>. The <CODE>DataSetView</CODE> itself has no storage of
 * data but sees all the unfiltered data contained in its
 * <CODE>storageDataSet</CODE> property to which you can apply a different sort
 * order and filter criterion than the original <CODE>StorageDataSet</CODE>. The
 * navigation of the <CODE>DataSetView</CODE> data is separate from that of its
 * <CODE>StorageDataSet</CODE>.
 * 
 * <P>
 * The use of a <CODE>DataSetView</CODE> component is optional. If you only need
 * a single sort or filter criterion at a time in your application, you may
 * apply those settings directly on the <CODE>QueryDataSet</CODE>,
 * <CODE>ProcedureDataSet</CODE>, or <CODE>TableDataSet</CODE>. If however you
 * wish to present the data in the <CODE>DataSet</CODE> in multiple "views", the
 * <CODE>DataSetView</CODE> provides that capability without the need for
 * multiple objects that each store data.
 * 
 * <!-- JDS start - remove part about designer and UI -->
 * <P>
 * When a <CODE>DataSetView</CODE> shares the data storage of a
 * <CODE>DataSet</CODE> with another <CODE>DataSetView</CODE>, each
 * <CODE>DataSetView</CODE> sees edits that both make to the data. Calling the
 * <CODE>saveChanges()</CODE> method saves changes that were made to both. Both
 * also share column properties such as edit and display masks, and so on. You
 * cannot, however, display the <CODE>DataSetView</CODE> component's columns in
 * the JBuilder UI designer nor set its column properties (including persistent)
 * through the UI or programmatically. This is because the
 * <CODE>DataSetView</CODE> component doesn't have its own data storage. <!--
 * JDS end -->
 * 
 * <P>
 * The <CODE>DataSetView</CODE> component also allows for an additional level of
 * indirection which provides for greater flexibility when changing the binding
 * of your UI components. If you anticipate the need to rebind your UI
 * components and have several of them, bind the components to a
 * <CODE>DataSetView</CODE> instead of directly to the
 * <CODE>StorageDataSet</CODE>. When you need to rebind, change the
 * <CODE>DataSetView</CODE> component to the appropriate
 * <CODE>StorageDataSet</CODE>, thereby making a single change that affects all
 * UI components connected to the <CODE>DataSetView</CODE> as well.
 * 
 * 
 * <P>
 * <STRONG>Warning:</STRONG> The <CODE>DataSetView</CODE> component extends the
 * functionality provided by its superclass (<CODE>DataSet</CODE>). The
 * <CODE>close()</CODE> method is inherited by <CODE>DataSetView</CODE> and has
 * particular importance with this component as it must be called to ensure that
 * the component is garbage collected. Otherwise, a <CODE>DataSetView</CODE>
 * component cannot be garbage collected until its associated
 * <CODE>StorageDataSet</CODE> is garbage collected.
 * 
 * Presents alternate views and navigation of the data in the DataSet, without
 * the need for multiple objects that each store data. Can display a different
 * sort order and filtering than the source StorageDataSet.
 */

public class DataSetView extends DataSet {
  public DataSetView() {
    super();
  }
  
  // Complication: If FetchAsNeeded is set on the MasterLinkDescriptor for the
  // associated
  // StorageDataSet the DataSetView should share that MasterLinkDescriptor.
  // A DataSetView cannot have its own MasterLinkDescriptor with fetchAsNeeded.
  //
  @Override
  public synchronized void setMasterLink(MasterLinkDescriptor descriptor)
  /*-throws DataSetException-*/
  {
    if (descriptor != null && descriptor.isFetchAsNeeded()) {
      StorageDataSet sds = getStorageDataSet();
      
      if (sds != null
          && (!sds.isDetailDataSetWithFetchAsNeeded() || !descriptor
              .linkEquals(sds.getMasterLink()))) {
        DataSetException.needStorageDataSetForFetchAsNeeded();
      }
    }
    saveMasterLinkDescriptor = descriptor;
    super.setMasterLink(descriptor);
  }
  
  /*
   * public final MasterLinkDescriptor getMasterLink() { StorageDataSet sds =
   * getStorageDataSet(); if (sds != null &&
   * sds.isDetailDataSetWithFetchAsNeeded()) { return sds.getMasterLink(); }
   * else { return super.getMasterLink(); } }
   */
  
  public synchronized void setStorageDataSet(StorageDataSet dataSetStore)
  /*-throws DataSetException-*/
  {
    failIfOpen();
    if (resolverStorageDataSet != null
        && resolverStorageDataSet != dataSetStore) {
      setVisibleMask(RowStatus.DEFAULT, RowStatus.DEFAULT_HIDDEN);
      resolverStorageDataSet = null;
    }
    _setStorageDataSet(dataSetStore);
  }
  
  synchronized void _setStorageDataSet(StorageDataSet dataSetStore)
  /*-throws DataSetException-*/
  {
    if (this.dataSetStore == dataSetStore) {
      return;
    }
    failIfOpen();
    this.dataSetStore = dataSetStore;
    if (dataSetStore != null) {
      this.columnList = dataSetStore.columnList;
    } else {
      this.columnList = null;
    }
    setCompatibleList(columnList);
  }
  
  @Override
  int[] getRequiredOrdinals() {
    return dataSetStore.getRequiredOrdinals();
  }
  
  @Override
  protected boolean open(AccessEvent event) {
    if (!isOpen() && saveMasterLinkDescriptor != null
        && getMasterLink() == null) {
      super.setMasterLink(saveMasterLinkDescriptor);
      saveMasterLinkDescriptor = null;
    }
    boolean opened = super.open(event);
    if (opened) {
      incOpen();
    }
    return opened;
  }
  
  @Override
  protected boolean close(boolean preserveAccessListener, int reason,
      boolean closeData) {
    boolean closed = super.close(preserveAccessListener, reason, closeData);
    if (closed) {
      decOpen();
      saveMasterLinkDescriptor = getMasterLink();
      super.setMasterLink(null);
    }
    return closed;
  }
  
  @Override
  public boolean close() {
    // Schließt den Provider NICHT!!
    return close(false, AccessEvent.UNKNOWN, true);
  }
  
  MasterLinkDescriptor saveMasterLinkDescriptor;
  
  @Override
  public String toString() {
    if (dataSetStore != null) {
      return "DataSetView: " + dataSetStore.toString();
    } else {
      return "DataSetView: " + super.toString();
    }
  }
  
  private boolean wasOpen;
  private static final long serialVersionUID = 1L;
  
  /**
   * (SS): Wird das zugehörige Storage (=DataSet) geöffnet oder geschlossen,
   * dann auch dieses DataSetView öffnen bzw schließen
   */
  
  @Override
  public void accessChange(AccessEvent event) {
    
    switch (event.getID()) {
    case AccessEvent.OPEN:
      if (wasOpen) {
        open(event);
      }
      break;
    
    case AccessEvent.CLOSE:
      if (isOpen()) {
        wasOpen = true;
      }
      break;
    }
    
    super.accessChange(event);
  }
  
  @Override
  public boolean isUnused() {
    boolean unused = super.isUnused();
    if (unused) {
      // Wenn selber nicht im DetailIndex verwendet...
      if (dataSetStore != null && dataSetStore.index instanceof DetailIndex) {
        return !((DetailIndex) dataSetStore.index).uses(this);
      }
    }
    return unused;
  }
  
  public long getOpenTime() {
    return openTime;
  }
  
  public void incOpen() {
    // openViews++;
    // System.out.println("++Views: " + openViews + " " + toString());
    openTime = System.currentTimeMillis();
  }
  
  public void decOpen() {
    // openViews--;
    // System.out.println("--Views: " + openViews + " " + toString());
  }
  
  // private static int openViews = 0;
  
  private long openTime;
}
