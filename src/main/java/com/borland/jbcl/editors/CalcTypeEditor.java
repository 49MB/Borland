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

import com.borland.dx.dataset.CalcType;

public class CalcTypeEditor extends IntegerTagEditor
{
  public CalcTypeEditor() {
    super(values, makeResourceStrings(), sourceStrings);
  }

  static String[] makeResourceStrings() {
    if (resourceStrings == null) {
      resourceStrings = new String[4];
      resourceStrings[0] = Res._NO_CALC;     
      resourceStrings[1] = Res._CALC;     
      resourceStrings[2] = Res._AGGREGATE;     
      resourceStrings[3] = Res._LOOKUP;     
    }
    return resourceStrings;
  }

  static int[] values = {
    CalcType.NO_CALC,
    CalcType.CALC,
    CalcType.AGGREGATE,
    CalcType.LOOKUP
  };
  static String[] sourceStrings = {
    "com.borland.dx.dataset.CalcType.NO_CALC",
    "com.borland.dx.dataset.CalcType.CALC",
    "com.borland.dx.dataset.CalcType.AGGREGATE",
    "com.borland.dx.dataset.CalcType.LOOKUP"
  };
  static String[] resourceStrings;
}
