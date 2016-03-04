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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.util.TooManyListenersException;

import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.ColumnVariant;
import com.borland.dx.dataset.DataRow;
import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.DataSetException;
import com.borland.dx.dataset.DataSetView;
import com.borland.dx.dataset.Locate;
import com.borland.dx.dataset.PickListDescriptor;
import com.borland.dx.dataset.ReadRow;
import com.borland.dx.dataset.RowFilterListener;
import com.borland.dx.dataset.Variant;
import com.borland.jbcl.model.ItemEditSite;
import com.borland.jbcl.model.ItemEditor;
import com.borland.jbcl.util.TriState;

/**
 * PopupPickListItemEditor is a custom JBCL Model/View ItemEditor specifically
 * designed for use with DataSet Column components. When assigned as the
 * ItemEditor for a Column with a PickListDescriptor attached to a data-aware
 * JBCL control (e.g. a FieldControl), PopupPickListItemEditor presents the user
 * with a multi-column table (GridControl) of values stored in a DataSet from
 * which to make a selection. When a selection is made, PopupPickListItemEditor
 * copies one or more column values from a row of the "picklist" DataSet into
 * columns of the DataSet to which the PopupPickListItemEditor is attached.
 * <p>
 * For example, a PickListDescriptor has been specified for the Customer_ID
 * column of the Orders dataset. It specifies that the the "picklist" dataset is
 * the Customer dataset, the display columns are the Last_Name and
 * Social_Security_Number field, and that the Customer_ID and Phone_Number
 * columns from the Customer dataset should be copied into the Customer_ID and
 * Contact_Phone columns of the Orders dataset. When the user edits the control
 * containing the Customer_ID column, a table of last names and social security
 * numbers from the Customer dataset is presented in a GridControl, from which
 * the user can make a selection. If the user selects a row, the Customer_ID and
 * Phone_Number values for that customer will be copied into the Customer_ID and
 * Contact_Phone columns, respectively, of the Orders dataset.
 * <p>
 * Users can select the currently highlighted row from the picklist dialog by
 * either double-clicking the row, or pressing [Enter]. To cancel the edit
 * without making a selection, the user should either click the close icon of
 * the dialog box or press [Esc].
 * <p>
 * A PickListDescriptor (created for you automatically via the UI Designer) is
 * used to specify the columns of a DataSet to display, as well as the names of
 * source and target columns for copying. Note that the columns displayed can be
 * different in name and number from the columns transferred between datasets.
 * PopupPickListItemEditor does not make use of the enforceIntegrity
 * PickListDescriptor attribute.
 * 
 */
