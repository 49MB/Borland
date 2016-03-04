//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.util;


/**
 * The <code>ExceptionHandler</code> interface allows an object to
 * generically handle exceptions.
 */
public interface ExceptionHandler
{
  /**
   * This method processes the exception as appropriate.
   * @param x The exception to handle.
   */
  public void handleException(Exception ex);
}
