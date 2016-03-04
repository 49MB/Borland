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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class FontChooserPanel extends JPanel implements ItemListener,
    KeyListener, java.io.Serializable, ListSelectionListener {
  public FontChooserPanel() {
    try {
      jbInit();
    }
    catch (Exception x) {
      x.printStackTrace();
    }
  }

  public FontChooserPanel(Font font) {
    this();
    setFontValue(font);
  }

  ButtonDialog findButtonDialog() {
    Component c = getParent();
    while (c != null && !(c instanceof ButtonDialog)) {
      c = c.getParent();
    }
    if (c instanceof ButtonDialog) {
      return (ButtonDialog) c;
    }
    return null;
  }

  public void addNotify() {
    super.addNotify();
    ButtonDialog bd = findButtonDialog();
    if (bd != null) {
      bd.setEnterOK(true);
      bd.setEscapeCancel(true);
    }
    sizeField.requestDefaultFocus();
  }

  private Font font;
  private boolean changed;
  private JPanel groupBox1 = new JPanel();
  private JList nameList = new JList();
  private JTextField sizeField = new JTextField();
  private JLabel previewField = new JLabel();
  private JPanel previewFiller = new JPanel();
  private JCheckBox boldBox = new JCheckBox();
  private JCheckBox italicBox = new JCheckBox();
  private JPanel jPanel1 = new JPanel();
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JScrollPane listPane = new JScrollPane();
  private DefaultListModel nameModel = new DefaultListModel();
  private GridBagLayout gridBagLayout2 = new GridBagLayout();

  private void jbInit() {
    nameList.setModel(nameModel);
    this.setLayout(gridBagLayout2);
    setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
    boldBox.setText(Res._Bold);     
    boldBox.setMnemonic(Res._BoldMnemonic.charAt(0));     
    italicBox.setText(Res._Italic);     
    italicBox.setMnemonic(Res._ItalicMnemonic.charAt(0));     
    groupBox1.setBorder(BorderFactory.createTitledBorder(Res._Size));     
    previewField.setText(Res._FontSample);     
    listPane.getViewport().add(nameList);
    jPanel1.setLayout(gridBagLayout1);
    this.add(previewFiller, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0,
        0, 0), 0, 80)); 

    this.add(listPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0,
        0, 0), 0, 0)); 
    this.add(previewField, new GridBagConstraints(0, 1, 2, 1, 0.0, 1.0
        , GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(6,
        0, 0, 0), 0, 0)); 
    groupBox1.setLayout(new BorderLayout());
    this.add(jPanel1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 6,
        0, 0), 0, 0));
    jPanel1.add(boldBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0,
        0), 0, 0));
    jPanel1.add(italicBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0,
        0), 0, 0));
    jPanel1.add(groupBox1, new GridBagConstraints(0, 2, 1, 1, 0.0, 1.0
        , GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
        new Insets(0, 0, 0, 0), 0, 0));
    groupBox1.add(sizeField, BorderLayout.CENTER);

    String[] fontList = getFontList();
    for (int i = 0; i < fontList.length; i++) {
      nameModel.addElement(fontList[i]);

    }
    nameList.addListSelectionListener(this);
    boldBox.addItemListener(this);
    italicBox.addItemListener(this);
    sizeField.addKeyListener(this);
  }

  private String[] getFontList() {
//    return Toolkit.getDefaultToolkit().getFontList();
    return GraphicsEnvironment.getLocalGraphicsEnvironment().
        getAvailableFontFamilyNames();
  }

  public Font getFontValue() {
    return font;
  }

  public void setFontValue(Font font) {
    if (font != null) {
      sizeField.setText(Integer.toString(font.getSize()));
      changeFont(font);
    }
    else {
      sizeField.setText("12");
      nameList.clearSelection();
    }
    sizeField.setEnabled(font != null);
    boldBox.setEnabled(font != null);
    italicBox.setEnabled(font != null);
    changed = false;
  }

  public boolean isChanged() {
    return changed;
  }

  protected void fontChanged(Font newFont) {
    changed = true;
  }

  protected void changeFont(Font font) {
    changeFont(font, false, false);
  }

  protected void changeFont(Font font, boolean suppressList,
      boolean suppressText) {
    //System.err.println("changeFont: " + this.font + " to: " + font);
    if (font != null && font.equals(this.font)) {
      return;
    }
    this.font = font;
    if (font == null) {
      return;
    }

    String[] fontList = getFontList();

    if (!suppressList && fontList != null && nameList != null) {
      for (int i = 0; i < fontList.length; i++) {
        if (fontList[i].equals(font.getName())) {
          nameList.setSelectedIndex(i);
          nameList.scrollRectToVisible(nameList.getCellBounds(i, i));
          break;
        }
      }
    }

    /*
         if (!suppressText && sizeField != null)
      sizeField.setText(Integer.toString(font.getSize()));
     */
    if (boldBox != null) {
      boldBox.setSelected(font.isBold());
    }
    if (italicBox != null) {
      italicBox.setSelected(font.isItalic());

    }
    previewField.setFont(font);
    //System.err.println("FONT:"+font);
    fontChanged(font);
  }

  int getFontSize() {
    try {
      int sz = Integer.parseInt(sizeField.getText());
      if (sz > 3000) {
        sizeField.setText(String.valueOf(3000));
      }
      return sz < 3000 ? sz : 3000;
    }
    catch (NumberFormatException e) {
    }
    return 1;
  }

  public void itemStateChanged(ItemEvent e) {
    String name = (String) nameList.getSelectedValue();
    sizeField.setEnabled(true);
    boldBox.setEnabled(true);
    italicBox.setEnabled(true);
    Font f = new Font(name != null ? name : "Dialog", 
        (boldBox.isSelected() ? Font.BOLD : Font.PLAIN) +
        (italicBox.isSelected() ? Font.ITALIC : Font.PLAIN),
        getFontSize());
//System.err.println("FontChooserPanel.itemStateChanged(" + e + ") => " + f);
    changeFont(f, e != null && e.getSource()instanceof JList, false);
  }

  public void valueChanged(ListSelectionEvent e) {
    String name = (String) nameList.getSelectedValue();
    sizeField.setEnabled(true);
    boldBox.setEnabled(true);
    italicBox.setEnabled(true);
    Font f = new Font(name != null ? name : "Dialog", 
        (boldBox.isSelected() ? Font.BOLD : Font.PLAIN) +
        (italicBox.isSelected() ? Font.ITALIC : Font.PLAIN),
        getFontSize());
//System.err.println("FontChooserPanel.itemStateChanged(" + e + ") => " + f);
    changeFont(f, true, false);
  }

  public void keyPressed(KeyEvent e) {
  }

  public void keyTyped(KeyEvent e) {
  }

  public void keyReleased(KeyEvent e) {
    itemStateChanged(null);
  }

  public Dimension getPreferredSize() {
    Dimension d = super.getPreferredSize();
    d.width += 8;
    d.height += 8;
    return d;
  }
}
