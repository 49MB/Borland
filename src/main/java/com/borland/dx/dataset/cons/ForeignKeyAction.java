//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset.cons;


 /**
  * Possible values for a foreign key update or delete action.
  * The meanings are as specified by the sql-92 standard.
  */
public interface ForeignKeyAction {
  static final int RESTRICT     = 0;
  static final int NO_ACTION    = RESTRICT;
  static final int CASCADE      = 1;
  static final int SET_NULL     = 2;
  static final int SET_DEFAULT  = 3;
}