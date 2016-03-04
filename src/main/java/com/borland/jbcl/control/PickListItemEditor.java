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
import java.awt.Choice;
import java.awt.Component;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import com.borland.jbcl.util.BlackBox;

/**
 * PickListItemEditor is a custom JBCL Model/View ItemEditor specifically
 * designed for use with DataSet Column components. It is the default ItemEditor
 * for a column on which a PickListDescriptor property has been set (via the UI
 * designer). Upon editing a control attached to a column with a
 * PickListDescriptor, the user is presented with a drop-down list of values
 * from a single display column of a "picklist" dataset from which to make a
 * selection. When a user makes a selection, the PickListItemEditor copies one
 * or more column values from a row of the "picklist" dataset into columns of
 * the dataset being edited.
 * <p>
 * For example, a PickListDescriptor has been specified for the Customer_ID
 * column of the Orders dataset. It specifies that the the "picklist" dataset is
 * the Customer dataset, the display column is the Last_Name field, and that the
 * Customer_ID and Phone_Number columns from the Customer dataset should be
 * copied into the Customer_ID and Contact_Phone columns of the Orders dataset.
 * When the user edits the control containing the Customer_ID column, a
 * drop-down list of last names from the Customer dataset is presented in a
 * Choice control, from which the user can make a selection. If the user selects
 * a last name, the Customer_ID and Phone_Number values for that customer will
 * be copied into the Customer_ID and Contact_Phone columns, respectively, of
 * the Orders dataset.
 * <p>
 * PickListItemEditor respects any row filtering that may be performed by a
 * RowFilterListener attached to the picklist dataset.
 * <p>
 * PickListItemEditor is only able to display values from a single column of the
 * "picklist" dataset. To display values from multiple columns of a dataset,
 * assign the PopupPickListItemEditor as the ItemEditor for the column.
 * PickListItemEditor does not make use of the enforceIntegrity
 * PickListDescriptor attribute.
 */
