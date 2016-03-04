//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.memorystore;

import java.util.Vector;

import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.InternalRow;
import com.borland.dx.dataset.Locate;
import com.borland.dx.dataset.RowFilterListener;
import com.borland.dx.dataset.RowVariant;
import com.borland.dx.dataset.SortDescriptor;
import com.borland.dx.dataset.ValidationException;
import com.borland.jb.util.Diagnostic;
import com.borland.jb.util.Trace;

//!Steves TODO Need to add collation support for international ordering.

// Not public.  Not used outside package.
//
class SortedMemoryIndex extends MemoryIndex {
  
  private Boolean appended;
  
  SortedMemoryIndex(SortDescriptor descriptor,
      Vector<RowFilterListener> rowFilterListeners, InternalRow filterRow,
      MemoryData data, DataColumn[] dataColumns, int visibleMask,
      int invisibleMask, IntColumn statusColumn, DataColumn[] keyDataColumns,
      Column[] columns) {
    super(descriptor, rowFilterListeners, filterRow, data, visibleMask,
        invisibleMask, statusColumn);
    this.caseInsensitive = descriptor.isCaseInsensitive();
    this.comparator = new KeyComparator(vector, keyDataColumns, descriptor);
    this.keyDataColumns = keyDataColumns;
    this.columns = columns;
    this.columnCount = columns.length;
    if (descriptor.isSortAsInserted()) {
      this.columnCount--;
      insertColumn = (IntColumn) keyDataColumns[keyDataColumns.length - 1];
    }
    this.descending = descriptor.isDescending();
  }
  
  @Override
  void growVector(int size) {
    super.growVector(size);
    if (insertColumn != null) {
      memoryData.growTo(insertColumn);
    }
    comparator.setIndexVector(vector);
  }
  
  @Override
  public final boolean addStore(long internalRow)
  /*-throws DataSetException-*/
  {
    Diagnostic.trace(Trace.DataSetEdit, "Index.add " + internalRow
        + " lastRow " + lastRow);
    // ! Diagnostic..printStackTrace();
    if (canAdd((int) internalRow)) {
      if (insertColumn != null) {
        if (memoryData.getRowCount() >= vectorLength)
          growVector();
        // insertColumn.setInt((int)internalRow, (int)internalRow);
        insertColumn.shift(vector, (int) internalRow, insertPos, lastRow + 1);
      }
      
      int pos = findClosest(internalRow);
      if (pos < 0)
        pos = 0;
      
      boolean appended = pos >= lastRow;
      if (this.appended == null)
        this.appended = appended;
      else
        this.appended = this.appended & appended;
      
      vectorInsert(pos, (int) internalRow);
      
      return true;
    }
    Diagnostic.trace(Trace.DataSetEdit, "Index.add - cantAdd!!!");
    return false;
  }
  
  @Override
  public boolean isAppended() {
    boolean result = false;
    if (appended != null)
      result = appended.booleanValue();
    appended = null;
    return result;
  }
  
  @Override
  public final void prepareUpdate(long internalRow) {
    replaceRow = findClosest(internalRow);
    // (SS) Sometimes Index is filtered and does not contain this internalRow !!
    // So do NOT check comp where this row is not added!
    // Diagnostic.check(comp == 0);
    if (comp != 0)
      replaceRow = -1;
    // Diagnostic.check(comp == 0 || (isFiltered() &&
    // !canAdd((int)internalRow))); //SS
  }
  
  @Override
  public final void updateStore(long inInternalRow)
  /*-throws DataSetException-*/
  {
    if (replaceRow == -1)
      return;
    
    int internalRow = (int) inInternalRow;
    if (canAdd(internalRow)) {
      if (replaceRow > 0) {
        compare(internalRow, vector[replaceRow - 1]);
        if (comp <= 0) {
          vectorDelete(replaceRow);
          addStore(internalRow);
          replaceRow = -1;
          return;
        }
      }
      if (replaceRow < lastRow) {
        compare(internalRow, vector[replaceRow + 1]);
        if (comp >= 0) {
          vectorDelete(replaceRow);
          addStore(internalRow);
          replaceRow = -1;
          return;
        }
      }
    } else
      vectorDelete(replaceRow);
    
    replaceRow = -1;
  }
  
