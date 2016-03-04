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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

import com.borland.jbcl.model.ItemPaintSite;
import com.borland.jbcl.model.ItemPainter;

/**
 * state/value pairs are registered in LIFO ordering (last one checked first)
 * Checking succeeds if: the states are equal (so that 0 matches) or their bitwise AND is not zero
 * The last pair registered with state of 0 is considered the default to use when no matches
 */
public class StateItemPainter implements ItemPainter, Serializable
{
  public StateItemPainter() {}

  public StateItemPainter(ItemPainter painter) {
    this.painter = painter;
  }

  public void register(int state, int value) {
    if (state == 0) {
      defaultValue = new Integer(value);
    }
    else {
      states.insertElementAt(new Integer(state), 0);
      values.insertElementAt(new Integer(value), 0);
    }
  }

  public Dimension getPreferredSize(Object data, Graphics g, int state, ItemPaintSite site) {
    return (painter != null) ? painter.getPreferredSize(getValue(state), g, state, site) : new Dimension();
  }

  public void paint(Object data, Graphics g, Rectangle bounds, int state, ItemPaintSite site) {
    if (painter != null)
      painter.paint(getValue(state), g, bounds, state, site);
  }

  public Integer getValue(int state) {
    for (int i = 0; i < states.size(); i++) {
      int thisState = ((Integer)states.elementAt(i)).intValue();
      if (state == thisState || (state & thisState) != 0)
        return (Integer)values.elementAt(i);
    }
    return defaultValue;
  }

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    s.writeObject(painter instanceof Serializable ? painter : null);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    Object data = s.readObject();
    if (data instanceof ItemPainter)
      painter = (ItemPainter)data;
  }

  transient ItemPainter painter;
  Vector   values = new Vector();
  Vector   states = new Vector();
  Integer defaultValue;
}
