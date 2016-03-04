package com.borland.dx.dataset;

public interface LookupFallback {
  boolean lookupFallback(DataSet plds, ReadRow pickListRow,
      String[] pickListColumns, Column displayColumn, Variant result);
}
