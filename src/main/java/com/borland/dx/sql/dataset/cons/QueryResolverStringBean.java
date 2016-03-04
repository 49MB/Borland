//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

//NOTTRANSLATABLE

package com.borland.dx.sql.dataset.cons;

public class QueryResolverStringBean
{
    public static final String[][] strings = {
      {"database",    Res.bundle.getString(ResIndex.BI_database), "getDatabase", "setDatabase"},
      {"updateMode",  Res.bundle.getString(ResIndex.BI_updateMode), "getUpdateMode", "setUpdateMode", "com.borland.jbcl.editors.UpdateModeEditor"},
      {"resolverQueryTimeout",  Res.bundle.getString(ResIndex.BI_resolverQueryTimeout), "getResolverQueryTimeout", "setResolverQueryTimeout"},
    };
}
