//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.memorystore;

import java.util.Vector;

import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.DataIndex;
import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.DataSetException;
import com.borland.dx.dataset.InternalRow;
import com.borland.dx.dataset.Locate;
import com.borland.dx.dataset.RowFilterListener;
import com.borland.dx.dataset.RowFilterResponse;
import com.borland.dx.dataset.RowVariant;
import com.borland.dx.dataset.SortDescriptor;
import com.borland.jb.util.Diagnostic;
import com.borland.jb.util.Trace;

// Not public.  Not used outside package.
//
class MemoryIndex extends DataIndex {
	public MemoryIndex(SortDescriptor descriptor,
			Vector<RowFilterListener> rowFilterListeners, InternalRow filterRow,
			MemoryData data, int visibleMask, int invisibleMask,
			IntColumn statusColumn) {
		super(data, visibleMask, invisibleMask);
		this.memoryData = data;
		this.descriptor = descriptor;
		this.statusColumn = statusColumn;
		this.rowFilterListeners = rowFilterListeners;
		this.filterRow = filterRow;

		if (filterRow != null)
			rowFilterResponse = new RowFilterResponse();

		// ! Diagnostic..println("new Index statusColumn "+statusColumn.toString());
		// ! Diagnostic..printStackTrace();
		lastRow = -1;

		vector = new int[DataColumn.InitialSize];
		vectorLength = vector.length;
	}

	@Override
	public void emptyAllRows(DataSet dataSet)
	{
		lastRow = -1;
		super.emptyAllRows(dataSet);
	}
	@Override
	public final long internalRow(int longRow) {
		int row = longRow;
		// Protects against multi-threaded issue when switching from
		// one index to another when the new index has less rows. Would
		// not want to get An array indexing exception. Just dummy things
		// up.
		//
		if (row > lastRow)
			row = 0;
		// ! Diagnostic..println(row + " v " +vector[row]);
		int internalRow = vector[row];
		// Note that any non zero internal row has a retrievable value. A zero
		// internalRow could come from an index switch with a filter of less
		// records. Note
		// that index switches always make sure that their array size is at least
		// as large as the previous index to avoid array indexing exceptions.
		//
		if (internalRow < 1)
			return 1;
		return internalRow;
	}

	final boolean canAdd(int internalRow)
	/*-throws DataSetException-*/
	{
		int status = statusColumn.vector[internalRow];
		// !
		// Diagnostic.println(this+" canAdd: "+Integer.toHexString(visibleMask)+" "+Integer.toHexString(invisibleMask)+" "+Integer.toHexString(statusColumn.vector[internalRow]));
		if ((status & visibleMask) == 0 || (status & invisibleMask) != 0) {
			// ! Diagnostic.trace(Trace.DataSetEdit,
			// "cantAdd not visible "+Integer.toString(visibleMask,
			// 16)+" "+Integer.toString(statusColumn.vector[internalRow], 16));
			// !
			// Diagnostic..println(internalRow+" mask "+Integer.toString(visibleMask,
			// 16) + " "+Integer.toString(statusColumn.vector[internalRow], 16));
			// ! Diagnostic.println("not adding:  "+Integer.toHexString(status));
			return false;
		}

		// !
		// Diagnostic.println("rowFilterListener:  "+rowFilterListener+" "+filterRow);
		if (filterRow != null) {
			filterRow.setInternalRow(internalRow);
			// (SS) rowFilterResponse.ignore();
			rowFilterResponse.add();

			try {
				for (RowFilterListener listener : rowFilterListeners) {
					listener.filterRow(filterRow, rowFilterResponse);
					if (!rowFilterResponse.canAdd())
						break;
				}
				return rowFilterResponse.canAdd();
			} catch (DataSetException ex) {
				Diagnostic.printStackTrace(ex);
				return false;
			}
		}

		return true;
	}

	/**
	 * check if filter installed (SS)
	 */

	boolean isFiltered() {
		return rowFilterListeners != null && rowFilterListeners.size() > 0;
	}

