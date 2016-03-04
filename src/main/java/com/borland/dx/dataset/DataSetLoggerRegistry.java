package com.borland.dx.dataset;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class DataSetLoggerRegistry
{
  Map<String, DataSetLoggerBean> registry = new HashMap<String, DataSetLoggerBean>();
  DataSetLoggerBeanConfiguration defaultConfiguration = new DataSetLoggerBeanConfiguration();
  // Singleton
  private static DataSetLoggerRegistry instance = null;

  public static DataSetLoggerRegistry getInstance()
  {
    if (instance == null)
    {
      instance = new DataSetLoggerRegistry();
    }
    return instance;
  }

  private DataSetLoggerRegistry()
  {

  }

  // API
  /**
   * Meldet den DataSet zum Logging an. Die Werte aller Columns werden
   * ausgegeben.
   *
   * @param ds
   * @return true = der DataSet wurde erfolgreich zum Logging angemeldet, false =
   *         sonst.
   */
  public boolean registerDataSet(DataSet ds)
  {
    return registerDataSet(ds, null, null);
  }


  /**
   * Meldet den DataSet zum Logging an. Die Werte der übergebenen Columns werden
   * ausgegeben.
   *
   * @param ds
   * @param cols
   * @return true = der DataSet wurde erfolgreich zum Logging angemeldet, false =
   *         sonst.
   */
  public boolean registerDataSet(DataSet ds, Column[] cols)
  {
   return registerDataSet(ds, null, cols);
  }

  /**
   * Meldet den DataSet zum Logging an. Die Werte der übergebenen Columns werden
   * ausgegeben.
   *
   * @param ds
   * @param cols
   * @return true = der DataSet wurde erfolgreich zum Logging angemeldet, false =
   *         sonst.
   */
  public boolean registerDataSet(DataSet ds, DataSetLoggerBeanConfiguration conf, Column[] cols)
  {
    String tableName = ds.getTableName();
    // Wenn der DataSet nicht einer Tabelle zuzuordnen ist, dann kann er nicht
    // angemeldet werden.
    if (tableName == null || tableName.length() == 0)
      return false;
    // Ist der DataSet bereits angemeldet?
    if (registry.containsKey(tableName))
      return false;

    Column[] columns = null;
    if (cols == null)
    {
      // alle Columns des DataSets sind zu berücksichtigen
      columns = ds.getColumns();
    }
    else
    {
      columns = cols;
    }
    DataSetLoggerBeanConfiguration configuration = this.defaultConfiguration;
    if(conf != null)
      configuration = conf;
    DataSetLoggerBean bean = new DataSetLoggerBean(ds, configuration,
        columns);
    registry.put(tableName, bean);
    return true;
  }

  public Collection<DataSetLoggerBean> getAllDataSetLoggerBeans()
  {
    Collection<DataSetLoggerBean> allBeans = registry.values();
    return allBeans;
  }

  public boolean hasEntries()
  {
    return !registry.isEmpty();
  }

  public boolean isTableRegistered(String tableName)
  {
    return registry.containsKey(tableName);
  }

  public boolean isTableActive(String tableName)
  {
    DataSetLoggerBean dsLoggerBean = registry.get(tableName);
    return dsLoggerBean != null ? dsLoggerBean.isActive() : false;
  }
  public void setTableActive(String tableName, boolean isActive)
  {
    DataSetLoggerBean dsLoggerBean = registry.get(tableName);
    if(dsLoggerBean != null)
    {
      dsLoggerBean.setActive(isActive);
    }
  }

  // getter/setter

  public DataSetLoggerBeanConfiguration getDefaultConfiguration()
  {
    return defaultConfiguration;
  }

  public void setDefaultConfiguration(
      DataSetLoggerBeanConfiguration defaultConfiguration)
  {
    this.defaultConfiguration = defaultConfiguration;
  }
}
