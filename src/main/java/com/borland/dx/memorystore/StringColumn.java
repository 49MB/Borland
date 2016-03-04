//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.memorystore;

import java.text.Collator;

import com.borland.dx.dataset.Variant;
import com.borland.jb.util.Diagnostic;
import com.ibm.icu.util.ULocale;

// Do not make public.
//

class StringColumn extends DataColumn {
  public StringColumn(NullState nullState) {
    super(nullState);
    vector = new String[InitialSize];
    vectorLength = vector.length;
    immutable = true;
  }
  
  @Override
  final void copy(int source, int dest) {
    vector[dest] = vector[source];
    Diagnostic.check(vector[dest] != null);
    if (hasNulls)
      nullState.copy(source, dest, nullMask);
  }
  
  @Override
  final void copyReference(int source, int dest) {
    vector[dest] = vector[source];
  }
  
  @Override
  void grow(int newLength) {
    Diagnostic.check(newLength > vector.length);
    String newVector[] = new String[newLength];
    System.arraycopy(vector, 0, newVector, 0, vectorLength);
    vector = newVector;
    vectorLength = vector.length;
  }
  
  @Override
  int compare(int index1, int index2) {
    // if (d.verboseLocate) Diagnostic.println(vector[index1]+" "+vector[index2]
    // + " " + vector[index1].compareTo(vector[index2]));
    // if (d.verboseLocate) Diagnostic.println(" "+index1+" "+index2);
    if (hasNulls) {
      if ((comp = nullState.compare(index1, index2, nullMask)) != 0)
        return comp;
    }
    // if (vector[index1] == null)
    // Diagnostic.println("null compare1 " + index1);
    // if (vector[index2] == null)
    // Diagnostic.println("null compare2 " + index2);
    return ser.compare(vector[index1], vector[index2]);
  }
  
  @Override
  int compareIgnoreCase(int index1, int index2) {
    if (hasNulls) {
      if ((comp = nullState.compare(index1, index2, nullMask)) != 0)
        return comp;
    }
    return ter.compare(vector[index1], vector[index2]);
  }
  
  int compare(int index1, String string2) {
    String string1 = vector[index1];
    return ser.compare(string1, string2);
  }
  
  int compareIgnoreCase(int index1, String string2) {
    String string1 = vector[index1];
    return ter.compare(string1, string2);
  }
  
  // Used for quickSort.
  //
  @Override
  void setPivot(int indexVector[], int pivotDataRow) {
    this.indexVector = indexVector;
    this.pivotValue = vector[pivotDataRow];
  }
  
  // Used for quickSort. Does not compare nulls becuase insertion
  // sort will follow that does.
  //
  @Override
  int forwardCompare(int leftPivot, boolean caseInsensitive, boolean descending) {
    if (descending) {
      if (caseInsensitive) {
        while ((comp = compareIgnoreCase(indexVector[++leftPivot], pivotValue)) > 0)
          ;
      } else {
        while ((comp = compare(indexVector[++leftPivot], pivotValue)) > 0)
          ;
      }
    } else {
      if (caseInsensitive) {
        while ((comp = compareIgnoreCase(indexVector[++leftPivot], pivotValue)) < 0)
          ;
      } else {
        while ((comp = compare(indexVector[++leftPivot], pivotValue)) < 0)
          ;
      }
    }
    return leftPivot;
  }
  
  // Used for quickSort. Does not compare nulls becuase insertion
  // sort will follow that does.
  //
  @Override
  int reverseCompare(int rightPivot, boolean caseInsensitive, boolean descending) {
    if (descending) {
      if (caseInsensitive) {
        while ((comp = compareIgnoreCase(indexVector[--rightPivot], pivotValue)) < 0)
          ;
      } else {
        Diagnostic.check(rightPivot > 0);
        while ((comp = compare(indexVector[--rightPivot], pivotValue)) < 0)
          Diagnostic.check(rightPivot > 0);
      }
    } else {
      if (caseInsensitive) {
        while ((comp = compareIgnoreCase(indexVector[--rightPivot], pivotValue)) > 0)
          ;
      } else {
        while ((comp = compare(indexVector[--rightPivot], pivotValue)) > 0)
          ;
      }
    }
    return rightPivot;
  }
  
  @Override
  boolean partialCompare(int index1, int index2, boolean caseInsensitive,
      boolean full) {
    
    // ! Diagnostic..println(vector[index1]+" "+ vector[index2]);
    if (full)
      return fullPartialCompare(vector[index1], vector[index2], caseInsensitive);
    return vector[index1].regionMatches(caseInsensitive, 0, vector[index2], 0,
        vector[index2].length());
  }
  
  @Override
  final void getVariant(int row, Variant value) {
    if (hasNulls && vector[row] == NULL_STRING) {
      nullState.getNull(row, value, nullMask, assignedMask);
      Diagnostic.check(value.isNull());
    } else {
      if (hasNulls)
        nullState.vector[row] &= ~nullMask;
      value.setString(vector[row]);
    }
  }
  
  @Override
  void setVariant(int index, Variant val) {
    if (val.isNull()) {
      vector[index] = NULL_STRING;
      setNull(index, val.getType());
    } else {
      if (hasNulls)
        nullState.vector[index] &= ~nullMask;
      vector[index] = val.getString();
      if (vector[index] == null) {
        vector[index] = NULL_STRING;
        setNull(index, Variant.ASSIGNED_NULL);
      }
    }
  }
  
  String pivotValue;
  String[] vector;
  
  private static com.ibm.icu.text.Collator ser;
  private static com.ibm.icu.text.Collator ter;
  
  static {
    // Collator for Internationale vergleiche, gross/klein berücksichtigen:
    ULocale ul = ULocale.ROOT;
    ser = com.ibm.icu.text.Collator.getInstance(ul);
    ser.setStrength(Collator.TERTIARY);
    // Collator for Internationale vergleiche, gross/klein ignorieren:
    ter = com.ibm.icu.text.Collator.getInstance(ul);
    ter.setStrength(Collator.SECONDARY);
  }
}
