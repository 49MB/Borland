package com.borland.dx.dataset;

public class DataSetLoggerBeanConfiguration
{
  public final static int NON_VALID_VALUE = -1;
  // Attribute eines DataSets, die beim Logging miteinbezogen werden sollen. Die
  // Vorbelegungen entsprechen einer DefaultConfiguration.
  private boolean open = true;
  private boolean editing = true;

  private boolean allowInsert = false;
  private boolean allowUpdate = false;
  private boolean allowDelete = false;
  private boolean editable = false;

  private boolean newRow = false;
  private boolean rowDirty = false;

  private long currentRow = NON_VALID_VALUE;
  private long internalRow = NON_VALID_VALUE;
  private long postedRow = NON_VALID_VALUE;

  private int needsSynch = NON_VALID_VALUE;

  public DataSetLoggerBeanConfiguration()
  {

  }

  public boolean isOpen()
  {
    return open;
  }

  public void setOpen(boolean isOpen)
  {
    this.open = isOpen;
  }

  public boolean isEditing()
  {
    return editing;
  }

  public void setEditing(boolean isEditing)
  {
    this.editing = isEditing;
  }

  boolean isAllowInsert()
  {
    return allowInsert;
  }

  void setAllowInsert(boolean allowInsert)
  {
    this.allowInsert = allowInsert;
  }

  boolean isAllowUpdate()
  {
    return allowUpdate;
  }

  void setAllowUpdate(boolean allowUpdate)
  {
    this.allowUpdate = allowUpdate;
  }

  boolean isAllowDelete()
  {
    return allowDelete;
  }

  void setAllowDelete(boolean allowDelete)
  {
    this.allowDelete = allowDelete;
  }

  boolean isEditable()
  {
    return editable;
  }

  void setEditable(boolean editable)
  {
    this.editable = editable;
  }

  boolean isNewRow()
  {
    return newRow;
  }

  void setNewRow(boolean newRow)
  {
    this.newRow = newRow;
  }

  boolean isRowDirty()
  {
    return rowDirty;
  }

  void setRowDirty(boolean rowDirty)
  {
    this.rowDirty = rowDirty;
  }

  long getCurrentRow()
  {
    return currentRow;
  }

  void setCurrentRow(long currentRow)
  {
    this.currentRow = currentRow;
  }

  long getInternalRow()
  {
    return internalRow;
  }

  void setInternalRow(long internalRow)
  {
    this.internalRow = internalRow;
  }

  long getPostedRow()
  {
    return postedRow;
  }

  void setPostedRow(long postedRow)
  {
    this.postedRow = postedRow;
  }

  int getNeedsSynch()
  {
    return needsSynch;
  }

  void setNeedsSynch(int needsSynch)
  {
    this.needsSynch = needsSynch;
  }

}
