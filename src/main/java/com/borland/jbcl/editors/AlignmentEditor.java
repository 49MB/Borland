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


public class AlignmentEditor extends IntegerTagEditor
{
  public AlignmentEditor() {
    super(new int[] {
            com.borland.dx.text.Alignment.LEFT | com.borland.dx.text.Alignment.TOP,
            com.borland.dx.text.Alignment.LEFT | com.borland.dx.text.Alignment.MIDDLE,
            com.borland.dx.text.Alignment.LEFT | com.borland.dx.text.Alignment.BOTTOM,
            com.borland.dx.text.Alignment.LEFT | com.borland.dx.text.Alignment.VSTRETCH,
            com.borland.dx.text.Alignment.CENTER | com.borland.dx.text.Alignment.TOP,
            com.borland.dx.text.Alignment.CENTER | com.borland.dx.text.Alignment.MIDDLE,
            com.borland.dx.text.Alignment.CENTER | com.borland.dx.text.Alignment.BOTTOM,
            com.borland.dx.text.Alignment.CENTER | com.borland.dx.text.Alignment.VSTRETCH,
            com.borland.dx.text.Alignment.RIGHT | com.borland.dx.text.Alignment.TOP,
            com.borland.dx.text.Alignment.RIGHT | com.borland.dx.text.Alignment.MIDDLE,
            com.borland.dx.text.Alignment.RIGHT | com.borland.dx.text.Alignment.BOTTOM,
            com.borland.dx.text.Alignment.RIGHT | com.borland.dx.text.Alignment.VSTRETCH,
            com.borland.dx.text.Alignment.HSTRETCH | com.borland.dx.text.Alignment.TOP,
            com.borland.dx.text.Alignment.HSTRETCH | com.borland.dx.text.Alignment.MIDDLE,
            com.borland.dx.text.Alignment.HSTRETCH | com.borland.dx.text.Alignment.BOTTOM,
            com.borland.dx.text.Alignment.HSTRETCH | com.borland.dx.text.Alignment.VSTRETCH},
          new String[] {
            Res._LeftTop,     
            Res._LeftMiddle,     
            Res._LeftBottom,     
            Res._LeftVStretch,     
            Res._CenterTop,     
            Res._CenterMiddle,     
            Res._CenterBottom,     
            Res._CenterVStretch,     
            Res._RightTop,     
            Res._RightMiddle,     
            Res._RightBottom,     
            Res._RightVStretch,     
            Res._HStretchTop,     
            Res._HStretchMiddle,     
            Res._HStretchBottom,     
            Res._HStretchVStretch},     
          new String[] {
            "com.borland.dx.text.Alignment.LEFT | com.borland.dx.text.Alignment.TOP",         
            "com.borland.dx.text.Alignment.LEFT | com.borland.dx.text.Alignment.MIDDLE",      
            "com.borland.dx.text.Alignment.LEFT | com.borland.dx.text.Alignment.BOTTOM",      
            "com.borland.dx.text.Alignment.LEFT | com.borland.dx.text.Alignment.VSTRETCH",    
            "com.borland.dx.text.Alignment.CENTER | com.borland.dx.text.Alignment.TOP",       
            "com.borland.dx.text.Alignment.CENTER | com.borland.dx.text.Alignment.MIDDLE",    
            "com.borland.dx.text.Alignment.CENTER | com.borland.dx.text.Alignment.BOTTOM",    
            "com.borland.dx.text.Alignment.CENTER | com.borland.dx.text.Alignment.VSTRETCH",  
            "com.borland.dx.text.Alignment.RIGHT | com.borland.dx.text.Alignment.TOP",        
            "com.borland.dx.text.Alignment.RIGHT | com.borland.dx.text.Alignment.MIDDLE",     
            "com.borland.dx.text.Alignment.RIGHT | com.borland.dx.text.Alignment.BOTTOM",     
            "com.borland.dx.text.Alignment.RIGHT | com.borland.dx.text.Alignment.VSTRETCH",   
            "com.borland.dx.text.Alignment.HSTRETCH | com.borland.dx.text.Alignment.TOP",     
            "com.borland.dx.text.Alignment.HSTRETCH | com.borland.dx.text.Alignment.MIDDLE",  
            "com.borland.dx.text.Alignment.HSTRETCH | com.borland.dx.text.Alignment.BOTTOM",  
            "com.borland.dx.text.Alignment.HSTRETCH | com.borland.dx.text.Alignment.VSTRETCH"}); 
  }
}
