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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

import com.borland.jbcl.util.BlackBox;

public class ShapeControl extends JComponent implements java.io.Serializable  , BlackBox
{
  public final static int RECTANGLE      = 0;
  public final static int ROUND_RECT     = 1;
  public final static int SQUARE         = 2;
  public final static int ROUND_SQUARE   = 3;
  public final static int ELLIPSE        = 4;
  public final static int CIRCLE         = 5;
  public final static int HORZ_LINE      = 6;
  public final static int VERT_LINE      = 7;
  public final static int POS_SLOPE_LINE = 8;
  public final static int NEG_SLOPE_LINE = 9;

  public ShapeControl() {
    setSize(100, 100);
    super.setForeground(Color.white);
  }

  public ShapeControl(int type) {
    this();
    setType(type);
  }

  public void setForeground(Color color) {
    super.setForeground(color);
    repaint(100);
  }

  public void setType(int type) {
    if (this.type != type) {
      this.type = type;
      repaint(100);
    }
  }

  public int getType() {
    return type;
  }

  public void setFill(boolean fill) {
    this.fill = fill;
    repaint(100);
  }

  public boolean isFill() {
    return fill;
  }

  public void setEdgeColor(Color edgeColor) {
    this.edgeColor = edgeColor;
    repaint(100);
  }

  public Color getEdgeColor() {
    return edgeColor;
  }

  public void setDrawEdge(boolean drawEdge) {
    this.drawEdge = drawEdge;
    repaint(100);
  }

  public boolean isDrawEdge() {
    return drawEdge;
  }

  public void paintComponent(Graphics g) {
    // Allow rest of world to paint (fill in background region)
    super.paintComponent(g);

    // Compute the desired position and size
    int x = 0;
    int y = 0;
    int w = getSize().width;
    int h = getSize().height;
    int s = w < h ? w : h;

    // If it's an equal-sided type, then equalize and center it
    if (type == SQUARE || type == ROUND_SQUARE || type == CIRCLE) {
      x += ((w - s) / 2);
      y += ((h - s) / 2);
      w = s;
      h = s;
    }

    // Paint the shape
    switch (type) {
      case RECTANGLE:
      case SQUARE:
        if (fill) {
          g.setColor(getForeground());
          g.fillRect(x, y, w, h);
        }
        if (drawEdge) {
          g.setColor(edgeColor);
          g.drawRect(x, y, w - 1, h - 1);
        }
        break;
      case ROUND_RECT:
      case ROUND_SQUARE:
        if (fill) {
          g.setColor(getForeground());
          g.fillRoundRect(x, y, w, h, s / 4, s / 4);
        }
        if (drawEdge) {
          g.setColor(edgeColor);
          g.drawRoundRect(x, y, w - 1, h - 1, s / 4, s / 4);
        }
        break;
      case CIRCLE:
      case ELLIPSE:
        if (fill) {
          g.setColor(getForeground());
          g.fillOval(x, y, w, h);
        }
        if (drawEdge) {
          g.setColor(edgeColor);
          g.drawOval(x, y, w - 1, h - 1);
        }
        break;
      case HORZ_LINE:
        g.setColor(getForeground());
        g.drawLine(x, y, x + w, y);
        if (drawEdge) {
          g.setColor(edgeColor);
          g.drawLine(x, y + 1, x + w, y + 1);
        }
        break;
      case VERT_LINE:
        g.setColor(getForeground());
        g.drawLine(x, y, x, y + h);
        if (drawEdge) {
          g.setColor(edgeColor);
          g.drawLine(x + 1, y, x + 1, y + h);
        }
        break;
      case POS_SLOPE_LINE:
        if (drawEdge) {
          g.setColor(edgeColor);
          g.drawLine(x, y + 1 + h, x + w, y + 1);
        }
        g.setColor(getForeground());
        g.drawLine(x, y + h, x + w, y);
        break;
      case NEG_SLOPE_LINE:
        if (drawEdge) {
          g.setColor(edgeColor);
          g.drawLine(x, y + 1, x + w, y + h + 1);
        }
        g.setColor(getForeground());
        g.drawLine(x, y, x + w, y + h);
        break;
    }
  }

  public Dimension getPreferredSize() {
    return new Dimension(100, 100);
  }
  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  private int type = RECTANGLE;
  private Color edgeColor = Color.black;
  private boolean drawEdge = true;
  private boolean fill = true;
}
