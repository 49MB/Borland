//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.memorystore;

import java.io.InputStream;

import com.borland.dx.dataset.Variant;

// Do not make public.
//
class BinaryStreamColumn extends StreamColumn {

  BinaryStreamColumn(NullState nullState) {
    super(nullState);
  }

  final void getVariant(int row, Variant value) {
    if (hasNulls && vector[row] == NULL_STREAM)
      nullState.getNull(row, value, nullMask, assignedMask);
    else
      value.setInputStream(vector[row]);
  }

  final void setVariant (int index, Variant val) {
    if (val.isNull()) {
      vector[index] = NULL_STREAM;
      setNull(index, val.getType());
    }
    else {
      if (hasNulls)
        nullState.vector[index] &= ~nullMask;
      InputStream stream  = val.getInputStream();
 //!     if (stream == null)
//!        stream  = NULL_STREAM;
      vector[index]  = stream;
    }
  }
}
