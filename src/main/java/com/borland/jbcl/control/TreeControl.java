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

import java.awt.Image;

import com.borland.jbcl.model.BasicGraphSelection;
import com.borland.jbcl.model.BasicTreeContainer;
import com.borland.jbcl.model.BasicViewManager;
import com.borland.jbcl.model.GraphLocation;
import com.borland.jbcl.model.GraphModel;
import com.borland.jbcl.model.ItemEditor;
import com.borland.jbcl.model.ItemPainter;
import com.borland.jbcl.model.WritableGraphModel;
import com.borland.jbcl.util.BlackBox;
import com.borland.jbcl.util.ImageLoader;
import com.borland.jbcl.view.ExpandingTextItemEditor;
import com.borland.jbcl.view.FocusableItemPainter;
import com.borland.jbcl.view.SelectableItemPainter;
import com.borland.jbcl.view.TextItemPainter;
import com.borland.jbcl.view.TreeView;

public class TreeControl
     extends TreeView
  implements BlackBox, GraphModel, java.io.Serializable
{
  public TreeControl() {
    setModel(new BasicTreeContainer());
    setupViewers();
    setSelection(new BasicGraphSelection());
  }

  public void setItems(String[] items) {
    this.items = items;
    TreeItems ti = new TreeItems(items);
    setModel(ti.getModel());
    setupViewers();
  }
  public String[] getItems() {
    return items;
  }

  public void setTextureName(String path) {
    if (path != null && !path.equals("")) {
      Image i = ImageLoader.load(path, this);
      if (i != null) {
        ImageLoader.waitForImage(this, i);
        textureName = path;
        setTexture(i);
      }
      else {
        throw new IllegalArgumentException(path);
      }
    }
    else {
      textureName = null;
      setTexture(null);
    }
  }
  public String getTextureName() {
    return textureName;
  }

  public void setModel(GraphModel model) {
    if (model == this)
      throw new IllegalArgumentException(Res._RecursiveModel);     
    super.setModel(model);
  }

  private void setupViewers() {
    ItemPainter iv = new FocusableItemPainter(new SelectableItemPainter(new TextItemPainter()));
    ItemEditor ie = new ExpandingTextItemEditor();
    setViewManager(new BasicViewManager(iv, ie));
  }

  // WritableGraphModel Implementation

  public GraphLocation getRoot() {
    GraphModel m = getModel();
    return m != null ? m.getRoot() : null;
  }

  public Object get(GraphLocation node) {
    GraphModel m = getModel();
    return m != null ? m.get(node) : null;
  }

  public GraphLocation find(Object data) {
    GraphModel m = getModel();
    return m != null ? m.find(data) : null;
  }

  public GraphLocation setRoot(Object data) {
    WritableGraphModel m = getWriteModel();
    return m != null ? m.setRoot(data) : null;
  }

  public boolean canSet(GraphLocation node) {
    WritableGraphModel m = getWriteModel();
    return m != null ? m.canSet(node) : false;
  }

  public void set(GraphLocation node, Object data) {
    WritableGraphModel m = getWriteModel();
    if (m != null)
      m.set(node, data);
  }

  public void touched(GraphLocation node) {
    WritableGraphModel m = getWriteModel();
    if (m != null)
      m.touched(node);
  }

  public boolean isVariableSize() {
    WritableGraphModel m = getWriteModel();
    return m != null ? m.isVariableSize() : false;
  }

  public GraphLocation addChild(GraphLocation parent, Object data) {
    WritableGraphModel m = getWriteModel();
    return m != null ? m.addChild(parent, data) : null;
  }

  public GraphLocation addChild(GraphLocation parent, GraphLocation aheadOf, Object data) {
    WritableGraphModel m = getWriteModel();
    return m != null ? m.addChild(parent, aheadOf, data) : null;
  }

  public void removeChildren(GraphLocation parent) {
    WritableGraphModel m = getWriteModel();
    if (m != null)
      m.removeChildren(parent);
  }

  public void remove(GraphLocation node) {
    WritableGraphModel m = getWriteModel();
    if (m != null)
      m.remove(node);
  }

  /**
   * @DEPRECATED - To remove data items, use removeAllItems()
   * DANGER!  This method in java.awt.Container is intended to remove all child components
   * of a Container.  In this case, that would be the viewport (TreeCore), and the scrollbar.
   * DO NOT CALL THIS METHOD unless you intend to remove the tree's subcomponents (very unlikely).
   * To remove the tree's data, use removeAllItems().
   */
  public void removeAll() {
    super.removeAll();
  }

  public void removeAllItems() {
    WritableGraphModel m = getWriteModel();
    if (m != null)
      m.removeAll();
  }

  public void enableModelEvents(boolean enable) {
    WritableGraphModel m = getWriteModel();
    if (m != null)
      m.enableModelEvents(enable);
  }

  private String[] items;
  protected String textureName;
}
