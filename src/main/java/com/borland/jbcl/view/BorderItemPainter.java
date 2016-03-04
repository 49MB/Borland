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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.borland.jbcl.model.ItemPaintSite;
import com.borland.jbcl.model.ItemPainter;

//
// Puts a specific border state around another item painter
//
public class BorderItemPainter implements ItemPainter, Serializable
{
  private static final long serialVersionUID = 200L;

  // Enumeration describing the type of edge(s) to be drawn, used by 'edge' parameter
  //
  public static final int OUTER_RAISED = 0x0100;   // Raised outer edge only
  public static final int OUTER_SUNKEN = 0x0200;   // Sunken outer edge only
  public static final int INNER_RAISED = 0x0400;   // Raised inner edge only
  public static final int INNER_SUNKEN = 0x0800;   // Sunken inner edge only
  public static final int EDGE_RAISED  = OUTER_RAISED | INNER_RAISED;   // Both inner & outer raised
  public static final int EDGE_SUNKEN  = OUTER_SUNKEN | INNER_SUNKEN;   // Both inner & outer sunken
  public static final int EDGE_ETCHED  = OUTER_SUNKEN | INNER_RAISED;   // Outer sunken, inner raised
  public static final int EDGE_BUMP    = OUTER_RAISED | INNER_SUNKEN;   // Outer raised, inner sunken
  public static final int EDGE_OUTER   = 0x0300;   // Mask for outer edge bits
  public static final int EDGE_INNER   = 0x0C00;   // Mask for inner edge bits
  public static final int EDGE_ALL     = 0x0F00;   // Mask for all edge bits

  // Enumeration describing high level border styles which define prepackaged edges & flags
  //
  public static final int NONE         =  0;  //  No border painted at all
  public static final int PLAIN        =  1;  //  PLAIN plain window frame
  public static final int RAISED       =  2;  //  Status field style raised
  public static final int RECESSED     =  3;  //  Status field style recessed
  public static final int EMBOSSED     =  4;  //  Grouping raised emboss bead
  public static final int GROOVED      =  5;  //  Grouping groove
  public static final int BUTTON_UP    =  6;  //  Button in up position
  public static final int BUTTON_DN    =  7;  //  Button in down position
  public static final int WND_RAISED   =  8;  //  Raised window outer+inner edge
  public static final int WND_RECESSED =  9;  //  Input field & other window recessed
  public static final int WELL_SET     = 10;  //  Well option set (auto grows + 1)

  // Which edge(s) to draw. ctor defaults to all 4
  //
  public static final int LEFT         = 0x01;
  public static final int TOP          = 0x02;
  public static final int RIGHT        = 0x04;
  public static final int BOTTOM       = 0x08;
  public static final int TOP_LEFT     = TOP | LEFT;
  public static final int TOP_RIGHT    = TOP | RIGHT;
  public static final int BOTTOM_LEFT  = BOTTOM | LEFT;
  public static final int BOTTOM_RIGHT = BOTTOM | RIGHT;
  public static final int RECT         = TOP | LEFT | BOTTOM | RIGHT;

  // Flags to tweak the look
  //
  public static final int FILL = 0x0800;  // Fill in middle
  public static final int SOFT = 0x1000;  // Soft edge look for buttons
  public static final int FLAT = 0x4000;  // Flat instead of 3d for use in non-3d windows
  public static final int MONO = 0x8000;  // Monochrome

  // edge & flag lookup table for styles
  private static int[] edges = {
    0,                           // NONE
    0,                           // PLAIN
    INNER_RAISED,                // RAISED
    INNER_SUNKEN,                // RECESSED
    OUTER_RAISED | INNER_SUNKEN, // EMBOSSED
    OUTER_SUNKEN | INNER_RAISED, // GROOVED
    OUTER_RAISED | INNER_RAISED, // BUTTON_UP
    OUTER_SUNKEN | INNER_SUNKEN, // BUTTON_DN
    OUTER_RAISED | INNER_RAISED, // WND_RAISED
    OUTER_SUNKEN | INNER_SUNKEN, // WND_RECESSED
    OUTER_SUNKEN | INNER_RAISED, // WELL_SET ???
  };
  private static int[] flagses = {
    0,    // NONE
    0,    // PLAIN
    0,    // RAISED
    0,    // RECESSED
    0,    // EMBOSSED
    0,    // GROOVED
    SOFT, // BUTTON_UP
    SOFT, // BUTTON_DN
    0,    // WND_RAISED
    0,    // WND_RECESSED
    0,    // WELL_SET ???
  };

  /**
   * Construct a Border object given:
   *   - a hi-level style type, calculating edge and modifier flags internally as needed.
   *     or specific edge style flags
   *   - which of the 4 edges to draw
   *   - any flags to add in
   */
  public BorderItemPainter(int edgeOrStyle, int which, int flags)
  {
    //Diagnostic.precondition(style >= 0 && style <= WELL_SET);

    if ((edgeOrStyle & EDGE_ALL) == 0) {
      edge = edges[edgeOrStyle];
      this.flags = flagses[edgeOrStyle] | flags;
    }
    else {
      edge = edgeOrStyle;
      this.flags = flags;
    }
    this.which = which;
    //Diagnostic.println("BorderItemPainter edge:" + Integer.toString(edge, 16) +
    //                                   " which:" + Integer.toString(which, 16) +
    //                                   " flags:" + Integer.toString(flags, 16));
  }