public class PopupPickListItemEditor implements ItemEditor,
    java.io.Serializable {
  public PopupPickListItemEditor() {
    super();
  }
  
  /**
   * Specifies an optional string to display in the title area of the window. By
   * default, no title is displayed. To clear the title after setting one, pass
   * setTitle() an empty string.
   */
  public final void setTitle(String windowTitle) {
    this.windowTitle = windowTitle;
  }
  
  public final String getTitle() {
    return windowTitle;
  }
  
  /**
   * Specifies whether or not an incremental search control should be provided
   * at the top of the window.
   */
  public final void setAllowSearch(boolean allowSearch) {
    if (lookupDialog != null) {
      lookupDialog.setAllowSearch(allowSearch);
    }
    this.allowSearch = allowSearch;
  }
  
  public final boolean isAllowSearch() {
    return allowSearch;
  }
  
  /**
   * Specifies whether or not the picklist window should always be centered when
   * displayed, or the most recent size and position should be maintained. Set
   * false by default.
   */
  public final void setAlwaysCenter(boolean alwaysCenter) {
    if (lookupDialog != null) {
      lookupDialog.setAlwaysCenter(alwaysCenter);
    }
    this.alwaysCenter = alwaysCenter;
  }
  
  public final boolean isAlwaysCenter() {
    return alwaysCenter;
  }
  
  /**
   * Adds a RowFilterListener to the picklist dataset, which can be used to
   * dynamically select which PickListDataSet rows should be displayed by
   * PopupPickListItemEditor.
   * 
   * @deprecated A RowFilterListener should be attached to the picklist dataset
   *             itself, rather than to this item editor.
   */
  @Deprecated
  public final void addRowFilterListener(RowFilterListener listener)
      throws TooManyListenersException {
    if (listener == null) {
      throw new IllegalArgumentException();
    }
    
    if (this.listener != null) {
      throw new TooManyListenersException();
    }
    
    this.listener = listener;
    if (pickListDataSet != null) {
      pickListDataSet.addRowFilterListener(listener);
    }
  }
  
  /**
   * Removes the RowFilterListener from the picklist dataset.
   * 
   * @deprecated A RowFilterListener should be attached to the picklist dataset
   *             itself, rather than to this item editor.
   */
  @Deprecated
  public final void removeRowFilterListener(RowFilterListener listener) {
    this.listener = null;
    if (pickListDataSet != null) {
      pickListDataSet.removeRowFilterListener(listener);
    }
  }
  
  /**
   * Specifies whether or not an OK/Cancel button bar should appear at the
   * bottom of the popup window. Set false by default.
   */
  public final void setDisplayOKCancel(boolean displayOKCancel) {
    if (lookupDialog != null) {
      lookupDialog.setDisplayOKCancel(displayOKCancel);
    }
    this.displayOKCancel = displayOKCancel;
  }
  
  public final boolean isDisplayOKCancel() {
    return displayOKCancel;
  }
  
  // loadPickList() has the side effect of extracting the
  // pickListDescriptor info into class variables. It also creates a
  // modal lookup dialog containing a data-aware gridControl
  // containing the appropriately structured pickListDataSet
  protected void loadPickList(PickListDescriptor pickList, Component component) {
    try {
      pickListDataSet = new DataSetView();
      if (listener != null) {
        // In the case where a rowFilter listener is already attached to the
        // pickListDataSet and a rowFilter listener is also attached to this
        // item editor, the listener set on the item editor takes precedence.
        if (pickListDataSet.getRowFilterListeners() != null) {
          pickListDataSet.removeRowFilterListener(pickListDataSet
              .getRowFilterListeners());
        }
        pickListDataSet.addRowFilterListener(listener);
      }
      // if pickList.getPickListDataSet() returns null, we'll catch
      // the NPE below and set pickListDataSet to be null. startEdit()
      // checks if this is the case and if so, immediately cancels
      // the edit.
      pickListDataSet.setStorageDataSet(pickList.getPickListDataSet()
          .getStorageDataSet());
      sourceColumns = pickList.getPickListColumns();
      targetColumns = pickList.getDestinationColumns();
      displayColumns = pickList.getPickListDisplayColumns();
      // if any of the columns are null or don't have at least one
      // column specified, then setting pickListDataSet null and
      // returning will cause startEdit() to immediately cancel
      // the edit.
      if ((sourceColumns == null) || (sourceColumns.length == 0)
          || (targetColumns == null) || (targetColumns.length == 0)
          || (displayColumns == null) || (displayColumns.length == 0)) {
        pickListDataSet = null;
        component.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        return;
      }
      pickListDataSet.open();
      locateRow = new DataRow(pickListDataSet, sourceColumns);
      
      // display choices using a GridControl in a modal dialog box
      pickListColumns = pickList.getPickListDataSet().getStorageDataSet()
          .getColumns();
      columnOriginalState = new int[pickListColumns.length];
      columnDialogState = new int[pickListColumns.length];
      // save the current visible state of columns so we can restore the values
      // later
      for (int index = 0; index < pickListColumns.length; index++) {
        columnOriginalState[index] = pickListColumns[index].getVisible();
        pickListColumns[index].setVisible(TriState.NO);
      }
      // set only the PickListDisplay columns as visible
      for (String displayColumn : displayColumns) {
        pickList.getPickListDataSet().getStorageDataSet()
            .getColumn(displayColumn).setVisible(TriState.YES);
      }
      // cache the dialog visible state of columns for later use
      for (int index = 0; index < pickListColumns.length; index++) {
        columnDialogState[index] = pickListColumns[index].getVisible();
      }
      
      gridControl.setDataSet(pickListDataSet);
      gridControl.setReadOnly(true);
      // set some cosmetic properties of the gridcontrol display
      gridControl.setMultiSelect(false);
      gridControl.setRowHeaderVisible(false);
      // [Enter] selects a row, so we want to disable the
      // navigation effects of [Enter]
      gridControl.setNavigateOnEnter(false);
      
      Frame frame = null;
      Component parent = component;
      
      while ((parent = parent.getParent()) != null) {
        if (parent instanceof Frame) {
          frame = (Frame) parent;
          break;
        }
      }
      
      // frame should never be null, but just in case...
      if (frame == null) {
        frame = new Frame();
      }
      
      lookupDialog = new LookupDialog(frame, gridControl, allowSearch,
          alwaysCenter, displayOKCancel);
      lookupDialog.setTitle(windowTitle);
      
    } catch (Exception ex) {
      pickListDataSet = null;
      com.borland.jbcl.model.DataSetModel.handleException(component, ex, true);
    }
  }
  
  public Object getValue() {
    // try {
    if (pickListDataSet != null) {
      if (lookupDialog != null && !lookupDialog.useValue()) {
        return value;
      }
      // Copy the PickList columns to the corresponding destination columns.
      // ReadRow.copyTo(sourceColumns, pickListDataSet, targetColumns,
      // targetDataSet);
    }
    
    // // Return the current column's value, or whatever value was copied into
    // the
    // // current column of the targetDataSet as a variant of the same type
    // // as the current column. Note that if a lookup is defined on this
    // column,
    // // then the value passed into startEdit() method will contain the lookup
    // // display value rather than the actual column data value. Thus here we
    // // make sure we return the column's actual value, rather than the display
    // // value.
    // targetDataSet.getVariant(currentColumnName, value);
    //
    // } catch(DataSetException ex) {
    // value.setUnassignedNull();
    // com.borland.jbcl.model.DataSetModel.handleException(gridControl,ex,true);
    // return value;
    // }
    return pickListDataSet;
  }
  
  public Component getComponent() {
    return null;
  }
  
  public void startEdit(Object data, Rectangle bounds, ItemEditSite editSite) {
    Column currentColumn = null;
    ColumnVariant currentColumnVariant = null;
    
    if (data instanceof ColumnVariant) {
      currentColumnVariant = (ColumnVariant) data;
      value.setVariant(currentColumnVariant);
      currentColumn = currentColumnVariant.getColumn();
      currentColumnName = currentColumn.getColumnName();
      targetDataSet = currentColumnVariant.getDataSet();
      if (pickListDataSet == null
          || !pickListDataSet.isCompatibleList(locateRow)) {
        loadPickList(currentColumn.getPickList(), editSite.getSiteComponent());
      }
      // if there was a problem with information in the PickListDescriptor,
      // then quietly cancel the edit.
      if (pickListDataSet == null) {
        editSite.safeEndEdit(false);
        return;
      }
      if (listener != null) {
        try {
          pickListDataSet.refilter();
        } catch (DataSetException ex) {
          com.borland.jbcl.model.DataSetModel.handleException(gridControl, ex,
              true);
        }
      }
    }
    
    if (pickListDataSet != null && locateRow != null) {
      try {
        ;
        currentColumnVariant.getDataSet();
        // Doing a locate() on the pickListDataSet should move the
        // current row to the target row if it exists.
        ReadRow.copyTo(targetColumns, currentColumnVariant.getDataSet(),
            sourceColumns, locateRow);
        pickListDataSet.locate(locateRow, Locate.FIRST);
      } catch (DataSetException ex) {
        com.borland.jbcl.model.DataSetModel.handleException(gridControl, ex,
            true);
      }
    }
    /*
     * if (editSite != null) {
     * gridControl.setBackground(editSite.getBackground());
     * gridControl.setForeground(editSite.getForeground());
     * gridControl.setFont(editSite.getFont()); }
     */
    // make only the picklist display columns visible
    try {
      for (int index = 0; index < pickListColumns.length; index++) {
        pickListColumns[index].setVisible(columnDialogState[index]);
      }
      
      lookupDialog.show();
      
      // restore the original visible state of the columns
      for (int index = 0; index < pickListColumns.length; index++) {
        pickListColumns[index].setVisible(columnOriginalState[index]);
      }
    } catch (DataSetException ex) {
      com.borland.jbcl.model.DataSetModel
          .handleException(gridControl, ex, true);
    }
    
    // calling safeEndEdit() bypasses the regular ItemEditor
    // UI semantics, immediately ending the edit interaction
    // The passed boolean parameter indicates whether or
    // not to put the value returned by getValue().
    editSite.safeEndEdit(lookupDialog.useValue());
    
  }
  
  public void changeBounds(Rectangle bounds) {
  }
  
  public boolean canPost() {
    return true;
  }
  
  public void endEdit(boolean post) {
  }
  
  // no implemenation necessary because we explicitly end the edit using
  // safeEndEdit()
  public void addKeyListener(KeyListener l) {
  }
  
  // no implemenation necessary because we explicitly end the edit using
  // safeEndEdit()
  public void removeKeyListener(KeyListener l) {
  }
  
  String currentColumnName;
  String[] sourceColumns;
  String[] targetColumns;
  Column[] pickListColumns;
  String[] displayColumns;
  DataSetView pickListDataSet;
  DataSet targetDataSet;
  DataRow locateRow;
  int[] columnOriginalState;
  int[] columnDialogState;
  Variant value = new Variant();
  GridControl gridControl = new GridControl();
  LookupDialog lookupDialog;
  String windowTitle = "";
  boolean allowSearch = true;
  boolean alwaysCenter = false;
  boolean displayOKCancel = false;
  transient RowFilterListener listener; // ignore for serialization
}

