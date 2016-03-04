package com.borland.dx.memorystore;

import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.DataRow;
import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.PickListDescriptor;
import com.borland.dx.dataset.Variant;
import com.borland.jb.util.Diagnostic;

/**
 * <p>Title: </p>
 * LookupColumn
 *
 * <p>Description: </p>
 * Speichert Lookup-werte bzw ermittelt diese dynamisch
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: Softwareschmiede Höffl GmbH</p>
 *
 * @author Stefan Schmaltz
 * @version 1.0
 */
public class LookupColumn extends CalcPlaceHolderColumn
{
  MemoryData memoryData;
  DataSet dataSet;
  Column column;
  DataRow dataRow;
  PickListDescriptor pld;
  int[] ordinalIndex;
  Variant pivotValue;

  public LookupColumn(MemoryData memoryData, DataSet dataSet, Column column, NullState nullState)
  {
    super(nullState);
    this.memoryData = memoryData;
    this.dataSet = dataSet;
    this.column = column;
    pld = column.getPickList();
    makeIndex();
  }
  
  private void makeIndex() {
    String[] dest = pld.getDestinationColumns();
    dataRow = new DataRow(dataSet, dest);
    ordinalIndex = new int[dest.length];
    for (int i = 0; i < ordinalIndex.length; i++)
    {
      ordinalIndex[i] = dataSet.getColumn(dest[i]).getOrdinal();
    }
  }

  Variant lookupValue(int index)
  {
    if (!dataSet.isCompatibleList(dataRow))
      makeIndex();

    for (int i = 0; i < ordinalIndex.length; i++)
    {
      Variant v = new Variant();
      memoryData.getVariant(index, ordinalIndex[i], v);
      dataRow.setVariant(i, v);
    }
    Variant value = new Variant();
    dataSet.getLookupValue(column.getOrdinal(), dataRow, value);
    return value;
  }

  @Override
  int compare(int index1, int index2)
  {
    if (hasNulls) {
      if ((comp = nullState.compare(index1, index2, nullMask)) != 0)
        return comp;
    }
    Variant v1 = lookupValue(index1);
    Variant v2 = lookupValue(index2);
    return v1.compareTo(v2);
  }

  @Override
  int compareIgnoreCase(int index1, int index2) {
    if (hasNulls) {
      if ((comp = nullState.compare(index1, index2, nullMask)) != 0)
        return comp;
    }
    Variant v2 = lookupValue(index2);
    return compareIgnoreCase(index1, v2);
  }

  int compareIgnoreCase(int index1, Variant variant2)
  {
    Variant  variant1  = lookupValue(index1);
    if (variant1.getType() == Variant.STRING && variant2.getType() == Variant.STRING)
    {
      String name1 = variant1.toString();
      String name2 = variant2.toString();
      int length1 = name1.length();
      int length2 = name2.length();
      int length;
      int comp;
      if (length1 > length2)
	length = length2;
      else
	length = length1;

      for (int index = 0; index < length; ++index) {
	comp = Character.toUpperCase(name1.charAt(index))
	       - Character.toUpperCase(name2.charAt(index));
	if (comp != 0)
	  return comp;
      }

      if (length1 == length2)
	return 0;

      return length1 - length2;
    }
    else
      return variant1.compareTo(variant2);

  }

  // Used for quickSort.
  //
  @Override
  void setPivot(int indexVector[], int pivotDataRow) {
    this.indexVector  = indexVector;
    this.pivotValue   = lookupValue(pivotDataRow);
  }
  // Used for quickSort.  Does not compare nulls becuase insertion
  // sort will follow that does.
  //
  @Override
  int forwardCompare(int leftPivot, boolean caseInsensitive, boolean descending) {
    if (descending) {
      if (caseInsensitive) {
        while ((comp = compareIgnoreCase(indexVector[++leftPivot], pivotValue)) > 0)
          ;
      }
      else {
        while ((comp = lookupValue(indexVector[++leftPivot]).compareTo(pivotValue)) > 0)
          ;
      }
    }
    else {
      if (caseInsensitive) {
        while ((comp = compareIgnoreCase(indexVector[++leftPivot], pivotValue)) < 0)
          ;
      }
      else {
        while ((comp = lookupValue(indexVector[++leftPivot]).compareTo(pivotValue)) < 0)
          ;
      }
    }
    return leftPivot;
  }
  // Used for quickSort.  Does not compare nulls becuase insertion
  // sort will follow that does.
  //
  @Override
  int reverseCompare(int rightPivot, boolean caseInsensitive, boolean descending)
  {
    if (descending) {
      if (caseInsensitive) {
        while ((comp = compareIgnoreCase(indexVector[--rightPivot], pivotValue)) < 0)
          ;
      }
      else {
        Diagnostic.check(rightPivot > 0);
        while ((comp = lookupValue(indexVector[--rightPivot]).compareTo(pivotValue)) < 0)
          Diagnostic.check(rightPivot > 0);
      }
    }
    else {
      if (caseInsensitive) {
        while ((comp = compareIgnoreCase(indexVector[--rightPivot], pivotValue)) > 0)
          ;
      }
      else {
        while ((comp = lookupValue(indexVector[--rightPivot]).compareTo(pivotValue)) > 0)
          ;
      }
    }
    return rightPivot;
  }



  @Override
  boolean partialCompare(int index1, int index2, boolean caseInsensitive, boolean full)
  {
    Variant v1 = lookupValue(index1);
    Variant v2 = lookupValue(index2);
    String s1 = v1.toString();
    String s2 = v2.toString();
    if (full)
      return fullPartialCompare(s1, s2, caseInsensitive);
    return s1.regionMatches(  caseInsensitive, 0, s2, 0, s2.length());
  }

}
