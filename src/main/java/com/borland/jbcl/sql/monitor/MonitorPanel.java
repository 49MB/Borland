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
package com.borland.jbcl.sql.monitor;

// import sun.awt.HorizBagLayout;

// AWT imports
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.DriverManager;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * MonitorPanel provides a simple UI for logging output from the JDBC DriverManager.
 * It contains a checkbox for enabling/disabling log output, a button for
 * saving the current log output to a file, and a button for clearing the
 * log.  The save-to-file and clear log buttons will respect the current
 * selected log text.  If no log text has been selected, both buttons
 * work on all the text in the log.
 *<P>
 * In some environments, an unlimited log file size will crash the
 * TextArea used to display the log.  Use setMaxLogSize() to adjust
 * the max log size appropriate for your environment.  The default
 * size is 8K.
 *
 */

public class MonitorPanel extends JPanel
  implements ActionListener, ItemListener
{
  public final int hlpMonitorPanel = 0;
  public final int hlpSourceDialog = 1;

  protected BorderLayout borderLayout1 = new BorderLayout();
  protected JTextArea textArea = new JTextArea();
  protected JScrollPane scrollPane = new JScrollPane();
  protected JButton clearButton = new JButton(Res._Clear);     
  protected JButton saveButton = new JButton(Res._Save);     
  protected JCheckBox enableOutputCheckbox = new JCheckBox(Res._Enable);     
  protected JPanel buttonPanel = new JPanel(new GridBagLayout());
  protected JPanel outerButtonPanel = new JPanel();
  protected TextAreaStream textAreaStream = new TextAreaStream(textArea);

  public MonitorPanel() {
    try {
      jbInit();
    }
    catch (Exception x) {
      x.printStackTrace();
    }
  }

  public void jbInit() throws Exception {
    scrollPane.setPreferredSize(new Dimension(100, 250));
    clearButton.setMnemonic(Res._ClearMnemonic.charAt(0));     
    saveButton.setMnemonic(Res._SaveMnemonic.charAt(0));     
    enableOutputCheckbox.setMnemonic(Res._EnableMnemonic.charAt(0));     
    this.setLayout(borderLayout1);
    setBackground(SystemColor.control);
    setForeground(SystemColor.controlText);

    buttonPanel.add(enableOutputCheckbox, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    buttonPanel.add(saveButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 6, 0, 0), 0, 0));
    buttonPanel.add(clearButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
        GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 6, 0, 0), 0, 0));

    saveButton.addActionListener(this);
    clearButton.addActionListener(this);

    enableOutputCheckbox.setSelected(true);
    enableOutputCheckbox.addItemListener(this);

    textAreaStream.setEnabled(true);

    textArea.setEditable(false);
    scrollPane.getViewport().add(textArea);
    this.add(scrollPane, BorderLayout.CENTER);
    this.add(buttonPanel, BorderLayout.SOUTH);
  }

  public void setOutputEnabled(boolean outputEnabled) {
    textAreaStream.setEnabled(outputEnabled);
  }

  public boolean isOutputEnabled() {
    return textAreaStream.isEnabled();
  }

  /**
   * In some environments, an unlimited log file size
   * will crash the TextArea used to display the log.
   * Use setMaxLogSize() to adjust the max log size
   * appropriate for your environment.  The default
   * size is 8K.
   */
  public void setMaxLogSize(int length) {
    textAreaStream.setMaxLength(length);
  }

  public int getMaxLogSize() {
    return textAreaStream.getMaxLength();
  }

  //
  // ItemListener
  //
  public void itemStateChanged(ItemEvent e) {
    if (e.getSource() == enableOutputCheckbox) {
      textAreaStream.setEnabled(enableOutputCheckbox.isSelected());
    }
  }

  //
  // ActionListener
  //
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == saveButton) {
      saveToFile();
    }
    else if (e.getSource() == clearButton) {
      if (textArea.getSelectionEnd() - textArea.getSelectionStart() == 0) {
        textArea.setText("");  
      }
      else {
        textArea.replaceRange("", textArea.getSelectionStart(), textArea.getSelectionEnd());
      }
      textAreaStream.resetLength();
    }
  }

  private void saveToFile() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    int val = fileChooser.showSaveDialog(this);
    if ( val == JFileChooser.APPROVE_OPTION ) {
      try {
        File file = new File(fileChooser.getSelectedFile().toString());
        if (file != null) {
          PrintWriter stream = new PrintWriter(new BufferedOutputStream(new FileOutputStream(file), 1024));
          if (textArea.getSelectionEnd() - textArea.getSelectionStart() == 0) {
            stream.println(textArea.getText());  
          }
          else {
            stream.println(textArea.getSelectedText());
          }
          stream.close();
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}

class TextAreaStream extends PrintStream {
  JTextArea textArea;
  boolean enabled = false;
  int maxLength = 8096;
  int length = 0;

  public TextAreaStream(JTextArea textArea){
//    super(null);  // Yes, this is deprecated and used on purpose here.
    // in JDK 1.2, null could no longer be passed to the PrintStream
    // constructor, so instead we send a dummy ByteArrayOutputStream instead.
    super(new ByteArrayOutputStream());
    this.textArea = textArea;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    if (enabled) {
      DriverManager.setLogStream(this);
//      DriverManager.setLogWriter(this);
    }
    else {
      DriverManager.setLogStream(null);
//      DriverManager.setLogWriter(null);
    }
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setMaxLength(int maxLength) {
    this.maxLength = maxLength;
  }

  public int getMaxLength() {
    return maxLength;
  }

  void resetLength() {
    length = 0;
  }

  public void println(String string) {
    if (enabled) {
      int appendLen = string.length() + 1;

      if (length + appendLen > maxLength) {
        textArea.replaceRange("", 0, appendLen);  
        length = maxLength;
      }
      else {
        length += appendLen;
      }
      textArea.append(string + "\n");   
    }
  }
  //Depricated setLogStream() is used instead of setLogWriter() because
  //backward compatibility probelm(According to Steve S.).
  //When a driver call getLogWriter() and use println() on that writer,
  //PrintStream's print() instead of println() is called.
  //That caused log info is missed for some drivers.
  public void print(String string) {
    if (enabled) {
      int appendLen = string.length() ;

      if (length + appendLen > maxLength) {
        textArea.replaceRange("", 0, appendLen);  
        length = maxLength;
      }
      else {
        length += appendLen;
      }
      textArea.append(string );   
    }
  }
  public void print(int i) {
    print(String.valueOf(i));
  }
  public void print(boolean b) {
    print(b ? "true" : "false");   
  }
  public void print(float f) {
    print(String.valueOf(f));
  }
  public void print(char c) {
    print(String.valueOf(c));
  }
  public void println() {
    print("\n");  
  }
  public void print(Object obj) {
    print(String.valueOf(obj));
  }
  public void print(double d) {
    print(String.valueOf(d));
  }
  public void print(long l) {
    print(String.valueOf(l));
  }
  public void println(Object x) {
    print(x);
    println();
  }
  public void write(int b) {
    print(b);
  }
  public void print(char[] parm1) {
    print(new String(parm1));
  }
  public void println(int x) {
    print(x);
    println();
  }
  public void println(double x) {
    print(x);
    println();
  }
  public void write(byte[] parm1, int parm2, int parm3) {
    print(new String(parm1,parm2,parm3));
  }
  public void println(boolean x) {
    print(x);
    println();
  }
  public void println(float x) {
    print(x);
    println();
  }
  public void println(char x) {
    print(x);
    println();
  }
  public void println(long x) {
    print(x);
    println();
  }
}

