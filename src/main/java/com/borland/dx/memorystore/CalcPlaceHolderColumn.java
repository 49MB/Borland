//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.memorystore;

import com.borland.dx.dataset.Variant;
import com.borland.jb.util.Diagnostic;

// Do not make public.
//
class CalcPlaceHolderColumn extends DataColumn {

  public CalcPlaceHolderColumn(NullState nullState) {
    super(nullState);
    vectorLength  = InitialSize;
  }

  void copy(int source, int dest) {
  }

   final void  grow(int newLength) {
    vectorLength  = newLength;
  }

  int compare(int index1, int index2) {
    Diagnostic.fail();
    return 0;
  }

  int compareIgnoreCase(int index1, int index2) {
    Diagnostic.fail();
    return 0;
  }

  void  getVariant(int index, Variant value) { }

  void  setVariant(int index, Variant val) { }
}
