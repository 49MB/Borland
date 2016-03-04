package com.borland.dx.dataset;

public class LookupFactory {
  public interface LookupCreater {
    Lookup createLookup(StorageDataSet dataSet, Column column,
        PickListDescriptor pickList);
  }
  
  private static LookupCreater lookupCreater = null;
  
  public static Lookup createLookup(StorageDataSet dataSet, Column column,
      PickListDescriptor pickList) {
    if (lookupCreater == null)
      return new LookupDefault(dataSet, column, pickList);
    else
      return lookupCreater.createLookup(dataSet, column, pickList);
  }
  
  public static void setLookupCreater(LookupCreater lookupCreater) {
    LookupFactory.lookupCreater = lookupCreater;
  }
}
