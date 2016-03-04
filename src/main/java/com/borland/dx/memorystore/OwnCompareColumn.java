package com.borland.dx.memorystore;

import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.ColumnCompare;
import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.Variant;
import com.borland.jb.util.Diagnostic;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: Softwareschmiede Höffl GmbH
 * </p>
 * 
 * @author unbekannt
 * @version 1.0
 */
public class OwnCompareColumn extends DataColumn {
  DataSet dataSet;
  Column column;
  ColumnCompare columnCompare;
  Variant pivotValue;
  
  Variant[] vector;
  
  OwnCompareColumn(NullState nullState, DataSet dataSet, Column column,
      ColumnCompare columnCompare) {
    super(nullState);
    this.dataSet = dataSet;
    this.column = column;
    this.columnCompare = columnCompare;
    
    vector = new Variant[InitialSize];
    vectorLength = vector.length;
  }
  
  // Used for quickSort. Does not compare nulls becuase insertion
  // sort will follow that does.
  //
  @Override
  int forwardCompare(int leftPivot, boolean caseInsensitive, boolean descending) {
    if (descending) {
      if (caseInsensitive) {
        while ((comp = compareIgnoreCase(indexVector[++leftPivot], pivotValue)) > 0)
          ;
      } else {
        while ((comp = compare(indexVector[++leftPivot], pivotValue)) > 0)
          ;
      }
    } else {
      if (caseInsensitive) {
        while ((comp = compareIgnoreCase(indexVector[++leftPivot], pivotValue)) < 0)
          ;
      } else {
        while ((comp = compare(indexVector[++leftPivot], pivotValue)) < 0)
          ;
      }
    }
    return leftPivot;
  }
  
  // Used for quickSort. Does not compare nulls becuase insertion
  // sort will follow that does.
  //
  @Override
  int reverseCompare(int rightPivot, boolean caseInsensitive, boolean descending) {
    if (descending) {
      if (caseInsensitive) {
        while ((comp = compareIgnoreCase(indexVector[--rightPivot], pivotValue)) < 0)
          ;
      } else {
        Diagnostic.check(rightPivot > 0);
        while ((comp = compare(indexVector[--rightPivot], pivotValue)) < 0)
          Diagnostic.check(rightPivot > 0);
      }
    } else {
      if (caseInsensitive) {
        while ((comp = compareIgnoreCase(indexVector[--rightPivot], pivotValue)) > 0)
          ;
      } else {
        while ((comp = compare(indexVector[--rightPivot], pivotValue)) > 0)
          ;
      }
    }
    return rightPivot;
  }
  
  @Override
  boolean partialCompare(int index1, int index2, boolean caseInsensitive,
      boolean full) {
    
    // ! Diagnostic..println(vector[index1]+" "+ vector[index2]);
    if (full)
      return fullPartialCompare(vector[index1], vector[index2], caseInsensitive);
    else
      return partialCompare(vector[index1], vector[index2], caseInsensitive);
  }
  
  boolean fullPartialCompare(Variant var1, Variant var2, boolean caseInsensitive) {
    String s1 = "";
    if (var1 != null)
      s1 = var1.toString();
    String s2 = "";
    if (var2 != null)
      s2 = var2.toString();
    return fullPartialCompare(s1, s2, caseInsensitive);
  }
  
  boolean partialCompare(Variant var1, Variant var2, boolean caseInsensitive) {
    String s1 = "";
    if (var1 != null)
      s1 = var1.toString();
    String s2 = "";
    if (var2 != null)
      s2 = var2.toString();
    return s1.regionMatches(caseInsensitive, 0, s2, 0, s2.length());
  }
  
  private void lookup(int row) {
    if (column.getPickList() == null)
      return;
    
    vector[row] = new Variant();
    dataSet.getStorageDataSet().getStorageVariant(row, column.getOrdinal(),
        vector[row]);
  }
  
  /**
   * compare
   * 
   * @param row1
   *          int
   * @param row2
   *          int
   * @return int
   */
  @Override
  int compare(int row1, int row2) {
    lookup(row1);
    lookup(row2);
    return columnCompare.compare(dataSet, column, vector[row1], vector[row2]);
  }
  
  /**
   * compareIgnoreCase
   * 
   * @param row1
   *          int
   * @param row2
   *          int
   * @return int
   */
  @Override
  int compareIgnoreCase(int row1, int row2) {
    lookup(row1);
    lookup(row2);
    return columnCompare.compareIgnoreCase(dataSet, column, vector[row1],
        vector[row2]);
  }
  
  int compareIgnoreCase(int row, Variant var) {
    lookup(row);
    return columnCompare.compareIgnoreCase(dataSet, column, vector[row], var);
  }
  
  int compare(int row, Variant var) {
    lookup(row);
    return columnCompare.compare(dataSet, column, vector[row], var);
  }
  
  @Override
  void setPivot(int indexVector[], int pivotDataRow) {
    this.indexVector = indexVector;
    this.pivotValue = vector[pivotDataRow];
  }
  
  /**
   * copy
   * 
   * @param source
   *          int
   * @param dest
   *          int
   */
  @Override
  void copy(int source, int dest) {
    vector[dest] = vector[source];
    if (hasNulls)
      nullState.copy(source, dest, nullMask);
  }
  
  /**
   * getVariant
   * 
   * @param row
   *          int
   * @param val
   *          Variant
   */
  @Override
  void getVariant(int row, Variant val) {
    if (hasNulls && (nullState.vector[row] & nullMask) != 0)
      nullState.getNull(row, val, nullMask, assignedMask);
    else {
      if (hasNulls)
        nullState.vector[row] &= ~nullMask;
      val.setVariant(vector[row]);
    }
  }
  
  /**
   * grow
   * 
   * @param length
   *          int
   */
  @Override
  void grow(int newLength) {
    Diagnostic.check(newLength > vector.length);
    Variant newVector[] = new Variant[newLength];
    System.arraycopy(vector, 0, newVector, 0, vectorLength);
    vector = newVector;
    vectorLength = vector.length;
  }
  
  /**
   * setVariant
   * 
   * @param row
   *          int
   * @param val
   *          Variant
   */
  @Override
  void setVariant(int row, Variant val) {
    if (val.isNull()) {
      vector[row] = null;
      setNull(row, val.getType());
    } else {
      if (hasNulls)
        nullState.vector[row] &= ~nullMask;
      Variant newVal = new Variant();
      newVal.setVariant(val);
      vector[row] = newVal;
    }
  }
}
