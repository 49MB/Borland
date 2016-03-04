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

import com.borland.dx.sql.dataset.SQLDialect;

public class SQLDialectEditor extends IntegerTagEditor
{
  public SQLDialectEditor() {
    super(values, makeResourceStrings(), sourceStrings);
  }

  static String[] makeResourceStrings() {
    if (resourceStrings == null) {
      resourceStrings = new String[3];
      resourceStrings[0] = Res._UNKNOWN;     
      resourceStrings[1] = Res._INTERBASE;     
      resourceStrings[2] = Res._ORACLE;     
    }
    return resourceStrings;
  }

  static int[] values = {SQLDialect.UNKNOWN,
                         SQLDialect.INTERBASE,
                         SQLDialect.ORACLE,
                        };

  static String[] sourceStrings = {"com.borland.dx.dataset.SQLDialect.UNKNOWN",
                                   "com.borland.dx.dataset.SQLDialect.INTERBASE",
                                   "com.borland.dx.dataset.SQLDialect.ORACLE"};
  static String[] resourceStrings;
}
