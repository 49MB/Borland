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

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;

import com.borland.dx.dataset.Locate;

/**

  LocatorControl is a utility control that has built in row locating functionality
  when its DataSet property is set.  If its columnName property is set it will
  only locate on that column.  If the columnName property is not set, it will
  locate on the DataSet column that last had focus in a control.  If no column
  had focus in a control, the first column in the DataSet that supports
  locate operations is chosen.

  If the column located on is of String type, the locate operation will performed
  incrementally as characters are typed.  If the search string is all lower
  case, then the search will be performed case insensitive.  If the search
  string is mixed case, then the search will be case sensitive.

  If the column located on is not a String, the locate operation will not be
  performed until the enter key is performed.

  Prior and next matches can be found by pressing up and down keys.

  For automatic prompting use this in conjunction with a LabelControl that
  has the DataSet property set, but not its ColumnName property set.

*/

public class LocatorControl extends TextFieldControl implements java.io.Serializable
{
  public LocatorControl() {
    super();
    enableEvents(AWTEvent.KEY_EVENT_MASK|AWTEvent.FOCUS_EVENT_MASK);
    addFocusListener(new LocatorControl_FocusAdapter(this));
    locateOnly = true;
  }

  // ignore these requests!
  protected void postText() {}
  protected void updateText() {
    Component c = getParent();
    while (c != null && !(c instanceof Window))
      c = c.getParent();
    if (c instanceof Window && ((Window)c).getFocusOwner() != this)
      super.updateText();
  }
  public boolean canSet(boolean startingEdit) {
    return super.canSet(false); // never start an edit session!
  }

  protected void processKeyEvent(KeyEvent e) {
    //Diagnostic.println("LocatorControl.processKeyEvent:  "+" "+e.getKeyChar()+e.getKeyCode()+" "+e.ENTER);

    // Must eat up/down on key pressed otherwise action (go left in the text)
    // will be taken on KeyEvent.VK_UP
    //
    if (e.getID() == KeyEvent.KEY_PRESSED && (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN))
      e.consume(); 
    if (e.getID() == KeyEvent.KEY_RELEASED)
      locatorKeyReleased(e);
    super.processKeyEvent(e);
  }

  /**
   * Invoked when a key has been pressed/released when this locator has focus.
   */
  private void locatorKeyReleased(KeyEvent e) {
    if (getDataSet() != null) {
      int locateOptions;
      if (e.getKeyCode() == KeyEvent.VK_DOWN)
        locateOptions = Locate.NEXT;
      else if (e.getKeyCode() == KeyEvent.VK_UP)
        locateOptions = Locate.PRIOR;
      else
        locateOptions = Locate.FIRST;

      String text = getText();

      if (text.toLowerCase().equals(text))
        locateOptions |= Locate.CASE_INSENSITIVE;

      if (!caseSensitive) {
        text = text.toLowerCase();
        locateOptions |= Locate.CASE_INSENSITIVE;
      }

      try {
        getDataSet().interactiveLocate(text,
                                       getColumnName(),
                                       locateOptions,
                                       e.getKeyCode() == KeyEvent.VK_ENTER);
      }
      catch(Exception ex) {
        com.borland.jbcl.model.DataSetModel.handleException(ex);
      }
    }
  }

  public void setCaseSensitive(boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
  }

  public boolean isCaseSensitive() {
    return caseSensitive;
  }

  private boolean caseSensitive = true;
}

class LocatorControl_FocusAdapter extends FocusAdapter implements java.io.Serializable {
  public LocatorControl_FocusAdapter(LocatorControl lc) {
    this.lc = lc;
  }
  public void focusLost(FocusEvent e) {
    if (lc.getDataSet() != null)
      lc.getDataSet().clearStatus();
  }
  private LocatorControl lc;
}
