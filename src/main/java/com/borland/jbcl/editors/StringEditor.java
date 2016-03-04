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
package com.borland.jbcl.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;

import com.borland.jb.util.Diagnostic;
import com.borland.jb.util.FastStringBuffer;

public class StringEditor implements PropertyEditor
{
  public void setValue(Object o) {
    //System.err.println("setValue(" + o + ")");
    value = o;
    fire();
  }

  public Object getValue() {
    //System.err.println("getValue() returning " + value);
    return value;
  }

  public boolean isPaintable() {
    return false;
  }

  public void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box) {
    // Silent noop.
  }

  /**
   * getAsText now takes buffer through full round trip (text-->source-->text)
   * This emulates the round trip the string will take when persisted to disk and read back in.
   * It will cause escape sequences that can turn into printable characters to do that now.
   */
  public String getAsText() {
    //return (value == null) ? null : FastStringBuffer.sourceToText(textToSource(value)).toString();
    String s = (value == null) ? null : rawTextToDisplay(value);
    //System.err.println("getAsText() returning " + s);
    return s;
  }

  public String getJavaInitializationString() {
    String s = textToSource(value);
    //System.err.println("getJavaString returning " + s);
    return s;
  }

  /**
   * This method prepares a String to be legal to appear
   * in source code.  It is sensitive to escape sequences.
   * Do NOT use this for filenames, or you may get single
   * backslashes when it looks like an escape sequence.
   */
  public static String textToSource(Object text) {
    if (text instanceof String)
    return FastStringBuffer.textToSource((String)text, false).toString();
    if (text == null)
      return "null"; 
    return text.toString();
  }

  public void setAsText(String text) throws java.lang.IllegalArgumentException {
    //System.err.println("setAsText(" + text + ")");
    value = (text == null) ? null : displayTextToRaw(text);
  }

  public String[] getTags() {
    return null;
  }

  public java.awt.Component getCustomEditor() {
    return null;
  }

  public boolean supportsCustomEditor() {
    return false;
  }

  // Some notes about the workings here...
  // Whenever we need to edit a string, we convert from "raw" form (which has ANSI values for
  // all characters, including nonprintables) to "display" form (where nonprintable characters
  // become a hex or logical value inside "<>".
  // When we are asked to generate source code, we turn raw ANSI values into logical
  // Java escape codes (e.g. '\u0061' or '\n')

  // notice NL and LF are redundant -- done to allow some flexibility for those
  // who like <CR><LF>
  private static String[] logicalChars = { "<TAB>", "<CR>", "<NL>", "<FF>", "<<>", "<LF>"}; 
  private static char[] physicalChars =  { '\t',    '\r',   '\n',   '\f',   '<',   '\n' };

  /**
   * This method tests whether a character is displayable in the current
   * system code page.  It has been created with consultation from INTL.
   */
  public static boolean inCurrentCodePage(char ch) {
    //System.err.println("inCurrentCodePage(" + ((int) ch) + ")");
    String src = String.valueOf(ch);
    byte[] bts = src.getBytes();
    String dst = new String(bts);
    //System.err.println(" returning " + ((boolean)(src.charAt(0) == dst.charAt(0))));
    return src.charAt(0) == dst.charAt(0);
  }

  /**
   * Converts String from "raw format" (i.e. nonprintable chars, etc., such as "\tHello")
   * into Display/Edit form (e.g. "<TAB>hello")
   */
  public static String rawTextToDisplay(Object s) {
    if (!(s instanceof  String))
      return s.toString();
    FastStringBuffer src = new FastStringBuffer((String)s);
    FastStringBuffer dst = new FastStringBuffer();

    for (char ch = src.firstChar(); ch != FastStringBuffer.NOT_A_CHAR; ch = src.nextChar()) {

      // See if we have a logical name for this char
      int i;
      for (i = 0; i < physicalChars.length; ++i)
        if (ch == physicalChars[i]) {
          dst.append(logicalChars[i]);
          break;
        }
      if (i < physicalChars.length)
        continue;

    // Characters from 32 to 256 just go through unprocessed
      if ((int)ch > 31 && inCurrentCodePage(ch) /*&& (int) ch < 256*/) {
        dst.append(ch);
        continue;
      }

      // Finally, if not a logical char, just show the character in hex
      dst.append("<" + Integer.toString((int)ch, 16) + ">");
    }

    return dst.toString();
  }

  /**
   * Converts String from "display format" (e.g. "<CR><NL>Hello") to "raw format" (i.e. raw characters)
   */
  public static String displayTextToRaw(String s) {
    FastStringBuffer src = new FastStringBuffer(s);
    FastStringBuffer dst = new FastStringBuffer();

    for (char ch = src.firstChar(); ch != FastStringBuffer.NOT_A_CHAR; ch = src.nextChar()) {

      // All characters outside <> just copy through unmodified
      if (ch != '<') {
        //System.err.println("displayToRaw: appending: " + ch + " which is integer " + (int) ch);
        dst.append(ch);
        continue;
      }

      // Anything inside <> is either a logical char (<CR>) or hex (<0061>)
      int offset = src.getOffset();
      FastStringBuffer log = new FastStringBuffer();
      for (ch = src.nextChar(); ch != FastStringBuffer.NOT_A_CHAR; ch = src.nextChar()) {
        if (ch != '>') {
          //System.err.println("displayToRaw: buffering<>: " + ch + " which is integer " + (int) ch);
          log.append(ch);
          continue;
        }
        String logStr = "<" + log.toString() + ">";
        //System.err.println(" logical str is: " + logStr);
        int i;
        for (i = 0; i < logicalChars.length; ++i) {
          if (logStr.equalsIgnoreCase(logicalChars[i])) {
            //System.err.println("  which maps to physical[" + i + "] which is integer " + (int) physicalChars[i]);
            dst.append(physicalChars[i]);
            break;
          }
        }
        if (i < logicalChars.length)
          break;

        String logStrNumber = log.toString();
        try {
          //System.err.println("  parsing " + logStr + " yields " + Integer.valueOf(logStr,16).toString());
          dst.append((char)Integer.valueOf(logStrNumber, 16).intValue());
        }

        // If cannot recognize number inside angle brackets, just quote them verbatim
        catch (Exception ex) {
          Diagnostic.printStackTrace(ex);
          dst.append(logStr);
        }
        break;      // break back to main scan loop
      }

      // Running off end of string with no matching angle bracket reinterprets
      // the leading angle bracket as plain angle bracket
      if (ch == FastStringBuffer.NOT_A_CHAR) {
        dst.append('<');
        src.setOffset(offset);
      }
    }
    return dst.toString();
  }


  private void fire() {
    if (listener != null)
      listener.propertyChange(new PropertyChangeEvent(this, "StringEditor???", null, value)); 
  }

  public void addPropertyChangeListener(PropertyChangeListener l) {
    listener = l;
  }

  public void removePropertyChangeListener(PropertyChangeListener l) {
    listener = null;
  }

  private PropertyChangeListener listener;
  protected Object value;
}
