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

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.UIManager;

import com.borland.dx.text.Alignment;
import com.borland.jb.util.Diagnostic;
import com.borland.jb.util.Trace;
import com.borland.jbcl.model.BasicVectorContainer;
import com.borland.jbcl.model.BasicViewManager;
import com.borland.jbcl.model.VectorModelEvent;
import com.borland.jbcl.model.VectorSelectionEvent;
import com.borland.jbcl.util.ImageLoader;
import com.borland.jbcl.view.FocusableItemPainter;
import com.borland.jbcl.view.TabsetView;
import com.borland.jbcl.view.TextItemPainter;

/**
 *
 */
public class TabsetPanel extends TabsetView implements java.io.Serializable{
  private Vector pageInfo = new Vector();
  CardLayout     cardLayout = new CardLayout();
  Insets         margins    = new Insets(4, 4, 4, 4);
  String         textureName;

  /**
   * Construct a default TabsetPanel
   */
  public TabsetPanel() {
    super.setModel(new BasicVectorContainer());
    TextItemPainter textPainter = new TextItemPainter(Alignment.LEFT | Alignment.MIDDLE, new Insets(1, 1, 1, 1));
    super.setViewManager(new BasicViewManager(new FocusableItemPainter(textPainter)));

    setClientBordered(true);
    super.setLayout(cardLayout);
    // Insert the blank component that displays when there is no selection.
    super.addImpl(new JPanel(), "BLANK", -1); 
    super.setBackground(UIManager.getColor("TabbedPane.tabBackground")); 
    super.setForeground(UIManager.getColor("TabbedPane.tabForeground")); 
  }

  public void updateUI() {
    super.setBackground(UIManager.getColor("TabbedPane.tabBackground")); 
    super.setForeground(UIManager.getColor("TabbedPane.tabForeground")); 
  }

  /**
   * Get/Set the selected page
   */
  public void setSelectedPage(Component page) {
    int index = pageInfo.indexOf(page);

    if (index != -1) {
      setSelectedIndex(index);
    }
  }

  public Component getSelectedPage() {
    int index = getSelectedIndex();
    if (index == -1)
      return null;

    Component comp = (Component) pageInfo.elementAt(index);
    if (comp instanceof PlaceHolder)
      return null;
    else
      return comp;
  }

  public void removePage(Component page) {
    int index = pageInfo.indexOf(page);

    if (index != -1) {
      removeTab((String) model.get(index));
    }
  }

  //------------------------------------------------------------------------------------------------
  // superclass overrides
  //------------------------------------------------------------------------------------------------

  public void setLabels(String[] labels) {
/*
    if (isReadOnly())
      return;
    if (labels != null) {
      for (int i = pageInfo.size(); i < labels.length; i++)
        pageInfo.addElement(new PlaceHolder());
    }
*/
    super.setLabels(labels);
  }

  public void setTextureName(String path) {
    if (path != null && !path.equals("")) {
      Image i = ImageLoader.load(path, this);
      if (i != null) {
        ImageLoader.waitForImage(this, i);
        textureName = path;
        setTexture(i);
      }
      else {
        throw new IllegalArgumentException(path);
      }
    }
    else {
      textureName = null;
      setTexture(null);
    }
  }
  public String getTextureName() {
    return textureName;
  }

  public void addTab(Object item) {
/*
    if (isReadOnly())
      return;
    pageInfo.addElement(new PlaceHolder());
*/
    super.addTab(item);
  }

  public void removeTab(String item) {
    if (isReadOnly())
      return;
    int index = writeModel.find(item);
    if (index != -1) {
      Component comp = (Component) pageInfo.elementAt(index);
      pageInfo.removeElementAt(index);

      if (!(comp instanceof PlaceHolder))
        super.remove(comp);

      super.removeTab(item);
    }
  }

  public void addTab(int aheadOf, String item) {
    if (isReadOnly())
      return;

    if (aheadOf == -1)
      pageInfo.addElement(new PlaceHolder());
    else
      pageInfo.insertElementAt(new PlaceHolder(), aheadOf);

//    if (aheadOf != -1)
//      pageInfo.insertElementAt(null, aheadOf);
    super.addTab(aheadOf, item);
  }

  /**
   * Used when add(Component page, Object label) is called.
   */
  public void addImpl(Component page, Object label, int index) {
//    System.err.println("** addImpl " + page + "," + label + "," + index);

    writeModel.enableModelEvents(false);

    if (label != null) {
      int oldIndex = model.find(label);
      if (oldIndex != -1) {
        // If we're here, then we're adding a component to an existing
        // label.
        // If there is already a component there, we'll replace it in
        // our internal list, and remove it from the container.
        Component oldComp = (Component) pageInfo.elementAt(oldIndex);
        if (!(oldComp instanceof PlaceHolder))
          super.remove(oldComp);
        pageInfo.setElementAt(page, oldIndex);
      }
      else {
        // If we're here, then the user is adding a component to a page
        // that doesn't yet exist.  We'll add it.
        pageInfo.addElement(page);
        super.addTab(label);
      }
    }
    else {
      // If we're here, the user specified a null key, so we'll make one up.
      pageInfo.addElement(page);
      label = page.getName();
      super.addTab(label);
    }
    super.addImpl(page, label, index);

    writeModel.enableModelEvents(true);

    String selectedLabel = getSelectedTab();
    if (selectedLabel != null && selectedLabel.equals((String) label))
      cardLayout.show(this, selectedLabel);
  }

