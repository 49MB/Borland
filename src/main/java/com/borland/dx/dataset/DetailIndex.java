//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.Diagnostic;
import com.borland.jb.util.Trace;

class DetailIndex extends Index implements MasterUpdateListener {

	public void masterUpdate(MasterUpdateEvent event) {
	}

	DetailIndex(DataSet detailDataSet)
	/*-throws DataSetException-*/
	{
		MasterLinkDescriptor masterLink = detailDataSet.getMasterLink();

		this.detailDataSet = detailDataSet;
		this.detailDataSetStore = detailDataSet.getStorageDataSet();
		this.masterDataSet = masterLink.getMasterDataSet();
		this.masterLinkColumns = masterLink.getMasterLinkColumns();
		this.detailLinkColumns = masterLink.getDetailLinkColumns();
		this.fetchAsNeeded = masterLink.isFetchAsNeeded();
		this.cascadeUpdates = masterLink.isCascadeUpdates();
		this.cascadeDeletes = masterLink.isCascadeDeletes();

		detailDataSetStore.checkMasterLink(masterLink);

		if (this.detailLinkColumns == null)
			this.detailLinkColumns = this.masterLinkColumns;

		Diagnostic.trace(Trace.Detail, "StorageDataSet.openDetailIndex " // NORES
				+ masterDataSet.getStorageDataSet().getTableName()
				+ "->>"
				+ detailDataSet.getTableName() // NORES
				);

		// ! MasterLinkDescriptor masterLink = detailDataSet.getMasterLink();

		if (masterDataSet == null
				|| masterLinkColumns == null
				|| masterLinkColumns.length < 1
				|| (detailLinkColumns != null && masterLinkColumns.length != detailLinkColumns.length)) {
			DataSetException.throwLinkColumnsError();
		}

		// ! Need to force master open to do tests. fix for 5518.
		// !
		masterDataSet.open();

		if (fetchAsNeeded) {
			// ! Diagnostic.println("detail:  "+detailDataSet.getTableName()+" "+
			// detailDataSet.dataSetStore.getStoreName());
			detailDataSetStore.initFetchDataSet(masterDataSet, masterLinkColumns,
					detailLinkColumns);

			// if (canLoadDetails(masterDataSet, masterLinkColumns, true)) {
			// SS: Wenn das detail nicht geladen wird, hat es keine Columns. Somit
			// hagelts Exceptions.
			// Somit immer details laden. Hat der Master Null-Werte, dann laden wir
			// halt die verknüpften Nulls!

			// MB: Wenn man die if-Bedienung weg lässt, dann löst es zwar das oben
			// beschriebene Problem
			// bringt aber tausend neue Probleme. Unter anderem die Steigerungsform
			// vom Dopplerbug genannnt
			// Endlosbug, soll heißen er lädt immer wieder die gleichen Details
			if (canLoadDetails(masterDataSet, masterLinkColumns, true)) {
				detailDataSetStore.recordDetailsFetched();
				loadDetails();
			}
		}
	}

	DataSet getTempMasterDataSet() {
		return masterDataSet;
	}

	DataRow getTempMasterRow() {
		return masterRow;
	}

	void setTempMasterDataSet(DataSet masterDataSet) {
		this.masterDataSet = masterDataSet;
	}

	void setTempMasterDataRow(DataRow masterRow) {
		this.masterRow = masterRow;
	}

