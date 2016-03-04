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

import java.awt.AWTEvent;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

import com.borland.jbcl.model.ItemEditSite;
import com.borland.jbcl.model.ItemEditor;

/**
 * A Choice item editor
 */
public class ChoiceItemEditor
     extends Choice
  implements ItemEditor, KeyListener, java.io.Serializable
{
  public ChoiceItemEditor() {
    super();
    setBounds(0,0,0,0);
    setVisible(false);
    addKeyListener(this);
    enableEvents(AWTEvent.MOUSE_EVENT_MASK);
  }

  public ChoiceItemEditor(String[] items) {
    this();
    for (int i = 0; i < items.length; i++)
      add(items[i]);
  }

  public Object getValue() {
    return getSelectedItem();
  }

  public Component getComponent() { return this; }

  public void startEdit(Object data, Rectangle bounds, ItemEditSite site) {
    if (data != null)
      select(data.toString());
    changeBounds(bounds);
    if (site != null) {
      setBackground(site.getBackground());
      setForeground(site.getForeground());
      setFont(site.getFont());
    }
    setVisible(true);
    requestFocus();
  }

  public void changeBounds(Rectangle bounds) {
    int height = 0;
    Font f = getFont();
    Graphics g = getGraphics();
    FontMetrics fm = f != null && g != null ? g.getFontMetrics(f) : null;
    if (fm != null)
      height = fm.getHeight() * (getItemCount() < 6 ? getItemCount() : 6);
    setBounds(bounds.x, bounds.y, bounds.width, bounds.height < height ? bounds.height : height);
  }

  public boolean canPost() {
    return true;
  }

  public void endEdit(boolean post) {
    setBounds(0,0,0,0);
    setVisible(false);
  }

  // KeyListener Implementation

  public void keyPressed(KeyEvent e) {
    int code = e.getKeyCode();
    int sel = getSelectedIndex();
    switch (code) {
      case KeyEvent.VK_DOWN:
        if (e.isAltDown())
          return;
        if (sel < (getItemCount() - 1))
          select(sel + 1);
        e.consume();
        break;
      case KeyEvent.VK_UP:
        if (sel > 0)
          select(sel - 1);
        e.consume();
        break;
    }
  }
  public void keyReleased(KeyEvent e) {}
  public void keyTyped(KeyEvent e) {
    char c = e.getKeyChar();
    char s = getSelectedItem().toLowerCase().charAt(0);
    int[] matches = new int[0];
    if (c != KeyEvent.CHAR_UNDEFINED) {
      // scan for all matching values
      for (int i = 0; i < getItemCount(); i++) {
        String value = getItem(i).toLowerCase();
        if (value.length() > 0 && value.charAt(0) == c) {
          // this has a different first char than currently selected, so select it
          if (s != c) {
            select(i);
            return;
          }
          int[] old = matches;
          matches = new int[old.length + 1];
          System.arraycopy(old, 0, matches, 0, old.length);
          matches[matches.length - 1] = i;
        }
      }
      // place selection accordingly within matches (we know that s==c)
      if (matches.length > 0) {
        int sel = getSelectedIndex();
        for (int i = 0; i < matches.length; i++) {
          // if we are past the current value, select it
          if (matches[i] > sel) {
            select(matches[i]);
            return;
          }
          // if we are at the current value, and it's the end, cycle back to the first one
          if (i == matches.length - 1 && matches[i] == sel) {
            select(matches[0]);
            return;
          }
        }
      }
    }
  }

  protected void processMouseEvent(MouseEvent e) {
    if (e.getID() == MouseEvent.MOUSE_PRESSED) {
      if (e.getX() < getSize().width - getSize().height) {
        if (e.getClickCount() % 2 == 0) {
          int sel = getSelectedIndex();
          int last = getItemCount() - 1;
          if (sel < last)
            select(sel + 1);
          else if (sel == last)
            select(0);
        }
        e.consume();
      }
    }
    super.processMouseEvent(e);
  }
}
