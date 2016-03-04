package com.borland.dx.dataset;



/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: Softwareschmiede Höffl GmbH</p>
 *
 * @author unbekannt
 * @version 1.0
 */
public interface ColumnCompare
{
  public int compare(DataSet dataSet, Column column, Variant a, Variant b);
  public int compareIgnoreCase(DataSet dataSet, Column column, Variant a, Variant b);
}
