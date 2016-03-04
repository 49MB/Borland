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
package com.borland.jbcl.editors;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.borland.jb.util.VetoException;
import com.borland.jbcl.control.ButtonControl;
import com.borland.jbcl.control.TreeControl;
import com.borland.jbcl.layout.VerticalFlowLayout;
import com.borland.jbcl.model.BasicTreeContainer;
import com.borland.jbcl.model.GraphLocation;
import com.borland.jbcl.model.GraphSubfocusEvent;
import com.borland.jbcl.model.GraphSubfocusListener;


public class TreeItemsEditorPanel extends JComponent implements ActionListener, GraphSubfocusListener
{
  TreeControl   tree   = new TreeControl();
  JPanel         butPan =  new JPanel();
  ButtonControl add    = new ButtonControl();
  ButtonControl remove = new ButtonControl();
  ButtonControl up     = new ButtonControl();
  ButtonControl down   = new ButtonControl();

  public TreeItemsEditorPanel() {
    super();
    butPan.setLayout(new VerticalFlowLayout());
//    butPan.add(up);
//    butPan.add(down);
//    butPan.add(new Spacer());
    butPan.add(add);
    butPan.add(remove);

    this.setLayout(new BorderLayout(10,10));
    this.add(tree, BorderLayout.CENTER);
    this.add(butPan, BorderLayout.EAST);
    tree.setExpandByDefault(true);
    tree.setShowRollover(true);

    tree.addSubfocusListener(this);
    up.setLabel(Res._MoveNodeUp);     
    up.addActionListener(this);
    down.setLabel(Res._MoveNodeDown);     
    down.addActionListener(this);
    add.setLabel(Res._AddChild);     
    add.addActionListener(this);
    remove.setLabel(Res._RemoveNode);     
    remove.addActionListener(this);
    checkButtons();
  }

  public void actionPerformed(ActionEvent e) {
    Object target = e.getSource();
    if (target == remove) {
      if (tree.getSubfocus() != tree.getRoot())
        tree.remove(tree.getSubfocus());
    }
    else if (target == add) {
      tree.addChild(tree.getSubfocus(), Res._NewChild);     
    }
    else if (target == down) {
      GraphLocation parent = tree.getSubfocus().getParent();
      GraphLocation[] children = parent.getChildren();
      BasicTreeContainer temp = new BasicTreeContainer(tree.getSubfocus());
      for (int i = 0; i < children.length; i++) {
        if (children[i] == tree.getSubfocus()) {
          tree.remove(tree.getSubfocus());
          if (i > 0 && i == children.length - 1) {
            tree.addChild(parent, children[0], temp.getRoot());
          }
          else if (i == children.length - 2) {
            tree.addChild(parent, temp.getRoot());
          }
          else if (i < children.length - 2) {
            tree.addChild(parent, children[i + 2], temp.getRoot());
          }
        }
      }
      tree.setSubfocus(tree.getSubfocus());
    }
    else if (target == up) {
      GraphLocation parent = tree.getSubfocus().getParent();
      GraphLocation[] children = parent.getChildren();
      BasicTreeContainer temp = new BasicTreeContainer(tree.getSubfocus());
      for (int i = 0; i < children.length; i++) {
        if (children[i] == tree.getSubfocus()) {
          tree.remove(tree.getSubfocus());
          if (i > 0) {
            tree.addChild(parent, children[i-1], temp.getRoot());
          }
          else
            tree.addChild(parent, temp.getRoot());
        }
      }
      tree.setSubfocus(tree.getSubfocus());
    }
    checkButtons();
  }

  public Insets getInsets() {
    return new Insets(10, 10, 5, 10);
  }

  void checkButtons() {
    GraphLocation sf = tree.getSubfocus();
    GraphLocation root = tree.getRoot();

    remove.setEnabled(sf != root);
    up.setEnabled(sf != root);
  }

  public void subfocusChanging(GraphSubfocusEvent e) throws VetoException {}
  public void subfocusChanged(GraphSubfocusEvent e) {
    checkButtons();
  }
}