/*
 * A modal dialog used to display column display values from a
 * PickListDescriptor. Because the dialog is modal (and because in JDK1.1.x
 * Dialog.show() does not return immediately), a callback mechanism into the
 * caller is not necessary. After calling LookupDialog.show(), the caller calls
 * the boolean method LookupDialog.useValue() to determine whether or not the
 * user selected a row. Double-clicking a row or pressing [Enter] selects a
 * value, closing the dialog or pressing [Esc] does not select a value. Note
 * that the since the PickList DataSetView maintains the currently selected row,
 * no dataset data needs to be passed from the dialog back to the caller.
 */
class LookupDialog extends Dialog implements MouseListener, KeyListener,
    ActionListener, java.io.Serializable {
  
  GridLayout gridLayout = new GridLayout();
  BorderLayout borderLayout = new BorderLayout();
  ISearchControl iSearchControl;
  FlowLayout flowLayout = new FlowLayout();
  Panel buttonGridPanel = new Panel();
  Panel buttonFlowPanel = new Panel();
  Button okButton = new Button();
  Button cancelButton = new Button();
  GridControl lookupGrid;
  boolean useValue = false;
  boolean allowSearch = false;
  boolean alwaysCenter = false;
  boolean displayOKCancel = false;
  Dimension screenSize = null;
  Frame frame;
  
  public LookupDialog(Frame frame, GridControl lookupGrid) {
    this(frame, lookupGrid, true);
  }
  
  public LookupDialog(Frame frame, GridControl lookupGrid, boolean allowSearch) {
    this(frame, lookupGrid, true, false);
  }
  
  public LookupDialog(Frame frame, GridControl lookupGrid, boolean allowSearch,
      boolean alwaysCenter) {
    this(frame, lookupGrid, true, false, false);
  }
  
  public LookupDialog(Frame frame, GridControl lookupGrid, boolean allowSearch,
      boolean alwaysCenter, boolean displayOKCancel) {
    super(frame, "", true);
    try {
      this.frame = frame;
      this.lookupGrid = lookupGrid;
      this.allowSearch = allowSearch;
      this.alwaysCenter = alwaysCenter;
      this.displayOKCancel = displayOKCancel;
      iSearchControl = new ISearchControl(lookupGrid);
      jbInit();
      enableEvents(AWTEvent.WINDOW_EVENT_MASK);
      pack();
      screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      centerDialog();
    } catch (Exception e) {
      com.borland.jb.util.Diagnostic.printStackTrace(e);
    }
  }
  
  public void jbInit() throws Exception {
    setLayout(borderLayout);
    
    okButton.setLabel(Res._OK);
    okButton.addActionListener(this);
    
    cancelButton.setLabel(Res._Cancel);
    cancelButton.addActionListener(this);
    
    gridLayout.setHgap(5);
    
    buttonGridPanel.setLayout(gridLayout);
    buttonGridPanel.add(okButton, null);
    buttonGridPanel.add(cancelButton, null);
    
    buttonFlowPanel.setLayout(flowLayout);
    buttonFlowPanel.add(buttonGridPanel);
    
    iSearchControl.setDataSet(lookupGrid.getDataSet());
    iSearchControl.addKeyListener(this);
    
    lookupGrid.addMouseListener(this);
    lookupGrid.addKeyListener(this);
    
    add(iSearchControl, BorderLayout.NORTH);
    // iSearchControl.setVisible(allowSearch);
    
    add(buttonFlowPanel, BorderLayout.SOUTH);
    // buttonFlowPanel.setVisible(displayOKCancel);
    
    add(lookupGrid, BorderLayout.CENTER);
    
  }
  
  public void actionPerformed(ActionEvent e) {
    Object pushedButton = e.getSource();
    
    if (pushedButton == okButton) {
      useValue = true;
    } else if (pushedButton == cancelButton) {
      useValue = false;
    }
    dispose();
  }
  
  @Override
  public void show() {
    if (alwaysCenter) {
      centerDialog();
    }
    iSearchControl.setVisible(allowSearch);
    buttonFlowPanel.setVisible(displayOKCancel);
    super.show();
  }
  
  private void centerDialog() {
    // Initially center the window, and resize it to half its width or height
    // if the preferred size is larger than the screen size
    Dimension frameSize = this.getPreferredSize();
    if (frameSize.height >= screenSize.height) {
      frameSize.height = screenSize.height / 2;
    }
    if (frameSize.width >= screenSize.width) {
      frameSize.width = screenSize.width / 2;
    }
    setSize(frameSize.width, frameSize.height);
    setLocation((screenSize.width - frameSize.width) / 2,
        (screenSize.height - frameSize.height) / 2);
  }
  
  @Override
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      useValue = false;
      setVisible(false);
      frame.toFront();
      dispose();
    }
    super.processWindowEvent(e);
  }
  
  public void mouseClicked(MouseEvent e) {
    if (e.getClickCount() >= 2) {
      useValue = true;
      setVisible(false);
      frame.toFront();
      dispose();
    }
  }
  
  public void mousePressed(MouseEvent e) {
  }
  
  public void mouseReleased(MouseEvent e) {
  }
  
  public void mouseEntered(MouseEvent e) {
  }
  
  public void mouseExited(MouseEvent e) {
  }
  
  public void keyPressed(KeyEvent e) {
    if ((e.getKeyCode() == KeyEvent.VK_ENTER)
        || (e.getKeyCode() == KeyEvent.VK_ESCAPE)) {
      useValue = false;
      if (e.getKeyCode() == KeyEvent.VK_ENTER) {
        useValue = true;
      }
      setVisible(false);
      frame.toFront();
      dispose();
    }
  }
  
  public void keyTyped(KeyEvent e) {
  }
  
  public void keyReleased(KeyEvent e) {
  }
  
  public boolean useValue() {
    return useValue;
  }
  
  public void setAllowSearch(boolean allowSearch) {
    this.allowSearch = allowSearch;
    iSearchControl.setVisible(allowSearch);
  }
  
  public boolean isAllowSearch() {
    return allowSearch;
  }
  
  public void setAlwaysCenter(boolean alwaysCenter) {
    this.alwaysCenter = alwaysCenter;
  }
  
  public boolean isAlwaysCenter() {
    return alwaysCenter;
  }
  
  public void setDisplayOKCancel(boolean displayOKCancel) {
    this.displayOKCancel = displayOKCancel;
    buttonFlowPanel.setVisible(displayOKCancel);
  }
  
  public boolean isDisplayOKCancel() {
    return displayOKCancel;
  }
  
}

