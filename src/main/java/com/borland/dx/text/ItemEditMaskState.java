//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
* This class carries "state" information for a control while it is using an ItemEditMask interface.
* The control owns this information, though it is instantiated by the ItemEditMask.  This allows multiple
* controls to share a common ItemEditMask.
*/
public class ItemEditMaskState implements Serializable
{
  private static final long serialVersionUID = 200L;

  /**
   * The string the control displays to the user.
   */
  public StringBuffer   displayString;    // The buffer which the control should display
            // Note: this buffer must NEVER grow, since we maintain
            // a parallel mapping from char pos to edit region UNLESS we are dealing
            // with a patternless string, in which case it grows on demand
            /**
             * The position of the cursor in the display string
             */
  public int  cursorPos;        // Where the cursor is (input) or should be (output)
  int         variantType;      // The type of variant delivered initially
  transient Object privateObject;    // Optional: any object the EditMasker wants to attach

  /**
   * Constructs an ItemEditMaskState object. The default size of the string buffer is 16 characters,
   *  and the cursor is initially positioned at the beginning of the display string.
   */
  public ItemEditMaskState() {
    this(16, 0);
  }
  /**
   * Constructs an ItemEditMaskState object using the specified display
   * string size and the specified cursor position in the display string.
   */
  public ItemEditMaskState(int size, int cursorPos) {
   displayString = new StringBuffer(size);  // the text which the control should show to the user
   this.cursorPos = cursorPos;      // input and output -- it shows where cursor is or should be
   privateObject = null;      // to be used in any way EditMask implementor wants
  }

  // Serialization support

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    s.writeObject(privateObject instanceof Serializable ? privateObject : null);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    privateObject = s.readObject();
  }
}
