//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.util;
import java.util.Hashtable;

import javax.sql.DataSource;


public abstract class JndiDataSourceRepository extends DataSourceRepository {

  public DataSource getDataSource(String name) {
    DataSource source = null;
    try {
      javax.naming.Context ctx = new javax.naming.InitialContext();
      source = (DataSource)ctx.lookup(name);
    }
    catch(Exception ex) {
      DataSourceException.throwExceptionChain(ex);
    }
    return source;
  }
  public void       addDataSource(String name, DataSource dataSource) {
  }
  public void       removeDataSource(String name) {
  }
  public abstract Hashtable  	 getDataSources();

}
