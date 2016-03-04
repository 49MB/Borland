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
package com.borland.jbcl.layout;

public class PaneConstraints implements java.io.Serializable
{
  public float  proportion = 0.5f;             
  public String position = TOP;
  public String splitComponentName;
  public String name;
  public static final String TOP    = "Top";       
  public static final String BOTTOM = "Bottom";    
  public static final String LEFT   = "Left";      
  public static final String RIGHT  = "Right";     
  public static final String ROOT   = "Root";      

  public PaneConstraints() {
  }

  public PaneConstraints(String name,String splitComponentName, String position, float proportion) {
    this.name = name;
    this.splitComponentName = splitComponentName;
    this.position = position;
    this.proportion = proportion;
  }

  public String toString() {
    return name + ": " + splitComponentName + "," + position + " proportion:" + proportion;    
  }
}
