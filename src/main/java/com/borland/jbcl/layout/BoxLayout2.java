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
package com.borland.jbcl.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.io.Serializable;

import javax.swing.BoxLayout;
/**
*  A Bean wrapper class for javax.swing.BoxLayout (Sun - the company that invented java beans)
*/
public class BoxLayout2 implements LayoutManager2 , Serializable {
BoxLayout layout;
int axis = BoxLayout.X_AXIS;
  public BoxLayout2() {
  }
  public BoxLayout2(Container parent, int axis) {
    this.axis = axis;
    layout = new BoxLayout(parent,axis);
  }
  public int getAxis() {
    return axis;
  }
  public void setAxis(int axis) {
    if (axis != this.axis ) {
      layout = null;
      this.axis = axis;
    }
  }
  void verifyInstance(Container parent) {
    if (layout == null)
      layout = new BoxLayout(parent,axis);
  }
  public void addLayoutComponent(Component component, Object constraint) {
    try {
      component.getParent().invalidate();
    }
    catch (Exception e) {}
  }
  public Dimension maximumLayoutSize(Container parent) {
      verifyInstance(parent);
      return layout.maximumLayoutSize(parent);
  }
  public float getLayoutAlignmentX(Container parent) {
     verifyInstance(parent);
     return layout.getLayoutAlignmentX(parent);
  }
  public float getLayoutAlignmentY(Container parent) {
     verifyInstance(parent);
     return layout.getLayoutAlignmentY(parent);
  }
  public void invalidateLayout(Container parent) {
     verifyInstance(parent);
     layout.invalidateLayout(parent);
  }
  public void addLayoutComponent(String name, Component component) {
    try {
      component.getParent().invalidate();
    }
    catch (Exception e) {}
  }
  public void removeLayoutComponent(Component component) {
    try {
      component.getParent().invalidate();
    }
    catch (Exception e) {}
  }
  public Dimension preferredLayoutSize(Container parent) {
      verifyInstance(parent);
      return layout.preferredLayoutSize(parent);
  }
  public Dimension minimumLayoutSize(Container parent) {
      verifyInstance(parent);
      return layout.minimumLayoutSize(parent);
  }
  public void layoutContainer(Container parent) {
      verifyInstance(parent);
      layout.layoutContainer(parent);
  }

}
