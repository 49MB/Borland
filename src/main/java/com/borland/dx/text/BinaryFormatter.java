//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.text;

import java.io.Serializable;
import java.text.Format;
import java.util.Locale;

import com.borland.dx.dataset.Variant;
import com.borland.jb.util.Diagnostic;

/**
 * The BinaryFormatter component is the default formatter and parser of
 * Variant.INPUTSTREAM type data. This is a placeholder class that ensures that
 * formatting requests of binary values do not generate an Exception.
 */
public class BinaryFormatter extends VariantFormatter implements Serializable {
  /**
   * Constructs a BinaryFormatter object.
   */
  public BinaryFormatter() {
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
   * @return String
   */
  @Override
  public String format(Variant value) {
    // Diagnostic.fail();
    return "(BLOB)";
  }
  
  /**
   * Analyzes the given String and produces as output an Object containing the
   * appropriate value. A null return value results when stringValue is null or
   * empty.
   * 
   * @param stringValue
   *          String
   * @param value
   *          Variant
   * @throws InvalidFormatException
   */
  @Override
  public void parse(String stringValue, Variant value)
      throws InvalidFormatException {
    Diagnostic.fail();
  }
  
  /**
   * An alternative form of parse() that allows the type of Variant returned to
   * be specified.
   * 
   * @param stringValue
   *          String
   * @param value
   *          Variant
   * @param variantType
   *          integer
   * @throws InvalidFormatException
   */
  @Override
  public void parse(String stringValue, Variant value, int variantType)
      throws InvalidFormatException {
    Diagnostic.fail();
  }
  
  @Override
  public int getVariantType() {
    return Variant.INPUTSTREAM;
  }
  
  @Override
  public String getPattern() {
    Diagnostic.fail();
    return null;
  }
  
  /**
   * Sets the pattern used for parsing and formatting to a new pattern,
   * returning the old pattern. The new pattern must be of the same basic type
   * associated with this type of formatter. For example, if you used a
   * Date/Time pattern in the constructor, you can't switch to a numeric pattern
   * as each basic pattern type has its own data-dependent format() and parse()
   * methods.
   * 
   * @param pattern
   *          String
   * @return String
   */
  @Override
  public String setPattern(String pattern) {
    Diagnostic.fail();
    return null;
  }
  
  /**
   * Some formatter classes define special objects for their own use. This
   * method allows them to be set. You must know the internal details of the
   * Format subclass being used to use setSpecialObject().
   * <p>
   * The returned value is the prior value of the object.
   * 
   * @param objType
   *          int
   * @param obj
   *          Object
   * @return Object
   */
  @Override
  public Object setSpecialObject(int objType, Object obj) {
    Diagnostic.fail();
    return null;
  }
  
  /**
   * Returns the value of the specified special object. Some formatter classes
   * define special objects for their own use.
   * <p>
   * You must know the internal details of the Format subclass being used to use
   * getSpecialObject().
   * 
   * @param objType
   *          integer
   * @return Object
   */
  @Override
  public Object getSpecialObject(int objType) {
    Diagnostic.fail();
    return null;
  }
  
  @Override
  public Locale getLocale() {
    Diagnostic.fail();
    return null;
  }
  
  @Override
  public Format getFormatObj() {
    Diagnostic.fail();
    return null;
  }
}
