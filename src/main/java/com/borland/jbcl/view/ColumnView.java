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
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;

import com.borland.jbcl.model.ItemEditSite;
import com.borland.jbcl.model.ItemEditor;
import com.borland.jbcl.model.ItemPainter;

/**
 * ColumnView is the data display information 'persister'.  It stores
 * all the of data related property values.
 */
public class ColumnView implements Cloneable, ItemEditSite, Serializable
{
  private static final long serialVersionUID = 200L;

  public static final int PROP_NAME        = 0;
  public static final int PROP_ORDINAL     = 1;
  public static final int PROP_FONT        = 2;
  public static final int PROP_ALIGNMENT   = 3;
  public static final int PROP_BACKGROUND  = 4;
  public static final int PROP_FOREGROUND  = 5;
  public static final int PROP_CAPTION     = 6;
  public static final int PROP_WIDTH       = 7;
  public static final int PROP_MARGINS     = 8;
  public static final int PROP_ITEMPAINTER = 9;
  public static final int PROP_ITEMEDITOR  = 10;

  public ColumnView() {}
  public ColumnView(int ordinal) {
    this.ordinal = ordinal;
  }

  public ColumnView(ColumnView clonee) {
    name        = clonee.getName();
    ordinal     = clonee.getOrdinal();
    font        = clonee.getFont();
    alignment   = clonee.getAlignment();
    background  = clonee.getBackground();
    foreground  = clonee.getForeground();
    caption     = clonee.getCaption();
    width       = clonee.getWidth();
    margins     = clonee.getItemMargins();
    itemPainter = clonee.getItemPainter();
    itemEditor  = clonee.getItemEditor();

    userSetName        = clonee.userSetName;
    userSetOrdinal     = clonee.userSetOrdinal;
    userSetFont        = clonee.userSetFont;
    userSetAlignment   = clonee.userSetAlignment;
    userSetBackground  = clonee.userSetBackground;
    userSetForeground  = clonee.userSetForeground;
    userSetCaption     = clonee.userSetCaption;
    userSetWidth       = clonee.userSetWidth;
    userSetMargins     = clonee.userSetMargins;
    userSetItemPainter = clonee.userSetItemPainter;
    userSetItemEditor  = clonee.userSetItemEditor;

    core = clonee.getGridCore();
  }

  public Object clone() {
    ColumnView clone = new ColumnView(this);
    return clone;
  }

  /**
   * Column Name
   */
  public String getName() { return name; }
  public void setName(String newName) {
    if (name == newName || name != null && name.equals(newName))
      return;
    name = newName;
    userSetName = true;
    if (core != null)
      core.columnViewChanged(this, PROP_NAME);
  }

  /**
   * Column Ordinal.  This is the column index in the MatrixModel for
   * this column.  An ordinal less than 0 means use the index of the
   * columnView array to determine the MatrixModel column.
   */
  public int getOrdinal() { return ordinal; }
  public void setOrdinal(int newOrdinal) {
    if (ordinal != newOrdinal) {
      ordinal = newOrdinal;
      userSetOrdinal = true;
      if (core != null)
        core.columnViewChanged(this, PROP_ORDINAL);
    }
  }

  /**
   * Display font
   * @See java.awt.Font for Font information.
   */
  public Font getFont() { return font; }
  public void setFont(Font newFont) {
    if (font == newFont || font != null && font.equals(newFont))
      return;
    font = newFont;
    userSetFont = true;
    if (core != null)
      core.columnViewChanged(this, PROP_FONT);
  }

  /**
   * Text display alignment
   * @See com.borland.jbcl.util.Alignment for alignment settings.
   */
  public int getAlignment() { return alignment; }
  public void setAlignment(int newAlignment) {
    if (alignment != newAlignment) {
      alignment = newAlignment;
      userSetAlignment = true;
      if (core != null)
        core.columnViewChanged(this, PROP_ALIGNMENT);
    }
  }

  /**
   * Background display color
   * @See java.awt.Color for color settings.
   */
  public Color getBackground() { return background; }
  public void setBackground(Color newColor) {
    if (background == newColor || background != null && background.equals(newColor))
      return;
    background = newColor;
    userSetBackground = true;
    if (core != null)
      core.columnViewChanged(this, PROP_BACKGROUND);
  }

  /**
   * Foreground display color (text color)
   * @See java.awt.Color for color settings.
   */
  public Color getForeground() { return foreground; }
  public void setForeground(Color newColor) {
    if (foreground == newColor || foreground != null && foreground.equals(newColor))
      return;
    foreground = newColor;
    userSetForeground = true;
    if (core != null)
      core.columnViewChanged(this, PROP_FOREGROUND);
  }