/**
 * ISearchControl is a special LocatorControl modified for use specifically with
 * the LookupDialog. It borrows most of its code and behavior from
 * LocatorControl. However, its functionality differs in that: - a search is
 * performed on every key stroke, even on non-string type columns. - [Up],
 * [Down], [Left], [Right], [Home], and [End] keys navigate the grid without
 * doing a locate
 */

class ISearchControl extends TextFieldControl implements java.io.Serializable {
  GridControl lookupGrid;
  
  public ISearchControl(GridControl lookupGrid) {
    super();
    this.lookupGrid = lookupGrid;
    enableEvents(AWTEvent.KEY_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK);
    locateOnly = true;
  }
  
  // ignore these requests!
  @Override
  protected void postText() {
  }
  
  @Override
  protected void updateText() {
    Component c = getParent();
    while (c != null && !(c instanceof Window))
      c = c.getParent();
    if (c instanceof Window && ((Window) c).getFocusOwner() != this)
      super.updateText();
  }
  
  @Override
  public boolean canSet(boolean startingEdit) {
    return super.canSet(false); // never start an edit session!
  }
  
  @Override
  protected void processKeyEvent(KeyEvent e) {
    // Diagnostic.println("LocatorControl.processKeyEvent:  "+" "+e.getKeyChar()+e.getKeyCode()+" "+e.ENTER);
    
    // Must eat up/down on key pressed otherwise action (go left in the text)
    // will be taken on KeyEvent.VK_UP
    //
    if (e.getID() == KeyEvent.KEY_PRESSED
        && (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN))
      e.consume();
    if (e.getID() == KeyEvent.KEY_RELEASED)
      locatorKeyReleased(e);
    super.processKeyEvent(e);
  }
  
