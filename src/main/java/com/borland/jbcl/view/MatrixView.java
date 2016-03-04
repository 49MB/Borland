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
package com.borland.jbcl.view;

import com.borland.jbcl.model.MatrixLocation;
import com.borland.jbcl.model.MatrixModel;
import com.borland.jbcl.model.MatrixModelListener;
import com.borland.jbcl.model.MatrixSelectionListener;
import com.borland.jbcl.model.MatrixSubfocusListener;
import com.borland.jbcl.model.MatrixViewManager;
import com.borland.jbcl.model.WritableMatrixModel;
import com.borland.jbcl.model.WritableMatrixSelection;

/**
 * Implementers of MatrixView have the simple properties that exist on all views
 * of a MatrixModel data source.
 */
public interface MatrixView
{
 /**
  * The model provides read/write access to matrix data.  A read-only
  * model is always passed, but a down-cast is attempted to use a read/write
  * model.
  */
  public MatrixModel getModel();
  public void setModel(MatrixModel model);

 /**
  * Provides access to a read/write model (if available).
  */
  public WritableMatrixModel getWriteModel();

 /**
  * Provides access to the data source's events.
  */
  public void addModelListener(MatrixModelListener listener);
  public void removeModelListener(MatrixModelListener listener);

 /**
  * The readOnly property overrides the existance of a writeable model.
  * When set to true, isReadOnly always returns true.  When set to false, isReadOnly
  * still returns true if the model is not writeable.
  */
  public boolean isReadOnly();
  public void setReadOnly(boolean readOnly);

 /**
  * The viewManager is a model of itemPainters and itemEditors based on the
  * the data type.
  */
  public MatrixViewManager getViewManager();
  public void setViewManager(MatrixViewManager viewManager);

 /**
  * The subfocus is the current position in the matrix with focus.
  */
  public MatrixLocation getSubfocus();
  public void setSubfocus(MatrixLocation subfocus);

 /**
  * Provides access to subfocus events.
  */
  public void addSubfocusListener(MatrixSubfocusListener listener);
  public void removeSubfocusListener(MatrixSubfocusListener listener);

 /**
  * The selection manages the selected items in the view.
  */
  public WritableMatrixSelection getSelection();
  public void setSelection(WritableMatrixSelection selection);

 /**
  * Provides access to the selection manager's events.
  */
  public void addSelectionListener(MatrixSelectionListener listener);
  public void removeSelectionListener(MatrixSelectionListener listener);
}
