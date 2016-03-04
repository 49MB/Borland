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
 * NullMatrixSelection implements a no-selection WritableMatrixSelection
 */
public class NullMatrixSelection implements WritableMatrixSelection, Serializable
{
  public boolean contains(MatrixLocation location) { return false; }
  public boolean contains(int row, int column) { return false; }
  public int getCount() { return 0; }
  public MatrixLocation[] getAll() { return new MatrixLocation[0]; }
  public void addSelectionListener(MatrixSelectionListener listener) {}
  public void removeSelectionListener(MatrixSelectionListener listener) {}
  public void set(MatrixLocation[] locations) {}
  public void add(MatrixLocation location) {}
  public void add(int row, int column) {}
  public void add(MatrixLocation[] locations) {}
  public void addRange(MatrixLocation begin, MatrixLocation end) {}
  public void addRange(int beginRow, int beginColumn, int endRow, int endColumn) {}
  public void remove(MatrixLocation location) {}
  public void remove(int row, int column) {}
  public void remove(MatrixLocation[] locations) {}
  public void removeRange(MatrixLocation begin, MatrixLocation end) {}
  public void removeRange(int beginRow, int beginColumn, int endRow, int endColumn) {}
  public void removeAll() {}
  public void enableSelectionEvents(boolean enable) {}
}
