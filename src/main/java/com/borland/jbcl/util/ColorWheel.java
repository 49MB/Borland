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

import java.awt.Color;

public class ColorWheel implements java.io.Serializable
{
  public static final int RED_TO_YELLOW   = 1;  // 0xFF0000 --> 0xFFFF00 (adding green)
  public static final int YELLOW_TO_GREEN = 2;  // 0xFFFF00 --> 0x00FF00 (subtracting red)
  public static final int GREEN_TO_CYAN   = 3;  // 0x00FF00 --> 0x00FFFF (adding blue)
  public static final int CYAN_TO_BLUE    = 4;  // 0x00FFFF --> 0x0000FF (subtracting green)
  public static final int BLUE_TO_MAGENTA = 5;  // 0x0000FF --> 0xFF00FF (adding red)
  public static final int MAGENTA_TO_RED  = 6;  // 0xFF00FF --> 0xFF0000 (subtracting blue)

  public ColorWheel() {}

  public ColorWheel(Color startColor) {
    current = startColor;
  }

  public ColorWheel(Color startColor, int startCycle) {
    current = startColor;
    cycle = startCycle;
  }

  public ColorWheel(Color startColor, int startCycle, int increment) {
    current = startColor;
    cycle = startCycle;
    this.increment = increment;
  }

  private int increment = 50;
  private int cycle = RED_TO_YELLOW;
  private Color current = Color.red;

  public int getIncrement() { return increment; }
  public void setIncrement(int newIncrement) {
    if (newIncrement >= 1 && newIncrement <= 255)
      increment = newIncrement;
    else
      throw new IllegalArgumentException(Res._NotInRange);     
  }

  public int getCycle() { return cycle; }
  public void setCycle(int newCycle) {
    if (newCycle >= RED_TO_YELLOW && newCycle <= MAGENTA_TO_RED)
      cycle = newCycle;
    else
      throw new IllegalArgumentException(Res._InvalidCycle + newCycle);     
  }

  public Color getColor() { return current; }
  public void setColor(Color newColor) {
    if (newColor != null)
      current = newColor;
  }
  public Color next() { return next(current); }

  public Color next(Color color) {
    int r = color.getRed();
    int g = color.getGreen();
    int b = color.getBlue();
    switch (cycle) {
      case RED_TO_YELLOW:
        g += increment;
        if (g > 255) {
          g = 255;
          cycle = YELLOW_TO_GREEN;
        }
        break;
      case YELLOW_TO_GREEN:
        r -= increment;
        if (r < 0) {
          r = 0;
          cycle = GREEN_TO_CYAN;
        }
        break;
      case GREEN_TO_CYAN:
        b += increment;
        if (b > 255) {
          b = 255;
          cycle = CYAN_TO_BLUE;
        }
        break;
      case CYAN_TO_BLUE:
        g -= increment;
        if (g < 0) {
          g = 0;
          cycle = BLUE_TO_MAGENTA;
        }
        break;
      case BLUE_TO_MAGENTA:
        r += increment;
        if (r > 255) {
          r = 255;
          cycle = MAGENTA_TO_RED;
        }
        break;
      case MAGENTA_TO_RED:
        b -= increment;
        if (b < 0) {
          b = 0;
          cycle = RED_TO_YELLOW;
        }
        break;
      default:
        cycle = RED_TO_YELLOW;
        break;
    }
    current = new Color(r, g, b);
    return current;
  }
}