	final void init() /*-throws DataSetException-*/{

		// ! fix for 14358. TextDataFile load causes storageDataSet.fetchDataSet to
		// ! be set to null in between DetailIndex() constructor and this init()
		// method.
		// !
		if (fetchAsNeeded && detailDataSetStore.fetchDataSet == null) {
			detailDataSetStore.initFetchDataSet(masterDataSet, masterLinkColumns,
					detailLinkColumns);
		}
		if (detailDataSet.getColumnCount() < 1) {
			// SS: Fix für detailDataSet.getColumnCount == 0
			detailDataSetStore.openFetchAsNeededStorage(detailDataSet, masterDataSet,
					masterLinkColumns, detailLinkColumns);
		}

		// SS Why?? if (detailDataSet.getColumnCount() < 1 &&
		// masterDataSet.getLongRowCount() < 1)
		if (detailDataSet.getColumnCount() < 1) // SS
			DataSetException.dataSetHasNoColumns(detailDataSet);
		// ! Diagnostic.println("detail:  "+detailDataSet.columnCount());

		// ! Diagnostic.println("detailDataSetStore:  "+detailDataSetStore.
		// getColumnCount());
		for (int index = 0; index < detailLinkColumns.length; ++index) {
			if (detailDataSetStore.getColumn(detailLinkColumns[index]).getDataType() != masterDataSet
					.getColumn(masterLinkColumns[index]).getDataType()) {
				DataSetException.throwLinkColumnsError();
			}
		}

		oldSort = detailDataSet.getSort();
		// Being the nice guy I am, I try to reconcile the sort descriptor with
		// the linking columns. If things are too out of sync, I throw an exception.
		//
		String indexName = null;
		String[] sortKeys = detailDataSet.getSortKeys(oldSort);
		String[] newSortKeys = null;
		boolean[] descending = null;
		if (oldSort != null) {
			indexName = oldSort.getIndexName();
			descending = oldSort.getDescending();
		}
		boolean[] newDescending = null;

		if (sortKeys != null && sortKeys.length > 0) {
			int length = sortKeys.length;
			if (length > detailLinkColumns.length)
				length = detailLinkColumns.length;

			boolean columnsOverlap = true;
			for (int index = 0; index < length; ++index) {
				if (!detailLinkColumns[index].equals(sortKeys[index]))
					columnsOverlap = false;
			}

			if (!columnsOverlap) {
				int needsFixSorting = 0;
				for (String sortKey : sortKeys) {
					if (isDetailLinkColumn(sortKey))
						// DataSetException.masterDetailViewError();
						needsFixSorting++;
				}
				newSortKeys = new String[detailLinkColumns.length + sortKeys.length
				                         - needsFixSorting];
				System.arraycopy(detailLinkColumns, 0, newSortKeys, 0,
						detailLinkColumns.length);
				int indexNew = detailLinkColumns.length;
				for (int i = 0; i < sortKeys.length; ++i) {
					if (needsFixSorting == 0 || !isDetailLinkColumn(sortKeys[i])) {
						newSortKeys[indexNew] = sortKeys[i];
						indexNew++;
					}
				}
				newDescending = new boolean[newSortKeys.length];
				for (int i = 0; i < detailLinkColumns.length; i++)
					newDescending[i] = false;
				indexNew = detailLinkColumns.length;
				for (int i = 0; i < sortKeys.length; i++) {
					if (needsFixSorting == 0 || !isDetailLinkColumn(sortKeys[i])) {
						newDescending[indexNew] = oldSort.isDescending(i);
						indexNew++;
					}
				}

			} else if (detailLinkColumns.length > sortKeys.length) {
				newSortKeys = detailLinkColumns;
				newDescending = null;
			} else {
				newSortKeys = sortKeys;
				newDescending = descending;
			}
		} else
			newSortKeys = detailLinkColumns;

		// Must always set the sort property because we don't support
		// caseInsensitive
		// links and sortKeys may change.
		//
		detailDataSet.resetSort(new SortDescriptor(indexName, newSortKeys,
				newDescending, (oldSort == null ? null : oldSort.getLocaleName()),
				oldSort == null ? 0 : oldSort.getOptions()));

		detailDataSetStore.openIndex(detailDataSet);
		this.index = detailDataSet.index;
		this.detailRow = new DataRow(detailDataSet, detailLinkColumns);

		detailDataSetView = new DataSetView();
		detailDataSetView.setStorageDataSet(detailDataSetStore);

		if (fetchAsNeeded)
			detailDataSetView.setSort(detailDataSet.getSort());
		else
			detailDataSetView.setSort(new SortDescriptor(detailDataSet.getSort())); // SS
		// Kopie
		// muss!
		if (detailDataSet.getRowFilterListeners() != null) {
			detailDataSetView.addRowFilterListener(detailDataSet
					.getRowFilterListeners());
		}
		detailDataSetView.open();
		// ! Diagnostic.println(" opened:  "+detailDataSetStore.getName());
		detailRow = new DataRow(detailDataSet, detailLinkColumns);

		Diagnostic.trace(Trace.Detail, "initMasterLink");
		masterRow = new DataRow(masterDataSet, masterLinkColumns);
		masterDataSet.removeMasterUpdateListener(this);
		masterDataSet.addMasterUpdateListener(this);
		// Must remove first, because could get here by dependent close access event
		// and then a reopen of the dependent (detailDataSet). ie sort master and
		// detail
		// must be closed and opened again.
		//
		masterDataSet.removeAccessListener(detailDataSet);
		masterDataSet.addAccessListener(detailDataSet);
		// !
		// Diagnostic.println("detailDataSet:  "+detailDataSet.getStorageDataSet().
		// getName()+" "+detailDataSet.getTableName());
		Diagnostic.trace(Trace.Detail,
				"detailDataSet:  " + detailDataSet.getClass().getName() + " "
						+ detailDataSet.getTableName());
		Diagnostic.trace(Trace.Detail,
				"MasterDataSet:  " + masterDataSet.getClass().getName() + " "
						+ masterDataSet.getTableName());
		masterDataSet.removeMasterNavigateListener(detailDataSet);
		masterDataSet.addMasterNavigateListener(detailDataSet);

		reLink(false);
	}