  /**
   * Invoked when a key has been pressed/released when this locator has focus.
   */
  private void locatorKeyReleased(KeyEvent e) {
    if (getDataSet() != null) {
      int locateOptions;
      
      try {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
          getDataSet().next();
        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
          getDataSet().prior();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
          int currentRow = lookupGrid.getSubfocus().row;
          int currentCol = lookupGrid.getSubfocus().column;
          if (currentCol > 0) {
            lookupGrid.setSubfocus(currentRow, currentCol - 1);
          } else {
            if (currentRow > 0) {
              lookupGrid.setSubfocus(currentRow - 1,
                  lookupGrid.getColumnCount() - 1);
            }
          }
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
          int currentRow = lookupGrid.getSubfocus().row;
          int currentCol = lookupGrid.getSubfocus().column;
          if (currentCol < lookupGrid.getColumnCount() - 1) {
            lookupGrid.setSubfocus(currentRow, currentCol + 1);
          } else {
            if (currentRow < lookupGrid.getRowCount() - 1) {
              lookupGrid.setSubfocus(currentRow + 1, 0);
            }
          }
        } else if (e.getKeyCode() == KeyEvent.VK_HOME) {
          getDataSet().first();
        } else if (e.getKeyCode() == KeyEvent.VK_END) {
          getDataSet().last();
        } else {
          locateOptions = Locate.FIRST;
          
          String text = getText();
          
          if (text.toLowerCase().equals(text)) {
            locateOptions |= Locate.CASE_INSENSITIVE;
          }
          
          getDataSet().interactiveLocate(text, getColumnName(), locateOptions,
              true);
          
        }
      } catch (DataSetException ex) {
        com.borland.jb.util.Diagnostic.printStackTrace(ex);
      }
    }
  }
}
