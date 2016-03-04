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
package com.borland.jbcl.model;

import java.io.Serializable;

/**
 * NullVectorSelection implements a no-selection WritableVectorSelection
 */
public class NullVectorSelection implements WritableVectorSelection, Serializable
{
  public boolean contains(int location) { return false; }
  public int getCount() { return 0; }
  public int[] getAll() { return new int[0]; }
  public void addSelectionListener(VectorSelectionListener listener) {}
  public void removeSelectionListener(VectorSelectionListener listener) {}
  public void set(int[] locations) {}
  public void add(int location) {}
  public void add(int[] locations) {}
  public void addRange(int begin, int end) {}
  public void remove(int location) {}
  public void remove(int[] locations) {}
  public void removeRange(int begin, int end) {}
  public void removeAll() {}
  public void enableSelectionEvents(boolean enable) {}
}
