//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.BasicBeanInfo;

public class ParameterRowBeanInfo extends BasicBeanInfo
{
  public ParameterRowBeanInfo() {
    beanClass = ParameterRow.class;
    propertyDescriptors = com.borland.dx.dataset.cons.ParameterRowStringBean.strings;
  }
}
