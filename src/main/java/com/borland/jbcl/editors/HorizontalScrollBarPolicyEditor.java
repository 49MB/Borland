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

public class HorizontalScrollBarPolicyEditor extends IntegerTagEditor
{
  public HorizontalScrollBarPolicyEditor() {
    super(
      new int[] {
        javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED,
        javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER,
        javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS},
      new String[] {
        Res._SBP_AsNeeded,     
        Res._SBP_Never,     
        Res._SBP_Always},     
      new String[] {
        "javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED", 
        "javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER", 
        "javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS"}); 
  }
}
