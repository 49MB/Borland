package com.borland.dx.dataset;

public class SimpleCopyRow implements CopyRow {

  @Override
  public void beforePost(DataSet dataSet) {
  }

  @Override
  public boolean canCopyColumn(Column column) {
    return column.canDitto();
  }

  @Override
  public boolean canCopyDataSet(DataSet dataSet) {
    return true;
  }

  @Override
  public boolean canCopyRow(ReadRow dataSet) {
    return true;
  }

  @Override
  public boolean copyData(DataSet dataSet, Column column, Variant value) {
    return true;
  }

  @Override
  public void initRow(DataSet row) {
  }

}
