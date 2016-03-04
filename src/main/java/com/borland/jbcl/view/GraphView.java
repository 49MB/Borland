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

import com.borland.jbcl.model.GraphLocation;
import com.borland.jbcl.model.GraphModel;
import com.borland.jbcl.model.GraphModelListener;
import com.borland.jbcl.model.GraphSelectionListener;
import com.borland.jbcl.model.GraphSubfocusListener;
import com.borland.jbcl.model.GraphViewManager;
import com.borland.jbcl.model.WritableGraphModel;
import com.borland.jbcl.model.WritableGraphSelection;

/**
 * Implementers of GraphView have the simple properties that exist on all views
 * of a GraphModel data source.
 */
public interface GraphView
{
 /**
  * The model provides read/write access to graph data.  A read-only
  * model is always passed, but a down-cast is attempted to use a read/write
  * model.
  */
  public GraphModel getModel();
  public void setModel(GraphModel model);

 /**
  * Provides access to a read/write model (if available).
  */
  public WritableGraphModel getWriteModel();

 /**
  * Provides access to the data source's events.
  */
  public void addModelListener(GraphModelListener listener);
  public void removeModelListener(GraphModelListener listener);

 /**
  * The readOnly property overrides the existance of a writeable model.
  * When set to true, isReadOnly always returns true.  When set to false, isReadOnly
  * still returns true if the model is not writeable.
  */
  public boolean isReadOnly();
  public void setReadOnly(boolean readOnly);

 /**
  * The viewManager provides itemPainters and itemEditors based on the model item's
  * data type.
  */
  public GraphViewManager getViewManager();
  public void setViewManager(GraphViewManager viewManager);

 /**
  * The subfocus is the current position in the graph with focus.
  */
  public GraphLocation getSubfocus();
  public void setSubfocus(GraphLocation subfocus);

 /**
  * Provides access to subfocus events.
  */
  public void addSubfocusListener(GraphSubfocusListener listener);
  public void removeSubfocusListener(GraphSubfocusListener listener);

 /**
  * The selection manages the selected items in the view.
  */
  public WritableGraphSelection getSelection();
  public void setSelection(WritableGraphSelection selection);

 /**
  * Provides access to the selection manager's events.
  */
  public void addSelectionListener(GraphSelectionListener listener);
  public void removeSelectionListener(GraphSelectionListener listener);
}
