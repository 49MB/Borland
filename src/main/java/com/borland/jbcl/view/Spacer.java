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

import java.awt.Component;
import java.awt.Dimension;

/**
 * Component to use as a spacer to place between other components in layouts.
 */
public class Spacer extends Component implements java.io.Serializable
{
  private static final long serialVersionUID = 200L;

  /**
   * Constructs a spacer component with the default size settings.<BR><UL>
   * <LI>preferredSize = 10x10
   * <LI>minimumSize = null
   * <LI>maximumSize = null</UL>
   */
  public Spacer() {}

  /**
   * Constructs a spacer component with the specified preferredSize setting
   * and default minimum (null) and maximum (null) size settings.
   * <LI>minimumSize = null
   * <LI>maximumSize = null</UL>
   */
  public Spacer(int size) {
    this.preferredSize = new Dimension(size, size);
  }

  /**
   * Constructs a spacer component with the specified preferredSize setting
   * and default minimum (null) and maximum (null) size settings.
   */
  public Spacer(Dimension preferredSize) {
    this.preferredSize = preferredSize;
  }

  /**
   * Constructs a spacer component with the specified preferred and minimum
   * size settings and default maximum (null) size setting.
   */
  public Spacer(Dimension preferredSize, Dimension minimumSize) {
    this.preferredSize = preferredSize;
    this.minimumSize = minimumSize;
  }

  /**
   * Constructs a spacer component with the specified preferred, minimum,
   * and maximum size settings.
   */
  public Spacer(Dimension preferredSize, Dimension minimumSize, Dimension maximumSize) {
    this.preferredSize = preferredSize;
    this.minimumSize = minimumSize;
    this.maximumSize = maximumSize;
  }

  /**
   * The preferred size for this spacer component.
   */
  public void setPreferredSize(Dimension preferredSize) {
    this.preferredSize = preferredSize;
  }
  public Dimension getPreferredSize() {
    return preferredSize;
  }

  /**
   * The minimum size for this spacer component.
   */
  public void setMinimumSize(Dimension minimumSize) {
    this.minimumSize = minimumSize;
  }
  public Dimension getMinimumSize() {
    return minimumSize;
  }

  /**
   * The maximum size for this spacer component.
   */
  public void setMaximumSize(Dimension maximumSize) {
    this.maximumSize = maximumSize;
  }
  public Dimension getMaximumSize() {
    return maximumSize;
  }

  protected Dimension preferredSize = new Dimension(10, 10);
  protected Dimension minimumSize;
  protected Dimension maximumSize;
}
