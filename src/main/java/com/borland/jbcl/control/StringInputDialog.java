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
import java.awt.Frame;
import java.awt.TextField;
import java.awt.event.KeyListener;

import javax.swing.JComponent;

import com.borland.jbcl.view.Spacer;

public class StringInputDialog extends ButtonDialog implements java.io.Serializable
{
  StringInputPanel panel = new StringInputPanel();

  private String initialValue;

  public StringInputDialog(Frame frame, String title, String value) {
    super(frame, title, null, OK_CANCEL);
    super.setCenterPanel(panel);
    initialValue = value;
    setValue(value);
  }

  public StringInputDialog(Frame frame, String title) {
    this(frame, title, "");
  }

  public StringInputDialog(Frame frame) {
    this(frame, "", "");
  }

  public void setValue(String value) {
    if (value != null) {
      panel.tf.setText(value);
      panel.tf.setSelectionStart(0);
      panel.tf.setSelectionEnd(value.length());
    }
    else {
      panel.tf.setText("");
    }
  }

  public String getValue() {
    return panel.tf.getText();
  }
}

class StringInputPanel extends JComponent implements java.io.Serializable
{
  TextField tf = new TextField(30);

  public StringInputPanel() {
    super();
    this.setLayout(new BorderLayout());
    this.add(new Spacer(), BorderLayout.NORTH);
    this.add(new Spacer(), BorderLayout.SOUTH);
    this.add(new Spacer(), BorderLayout.EAST);
    this.add(new Spacer(), BorderLayout.WEST);
    this.add(tf = new TextField(30), BorderLayout.CENTER);
  }

  public void addKeyListener(KeyListener l) { tf.addKeyListener(l); }
  public void removeKeyListener(KeyListener l) { tf.removeKeyListener(l); }
}
