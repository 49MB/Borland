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

import com.borland.dx.dataset.ParameterType;

public class ParameterTypeEditor extends IntegerTagEditor
{
  public ParameterTypeEditor() {
    super(values, makeResourceStrings(), sourceStrings);
  }
/* ParameterType Editor
Parameter_In                     "IN"
Parameter_Out                    "OUT"
Parameter_In_Out                 "IN_OUT"
Parameter_Return                 "RETURN"
Parameter_Result                 "RESULT"
*/
  static String[] makeResourceStrings() {
    if (resourceStrings == null) {
      resourceStrings = new String[6];
      resourceStrings[0] = Res._NONE;     
      resourceStrings[1] = "IN"; 
      resourceStrings[2] = "OUT"; 
      resourceStrings[3] = "IN_OUT"; 
      resourceStrings[4] = "RETURN"; 
      resourceStrings[5] = "RESULT"; 
    }
    return resourceStrings;
  }

  static int[] values = {
                         ParameterType.NONE,
                         ParameterType.IN,
                         ParameterType.OUT,
                         ParameterType.IN_OUT,
                         ParameterType.RETURN,
                         ParameterType.RESULT,
                        };

  static String[] sourceStrings = {
                         "com.borland.dx.dataset.ParameterType.NONE", 
                         "com.borland.dx.dataset.ParameterType.IN", 
                         "com.borland.dx.dataset.ParameterType.OUT", 
                         "com.borland.dx.dataset.ParameterType.IN_OUT", 
                         "com.borland.dx.dataset.ParameterType.RETURN", 
                         "com.borland.dx.dataset.ParameterType.RESULT" 
                         };

  static String[] resourceStrings;
}