public class PickListItemEditor extends Panel implements ItemEditor,
    ItemListener, KeyListener, BlackBox, java.io.Serializable {
  public PickListItemEditor() {
    super();
    this.setVisible(false);
    this.setLayout(new BorderLayout());
    cache = true;
    value = new Variant();
    choice.addItemListener(this);
    choice.addKeyListener(this);
  }
  
  /**
   * The CachePickList property determines whether or not values from the
   * display ("picklist") DataSet are cached. If the column of display choices
   * from which to choose is subject to frequent change, setting this property
   * false will ensure that the most recent list of choices will be displayed
   * when the PickListItemEditor is invoked. If a RowFilterListener is attached
   * to the picklist DataSet, this property will automatically be set false.
   * Otherwise, for performance, this property is true by default.
   */
  public final void setCachePickList(boolean cache) {
    this.cache = cache;
  }
  
  public final boolean isCachePickList() {
    return cache;
  }
  
  /**
   * Sets a RowFilterListener on the picklist dataset. By specifying a
   * RowFilterListener and setting Cache false, you can dynamically change the
   * list of values displayed by PickListItemEditor through the row filter.
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
  
  // loadPickList() is called by startEdit() when a user begins
  // editing the control. It has the side effect of extracting the
  // pickListDescriptor info into class variables.
  protected void loadPickList(PickListDescriptor pickList) {
    if (pickListDataSet == null || !cache) {
      setVisible(true);
      try {
        if (pickListDataSet == null) {
          pickList.getPickListDataSet().open();
          
          // if pickList.getPickListDataSet() returns null, we'll catch
          // the NPE below and set pickListDataSet to be null. startEdit()
          // checks if this is the case and if so, immediately cancels
          // the edit.
          pickListDataSet = pickList.getPickListDataSet().cloneDataSetView();
          // disable cache when a RowFilterListener is attached to the picklist
          // dataset,
          // since presumably row filtering will invalidate the rows in the
          // cache
          if (pickListDataSet.getRowFilterListeners() != null) {
            cache = false;
          }
          // in case a row listener was registered with this item editor,
          // use it in favor of the row listener attached to the picklist
          // dataset.
          if (listener != null) {
            if (pickListDataSet.getRowFilterListeners() != null)
              pickListDataSet.removeRowFilterListener(pickListDataSet
                  .getRowFilterListeners());
            pickListDataSet.addRowFilterListener(listener);
          }
          pickListDataSet.close();
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
            return;
          }
        }
        pickListDataSet.open();
        locateRow = new DataRow(pickListDataSet, sourceColumns);
        
        if (!cache) {
          choice.removeAll();
        }
        int ordinal = pickListDataSet.getColumn(displayColumns[0]).getOrdinal();
        if ((listener != null)
            || ((listener == null) && (pickListDataSet.getRowFilterListeners() != null))) {
          pickListDataSet.refilter();
        }
        pickListDataSet.first();
        while (pickListDataSet.inBounds()) {
          choice.add(pickListDataSet.format(ordinal));
          pickListDataSet.next();
        }
        // in case cache is false (and loadPickList is called more than
        // once), don't add the choice again to the panel
        if (getComponentCount() == 0) {
          add(choice, BorderLayout.CENTER);
        }
        
      } catch (Exception ex) {
        pickListDataSet = null;
        com.borland.jbcl.model.DataSetModel.handleException(this, ex, true);
      }
    }
  }
  
  public Object getValue() {
    
    try {
      if (pickListDataSet != null) {
        // Go to the row in the PickListDataSet corresponding to
        // the row selected by the user.
        pickListDataSet.goToRow(choice.getSelectedIndex());
        
        // // Copy the PickList columns to the corresponding destination
        // columns.
        // ReadRow.copyTo(sourceColumns, pickListDataSet, targetColumns,
        // targetDataSet);
      }
      
      // // Return the current column's value, or whatever value was copied into
      // the
      // // current column of the targetDataSet as a variant of the same type
      // // as the current column. Note that if a lookup is defined on this
      // column,
      // // then the value passed into startEdit() method will contain the
      // lookup
      // // display value rather than the actual column data value. Thus here we
      // // make sure we return the column's actual value, rather than the
      // display
      // // value.
      // targetDataSet.getVariant(currentColumnName, value);
      
    } catch (DataSetException ex) {
      value.setUnassignedNull();
      com.borland.jbcl.model.DataSetModel.handleException(this, ex, true);
      return value;
    }
    return pickListDataSet;
  }
  
  public Component getComponent() {
    return this;
  }
  
  public void startEdit(Object data, Rectangle bounds, ItemEditSite editSite) {
    Column currentColumn = null;
    ColumnVariant currentColumnVariant = null;
    
    setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
    this.editSite = editSite;
    
    if (data instanceof ColumnVariant) {
      currentColumnVariant = (ColumnVariant) data;
      value.setVariant(currentColumnVariant);
      currentColumn = currentColumnVariant.getColumn();
      currentColumnName = currentColumn.getColumnName();
      targetDataSet = currentColumnVariant.getDataSet();
      loadPickList(currentColumn.getPickList());
      // if there was a problem with information in the PickListDescriptor,
      // then quietly cancel the edit.
      if (pickListDataSet == null) {
        editSite.safeEndEdit(false);
      }
    }
    
    if (pickListDataSet != null && locateRow != null) {
      try {
        ;
        currentColumnVariant.getDataSet();
        ReadRow.copyTo(targetColumns, currentColumnVariant.getDataSet(),
            sourceColumns, locateRow);
        // Doing a locate() on the pickListDataSet should move the
        // current row to the target row if it exists.
        // We need to select() the corresponding row as an
        // index into the choices in the Choice (only if the locate
        // succeeds, i.e., the value exists in the dataset).
        //
        // Do limit check on choice.getItemCount() in case asynchronous query
        // performed
        //
        if (pickListDataSet.locate(locateRow, Locate.FIRST)
            && choice.getItemCount() > pickListDataSet.getRow()) {
          choice.select(pickListDataSet.getRow());
        }
      } catch (DataSetException ex) {
        com.borland.jbcl.model.DataSetModel.handleException(this, ex, true);
      }
    }
    
    choice.invalidate();
    
    if (editSite != null) {
      choice.setBackground(editSite.getBackground());
      choice.setForeground(editSite.getForeground());
      choice.setFont(editSite.getFont());
    }
    
    validate();
    setVisible(true);
    
    choice.requestFocus();
    
  }
  
  public void changeBounds(Rectangle bounds) {
    choice.invalidate();
    setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
    validate();
  }
  
  public boolean canPost() {
    return true;
  }
  
  public void endEdit(boolean post) {
    setBounds(0, 0, 0, 0);
    setVisible(false);
  }
  
  public void keyTyped(KeyEvent e) {
  }
  
  public void keyPressed(KeyEvent e) {
    int code = e.getKeyCode();
    int sel = choice.getSelectedIndex();
    switch (code) {
    case KeyEvent.VK_DOWN:
    case KeyEvent.VK_RIGHT:
      if (sel < (choice.getItemCount() - 1)) {
        choice.select(sel + 1);
      }
      e.consume();
      break;
    case KeyEvent.VK_LEFT:
    case KeyEvent.VK_UP:
      if (sel > 0) {
        choice.select(sel - 1);
      }
      e.consume();
      break;
    case KeyEvent.VK_HOME:
      choice.select(0);
      e.consume();
      break;
    case KeyEvent.VK_END:
      choice.select(choice.getItemCount() - 1);
      e.consume();
      break;
    case KeyEvent.VK_PAGE_DOWN:
      choice.select(Math.min(sel + 10, choice.getItemCount() - 1));
      e.consume();
      break;
    case KeyEvent.VK_PAGE_UP:
      choice.select(Math.max(sel - 10, 0));
      e.consume();
      break;
    }
  }
  
  public void keyReleased(KeyEvent e) {
  }
  
  @Override
  public void addKeyListener(KeyListener l) {
    choice.addKeyListener(l);
  }
  
  @Override
  public void removeKeyListener(KeyListener l) {
    choice.removeKeyListener(l);
  }
  
  @Override
  public void addFocusListener(FocusListener l) {
    choice.addFocusListener(l);
  }
  
  @Override
  public void removeFocusListener(FocusListener l) {
    choice.removeFocusListener(l);
  }
  
  public void itemStateChanged(ItemEvent e) {
    if (e.getID() == ItemEvent.ITEM_STATE_CHANGED) {
      editSite.safeEndEdit(true);
    }
  }
  
  String currentColumnName;
  String[] sourceColumns;
  String[] targetColumns;
  Column[] pickListColumns;
  String[] displayColumns;
  DataSetView pickListDataSet;
  DataSet targetDataSet;
  DataRow locateRow;
  boolean cache;
  Variant value;
  Choice choice = new Choice();
  transient RowFilterListener listener; // ignore for serialization
  transient ItemEditSite editSite; // ignore for serialization
}
