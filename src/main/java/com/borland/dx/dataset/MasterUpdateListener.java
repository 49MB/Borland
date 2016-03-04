//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.EventListener;

public interface MasterUpdateListener extends EventListener {
  public void masterDeleting(MasterUpdateEvent event) throws Exception;
  
  public void masterEmptying(MasterUpdateEvent event) throws Exception;
  
  public void masterChanging(MasterUpdateEvent event) throws Exception;
  
  public void masterCanChange(MasterUpdateEvent event) throws Exception;
}
