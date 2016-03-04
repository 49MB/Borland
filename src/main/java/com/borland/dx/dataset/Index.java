//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.Diagnostic;

// Not public.  Not used outside package.
//
public abstract class Index {

	public abstract int lastRow() /*-throws DataSetException-*/;

	public abstract long internalRow(int row) /*-throws DataSetException-*/;

	public abstract int findClosest(long internalRow) /*-throws DataSetException-*/;

	public abstract int findClosest(long internalRow, int row) /*-throws DataSetException-*/;

	public abstract int locate(int startRow, Column[] scopedColumns,
			RowVariant[] values, int locateOptions) /*-throws DataSetException-*/;

	public void markStatus(int row, int status, boolean on)
	/*-throws DataSetException-*/
	{
		Diagnostic.fail();
	}

	// Overrriden by DetailIndex.
	//
	public void emptyAllRows(DataSet dataSet)
	/*-throws DataSetException-*/
	{
		StorageDataSet dataSetStore = dataSet.getStorageDataSet();
		if (dataSetStore != null)
			dataSetStore.empty();
	}

	public void setInsertPos(int pos)
	/*-throws DataSetException-*/
	{
		Diagnostic.fail();
	}

	public abstract long getInternalRow();

	public long moveRow(long pos, long delta)
	/*-throws DataSetException-*/
	{
		return 0;
	}

	public boolean isFullLookup() {
		return true;
	}

	public abstract boolean isSorted();

	public Index getLookupIndex() { // Overwritten in DetailIndex
		return this;
	}

	public void loadDetails(ReadRow tempMaster) { // For DetailIndex
	}

	public abstract boolean isAppended(); // Für provideMoreData entscheident!

}
