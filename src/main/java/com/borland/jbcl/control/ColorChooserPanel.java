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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JTextField;

import com.borland.jbcl.layout.PaneConstraints;
import com.borland.jbcl.layout.PaneLayout;

public class ColorChooserPanel
     extends BevelPanel
  implements AdjustmentListener, KeyListener, ItemListener, java.io.Serializable
{
  public static final String CUSTOM_TAG = Res._CustomColorTag;     
  private boolean showColorList = true;
  public ColorChooserPanel() {
    this(true);
  }
  public ColorChooserPanel(boolean showColorList) {
    this.showColorList = showColorList;
    setBevelInner(FLAT);
    setMargins(new Insets(0,5,5,5));
    try {
      jbInit();
      hsbMode = true; // make it recalc
      setHsbMode(false);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    value = Color.white;
  }
  public ColorChooserPanel(Color value,boolean showColorList) {
    this(showColorList);
    this.value = value;
  }
  public ColorChooserPanel(Color value) {
    this(true);
    this.value = value;
  }

  ButtonDialog findButtonDialog() {
    Component c = getParent();
    while (c != null && !(c instanceof ButtonDialog))
      c = c.getParent();
    if (c instanceof ButtonDialog)
      return (ButtonDialog)c;
    return null;
  }

  public void addNotify() {
    super.addNotify();
    ButtonDialog bd = findButtonDialog();
    if (bd != null) {
      bd.setEnterOK(true);
      bd.setEscapeCancel(true);
    }
  }

  void jbInit() throws Exception {
    // Standard Color Choice
    JPanel choicePan = new JPanel();
    choicePan.setLayout(new GridLayout(2,0));
    choicePan.add(new JLabel(Res._StandardColors));     
    fillChoice();
    choice.addItemListener(this);
    choicePan.add(choice);

    // Custom Aspect Pickers
    JPanel pickPan = new JPanel();
    pickPan.setLayout(new GridLayout(0,3));
    for (int i = 0; i < 3; i++) {
      pickers[i] = new AspectPicker();
      pickers[i].text.addKeyListener(this);
      pickers[i].scrollbar.addAdjustmentListener(this);
      pickPan.add(pickers[i]);
    }

    // RGB / HSB selection
    JPanel checkPan = new JPanel();
    ButtonGroup g = new ButtonGroup();
    checkPan.add(checkbox[0] = new JRadioButton("RGB", true));  
    checkPan.add(checkbox[1] = new JRadioButton("HSB", false));  
    g.add(checkbox[0]);
    g.add(checkbox[1]);
    checkbox[0].addItemListener(this);
    checkbox[1].addItemListener(this);

    // Custom color groupbox
    //GroupBox customPan = new GroupBox(Res._CustomColorGroupBox);
    JPanel customPan = new JPanel();
    customPan.setBorder(BorderFactory.createTitledBorder(Res._CustomColorGroupBox));     
    customPan.setLayout(new BorderLayout());
    customPan.add(pickPan, BorderLayout.CENTER);
    customPan.add(checkPan, BorderLayout.SOUTH);

    // Sample panel
    JPanel samplePan = new JPanel();
    samplePan.setLayout(new BorderLayout());
    samplePan.add(new JLabel(Res._Sample), BorderLayout.NORTH);     
    samplePan.add(sample, BorderLayout.CENTER);

    // Split the sample and the custom stuff
    PaneConstraints pc = new PaneConstraints();
    pc.position = PaneConstraints.ROOT;
    JPanel split = new JPanel();
    split.setLayout(new PaneLayout());
    split.add(customPan, pc);
    pc.position = PaneConstraints.RIGHT;
    pc.proportion = 0.30f;
    split.add(samplePan, pc);
    //split.setGap(2);

    // Now assemble the whole thing...
    this.setLayout(new BorderLayout(0, 5));
    if (showColorList)
      this.add(choicePan, BorderLayout.NORTH);
    this.add(split, BorderLayout.CENTER);
  }

  public Color getColorValue() {
    return value;
  }

  public void setColorValue(Color value) {
    changeColor(value);
    changed = false;
  }

  public boolean isChanged() {
    return changed;
  }

  protected void colorChanged(Color newColor) {
    changed = true;
  }

  protected void changeColor(Color value) {
    changeColor(value, false, false);
  }

  protected void changeColor(Color value, boolean suppressText, boolean suppressScroll) {
    if (value != null && value.equals(this.value))
      return;
    this.value = value;
    if (value == null)
      return;

    // decompose the color
    if (!hsbMode) {
      comp[0] = value.getRed();
      comp[1] = value.getGreen();
      comp[2] = value.getBlue();
    }
    else {
      float[] hsbComp = Color.RGBtoHSB(value.getRed(), value.getGreen(), value.getBlue(), null);
      comp[0] = (int)(hsbComp[0]*100);
      comp[1] = (int)(hsbComp[1]*100);
      comp[2] = (int)(hsbComp[2]*100);
    }

    //System.err.println("changeColor: " + value + " hsbMode:" + hsbMode + " 0:" + comp[0] + " 1:" + comp[1] + " 2:" + comp[2]);
   changing = true;
    // update text columns and scrollers with color components--rgb or hsb
    for (int i = 0; i < 3; i++) {
      if (!suppressText) {
        String t = Integer.toString(comp[i]);
        if (text[i] == null || !text[i].equals(t)) {
          text[i] = t;
          pickers[i].text.setText(t);
        }
      }
      int max = hsbMode ? 100 : 255;
      if (!suppressScroll && pickers[i].scrollbar.getValue() != comp[i])
        pickers[i].scrollbar.setValues(comp[i], 0, 0, (i==0 && hsbMode)? max : max+1);
    }

    String text = valueToText(value, true);
    if (text != null && showColorList) {
      String oldText = (String)choice.getSelectedItem();
      if (!text.equals(oldText))
        choice.setSelectedItem(text);
    }
    if (value != sample.getForeground())
      sample.setForeground(value);

    if (!hsbMode) {
      pickers[0].sample.setForeground(new Color(255, 255 - comp[0], 255 - comp[0]));
      pickers[1].sample.setForeground(new Color(255 - comp[1], 255, 255 - comp[1]));
      pickers[2].sample.setForeground(new Color(255 - comp[2], 255 - comp[2], 255));
    }
//    else {
//      pickers[0].sample.setForeground(Color.getHSBColor(comp[0], 50, 100));
//      pickers[1].sample.setForeground(Color.getHSBColor(comp[0], comp[1], 100));
//      pickers[2].sample.setForeground(Color.getHSBColor(comp[0], 50, comp[2]));
//    }
    changing = false;
    colorChanged(value);
  }

  public void setHsbMode(boolean hsbMode) {
    if (this.hsbMode != hsbMode) {
      changing = true;
      this.hsbMode = hsbMode;
      int max = hsbMode ? 100 : 255;
      for (int i = 0; i < 3; i++) {
        pickers[i].label.setText(hsbMode ? hsbLabels[i] : rgbLabels[i]);
        pickers[i].scrollbar.setValues(comp[i], 0, 0, (i==0 && hsbMode)? max : max+1);
        pickers[i].sample.setVisible(!hsbMode);
      }
      changing = false;
      //checkbox[0].setSelected(!hsbMode);
      if (checkbox[1].isSelected() != hsbMode)
        checkbox[1].setSelected(hsbMode);
      Color newValue = value;
      value = null;
      changeColor(newValue);
    }
  }

  public boolean isHsbMode() {
    return hsbMode;
  }
  boolean changing;
  public void adjustmentValueChanged(AdjustmentEvent event) {
    if (changing)
      return;
    Color c;
    int[] cValue = new int[3];
    int max[] = new int[3];
    //If you drag the thumb outside the bounds, it returns a goofy value
    for (int i=0; i< 3; i++) {
      cValue[i] = pickers[i].scrollbar.getValue();
      max[i] = pickers[i].scrollbar.getMaximum() - 1;
      if (cValue[i] < 0)
        cValue[i] = 0;
      else if (cValue[i] >  max[i])
        cValue[i] = max[i];
    }
    if (hsbMode)
      c = Color.getHSBColor(Math.min(cValue[0] / 100.0f, 0.999999f),
                                     cValue[1] / 100.0f,
                                     cValue[2] / 100.0f);
    else
      c = new Color(cValue[0], cValue[1], cValue[2]);
    if (!c.equals(value))
      changeColor(c, false, false);
  }

  public void keyTyped(KeyEvent e) {}
  public void keyPressed(KeyEvent e) {}
  public void keyReleased(KeyEvent e) {
    try {
      int[] t = new int[3];
      boolean bad = false;
      int max = hsbMode ? 100 : 255;
      for (int i = 0; i < 3; i++) {
        t[i] = Integer.parseInt(pickers[i].text.getText());
        if (t[i] < 0 || t[i] > max)
          bad = true;
      }
      if (!bad) {
        Color c;
        if (hsbMode)
          c = Color.getHSBColor(Math.min(t[0] / 100.0f, 0.999999f), t[1] / 100.0f, t[2] / 100.0f);
        else
          c = new Color(t[0], t[1], t[2]);
        if (!c.equals(value))
          changeColor(c, true, false);
      }
    }
    catch (NumberFormatException x) {
    }
  }

  public void itemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.DESELECTED || changing)
      return;
    if (e.getSource() == choice) {
      Color c = textToValue((String)choice.getSelectedItem(), true);
      if (c != null)
        changeColor(c, false, false);
    }
    else {
    //System.err.println("itemStateChanged: " + e);
//    setHsbMode(e.getStateChange() == ItemEvent.SELECTED);
      setHsbMode(e.getItemSelectable() == checkbox[1]);
    }
  }

  void fillChoice() {
    DefaultComboBoxModel model = (DefaultComboBoxModel)choice.getModel();
    model.addElement(CUSTOM_TAG);
    for (int i = 0; i < colorValues.length; i++)
      model.addElement(colorNames[0][i]);
  }

  // colorNames[0] = display text (translated)
  // colorNames[1] = source code text (not translated)
  //
  public static final String[][] colorNames = {
    {
      Res._White,     
      Res._LightGray,     
      Res._Gray,     
      Res._DarkGray,     
      Res._Black,     
      Res._Red,     
      Res._Pink,     
      Res._Orange,     
      Res._Yellow,     
      Res._Green,     
      Res._Magenta,     
      Res._Cyan,     
      Res._Blue,     
      Res._Desktop,     
      Res._ActiveCaption,     
      Res._ActiveCaptionText,     
      Res._ActiveCaptionBorder,     
      Res._InactiveCaption,     
      Res._InactiveCaptionText,     
      Res._InactiveCaptionBorder,     
      Res._Window,     
      Res._WindowBorder,     
      Res._WindowText,     
      Res._Menu,     
      Res._MenuText,     
      Res._Text,     
      Res._TextText,     
      Res._Highlight,     
      Res._HighlightText,     
      Res._InactiveText,     
      Res._Control,     
      Res._ControlText,     
      Res._ControlHighlight,     
      Res._ControlLtHighlight,     
      Res._ControlShadow,     
      Res._ControlDkShadow,     
      Res._Scrollbar,     
      Res._Info,     
      Res._InfoText},     
    {
    "Color.white",                                
    "Color.lightGray",                            
    "Color.gray",                                 
    "Color.darkGray",                             
    "Color.black",                                
    "Color.red",                                  
    "Color.pink",                                 
    "Color.orange",                               
    "Color.yellow",                               
    "Color.green",                                
    "Color.magenta",                              
    "Color.cyan",                                 
    "Color.blue",                                 
    "SystemColor.desktop",                        
    "SystemColor.activeCaption",                  
    "SystemColor.activeCaptionText",              
    "SystemColor.activeCaptionBorder",            
    "SystemColor.inactiveCaption",                
    "SystemColor.inactiveCaptionText",            
    "SystemColor.inactiveCaptionBorder",          
    "SystemColor.window",                         
    "SystemColor.windowBorder",                   
    "SystemColor.windowText",                     
    "SystemColor.menu",                           
    "SystemColor.menuText",                       
    "SystemColor.text",                           
    "SystemColor.textText",                       
    "SystemColor.textHighlight",                  
    "SystemColor.textHighlightText",              
    "SystemColor.textInactiveText",               
    "SystemColor.control",                        
    "SystemColor.controlText",                    
    "SystemColor.controlHighlight",               
    "SystemColor.controlLtHighlight",             
    "SystemColor.controlShadow",                  
    "SystemColor.controlDkShadow",                
    "SystemColor.scrollbar",                      
    "SystemColor.info",                           
    "SystemColor.infoText"},                      
  };

  static final Color[] colorValues = {
    Color.white,
    Color.lightGray,
    Color.gray,
    Color.darkGray,
    Color.black,
    Color.red,
    Color.pink,
    Color.orange,
    Color.yellow,
    Color.green,
    Color.magenta,
    Color.cyan,
    Color.blue,
    SystemColor.desktop,
    SystemColor.activeCaption,
    SystemColor.activeCaptionText,
    SystemColor.activeCaptionBorder,
    SystemColor.inactiveCaption,
    SystemColor.inactiveCaptionText,
    SystemColor.inactiveCaptionBorder,
    SystemColor.window,
    SystemColor.windowBorder,
    SystemColor.windowText,
    SystemColor.menu,
    SystemColor.menuText,
    SystemColor.text,
    SystemColor.textText,
    SystemColor.textHighlight,
    SystemColor.textHighlightText,
    SystemColor.textInactiveText,
    SystemColor.control,
    SystemColor.controlText,
    SystemColor.controlHighlight,
    SystemColor.controlLtHighlight,
    SystemColor.controlShadow,
    SystemColor.controlDkShadow,
    SystemColor.scrollbar,
    SystemColor.info,
    SystemColor.infoText,

  };

  public static String valueToText(Color c, boolean localized) {
    for (int i = 0; i < colorValues.length; i++)
      if (c == colorValues[i])
        return localized ? colorNames[0][i] : colorNames[1][i];
    for (int i = 0; i < colorValues.length; i++)
      if (c.equals(colorValues[i]))
        return localized ? colorNames[0][i] : colorNames[1][i];
    return CUSTOM_TAG;
  }

  public static Color textToValue(String text, boolean localized) {
    return localized ? (Color)localizedTextToValueMap.get(text) : (Color)textToValueMap.get(text);
  }

  static Hashtable textToValueMap;
  static Hashtable localizedTextToValueMap;

  static {
    int i = 0;
    try {
      textToValueMap = new Hashtable();
      localizedTextToValueMap = new Hashtable();
      //System.err.println("color values len -" +colorValues.length);
      for ( i = 0; i < colorValues.length; i++) {
        if (colorValues[i] != null) {
          localizedTextToValueMap.put(colorNames[0][i], colorValues[i]);
          textToValueMap.put(colorNames[1][i], colorValues[i]);
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      System.err.println("color values len " + i + " " + colorValues[i] + colorNames[0][i] + colorNames[1][i]); 
    }
  }

  public Dimension getPreferredSize() {
    Dimension ps = super.getPreferredSize();
    // widen labels (for Hue/Saturation/Brightness)
    ps.width += 40;
    return ps;
  }

  private Color          value;
  private ShapeControl   sample    = new ShapeControl(ShapeControl.ROUND_RECT);
  private JRadioButton[]     checkbox  = new JRadioButton[2];
  private String[]       rgbLabels = {Res._RedLabel, Res._GreenLabel, Res._BlueLabel};     
  private String[]       hsbLabels = {Res._Hue, Res._Saturation, Res._Brightness};     
  private int[]          comp      = new int[3];
  private boolean        hsbMode   = false;
  private boolean        changed   = false;
  private String[]       text      = new String[3];
  private JComboBox      choice    = new JComboBox();
  private AspectPicker[] pickers   = new AspectPicker[3];
}

class AspectPicker extends JComponent implements java.io.Serializable
{
  public AspectPicker() {
    sample.setDrawEdge(false);
    bevel.setBevelInner(BevelPanel.LOWERED);
    bevel.setBevelOuter(BevelPanel.LOWERED);
    bevel.setLayout(new BorderLayout());
    pan1.setLayout(new BorderLayout());
    pan1.add(text, BorderLayout.NORTH);
    pan1.add(bevel, BorderLayout.CENTER);
    bevel.add(sample, BorderLayout.CENTER);
    bevel.add(scrollbar, BorderLayout.NORTH);
    this.setLayout(new BorderLayout());
    this.add(label, BorderLayout.NORTH);
    this.add(pan1, BorderLayout.CENTER);
  }

  public Dimension getPreferredSize() {
    Dimension ps = super.getPreferredSize();
    // shrink up ShapeControl
    ps.width -= 80;
    ps.height -= 80;
    return ps;
  }

  ShapeControl sample    = new ShapeControl();   // color level sample (for this aspect)
  JScrollBar    scrollbar = new JScrollBar(JScrollBar.HORIZONTAL); // draggable value changes
  JTextField    text      = new JTextField("", 3); // numeric setting
  JLabel        label     = new JLabel();          // "Red:" or "Hue:" etc...
  BevelPanel   bevel     = new BevelPanel();     // layout containment panel
  JPanel        pan1      = new JPanel();          // layout containment panel
}
