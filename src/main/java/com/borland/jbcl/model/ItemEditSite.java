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
package com.borland.jbcl.model;

import java.awt.Graphics;
import java.awt.Point;

/**
 * This interface is implemented where ItemEditors can be provided with more
 * information about their host containers and the details of a particular
 * edit session.
 */
public interface ItemEditSite extends ItemPaintSite
{
  /**
   * This method allows an ItemEditor to request the control to end the
   * edit session for this item.
   * @param post Setting to indicate if the control should save the changes.
   */
  public void safeEndEdit(boolean post);

  /**
   * Returns the mouse click Point that initiated the edit session for this
   * item.  This is used to determine where to place text cursor in a TextField,
   * or which editor to start when using a CompositeItemEditor.
   * @return The point (relative to the container) where the user clicked to
   * initiate the edit session.
   */
  public Point getEditClickPoint();

  /**
   * Returns a correctly assembled Graphics object for coordinate comparisons with
   * ItemPainter coordinate calculations.
   * @return A Graphics object correctly prepared for a paint of the current item.
   */
  public Graphics getSiteGraphics();
}
