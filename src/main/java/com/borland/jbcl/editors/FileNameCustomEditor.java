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

package com.borland.jbcl.editors;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.borland.jbcl.control.ButtonDialog;
import com.borland.jbcl.layout.GridBagConstraints2;

public class FileNameCustomEditor extends JComponent implements ActionListener
{
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel label1 = new JLabel();
  JButton button1 = new JButton();
  JTextField textField1 = new JTextField();
  boolean directoriesOnly = false;
  private ImageIcon dotDotDotImage = new ImageIcon(getClass().getResource("image/dotdotdot.gif")); 
  //FileNameEditor editor;
  public FileNameCustomEditor(String value,String labelText) {
    this(value);
    directoriesOnly = true;
    label1.setText(labelText);
  }
  public FileNameCustomEditor(String value) {
    //this.editor = editor;
    try {
      jbInit();
      textField1.setText(value);
    }
    catch (Exception e) {
      com.borland.jb.util.Diagnostic.printStackTrace(e);
    }
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
  public void removeNotify() {
    super.removeNotify();
    //editor.editor = null;
    //editor = null;
  }

  public void jbInit(){
    label1.setText(Res._FileNameLabel);     
//    button1.setText(Res._Browse);
    button1.setIcon(dotDotDotImage);

    this.setLayout(gridBagLayout1);
    this.add(label1, new GridBagConstraints2(0, 0, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(8, 8, 0, 0), 0, 0));
    this.add(textField1, new GridBagConstraints2(0, 1, 1, 1, 1.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1, 8, 8, 0), 0, 0));
    this.add(button1, new GridBagConstraints2(1, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 4, 8, 8), 0, 0));
    button1.addActionListener(this);
  }

  static String lastDir = ""; 
  public void actionPerformed(ActionEvent actionEvent) {
    JFileChooser dialog = null;
    String filename = textField1.getText();
    File file = new java.io.File(filename);
    if (file.isDirectory())
      lastDir = filename;
    else
      lastDir = file.getParent();
    if (file.isDirectory())
      dialog = new JFileChooser(file);
    else
      dialog = new JFileChooser(file.getParent());
    dialog.setDialogTitle(Res._FileChooserTitle);     
    if (directoriesOnly)
      dialog.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    int returnVal = dialog.showOpenDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
//    System.out.println("You chose to open this file: " +
      file = dialog.getSelectedFile();
      if (file != null){
        filename = file.getAbsolutePath();
        lastDir = file.getParent();
      }
      else {
        file = dialog.getCurrentDirectory();
        lastDir = file.getAbsolutePath();
        filename = lastDir;
      }
      textField1.setText(filename);
    }
    //System.err.println("lastdir=" +lastDir);
  }

  Frame getFrame() {
    Component c = this;
    while (!(c instanceof Frame))
      c = c.getParent();
    return (Frame)c;
  }
}


