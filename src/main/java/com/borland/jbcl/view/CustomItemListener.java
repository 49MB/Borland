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
//-------------------------------------------------------------------------------------------------
package com.borland.jbcl.view;

import com.borland.dx.dataset.CustomPaintSite;
import com.borland.jbcl.control.FieldControl;
import com.borland.jbcl.control.GridControl;
import com.borland.jbcl.control.ListControl;
import com.borland.jbcl.control.TreeControl;
import com.borland.jbcl.model.ItemEditor;
import com.borland.jbcl.model.ItemPainter;

/**
 * An implementor of CustomItemListener is listening to a ViewManager receive requests
 * for ItemPainters and ItemEditors.  When a request is made, an event is fired to allow
 * a listener (implementor of this interface) to customize the properties of the ItemPaintSite
 * via the CustomPaintSite interface.  The listener has access to all contextual information
 * in the event call, including address, data, and state information.  The listener can analyze
 * any of this information (or none of it) to determine what custom changes to make to this
 * CustomPaintSite.<P>
 *
 * An common useage of these events would be to make all negative numbers appear red on
 * any JBCL component - and for even numbered rows to have a gray background.  Following is
 * an example event handler that demonstrates how this would be accomplished:<P>
 *
 * <CODE>
 * public void customizeItem(Object address, Object data, int state, CustomPaintSite site) {
 *   // make the foreground red if the data is numeric, and is a negative number.
 *   if (data instanceof Number) {
 *     int value = ((Number)data).intValue();
 *     if (value < 0)
 *       site.setForeground(Color.red);
 *   }
 *   // make all even numbered rows on a grid have a light gray background.
 *   if (address instanceof MatrixLocation) {
 *     int row = ((MatrixLocation)address).row;
 *     if (row % 2 == 0)
 *       site.setBackground(Color.lightGray);
 *   }
 * }
 * </CODE>
 *
 * @see CustomViewManager
 * @see ItemPainter
 * @see ItemEditor
 * @see CustomPaintSite
 * @see GridControl
 * @see TreeControl
 * @see ListControl
 * @see FieldControl
 */
public interface CustomItemListener extends java.util.EventListener
{
  /**
   * This event is triggered when a ViewManager is asked for an ItemPainter to paint an item
   * within a component, or asked for an ItemEditor to edit a component.  A listener can
   * 'hook-in' and set the properties of the CustomPaitSite based on the address, data, state
   * information, or whatever they want.
   *
   * @param address The address object of the data item.  This will be 'null' for Singleton, An
   *        'Integer' for Vector, a 'MatrixLocation' for Matrix, or a 'GraphLocation' for Graph.
   * @param data The data object from the model
   * @param state The state bitmask information (FOCUSED, SELECTED, etc...)
   * @param site The CustomPaintSite that allows the listener to set the background, foreground,
   *        font, alignment, and itemMargins for the particular item being painted (or edited).
   */
  public void customizeItem(Object address, Object data, int state, CustomPaintSite site);
}