  public BorderItemPainter(int edgeOrStyle, int which) {
    this(edgeOrStyle, which, 0);
  }

  public BorderItemPainter(int edgeOrStyle) {
    this(edgeOrStyle, RECT, 0);
  }

  public BorderItemPainter() {
    this(NONE, RECT, 0);
  }

  public BorderItemPainter(ItemPainter painter, int edgeOrStyle, int which, int flags) {
    this(edgeOrStyle, which, flags);
    this.painter = painter;
  }

  public BorderItemPainter(ItemPainter painter, int edgeOrStyle, int which) {
    this(edgeOrStyle, which);
    this.painter = painter;
  }

  public BorderItemPainter(ItemPainter painter, int edgeOrStyle) {
    this(edgeOrStyle);
    this.painter = painter;
  }

  public BorderItemPainter(ItemPainter painter) {
    this();
    this.painter = painter;
  }

  public int getEdgeOrStyle() { return edge; }
  public void setEdgeOrStyle(int newEdge) {
    if ((newEdge & EDGE_ALL) == 0) {
      edge = edges[newEdge];
      flags = flagses[newEdge] | flags;
    }
    else
      edge = newEdge;
  }

  public int getWhichEdges() { return which; }
  public void setWhichEdges(int newWhich) { which = newWhich; }

  public int getFlags() { return flags; }
  public void setFlags(int newFlags) { flags = newFlags; }

  public ItemPainter getPainter() { return painter; }
  public void setPainter(ItemPainter newPainter) { painter = newPainter; }

  public Insets getInsets() {
    int thickness = ((edge & EDGE_OUTER)!=0 ? 1 : 0) + ((edge & EDGE_INNER)!=0 ? 1 : 0);
    return new Insets(thickness * ((which & TOP)!=0 ? 1 : 0),
                      thickness * ((which & LEFT)!=0 ? 1 : 0),
                      thickness * ((which & BOTTOM)!=0 ? 1 : 0),
                      thickness * ((which & RIGHT)!=0 ? 1 : 0));
  }

  // ItemPainter interface

  /**
   * Calculate the dimensions of the border itself
   */
  public Dimension getPreferredSize(Object data, Graphics g, int state, ItemPaintSite site) {
    Insets insets = getInsets();
    if (painter != null) {
      Dimension inner = painter.getPreferredSize(data, g, state, site);
      return new Dimension(inner.width + insets.left + insets.right,
                           inner.height + insets.top + insets.bottom);
    }
    return new Dimension(insets.left + insets.right, insets.top + insets.bottom);
  }

  /**
   * Paint this Border object onto a given graphics
   */
  public void paint(Object data, Graphics g, Rectangle bounds, int state, ItemPaintSite site) {
    Rectangle clip = g.getClipBounds();
    if (clip == null)
      return;
    g.clipRect(bounds.x, bounds.y, bounds.width, bounds.height);
    boolean trans = site != null ? site.isTransparent() : false;
    if (trans)
      flags &= ~FILL;
    drawEdge(g, bounds, edge, which, flags);
    if (painter != null) {
      Insets insets = getInsets();
      int thickness = ((edge & EDGE_OUTER)!=0 ? 1 : 0) + ((edge & EDGE_INNER)!=0 ? 1 : 0);
      bounds.x += insets.left;
      bounds.width -= insets.left + insets.right;
      bounds.y += insets.top;
      bounds.height -= insets.top + insets.bottom;
      g.setClip(bounds.x, bounds.y, bounds.width, bounds.height);
      painter.paint(data, g, bounds, state, site);
    }
    g.setClip(clip.x, clip.y, clip.width, clip.height);
  }

  /**
   * Static function that performs the actual drawing of edges for a Border,
   * or an external client. Uses the drawEdge if available.
   */
  public static boolean drawEdge(Graphics g, Rectangle bounds, int edge, int which, int flags) {
    Rectangle b = new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);  // working bounds rectangle

    // If mono is set, draw a thin, flat, black (windowFrame) frame
    //
    if ((flags & MONO) != 0) {
      if ((edge & EDGE_OUTER) != 0) {
        paintFrame(g, b, which, SystemColor.windowBorder, SystemColor.windowBorder);
        b.grow(-1,-1);
      }
      if ((flags & FILL) != 0) {
        g.setColor(SystemColor.window);
        g.drawRect(b.x, b.y, b.width, b.height);
      }
      return true;
    }

    // If flat is set, draw a thin, flat, shadow frame
    //
    if ((flags & FLAT) != 0) {
      if ((edge & EDGE_OUTER) != 0) {
        paintFrame(g, b, which, SystemColor.controlShadow, SystemColor.controlShadow);
        b.grow(-1,-1);
      }
      if ((flags & FILL) != 0) {
        g.setColor(SystemColor.control);
        g.drawRect(b.x, b.y, b.width, b.height);
      }
      return true;
    }

