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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.text.BreakIterator;
import java.util.Enumeration;
import java.util.Vector;

import com.borland.dx.text.Alignment;

/**
 * This class word-wraps text to fit into a retricted width. The TextWrapper
 * class uses the current locale settings to do language-smart word wrapping.
 */
public class TextWrapper implements java.io.Serializable {
  private static final long serialVersionUID = 200L;
  
  /**
   * Constructs a TextWrapper class using the passed font, text, alignment, and
   * hintWidth.
   * 
   * @param font
   *          The font to use for wrapping
   * @param text
   *          The text to wrap
   * @param alignment
   *          The alignment setting
   * @param hintWidth
   *          The width to use for wrapping
   */
  TextWrapper(Font font, String text, int alignment, int hintWidth) {
    this.alignment = alignment;
    this.font = font;
    fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
    textBoundary = BreakIterator.getWordInstance();
    this.text = text;
    this.hintWidth = hintWidth;
    charHeight = fontMetrics.getHeight();
    maxAscent = fontMetrics.getMaxAscent();
  }
  
  // Properties
  
  /**
   *
   */
  public Dimension getSize(Graphics g) {
    if (size == null)
      calcSize(g);
    return size;
  }
  
  /**
   *
   */
  protected void calcSize(Graphics g) {
    Vector<String> words = new Vector<String>();
    
    textBoundary.setText(text);
    int start = textBoundary.first();
    for (int end = textBoundary.next(); end != BreakIterator.DONE; start = end, end = textBoundary
        .next()) {
      words.addElement(text.substring(start, end));
    }
    
    lines = new String[words.size()];
    length = new int[words.size()];
    lineCount = 0;
    
    int l = 0;
    int curLine = 0;
    int width = 0;
    int height = 0;
    
    for (Enumeration<String> enumerator = words.elements(); enumerator
        .hasMoreElements();) {
      String w = (String) enumerator.nextElement();
      int wl = fontMetrics.stringWidth(w);
      int newLength = length[curLine] + wl;
      boolean isCR = (w.charAt(0) == '\r');
      boolean isLF = (w.charAt(0) == '\n');
      
      if (isCR)
        continue;
      if (!isLF && newLength <= hintWidth) {
        if (lines[curLine] == null) {
          lines[curLine] = w;
          length[curLine] = wl;
          l = 0;
          lineCount++;
          height += charHeight;
          // Diagnostic.println("charHeight:  "+height);
          if (newLength > width)
            width = newLength;
        } else {
          lines[curLine] = lines[curLine] + w;
          length[curLine] = newLength;
          if (newLength > width)
            width = newLength;
        }
      } else {
        if (isLF) {
          wl = 0;
          w = w.substring(1);
        }
        if (wl > width)
          width = wl;
        if (length[curLine] == 0) {
          // First line and it's already too long! As there is no way to have it
          // fit,
          // just stick it like that
          lines[curLine] = w;
          length[curLine] = wl;
          lineCount++;
          curLine++;
        } else {
          // Reject to the next line
          lineCount++;
          curLine++;
          lines[curLine] = w;
          length[curLine] = wl;
        }
        l = 0;
        height += charHeight;
        // Diagnostic.println("charHeight2:  "+height);
      }
    }
    size = new Dimension(width, height);
  }
  
  /**
   *
   */
  public void paint(Graphics g, int x, int y, int maxWidth, int maxHeight) {
    int stop = y + maxHeight + charHeight;
    int hAlign = alignment & Alignment.HORIZONTAL;
    int vAlign = alignment & Alignment.VERTICAL;
    y -= charHeight - maxAscent;
    switch (vAlign) {
    case Alignment.MIDDLE:
      y += (maxHeight - (lineCount * charHeight)) / 2;
      break;
    case Alignment.BOTTOM:
      y += maxHeight - (lineCount * charHeight);
    }
    for (int i = 0; i < lineCount; i++) {
      int dx = 0;
      switch (hAlign) {
      case Alignment.CENTER:
        dx = (maxWidth - length[i]) / 2;
        break;
      case Alignment.RIGHT:
        dx = maxWidth - length[i];
      }
      if (lines[i] != null)
        g.drawString(lines[i], x + dx, y + charHeight);
      y += charHeight;
      if (y > stop)
        return;
    }
  }
  
  int alignment;
  FontMetrics fontMetrics;
  BreakIterator textBoundary;
  Font font;
  String[] lines;
  int[] length;
  String text;
  int hintWidth;
  int lineCount;
  int charHeight;
  int maxAscent;
  Dimension size;
}
