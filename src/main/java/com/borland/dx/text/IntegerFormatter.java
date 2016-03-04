//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.text;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;

import com.borland.dx.dataset.Variant;

/**
 * The IntegerFormatter class formats and parses data of type int.
 */
public class IntegerFormatter extends VariantFormatter implements Serializable {
  /**
   *Constructs an IntegerFormatter object.
   */
  public IntegerFormatter(int type) {
    super();
    this.type = type;
  }
  
  /**
   * Returns a string representation of the int value stored in Variant. All
   * reasonable attempts are made to "cast" the type found in the object to an
   * int.
   * 
   * @param value
   *          Variant
   * 
   */
  @Override
  public final String format(Variant value) {
    return (value == null || value.isNull()) ? "" : new Integer(value
        .getAsInt()).toString();
  }
  
  // ! TODO <rac> Consider hand coding an override for this one too...
  // ! public FastStringBuffer format(Variant value, FastStringBuffer fsb);
  // ! for JB 3.0, catch possible NumberFormatException, and rethrow as
  // InvalidFormatException (dcy BTS 19777)
  /**
   * Analyzes the given String and produces as output a Variant containing the
   * appropriate value.
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
    if (stringValue == null || (stringValue = stringValue.trim()).length() == 0) {
      value.setUnassignedNull();
      return;
    }
    try {
      int intValue = NumberFormat.getIntegerInstance().parse(stringValue)
          .intValue();
      setFromInt(value, type, intValue);
    } catch (NumberFormatException e) {
      // ! confirmed this message concatentation was okay with intl dept (dcy)
      if (!stringValue.equals(e.getMessage())) {
        throw new InvalidFormatException(Res.bundle
            .getString(ResIndex.JDKErrMsg)
            + ": " + stringValue + ": " + e.getMessage()); // !NORES
      } else {
        throw new InvalidFormatException(stringValue);
      }
    } catch (ParseException e) {
      throw new InvalidFormatException(stringValue);
    }
  }
  
  /**
   * A high-speed parse that parses directly into a character array.
   * 
   * @param variant
   *          Variant
   * @param value
   *          char[]
   * @param offset
   *          integer
   * @param len
   *          integer
   * @throws InvalidFormatException
   */
  @Override
  public void parse(Variant variant, char[] value, int offset, int len)
      throws InvalidFormatException {
    if (value == null || len == 0 || value.length == 0) {
      variant.setUnassignedNull();
      return;
    }
    boolean negative = (value[offset] == '-');
    int digit;
    int result = 0;
    if (negative) {
      ++offset;
      --len;
    }
    for (int index = offset; len > 0; ++index, --len) {
      digit = value[index];
      if (digit >= '0' && digit <= '9')
        digit = digit - '0';
      else {
        digit = Character.digit((char) digit, 10);
        if (digit < 0)
          throw new InvalidFormatException(new String(value, offset, len));
      }
      result = result * 10 + digit;
    }
    
    setFromInt(variant, type, negative ? -result : result);
  }
  
  /**
   * Returns the Variant type
   * 
   * @return integer
   */
  @Override
  public int getVariantType() {
    return type;
  }
  
  protected int type;
}
