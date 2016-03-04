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

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

/**
 * MonitorButton is a button you can add to the UI of your database application
 * to display JDBC Monitor log output.  When the button is pressed, the
 * JDBC Monitor appears as a separate, non-modal dialog box.
 *<P>
 * MonitorButton has two properties you can set to customize the JDBC Monitor.
 * Set the <I>outputEnabled</I> property true or false to specify whether the
 * JDBC Monitor dialog initially has output logging enabled or disabled.
 * At runtime, the user can check the dialog box's checkbox to toggle log
 * output on or off.
 *<P>
 * The <I>maxLogSize</I> property is used to set the default size of the
 * output log displayed in the JDBC Monitor's TextArea component.  Because
 * of the large amount of log data generated, in some environments the
 * TextArea component may crash.  Use <I>maxLogSize</I> to limit the size
 * of the log appropriately for your environment.  The default size of the
 * log is 8k.
 */
public class MonitorButton extends JButton implements ActionListener{

  private boolean outputEnabled = true;
  private int maxLogSize = 8096;

  private MonitorPanel mw = null;
  private Dialog d = null;
  public MonitorButton() {
    this.addActionListener(this);
  }

  public void actionPerformed(ActionEvent e){
    Object o = e.getSource();
    if ((Object) this == o) {
      Frame f = null;
      Container C = this.getParent();
      while (C != null) {
        if (C instanceof Frame) {
          f = (Frame) C;
          break;
        }
        C = C.getParent();
      }
      MonitorDialog d = new MonitorDialog(f, Res._MonitorTitle, false);     
      mw = new MonitorPanel();
      d.getContentPane().add(mw, null);
      //      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      d.setSize(400, 300);
      d.setLocation(100,100);
      mw.setOutputEnabled(isOutputEnabled());
      mw.setMaxLogSize(getMaxLogSize());
      d.setVisible(true);
    }
  }
  public void setOutputEnabled(boolean outputEnabled) {
    this.outputEnabled = outputEnabled;
  }

  public boolean isOutputEnabled() {
    return outputEnabled;
  }

  /**
   * In some environments, an unlimited log file size
   * will crash the TextArea used to display the log.
   * Use setMaxLogSize() to adjust the max log size
   * appropriate for your environment.  The default
   * size is 8K.
   */
  public void setMaxLogSize(int maxLogSize) {
    this.maxLogSize = maxLogSize;
  }

  public int getMaxLogSize() {
    return maxLogSize;
  }

}
