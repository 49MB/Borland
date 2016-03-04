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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import com.borland.dx.text.Alignment;
import com.borland.jbcl.model.ItemPaintSite;

/**
 * Image/state pairs are registered in LIFO ordering (last one checked first)
 * Checking succeeds if: the states are equal (so that 0 matches) or their bitwise AND is not zero
 * The last registered image with state 0 is considered the default one, and returned if no others
 * match.
 */
public class StateImageItemPainter extends ImageArrayItemPainter implements java.io.Serializable
{
  StateItemPainter statePainter = new StateItemPainter();

  public StateImageItemPainter(Component component, int alignment) {
    super(component, alignment, new Image[0]);
    genDisabledImage = false;
  }

  public StateImageItemPainter(Component component) {
    this(component, Alignment.LEFT);
  }

  public StateImageItemPainter() {
    super();
  }

  public void register(int state, Image image) {
    int i = find(image);
    if (i < 0)
      i = add(image);
    statePainter.register(state, i);
  }

  public Dimension getPreferredSize(Object object, Graphics g, int state, ItemPaintSite site) {
    return super.getPreferredSize(statePainter.getValue(state), g, state, site);
  }

  public void paint(Object object, Graphics g, Rectangle bounds, int state, ItemPaintSite site) {
    super.paint(statePainter.getValue(state), g, bounds, state, site);
  }

  /*
  protected Image getImage(Object object, int state) {
    Image defaultImage = null;
    for (int i = 0; i < states.getSize(); i++) {
      Integer thisState = (Integer)states.at(i);
      if (state == thisState.intValue() || (state&thisState.intValue()) != 0)
        return (Image)images.at(i);
    }
    return defaultImage;
  }
*/
}