    // Draw outer edge if indicated, adjusting rect afterwards
    //
    if ((edge & EDGE_OUTER) != 0) {
      Color[] tlColors = {
        SystemColor.controlHighlight,   // OUTER_RAISED
        SystemColor.controlLtHighlight, // OUTER_RAISED + SOFT
        SystemColor.controlShadow,      // OUTER_SUNKEN
        SystemColor.controlDkShadow,    // OUTER_SUNKEN + SOFT
      };
      Color[] brColors = {
        SystemColor.controlDkShadow,    // OUTER_RAISED
        SystemColor.controlDkShadow,    // OUTER_RAISED + SOFT
        SystemColor.controlLtHighlight, // OUTER_SUNKEN
        SystemColor.controlLtHighlight, // OUTER_SUNKEN + SOFT
      };
      int ci = ((edge & OUTER_SUNKEN)!=0 ? 2 : 0) | ((flags & SOFT)!=0 ? 1 : 0);
      paintFrame(g, b, which, tlColors[ci], brColors[ci]);
      b.grow(-1,-1);
    }

    // Draw inner edge if indicated, adjusting rect afterwards
    //
    if ((edge & EDGE_INNER) != 0) {
      Color[] tlColors = {
        SystemColor.controlLtHighlight, // INNER_RAISED
        SystemColor.controlHighlight,   // INNER_RAISED + SOFT
        SystemColor.controlDkShadow,    // INNER_SUNKEN
        SystemColor.controlShadow,      // INNER_SUNKEN + SOFT
      };
      Color[] brColors = {
        SystemColor.controlShadow,      // INNER_RAISED
        SystemColor.controlShadow,      // INNER_RAISED + SOFT
        SystemColor.controlHighlight,   // INNER_SUNKEN
        SystemColor.controlHighlight,   // INNER_SUNKEN + SOFT
      };
      int ci = ((edge & INNER_SUNKEN)!=0 ? 2 : 0) | ((flags & SOFT)!=0 ? 1 : 0);
      paintFrame(g, b, which, tlColors[ci], brColors[ci]);
      b.grow(-1,-1);
    }

    // Fill interior if indicated
    //
    if ((flags & FILL) != 0) {
      g.setColor(SystemColor.control);
      g.fillRect(b.x, b.y, b.width, b.height);
    }

    return true;
  }

  /**
   * Paint a 2-color single pixel thick frame, bevel corners get br color
   */
  static void paintFrame(Graphics g, Rectangle b, int which, Color tlColor, Color brColor)
  {
    if ((which & (LEFT | TOP)) != 0) {
      g.setColor(tlColor);
      if ((which & LEFT) != 0)
        g.drawLine(b.x, b.y + 1, b.x, b.y + b.height - 1);
      if ((which & TOP) != 0)
        g.drawLine(b.x, b.y, b.x + b.width - 1, b.y);
    }

    if ((which & (RIGHT | BOTTOM)) != 0) {
      g.setColor(brColor);
      if ((which & RIGHT) != 0)
        g.drawLine(b.x + b.width - 1, b.y, b.x + b.width - 1, b.y + b.height - 1);
      if ((which & BOTTOM) != 0)
        g.drawLine(b.x, b.y + b.height - 1, b.x + b.width - 1, b.y + b.height - 1);
    }
  }

  /**
   * Paint a 2-color single pixel thick bame, bevel corners get their own color
   */
  static void paintFrameC(Graphics g, Rectangle b, int which, Color tlColor, Color brColor, Color bcColor)
  {
    if ((which & (LEFT | TOP)) != 0) {
      g.setColor(tlColor);
      if ((which & LEFT) != 0)
        g.drawLine(b.x, b.y + 1, 1, b.y + b.height - 1);
      if ((which & TOP) != 0) {
        g.drawLine(b.x, b.y, b.x + b.width - 2, b.y);
        g.setColor(bcColor);
        g.drawLine(b.x + b.width - 1, b.y, b.x + b.width - 1, b.y);
      }
    }

    if ((which & (RIGHT | BOTTOM)) != 0) {
      g.setColor(brColor);
      if ((which & RIGHT) != 0)
        g.drawLine(b.x + b.width - 1, b.y, b.x + b.width - 1, b.y + b.height - 1);
      if ((which & BOTTOM) != 0) {
        g.drawLine(b.x + 1, b.y + b.height - 1, b.width - 2, b.y + b.height - 1);
        g.setColor(bcColor);
        g.drawLine(b.x, b.y + b.height - 1, b.x, b.y + b.height - 1);
      }
    }
  }

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    s.writeObject(painter instanceof Serializable ? painter : null);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    Object data = s.readObject();
    if (data instanceof ItemPainter)
      painter = (ItemPainter)data;
  }

  private int edge;
  private int which;
  private int flags;
  private transient ItemPainter painter;
}
