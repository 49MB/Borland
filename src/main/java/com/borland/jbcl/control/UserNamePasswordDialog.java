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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;

import com.borland.dx.sql.dataset.Database;
import com.borland.jb.util.Diagnostic;
import com.borland.jb.util.Trace;

public class UserNamePasswordDialog extends ButtonDialog implements java.io.Serializable
{
  //protected UserNamePasswordPanel panel = new UserNamePasswordPanel();
  protected UserNamePasswordPanel panel = null;

  public UserNamePasswordDialog(java.awt.Frame frame, String title, Database db) {
    super(frame, title, null, ButtonDialog.OK_CANCEL);
    super.setCenterPanel(panel = new UserNamePasswordPanel(db));
    // We want the ENTER key to commit the dialog
    setEnterOK(true);
    panel.setDatabase(db);
  }

  void commitValue() {
    try {
      panel.commit();
    }
    catch (Exception ex) {
      Diagnostic.trace(Trace.ConnectionDescriptor, "commitValue: exception"); 
      Diagnostic.printStackTrace(ex);
    }
  }

  public Dimension getPreferredSize() {
    Dimension d = super.getPreferredSize();
//    System.err.println("preferred size = " + d);
    Insets i = getInsets();
//    d.width += i.left + i.right; //8;
//    d.height += i.top + i.bottom; //28;
    //d.width += 8;
    //d.height += 28;
    d.width += 60;
    d.height += 60;
    return d;
  }

  /**
   * Invokes the UserName/Password dialog
   * @return True means OK was chosen, False means Cancel was chosen
   */
  static public boolean invoke(Frame frame, String title, Database db) {
    boolean madeNewFrame = false;
    if (frame == null) {
      frame = new Frame();
      madeNewFrame = true;
    }

    if (title == null)
      title = db.getConnection().getConnectionURL();

    UserNamePasswordDialog dlg = new UserNamePasswordDialog(frame, title, db /*,result*/);
    //System.err.println("showing...");
    dlg.setVisible(true);
    //System.err.println("returned from show");
    //System.err.println(" dlg result is " + dlg.getResult());
    boolean committed = dlg.getResult() == ButtonDialog.OK;
    if (committed)
      dlg.commitValue();
 //   if (madeNewFrame)
//      frame.dispose();

    return committed;
  }
}
