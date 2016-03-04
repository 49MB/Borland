//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.metadata;

import com.borland.dx.dataset.Column;

abstract public class DataSetAnalyzer
{
  /**
  *  Returns the number of columns in a DataSet.
  */
  public abstract int getColumnCount();

  /**
  *  Returns a Column component for given column index.
  */
  public abstract Column getColumn(int ordinal) throws MetaDataException;

  /**
  *  Call this to release resources correctly.
  */
  public void close() {};
}
