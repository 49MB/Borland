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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.borland.dx.dataset.AccessEvent;
import com.borland.dx.dataset.AccessListener;
import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.DataSetAware;
import com.borland.dx.dataset.DataSetException;
import com.borland.dx.dataset.NavigationEvent;
import com.borland.dx.dataset.NavigationListener;
import com.borland.dx.dataset.StatusEvent;
import com.borland.dx.dataset.StatusListener;
import com.borland.jb.util.Diagnostic;
import com.borland.jbcl.util.BlackBox;

/**
 * Data aware StatusBar component.  If DataSet property is set, the StatusBar displays
 * error and status messages related to operations on the DataSet.
 */
public class StatusBar
     extends BevelPanel
  implements NavigationListener, StatusListener, AccessListener, DataSetAware, BlackBox, java.io.Serializable
{
  public StatusBar() {
    super();
    setLayout(new BorderLayout());
    setMargins(new Insets(0,2,0,2));
    add(label, BorderLayout.CENTER);
  }

  /**
   * Gets the current alignment of this StatusBar.
   * @see #setAlignment
   */
  public int getAlignment() {
    return label.getAlignment();
  }

  /**
   * Sets the alignment for this StatusBar to the specified alignment.
   * @param alignment the alignment value
   * @exception IllegalArgumentException If an improper alignment was given.
   * @see #getAlignment
   */
  public void setAlignment(int alignment) {
    label.setAlignment(alignment);
  }

  /**
   * Gets the text of this StatusBar.
   * @see #setText
   */
  public String getText() {
    return label.getText();
  }

  /**
   * Sets the text for this StatusBar to the specified text.
   * @param text the text that makes up the StatusBar
   * @see #getText
   */
  public void setText(String text) {
    label.setText(text);
  }

  public void addNotify() {
    super.addNotify();
    if (!addNotifyCalled) {
      addNotifyCalled = true;
      if (dataSet != null)
        openDataSet(dataSet);
    }
  }

  /**
   * The dataSet property specifies a com.borland.dx.dataset.DataSet
   * object to display data from in the StatusBar.
   */
  public DataSet getDataSet() { return dataSet; }
  public void setDataSet(DataSet newDataSet) {
    if (dataSet != null) {
      dataSet.removeAccessListener(this);
      dataSet.removeNavigationListener(this);
      dataSet.removeStatusListener(this);
    }
    openDataSet(newDataSet);
    if (dataSet != null) {
      dataSet.addAccessListener(this);
      dataSet.addNavigationListener(this);
      dataSet.addStatusListener(this);
    }
  }

  private void openDataSet(DataSet newDataSet) {
    dataSet = newDataSet;
    if (dataSet == null) {
      return;
    }
    else if (addNotifyCalled && !dataSet.isOpen()) {
      try {
        dataSet.open();
      }
      catch (DataSetException ex) {
        com.borland.jbcl.model.DataSetModel.handleException(dataSet, this, ex);
        return;
      }
    }
    if (dataSet.isOpen()) {
      updateValue();
    }
  }

  /** Implementation of StatusListener.
  */
  public void statusMessage(StatusEvent event) {
    setText(event.getMessage());
  }

  /** Implementation of AccessListener.
  */

  public void accessChange(AccessEvent event) {
    switch(event.getID()) {
      case AccessEvent.OPEN:
        updateValue();
        break;
      case AccessEvent.CLOSE:
        setText("");
        break;
      default:
        Diagnostic.fail();
        break;
    }
  }

  /** Implementation of NavigationListener.
  */
  public void navigated(NavigationEvent event) { updateValue(); }

  protected void updateValue() {
    if (dataSet != null && dataSet.isOpen()) {
      try {
        if (dataSet.getRowCount() > 0)
          setText(java.text.MessageFormat.format(Res._RecordId,     
	          new Object[] {Integer.toString(dataSet.getRow() + 1),
		                Integer.toString(dataSet.getRowCount())}));
        else if (dataSet.isEnableInsert() && !dataSet.getStorageDataSet().isReadOnly())
          setText(Res._EmptyDataSet);     
      }
      catch (Exception ex) {
        Diagnostic.printStackTrace(ex);
      }
    }
  }

  public void addMouseListener(MouseListener l) {
    super.addMouseListener(l);
    label.addMouseListener(l);
  }
  public void removeMouseListener(MouseListener l) {
    super.removeMouseListener(l);
    label.removeMouseListener(l);
  }

  public void addMouseMotionListener(MouseMotionListener l) {
    super.addMouseMotionListener(l);
    label.addMouseMotionListener(l);
  }
  public void removeMouseMotionListener(MouseMotionListener l) {
    super.removeMouseMotionListener(l);
    label.removeMouseMotionListener(l);
  }

  /**
   * Returns the string value of the StatusBar's current value
   */
  public String toString() {
    return getText();
  }

  public Dimension getPreferredSize() {
    Dimension d = super.getPreferredSize();
    if (d.width < 200)
      d.width = 200;
    return d;
  }

  protected DataSet dataSet;
  protected TextControl label = new TextControl();
  private boolean addNotifyCalled = false;
}
