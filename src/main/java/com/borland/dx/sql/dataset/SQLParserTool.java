package com.borland.dx.sql.dataset;

import java.util.ArrayList;

public class SQLParserTool {
  
  public static String[] parseNamedParameters(Database database, String query) {
    char quoteCharacter = database.getIdentifierQuoteChar();
    SimpleParser parser = new SimpleParser(query, quoteCharacter);
    QueryParseToken tokens = parser.getParameterTokens();
    
    ArrayList<String> params = new ArrayList<String>();
    while (tokens != null) {
      if (tokens.isParameter()) {
        if (tokens.getName() != null) {
          params.add(tokens.getName());
        }
      }
      tokens = tokens.getNextToken();
    }
    return params.toArray(new String[params.size()]);
  }
}