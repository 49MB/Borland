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

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.KeyListener;

/**
 * The ItemEditor interface defines a single item editor for editing data items.
 */
public interface ItemEditor
{
  /**
   * The editor host may ask for the value at anytime,
   * and will always ask for it when posting.
   * @return The current value in the editor.
   */
  public Object getValue();

  /**
   * Returns the actual component that will be added to the host as an editor.
   * If an editor is a composite component (like a panel with several components
   * in it), be sure to re-dispatch all key events from the main component to the
   * individual components.
   * An ItemEditor is not required to subclass awt.component: returning null is valid.
   * @return The component to be added to the host control as an editor.
   */
  public Component getComponent();

  /**
   * Begin an edit session. This is called after the editor is added to the host
   * container.  There are certain guidelines to follow for an ItemEditor to work
   * properly in a host container:  When this method is called on the class that
   * implements ItemEditor, it should:<BR>
   * <OL>
   * <LI> Immediately make a local copy of the passed data value.
   * <LI> Set the main editor component visible, and make sure to properly
          layout any sub-components within the given rectangle (rect).
   * <LI> The ItemEditor should then request focus.
   * </OL>
   * @param data The data object to start editing.
   * @param rect The rect for the item to be edited.
   * @param editSite Access to the editor host information.
   */
  public void startEdit(Object data, Rectangle rect, ItemEditSite editSite);

  /**
   * Called when the editor site changes size due to a resize of the editor
   * host.  The editor should adjust its bounds to fit in the given rectangle,
   * and be sure to properly layout any sub-components.
   * @param rect The new Rectangle for the editor to occupy.
   */
  public void changeBounds(Rectangle rect);

  /**
   * Is the current value in the editor valid for posting?
   * @return True if the value is valid for posting, false if not.
   */
  public boolean canPost();

  /**
   * End edit is always called just before the editor is removed. This can
   * be used to do any cleanup work in the ItemEditor.  The posted flag is
   * for informational purposes only, and may be ignored.
   * @param posted True if the item was posted, false if not.
   */
  public void endEdit(boolean posted);

  /**
   * ItemEditors can supply key events to the host container - which can
   * trigger an endEdit call.  If an editor is a composite component (like
   * a panel with several components in it), be sure to re-dispatch all key
   * events from the main component to the individual components.
   */
  public void addKeyListener(KeyListener l);
  public void removeKeyListener(KeyListener l);
}