	private boolean isDetailLinkColumn(String columnName) {
		for (String detailLinkColumn : detailLinkColumns) {
			if (detailLinkColumn.equals(columnName))
				return true;
		}
		return false;
	}

	final void setDefaultValues(ReadWriteRow row)
	/*-throws DataSetException-*/
	{
		Diagnostic.trace(Trace.Detail, "assign default link column values: "
				+ masterDataSet.toString());
		copyMasterRowToDetailRow(masterDataSet, row);
	}

	final void close(DataSet detailDataSet, boolean preserveAccessListener) /*-throws DataSetException-*/{
		if (!preserveAccessListener)
			masterDataSet.removeAccessListener(detailDataSet);
		masterDataSet.removeMasterUpdateListener(this);
		masterDataSet.removeMasterNavigateListener(detailDataSet);
		Diagnostic.check(detailDataSetView != null);
		if (detailDataSetView != null)
			detailDataSetView.close();
		if (!preserveAccessListener)
			detailDataSet.setSort(oldSort);
	}

	final void copyMasterRowToDetailRow(ReadRow masterRow, ReadWriteRow detailRow)
	/*-throws DataSetException-*/
	{
		ReadRow.copyTo(masterLinkColumns, masterRow, detailLinkColumns, detailRow);
	}

	final void copyPostedMasterRowToDetailRow(ReadWriteRow detailRow) {
		copyPostedMasterRowToDetailRow(detailRow, masterDataSet);
	}

	final void copyPostedMasterRowToDetailRow(ReadWriteRow detailRow,
			DataSet master)
	/*-throws DataSetException-*/
	{
		if (cascadeUpdates && master.isEditing() && !master.isEditingNewRow()) {
			if (!masterRow.isCompatibleList(master))
				masterRow = new DataRow(master, masterLinkColumns);
			master.getStorageDataSet().getRowData(master, master.getRow(), masterRow);
			ReadRow
			.copyTo(masterLinkColumns, masterRow, detailLinkColumns, detailRow);
		} else {
			ReadRow.copyTo(masterLinkColumns, master, detailLinkColumns, detailRow);
		}
	}

	private int locateDetail(ReadRow row, int options)
	/*-throws DataSetException-*/
	{
		Diagnostic.check(detailDataSetView.getStorageDataSet() == detailDataSet
				.getStorageDataSet());
		detailDataSetView.open();
		if (detailDataSetView.locate(row, options)) {
			return detailDataSetView.getRow();
		} else
			return -1;
	}

	public boolean uses(DataSet dataSet) {
		return detailDataSetView == dataSet || masterDataSet == dataSet
				|| detailDataSetStore == dataSet;
	}

	private final boolean isOkToRelink() {
		// Be careful about opening the master. Detail may be linked to itself.
		// Potential for infinite recursion.
		//
		if (!masterDataSet.isOpen() || masterDataSet.getRowCount() < 1) {
			Diagnostic.trace(Trace.Detail, "relink on empty master");
		} else if (!detailDataSetView.isOpen()) {
			// Can get here inadvertently when all dependents of a StorageDataSet
			// are asked to close. - detailDataSetView.close() happens before
			// detail.close() and detail has an unposted row, _post will ask to
			// relink the details.
			//
			Diagnostic.trace(Trace.Detail, "detail closed");
		} else if (!isNullMaster())
			return true;
		return false;
	}

	private boolean isNullMaster() {
		for (String c : masterLinkColumns) {
			if (!masterDataSet.isNull(c))
				return false;
		}
		return true;
	}

