//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.EventListener;

public interface MasterNavigateListener extends EventListener {
  public void masterNavigating(MasterNavigateEvent event) /*-throws DataSetException-*/;
  public void masterNavigated(MasterNavigateEvent event) /*-throws DataSetException-*/;
}

