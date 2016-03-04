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
import java.awt.Event;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Locale;

import com.borland.dx.dataset.ColumnVariant;
import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.ValidationException;
import com.borland.dx.dataset.Variant;
import com.borland.dx.text.Alignment;
import com.borland.dx.text.InvalidFormatException;
import com.borland.dx.text.ItemEditMask;
import com.borland.dx.text.ItemEditMaskState;
import com.borland.dx.text.ItemEditMaskStr;
import com.borland.dx.text.ItemFormatter;
import com.borland.jb.util.Diagnostic;
import com.borland.jb.util.Trace;
import com.borland.jbcl.model.ItemEditSite;
import com.borland.jbcl.model.ItemEditor;
import com.borland.jbcl.util.JbclUtil;


public class MaskableTextItemEditor extends TextField implements ItemEditor, Serializable
{
  private transient ItemEditMask ems;
  private ItemEditMaskState state;
  private boolean hookedEvents;
  private boolean editingNow;
  private DataSet ds;
  private String startingText;
  private transient Object startingValue; // ignore for serialization
  boolean changed;
  boolean allSelected;

  transient ItemFormatter formatter;
  int alignment = Alignment.LEFT | Alignment.MIDDLE;
  //ColumnView view;
  int dataType;
//  long whenTyped;
//  boolean keyTypedEnabled;

  public MaskableTextItemEditor() {
    super();
    //ems = null;               // default constructor behavior is zero fill
    //state = null;             // ditto
    //hookedEvents = false;     // ditto
    //view=null;                // ditto
    //keyTypedEnabled = false;  // ditto
    //Diagnostic.addTraceCategory(Trace.MaskableEditor);
    Diagnostic.trace(Trace.MaskableEditor, "Maskable: constructor with no formatter or editmasker");
   }

  public MaskableTextItemEditor(int alignment, Insets margins) {
    this(alignment, margins, null, null);
  }

  public MaskableTextItemEditor(ItemFormatter formatter, ItemEditMask editMasker) {
    this();
    this.formatter = formatter;
    setEditMasker(editMasker);
  }

  public MaskableTextItemEditor(int alignment,
                                Insets margins,
                                ItemFormatter formatter,
                                ItemEditMask editMasker) {
    this();
    this.alignment = alignment;
    this.formatter = formatter;
    setEditMasker(editMasker);
  }

// -------- Implementation methods for ItemEditor ---------------------

  public Object getValue() {
  Diagnostic.trace(Trace.MaskableEditor, "MaskableTextItemEditor.getValue(" + getText() + ")");
  //System.err.println("MaskableTextItemEditor.getValue(" + getText() + ")");
    Variant value = new Variant();
    //
    // If not edit mask, try to use formatter.  If no formatter, merely return string
    //
    if (ems == null) {
      String s = getText();                                   // No editmask, get the text as it is

      // Optimization and robustness measure:
      // If the text is the same as when we started, return original object.
      // This protects against potential "cannot parse what can format" bugs
      if (startingText != null && s.equals(startingText)) {
        //System.err.println(" returning startingValue " + startingValue);
        return startingValue;
      }

      if (formatter != null) {                                // However, if there is a formatter, we will need
        try {
//          formatter.parse(s, v);
          value = (Variant) formatter.parse(s);
        }
        catch (Exception e) {
          Diagnostic.printStackTrace(e);
          if (e instanceof InvalidFormatException) {
            state.cursorPos = ((InvalidFormatException)e).getErrorOffset();               // error adjusts cursor
          }
          updateSelection();                                  // and shows it to user
          if (ds != null) {
            //System.err.println("calling handleException...");
            //com.borland.jbcl.model.DataSetModel.handleException(ds, this, e);
            handleException(e);
          }
//          else System.err.println(" no ds");
          throw new IllegalStateException();
        }
        Diagnostic.check((value instanceof Variant), "Maskable.getValue() must be variant!");
        return (Variant) value;
      }
      value.setString(s);
      return value;
    }
    //
    // We have an editmask -- get value from editmask
    //
    else {
        // Note: we detect changes by the keys we fed the edit mask, not its current text,
        // since it may have changed.
        if (!changed) {
          //System.err.println(" returning startingValue " + startingValue);
          return startingValue;
        }

        try {
          //System.err.println(" calling getFinalValue");
        ems.getFinalValue(state, value);                         // else try to parse according to edit mask
        return value;
      }
      catch (Exception e) {
        Diagnostic.printStackTrace(e);
        if (e instanceof InvalidFormatException) {
          state.cursorPos = ((InvalidFormatException)e).getErrorOffset();                // error adjusts cursor
        }
        updateSelection();                                   // and shows it to user
        if (ds != null)
          handleException(e);
          //com.borland.jbcl.model.DataSetModel.handleException(ds, this, e);
        throw new IllegalStateException();
      }
    }
  }