	boolean needsRelink() {
		if (isOkToRelink() && rangeEnd > -1) {
			copyPostedMasterRowToDetailRow(detailRow);
			// Relink is sometimes unnecessary. Note that when there are many
			// DataSet instances for a master, an edit to any of these masters will
			// cause all masters to consider relinking even if the linking columns
			// have not been modified. Relinking details can be annoying to apps
			// since it causes edited rows to be posted and repositions to the start
			// of the group.
			//
			detailDataSetView.open();
			if (detailDataSetView.getRowCount() > 0
					&& detailDataSetView.findDifference(0, detailRow) < 0)
				return false;
		}
		return true;
	}

	void reLink(boolean doFetchAsNeeded)
	/*-throws DataSetException-*/
	{
		int rowStart;
		int rowEnd;

		rangeStart = 0;
		rangeEnd = -1;

		Diagnostic.trace(Trace.Detail, "DetailIndex.reLink()");

		// try {
		if (isOkToRelink()) {
			// detailRow.clearValues();
			copyPostedMasterRowToDetailRow(detailRow);
			/*
			 * These slow down debug build because of the toString() operations.
			 * Diagnostic.trace(Trace.Detail, masterDataSet.getRow()+" masterRow:  " +
			 * masterDataSet.toString()); Diagnostic.trace(Trace.Detail,
			 * "linking on master rowValue:  " + detailRow.toString());
			 */

			// ! Diagnostic.println("relink on:  "+detailRow);
			rowStart = locateDetail(detailRow, Locate.FIRST);
			// !/*
			// ! Variant[] rowValues =
			// detailRow.getRowValues(detailRow.getColumnList());
			// !
			// ! rowStart = index.locate( 0,
			// detailRow.getColumnList().getScopedArray(),
			// ! rowValues, Locate.FIRST
			// ! );
			// !*/

			if (canLoadDetails(detailRow, detailLinkColumns, true) && rowStart < 0
					&& doFetchAsNeeded && detailsLoaded) {
				if (fetchAsNeeded)
					detailDataSetStore.recordDetailsFetched();
				loadDetails();
				rowStart = locateDetail(detailRow, Locate.FIRST);
				// !/*
				// !rowStart = index.locate( 0,
				// detailRow.getColumnList().getScopedArray(),
				// ! rowValues, Locate.FIRST
				// ! );
				// !*/
			}

			if (rowStart > -1) {
				rowEnd = locateDetail(detailRow, Locate.LAST);
				// !/*
				// ! rowEnd = index.locate( 0,
				// detailRow.getColumnList().getScopedArray(),
				// ! detailRow.getRowValues(detailRow.getColumnList()),
				// ! Locate.LAST
				// ! );
				// !*/

				if (rowEnd > -1) {
					rangeStart = rowStart;
					rangeEnd = rowEnd;
					// Diagnostic.trace(Trace.Detail, "reLink success rangeStart:  " +
					// rangeStart + " rangeEnd: " +rangeEnd);
				} else {
					Diagnostic.println("detailRow:  " + detailRow);
					rowEnd = locateDetail(detailRow, Locate.LAST);
					Diagnostic.trace(Trace.Detail, "reLink FAIL rangeStart:  "
							+ rangeStart + " rangeEnd: " + rangeEnd);
					Diagnostic.fail();
				}
			} else
				Diagnostic.trace(Trace.Detail, "reLink failed on locate first");
		}
		// }
		// catch(Exception ex) {
		// Diagnostic.printStackTrace(ex);
		// }
	}

	private boolean canLoadDetails(ReadRow row, String[] sourceLinkNames,
			boolean recordFetch)
	/*-throws DataSetException-*/
	{
		// Avoid recursion.
		//
		if (loadingRows || detailDataSetStore.isLoadBlocked())
			return false;

		if (!fetchAsNeeded)
			return false;

		masterDataSet.open();

		if (masterDataSet.getRowCount() < 1)
			return false;
		// !/*
		// ! if ((masterDataSet.getStatus()&RowStatus.DETAILS_FETCHED)!=0)
		// ! return false;
		// !*/

		// Some jdbc drivers (ie Visigenics) do not like the setting of null
		// paramatized queries, so just don't load if any of the linking values
		// are null - could add a property to the MasterLinkDescriptor to allow the
		// user to control this in the future.
		//
		for (String masterLinkColumn : masterLinkColumns) {
			if (masterDataSet.getVariantStorage(masterLinkColumn).isNull())
				return false;
		}

		if (fetchAsNeeded
				&& detailDataSetStore.detailsFetched(row, sourceLinkNames,
						detailLinkColumns))
			return false;

		return true;
	}

