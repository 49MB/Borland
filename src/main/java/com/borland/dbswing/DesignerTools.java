package com.borland.dbswing;

import com.borland.dx.dataset.StorageDataSet;
import com.borland.dx.dataset.Variant;

public class DesignerTools {
  
  public static boolean isDesignMode()
  {
    return java.beans.Beans.isDesignTime();
  }
  
  public static void checkDesignMode()
  {
    if (!isDesignMode())
      throw new IllegalStateException("Use DesignTools only in DesignTime!");
  }
  
  public static StorageDataSet getDesignDataSet()
  {
    checkDesignMode();
    
    StorageDataSet nullTable = new StorageDataSet();
    for (int i = 0; i < 10; i++)
    {
      char ch = (char) ('A'+i);
      String name = new String(new char[]{ch});
      nullTable.addColumn(name, Variant.STRING);
    }
    nullTable.open();
    for (int i = 0; i < 10; i++)
    {
      nullTable.insertRow(false);
      for (int j = 0; j < 10; j++)
      {
        char ch = (char)('A'+j);
        String text = new String(new char[]{ch});
        nullTable.setString(j, text);
      }
      nullTable.post();
    }
    nullTable.first();
    return nullTable;
  }
}
