//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jb.io;

import java.io.File;

/**
  * This interface is used internally by other
  * <code>com.borland</code> classes. You should
  * never use this interface directly.
  */
public interface TraverseAction
{
  static public final int DIVE_TRAVERSE     = 1;
  static public final int CONTINUE_TRAVERSE = 2;
  static public final int ABORT_TRAVERSE    = 3;

  /**
   * This method is used internally by other
   * <code>com.borland</code> classes. You should
   * never use this method directly.
 */
  public int onFile(File file) throws Exception;

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  public int onDirEntry(File file) throws Exception;

  /**
    * This method is used internally by other
    * <code>com.borland</code> classes. You should
    * never use this method directly.
 */
  public int onDirExit(File file, String[] fileList) throws Exception;
}
