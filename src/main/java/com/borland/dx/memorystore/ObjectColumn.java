//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.memorystore;

import com.borland.dx.dataset.Variant;
import com.borland.jb.util.Diagnostic;

// Do not make public.
//
class ObjectColumn extends DataColumn {

  public ObjectColumn(NullState nullState) {
    super(nullState);
    vector        = new Object[InitialSize];
    vectorLength  = vector.length;
  }

  final void copy(int source, int dest) {
    vector[dest]  = vector[source];
    // Diagnostic.check(vector[dest] != null);
    if (hasNulls)
      nullState.copy(source, dest, nullMask);
  }

   void  grow(int newLength) {
    Diagnostic.check(newLength > vector.length);
    Object newVector[] = new Object[newLength];
    System.arraycopy(vector, 0, newVector, 0, vectorLength);
    vector        = newVector;
    vectorLength  = vector.length;
  }

  // Technically this only provides "equal" functionality, not relative
  // comparison.  Should not rely on this implementation for "sorting.
  //
  final int compare(int index1, int index2) {
//!    Diagnostic.println("compare:  "+vector[index1]+" "+vector[index2]+" "+index2);
    if (hasNulls) {
      if ((comp = nullState.compare(index1, index2, nullMask)) != 0)
        return comp;
    }
    if (vector[index1] != null && vector[index1].equals(vector[index2]))
      return 0;
    return 1;
  }

  final int compareIgnoreCase(int index1, int index2) {
    Diagnostic.fail();
    return 0;
  }

  void  getVariant(int row, Variant value) {
    if (hasNulls && vector[row] == null)
      nullState.getNull(row, value, nullMask, assignedMask);
    else
      value.setObject(vector[row]);
  }

  void  setVariant(int index, Variant val){
    if (val.isNull()) {
      vector[index] = null;
      setNull(index, val.getType());
    }
    else {
      Object object  = val.getObject();
      if (hasNulls)
        nullState.vector[index] &= ~nullMask;
      vector[index]  = object;
    }
  }

  private   Class     javaClass;
   Object[]  vector;
}
