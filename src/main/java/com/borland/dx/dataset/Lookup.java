package com.borland.dx.dataset;

public interface Lookup {
  public void close();
  
  public void lookup(DataSet dataSet, int row, Variant value);
  
  public void lookup(ReadRow readRow, Variant value);
  
  public boolean isCalcField();
  
  public void fillIn(DataSet dataSet, Variant value);
}