  public Component getComponent() { return this; }

  public void startEdit(Object data, Rectangle bounds, ItemEditSite editSite) {
    Diagnostic.trace(Trace.MaskableEditor, "MaskableTextItemEditor.startEdit()");

    if (data != null && data != startingValue && data instanceof Variant) {
      //System.err.println("cloning " + data.hashCode() + ": " + data + "...");
      startingValue = new Variant();
      ((Variant)startingValue).setVariant((Variant) data);
      //System.err.println(" into " + startingValue.hashCode() + ": " + startingValue);
    }

    else startingValue = data;
    startingText = null;
    changed = false;

    // When we are used to edit Variants belonging to DataSets, we capture
    // that DataSet so we can report any errors
    ds = (data != null && data instanceof ColumnVariant)
           ? ((ColumnVariant) data).getDataSet()
           : null;

    if (ems == null) {
      try {
      if (data == null)
        setText("");
      else if (formatter != null && data instanceof Variant) {
        // always expand default year pattern to 4 digits when displaying default dates (no editmask and no displaymask)
        String oldPattern = formatter.getPattern();
        String widePattern = null;
        if (ems == null &&   // no editmask
            data != null && data instanceof ColumnVariant && ((ColumnVariant) data).getColumn().getDisplayMask() == null && // no displaymask
            formatter.getFormatObj() != null && formatter.getFormatObj() instanceof java.text.DateFormat) { // displaying a date
          int yearMaskIndex = -1;
          if ((yearMaskIndex = oldPattern.indexOf('y')) != -1) {
            int yearMaskCount = 1;
            int patternLength = oldPattern.length();
            while ((yearMaskIndex + yearMaskCount) < patternLength && oldPattern.charAt(yearMaskIndex + yearMaskCount) == 'y') {
              yearMaskCount++;
            }
            if (yearMaskCount < 4) {
              widePattern = oldPattern.substring(0, yearMaskIndex) + "yyyy" +
                ((yearMaskIndex + yearMaskCount < patternLength) ? oldPattern.substring(yearMaskIndex + yearMaskCount) : "");
            }
          }
        }
        if (widePattern != null) {
          formatter.setPattern(widePattern);
        }
        setText(formatter.format(data));
        if (widePattern != null) {
          formatter.setPattern(oldPattern);
        }
      }
      else
        setText(data.toString());
      }
      catch (Exception ex) {
        Diagnostic.printStackTrace(ex);
        setText("");
      }
    }
    else {
      if (data == null || !(data instanceof Variant)) {
        Variant v = new Variant();
        if (data == null)
          v.setNull(Variant.ASSIGNED_NULL);
        else v.setString(data.toString());
        data = (Object) v;
      }
      state = ems.prepare((Variant)data);
      allSelected = true;
      updateDisplay();
    }

    editingNow = true;

    startingText = getText();

    if (bounds != null)
      setBounds(bounds.x, bounds.y, bounds.width, bounds.height);

    if (editSite != null) {
      setBackground(editSite.getBackground());
      setForeground(editSite.getForeground());
      setFont(editSite.getFont());
    }

    setVisible(true);

    // We are going to try to determine the best place to set the insertion point
    //
    String text = getText();
    Point clickPoint = editSite != null ? editSite.getEditClickPoint() : null;
    int position = 0;
    if (clickPoint == null && text != null) {
      // The editing was initiated without a mouse click. Here we will set the insertion point
      // at the end of the string
      Diagnostic.trace(Trace.MaskableEditor, "StateCursorPos = " + (state != null ? state.cursorPos : text.length()));
      position = (state == null) ? text.length() : state.cursorPos;
    }
    else if (text != null) {
      int xClick = clickPoint.x - bounds.x;
      position = JbclUtil.findInsertPoint(xClick, text, clickPoint, getFont());
    }

    if (ems == null) {
      if (clickPoint != null)
        select(position, position);
      else
        select(0, position);
    }
    //
    // If an edit mask is active, suggest this as the insertion point and let the edit mask adjust
    //
    else {
      state.cursorPos = position;
      ems.move(state, Event.MOUSE_DOWN);
      updateDisplay();
    }
    requestFocus();
  }

