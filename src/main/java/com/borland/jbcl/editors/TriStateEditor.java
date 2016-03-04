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

import com.borland.jb.util.TriStateProperty;

public class TriStateEditor extends IntegerTagEditor
{
  public TriStateEditor() {
    super(values, makeResourceStrings(), sourceStrings);
    //System.err.println("Tristate editor constructor");
  }

  static String[] makeResourceStrings() {
    if (resourceStrings == null) {
      resourceStrings = new String[3];
      resourceStrings[0] = "DEFAULT";
      resourceStrings[1] = "TRUE";
      resourceStrings[2] = "FALSE";

    }
    return resourceStrings;
  }

  static int[] values = {TriStateProperty.DEFAULT, TriStateProperty.TRUE, TriStateProperty.FALSE};
  static String[] sourceStrings = {"com.borland.jb.util.TriStateProperty.DEFAULT",    
                                   "com.borland.jb.util.TriStateProperty.TRUE",        
                                   "com.borland.jb.util.TriStateProperty.FALSE"};        
  static String[] resourceStrings;
}
