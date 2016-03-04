//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

//
// Tokens for light-weight SQL Parser
//
// Not public.
//
/**
 * This interface is used internally by other com.borland classes. You should
 * never use this interface directly.
 */
interface SQLToken {
  public static final int UNKNOWN = 0;
  public static final int SELECT = 1;
  public static final int FIELD = 2;
  public static final int CONSTANT = 3;
  public static final int STRING = 4;
  public static final int FUNCTION = 5;
  public static final int EXPRESSION = 6;
  public static final int FROM = 7;
  public static final int TABLE = 8;
  public static final int JOIN = 9;
  public static final int WHERE = 10;
  public static final int PARAMETER = 11;
  public static final int COMMENT = 12;
  public static final int OTHER = 13;
  public static final int GROUP = 14;
  public static final int HAVING = 15;
  public static final int ORDER = 16;
}
