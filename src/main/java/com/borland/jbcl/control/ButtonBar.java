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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.MediaTracker;
import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import com.borland.dx.text.Alignment;
import com.borland.jb.util.Diagnostic;
import com.borland.jbcl.util.ActionMulticaster;
import com.borland.jbcl.util.BlackBox;
import com.borland.jbcl.util.ImageLoader;
import com.borland.jbcl.util.MouseMotionMulticaster;
import com.borland.jbcl.util.MouseMulticaster;
import com.borland.jbcl.util.Orientation;
import com.borland.jbcl.util.SerializableImage;
import com.borland.jbcl.view.BorderItemPainter;
import com.borland.jbcl.view.Spacer;

public class ButtonBar extends BevelPanel implements BlackBox, Serializable {
  private static final long serialVersionUID = 200L;
  
  /**
   * buttonType constant that will use text-only buttons.
   */
  public static final int TEXT_ONLY = 1;
  
  /**
   * buttonType constant that will use image-only buttons.
   */
  public static final int IMAGE_ONLY = 2;
  
  /**
   * buttonType constant that will use both text and image buttons.
   */
  public static final int TEXT_AND_IMAGE = 3;
  
  /**
   * Constructs a ButtonBar component with the default settings: -
   * TEXT_AND_IMAGE type buttons - HORIZONTAL orientation
   */
  public ButtonBar() {
    super.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    super.setBackground(SystemColor.control);
    setMargins(new Insets(2, 2, 2, 2));
  }
  
  /**
   * Sets the set of buttons in the ButtonBar
   * 
   * @param labels
   *          The String array representing the buttons to be added (replacing
   *          old) to the ButtonBar. These labels will also distinguish the
   *          added buttons as the ActionCommand in the ActionPreformed method
   *          on the button bar.
   */
  public void setLabels(String[] labels) {
    if (labels == null)
      labels = new String[0];
    String[] s = new String[labels.length];
    System.arraycopy(labels, 0, s, 0, s.length);
    if (labels.length != enabledState.length) {
      boolean[] es = new boolean[labels.length];
      for (int i = 0; i < labels.length; i++)
        es[i] = true;
      enabledState = es;
    }
    if (labels.length != visibleState.length) {
      boolean[] vs = new boolean[labels.length];
      for (int i = 0; i < labels.length; i++)
        vs[i] = true;
      visibleState = vs;
    }
    this.labels = s;
    invalidateButtons();
  }
  
  /**
   * Returns an array of Strings, which represent the buttons in the ButtonBar
   */
  public String[] getLabels() {
    String[] s = new String[labels.length];
    System.arraycopy(labels, 0, s, 0, s.length);
    return s;
  }
  
  /**
   * Sets the base location for finding the images in the imageNames property.
   * 
   * @param imageBase
   *          The base image location string. Can be a url, relative path or
   *          absolute path prefix
   */
  public void setImageBase(String ib) {
    imageBase = ib;
    if (buttonType == IMAGE_ONLY || buttonType == TEXT_AND_IMAGE) {
      invalidateImages();
      invalidateButtons();
    }
  }
  
  /**
   * Returns the base location for finding the images.
   */
  public String getImageBase() {
    return imageBase;
  }
  
  /**
   * Sets the Names for the images to be associated with the buttons on the
   * ButtonBar.
   * 
   * @param imageNames
   *          The String array of Names for the images on the ButtonBar.
   */
  public void setImageNames(String[] imageNames) {
    if (imageNames != null) {
      String[] s = new String[imageNames.length];
      System.arraycopy(imageNames, 0, s, 0, s.length);
      this.imageNames = s;
      if (buttonType == IMAGE_ONLY || buttonType == TEXT_AND_IMAGE) {
        invalidateImages();
        invalidateButtons();
      }
    }
  }
  
