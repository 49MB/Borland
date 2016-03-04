//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.EventListener;

/** This is used as a notification that a load operation on a StorageDataSet
    has been completed.  Currently load operations occur when a query/procedure
    execution and when a StorageDataSet is loaded from an import operation.
    This notification is most interesting for queries/procedures that are executed
    with asynchronous fetching, since this is done with a separate thread.
*/
public class LoadEvent extends com.borland.jb.util.DispatchableEvent
{
  public LoadEvent(Object source) {
    super(source);
  }

  public void dispatch(EventListener listener) {
    ((LoadListener)listener).dataLoaded(this);
  }
}
