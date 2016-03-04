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

import com.borland.jbcl.model.SingletonModel;
import com.borland.jbcl.model.SingletonModelListener;
import com.borland.jbcl.model.WritableSingletonModel;

/**
 * Implementers of SingletonModelView have the simple properties that exist on all views
 * of a SingletonModel data source.
 */
public interface SingletonModelView
{
 /**
  * The model provides access to a singleton data source.  A read-only model
  * is always passed, but a down-cast is attempted to access a read/write model.
  */
  public SingletonModel getModel();
  public void setModel(SingletonModel model);

 /**
  * Provides access to a singleton data source's events.
  */
  public void addModelListener(SingletonModelListener listener);
  public void removeModelListener(SingletonModelListener listener);

 /**
  * Provides access to a read/write model (if available).
  */
  public WritableSingletonModel getWriteModel();

 /**
  * The readOnly property overrides the existance of a writeable model.
  * When set to true, isReadOnly always returns true.  When set to false, isReadOnly
  * still returns true if the model is not writeable.
  */
  public boolean isReadOnly();
  public void setReadOnly(boolean readOnly);
}