  /**
   * Returns the array of Names for the images displayed on the ButtonBar.
   */
  public String[] getImageNames() {
    String[] s = new String[imageNames.length];
    System.arraycopy(imageNames, 0, s, 0, s.length);
    return s;
  }
  
  /**
   * Sets the buttonType for this ButtonBar.
   * 
   * @param buttonType
   *          The desired button type. Can be TEXT_ONLY, IMAGE_ONLY, or
   *          TEXT_AND_IMAGE
   */
  public void setButtonType(int buttonType) {
    if (buttonType == TEXT_ONLY || buttonType == IMAGE_ONLY
        || buttonType == TEXT_AND_IMAGE) {
      if (buttonType != this.buttonType) {
        this.buttonType = buttonType;
        invalidateButtons();
      }
    } else
      throw new IllegalArgumentException(java.text.MessageFormat.format(
          Res._InvalidButtonType, new Object[] { String.valueOf(buttonType) }));
  }
  
  /**
   * Returns the buttonType setting for this ButtonBar. buttonType property can
   * be: TEXT_ONLY, IMAGE_ONLY, or TEXT_AND_IMAGE
   */
  public int getButtonType() {
    return buttonType;
  }
  
  /**
   * orentation property defines how label & image are oriented: either
   * Orientation.HORIZONTAL or Orientation.VERTICAL
   */
  public void setButtonOrientation(int o) {
    buttonOrient = o;
    for (int i = 0; i < getComponentCount(); i++) {
      ((ButtonControl) getComponent(i)).setOrientation(buttonOrient);
      getComponent(i).invalidate();
    }
  }
  
  public int getButtonOrientation() {
    return buttonOrient;
  }
  
  public void setOpaque(boolean opaque) {
    super.setOpaque(opaque);
    for (int i = 0; i < getComponentCount(); i++)
      ((ButtonControl) getComponent(i)).setOpaque(opaque);
  }
  
  /**
   * buttonAlignment property defines how label & image are aligned on button
   */
  public void setButtonAlignment(int a) {
    buttonAlign = a;
    for (int i = 0; i < getComponentCount(); i++) {
      ((ButtonControl) getComponent(i)).setAlignment(buttonAlign);
      getComponent(i).invalidate();
    }
  }
  
  public int getButtonAlignment() {
    return buttonAlign;
  }
  
  /**
   * imageFirst property defines how label & image are arranged: either true for
   * image on left/top or false for image on right/bottom
   */
  public void setImageFirst(boolean first) {
    imageFirst = first;
    for (int i = 0; i < getComponentCount(); i++) {
      ((ButtonControl) getComponent(i)).setImageFirst(imageFirst);
    }
  }
  
  public boolean isImageFirst() {
    return imageFirst;
  }
  
  /**
   * The showRollover property enables/disables the repainting of the rollover
   * item. The rollover item is the item that currently has the mouse floating
   * over it. If an ItemPainter plugged into the list ignores the ROLLOVER bit,
   * this property will have no effect. By default, showRollover is false.
   */
  public void setShowRollover(boolean showRollover) {
    this.showRollover = showRollover;
    for (int i = 0; i < getComponentCount(); i++) {
      ((ButtonControl) getComponent(i)).setShowRollover(showRollover);
    }
  }
  
  public boolean isShowRollover() {
    return showRollover;
  }
  
  /**
   * Returns the Alignment setting for this ButtonBar.
   */
  public int getAlignment() {
    LayoutManager layout = getLayout();
    if (layout instanceof FlowLayout)
      return ((FlowLayout) layout).getAlignment();
    return 0;
  }
  
  /**
   * Sets the Alignment for this ButtonBar.
   * 
   * @param alignment
   *          The desired aligment setting.
   */
  public void setAlignment(int alignment) {
    LayoutManager layout = getLayout();
    if (layout instanceof FlowLayout)
      ((FlowLayout) layout).setAlignment(alignment);
  }
  
