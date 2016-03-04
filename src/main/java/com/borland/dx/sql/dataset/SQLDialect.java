//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;


/**
 * The SQLDialect interface defines constants for SQL database servers.
*/

public interface SQLDialect
{
  /**
   * A server of an unknown type.
   */
  public final static int UNKNOWN   = 0x1;
  /**
   * An InterBase Server
   */
  public final static int INTERBASE = 0x2;
  /**
   * An Oracle Server
   */
  public final static int ORACLE    = 0x3;
}