  public void changeBounds(Rectangle bounds) {
    Diagnostic.trace(Trace.MaskableEditor, "MaskableTextItemEditor.changeBounds()");
    setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
  }


  // internal method to centralize handling of exception.
  // By going through the DataSetException handler, we will
  // send our messages to any listeners or alternatively go
  // out to a dialog
  private void handleException(Exception x) {
    // Improve on empty strings
    //System.err.println("handleException...");
    //x.printStackTrace(System.err);


/*
    String message = x.getMessage();
    if (message == null || message.length() == 0) {
System.err.println(" changing message text");
      message = Res._InvalidCharacters;
    }
      //if (x instanceof NumberFormatException)
      //  x = new InvalidFormatException(Res._InvalidCharacters);
*/
    //RC! Wrap all exceptions inside Validation Exception to give better text
    try {
      ValidationException.invalidFormat(x, (String)null, null);
    }
    catch(ValidationException ex) {
      com.borland.jbcl.model.DataSetModel.handleException(ds, this, ex);
    }
  }

  public boolean canPost() {
    Diagnostic.trace(Trace.MaskableEditor, "MaskableTextItemEditor.canPost(" + getText() + ")");
    //System.err.println("canPost()");

    // If being used without an EditMask interface, the only grounds for blocking
    // a post is when the item cannot be parsed.  So parse it now and see...
    if (ems == null) {                    //
      if (formatter == null)              // if no formatter, always succeed
        return true;
      String s = getText();               // otherwise, see if can coerce to

      // Optimization and robustness measure:
      // If the text is the same as when we started editing, guarentee the post
      if (startingText != null && s.equals(startingText))
        return true;

      // Something is different -- try to parse (error throws exception)
      try {
        formatter.parse(s);
      }

      // A parse exception is grounds for informing the DataSet (if we have one)
      // what went wrong
      catch (Exception x) {
        Diagnostic.printStackTrace(x);
        handleException(x);
        //System.err.println(" exception in canPost, ds is " + ds);
/*        if (ds != null) {
          String message = x.getMessage();
          if (message == null || message.length() == 0)
            message = Res._InvalidCharacters;
          //if (x instanceof NumberFormatException)
          //  x = new InvalidFormatException(Res._InvalidCharacters);
          x = new ValidationException(ValidationException.INVALID_FORMAT, message, null);

          com.borland.jbcl.model.DataSetModel.handleException(ds, this, x);
        }
*/
        return false;
      }
      return true;
    }

    // If operating under an edit mask, let the edit mask interface determine
    // whether the user is complete.  Failure logs a generic message to the DataSet.
    else {
      String msg = null;

      if (!changed)                        // if user made no change, permit the nop post
        return true;

      boolean result = false;
      try {
        result = ems.isComplete(state);    // let edit mask perform final validation
      }
      catch (Exception ex) {
        Diagnostic.printStackTrace(ex);
      }
      updateSelection();                         // be sensitive to changes in cursor position
      if (result) {
        try {
          ems.getFinalValue(state, new Variant());
        }
        catch (Exception e) {
          Diagnostic.printStackTrace(e);
          if (e instanceof InvalidFormatException)
            msg = e.getMessage();
          result = false;
        }
      }
      if (!result && ds != null) {
        if (msg == null)
          msg = Res._DataEntryIncomplete;
        handleException(new InvalidFormatException(msg));
      }
//        com.borland.jbcl.model.DataSetModel.handleException(ds, this, new InvalidFormatException(Res._DataEntryIncomplete));
      return result;
    }
  }

  public void endEdit(boolean posted) {
    Diagnostic.trace(Trace.MaskableEditor, "MaskableTextItemEditor.endEdit(" + getText() + ")");
    editingNow = false;
    startingValue = null;
    startingText = null;
    changed = false;
  }

// -----------------------------------------------------------------------

  public void setEditMask(String editMask, int variantType, Locale locale) {
    setEditMasker(new ItemEditMaskStr(editMask, null, variantType, locale));
  }

