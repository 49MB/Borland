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
package com.borland.dbswing.plaf.basic;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.borland.dbswing.JdbComboBox.DBComboBoxModel;

public class BasicJdbComboBoxEditor implements ComboBoxEditor, FocusListener,
Serializable {
  protected ComboBoxTextField editor;
  protected JComboBox comboBox;
  
  public BasicJdbComboBoxEditor(JComboBox comboBox) {
    this(comboBox, null);
  }
  
  public BasicJdbComboBoxEditor(JComboBox comboBox, Border border) {
    this.comboBox = comboBox;
    editor = new ComboBoxTextField("", 9, comboBox);
    editor.addFocusListener(this);
    if (border != null) {
      editor.setBorder(border);
    }
  }
  
  public Component getEditorComponent() {
    return editor;
  }
  
  public void setItem(Object anObject) {
    if (anObject != null) {
      editor.setText(anObject.toString());
    }
    else {
      editor.setText("");
    }
  }
  
  public Object getItem() {
    return editor.getText();
  }
  
  public int getIndex() {
    return editor.getIndex();
  }
  
  public void selectAll() {
    editor.selectAll();
    editor.requestFocus();
  }
  
  public void focusGained(FocusEvent e) {}
  
  public void focusLost(FocusEvent e) {
    /*if (!e.isTemporary())
         {
      editor.postActionEvent();
         }*/
    comboBox.setPopupVisible(false);
  }
  
  public void addActionListener(ActionListener l) {
    editor.addActionListener(l);
  }
  
  public void removeActionListener(ActionListener l) {
    editor.removeActionListener(l);
  }
  
  class ComboBoxTextField extends JTextField {
    private BasicComboBoxDocument document;
    
    boolean doSearch;
    
    public ComboBoxTextField(String value, int n, JComboBox comboBox) {
      super(value, n);
      super.setBorder(null);
      setDocument(document = new BasicComboBoxDocument(comboBox, this));
      addKeyListener(new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent e) {}
        
        @Override
        public void keyPressed(KeyEvent e) {
          int key = e.getKeyCode();
          int mod = e.getModifiers();
          doSearch = ((key >= KeyEvent.VK_0 && key < KeyEvent.VK_F1 &&
              (mod == 0 || (mod & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK)) ||
              (key == KeyEvent.VK_BACK_SPACE) ||
              (key == KeyEvent.VK_DELETE));
          if (doSearch ||
              (key != KeyEvent.VK_ENTER && key != KeyEvent.VK_ESCAPE &&
                  key != KeyEvent.VK_TAB && key != KeyEvent.VK_SHIFT &&
                  key != KeyEvent.VK_CONTROL && key != KeyEvent.VK_ALT &&
                  (key < KeyEvent.VK_F1 || key >= KeyEvent.VK_F24))) {
            if (!document.comboBox.isPopupVisible()) {
              document.comboBox.setPopupVisible(true);
              if (key <= KeyEvent.VK_DOWN)
                e.consume();
            }
          }
        }
        
        @Override
        public void keyReleased(KeyEvent e) {
          doSearch = false;
        }
      });
    }
    
    public int getIndex() {
      return document.getIndex();
    }
    
    //    public void setBorder(Border b) {}
  }
  
  class BasicComboBoxDocument extends PlainDocument implements
  PropertyChangeListener {
    protected JComboBox comboBox;
    protected ComboBoxModel comboBoxModel;
    protected int lastIndex = 0;
    protected JTextField textField;
    
    public BasicComboBoxDocument(JComboBox comboBox, JTextField textField) {
      this.comboBox = comboBox;
      comboBox.addPropertyChangeListener(this);
      this.textField = textField;
      comboBoxModel = comboBox.getModel();
    }
    
    public void propertyChange(PropertyChangeEvent e) {
      if (e.getPropertyName().equals("model")) {
        comboBoxModel = (ComboBoxModel) e.getNewValue();
      }
    }
    
    @Override
    public void insertString(int offs, String str, AttributeSet a) throws
    BadLocationException {
      if (str == null) {
        return;
      }
      
      super.insertString(offs, str, a);
      
      String searchText = getText(0, getLength());
      
      int size = comboBoxModel.getSize();
      //rh wenn keine Datensätze in der Auswahl vorhanden sind, gleich rausgehen
      if(size == 0)
      {
        return;
      }
      
      String value;
      
      //Nur suchen, wenn auch tatsächlich eine Taste gedrückt wurde!
      if (textField instanceof ComboBoxTextField &&
          ((ComboBoxTextField) textField).doSearch) {
        
        ((ComboBoxTextField) textField).doSearch = false;
        
        //Suche nach exaktem Treffer:
        if (comboBoxModel instanceof DBComboBoxModel) {
          DBComboBoxModel m = (DBComboBoxModel) comboBoxModel;
          int index = m.locate(searchText, false);
          if (index >= 0) {
            selectItem(searchText, index);
            return;
          }
        }
        else {
          for (int i = 0; i < size; i++) {
            Object valueObj = comboBoxModel.getElementAt(i);
            if (valueObj == null) {
              value = "";
            }
            else {
              value = valueObj.toString();
            }
            if (value.toUpperCase().equals(searchText.toUpperCase())) {
              selectItem(searchText, i);
              return;
            }
          }
        }
        
        //Suche nach Teilstring:
        if (comboBoxModel instanceof DBComboBoxModel) {
          DBComboBoxModel m = (DBComboBoxModel) comboBoxModel;
          int index = m.locate(searchText, true);
          if (index >= 0) {
            selectItem(searchText, index);
            return;
          }
        }
        else {
          for (int i = 0; i < size; i++) {
            Object valueObj = comboBoxModel.getElementAt(i);
            if (valueObj == null) {
              value = "";
            }
            else {
              value = valueObj.toString();
            }
            if (value.toUpperCase().startsWith(searchText.toUpperCase())) {
              selectItem(searchText, i);
              return;
            }
          }
        }
      }
    }
    
    public void selectItem(String searchText, int i) {
      /*try {
        remove(0, getLength());
        Object valueObj = comboBoxModel.getElementAt(i);
        String value;
        if (valueObj == null) {
          value = "";
        }
        else {
          value = valueObj.toString();
        }
        super.insertString(0, value, null);*/
      comboBox.setSelectedIndex(i);
      int l = getLength();
      textField.setCaretPosition(Math.min(l, searchText.length()));
      textField.moveCaretPosition(l);
      lastIndex = i;
      /*}
      catch (BadLocationException ex) {
        ex.printStackTrace();
      }*/
    }
    
    public int getIndex() {
      return lastIndex;
    }
  }
}