  /**
   * Returns the Horizontal gap setting, in pixels, for this ButtonBar.
   */
  public int getHgap() {
    LayoutManager layout = getLayout();
    if (layout instanceof FlowLayout)
      return ((FlowLayout) layout).getHgap();
    if (layout instanceof GridLayout)
      return ((GridLayout) layout).getHgap();
    return 0;
  }
  
  /**
   * Sets the horizontal gap setting, in pixels, for this ButtonBar.
   * 
   * @param gap
   *          The desired gap setting
   */
  public void setHgap(int gap) {
    LayoutManager layout = getLayout();
    if (layout instanceof FlowLayout)
      ((FlowLayout) layout).setHgap(gap);
    else if (layout instanceof GridLayout)
      ((GridLayout) layout).setHgap(gap);
  }
  
  /**
   * Returns the vertical gap setting, in pixels, for this ButtonBar.
   */
  public int getVgap() {
    LayoutManager layout = getLayout();
    if (layout instanceof FlowLayout)
      return ((FlowLayout) layout).getVgap();
    if (layout instanceof GridLayout)
      return ((GridLayout) layout).getVgap();
    return 0;
  }
  
  /**
   * Sets the vertical gap setting, in pixels, for this ButtonBar.
   * 
   * @param gap
   *          The desired gap setting
   */
  public void setVgap(int gap) {
    LayoutManager layout = getLayout();
    if (layout instanceof FlowLayout)
      ((FlowLayout) layout).setVgap(gap);
    else if (layout instanceof GridLayout)
      ((GridLayout) layout).setVgap(gap);
  }
  
  /**
   * Enables or disables a particular button by index
   * 
   * @param index
   *          The index of the button to be enabled.
   * @param enabled
   *          Whether or not to enable the button.
   */
  public void setButtonEnabled(int index, boolean enabled) {
    if (index < 0 || index >= enabledState.length)
      throw new IllegalArgumentException();
    if (!needsRebuild && isEnabled()) {
      Object object = buttons.elementAt(index);
      if (object instanceof ButtonControl)
        ((ButtonControl) object).setEnabled(enabled);
    }
    enabledState[index] = enabled;
  }
  
  public boolean isButtonEnabled(int index) {
    if (index < 0 || index >= enabledState.length)
      throw new IllegalArgumentException();
    if (!needsRebuild) {
      Object object = buttons.elementAt(index);
      if (object instanceof ButtonControl)
        return ((ButtonControl) object).isEnabled();
    }
    return false;
  }
  
  /**
   * Sets a particular button to visible or invisible by index
   * 
   * @param index
   *          The index of the button to be affected.
   * @param visible
   *          Whether or not to make the button visible.
   */
  public void setButtonVisible(int index, boolean visible) {
    if (index < 0 || index >= visibleState.length)
      throw new IllegalArgumentException();
    if (!needsRebuild) {
      Object object = buttons.elementAt(index);
      if (object instanceof ButtonControl)
        ((ButtonControl) object).setVisible(visible);
    }
    visibleState[index] = visible;
  }
  
  public boolean isButtonVisible(int index) {
    if (index < 0 || index >= visibleState.length)
      throw new IllegalArgumentException();
    if (!needsRebuild) {
      Object object = buttons.elementAt(index);
      if (object instanceof ButtonControl)
        return ((ButtonControl) object).isVisible();
    }
    return false;
  }
  
  /**
   * Enables or disables a particular button by label
   * 
   * @param label
   *          The label of the button to be enabled.
   * @param enabled
   *          Whether or not to enable the button.
   */
  public void setButtonEnabled(String label, boolean enabled) {
    for (int i = 0; i < labels.length; i++) {
      if (labels[i].equals(label)) {
        setButtonEnabled(i, enabled);
        return;
      }
    }
  }
  
  public boolean isButtonEnabled(String label) {
    for (int i = 0; i < labels.length; i++) {
      if (labels[i].equals(label))
        return isButtonEnabled(i);
    }
    return false;
  }
  
