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
package com.borland.jbcl.view;

/**
 *
 */
public class VariableSizeVector implements SizeVector, java.io.Serializable
{
  public VariableSizeVector() {}

  public VariableSizeVector(int[] newSizes) {
    if (newSizes != null)
      sizes = newSizes;
  }

  public int getSize(int index) {
    if (sizes.length > index && index >= 0)
      return sizes[index];
    return 0;
  }

  public void setSize(int index, int size) {
    if (index >= sizes.length && index >= 0) {
      int newSizes[] = new int[index+1];
      System.arraycopy(sizes, 0, newSizes, 0, sizes.length);
      sizes = newSizes;
    }
    sizes[index] = size;
  }

  public int getSizeUpTo(int lastIndex) {
    int size = 0;
    int effectiveLast = lastIndex <= sizes.length ? lastIndex : sizes.length;
    for (int index = 0; index < effectiveLast; ++index)
      size += sizes[index];
    return size;
  }

  int[] sizes = new int[0];
}