  public void setEditMasker(ItemEditMask editMasker) {
    Diagnostic.trace(Trace.MaskableEditor, "setEditMasker - " + editMasker);
    ems = editMasker;
    state = null;
    if (ems != null)
      hookEvents();
    if (editingNow)                     // if change edit mask while editing, we conform to it
      startEdit(null, null, null);      // but lose any value entered to date
  }

// -------------- Internal methods for direct event interception ------------------

  protected void processKeyEvent(KeyEvent e) {
    Diagnostic.trace(Trace.MaskableEditor, "MaskableTextItemEditor: processKeyEvent - " + e);
    //System.err.println("MaskableTextItemEditor: processKeyEvent - " + e);

    Diagnostic.trace(Trace.MaskableEditor, "keyCode = " + (int) e.getKeyCode()
                     + " and char = " + (int)e.getKeyChar());
    //System.err.println("keyCode = " + (int) e.getKeyCode() + " and char = " + (int)e.getKeyChar());  //NORES

    if (hookedEvents /*&& whenTyped != oldWhen*/) {
      handleKeyEvent(e);
    }
    super.processKeyEvent(e);
  }

  protected void processFocusEvent(FocusEvent e) {
    Diagnostic.trace(Trace.MaskableEditor, "MaskableTextItemEditor: FocusEvent - " + e);
    super.processFocusEvent(e);
    maskControl_gotFocus();
  }

  // TODO appears to be dead
  protected void processMouseEvent(MouseEvent e) {
//    Diagnostic.trace(Trace.MaskableEditor, "MaskableTextItemEditor: MouseEvent - " + e);
    if (hookedEvents && (ems != null) && e.getID() == MouseEvent.MOUSE_RELEASED)
      updateSelection();
    else if (hookedEvents && (ems != null) && e.getID() == MouseEvent.MOUSE_CLICKED)
      handleMouseClicked(e);
    else super.processMouseEvent(e);
  }

  // TODO appears to be dead
  protected void processMouseMotionEvent(MouseEvent e) {
//    Diagnostic.trace(Trace.MaskableEditor, "MaskableTextItemEditor: MouseMotionEvent - " + e);
    super.processMouseMotionEvent(e);
  }

/*
  public boolean keyUp(Event e, int key) {
    Diagnostic.trace(Trace.MaskableEditor, "keyUp");
    return true;
  }
  public boolean keyDown(Event e, int key) {
    Diagnostic.trace(Trace.MaskableEditor, "keyDown");
    return true;
  }
*/

//
// ----------------- Private methods associated with EditMask event hooking ----------------------
//
  void maskControl_gotFocus() {
    Diagnostic.trace(Trace.MaskableEditor, "Got focus, hookedEvents, state: true, "+hookedEvents+", "+state);
    if (hookedEvents) {
      if (state == null)
        startEdit(null, null, null);
    }
    updateSelection();
  }

  void updateSelection() {

    if (state != null) {
      Diagnostic.trace(Trace.MaskableEditor, "updateSelection(" + state.cursorPos + ")");
      Diagnostic.trace(Trace.MaskableEditor, " buffer is " + state.displayString.length() + " and text is " + getText().length());
      if (allSelected)
        select(0, state.displayString.toString().length());
      else
        select(state.cursorPos, state.cursorPos + 1);
    }
  }

  void updateDisplay() {
    if (state != null) {
      setText(state.displayString.toString());
      Diagnostic.trace(Trace.MaskableEditor, "updateDisplay(" + state.displayString.toString() + ")");
      updateSelection();
    }
  }

  void deleteSelection(boolean preserveAtCursor) {
    int selStart = getSelectionStart();
    int selEnd = getSelectionEnd();
    int nChars = selEnd - selStart;
    if (nChars < 0) {
      selStart = selEnd;
      nChars = -nChars;
    }
    if (nChars > 1 || !preserveAtCursor) {
      //Diagnostic.println("deleting " + nChars + " chars starting at pos " + selStart);
      ems.delete(state, selStart, nChars);
    }
    allSelected = false;
    changed = true;
  }