  /**
   * Sets a particular button to visible or invisible by label
   * 
   * @param label
   *          The label of the button to be affected.
   * @param visible
   *          Whether or not to make the button visible.
   */
  public void setButtonVisible(String label, boolean visible) {
    for (int i = 0; i < labels.length; i++) {
      if (labels[i].equals(label)) {
        setButtonVisible(i, visible);
        return;
      }
    }
  }
  
  public boolean isButtonVisible(String label) {
    for (int i = 0; i < labels.length; i++) {
      if (labels[i].equals(label))
        return isButtonVisible(i);
    }
    return false;
  }
  
  // Action Events
  
  public synchronized void addActionListener(ActionListener l) {
    actionMulticaster.add(l);
  }
  
  public synchronized void removeActionListener(ActionListener l) {
    actionMulticaster.remove(l);
  }
  
  // ActionListener interface
  
  protected void invalidateButtons() {
    needsRebuild = true;
    invalidate();
  }
  
  protected void invalidateImages() {
    if (images != null)
      images = null;
    if (imageNames.length > 0) {
      images = new Image[imageNames.length];
      mt = new MediaTracker(this);
      
      // prepare potential name prefix
      String prefix = imageBase;
      if (prefix != null && prefix.length() > 0 && !prefix.endsWith("/")
          && !prefix.endsWith(java.io.File.separator))
        prefix = prefix + "/";
      for (int i = 0; i < imageNames.length; i++) {
        String name = prefix != null ? (prefix + imageNames[i]) : imageNames[i];
        // System.err.println("prefix=" + prefix + " imageNames[i]=" +
        // imageNames[i] + " name=" + name);
        images[i] = ImageLoader.loadFromResource(name, this);
        if (images[i] == null) {
          try {
            images[i] = ImageLoader.load(new URL(name), this, true);
          } catch (MalformedURLException e) {
            images[i] = ImageLoader.load(name, this, true);
          }
          if (images[i] != null) {
            mt.addImage(images[i], i);
          }
        }
      }
      mt.checkAll(true);
      try {
        mt.waitForAll(0);
      } catch (InterruptedException x) {
      }
    }
  }
  
  protected void assureImages() {
    if (mt != null) {
      try {
        // System.err.println("assureImages waitForAll...");
        mt.waitForAll(0);
      } catch (InterruptedException e) {
        Diagnostic.printStackTrace(e);
      }
      mt = null;
    }
  }
  
