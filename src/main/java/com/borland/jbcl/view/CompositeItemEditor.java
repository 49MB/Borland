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
//------------------------------------------------------------------------------
package com.borland.jbcl.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;

import com.borland.jbcl.model.ItemEditSite;
import com.borland.jbcl.model.ItemEditor;
import com.borland.jbcl.model.ToggleItemEditor;
import com.borland.jbcl.util.KeyMulticaster;

/**
 * The CompositeItemEditor is used to be able to separately edit items painted
 * by a CompositeItemPainter.  It uses a pre-assembled CompositeItemPainter to
 * calculate the rectangles for the individual items, and delegates all ItemEditor
 * methods to the appropriate ItemEditor.
 */
public class CompositeItemEditor implements ToggleItemEditor, Serializable
{
  private static final long serialVersionUID = 200L;

  public CompositeItemEditor(CompositeItemPainter compositePainter, ItemEditor firstEditor, ItemEditor secondEditor, int defaultEditor) {
    this(compositePainter, firstEditor, secondEditor);
    this.defaultEditor = defaultEditor;
  }

  public CompositeItemEditor(CompositeItemPainter compositePainter, ItemEditor firstEditor, ItemEditor secondEditor) {
    this.compositePainter = compositePainter;
    this.editor1 = firstEditor;
    this.editor2 = secondEditor;
    if (editor1 != null)
      editor1.addKeyListener(keyMulticaster);
    if (editor2 != null)
      editor2.addKeyListener(keyMulticaster);
    if (editor1 != null)
      this.defaultEditor = 1;
    else if (editor2 != null)
      this.defaultEditor = 2;
  }

  public CompositeItemEditor(CompositeItemPainter compositePainter) {
    this(compositePainter, null, null);
  }

  public CompositeItemEditor() {}

  public void setCompositeItemPainter(CompositeItemPainter compositePainter) {
    this.compositePainter = compositePainter;
  }
  public CompositeItemPainter getCompositeItemPainter() {
    return compositePainter;
  }

  public void setEditor1(ItemEditor firstEditor) {
    editor1 = firstEditor;
  }
  public ItemEditor getEditor1() {
    return editor1;
  }

  public void setEditor2(ItemEditor secondEditor) {
    editor2 = secondEditor;
  }
  public ItemEditor getEditor2() {
    return editor2;
  }

  public int getDefaultEditor() {
    return defaultEditor;
  }

  public void setDefaultEditor(int defaultEditor) {
    if (defaultEditor > 0 && defaultEditor <= 2)
      this.defaultEditor = defaultEditor;
    else
      throw new IllegalArgumentException();
  }

  // ItemEditor Implementation

  public Object getValue() {
    if (activeEditor == 1 && editor1 != null) {
      return editor1.getValue();
    }
    else if (activeEditor == 2 && editor2 != null) {
      return editor2.getValue();
    }
    else {
      return null;
    }
  }

  public Component getComponent() {
    if (currentEditor != null) //non-null between startEdit and endEdit
      return currentEditor.getComponent();
    //we do not know which one will be the editor until startEdit is called
    if (editor1 != null && editor2 != null)
      return null;

    if (activeEditor == 1 && editor1 != null)
      return editor1.getComponent();
    else if (activeEditor == 2 && editor2 != null)
      return editor2.getComponent();
    else if (defaultEditor == 1 && editor1 != null)
      return editor1.getComponent();
    else if (defaultEditor == 2 && editor2 != null)
      return editor2.getComponent();
    else
      return null;
  }

  public void startEdit(Object data, Rectangle bounds, ItemEditSite editSite) {
    this.data = data;
    this.site = editSite;
    Rectangle rect1 = new Rectangle();
    Rectangle rect2 = new Rectangle();
    compositePainter.calculateRects(data, editSite.getSiteGraphics(), bounds, 0, editSite, rect1, rect2);
    Point click = editSite.getEditClickPoint();
    int hitEditor = hitTest(click, rect1, rect2);
    if (hitEditor == 0) {
      if (editor1 != null)
        hitEditor = 1;
      else
        hitEditor = 2;
    }
    activeEditor = hitEditor;
    currentEditor = null;
    Rectangle currentRect = null;
    if (activeEditor == 1 && editor1 != null) {
      currentEditor = editor1;
      currentRect = rect1;
    }
    else if (activeEditor == 2 && editor2 != null) {
      currentEditor = editor2;
      currentRect = rect2;
    }
    else if (defaultEditor == 1 && editor1 != null) {
      currentEditor = editor1;
      currentRect = rect1;
    }
    else if (defaultEditor == 2 && editor2 != null) {
     currentEditor = editor2;
     currentRect = rect2;
    }
    if (editor1 != null && editor2 != null && currentEditor.getComponent() != null) {
      // we have told anyone who asked we had no component because we did not know which one till now...
      // so we need to do what we hope they would have done if we could have told them
      currentEditor.getComponent().setVisible(false);
      try {
        ((Container)editSite.getSiteComponent()).add(currentEditor.getComponent());
      }
      catch (ClassCastException e) {
        editSite.getSiteComponent().getParent().add(currentEditor.getComponent());
      }
    }
    currentEditor.startEdit(data, currentRect, editSite);
  }

