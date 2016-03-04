//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.util.EventListener;

/**
 * This interface is used as a mechanism by which a status bar gets its information from a DataSet.
 * Use this interface to customize or suppress the message sent from the DataSet to the status bar.
 */
public interface StatusListener extends EventListener
{
/**
 * Called when a message is being sent to the status bar and other status listeners.
 * @param event   The event that prompted the message to be sent.
 */
  public void statusMessage(StatusEvent event);
}
