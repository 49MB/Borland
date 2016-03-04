/**
 * Copyright (c) 1996-2004 Borland Software Corp. All Rights Reserved.
 *
 * This SOURCE CODE FILE, which has been provided by Borland as part
 * of a Borland product for use ONLY by licensed users of the product,
 * includes CONFIDENTIAL and PROPRIETARY information of Borland.
 *
 * USE OF THIS SOFTWARE IS GOVERNED BY THE TERMS AND CONDITIONS
 * OF THE LICENSE STATEMENT AND LIMITED WARRANTY FURNISHED WITH
 * THE PRODUCT.
 *
 * IN PARTICULAR, YOU WILL INDEMNIFY AND HOLD BORLAND, ITS RELATED
 * COMPANIES AND ITS SUPPLIERS, HARMLESS FROM AND AGAINST ANY
 * CLAIMS OR LIABILITIES ARISING OUT OF THE USE, REPRODUCTION, OR
 * DISTRIBUTION OF YOUR PROGRAMS, INCLUDING ANY CLAIMS OR LIABILITIES
 * ARISING OUT OF OR RESULTING FROM THE USE, MODIFICATION, OR
 * DISTRIBUTION OF PROGRAMS OR FILES CREATED FROM, BASED ON, AND/OR
 * DERIVED FROM THIS SOURCE CODE FILE.
 */
//--------------------------------------------------------------------------------------------------
// Copyright (c) 1996 - 2004 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jbcl.control;

import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

public class StringInput implements WindowListener, java.io.Serializable
{
  public static final int OK     = ButtonDialog.OK;
  public static final int CANCEL = ButtonDialog.CANCEL;

  public StringInput(Frame frame, String title, String value) {
    this.frame = frame;
    this.title = title;
    this.value = value;
    if (frame != null)
      dialog = new StringInputDialog(frame, title, value);
  }

  public StringInput(Frame frame, String title) {
    this(frame, title, "");
  }

  public StringInput(Frame frame) {
    this(frame, "", "");
  }

  public StringInput() {
    this(null, "", "");
  }

  public void setFrame(Frame frame) {
    this.frame = frame;
  }

  public Frame getFrame() {
    return frame;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  public void setValue(String value) {
    this.value = value;
    if (dialog != null)
      dialog.setValue(value);
  }

  public String getValue() {
    if (dialog != null)
      value = dialog.getValue();
    return value;
  }

  public void setResult(int i) {
    result = i;
    if (dialog != null)
      dialog.setResult(result);
  }

  public int getResult() {
    if (dialog != null)
      result = dialog.getResult();
    return result;
  }

  public void show() {
    setVisible(true);
  }

  public void setVisible(boolean visible) {
    if (visible) {
      if (dialog == null) {
        if (frame == null)
          throw new IllegalStateException(Res._NoFrame);     
        dialog = new StringInputDialog(frame, title, value);
        dialog.setResult(result);
        dialog.addWindowListener(this);
        for (int i = 0; i < listeners.size(); i++) {
          dialog.addActionListener((ActionListener) listeners.elementAt(i));
        }
      }
      dialog.setVisible(true);
    }
    else {
      if (dialog != null)
        dialog.setVisible(false);
    }
  }

  public boolean isVisible() {
    return dialog != null ? dialog.isVisible() : false;
  }

  public void addActionListener(ActionListener l) {
    if (dialog != null)
      dialog.addActionListener(l);
    else
      listeners.addElement(l);
  }

  public void removeActionListener(ActionListener l) {
    if (dialog != null)
      dialog.removeActionListener(l);
    else
      listeners.removeElement(l);
  }

  public void windowOpened(WindowEvent e) {}
  public void windowClosing(WindowEvent e) {
    value = dialog.getValue();
    result = dialog.getResult();
  }
  public void windowClosed(WindowEvent e) {
    if (dialog != null) {
      value = dialog.getValue();
      result = dialog.getResult();
    }
    dialog = null;
    if (frame.isShowing()) {
      if (frame.getFocusOwner() != null)
        frame.getFocusOwner().requestFocus();
      else
        frame.requestFocus();
    }
  }
  public void windowIconified(WindowEvent e) {}
  public void windowDeiconified(WindowEvent e) {}
  public void windowActivated(WindowEvent e) {}
  public void windowDeactivated(WindowEvent e) {}

  protected StringInputDialog dialog;
  protected Frame frame;
  protected String title;
  protected String value;
  protected transient Vector listeners = new Vector();
  protected int result;
}
