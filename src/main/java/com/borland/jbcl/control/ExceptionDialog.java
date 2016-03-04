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
import java.awt.Frame;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Vector;

import com.borland.dx.text.Alignment;
import com.borland.jb.util.ChainedException;
import com.borland.jb.util.ExceptionChain;
import com.borland.jbcl.model.BasicSingletonContainer;
import com.borland.jbcl.model.BasicViewManager;
import com.borland.jbcl.view.WrappedTextItemPainter;

@SuppressWarnings("serial")
public class ExceptionDialog extends ButtonDialog implements ActionListener,
    Serializable {
  private static int makeButtonSet(Exception ex) {
    if (ex instanceof ChainedException
        && ((ChainedException) ex).getExceptionChain() != null)
      return PREVIOUS | NEXT | DETAILS | OK;
    return DETAILS | OK;
  }
  
  public ExceptionDialog(Frame frame, String title, Exception ex, boolean modal) {
    super(frame, title, modal, null, null,
        buttonSetToButtonDescriptors(makeButtonSet(ex)));
    this.ex = ex;
    
    details.setEditable(false);
    details.setBackground(SystemColor.window);
    details.setPreferredHeight(200);
    message.setPreferredHeight(100);
    message.setPreferredWidth(400);
    
    message.setModel(new BasicSingletonContainer());
    message.setAlignment(Alignment.LEFT);
    message.setViewManager(new BasicViewManager(new WrappedTextItemPainter(
        null, Alignment.LEFT, 0), null));
    
    makeExceptionList(ex);
    
    getContentPane().setLayout(new BorderLayout());
    
    centerPanel.setLayout(new BorderLayout());
    centerPanel.add(message, BorderLayout.CENTER);
    centerPanel.add(buttonPanel, BorderLayout.SOUTH);
    
    getContentPane().add(centerPanel, BorderLayout.CENTER);
    setBackground(SystemColor.control);
    
    pack();
    
    setEnterOK(true);
    
    displayException(0);
    
    ++showCount;
  }
  
  public ExceptionDialog(Frame frame, String title, Exception ex) {
    this(frame, title, ex, false);
  }
  
  public ExceptionDialog(Frame frame, String title, Exception ex, Component c) {
    this(frame, title, ex, false);
    this.returnFocusComponent = c;
    
  }
  
  public ExceptionDialog(Frame frame, String title, Exception ex,
      boolean modal, Component c) {
    this(frame, title, ex, modal);
    this.returnFocusComponent = c;
  }
  
  protected void processActionEvent(ActionEvent e) {
    super.processActionEvent(e);
    
    if (result != null) {
      if (result.closeDialog) {
        --showCount;
        if (returnFocusComponent != null) {
          returnFocusComponent.requestFocus();
          returnFocusComponent = null;
        }
      }
      if (result.command.equals(NEXT_COMMAND)) {
        if (position < (exceptionVector.size() - 1))
          displayException(++position);
      }
      if (result.command.equals(PREVIOUS_COMMAND)) {
        if (position > 0)
          displayException(--position);
      }
      if (result.command.equals(DETAILS_COMMAND)) {
        if (!showDetails) {
          Dimension dim = getSize();
          Rectangle rect = centerPanel.getBounds();
          remove(centerPanel);
          getContentPane().add(centerPanel, BorderLayout.NORTH);
          getContentPane().add(details, BorderLayout.CENTER);
          details.setBounds(rect.x, rect.y + rect.height, rect.width,
              detailHeight);
          setSize(dim.width, dim.height + detailHeight);
        } else {
          Dimension dim = getSize();
          detailHeight = details.getSize().height;
          remove(details);
          remove(centerPanel);
          getContentPane().add(centerPanel, BorderLayout.CENTER);
          setSize(dim.width, dim.height - detailHeight);
        }
        showDetails = !showDetails;
      }
    }
  }
  
  protected void processWindowEvent(WindowEvent e) {
    // System.err.println("processWindowEvent " + e);
    super.processWindowEvent(e);
    // Needed handle the close gadget on window to close the
    // dialog. Dialog does not have a cancel button.
    if (e.getID() == WindowEvent.WINDOW_CLOSING)
      processActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
          OK_COMMAND));
  }
  
  private void displayException(int pos) {
    enableButton(PREVIOUS_COMMAND, position > 0);
    enableButton(NEXT_COMMAND, position < (exceptionVector.size() - 1));
    
    Exception ex = (Exception) exceptionVector.elementAt(pos);
    String messageString = ex.getMessage();
    // Many java exceptions like NullPointerException have no message.
    //
    if (messageString == null || messageString.length() < 1)
      messageString = ex.getClass().getName();
    message.setText(messageString);
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream(512);
    PrintStream printStream = new PrintStream(byteStream);
    ex.printStackTrace(printStream);
    printStream.flush();
    details.setText(byteStream.toString());
  }
  
  private void makeExceptionList(Throwable ex) {
    exceptionVector = new Vector<Throwable>();
    exceptionVector.addElement(ex);
    if (ex instanceof ChainedException) {
      ExceptionChain chain = ((ChainedException) ex).getExceptionChain();
      while (chain != null) {
        ex = chain.getException();
        exceptionVector.addElement(ex);
        chain = chain.getNext();
      }
    }
    position = 0;
  }
  
  public static int getShowCount() {
    return showCount;
  }
  
  private FieldControl message = new FieldControl();
  private TextAreaControl details = new TextAreaControl();
  private Panel centerPanel = new Panel();
  private boolean showDetails = false;
  private int detailHeight = 200;
  private int position;
  private transient Vector<Throwable> exceptionVector;
  private transient Throwable ex;
  private static int showCount;
  private transient Component returnFocusComponent;
}