  public void changeBounds(Rectangle bounds) {
    Rectangle rect1 = new Rectangle();
    Rectangle rect2 = new Rectangle();
    if (site != null)
      compositePainter.calculateRects(data, site.getSiteGraphics(), bounds, 0, site, rect1, rect2);
    else
      rect1 = rect2 = bounds;
    if (activeEditor == 1 && editor1 != null) {
      editor1.changeBounds(rect1);
    }
    else if (activeEditor == 2 && editor2 != null) {
      editor2.changeBounds(rect2);
    }
  }

  public boolean canPost() {
    if (activeEditor == 1 && editor1 != null) {
      return editor1.canPost();
    }
    else if (activeEditor == 2 && editor2 != null) {
      return editor2.canPost();
    }
    else {
      return false;
    }
  }

  public void endEdit(boolean posted) {
    if (activeEditor == 1 && editor1 != null) {
      editor1.endEdit(posted);
    }
    else if (activeEditor == 2 && editor2 != null) {
      editor2.endEdit(posted);
    }
    currentEditor = null;
    activeEditor = 0;
    data = null;
  }

  public boolean isToggle(Object data, Rectangle rect, ItemEditSite editSite) {
    if (editor1 instanceof ToggleItemEditor || editor2 instanceof ToggleItemEditor) {
      this.data = data;
      this.site = editSite;
      Rectangle rect1 = new Rectangle();
      Rectangle rect2 = new Rectangle();
      compositePainter.calculateRects(data, editSite.getSiteGraphics(), rect, 0, editSite, rect1, rect2);
      Point click = editSite.getEditClickPoint();
      int hit = hitTest(click, rect1, rect2);
      if (hit == 1 && editor1 instanceof ToggleItemEditor && ((ToggleItemEditor)editor1).isToggle(data, rect1, site))
        return true;
      else if (hit == 2 && editor2 instanceof ToggleItemEditor && ((ToggleItemEditor)editor2).isToggle(data, rect2, site))
        return true;
    }
    return false;
  }

  public void addKeyListener(KeyListener l) { keyMulticaster.add(l); }
  public void removeKeyListener(KeyListener l) { keyMulticaster.remove(l); }

  // Internal

  // Return which editor (or painter) contains clickPoint
  public int hitTest(Point clickPoint, Rectangle rect1, Rectangle rect2) {
    if (clickPoint != null && rect1 != null && rect2 != null)
      return rect1.contains(clickPoint) ? 1 : rect2.contains(clickPoint) ? 2 : 0;
    else
      return defaultEditor;
  }

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    Hashtable hash = new Hashtable(2);
    if (editor1 instanceof Serializable)
      hash.put("1", editor1); 
    if (editor2 instanceof Serializable)
      hash.put("2", editor2); 
    s.writeObject(hash);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    Hashtable hash = (Hashtable)s.readObject();
    Object data = hash.get("1"); 
    if (data instanceof ItemEditor)
      editor1 = (ItemEditor)data;
    data = hash.get("2"); 
    if (data instanceof ItemEditor)
      editor2 = (ItemEditor)data;
  }

  private transient ItemEditor editor1;
  private transient ItemEditor editor2;
  private transient ItemEditSite site; // do not serialize (transient data)
  private transient Object data; // do not serialize (transient data)

  private CompositeItemPainter compositePainter;
  private int activeEditor = 0;
  private int defaultEditor;
  private transient ItemEditor currentEditor; // do not serialize (transient data)
  private transient KeyMulticaster keyMulticaster = new KeyMulticaster();
}
