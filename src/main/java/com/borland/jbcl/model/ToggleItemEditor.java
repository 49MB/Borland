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

import java.awt.Rectangle;

/**
 * The ToggleItemEditor interface is an extension to the ItemEditor interface that
 * notifies (with an instanceof check and a call to isToggle) all JBCL components
 * that this editor is a toggle.  The JBCL components will then start an edit session
 * with this editor on the first click (or SPACE) on the item.
 *
 * A ToggleItemEditor is expected to respond to the startEdit method by toggling
 * whatever value it needs to - then promptly call ItemEditSite.safeEndEdit(...)
 * to terminate the edit session.  A ToggleItemEditor typically does not provide
 * an editor component to embed in the view - and relies on an ItemPainter to
 * display the results of the toggle change.
 */
public interface ToggleItemEditor extends ItemEditor
{
  /**
   * The isToggle method allows a nesting ItemEditor to ask its nested ItemEditors
   * if they are toggles - and respond appropriately to the view.  An instanceof
   * ToggleItemEditor check would fail if the outermost ItemEditor in the nesting
   * chain is not a ToggleItemEditor.  This allows it to forward on the query, and
   * see if the passed point should trigger a toggle.
   *
   * @param data The data object from the model
   * @param rect The rectangle of the item
   * @param site The call-back ItemEditSite to get information from the view.
   * @return true if the conditions warrant a data toggle, false if not.
   */
  public boolean isToggle(Object data, Rectangle rect, ItemEditSite site);
}
