package com.borland.dbswing;

import java.awt.Component;

import com.borland.dx.dataset.DataSet;

/**
 * <p>Title: </p>
 * Update Current Data Set
 *
 * <p>Description: </p>
 * Interface for DBUtilities.updateCurrentDataSet
 * This will allow any Component to get updates when DataSet changes.
 * Just implement these interface
 * isAutoDetect must return true to work
 * updateCurrentDataSet ist called when Component moves
 * getDataSetAwareComponents can return null (because its deprecated)
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: Softwareschmiede Hoeffl GmbH</p>
 *
 * @author unbekannt
 * @version 1.0
 */
public interface UpdateCurrentDataSet {
  boolean isAutoDetect();
  void updateCurrentDataSet(Component c, DataSet dataSet);
  Component[] getDataSetAwareComponents();
}