	final boolean canLoadDetails(boolean recordFetch)
	/*-throws DataSetException-*/
	{
		if (canLoadDetails(masterDataSet, masterLinkColumns, recordFetch)) {
			copyMasterRowToDetailRow(masterDataSet, detailRow);

			return locateDetail(detailRow, Locate.FIRST) < 0;
		}
		return false;
	}

	private final void loadDetails()
	/*-throws DataSetException-*/
	{
		// ! Diagnostic.trace(Trace.Detail,
		// "reLink.index.lastRow():  "+index.lastRow());
		// ! Diagnostic.trace(Trace.Detail,
		// "reLink.masterDataSet current row:  "+masterDataSet.row()+
		// " "+masterDataSet);
		try {
			loadingRows = true;
			// !
			// Diagnostic.println("before loadDetailRows: "+detailDataSet.columnCount
			// ());
			detailDataSetStore.loadDetailRows(detailDataSet, masterDataSet);
			detailsLoaded = true;
			// !
			// Diagnostic.println("after loadDetailRows: "+detailDataSet.rowCount()+" "
			// +detailDataSetView.rowCount());
			// !
			// Diagnostic.println("after loadDetailRows: "+detailDataSet.columnCount(
			// ));
			/*
			 * if (fetchAsNeeded) detailDataSetStore.detailsFetched(masterDataSet,
			 * masterLinkColumns, detailLinkColumns); // SS aktualisiert die FetchRow,
			 * sonst // Dopplerbug!
			 */
		}
		// catch (DataSetException ex) {
		// Diagnostic.println("loadDetails encountered exception");//!
		// Diagnostic.printStackTrace(ex);
		// }
		finally {
			loadingRows = false;
		}
	}

	@Override
	public void loadDetails(ReadRow tempMaster) {
		if (!fetchAsNeeded)
			return;

		try {
			ReadRow.copyTo(masterLinkColumns, tempMaster, detailLinkColumns,
					detailRow);

			if (locateDetail(detailRow, Locate.FIRST) >= 0) // Already loaded
				return;

			loadingRows = true;
			detailDataSetStore.loadDetailRows(detailDataSet, tempMaster);
			detailsLoaded = true;
			if (!detailDataSetStore.detailsFetched(tempMaster, masterLinkColumns,
					detailLinkColumns))
				detailDataSetStore.recordDetailsFetched();
		} finally {
			loadingRows = false;
		}
	}

	@Override
	public int lastRow() {
		if (rangeEnd > -1)
			return rangeEnd - rangeStart;
		return -1;
	}

	@Override
	public Index getLookupIndex() {
		return index;
	}

	@Override
	public long internalRow(int row)
	/*-throws DataSetException-*/
	{
		if (!((row > -1 && row <= rangeEnd - rangeStart) || (row == 0))) {
			Diagnostic.printStackTrace();
			Diagnostic.println("row:  " + row + " " + rangeStart + " " + rangeEnd);
			// ! Diagnostic.exit(1);
		}

		// ! Diagnostic.check((row > -1 && row <= rangeEnd - rangeStart) || (row ==
		// 0));
		return index.internalRow(rangeStart + row);
	}

	@Override
	public long getInternalRow() {
		return internalRow;
	}

	@Override
	public void markStatus(int row, int status, boolean on)
	/*-throws DataSetException-*/
	{
		detailDataSet.dataSetStore.markStatus(internalRow(row), status, on);
	}

	private final int adjustForBounds(int row)
	/*-throws DataSetException-*/
	{

		if (row > rangeEnd && rangeEnd > -1 && row > 0) {
			// ! Diagnostic.println("end Going from "+row+" to "+rangeEnd);
			row = rangeEnd;
			internalRow = index.internalRow(row);
		} else if (row < rangeStart) {
			// ! Diagnostic.println("start Going from "+row+" to "+rangeStart);
			row = rangeStart;
			internalRow = index.internalRow(row);
		} else {
			internalRow = index.getInternalRow();
		}

		return row - rangeStart;
	}

	@Override
	public int findClosest(long internalRow, int row)
	/*-throws DataSetException-*/
	{
		return adjustForBounds(index.findClosest(internalRow, rangeStart + row));
	}

