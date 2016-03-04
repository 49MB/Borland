//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.text;

import java.io.Serializable;

import com.borland.dx.dataset.Variant;

/**
 * The StringFormatter component formats and parses string data. It uses the
 * pattern property of ItemFormatter to access the edit/display mask patterns
 * that are used to format and parse the data.
 */
public class StringFormatter extends VariantFormatter implements Serializable
{
  /**
   * Constructs a StringFormatter object.
   */
  public StringFormatter() {
    super();
  }
  
  /**
   * Returns a String representing the given value stored in the supplied
   * object. All reasonable attempts are made to "cast" the type found in the
   * object into the appropriate type specified in the constructor of the
   * implementing classes. A returned empty string indicates a null or empty
   * input value. null means the formatting failed.
   * 
   * @param value
   *          Variant
   * 
   */
  @Override
  public final String format(Variant value) {
    return value.toString();
  }
  
  /**
   * Analyzes the given String and produces as output a Variant containing the
   * approriate value.
   * 
   * @param stringValue
   *          String
   * @param value
   *          Variant
   */
  @Override
  public void parse(String stringValue, Variant value) {
    if (stringValue == null || stringValue.length() == 0)
      value.setUnassignedNull();
    else
      value.setString(stringValue);
  }
  
  /**
   * A high-speed parse that parses directly into a character array
   * 
   * @param variant
   *          Variant
   * @param value
   *          char[]
   * @param offset
   *          integer
   * @param len
   *          integer
   */
  @Override
  public void parse(Variant variant, char[] value, int offset, int len) {
    // Import code relies on this to create an empty string if len == 0
    // ie don't set it to null. (SteveS).
    //
    variant.setString(new String(value, offset, len));
  }
  
  /**
   * Returns the Variant type of StringFormatter, which is always Variant.STRING
   * for StringFormatter
   * 
   * @return integer
   */
  @Override
  public int getVariantType() {
    return Variant.STRING;
  }
  
}
