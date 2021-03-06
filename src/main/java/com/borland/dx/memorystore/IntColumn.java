//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.memorystore;

import com.borland.dx.dataset.Variant;
import com.borland.jb.util.Diagnostic;

// Do not make public.
//
class IntColumn extends DataColumn {
  // private class Dummy {
  // int joke;
  // }
  
  public IntColumn(NullState nullState) {
    super(nullState);
    vector = new int[InitialSize];
    vectorLength = vector.length;
  }
  
  @Override
  final void copy(int source, int dest) {
    vector[dest] = vector[source];
    if (hasNulls)
      nullState.copy(source, dest, nullMask);
  }
  
  @Override
  final void grow(int newLength) {
    Diagnostic.check(newLength > vector.length);
    int newVector[] = new int[newLength];
    System.arraycopy(vector, 0, newVector, 0, vectorLength);
    vector = newVector;
    vectorLength = vector.length;
  }
  
  @Override
  final int compare(int index1, int index2) {
    if (hasNulls) {
      if ((comp = nullState.compare(index1, index2, nullMask)) != 0)
        return comp;
    }
    // Diagnostic.check(index1 > -1);
    int val1 = vector[index1];
    int val2 = vector[index2];
    if (val1 < val2)
      return -1;
    if (val1 > val2)
      return 1;
    return 0;
  }
  
  @Override
  final int compareIgnoreCase(int index1, int index2) {
    return compare(index1, index2);
  }
  
  @Override
  final void getVariant(int index, Variant value) {
    if (hasNulls && (nullState.vector[index] & nullMask) != 0)
      nullState.getNull(index, value, nullMask, assignedMask);
    else
      value.setInt(vector[index]);
  }
  
  @Override
  final void setVariant(int index, Variant val) {
    if (val.isNull()) {
      // Force High sort. Not perfect because its a possible value.
      //
      vector[index] = NULL_INT;
      setNull(index, val.getType());
    } else {
      if (hasNulls)
        nullState.vector[index] &= ~nullMask;
      vector[index] = val.getInt();
    }
  }
  
  final int getInt(int index) {
    Diagnostic.check(!hasNulls);
    return vector[index];
  }
  
  final void setInt(int index, int val) {
    Diagnostic.check(!hasNulls);
    vector[index] = val;
  }
  
  final void shift(int[] indexVector, int internalRow, int insertPos,
      int rowCount) {
    if (insertPos < 0)
      vector[internalRow] = rowCount;
    else {
      int value = vector[indexVector[insertPos]];
      for (int index = 0; index < insertPos; ++index)
        vector[indexVector[index]] = index;
      for (int index = insertPos; index < rowCount; ++index)
        vector[indexVector[index]] = index + 1;
      vector[internalRow] = insertPos;
    }
  }
  
  int[] vector;
  
}
