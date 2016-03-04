//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.util;
import java.sql.Connection;
import java.util.Hashtable;
import java.util.Properties;

import javax.sql.DataSource;


public abstract class DataSourceRepository {

  public void       open() {}
  public void       close() {}

  public Connection getConnection(String name)
    throws java.sql.SQLException
  {
    return getDataSource(name).getConnection();
  }
  public abstract DataSource getDataSource(String name);
  public abstract void       addDataSource(String name, DataSource dataSource);
  public abstract void       removeDataSource(String name);
  public abstract Hashtable  getDataSources();
  public abstract void       load(DataSource dataSource, Properties props);
  public abstract void       store(DataSource dataSource, Properties props);
}
