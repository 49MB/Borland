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

import java.awt.Insets;

import com.borland.dx.dataset.Column;
import com.borland.jbcl.model.ItemEditor;
import com.borland.jbcl.model.ItemPainter;
import com.borland.jbcl.view.ColumnView;

/**
 * DatasetColumnView is a simple extention of ColumnView that fills the properties in
 * ColumnView from the passed com.borland.dx.dataset.Column object.  It has no extra
 * functionality except for its constructors.
 */
public class DatasetColumnView extends ColumnView implements java.io.Serializable
{
  public DatasetColumnView(Column column) {
    bindColumn(column);
  }

  public DatasetColumnView(Column column, ColumnView clonee) {
    super(clonee);
    bindColumn(column);
  }

  // sets each of the properties in the ColumnView from the Column
  // if they were not already set by the user.
  //
  private void bindColumn(Column column) {
    if (column == null)
      return;
    name = column.getColumnName();
//    ordinal = column.getOrdinal();

//    if (column.getPickList() != null)
//      column = MatrixDataSetManager.getDisplayColumn(column);

    if (!userSetFont)
      font = column.getFont();
    if (!userSetAlignment)
      alignment = column.getAlignment();
    if (!userSetBackground)
      background = column.getBackground();
    if (!userSetForeground)
      foreground = column.getForeground();
    if (!userSetCaption)
      caption = column.getCaption();
    if (!userSetItemPainter)
      itemPainter = (ItemPainter)column.getItemPainter();
    if (!userSetItemEditor)
      itemEditor = (ItemEditor)column.getItemEditor();
    if (!userSetMargins)
      margins = new Insets(0, 2, 0, 2);
  }
}
