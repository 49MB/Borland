//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.util;

import java.util.EventListener;

/**
 * The ExceptionDispatch interface is an interface for events
 * that {@link com.borland.jb.util.EventMulticaster} can send to multiple listeners.
 * The event can throw exceptions
 */
public interface ExceptionDispatch
{
/**
 * Sends an event that to the specified listener.
 * @param listener    The object listening for the event.
 * @throws Exception
 */
  public void exceptionDispatch(EventListener listener) throws Exception;
}
