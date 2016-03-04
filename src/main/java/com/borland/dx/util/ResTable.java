/* DO NOT EDIT THIS FILE
 *
 * Copyright (c) 2005 Borland Software Corporation. All Rights Reserved.
 *
 */

package com.borland.dx.util;
import com.borland.jb.util.StringArrayResourceBundle;

public class ResTable extends StringArrayResourceBundle {
  public ResTable() {
    strings = new String[] {
      "DataSource named: {0} is missing",
      "{0} property must start with one of the following:  {1}",
      "{0} property must be set",
      "Property type not handled:  {0}",
      "DataSource name:  {0} must be unique",
      "Property <{0}> not found",
      "{0} DataSource has no public {1}({2}) method",
    };
  }
}