  @Override
  final void compare(int dataRow1, int dataRow2) {
    comparator.compare(dataRow1, dataRow2);
    if (descending)
      comp = -comparator.comp;
    else
      comp = comparator.comp;
  }
  
  final int searchCompare(int dataRow1, int dataRow2) {
    comparator.searchCompare(columnCompareCount, dataRow1, dataRow2);
    if (descending)
      comp = -comparator.comp;
    else
      comp = comparator.comp;
    return comp;
  }
  
  final private void quickSort(int left, int right) {
    int middle;
    int temp;
    int leftPivot;
    int rightPivot;
    
    // Don't partition groups of records less than 10.
    //
    while ((right - left) > 4) {
      
      middle = (left + right) / 2;
      
      // !printVector();
      // Use median of three. Takes care of worst case of sorted data
      // and gurantees comparison sentinals at both ends.
      //
      if (comparator.compare(vector[left], vector[middle]) > 0) {
        temp = vector[left];
        vector[left] = vector[middle];
        vector[middle] = temp;
        // ! Diagnostic..println("swap left middle");
        // !printVector();
      }
      if (comparator.compare(vector[left], vector[right]) > 0) {
        temp = vector[left];
        vector[left] = vector[right];
        vector[right] = temp;
        // ! Diagnostic..println("swap left right");
        // !printVector();
      }
      if (comparator.compare(vector[middle], vector[right]) > 0) {
        temp = vector[middle];
        vector[middle] = vector[right];
        vector[right] = temp;
        // ! Diagnostic..println("swap middle right");
        // !printVector();
      }
      
      leftPivot = left;
      rightPivot = right - 1;
      
      // Can't have the pivot key being pivoted on us, so set it next to last.
      //
      temp = vector[middle];
      vector[middle] = vector[rightPivot];
      vector[rightPivot] = temp;
      // ! Diagnostic..println("left "+left+" middle "+middle+" right "+right);
      // !printVector();
      
      comparator.setPivot(rightPivot);
      
      while (true) {
        comparator.findPivots(leftPivot, rightPivot);
        leftPivot = comparator.leftPivot;
        rightPivot = comparator.rightPivot;
        
        if (leftPivot >= rightPivot)
          break;
        
        // swap leftPivot with rightPivot.
        //
        
        temp = vector[leftPivot];
        vector[leftPivot] = vector[rightPivot];
        vector[rightPivot] = temp;
        // !printVector();
      }
      
      // Put pivot key to valid position.
      //
      temp = vector[leftPivot];
      vector[leftPivot] = vector[right - 1];
      vector[right - 1] = temp;
      // ! Diagnostic..println("Pivot swaped " + leftPivot+" "+(right-1));
      // !printVector();
      
      if ((leftPivot - left) > (right - leftPivot)) {
        // Left partition is larger.
        //
        quickSort(leftPivot + 1, right);
        right = leftPivot - 1;
      } else {
        // Right partition is larger.
        //
        quickSort(left, leftPivot - 1);
        left = leftPivot + 1;
      }
    }
  }
  
  private final void insertionSort() {
    int step;
    int steper;
    int temp;
    int count = lastRow + 1;
    int insertPoint;
    
    for (step = 0; ++step < count;) {
      insertPoint = comparator.findInsertPoint(step, 0, true);
      
      // ! Diagnostic..println("insertPoint "+insertPoint+" "+step);
      if (insertPoint < step) {
        
        // Otherwise qickSort not doing its job.
        //
        Diagnostic.check((step - insertPoint) < 5);
        
        // Move em over.
        //
        temp = vector[step];
        steper = step;
        // !printVector();
        while (insertPoint < steper)
          vector[steper] = vector[--steper];
        vector[steper] = temp;
        // !printVector();
      }
    }
  }
  
