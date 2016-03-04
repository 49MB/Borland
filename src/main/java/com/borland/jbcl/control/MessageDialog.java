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
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.SystemColor;

import com.borland.jbcl.model.BasicViewManager;
import com.borland.jbcl.view.FieldView;
import com.borland.jbcl.view.WrappedTextItemPainter;

public class MessageDialog extends ButtonDialog
{
  protected Panel     messagePanel = new Panel(new BorderLayout());

  protected String    message;

  public MessageDialog(Frame frame, String title, String msg, int buttonSet) {
    super(frame, title, null, buttonSet);
    super.setCenterPanel(messagePanel);

    message = msg;

    messagePanel.add(buildMessageComponent(message), BorderLayout.CENTER);
    getContentPane().add(messagePanel, BorderLayout.CENTER);
  }

  public MessageDialog(Frame frame, String title, String msg) {
    this(frame, title, msg, OK);
  }

  public MessageDialog(Frame frame, String title) {
    this(frame, title, "", OK);
  }

  public MessageDialog(Frame frame) {
    this(frame, "", "", OK);
  }

  // Build the component that the dialog is going to use to display
  // the message.
  private Component buildMessageComponent(String message) {
    FontMetrics fm = getFontMetrics(getFont());
    int width = fm.stringWidth(message) + 20;

    // If the message will fit on one line, use a Label, and center
    // the message in the dialog.
    if (width < 320) {
      Label messageLabel = new Label(message, Label.CENTER);

      return messageLabel;
    }
    // If the message is too long to fit on one line, use a FieldView, and
    // left-justify the message in the dialog.
    else {
      FieldView messageField = new FieldView();

      messageField.setFlat(true);
      messageField.setItemMargins(new Insets(10, 10, 10, 10));
      messageField.setBackground(SystemColor.control);
      messageField.setViewManager(new BasicViewManager(new WrappedTextItemPainter()));
      messageField.getWriteModel().set(message);

      return messageField;
    }
  }

  public void setMessage(String s) {
    message = s;

    messagePanel.removeAll();
    messagePanel.add(buildMessageComponent(s));
  }

  public String getMessage() {
    return message;
  }

  public Dimension getPreferredSize() {
    return new Dimension(320, 160);
  }

  // If the font changes, recalculate the length of the message.
  public void setFont(Font f) {
    super.setFont(f);

    messagePanel.removeAll();
    messagePanel.add(buildMessageComponent(message));
  }
}