	@Override
	public int findClosest(long internalRow)
	/*-throws DataSetException-*/
	{
		return adjustForBounds(index.findClosest(internalRow));
	}

	final boolean compareRow(int row) {
		Diagnostic.fail();
		return false;
	}

	final void loadSearchValues(Variant[] values) {
		Diagnostic.fail();
	}

	@Override
	public int locate(int startRow, Column[] scopedColumns, RowVariant[] values,
			int locateOptions)
	/*-throws DataSetException-*/
	{
		startRow += rangeStart;
		if (!(startRow == 0 || (startRow > 0 && startRow <= index.lastRow())))
			// exakt wie bei DataIndex.Locate
			reLink(false);

		if (!(startRow == 0 || (startRow > 0 && startRow <= index.lastRow())))
			startRow = 0;

		int rowFound = index.locate(startRow, scopedColumns, values, locateOptions
				| Locate.DETAIL);
		if (rowFound > -1 && rowFound <= rangeEnd)
			return rowFound - rangeStart;
		return -1;
	}

	@Override
	public final void masterCanChange(MasterUpdateEvent event) throws Exception {
		// if (event.getMaster() == masterDataSet) {
		// if (!cascadeUpdates && lastRow() >= 0 &&
		// masterRow.hasColumn(event.getColumn().getColumnName()) != null)
		// ValidationException.cannotOrphanDetails(detailDataSet.getTableName());
		// }
	}

	@Override
	public final void masterDeleting(MasterUpdateEvent event) throws Exception {
		DataSet master = event.getMaster();
		// if (master == masterDataSet ||
		// (master.getStorageDataSet() == masterDataSet.getStorageDataSet() &&
		// master.getInternalRow() == masterDataSet.getInternalRow())) {
		if (master.getStorageDataSet() == masterDataSet.getStorageDataSet()
				&& master.getInternalRow() == masterDataSet.getInternalRow()) {
			Diagnostic.trace(Trace.Detail, "attempt to delete master row");

			// ! masterDataSet.getStorageDataSet().getRowData(masterDataSet,
			// masterDataSet.getRow(), masterRow);
			if (!masterDataSet.isCompatibleList(masterRow))
				masterRow = new DataRow(masterDataSet, masterLinkColumns); // SS: Fix
			// für Bug
			// "Unsync Row/Delete"
			// masterDataSet.getDataRow(masterRow); SS: Fix
			master.getDataRow(masterRow); // SS: Fix

			copyPostedMasterRowToDetailRow(detailRow, master);

			if (cascadeDeletes) {
				// Must used detailDataSet instead of detailDataSetView, because
				// we must navigate the master of the next level detail.
				//
				detailDataSet.cancel();
				while (detailDataSet.locate(detailRow, Locate.FIRST)) {
					detailDataSet.deleteRow();
				}
			}
			// else {
			// if (detailDataSetView.locate(detailRow, Locate.FIRST))
			// ValidationException.cannotOrphanDetails(detailDataSetView.getTableName()
			// );
			// }
		}
	}

	@Override
	public final void masterEmptying(MasterUpdateEvent event) throws Exception {
		DataSet master = event.getMaster();
		// if (master == masterDataSet ||
		// (master.getStorageDataSet() == masterDataSet.getStorageDataSet() &&
		// master.getInternalRow() == masterDataSet.getInternalRow())) {
		if (master.getStorageDataSet() == masterDataSet.getStorageDataSet()
				&& master.getInternalRow() == masterDataSet.getInternalRow()) {
			Diagnostic.trace(Trace.Detail, "attempt to empty master row");

			// ! masterDataSet.getStorageDataSet().getRowData(masterDataSet,
			// masterDataSet.getRow(), masterRow);
			if (!masterDataSet.isCompatibleList(masterRow))
				masterRow = new DataRow(masterDataSet, masterLinkColumns); // SS: Fix
			// für Bug
			// "Unsync Row/Delete"
			// masterDataSet.getDataRow(masterRow); SS: Fix
			master.getDataRow(masterRow); // SS: Fix

			copyPostedMasterRowToDetailRow(detailRow, master);

			if (cascadeDeletes) {
				// Must used detailDataSet instead of detailDataSetView, because
				// we must navigate the master of the next level detail.
				//
				detailDataSet.cancel();
				while (detailDataSet.locate(detailRow, Locate.FIRST)) {
					detailDataSet.emptyRow();
				}
			}
			// else {
			// if (detailDataSetView.locate(detailRow, Locate.FIRST))
			// ValidationException.cannotOrphanDetails(detailDataSetView.getTableName()
			// );
			// }
		}
	}

