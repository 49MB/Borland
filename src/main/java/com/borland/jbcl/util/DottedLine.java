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
package com.borland.jbcl.util;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.io.Serializable;

/**
 * The DottedLine class draws dotted vertcial and horizontal lines in several styles.  The styles
 * are defined as DottedLine static constants, and can be specified in the drawRect, drawHLine, and
 * drawVLine methods.  A defaultStyle static property exists, so you don't have to specify the line
 * style for each call the above methods.
 * The lines are drawn by blitting sections of the vlines.gif and hlines.gif images found in the
 * com.borland.jbcl.util.image.* package.  These images should not be modified, as the DottedLine logic
 * is based on the specific pixel layout of these images.
 */
public class DottedLine implements Serializable
{
  public static final int STYLE_1DOT_1SPACE     = 0;  // O O O O O O O O O O O O O
  public static final int STYLE_1DOT_2SPACE     = 1;  // O  O  O  O  O  O  O  O  O
  public static final int STYLE_1DOT_3SPACE     = 2;  // O   O   O   O   O   O   O

  public static final int STYLE_2DOT_1SPACE     = 3;  // OO OO OO OO OO OO OO OO OO
  public static final int STYLE_2DOT_2SPACE     = 4;  // OO  OO  OO  OO  OO  OO  OO
  public static final int STYLE_2DOT_3SPACE     = 5;  // OO   OO   OO   OO   OO   OO

  public static final int STYLE_3DOT_1SPACE     = 6;  // OOO OOO OOO OOO OOO OOO OOO
  public static final int STYLE_3DOT_2SPACE     = 7;  // OOO  OOO  OOO  OOO  OOO  OOO
  public static final int STYLE_3DOT_3SPACE     = 8;  // OOO   OOO   OOO   OOO   OOO

  public static final int STYLE_1DOT2DOT_1SPACE = 9;  // O OO O OO O OO O OO O OO O
  public static final int STYLE_1DOT2DOT_2SPACE = 10; // O  OO  O  OO  O  OO  O  OO

  public static final int STYLE_1DOT3DOT_1SPACE = 11; // O OOO O OOO O OOO O OOO O
  public static final int STYLE_1DOT3DOT_2SPACE = 12; // O  OOO  O  OOO  O  OOO  O

  public static final int STYLE_2DOT3DOT_1SPACE = 13; // OO OOO OO OOO OO OOO OO OOO
  public static final int STYLE_2DOT3DOT_2SPACE = 14; // OO  OOO  OO  OOO  OO  OOO

  // these are the repeat pixel lengths of the linetypes in vlines.gif and hlines.gif
  // (identical images 90 degrees rotated)
  static int[] blitSize = new int[] {100, 99, 100, 99, 100, 100, 100, 95, 96, 100, 98, 96, 96, 98, 99};

  public static final int STYLE_FIRST = STYLE_1DOT_1SPACE;
  public static final int STYLE_LAST  = STYLE_2DOT3DOT_2SPACE;

  static int defaultStyle = STYLE_1DOT_1SPACE;

  static void checkStyle(int style) {
    if (style < STYLE_FIRST || style > STYLE_LAST)
      throw new IllegalArgumentException("" + style);
  }

  public static void setDefaultStyle(int style) {
    checkStyle(style);
    defaultStyle = style;
  }

  public static int getDefaultStyle() {
    return defaultStyle;
  }

  public static void drawRect(Graphics g, int x, int y, int width, int height) {
    drawRect(g, x, y, width, height, defaultStyle);
  }

  public static void drawRect(Graphics g, int x, int y, int width, int height, int style) {
    int offset = 1;
    drawLine(g, false, y, x, x + width - 1, style);
    drawLine(g, false, y + height - 1, x, x + width - 1, style);
    drawLine(g, true, x, y + offset, y + height - offset, style);
    drawLine(g, true, x + width - 1, y + offset, y + height - offset, style);
  }

  public static void drawHLine(Graphics g, int x1, int x2, int y) {
    drawHLine(g, x1, x2, y, defaultStyle);
  }

  public static void drawHLine(Graphics g, int x1, int x2, int y, int style) {
    checkStyle(style);
    if (x2 >= x1)
      drawLine(g, false, y, x1, x2, style);
    else
      drawLine(g, false, y, x2, x1, style);
  }

  public static void drawVLine(Graphics g, int x, int y1, int y2) {
    drawVLine(g, x, y1, y2, defaultStyle);
  }

  public static void drawVLine(Graphics g, int x, int y1, int y2, int style) {
    checkStyle(style);
    if (y2 >= y1)
      drawLine(g, true, x, y1, y2, style);
    else
      drawLine(g, true, x, y2, y1, style);
  }

  static int blitcount = 1;
  static void drawLine(Graphics g, boolean vert, int anchor, int low, int high, int style) {
    if (low % 2 != 0)
      low++;
    if (vert && vlines == null || !vert && hlines == null) {
      Component c = new Component() {};
      Image image = ImageLoader.loadFromResource(vert ? "image/vlines.gif" : "image/hlines.gif", c, DottedLine.class); 
//      ImageLoader.waitForImage(c, image);
      if (vert)
        vlines = image;
      else
        hlines = image;
    }

    if (vert && vlines == null)
      return;
    else if (!vert && hlines == null)
      return;

    int length = high - low + 1;
    int painted = 0;
    blitcount = 1;
    while (length > painted) {
      int topaint = Math.min(blitSize[style], length - painted);
      if (vert) {
        g.drawImage(
          vlines, // source image
          anchor, low + painted, anchor + 1, low + painted + topaint - 1, // target coordinates
          style, 0, style + 1, topaint - 1, // source coordinates
          null); // observer
      }
      else {
        g.drawImage(
          hlines, // source image
          low + painted, anchor, low + painted + topaint - 1, anchor + 1, // target coordinates
          0, style, topaint - 1, style + 1, // source coordinates
          null); // observer
      }
      painted += topaint;
    }
  }

  static Image vlines;
  static Image hlines;
}