  /*
   * Remove a page (component)
   */
  public void remove(Component page) {
//      sure who's going to be calling this function.
//    if (isReadOnly())
//      return;
    int index = pageInfo.indexOf(page);

    if (index != -1) {
//      pageInfo.removeElementAt(index);
//      writeModel.remove(index);
      pageInfo.setElementAt(new PlaceHolder(), index);
    }
    super.remove(page);
  }

  /**
   * Remove all pages
   */
  public void removeAll() {
    if (isReadOnly())
      return;
    for (int i = 0; i < pageInfo.size(); i++) {
      if (!(pageInfo.elementAt(i) instanceof PlaceHolder))
        super.remove((Component) pageInfo.elementAt(i));
    }

    pageInfo.removeAllElements();
    writeModel.removeAll();
  }

  public Insets getInsets() {
    Insets i = super.getInsets();
    i.left = i.left + margins.left;
    i.top = i.top + margins.top;
    i.right = i.right + margins.right;
    i.bottom = i.bottom + margins.bottom;
    return i;
  }

  public Insets getMargins() {
    return margins;
  }
  public void setMargins(Insets margins) {
    if (margins == null)
      this.margins = new Insets(0, 0, 0, 0);
    else
      this.margins = margins;
    invalidate();
    repaint(100);
  }

  // Event listeners

  // VectorSelectionListener events...

  public void selectionChanged(VectorSelectionEvent e) {
    super.selectionChanged(e);
    int index = getSelectedIndex();
    if (index != -1) {
      if (!(pageInfo.elementAt(index) instanceof PlaceHolder)) {
        String label = model.get(index).toString();
        cardLayout.show(this, label);
        return;
      }
    }
    cardLayout.show(this, "BLANK"); 
  }

  public void modelContentChanged(VectorModelEvent e) {
    Diagnostic.trace(Trace.ModelEvents, "TabsetView.modelContentChanged(" + e + ")"); 
    switch (e.getChange()) {
      case (VectorModelEvent.CONTENT_CHANGED):
      case (VectorModelEvent.ITEM_CHANGED):
      case (VectorModelEvent.ITEM_TOUCHED):
        updateTabInfo();
        break;
    }
  }


  // This function is used to synchronize the pageInfo array, and
  // also makes sure that the cardLayout knows about what we want
  // it to know about.  It will be called whenever a tab is removed
  // or added, or setLabels(...) is called.

  protected void updateTabInfo() {
    if (pageInfo == null)
      return;

    // First, make a list of all the current components.
    Component[] oldComponents = new Component[pageInfo.size()];
    for (int i = 0; i < pageInfo.size(); i++) {
      oldComponents[i] = (Component) pageInfo.elementAt(i);
    }
    // Now, remove all of the components from the layout manager, then
    // add them again with the corresponding new label.  Note that this
    // is only affecting the layout manager here, since the components
    // are still associated with the container.
    int index = getSelectedIndex();

    for (int i = 0; i < model.getCount(); i++) {
      if (i < oldComponents.length) {
        if (!(oldComponents[i] instanceof PlaceHolder)) {
          cardLayout.removeLayoutComponent(oldComponents[i]);
          cardLayout.addLayoutComponent(oldComponents[i], model.get(i));

          if (index == i) {
            cardLayout.show(this, (String) model.get(i));
          }
        }
      }
      else {
        // If the new list of labels is longer than what we used to have,
        // grow the pageInfo list to match.
        pageInfo.addElement(new PlaceHolder());
      }
    }

    // Now, if the new list of labels is shorter than what we used to
    // have, remove the extra components, in reverse order.
    for (int i = oldComponents.length - 1; i >= model.getCount(); i--) {
      pageInfo.removeElementAt(i);
      if (!(oldComponents[i] instanceof PlaceHolder))
        super.remove(oldComponents[i]);
    }

    super.updateTabInfo();
  }

  // Make sure that nobody can change our layout from CardLayout.
  public final void setLayout(LayoutManager mgr) {
//    throw new IllegalArgumentException(Res._LayoutNotSupported);
  }

  public Dimension getPreferredSize() {
    Dimension prefSize = new Dimension(100, 100);
    if (!pageInfo.isEmpty()) {
      // CardLayout.preferredLayoutSize(...) already takes insets into account.
      prefSize = cardLayout.preferredLayoutSize(this);
      prefSize.width = Math.max(prefSize.width, super.getPreferredSize().width);
    }
    return prefSize;
  }

  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    if (getSelectedPage() != null)
      getSelectedPage().setEnabled(enabled);
  }

}

class PlaceHolder extends Component implements java.io.Serializable {}
