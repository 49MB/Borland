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

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Filer implements WindowListener, Serializable
{
  public static final int LOAD = FileDialog.LOAD;
  public static final int SAVE = FileDialog.SAVE;

  public Filer(Frame frame, String title, int mode) {
    this.frame = frame;
    this.title = title;
    this.mode = mode;
    if (frame != null)
      dialog = new FileDialog(frame, title, mode);
  }

  public Filer(Frame frame, String title) {
    this(frame, title, FileDialog.LOAD);
  }

  public Filer(Frame frame) {
    this(frame, "", FileDialog.LOAD);
  }

  public Filer() {
    this(null, "", FileDialog.LOAD);
  }

  public void setFrame(Frame frame) {
    this.frame = frame;
  }

  public Frame getFrame() {
    return frame;
  }

  public void setTitle(String title) {
    this.title = title;
    if (dialog != null)
      dialog.setTitle(title);
  }

  public String getTitle() {
    if (dialog != null)
      title = dialog.getTitle();
    return title;
  }

  public void setMode(int m) {
    this.mode = m;
    if (dialog != null)
      dialog.setMode(m);
  }

  public int getMode() {
    if (dialog != null)
      mode = dialog.getMode();
    return mode;
  }

  /**
   * Set the directory of the Dialog to the specified directory.
   * @param dir the specific directory
   */
  public void setDirectory(String dir) {
    this.dir = dir;
    if (dialog != null)
      dialog.setDirectory(dir);
  }

  /**
   * Gets the directory of the Dialog.
   */
  public String getDirectory() {
    if (dialog != null)
      dir = dialog.getDirectory();
    return dir;
  }

  /**
   * Sets the file for this dialog to the specified file. This will
   * become the default file if set before the dialog is shown.
   * @param file the file being set
   */
  public void setFile(String file) {
    this.file = file;
    if (dialog != null)
      dialog.setFile(file);
  }

  /**
   * Gets the file of the Dialog.
   */
  public String getFile() {
    if (dialog != null)
      file = dialog.getFile();
    return file;
  }

  /**
   * Sets the filter for this dialog to the specified filter.
   * @param filter the specified filter
   */
  public void setFilenameFilter(FilenameFilter filter) {
    this.filter = filter;
    if (dialog != null)
      dialog.setFilenameFilter(filter);
  }

  /**
   * Gets the filter.
   */
  public FilenameFilter getFilenameFilter() {
    if (dialog != null)
      filter = dialog.getFilenameFilter();
    return filter;
  }

  /**
   * Shows the dialog.
   */
  public void show() {
    setVisible(true);
  }

  public void setVisible(boolean visible) {
    if (visible) {
      if (dialog == null) {
        if (frame == null)
          throw new IllegalStateException(Res._NoFrame);     
        dialog = new FileDialog(frame, title, mode);
        dialog.setFile(file);
        dialog.setDirectory(dir);
        dialog.setFilenameFilter(filter);
        dialog.addWindowListener(this);
      }
      dialog.show();
    }
    else if (dialog != null) {
      dialog.setVisible(false);
    }
  }

  public boolean isVisible() {
    return dialog != null ? dialog.isVisible() : false;
  }

  public void windowOpened(WindowEvent e) {}

  public void windowClosing(WindowEvent e) {
    getFile();
    getDirectory();
    getFilenameFilter();
  }

  public void windowClosed(WindowEvent e) {
    if (dialog != null) {
      getFile();
      getDirectory();
      getFilenameFilter();
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

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    s.writeObject(filter instanceof Serializable ? filter : null);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    Object data = s.readObject();
    if (data instanceof FilenameFilter)
      filter = (FilenameFilter)data;
  }

  protected FileDialog dialog;
  protected Frame frame;
  protected String title;
  protected int mode;
  protected String file;
  protected String dir;
  protected transient FilenameFilter filter;
}
