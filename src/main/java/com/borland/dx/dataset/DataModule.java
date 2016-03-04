//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

/**
 * The DataModule class is intended to be a base class which developers extend for their own needs.
 */
public interface DataModule extends Designable
{
  /**
   * This static method is used to allow multiple apps to share a common DataModule. So rather
   * than using:
   *    DataModule1 myDM = new DataModule1()
   * you would use:
   *    DataModule1 myDM = DataModule1.getDataModule()
   *
   * @return Either the newly created DataModule or a clone if that is not permitted.
   */

//! public static DataModule1 getDataModule() {}
}
