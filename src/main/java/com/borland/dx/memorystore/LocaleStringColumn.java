//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.memorystore;

import java.text.CollationKey;
import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.Locale;

import com.borland.dx.dataset.Variant;


class LocaleStringColumn extends StringColumn
{
  RuleBasedCollator  collator;
  CollationKey[]     secKeys;      // case insensitive
  CollationKey[]     terKeys;      // case sensitive
  //int                pivotDataRow;


  public LocaleStringColumn(NullState nullState, Locale locale) {
    super(nullState);
    collator = (RuleBasedCollator)Collator.getInstance(locale);
    collator.setDecomposition(Collator.FULL_DECOMPOSITION);
    secKeys = null;
    terKeys = null;
  }



  final int compare(int index1, int index2) {
    if (hasNulls) {
      if ((comp = nullState.compare(index1, index2, nullMask)) != 0)
        return comp;
    }
    createCollationKeys();
    return terKeys[index1].compareTo(terKeys[index2]);
  }


  final int compareIgnoreCase(int index1, int index2) {
    if (hasNulls) {
      if ((comp = nullState.compare(index1, index2, nullMask)) != 0)
        return comp;
    }
    createCollationKeys();
    return secKeys[index1].compareTo(secKeys[index2]);
  }


  void setPivot(int indexVector[], int pivotDataRow) {
    this.indexVector  = indexVector;
    this.pivotValue   = vector[pivotDataRow];
    this.pivotDataRow = pivotDataRow;
  }




  int forwardCompare(int leftPivot, boolean caseInsensitive, boolean descending) {
    createCollationKeys();
    if (descending) { // bug 107024
      if (caseInsensitive) {
        while ((comp = secKeys[indexVector[++leftPivot]].compareTo(secKeys[pivotDataRow])) > 0)
          ;
      }
      else {
        while ((comp = terKeys[indexVector[++leftPivot]].compareTo(terKeys[pivotDataRow])) > 0)
          ;
      }
    }
    else {
      if (caseInsensitive) {
        while ((comp = secKeys[indexVector[++leftPivot]].compareTo(secKeys[pivotDataRow])) < 0)
          ;
      }
      else {
        while ((comp = terKeys[indexVector[++leftPivot]].compareTo(terKeys[pivotDataRow])) < 0)
          ;
      }
    }
    return leftPivot;
  }



  int reverseCompare(int rightPivot, boolean caseInsensitive, boolean descending) {
    createCollationKeys();
    if (descending) { // bug 107024
      if (caseInsensitive) {
        while ((comp = secKeys[indexVector[--rightPivot]].compareTo(secKeys[pivotDataRow])) < 0)
          ;
      }
      else {
        while ((comp = terKeys[indexVector[--rightPivot]].compareTo(terKeys[pivotDataRow])) < 0)
          ;
      }
    }
    else {
      if (caseInsensitive) {
        while ((comp = secKeys[indexVector[--rightPivot]].compareTo(secKeys[pivotDataRow])) > 0)
          ;
      }
      else {
        while ((comp = terKeys[indexVector[--rightPivot]].compareTo(terKeys[pivotDataRow])) > 0)
          ;
      }
    }
    return rightPivot;
  }


   void  grow(int newLength) {
    int oldLength = vectorLength;
    super.grow(newLength);
    if(secKeys != null) {
      CollationKey[] newSecKeys = new CollationKey[newLength];
      CollationKey[] newTerKeys = new CollationKey[newLength];
      System.arraycopy(secKeys, 0, newSecKeys, 0, oldLength);
      System.arraycopy(terKeys, 0, newTerKeys, 0, oldLength);
      secKeys = newSecKeys;
      terKeys = newTerKeys;

      collator.setStrength(Collator.SECONDARY);
      for (int i = oldLength; i < newLength; i++) {
        secKeys[i] = collator.getCollationKey(getVector(vector[i]));
      }
      collator.setStrength(Collator.TERTIARY);
      for (int i = oldLength; i < newLength; i++) {
        terKeys[i] = collator.getCollationKey(getVector(vector[i]));
      }
    }
  }



  void setVariant (int index, Variant val) {
    super.setVariant(index, val);
    if(secKeys != null) {
      collator.setStrength(Collator.SECONDARY);
      secKeys[index] = collator.getCollationKey(getVector(vector[index]));
      collator.setStrength(Collator.TERTIARY);
      terKeys[index] = collator.getCollationKey(getVector(vector[index]));
    }
  }



  void createCollationKeys() {
    if(secKeys == null) {
      int i;
      secKeys = new CollationKey[vectorLength];
      terKeys = new CollationKey[vectorLength];

      collator.setStrength(Collator.SECONDARY);
      for(i=0; i<vectorLength; i++) {
        secKeys[i] = collator.getCollationKey(getVector(vector[i]));
      }

      collator.setStrength(Collator.TERTIARY);
      for(i=0; i<vectorLength; i++) {
        terKeys[i] = collator.getCollationKey(getVector(vector[i]));
      }
    }
  }

  private String getVector(String v)
  {
    if (v == null)
      return "";
    else
      return v;
  }
}