  protected void rebuild() {
    if (needsRebuild) {
      removeAll();
      assureImages();
      buttons.removeAllElements();
      for (int i = 0; i < labels.length; i++) {
        if (labels[i] != null && labels[i].length() > 0) {
          if (buttonType == TEXT_ONLY || imageNames == null
              || i >= imageNames.length)
            addTextButton(labels[i], labels[i]);
          else {
            if (buttonType == TEXT_AND_IMAGE)
              addImageButton(images[i], labels[i], labels[i]);
            else
              addImageButton(images[i], null, labels[i]);
          }
          if (isEnabled())
            ((ButtonControl) buttons.elementAt(i)).setEnabled(enabledState[i]);
          else
            ((ButtonControl) buttons.elementAt(i)).setEnabled(false);
          ((ButtonControl) buttons.elementAt(i)).setVisible(visibleState[i]);
        } else
          addSpace();
      }
      needsRebuild = false;
    }
  }
  
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    needsRebuild = true;
    doLayout();
  }
  
  public void doLayout() {
    rebuild();
    super.doLayout();
  }
  
  public Dimension getPreferredSize() {
    rebuild();
    Dimension d = super.getPreferredSize();
    if (d.height == 150)
      d.height = 32;
    return d;
  }
  
  public void addMouseListener(MouseListener l) {
    super.addMouseListener(l);
    mouseMulticaster.add(l);
  }
  
  public void removeMouseListener(MouseListener l) {
    super.removeMouseListener(l);
    mouseMulticaster.remove(l);
  }
  
  public void addMouseMotionListener(MouseMotionListener l) {
    super.addMouseMotionListener(l);
    mouseMotionMulticaster.add(l);
  }
  
  public void removeMouseMotionListener(MouseMotionListener l) {
    super.removeMouseMotionListener(l);
    mouseMotionMulticaster.remove(l);
  }
  
  // protected methods for button maintenance available to subclasses
  
  protected Component addImageButton(Image image, String label, String command) {
    ButtonControl button = new ButtonControl(image);
    if (label != null)
      button.setLabel(label);
    button.setActionCommand(command);
    button.addActionListener(actionMulticaster);
    button.addMouseListener(mouseMulticaster);
    button.addMouseMotionListener(mouseMotionMulticaster);
    button.setFocusAware(false);
    button.setShowRollover(showRollover);
    button.setOrientation(buttonOrient);
    button.setAlignment(buttonAlign);
    button.setOpaque(isOpaque());
    button.setImageFirst(imageFirst);
    buttons.addElement(button);
    invalidate();
    return add(button);
  }
  
  protected Component addTextButton(String label, String command) {
    ButtonControl button = new ButtonControl();
    button.setLabel(label);
    button.setActionCommand(command);
    button.addActionListener(actionMulticaster);
    button.addMouseListener(mouseMulticaster);
    button.addMouseMotionListener(mouseMotionMulticaster);
    button.setFocusAware(false);
    button.setShowRollover(showRollover);
    button.setOrientation(buttonOrient);
    button.setAlignment(buttonAlign);
    button.setOpaque(isOpaque());
    button.setImageFirst(imageFirst);
    buttons.addElement(button);
    invalidate();
    return add(button);
  }
  
  protected Component addSpace() {
    invalidate();
    return addSpace(5); // default gap
  }
  
  protected Component addSpace(int gap) {
    Spacer space = new Spacer(gap);
    buttons.addElement(space);
    repaint(100);
    invalidate();
    return super.add(space);
  }
  
  // Serialization support
  
  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    if (images != null && images.length > 0) {
      SerializableImage[] si = new SerializableImage[images.length];
      for (int i = 0; i < images.length; i++)
        si[i] = SerializableImage.create(images[i]);
      s.writeObject(si);
    } else
      s.writeObject(null);
  }
  
  private void readObject(ObjectInputStream s) throws IOException,
      ClassNotFoundException {
    s.defaultReadObject();
    Object data = s.readObject();
    if (data instanceof SerializableImage[]) {
      SerializableImage[] si = (SerializableImage[]) data;
      images = new Image[si.length];
      for (int i = 0; i < si.length; i++)
        images[i] = si[i].getImage();
    }
  }
  
  private transient Image[] images; // non-null when images are loading & loaded
  
  // private int orientation = Orientation.HORIZONTAL;
  private int buttonType = TEXT_AND_IMAGE;
  private int buttonOrient = Orientation.VERTICAL;
  private int buttonAlign = Alignment.CENTER | Alignment.MIDDLE;
  private boolean imageFirst = true;
  private boolean showRollover = false;
  
  private String imageBase;
  private String[] labels = new String[0];
  private String[] imageNames = new String[0];
  private Vector<Component> buttons = new Vector<Component>();
  private boolean[] enabledState = new boolean[0];
  private boolean[] visibleState = new boolean[0];
  private MediaTracker mt; // non-null when image loading is in progress
  
  protected transient ActionMulticaster actionMulticaster = new ActionMulticaster();
  protected transient MouseMulticaster mouseMulticaster = new MouseMulticaster();
  protected transient MouseMotionMulticaster mouseMotionMulticaster = new MouseMotionMulticaster();
  
  protected BorderItemPainter border = new BorderItemPainter(
      BorderItemPainter.RAISED);
  protected boolean needsRebuild;
}
