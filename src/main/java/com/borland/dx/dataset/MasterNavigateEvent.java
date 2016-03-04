//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.EventListener;

import com.borland.jb.util.Diagnostic;

public class MasterNavigateEvent extends com.borland.jb.util.DispatchableEvent
implements DxDispatch
{
  public static final int NAVIGATED   = 1;
  public static final int NAVIGATING  = 2;
  
  public MasterNavigateEvent(Object source, boolean canceling, int id) {
    super(source);
    this.id         = id;
    this.canceling  = canceling;
  }
  
  public void setColumn(Column column)
  {
    this.column = column;
  }
  
  public Column getColumn()
  {
    return column;
  }
  
  public boolean isColumnsAffected(String[] columnNames)
  {
    if (columnNames == null)
      return false;
    
    if (column == null)
      return true;
    
    String myColumn = column.getColumnName();
    for (String col : columnNames)
    {
      if (col.equalsIgnoreCase(myColumn))
        return true;
    }
    return false;
  }
  
  public void dxDispatch(EventListener listener)
  /*-throws DataSetException-*/
  {
    switch(id) {
    case NAVIGATING:
      ((MasterNavigateListener)listener).masterNavigating(this);
      break;
    case NAVIGATED:
      ((MasterNavigateListener)listener).masterNavigated(this);
      break;
    default:
      Diagnostic.fail();
    }
  }
  
  @Override
  public void dispatch(EventListener listener)
  {
    Diagnostic.fail();
  }
  
  int     id;
  boolean canceling;
  Column column;
  
  public boolean isCanceling()
  {
    return canceling;
  }
}
