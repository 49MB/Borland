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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

public class NavigatorControl
     extends ButtonBar
  implements AccessListener, ActionListener, NavigationListener, StatusListener, DataSetAware,
             BlackBox, java.io.Serializable
{
  // Button action ids that are used by the navigator & are fired out
  //
  public static final String FIRST   = Res._First;     
  public static final String PRIOR   = Res._Prior;     
  public static final String NEXT    = Res._Next;     
  public static final String LAST    = Res._Last;     
  public static final String INSERT  = Res._Insert;     
  public static final String DELETE  = Res._Delete;     
  public static final String POST    = Res._Post;     
  public static final String CANCEL  = Res._Cancel1;     
  public static final String DITTO   = Res._Ditto;     
  public static final String SAVE    = Res._Save;     
  public static final String REFRESH = Res._Refresh;     

  public NavigatorControl() {
    super.setBevelInner(FLAT);
    super.setBevelOuter(FLAT);
    super.setLayout(new GridLayout(1, 0));
    super.setMargins(new Insets(0,0,0,0));
    super.setButtonType(IMAGE_ONLY);
    super.setLabels(new String[] {
      FIRST,
      PRIOR,
      NEXT,
      LAST,
      INSERT,
      DELETE,
      POST,
      CANCEL,
      DITTO,
      SAVE,
      REFRESH,
    });
    super.setImageNames(new String[] {
      "image/first.gif",   
      "image/prior.gif",   
      "image/next.gif",    
      "image/last.gif",    
      "image/insert.gif",  
      "image/delete.gif",  
      "image/post.gif",    
      "image/cancel.gif",  
      "image/ditto.gif",   
      "image/save.gif",    
      "image/refresh.gif", 
    });
    addActionListener(this);
  }

  public Dimension getPreferredSize() {
    Dimension ps = super.getPreferredSize();
    if (ps.width == 100)
      ps.width = 275;
    if (ps.height == 100)
      ps.height = 26;
    return ps;
  }

  public void addNotify() {
    super.addNotify();
    if (!addNotifyCalled) {
      addNotifyCalled = true;
      if (dataSet != null)
        openDataSet(dataSet);
    }
  }

  public void setLabels(String[] labels) {
    // noop!
  }

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
      rebuild();
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
      boundDataSet = dataSet;
      setButtonEnabled(SAVE, boundDataSet.saveChangesSupported());
      setButtonEnabled(REFRESH, boundDataSet.refreshSupported());
      updateSelection();
    }
  }

  protected void rebuild() {
    if (!addNotifyCalled)
      needsRebuild = true;
    super.rebuild();
    if (dataSet != null) {
      setButtonEnabled(INSERT, dataSet.isEnableInsert());
    }
    else {
      setButtonEnabled(FIRST, false);
      setButtonEnabled(PRIOR, false);
      setButtonEnabled(NEXT, false);
      setButtonEnabled(LAST, false);
      setButtonEnabled(INSERT, false);
      setButtonEnabled(DELETE, false);
      setButtonEnabled(POST, false);
      setButtonEnabled(CANCEL, false);
      setButtonEnabled(DITTO, false);
      setButtonEnabled(SAVE, false);
      setButtonEnabled(REFRESH, false);
    }
  }

  // AccessListener method

  public void accessChange(AccessEvent event) {
    switch (event.getID()) {
      case AccessEvent.OPEN:
        try {
          openDataSet(dataSet);
        }
        catch (Exception ex) {
          event.appendException(ex);
        }
        break;
      case AccessEvent.CLOSE:
        boundDataSet = null;
        break;
      default:
        Diagnostic.fail();
        break;
    }
  }

  // ActionListener method

  public void actionPerformed(ActionEvent e) {
    if (boundDataSet != null) {
      try {
        String command = e.getActionCommand();
        if (FIRST.equals(command))
          boundDataSet.first();
        else if (NEXT.equals(command))
          if (boundDataSet.atLast())
            boundDataSet.insertRow(false);
          else
            boundDataSet.next();
        else if (PRIOR.equals(command))
          boundDataSet.prior();
        else if (LAST.equals(command))
          boundDataSet.last();
        else if (INSERT.equals(command))
          boundDataSet.insertRow(true);
        else if (DELETE.equals(command))
          boundDataSet.deleteRow();
        else if (POST.equals(command))
          boundDataSet.post();
        else if (DITTO.equals(command))
          boundDataSet.dittoRow(false, true);
        else if (CANCEL.equals(command)) {
          if (boundDataSet.isEditing())
            boundDataSet.cancel();
          else
            boundDataSet.cancelLoading();
        }
        else if (SAVE.equals(command) && dataSet != null) {
          try {
            dataSet.saveChanges();
          }
          catch (Exception ex) {
            com.borland.jbcl.model.DataSetModel.handleException(dataSet, this, ex);
          }
        }
        else if (REFRESH.equals(command) && dataSet != null) {
          try {
            dataSet.refresh();
            Diagnostic.println("dataSet.columnCount:  " + dataSet.getColumnCount()); 
          }
          catch(Exception ex) {
            com.borland.jbcl.model.DataSetModel.handleException(dataSet, this, ex);
          }
        }
        else
          return; // how did we get this event?
      }
      catch (DataSetException ex) {
        com.borland.jbcl.model.DataSetModel.handleException(dataSet, this, ex);
      }
    }
  }

  // StatusListener method

  public void statusMessage(StatusEvent event) {
    // Fix for bug 14046.
    //
    if (boundDataSet != null) {
      try {
        switch (event.getCode()) {
          case (StatusEvent.EDIT_STARTED):
          case (StatusEvent.EDIT_CANCELED):
          case (StatusEvent.DATA_CHANGE):
            setButtonEnabled(INSERT, !boundDataSet.isEditingNewRow() && boundDataSet.isEnableInsert());
            setButtonEnabled(DELETE, !boundDataSet.isEmpty() && boundDataSet.isEnableDelete());
            setButtonEnabled(POST, boundDataSet.isEditing());
            setButtonEnabled(CANCEL, boundDataSet.isEditing());
            setButtonEnabled(DITTO, boundDataSet.isEditingNewRow() || !boundDataSet.isEditing());
            break;
        }
      }
      catch(DataSetException ex) {
        com.borland.jbcl.model.DataSetModel.handleException(dataSet, this, ex);
      }
    }
  }

  // NavigationListener method

  public void navigated(NavigationEvent event) { updateSelection(); }

  private void updateSelection() {
    if (boundDataSet != null) {
      try {
        setButtonEnabled(FIRST, !(boundDataSet.atFirst() || boundDataSet.isEmpty()));
        setButtonEnabled(PRIOR, !(boundDataSet.atFirst() || boundDataSet.isEmpty()));
        setButtonEnabled(NEXT, !(boundDataSet.atLast() && boundDataSet.isEditingNewRow()));
        setButtonEnabled(LAST, !(boundDataSet.atLast() || boundDataSet.isEmpty()));
        setButtonEnabled(INSERT, boundDataSet.isEnableInsert());
        setButtonEnabled(DELETE, !boundDataSet.isEmpty() && boundDataSet.isEnableDelete());
      }
      catch (DataSetException ex) {
        com.borland.jbcl.model.DataSetModel.handleException(dataSet, this, ex);
      }
    }
  }

  private DataSet dataSet;
  private DataSet boundDataSet;
  private boolean addNotifyCalled;
}
