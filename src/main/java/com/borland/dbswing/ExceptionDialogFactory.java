package com.borland.dbswing;

import java.awt.Component;
import java.awt.Frame;

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
public interface ExceptionDialogFactory {
  public ExceptionDialog createExceptionDialog(Frame frame, String title, Throwable ex, boolean modal, Component c);
}