  private final void uniqueInsertionSort()
  /*-throws DataSetException-*/
  {
    int step;
    int steper;
    int temp;
    int count = lastRow + 1;
    int insertPoint;
    int delta = 0;
    
    for (step = 0; ++step < count;) {
      insertPoint = comparator.findInsertPoint(step, delta, false);
      if (comparator.comp == 0 && insertPoint > -1)
        --delta;
      
      // ! Diagnostic..println("insertPoint "+insertPoint+" "+step);
      if (comparator.comp == 0)
        memoryData.copyDuplicate(vector[step]);
      else {
        
        steper = step + delta;
        // Otherwise qickSort not doing its job.
        //
        Diagnostic.check((steper - insertPoint) < 5);
        
        if (insertPoint < steper) {
          // Move em over.
          //
          temp = vector[step];
          // !printVector();
          while (insertPoint < steper)
            vector[steper] = vector[--steper];
          vector[steper] = temp;
        } else if (delta < 0) {
          vector[steper] = vector[step];
        }
        // !printVector();
      }
    }
    lastRow += delta;
  }
  
  // This simplifies sort order routines from having to
  // worry about descending order.
  //
  private final void reverseOrder() {
    int midPoint = (lastRow + 1) / 2;
    int temp;
    for (int index = 0; index < midPoint; ++index) {
      temp = vector[index];
      vector[index] = vector[lastRow - index];
      vector[lastRow - index] = temp;
    }
  }
  
  @Override
  public void sort()
  /*-throws DataSetException-*/
  {
    // ! /*
    // ! if (keyDataColumns[0] instanceof StringColumn) {
    // ! keyDataColumns;
    // ! for (int index = 0; index < lastRow; ++index) {
    // ! ((StringColumn)keyDataColumns[0])
    // ! }
    // ! */
    
    if (keyDataColumns[0] instanceof LocaleStringColumn) {
      ((LocaleStringColumn) keyDataColumns[0]).createCollationKeys();
    }
    
    comparator.disableReferenceCopy();
    quickSort(0, lastRow);
    if (!caseInsensitive)
      comparator.enableReferenceCopy();
    if (descriptor.isUnique())
      uniqueInsertionSort();
    else
      insertionSort();
    if (descending)
      reverseOrder();
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
    int columnIndex = 0;
    if (columnCount >= locateColumnCount) {
      for (; columnIndex < locateColumnCount; ++columnIndex) {
        if (columns[columnIndex].getOrdinal() != locateColumns[columnIndex]
            .getOrdinal()) {
          break;
        }
      }
    } else if (columnCount <= locateColumnCount) {
      for (; columnIndex < columnCount; ++columnIndex) {
        if (columns[columnIndex].getOrdinal() != locateColumns[columnIndex]
            .getOrdinal()) {
          break;
        }
      }
    }
    columnCompareCount = columnIndex;
    if (// columnCompareCount != locateColumnCount
    columnCompareCount == 0
        || ((locateOptions & Locate.CASE_INSENSITIVE) != 0) != caseInsensitive
        || (locateOptions & Locate.FULL_PARTIAL) != 0) {
      // ! Diagnostic..println("Can't keysearch on caseInsensitive keys");
      return super.locate(startRow, locateOptions);
    }
    
    if ((locateOptions & Locate.NEXT) != 0)
      return keyLocateForwards(startRow);
    if ((locateOptions & Locate.PRIOR) != 0)
      return keyLocateBackwards(startRow);
    
    // StringColumn.verbose = true;
    boolean first = (locateOptions & Locate.LAST) == 0;
    if ((locateOptions & Locate.FIRST) != 0)
      first = true;
    // ! This is problematic for bug 5480 where linking on descending sorted
    // ! column.
    // ! if (descending)
    // ! first = !first;
    
    Diagnostic.trace(Trace.Locate, "Find first " + first + " descending "
        + descending);
    int row = find(0, first);
    // Diagnostic.trace(Trace.Locate, "Found:  "+row);
    // ! StringColumn.verbose = false;
    
    if (comp == 0 || locatePartialIndex != -1) {
      if (columnCount == locateColumnCount && locatePartialIndex == -1)
        return row;
      // !StringColumn.verbose = true;
      if (row == 0 && locatePartialIndex != -1)
        return -1;
      if (first) {
        Diagnostic.trace(Trace.Locate, "locateForwards from " + row);
        return keyLocateForwards(row);
      } else {
        Diagnostic.trace(Trace.Locate, "locateBackwards from " + row);
        return keyLocateBackwards(row);
      }
    }
    
    return -1;
  }
  
