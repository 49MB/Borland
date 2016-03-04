//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import com.borland.jb.util.BasicBeanInfo;

public class DataSetViewBeanInfo extends BasicBeanInfo
{
  public DataSetViewBeanInfo() {
    beanClass = DataSetView.class;
    propertyDescriptors = com.borland.dx.dataset.cons.DataSetViewStringBean.strings;
  }
}
