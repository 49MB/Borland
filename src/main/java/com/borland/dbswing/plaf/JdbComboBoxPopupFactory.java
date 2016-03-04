package com.borland.dbswing.plaf;

import javax.swing.JComboBox;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: Softwareschmiede Hoeffl GmbH</p>
 *
 * @author unbekannt
 * @version 1.0
 */
public interface JdbComboBoxPopupFactory {
  public ComboPopup createPopupWindow(BasicComboBoxUI comboBoxUI, JComboBox comboBox);
}
