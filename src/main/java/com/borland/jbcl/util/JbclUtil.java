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
// Copyright (c) 1996 - 2004 Borland Software Corporation.  All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jbcl.util;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Toolkit;

public class JbclUtil {
  public static int findInsertPoint(int xClick, String text, Point clickPoint, Font font) {
    int position;
    // The editing was initiated with a mouse click. Here we will set the insertion point
    // at the click point
    FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
    int widths[] = metrics.getWidths();
    int i = 0;
    for (int x = 0; i < text.length(); i++) {
      // int charWidth = widths[text.charAt(i)];
      // ktien: widths[] from getWidths() is only good for
      // Latin-1 characters (i.e. first 256 characters).
      int charWidth;
      if (text.charAt(i) < 256) {
        charWidth = widths[text.charAt(i)];
      }
      else {
        charWidth = metrics.charWidth(text.charAt(i));
      }
      int offset = charWidth > 3? charWidth/3 : 1;
      if (x+offset >= xClick) {
        i--;
        break;
      }
      if (x+charWidth-offset >= xClick) {
        break;
      }
      x += charWidth;
    }
    position = i;
    return position;
  }
}
