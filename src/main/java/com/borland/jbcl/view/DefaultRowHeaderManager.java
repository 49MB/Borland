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

import java.awt.Insets;
import java.io.Serializable;

import com.borland.dx.text.Alignment;
import com.borland.jbcl.model.ItemEditor;
import com.borland.jbcl.model.ItemPainter;
import com.borland.jbcl.model.MatrixModelEvent;
import com.borland.jbcl.model.MatrixModelListener;
import com.borland.jbcl.model.VectorModel;
import com.borland.jbcl.model.VectorModelEvent;
import com.borland.jbcl.model.VectorModelListener;
import com.borland.jbcl.model.VectorViewManager;

// This the rowHeader's default model and view manager
// this can be overridden by subclassing GridView and implementing createDefaultRowHeaderView()
public class DefaultRowHeaderManager implements VectorViewManager, VectorModel, MatrixModelListener, Serializable
{
  public DefaultRowHeaderManager(GridView grid) {
    this.grid = grid;
  }
  public ItemPainter getPainter(int index, Object data, int state) { return painter; }
  public ItemEditor getEditor(int index, Object data, int state) { return null; }
  public Object get(int index) { return Integer.toString(index); }
  public int find(Object data) { return -1; }
  public int getCount() { return grid.getModel().getRowCount(); }
  public void addModelListener(VectorModelListener listener) {
    modelListeners.add(listener);
    if (modelListeners.getListenerCount() == 1)
      grid.addModelListener(this);
  }
  public void removeModelListener(VectorModelListener listener) {
    modelListeners.remove(listener);
    if (modelListeners.getListenerCount() == 0)
      grid.removeModelListener(this);
  }
  protected void processModelEvent(VectorModelEvent e) { modelListeners.dispatch(e); }

  public void modelContentChanged(MatrixModelEvent e) {}
  public void modelStructureChanged(MatrixModelEvent e) {
    processModelEvent(new VectorModelEvent(this, VectorModelEvent.ITEM_ADDED, 0));
  }

  private transient com.borland.jb.util.EventMulticaster modelListeners = new com.borland.jb.util.EventMulticaster();
  private GridView grid;
  private ItemPainter painter = new ButtonItemPainter(new FocusableItemPainter(new TextItemPainter(Alignment.RIGHT | Alignment.MIDDLE, new Insets(0,2,0,2))));
}
