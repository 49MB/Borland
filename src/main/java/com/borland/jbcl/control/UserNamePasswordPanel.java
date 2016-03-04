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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.SQLException;

import javax.swing.JComponent;

import com.borland.dx.dataset.DataSetException;
import com.borland.dx.sql.dataset.ConnectionDescriptor;
import com.borland.dx.sql.dataset.Database;
import com.borland.jb.util.Diagnostic;
import com.borland.jb.util.Trace;

public class UserNamePasswordPanel extends JComponent implements FocusListener, java.io.Serializable
{
  ConnectionDescriptor connectionDescriptor;
  Database db;
  boolean firstFocus;

  public UserNamePasswordPanel() {
    super();
    Diagnostic.trace(Trace.ConnectionDescriptor, "UserNamePasswordPanel constructor"); 
    try {
      init();
    }
    catch (Exception x) {
      Diagnostic.printStackTrace(x);
    }
  }

  public UserNamePasswordPanel(Database db) {
    this.db = db;
    connectionDescriptor = db.getConnection();

    Diagnostic.trace(Trace.ConnectionDescriptor, "UserNamePasswordPanel constructor"); 
    try {
      init();
    }
    catch (Exception x) {
      Diagnostic.printStackTrace(x);
    }

  }

  public void setDatabase(Database db) {
    this.db = db;
    connectionDescriptor = db.getConnection();
  }

  TextField userNameField;
  TextField passwordField;

  void init() {
    Diagnostic.trace(Trace.ConnectionDescriptor, "UserNamePasswordPanel.init()"); 

    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);

    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 140;
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;              // undo the custom settings from above
    gridBagConstraints.weightx = 1.0;
    //gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new Insets(0, 10, 0, 10);

    Label labelUser = new Label(Res._UserName);     
    labelUser.setAlignment(0);
    add(labelUser);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = GridBagConstraints.SOUTHWEST;       // anchor close to field below and left align
    //gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
    //gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
    gridBagLayout.setConstraints(labelUser, gridBagConstraints);

    userNameField = new TextField("", 12);
    add(userNameField);
    gridBagConstraints.gridx = GridBagConstraints.RELATIVE;
    gridBagConstraints.gridy = 2;
    //gridBagConstraints.gridwidth = 1
    gridBagConstraints.anchor = GridBagConstraints.WEST;
    gridBagLayout.setConstraints(userNameField, gridBagConstraints);
    userNameField.setText(connectionDescriptor.getUserName());

    Label labelPassword = new Label(Res._Password);     
    labelPassword.setAlignment(0);
    add(labelPassword);
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = GridBagConstraints.SOUTHWEST;
    //gridBagConstraints.gridwidth = 1;
    gridBagLayout.setConstraints(labelPassword, gridBagConstraints);

    passwordField = new TextField("", 12);
    add(passwordField);
    gridBagConstraints.gridy = 5;
    //gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
    gridBagConstraints.anchor = GridBagConstraints.WEST;
    gridBagLayout.setConstraints(passwordField, gridBagConstraints);

    passwordField.setEchoChar('*');                                     // password field shows "****" only

    userNameField.setEnabled(true);
    passwordField.setEnabled(true);

    firstFocus = false;
    userNameField.addFocusListener(this);
  }

  /**
   * This method is called to commit the changes to the ConnectionDescriptor
   * and to assign it back into the Database.  It does not try to open
   * the Database at this level.
   */
  void commit() throws DataSetException, SQLException {
    connectionDescriptor.setUserName(userNameField.getText());
    connectionDescriptor.setPassword(passwordField.getText());

//    System.err.println(">>> commit: writing connection: " + connectionDescriptor);
//    System.err.println(" into db: " + db);
    try {
      db.setConnection(connectionDescriptor);
    }
    catch (Exception ex) {
      Diagnostic.printStackTrace(ex);
    }
  }

    /**
     * Invoked when a component gains the keyboard focus.
     */
    public void focusGained(FocusEvent e) {

      // The very first time we acquire focus, move onto the password
      // field if the userName is already filled in
      if (!firstFocus) {
        firstFocus = true;
        String userName = userNameField.getText();
        if (userName != null && userName.length() > 0) {
          passwordField.requestFocus();
        }
      }
    }

    /**
     * Invoked when a component loses the keyboard focus.
     */
    public void focusLost(FocusEvent e) {}
}
