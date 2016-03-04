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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.borland.dx.dataset.CustomPaintSite;
import com.borland.jbcl.model.ItemEditSite;
import com.borland.jbcl.model.ItemEditor;
import com.borland.jbcl.model.ToggleItemEditor;

public class CustomItemEditor
  implements ToggleItemEditor, ItemEditSite, CustomPaintSite, Serializable
{
  private static final long serialVersionUID = 200L;

  public CustomItemEditor() {}
  public CustomItemEditor(ItemEditor editor) {
    this.editor = editor;
  }

  /**
   * Nested ItemEditor chain.
   */
  public void setEditor(ItemEditor editor) {
    this.editor = editor;
  }
  public ItemEditor getEditor() {
    return editor;
  }

  // ItemEditor Implementation

  public Object getValue() {
    return editor != null ? editor.getValue() : null;
  }

  public Component getComponent() {
    return editor != null ? editor.getComponent() : null;
  }

  public void startEdit(Object data, Rectangle rect, ItemEditSite editSite) {
    this.editSite = editSite;
    if (editor != null)
      editor.startEdit(data, rect, this);
  }

  public void changeBounds(Rectangle rect) {
    if (editor != null)
      editor.changeBounds(rect);
  }

  public boolean canPost() {
    return editor != null ? editor.canPost() : false;
  }

  public void endEdit(boolean posted) {
    if (editor != null)
      editor.endEdit(posted);
  }

  public boolean isToggle(Object data, Rectangle rect, ItemEditSite site) {
    if (editor instanceof ToggleItemEditor)
      return ((ToggleItemEditor)editor).isToggle(data, rect, site);
    return false;
  }

  public void addKeyListener(KeyListener l) {
    if (editor != null)
      editor.addKeyListener(l);
  }

  public void removeKeyListener(KeyListener l) {
    if (editor != null)
      editor.removeKeyListener(l);
  }

  // CustomPaintSite Implementation

  public void reset() {
    background = null;
    foreground = null;
    font = null;
    alignment = 0;
    margins = null;
  }

  public void setBackground(Color color) {
    this.background = color;
  }
  public Color getBackground() {
    if (background != null)
      return background;
    return editSite != null ? editSite.getBackground() : null;
  }

  public void setForeground(Color color) {
    this.foreground = color;
  }
  public Color getForeground() {
    if (foreground != null)
      return foreground;
    return editSite != null ? editSite.getForeground() : null;
  }

  // no override
  public boolean isTransparent() {
    return editSite != null ? editSite.isTransparent() : false;
  }

  public void setFont(Font font) {
    this.font = font;
  }
  public Font getFont() {
    if (font != null)
      return font;
    return editSite != null ? editSite.getFont() : null;
  }

  public void setAlignment(int alignment) {
    this.alignment = alignment;
  }
  public int getAlignment() {
    if (alignment != 0)
      return alignment;
    return editSite != null ? editSite.getAlignment() : 0;
  }

  public void setItemMargins(Insets margins) {
    this.margins = margins;
  }
  public Insets getItemMargins() {
    if (margins != null)
      return margins;
    return editSite != null ? editSite.getItemMargins() : null;
  }

  // no override
  public Component getSiteComponent() {
    return editSite != null ? editSite.getSiteComponent() : null;
  }

  // ItemEditSite Implementation

  public void safeEndEdit(boolean post) {
    if (editSite != null)
      editSite.safeEndEdit(post);
  }

  public Point getEditClickPoint() {
    return editSite != null ? editSite.getEditClickPoint() : null;
  }

  public Graphics getSiteGraphics() {
    return editSite != null ? editSite.getSiteGraphics() : null;
  }

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    s.writeObject(editor instanceof Serializable ? editor : null);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    Object data = s.readObject();
    if (data instanceof ItemEditor)
      editor = (ItemEditor)data;
  }

  protected transient ItemEditor   editor;
  protected transient ItemEditSite editSite; // ignore for serialization
  protected Color background;
  protected Color foreground;
  protected Font font;
  protected int alignment;
  protected Insets margins;
}
