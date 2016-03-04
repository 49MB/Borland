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
package com.borland.jbcl.sql.monitor;

import java.awt.List;
import java.io.IOException;
import java.io.OutputStream;

public class ListOutStream extends OutputStream /*implements MatrixModel*/ {
//vs  private StringBuffer s = new StringBuffer();
  private static int lineLen = 255;
  private byte[] byteArray = new byte[lineLen];
  private String line = null;
  private List mylist = null;
  private int index = 0;
  private boolean writing = false;
  private boolean printing = false;
  String reentrantString = null;

  public ListOutStream(List list1) {
    mylist = list1;
  }

  public void println(String s) {
    if (printing) {
      reentrantString = s;      // survive at least one level of recursion
    }
    else {
      printing = true;
      mylist.add(s);
      if (reentrantString != null) {
        mylist.add(reentrantString);
        reentrantString = null;
      }
                        mylist.makeVisible(mylist.getItemCount() - 1);
      printing = false;
    }
  }

  public void write(int b) throws IOException {

    if (writing) {
      return;
    }

    writing = true;

    byteArray[index++] = (byte)b;

    //Thread.yield();

    if (index == lineLen || (char)b == '\n') {

      try {
        line = new String(byteArray, 0, index-2);
        mylist.add(line);
      }
      catch (Throwable t) {
        t.printStackTrace();
      }
      index = 0;
    }
    writing = false;
  }


}
