//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------
//! OracleCallableStmt
//! This is a special version of CallableStmt.java, where some methods are overridden to retrieve
//! the first output value as a ResultSet.
//! Oracle doesn't support the standard JDBC way of getting ResultSets from execute().
//!   This class will code modify the methods in CallableStmt to retrieve this dataSet.
//!
//! **** WARNING !!! ****
//!
//! This file is related to: PreparedStmt.java, CallableStmt.java, and InterbaseCallableStmt.java.
//! Be careful when changing the signature of methods in this file and the related files. Many of
//! the methods are overridden, and the functionality can easily be broken if the derived methods
//! are not changed accordingly [it may compile, but...]
//!-------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.DataSetException;
import com.borland.dx.dataset.Variant;

// Special for Oracle PL-SQL.
// A procedure used for a ProcedureDataSet must have an OUT parameter of
// type CURSOR REF as the first parameter in the procedure Specification
// (queryString).  Typically this will be a PL-SQL stored function with a
// return type of a CURSOR REF.
//
// The Oracle jdbc drivers use an extension of jdbc for this data type.
// Since we dont have such a jdbc Variant type, a JBuilder user cannot
// specify this parameter in a parameterRow.
//
// This code in this class will modify the methods in CallableStmt to
// retrieve this dataSet.
//
class OracleCallableStmt extends CallableStmt {
  
  // ! Warning: Overloaded method from CallableStmt
  // !
  // Simply skip the first parameter in the token list.
  // This should be the ResultSet.
  //
  @Override
  boolean analyzeParameters(QueryParseToken tokens, int actualParamCount,
      char quoteChar) /*-throws DataSetException-*/{
    
    boolean isNamed = false;
    
    while (tokens != null && !tokens.isParameter())
      tokens = tokens.getNextToken();
    if (tokens != null) {
      isNamed = (tokens.getName() != null);
      tokens = tokens.getNextToken();
      isNamed |= super.analyzeParameters(tokens, actualParamCount - 1,
          quoteChar);
    }
    return isNamed;
  }
  
  // ! Warning: Overloaded method from PreparedStmt
  // !
  // Bind the first parameter as an ORACLE_CURSOR
  //
  @Override
  void bindParameters() throws SQLException, DataSetException {
    statement.registerOutParameter(1, ORACLE_CURSOR);
    super.bindParameters();
  }
  
  // ! Warning: Overloaded method from CallableStmt
  // !
  // The rest of the parameters are shifted 1 position
  //
  @Override
  void bindParameter(Variant data, Column column, int param)
      /*-throws DataSetException-*/throws SQLException {
    super.bindParameter(data, column, param + 1);
  }
  
  // ! Warning: Overloaded method from CallableStmt
  // !
  // The override array kept in CallableStmt is 0 based for parameter 1
  //
  @Override
  boolean isInputOverride(int param) {
    return super.isInputOverride(param - 1);
  }
  
  // ! Warning: Overloaded method from PreparedStmt
  // !
  // Get the ResultSet as an output parameter
  //
  @Override
  public ResultSet executeQuery()
      /*-throws DataSetException-*/throws SQLException {
    prepareParameters();
    statement.execute();
    return (ResultSet) statement.getObject(1);
  }
  
  // ! Warning: Overloaded method from CallableStmt
  // !
  // The output parameters are also shifted 1 position
  //
  @Override
  boolean bindOutParameter(Variant data, Column column, int param)
      /*-throws DataSetException-*/throws SQLException {
    return super.bindOutParameter(data, column, param + 1);
  }
  
  static final private int ORACLE_CURSOR = -10; // From
                                                // oracle.jdbc.driver.OracleTypes.CURSOR
}