	final void vectorInsert(int pos, int value) {
		// ! Diagnostic..println("vectorInsert "+pos);
		if (comp > 0 && pos <= lastRow)
			++pos;
		Diagnostic.check(pos >= 0);
		if (++lastRow >= vectorLength) {
			growVector();
		}

		// if (value == 0)
		// value = value;
		if (pos <= lastRow) {
			if (pos < lastRow)
				System.arraycopy(vector, pos, vector, pos + 1, lastRow - pos);
			vector[pos] = value;
			Diagnostic.trace(Trace.DataSetEdit, "Index.vectorInsert " + pos
					+ " value " + value + " lastRow " + lastRow);
		} else
			Diagnostic.fail();
		// ! Diagnostic..println(pos +
		// " vectorInsert "+vector[0]+" "+vector[1]+" "+vector[2]+" "+vector[3]);
	}

	final void growVector() {
		growVector(DataColumn.getNewSize(vectorLength));
	}

	void growVector(int newLength) {
		if (newLength > vectorLength) {
			int oldLength = vectorLength;
			int newVector[] = new int[newLength];
			if (oldLength > 0)
				System.arraycopy(vector, 0, newVector, 0, oldLength);
			vector = newVector;
			vectorLength = vector.length;
		} else
			Diagnostic.fail(); // Why are you trying to grow?
	}

	final void vectorDelete(int pos) {
		Diagnostic.check(pos >= 0);
		if (pos <= lastRow) {
			if (pos < lastRow)
				System.arraycopy(vector, pos + 1, vector, pos, lastRow - pos);
			--lastRow;
		}
	}

	@Override
	public final void loadStore(long longInternalRow)
	/*-throws DataSetException-*/
	{
		int internalRow = (int) longInternalRow;
		if (canAdd(internalRow)) {
			if (++lastRow >= vectorLength)
				growVector();
			vector[lastRow] = internalRow;
		}
	}

	@Override
	public boolean addStore(long longInternalRow)
	/*-throws DataSetException-*/
	{
		int internalRow = (int) longInternalRow;
		Diagnostic.trace(Trace.DataSetEdit, "Index.add " + internalRow);
		// ! Diagnostic..printStackTrace();
		if (lastRow < 0 || internalRow > vector[lastRow]) {
			loadStore(internalRow); // Faster.
			return true;
		}

		if (canAdd(internalRow)) {
			int pos = findClosest(internalRow);
			if (pos < 0)
				pos = 0;
			vectorInsert(pos, internalRow);
			return true;
		}
		Diagnostic.trace(Trace.DataSetEdit, "Index.add - cantAdd!!!");
		return false;
	}

	@Override
	public void prepareUpdate(long internalRow) {
	}

	@Override
	public void updateStore(long longInternalRow)
	/*-throws DataSetException-*/
	{
		int internalRow = (int) longInternalRow;
		deleteStore(internalRow);
		addStore(internalRow);
	}

	@Override
	public final void deleteStore(long internalRow) {
		// ! Diagnostic..printStackTrace();
		int pos = findClosest(internalRow);
		if (comp == 0) {
			vectorDelete(pos);
		}
		Diagnostic.trace(Trace.DataSetEdit, lastRow + " Index.delete Deleted "
				+ (comp == 0));
	}

	@Override
	public final void delete(long internalRow) {
		deleteStore(internalRow);
	}

	final void printVector() {
		for (int index = 0; index <= lastRow; ++index)
			Diagnostic.print(vector[index] + " ");
		Diagnostic.println("");
	}

	final int vectorLength() {
		return vectorLength;
	}

	@Override
	public final int findClosest(long searchRow, int row) {
		if (lastRow < 0) {
			internalRow = -1;
			return 0;
		}
		if (row <= lastRow && vector[row] == searchRow) {
			internalRow = searchRow;
			return row;
		}
		int newRow = findClosest(searchRow);
		if (row <= lastRow && vector[newRow] != searchRow) {
			internalRow = vector[row];
			return row;
		}
		return newRow;
	}

	@Override
	public final int findClosest(long longSearchRow) {
		// Assume it will be found.
		//
		internalRow = longSearchRow;
		int searchRow = (int) longSearchRow;

		if (lastRow == -1)
			return 0;
		int high = lastRow;
		int low = 0;
		int mid = -1;

		// Although locate operations can use searchRow of 0 for setting
		// search values, this function is not used for locating.
		//
		if (searchRow == 0 || searchRow >= statusColumn.lastRow)
			return 0;

		while (true) {
			mid = (low + high) / 2;

			compare(searchRow, vector[mid]);
			// ! Diagnostic..println(lastRow+" comp "+comp+" high "+high+
			// " low "+low+" mid "+mid);
			if (comp > 0) {
				if (low >= high) {
					if (check)
						checkIndex(mid);
					internalRow = vector[mid];
					return mid;
				}
				low = mid + 1;
			} else if (comp < 0) {
				if (high <= low) {
					if (check)
						checkIndex(mid);
					internalRow = vector[mid];
					return mid;
				}
				high = mid - 1;
			} else {
				if (check)
					checkIndex(mid);

				return mid;
			}
		}
	}

