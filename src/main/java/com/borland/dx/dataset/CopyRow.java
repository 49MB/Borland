package com.borland.dx.dataset;

/**
 * <p>Title: </p>
 * Copy Row
 *
 * <p>Description: </p>
 * Interface for managing the dittoRowDetails-Methode of DataSet
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: Softwareschmiede Höffl GmbH</p>
 *
 * @author Stefan Schmaltz
 * @version 1.0
 */
public interface CopyRow
{
  public boolean canCopyRow(ReadRow dataSet);
  public boolean canCopyColumn(Column column);
  public boolean canCopyDataSet(DataSet dataSet);
  public void initRow(DataSet row);
  public boolean copyData(DataSet dataSet, Column column, Variant value);
  public void beforePost(DataSet dataSet);
}
