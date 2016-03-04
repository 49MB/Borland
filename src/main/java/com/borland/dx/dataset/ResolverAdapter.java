//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.ErrorResponse;

/**
 * This is an adapter class for {@link ResolverListener}, which is used as notification
 * before and after a StorageDataSet is resolved.
 */
public class ResolverAdapter implements
   ResolverListener
{
  public void insertingRow(ReadWriteRow row, ResolverResponse response) /*-throws DataSetException-*/ {}
  public void deletingRow(ReadWriteRow row, ResolverResponse response) throws  DataSetException {}
  public void updatingRow(ReadWriteRow row, ReadRow oldRow, ResolverResponse response)  /*-throws DataSetException-*/ {}
  public void insertedRow(ReadWriteRow row) /*-throws DataSetException-*/ {}
  public void deletedRow(ReadWriteRow row) /*-throws DataSetException-*/ {}
  public void updatedRow(ReadWriteRow row, ReadRow oldRow) /*-throws DataSetException-*/ {}
  public void insertError(DataSet dataSet, ReadWriteRow row, DataSetException ex, ErrorResponse response) /*-throws DataSetException-*/ {}
  public void deleteError(DataSet dataSet, ReadWriteRow row, DataSetException ex, ErrorResponse response) throws  DataSetException {}
  public void updateError(DataSet dataSet, ReadWriteRow row, ReadRow oldRow, ReadWriteRow updRow, DataSetException ex, ErrorResponse response)  /*-throws DataSetException-*/ {}
}