	@Override
	public final void masterChanging(MasterUpdateEvent event) throws Exception {
		DataSet master = event.getMaster();
		if (master.getStorageDataSet() == masterDataSet.getStorageDataSet()) {
			// && master.getInternalRow() == masterDataSet.getInternalRow()) {
			// if (master.getStorageDataSet() == masterDataSet.getStorageDataSet()) {
			if (masterDataSet.getRow() >= masterDataSet.getRowCount())
				return;

			if (detailDataSet != master)
				detailDataSet.post();
			if (!masterRow.isCompatibleList(master))
				masterRow = new DataRow(master, masterLinkColumns);
			master.getStorageDataSet().getRowData(master, master.getRow(), masterRow);

			ReadRow changingRow = event.getChangingRow();

			int linkCount;

			if (changingRow.columnList.hasScopedColumns()) {
				linkCount = 0;
				for (String masterLinkColumn : masterLinkColumns) {
					if (changingRow.hasColumn(masterLinkColumn) != null)
						++linkCount;
				}
			} else
				linkCount = masterLinkColumns.length;

			if (linkCount != 0) {
				if (linkCount != masterLinkColumns.length
						|| !changingRow.equals(masterRow)) {
					copyMasterRowToDetailRow(masterRow, detailRow); // alte Daten!
					detailDataSetStore.updateFetchData(masterRow, changingRow,
							masterLinkColumns, detailLinkColumns);
					if (cascadeUpdates) {
						while (detailDataSet.locate(detailRow, Locate.FIRST)) {
							ReadRow.copyTo(masterLinkColumns, changingRow, detailLinkColumns,
									detailDataSet);
							detailDataSet.post();
							copyMasterRowToDetailRow(masterRow, detailRow);
						}
					}
					// else if (detailDataSetView.locate(detailRow, Locate.FIRST))
					// ValidationException.cannotOrphanDetails(detailDataSetView.
					// getTableName());
				}
			}
		}
	}

	final boolean detailsLoaded() {
		return detailsLoaded;
	}

	@Override
	public void emptyAllRows(DataSet dataSet)
	/*-throws DataSetException-*/
	{
		StorageDataSet dataSetStore = dataSet.getStorageDataSet();
		dataSet.open();
		if (dataSetStore != null) {
			dataSetStore.deleteAllRows(dataSet, true);
			if (dataSetStore.getDeletedRowCount() > 0) {
				DataSetView view = new DataSetView();
				dataSetStore.getDeletedRows(view);
				DataRow locRow = new DataRow(view, detailLinkColumns);
				copyMasterRowToDetailRow(masterDataSet, locRow);
				while (view.locate(locRow, Locate.FIRST)) {
					view.emptyRow();
				}
				view.close();
			}
			detailRow.clearValues();
			reLink(false);
		}
	}

	@Override
	public void setInsertPos(int pos)
	/*-throws DataSetException-*/
	{
		index.setInsertPos(rangeStart + pos);
	}

	@Override
	public long moveRow(long pos, long delta)
	/*-throws DataSetException-*/
	{
		long targetPos = rangeStart + pos + delta;
		if (targetPos < rangeStart)
			delta += (rangeStart - targetPos);
		else if (targetPos > rangeEnd)
			delta -= (targetPos - rangeEnd);

		return index.moveRow(rangeStart + pos, delta);
	}

	private long internalRow;

	private final boolean cascadeDeletes;
	private final boolean cascadeUpdates;
	private boolean detailsLoaded;
	private int rangeStart;
	private int rangeEnd;
	private DataRow detailRow;
	private DataRow masterRow;
	private DataSet masterDataSet;
	private Index index;
	private final StorageDataSet detailDataSetStore;
	private final DataSet detailDataSet;
	private DataSetView detailDataSetView;
	private boolean loadingRows;
	private final boolean fetchAsNeeded;
	private String[] detailLinkColumns;
	private final String[] masterLinkColumns;
	SortDescriptor oldSort;

	@Override
	public boolean isSorted() {
		return true;
	}

	@Override
	public boolean isAppended() {
		return index.isAppended();
	}
}