  /**
   * Column caption (display label)
   */
  public String getCaption() { return caption; }
  public void setCaption(String newCaption) {
    if (caption == newCaption || caption != null && caption.equals(newCaption))
      return;
    caption = newCaption;
    userSetCaption = true;
    if (core != null)
      core.columnViewChanged(this, PROP_CAPTION);
  }

  /**
   * Column width setting
   */
  public int getWidth() { return width; }
  public void setWidth(int newWidth) {
    if (width != newWidth) {
      width = newWidth;
      userSetWidth = true;
      if (core != null)
        core.columnViewChanged(this, PROP_WIDTH);
    }
  }

  /**
   * Item margins setting
   */
  public Insets getItemMargins() { return margins; }
  public void setItemMargins(Insets newMargins) {
    if (margins == newMargins || margins != null && margins.equals(newMargins))
      return;
    margins = newMargins;
    userSetMargins = true;
    if (core != null)
      core.columnViewChanged(this, PROP_MARGINS);
  }

  /**
   * Custom ItemPainter setting.
   */
  public ItemPainter getItemPainter() { return itemPainter; }
  public void setItemPainter(ItemPainter itemPainter) {
    if (this.itemPainter != itemPainter) {
      this.itemPainter = itemPainter;
      userSetItemPainter = true;
      if (core != null)
        core.columnViewChanged(this, PROP_ITEMPAINTER);
    }
  }

  /**
   * Custom ItemEditor setting.
   */
  public ItemEditor getItemEditor() { return itemEditor; }
  public void setItemEditor(ItemEditor itemEditor) {
    if (this.itemEditor != itemEditor) {
      this.itemEditor = itemEditor;
      userSetItemEditor = true;
      if (core != null)
        core.columnViewChanged(this, PROP_ITEMEDITOR);
    }
  }

  /**
   * This method resets all the user setting flags to false, so that any resolving
   * of property settings will result in the newly set ones.
   */
  public void resetUserFlags() {
    userSetName        = false;
    userSetOrdinal     = false;
    userSetFont        = false;
    userSetAlignment   = false;
    userSetBackground  = false;
    userSetForeground  = false;
    userSetCaption     = false;
    userSetWidth       = false;
    userSetMargins     = false;
    userSetItemPainter = false;
    userSetItemEditor  = false;
  }

  /**
   * ColumnView has a handle to the GridCore using it so that
   * it can notify the core when any state changes are made.
   * (package local property)
   */
  GridCore getGridCore() { return core; }
  void setGridCore(GridCore core) {
    this.core = core;
  }

  // ItemEditSite implementation

  public boolean isTransparent() {
    return core != null ? core.isTransparent() : false;
  }

  public Component getSiteComponent() {
    return core != null ? (Component)core : null;
  }

  public void safeEndEdit(boolean post) {
    if (core != null)
      core.safeEndEdit(post);
  }

  public Point getEditClickPoint() {
    return core != null ? core.getEditClickPoint() : null;
  }

  public Graphics getSiteGraphics() {
    return core != null ? core.getSiteGraphics() : null;
  }

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    Hashtable hash = new Hashtable(2);
    if (itemPainter instanceof Serializable)
      hash.put("p", itemPainter); 
    if (itemEditor instanceof Serializable)
      hash.put("e", itemEditor); 
    s.writeObject(hash);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    Hashtable hash = (Hashtable)s.readObject();
    Object data = hash.get("p"); 
    if (data instanceof ItemPainter)
      itemPainter = (ItemPainter)data;
    data = hash.get("e"); 
    if (data instanceof ItemEditor)
      itemEditor = (ItemEditor)data;
  }

  protected String      name;
  protected int         ordinal = -1;
  protected Font        font;
  protected int         alignment;
  protected Color       background;
  protected Color       foreground;
  protected String      caption;
  protected int         width;
  protected Insets      margins;

  protected transient ItemPainter itemPainter;
  protected transient ItemEditor  itemEditor;

  protected boolean userSetName;
  protected boolean userSetOrdinal;
  protected boolean userSetFont;
  protected boolean userSetAlignment;
  protected boolean userSetBackground;
  protected boolean userSetForeground;
  protected boolean userSetCaption;
  protected boolean userSetWidth;
  protected boolean userSetMargins;
  protected boolean userSetItemPainter;
  protected boolean userSetItemEditor;

  GridCore core;
}