	private final void checkIndex(int row) {
		if (check) {
			int saveComp = comp;
			if (row > 0) {
				compare(vector[row], vector[row - 1]);
				Diagnostic.check(comp > 0);
			}
			if (row < lastRow) {
				compare(vector[row], vector[row + 1]);
				Diagnostic.check(comp < 0);
			}
			comp = saveComp;
		}
	}

	@Override
	public final void loadSearchValues(Column[] locateColumns, RowVariant[] values)
	/*-throws DataSetException-*/
	{
		int ordinal;
		Diagnostic.check(vector != null);
		for (int columnIndex = 0; columnIndex < locateColumnCount; ++columnIndex) {
			ordinal = locateColumns[columnIndex].getOrdinal();
			memoryData.dataColumns[ordinal].setVariant(0, values[ordinal]);
		}
	}

	@Override
	public int locate(int startRow, int locateOptions)
	/*-throws DataSetException-*/
	{
		// Protects against multi-threaded issue when switching from
		// one index to another when the new index has less rows. Would
		// not want to get An array indexing exception.
		//
		if (startRow > lastRow)
			return -1;

		if ((locateOptions & (Locate.PRIOR | Locate.LAST)) != 0)
			return locateBackwards(startRow);
		else
			return locateForwards(startRow);
	}

	final int locateForwards(int startRow) {
		int count = lastRow() + 1;
		for (int row = startRow; row < count; ++row) {
			if (compareRow(row)) {
				return row;
			}
		}
		return -1;
	}

	final int locateBackwards(int startRow) {
		// ! Diagnostic..println("starting on "+startRow);
		for (int row = startRow; row > -1; --row) {
			if (compareRow(row))
				return row;
		}
		return -1;
	}

	final boolean compareRow(int row) {
		if (vector[row] == 0) {
			System.err.println("vector-0-hack: " + this);
			// return false;
		}
		for (int columnIndex = 0; columnIndex < locateColumnCount; ++columnIndex) {
			if (locatePartialIndex == columnIndex) {
				if (!memoryData.dataColumns[locateColumns[columnIndex].getOrdinal()]
						.partialCompare(vector[row], 0, locateCaseInsensitive,
								locateFullPartial))
					return false;
			} else if (locateCaseInsensitive) {
				if (memoryData.dataColumns[locateColumns[columnIndex].getOrdinal()]
						.compareIgnoreCase(vector[row], 0) != 0)
					return false;
			} else {
				// d.verbose = true;
				if (memoryData.dataColumns[locateColumns[columnIndex].getOrdinal()]
						.compare(vector[row], 0) != 0) {
					// d.verbose = false;
					return false;
				}
				// d.verbose = false;
			}
		}
		return true;
	}

	@Override
	public int lastRow() {
		return lastRow;
	}

	@Override
	public boolean markForUpdate(RowVariant[] values)
	/*-throws DataSetException-*/
	{
		// !SteveS TODO. Could optimize updates with this.
		return true;
	}

	void compare(int index1, int index2) {
		comp = index1 - index2;
		// ! Diagnostic..println("compare "+index1+ " "+index2+" comp "+ comp +
		// " "+index1+" "+vector[index2]);
	}

	@Override
	public void note(int note)
	/*-throws DataSetException-*/
	{
		Diagnostic.fail();
	}

	int[] vector;
	IntColumn statusColumn;
	int comp;

	int lastRow;
	int vectorLength;

	// ! private DataColumn[] dataColumns;
	MemoryData memoryData;
	private final InternalRow filterRow;
	private RowFilterResponse rowFilterResponse;

	private final static boolean check = false;

	@Override
	public boolean isSorted() {
		return false;
	}

	@Override
	public boolean isAppended() {
		return true;
	}
}
