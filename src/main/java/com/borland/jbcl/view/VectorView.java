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

import com.borland.jbcl.model.VectorModel;
import com.borland.jbcl.model.VectorModelListener;
import com.borland.jbcl.model.VectorSelectionListener;
import com.borland.jbcl.model.VectorSubfocusListener;
import com.borland.jbcl.model.VectorViewManager;
import com.borland.jbcl.model.WritableVectorModel;
import com.borland.jbcl.model.WritableVectorSelection;

/**
 * Implementers of VectorView have the simple properties that exist on all views
 * of a VectorModel data source.
 */
public interface VectorView
{
 /**
  * The model provides read/write access to vector data.  A read-only
  * model is always passed, but a down-cast is attempted to use a read/write
  * model.
  */
  public VectorModel getModel();
  public void setModel(VectorModel model);

 /**
  * Provides access to a read/write model (if available).
  */
  public WritableVectorModel getWriteModel();

 /**
  * Provides access to the data source's events.
  */
  public void addModelListener(VectorModelListener listener);
  public void removeModelListener(VectorModelListener listener);

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
  public VectorViewManager getViewManager();
  public void setViewManager(VectorViewManager viewManager);

 /**
  * The subfocus is the current position in the vector with focus.
  */
  public int getSubfocus();
  public void setSubfocus(int subfocus);

 /**
  * Provides access to subfocus events.
  */
  public void addSubfocusListener(VectorSubfocusListener listener);
  public void removeSubfocusListener(VectorSubfocusListener listener);

 /**
  * The selection manages the selected items in the view.
  */
  public WritableVectorSelection getSelection();
  public void setSelection(WritableVectorSelection selection);

 /**
  * Provides access to the selection manager's events.
  */
  public void addSelectionListener(VectorSelectionListener listener);
  public void removeSelectionListener(VectorSelectionListener listener);
}
