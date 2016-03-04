//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.util;

import com.borland.dx.dataset.DataSetException;

public class DataSourceException extends com.borland.dx.dataset.DataSetException {
  public DataSourceException(String message) {
    super(message);
  }
  public DataSourceException(Throwable ex) {
    super(DataSetException.EXCEPTION_CHAIN, ex.getMessage(), ex);
  }
}

