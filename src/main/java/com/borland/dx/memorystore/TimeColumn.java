//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.memorystore;

import com.borland.dx.dataset.LocalDateUtil;
import com.borland.dx.dataset.Variant;

// Do not make public.
//
class TimeColumn extends LongColumn {
  
  TimeColumn(NullState nullState) {
    super(nullState);
  }
  
  @Override
  final void getVariant(int index, Variant value) {
    if (hasNulls && (nullState.vector[index] & nullMask) != 0)
      nullState.getNull(index, value, nullMask, assignedMask);
    else
      LocalDateUtil.setAsLocalTime(value, vector[index], null);
  }
  
  @Override
  final void setVariant(int index, Variant val) {
    if (val.getTime() == null)
      val.setUnassignedNull();
    
    if (val.isNull()) {
      // Force High sort. Not perfect because its a possible value.
      //
      vector[index] = NULL_LONG;
      setNull(index, val.getType());
    } else {
      if (hasNulls)
        nullState.vector[index] &= ~nullMask;
      vector[index] = LocalDateUtil.getLocalTimeAsLong(val.getTime(), null);
    }
  }
}
