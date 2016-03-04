//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

//NOTTRANSLATABLE

package com.borland.dx.sql.dataset.cons;

public class ProcedureResolverStringBean
{
    public static final String[][] strings = {
      {"database",         Res.bundle.getString(ResIndex.BI_database), "getDatabase", "setDatabase"},
      {"insertProcedure",  Res.bundle.getString(ResIndex.BI_insertProcedure), "getInsertProcedure", "setInsertProcedure"},
      {"updateProcedure",  Res.bundle.getString(ResIndex.BI_updateProcedure), "getUpdateProcedure", "setUpdateProcedure"},
      {"deleteProcedure",  Res.bundle.getString(ResIndex.BI_deleteProcedure), "getDeleteProcedure", "setDeleteProcedure"},
    };
}
