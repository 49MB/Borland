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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JComponent;

import com.borland.dx.text.Alignment;
import com.borland.jbcl.util.BlackBox;

public class TextControl extends JComponent implements java.io.Serializable , BlackBox
{
  public TextControl() {
    setSize(150, 20);
  }

  public TextControl(String text) {
    this.text = text;
    setSize(150, 20);
  }

  public void setText(String text) {
    this.text = text;
    repaint(100);
  }
  public String getText() {
    return text;
  }

  public void setAlignment(int align) { alignment = align; }
  public int getAlignment() { return alignment; }

  public void setMargins(Insets margins) {
    this.margins = margins;
  }
  public Insets getMargins() {
    return margins;
  }

  public void setTransparent(boolean transparent) {
    this.transparent = transparent;
    repaint(100);
  }
  public boolean isTransparent() {
    return transparent;
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

  public Dimension getPreferredSize() {
    Graphics g = getGraphics();
    if (g == null || text == null || text.equals(""))
      return new Dimension(150, 20);
    Font f = getFont();
    FontMetrics fm = g.getFontMetrics(f);
    Insets m = margins != null ? margins : new Insets(0,0,0,0);
    return new Dimension(m.left + m.right + fm.stringWidth(text),
                         m.top + m.bottom + fm.getHeight());
  }

  public void update(Graphics g) {
    boolean t = transparent;
    transparent = false;
    paint(g);
    transparent = t;
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Dimension size = getSize();
    g.clipRect(0, 0, size.width, size.height);
    Color oc = g.getColor();
    Font of = g.getFont();
    Insets m = margins != null ? margins : new Insets(0,0,0,0);

    if (!transparent) {
      g.setColor(getBackground());
      g.fillRect(0, 0, size.width, size.height);
    }

    if (text != null && !text.equals("")) {
      Font f = getFont();
      g.setFont(f);
      FontMetrics fm = g.getFontMetrics(f);

      int xOffset;
      switch (alignment & Alignment.HORIZONTAL) {
        default:
        case Alignment.LEFT:
          xOffset = m.left;
          break;
        case Alignment.CENTER:
          xOffset = (size.width - fm.stringWidth(text)) / 2;
          break;
        case Alignment.RIGHT:
          xOffset = size.width - fm.stringWidth(text) - m.right;
      }

      int yOffset;
      switch (alignment & Alignment.VERTICAL) {
        default:
        case Alignment.TOP:
          yOffset = m.top;
          break;
        case Alignment.MIDDLE:
          yOffset = (size.height - fm.getHeight()) / 2;
          break;
        case Alignment.BOTTOM:
          yOffset = size.height - m.bottom - fm.getHeight();
          break;
      }
      yOffset += fm.getLeading() + fm.getAscent();

      g.setColor(getForeground());
      g.drawString(text, xOffset, yOffset);
    }

    // Draw the border
    if (drawEdge) {
      g.setColor(edgeColor);
      g.drawRect(0, 0, size.width - 1, size.height - 1);
    }

    g.setFont(of);
    g.setColor(oc);
  }

  protected String  text;
  protected Color   edgeColor = Color.black;
  protected boolean drawEdge = false;
  protected boolean transparent = true;
  protected int     alignment = Alignment.LEFT | Alignment.MIDDLE;
  protected Insets  margins;
}
