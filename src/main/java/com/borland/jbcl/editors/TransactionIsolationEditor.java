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
import java.sql.Connection;

public class TransactionIsolationEditor extends IntegerTagEditor
{
  public TransactionIsolationEditor() {
    super(values, makeResourceStrings(), sourceStrings);
  }

  static String[] makeResourceStrings() {
    if (resourceStrings == null) {
      resourceStrings = new String[5];
      resourceStrings[0] = Res._NONE;     
      resourceStrings[1] = Res._READ_UNCOMMITTED;     
      resourceStrings[2] = Res._READ_COMMITTED;     
      resourceStrings[3] = Res._REPEATABLE_READ;     
      resourceStrings[4] = Res._SERIALIZABLE;     
    }
    return resourceStrings;
  }

  static int[] values = {Connection.TRANSACTION_NONE,
                         Connection.TRANSACTION_READ_UNCOMMITTED,
                         Connection.TRANSACTION_READ_COMMITTED,
                         Connection.TRANSACTION_REPEATABLE_READ,
                         Connection.TRANSACTION_SERIALIZABLE,
                        };

  static String[] sourceStrings = {"java.sql.Connection.TRANSACTION_NONE",
                                   "java.sql.Connection.TRANSACTION_READ_UNCOMMITTED",
                                   "java.sql.Connection.TRANSACTION_READ_COMMITTED",
                                   "java.sql.Connection.TRANSACTION_REPEATABLE_READ",
                                   "java.sql.Connection.TRANSACTION_SERIALIZABLE",
                                   };

  static String[] resourceStrings;
}

