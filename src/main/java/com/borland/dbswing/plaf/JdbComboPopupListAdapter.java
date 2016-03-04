package com.borland.dbswing.plaf;

import javax.swing.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: Softwareschmiede Höffl GmbH</p>
 *
 * @author Stefan Schmaltz
 * @version 1.0
 */
public class JdbComboPopupListAdapter extends JList
{
  JdbComboPopupGetter popupGetter;
  
  public JdbComboPopupListAdapter(JdbComboPopupGetter popupGetter)
  {
    super();
    this.popupGetter = popupGetter;
  }
  
  /**
   * Returns the first selected index; returns -1 if there is no selected item.
   *
   * @return the value of <code>getMinSelectionIndex</code>
   */
  @Override
  public int getSelectedIndex()
  {
    if (popupGetter != null && popupGetter.getTable() != null)
      return popupGetter.getTable().getSelectedRow();
    return -1;
  }
  
  /**
   * Selects a single cell.
   *
   * @param index the index of the one cell to select
   */
  @Override
  public void setSelectedIndex(int index)
  {
    if (popupGetter != null && popupGetter.getTable() != null)
      popupGetter.getTable().setRowSelectionInterval(index, index);
  }
  
}