  final int find(int internalRow, boolean first) {
    int low = 0;
    int high = lastRow;
    int mid = -1;
    
    while (true) {
      
      mid = (low + high) / 2;
      
      // if (!first) StringColumn.verbose = true;
      searchCompare(internalRow, vector[mid]);
      // if (!first) StringColumn.verbose = false;
      // if (!first) Diagnostic.println(lastRow+" comp "+comp+" high "+high+
      // " low "+low+" mid "+mid);
      // ++searchCount;
      // ! Diagnostic..println("mid "+mid);
      if (comp == 0) {
        if (first) {
          if (high == mid)
            return mid;
          high = mid;
        } else {
          if (low == mid) {
            if (low == high)
              return mid;
            searchCompare(internalRow, vector[high]);
            if (comp == 0)
              return high;
            else {
              comp = 0;
              return mid;
            }
          } else
            low = mid;
        }
      } else if (comp > 0) {
        if (low >= high) {
          return mid;
        }
        low = mid + 1;
      } else if (comp < 0) {
        if (high <= low) {
          return mid;
        }
        high = mid - 1;
      }
    }
  }
  
  private final int keyLocateForwards(int row) {
    if (row >= 0 && row < vector.length) {
      int count = lastRow + 1;
      if (locatePartialIndex != -1) {
        for (; searchCompare(0, vector[row]) > 0 && row < count; ++row)
          ;
        if (vector[row] == 0)
          return -1;
        if (compareRow(row))
          return row;
      } else {
        for (; searchCompare(0, vector[row]) == 0 && row < count; ++row) {
          if (columnCount == locateColumnCount || compareRow(row))
            return row;
        }
      }
    }
    return -1;
  }
  
  private final int keyLocateBackwards(int row) {
    if (row >= 0 && row < vector.length) {
      if (locatePartialIndex != -1) {
        if (vector[row] == 0)
          return -1;
        if (compareRow(row))
          return row;
      } else {
        for (; searchCompare(0, vector[row]) == 0 && row > -1; --row) {
          if (columnCount == locateColumnCount || compareRow(row))
            return row;
        }
      }
    }
    return -1;
  }
  
  @Override
  public void uniqueCheck(long internalRow, RowVariant[] values,
      boolean updating)
  /*-throws DataSetException-*/
  {
    if (descriptor.isUnique() && lastRow > -1) {
      for (int index = 0; index < columnCount; ++index) {
        if (!updating || values[columns[index].getOrdinal()].changed) {
          for (int index2 = 0; index2 < columnCount; ++index2)
            keyDataColumns[index2].setVariant(0,
                values[columns[index2].getOrdinal()]);
          columnCompareCount = columnCount;
          find(0, true);
          if (comp == 0)
            ValidationException.duplicateKey(memoryData.dataSet, descriptor);
          return;
        }
      }
    }
  }
  
  @Override
  public void setInsertPos(int pos)
  /*-throws DataSetException-*/
  {
    insertPos = pos;
  }
  
  @Override
  public long moveRow(long longPos, long longDelta)
  /*-throws DataSetException-*/
  {
    int pos = (int) longPos;
    int delta = (int) longDelta;
    int destPos = pos + delta;
    if (destPos > lastRow)
      delta = lastRow - pos;
    if (destPos < 0)
      delta = -pos;
    destPos = pos + delta;
    
    if (keyDataColumns.length > 1) {
      columnCompareCount = keyDataColumns.length - 1;
      int findPos;
      if (destPos < pos) {
        findPos = find(vector[pos], true);
        if (destPos < findPos)
          delta = pos - findPos;
      } else {
        findPos = find(vector[pos], false);
        if (destPos > findPos)
          delta = pos - findPos;
      }
    }
    
    if (delta != 0) {
      long internalRow = vector[pos];
      // int order = insertColumn.getInt((int) internalRow);
      deleteStore(internalRow);
      insertPos = pos + delta;
      addStore(internalRow);
      insertPos = -1;
      return delta;
    }
    
    return 0;
  }
  
  // public static int searchCount;
  // public static int backCount;
  // public static int foreCount;
  
  private final DataColumn[] keyDataColumns;
  private final Column[] columns;
  private int columnCount;
  private final boolean descending;
  private final boolean caseInsensitive;
  private int replaceRow;
  private final KeyComparator comparator;
  private int columnCompareCount;
  private IntColumn insertColumn;
  private int insertPos;
  
  @Override
  public boolean isSorted() {
    return true;
  }
}
