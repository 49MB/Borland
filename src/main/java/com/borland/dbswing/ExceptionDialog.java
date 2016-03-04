package com.borland.dbswing;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: Softwareschmiede Höffl GmbH</p>
 *
 * @author unbekannt
 * @version 1.0
 */
public interface ExceptionDialog {
  public void setDisplayChainedExceptions(boolean displayChains);
  public void setDisplayStackTraces(boolean displayStack);
  public void setAllowExit(boolean allowExit);
  public void setCloseDataStoresOnExit(boolean closeDataStores);
  public void setCloseConnectionsOnExit(boolean closeConnections);
  public void setEnableSecretDebugKey(boolean enableSecretKey);
  public void show();
  public boolean isVisible();
  public void dispose();
}
