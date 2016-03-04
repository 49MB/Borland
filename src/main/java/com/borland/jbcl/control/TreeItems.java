/**
 * Copyright (c) 1996-2004 Borland Software Corp. All Rights Reserved.
 *
 * This SOURCE CODE FILE, which has been provided by Borland as part
 * of a Borland product for use ONLY by licensed users of the product,
 * includes CONFIDENTIAL and PROPRIETARY information of Borland.
 *
 * USE OF THIS SOFTWARE IS GOVERNED BY THE TERMS AND CONDITIONS
 * OF THE LICENSE STATEMENT AND LIMITED WARRANTY FURNISHED WITH
 * THE PRODUCT.
 *
 * IN PARTICULAR, YOU WILL INDEMNIFY AND HOLD BORLAND, ITS RELATED
 * COMPANIES AND ITS SUPPLIERS, HARMLESS FROM AND AGAINST ANY
 * CLAIMS OR LIABILITIES ARISING OUT OF THE USE, REPRODUCTION, OR
 * DISTRIBUTION OF YOUR PROGRAMS, INCLUDING ANY CLAIMS OR LIABILITIES
 * ARISING OUT OF OR RESULTING FROM THE USE, MODIFICATION, OR
 * DISTRIBUTION OF PROGRAMS OR FILES CREATED FROM, BASED ON, AND/OR
 * DERIVED FROM THIS SOURCE CODE FILE.
 */
//--------------------------------------------------------------------------------------------------
// Copyright (c) 1996 - 2004 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.jbcl.control;

import java.util.Vector;

import com.borland.jbcl.model.BasicTreeContainer;
import com.borland.jbcl.model.GraphLocation;
import com.borland.jbcl.model.GraphModel;
import com.borland.jbcl.util.TriState;

/**
 * This class is a string processor for the items property of the tree
 */
public class TreeItems implements java.io.Serializable
{
  public TreeItems(String[] items) {
    String text = "";
    if (items != null && items.length > 0) {
      text = items[0];
      for (int i = 1; i < items.length; i++) {
        text += CR + items[i]; 
      }
    }
    processItems(text);
  }

  public TreeItems(String text) {
    processItems(text);
  }

  public TreeItems(GraphModel model) {
    if (model.getRoot() != null) {
      itemArray = new Vector();
      processChildren(model.getRoot());
      items = new String[itemArray.size()];
      for (int i = 0; i < items.length; i++) {
        items[i] = itemArray.elementAt(i) != null ? itemArray.elementAt(i).toString() : ""; 
      }
    }
  }

  public String[] getItems() { return items; }
  public GraphModel getModel() { return model; }

  private void processItems(String text) {
    if (text.length() > 0) {
      int begin = 0;
      int end = text.indexOf(CR, begin) > 0? text.indexOf(CR, begin): text.length();
      model = new BasicTreeContainer(text.substring(begin, end));
      GraphLocation lastNode = model.getRoot();
      GraphLocation parent = lastNode;
      int lastTabs = 0;
      begin = end + 1;
      end = text.indexOf(CR, begin) > 0? text.indexOf(CR, begin): text.length();
       while (end > begin) {
        String thisText = text.substring(begin, end);
        int thisTabs = getTabCount(thisText);
        if (thisTabs > lastTabs) {
          parent = lastNode;
        }
        else if (thisTabs < lastTabs) {
          int tabDiff = lastTabs - thisTabs;
          for (int i = 0; i < tabDiff; i++) {
            parent = parent.getParent();
          }
        }
        thisText = thisText.substring(thisTabs);
        lastNode = model.addChild(parent, thisText);
        lastTabs = thisTabs;
        begin = end > 0? end + 1: end;
        end = text.indexOf(CR, begin) > 0? text.indexOf(CR, begin): text.length();
      }
    }
  }

  private int getTabCount(String text) {
    char[] chars = text.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      if (!(new Character(chars[i]).equals(new Character(TAB.charAt(0))))) { 
        return i;
      }
    }
    return 0;
  }

  private void processChildren(GraphLocation node) {
    String tabs = ""; 
    for (int t = 0; t < depth; t++) {
      tabs += TAB; 
    }
    itemArray.addElement(tabs + model.get(node));
    if (node.hasChildren() != TriState.NO) {
      depth++;
      GraphLocation[] children = node.getChildren();
      for (int i = 0; i < children.length; i++) {
        processChildren(node.getChildren()[i]);
      }
      depth--;
    }
  }

  private BasicTreeContainer model = new BasicTreeContainer();
  private String[] items;
  private final String CR = "\n"; 
  private final String TAB = "\t"; 
  Vector itemArray;
  int depth = 0;
}
