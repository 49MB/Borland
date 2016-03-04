package com.borland.dx.dataset;

import java.util.ArrayList;
import java.util.List;

public class DataSetLoggerBean
{
  public static final int SHOW_NO_ROWS = 0;
  public static final int SHOW_ALL_ROWS = Integer.MAX_VALUE;
  private DataSetLoggerBeanConfiguration configuration;
  private DataSet dataSet;
  private String tableName;
  private int numberOfRows = SHOW_ALL_ROWS;
  private boolean isActive = true;

  private List<Column> columns = new ArrayList<Column>();

  DataSetLoggerBean(DataSet dataSet, DataSetLoggerBeanConfiguration conf)
  {
    this(dataSet, conf, null);
  }

  DataSetLoggerBean(DataSet dataSet, DataSetLoggerBeanConfiguration conf,
      Column[] cols)
  {
    this.dataSet = dataSet;
    this.tableName = dataSet.getTableName();
    this.configuration = conf;
    if (cols != null)
    {
      for (Column col : cols)
      {
        addColumn(col);
      }
    }
  }

  public String getFormattedLogString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("------------------------------");
    sb.append("\nDataSet: " + this.getTableName() + "["
        + dataSet.getClass().getName() + "]");
    String shortName = this.identifyShortTableName();
    if (shortName != null && dataSet.isOpen())
    {
      sb.append("\n active row id=" + dataSet.getInt(shortName + "_ID"));
    }
    sb.append("\n");
    sb.append(configuration.isOpen() ? "open=" + dataSet.isOpen() + " " : "");
    if (dataSet.isOpen())
    {
      sb.append(configuration.isEditing() ? "editing=" + dataSet.isEditing()
          + " " : "");
      sb
          .append(configuration.getCurrentRow() != DataSetLoggerBeanConfiguration.NON_VALID_VALUE ? "curRow="
              + dataSet.getRow() + " "
              : "");
      sb
          .append(configuration.getInternalRow() != DataSetLoggerBeanConfiguration.NON_VALID_VALUE ? "curRow="
              + dataSet.getInternalRow() + " "
              : "");
      sb.append("\nrows=" + dataSet.getRowCount());
      sb.append("\n");
      sb.append(formattedRowData());
    }
    return sb.toString();
  }

  private String formattedRowData()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(" ----Data----");
    DataSetView clone = dataSet.cloneDataSetViewFast();
    try
    {
      clone.open();
      int row = 0;
      while (clone.inBounds() && row++ < numberOfRows)
      {
        sb.append("\n");
        for (Column col : columns)
        {
          sb.append(col.getColumnName() + ":" + getValue(clone, col) + ", ");
        }
        clone.next();
      }
    }
    finally
    {
      clone.close();
    }
    return sb.toString();
  }

  private String getValue(DataSetView dsv, Column col)
  {
    Variant var = new Variant();
    dsv.getVariant(col.getColumnName(), var);

    switch (col.getDataType())
    {
      case Variant.INT:
        return Integer.toString(var.getAsInt());
      case Variant.LONG:
        return Long.toString(var.getAsLong());
      case Variant.STRING:
        return var.getString();
      default:
        return "N.A.";
    }

  }

  private String identifyShortTableName()
  {
    if (columns.size() != 0)
    {
      return columns.get(0).getColumnName().substring(0, 3);
    }
    return null;
  }

  // getters setters
  boolean isActive()
  {
    return isActive;
  }

  void setActive(boolean isActive)
  {
    this.isActive = isActive;
  }

  DataSetLoggerBeanConfiguration getConfiguration()
  {
    return configuration;
  }

  void setConfiguration(DataSetLoggerBeanConfiguration configuration)
  {
    this.configuration = configuration;
  }

  DataSet getDataSet()
  {
    return dataSet;
  }

  void setDataSet(DataSet dataSet)
  {
    this.dataSet = dataSet;
  }

  String getTableName()
  {
    return tableName;
  }

  void setTableName(String tableName)
  {
    this.tableName = tableName;
  }

  void addColumn(Column column)
  {
    columns.add(column);
  }

  public int getNumberOfRows()
  {
    return numberOfRows;
  }

  public void setNumberOfRows(int numberOfRows)
  {
    this.numberOfRows = numberOfRows;
  }

  void removeColumn(String column)
  {
    columns.remove(column);
  }

  // overwrite
  public boolean equals(Object obj)
  {
    return super.equals(obj);
  }

  public int hashCode()
  {
    return super.hashCode();
  }

  public String toString()
  {
    return super.toString();
  }

}
