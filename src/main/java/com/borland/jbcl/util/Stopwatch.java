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
package com.borland.jbcl.util;

/**
 * Simple static stopwatch class for timing possibly nested blocks of code
 */
public class Stopwatch
{
  public static boolean enabled = false;

  static int level = 0;
  static String[] text = new String[16];
  static long[]   time = new long[16];
  static final String indent = "                                "; // 16*2 spaces

  public static void start(String t) {
    if (enabled) {
      text[level] = t;
      time[level] = System.currentTimeMillis();
      level++;
    }
  }

  static long print(boolean lap) {
    int l = level - 1;
    long duration = System.currentTimeMillis() - time[l];
    System.err.print(indent.substring(0, 2*l));
    if (lap)
      System.err.println(text[l] + " [" + duration + "ms]"); 
    else
      System.err.println(text[l] + "\t[" + duration + "ms]"); 
    return duration;
  }

  public static long lap() {
    if (enabled)
      return print(true);
    return 0;
  }

  public static long stop() {
    if (enabled) {
      long d = print(false);
      --level;
      return d;
    }
    return 0;
  }

}