  private void handleKeyEvent(KeyEvent e) {
    int id      = e.getID();
    int keyCode = e.getKeyCode();

    if (id != KeyEvent.KEY_PRESSED && id != KeyEvent.KEY_TYPED)
      return;

    allSelected = false;

    switch (keyCode) {
      case KeyEvent.VK_HOME:
      case KeyEvent.VK_END:
      case KeyEvent.VK_LEFT:
      case KeyEvent.VK_RIGHT:
        Diagnostic.trace(Trace.MaskableEditor, "move key");
        ems.move(state, keyCode);
        updateSelection();
        break;
      case KeyEvent.VK_BACK_SPACE:
        Diagnostic.trace(Trace.MaskableEditor, "backspace");
        if (ems.move(state, KeyEvent.VK_LEFT)) {
          updateSelection();
          deleteSelection(false);
          updateDisplay();
        }
        break;
      case KeyEvent.VK_DELETE:
        //System.err.println("virtual DEL");
        Diagnostic.trace(Trace.MaskableEditor, "delete key");
        deleteSelection(false);
        ems.move(state, KeyEvent.VK_RIGHT);
        updateDisplay();
        break;
      case KeyEvent.VK_TAB:
      case KeyEvent.VK_ENTER:
        if (id != KeyEvent.KEY_TYPED)
          return;
        //System.err.println("ENTER ...");
        Diagnostic.trace(Trace.MaskableEditor, "commit key");
        canPost();
        break;
      case KeyEvent.VK_ESCAPE:
        Diagnostic.trace(Trace.MaskableEditor, "esc key");
        startEdit(startingValue, null, null);
        changed = false;
        break;
      default:
        if ((e.isControlDown() && keyCode == KeyEvent.VK_V) ||
            (e.isShiftDown() && keyCode == KeyEvent.VK_INSERT)) {
          Clipboard clipboard = getToolkit().getSystemClipboard();
          Transferable content = clipboard.getContents(this);
          if (content != null) {
            try {
              String data = (String) (content.getTransferData(DataFlavor.stringFlavor));
              if (data.length() > 0) {
                deleteSelection(true);
              }
              for (int i = 0, end = data.length(); i < end; i++) {
                ems.insert(state, data.charAt(i));
              }
              changed = true;
              updateDisplay();
            }
            catch (Exception ex) {
              getToolkit().beep();
            }
          }
          e.consume();
          return;
        }
        char ch = e.getKeyChar();
        if (id != KeyEvent.KEY_TYPED || ch >= 0 && ch < ' ')
          return;
        Diagnostic.trace(Trace.MaskableEditor, "insert key");
        deleteSelection(true);
        ems.insert(state, ch);
        updateDisplay();
        break;
    }
    e.consume();
  }

  // The mouseClicked handler is concerned mostly with keeping our single character edit region
  // on a legal character position.

  private void handleMouseClicked(MouseEvent e) {
    Point clickPoint = e.getPoint();
    int xClick = clickPoint.x;
    String text = state.displayString.toString();
    state.cursorPos = JbclUtil.findInsertPoint(xClick, text, clickPoint, getFont());
    ems.move(state, MouseEvent.MOUSE_CLICKED);
    updateSelection();
  }

  private void hookEvents() {
    Diagnostic.trace(Trace.MaskableEditor, "Maskable: hooking events");
    if (!hookedEvents) {
      enableEvents(AWTEvent.FOCUS_EVENT_MASK |
                   AWTEvent.KEY_EVENT_MASK |
                   AWTEvent.MOUSE_EVENT_MASK |
                   AWTEvent.MOUSE_MOTION_EVENT_MASK);
//      setEchoChar((char) 0);

      // Note: the only way I could stop the TextComponent base class from echoing characters was to
      // declare it non-editable (this gives us complete control over which keys get in).  This MAY
      // not be platform independent.  It also has the side-effect of making the field gray
//%%%      setEditable(false);
//%%%      setBackground(SystemColor.window /*Color.white*/); // undo the gray caused by editable = false
      hookedEvents = true;
    }
  }

  /**
   * Returns the formatter object used
   */
  public ItemFormatter getFormatter() {
    return formatter;
  }

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    Hashtable hash = new Hashtable(2);
    if (ems instanceof Serializable)
      hash.put("e", ems);
    if (formatter instanceof Serializable)
      hash.put("f", formatter);
    s.writeObject(hash);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    Hashtable hash = (Hashtable)s.readObject();
    Object data = hash.get("e");
    if (data instanceof ItemEditMask)
      ems = (ItemEditMask)data;
    data = hash.get("f");
    if (data instanceof ItemFormatter)
      formatter = (ItemFormatter)data;
  }
}

