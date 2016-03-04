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

import java.text.NumberFormat;
import java.util.Locale;

public class LocaleNameEditor extends StringTagEditor
{
  static String[] resourceStrings;
  static String[] sourceCodeStrings;

  public LocaleNameEditor() {
    super(getResourceStrings(), getSourceCodeStrings());
  }

  static String[] getResourceStrings() {
    if (resourceStrings == null) {
      Locale[] locales = NumberFormat.getAvailableLocales();
      resourceStrings = new String[locales.length+1];
      sourceCodeStrings = new String[locales.length+1];
      resourceStrings[0] = Res._LOCALE_DEFAULT;     
      sourceCodeStrings[0] = "";
      for (int i = 0; i < locales.length; ++i) {
        resourceStrings[i+1] = locales[i].getDisplayName() + " [" + locales[i].toString() + "]";
        sourceCodeStrings[i+1] = "\"" + locales[i].toString() + "\"";
      }
    }
    return resourceStrings;
  }
  static String[] getSourceCodeStrings() {
    return sourceCodeStrings;
  }
}
