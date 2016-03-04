//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.memorystore;

import com.borland.dx.dataset.Variant;

// Do not make public.
//
class TimestampColumn extends LongColumn {
  
  TimestampColumn(NullState nullState) {
    super(nullState);
  }
  
  @Override
  final void copy(int source, int dest) {
    super.copy(source, dest);
  }
  
  @Override
  final void  grow(int newLength) {
    super.grow(newLength);
  }
  
  @Override
  final int compare(int index1, int index2) {
    intResult  = super.compare(index1, index2);
    return intResult;
  }
  
  @Override
  final int compareIgnoreCase(int index1, int index2) {
    return compare(index1, index2);
  }
  
  @Override
  final void  getVariant(int index, Variant value) {
    if (hasNulls && (nullState.vector[index] & nullMask) != 0)
      nullState.getNull(index, value, nullMask, assignedMask);
    else
      value.setTimestamp(vector[index]);
  }
  
  @Override
  void  setVariant(int index, Variant val) {
    if (val.isNull()) {
      // Force High sort.  Not perfect because its a possible value.
      //
      vector[index]     = NULL_LONG;
      setNull(index, val.getType());
    }
    else {
      if (hasNulls)
        nullState.vector[index] &= ~nullMask;
      vector[index]     = val.getAsLong();
    }
  }
  
  
  int         intResult;
}
