package com.borland.dbswing.plaf;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.TableModel;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.borland.dbswing.JdbComboBox;
import com.borland.dbswing.JdbTable;
import com.borland.dbswing.TableScrollPane;
import com.borland.dbswing.plaf.basic.BasicJdbComboBoxEditor;
import com.borland.dx.dataset.PickListDescriptor;

public class MultiDBComboBoxUI implements MethodInterceptor {
  MultiHandler multiHandler;
  
  public Object intercept(Object obj, Method method, Object[] args,
      MethodProxy methodProxy) throws Throwable {
    Object returnValue;
    if (multiHandler == null)
      multiHandler = new MultiHandler();
    
    String name = method.getName();
    // installUI Method:
    // System.out.println(name);
    if (name.equals("installUI")) {
      multiHandler.myComboBox = (JComboBox) args[0];
      returnValue = methodProxy.invokeSuper(obj, args);
    } else if (name.equals("uninstallUI")) {
      if (multiHandler != null)
        multiHandler.uninstallUI((JdbComboBox) args[0]);
      returnValue = methodProxy.invokeSuper(obj, args);
    }
    // createPopup Method:
    else if (name.equals("createPopup")) {
      return multiHandler.createPopup((BasicComboBoxUI) obj);
    }
    // createFocusListener():
    else if (name.equals("createFocusListener")) {
      returnValue = multiHandler.createFocusListener();
    } else if (name.equals("createPropertyChangeListener")) {
      returnValue = methodProxy.invokeSuper(obj, args);
      returnValue = multiHandler
          .createPropertyChangeListener((PropertyChangeListener) returnValue);
    } else if (name.equals("createKeyListener")) {
      if (multiHandler.myComboBox instanceof JdbComboBox
          && ((JdbComboBox) multiHandler.myComboBox).isCatchUIKeyListener())
        returnValue = ((JdbComboBox) multiHandler.myComboBox)
            .getUiKeyListener();
      else
        returnValue = methodProxy.invokeSuper(obj, args);
    }
    // getPopup
    else if (name.equals("getPopup")) {
      returnValue = multiHandler.getPopup();
    }
    // setUseLookAheadComboBoxEditor
    else if (name.equals("setUseLookAheadComboBoxEditor")) {
      multiHandler.setUseLookAheadComboBoxEditor((Boolean) args[0]);
      returnValue = null;
    }
    // isUseLookAheadComboBoxEditor
    else if (name.equals("isUseLookAheadComboBoxEditor")) {
      returnValue = multiHandler.isUseLookAheadComboBoxEditor();
    } else if (name.equals("createEditor")) {
      if (multiHandler.isUseLookAheadComboBoxEditor())
        returnValue = new BasicJdbComboBoxEditor(multiHandler.myComboBox);
      else
        returnValue = methodProxy.invokeSuper(obj, args);
    } else if (name.equals("isShowTable")) {
      returnValue = multiHandler.isShowTable();
    } else if (name.equals("getDisplaySize")) {
      returnValue = multiHandler.getDisplaySize();
      if (returnValue == null)
        returnValue = methodProxy.invokeSuper(obj, args);
    } else if (name.equals("selectNextPossibleValue")) {
      if (!multiHandler.selectNextPossibleValue())
        methodProxy.invokeSuper(obj, args);
      returnValue = null;
    } else if (name.equals("selectPreviousPossibleValue")) {
      if (!multiHandler.selectPreviousPossibleValue())
        methodProxy.invokeSuper(obj, args);
      returnValue = null;
    }
    // Other Methods:
    else
      returnValue = methodProxy.invokeSuper(obj, args);
    
    return returnValue;
  }
  
}

class MultiHandler implements PropertyChangeListener {
  public ComboPopup createPopup(BasicComboBoxUI obj) {
    ComboPopup popup;
    if (myComboBox instanceof JdbComboBoxPopupFactory)
      popup = ((JdbComboBoxPopupFactory) myComboBox).createPopupWindow(obj,
          myComboBox);
    else
      popup = new WindowsJdbComboPopup(myComboBox);
    if (popup instanceof BasicComboPopup)
      ((BasicComboPopup) popup).getAccessibleContext().setAccessibleParent(
          myComboBox);
    this.popup = popup;
    return popup;
  }
  
  public FocusListener createFocusListener() {
    return new FocusListener() {
      public void focusGained(FocusEvent e) {
      }
      
      public void focusLost(FocusEvent e) {
        ComboBoxModel model = myComboBox.getModel();
        if (model instanceof JdbComboBox.DBComboBoxModel) {
          ((JdbComboBox.DBComboBoxModel) model).setSelectedIndex(myComboBox
              .getSelectedIndex());
        }
      }
    };
  }
  
  public ComboPopup getPopup() {
    return popup;
  }
  
  public void setUseLookAheadComboBoxEditor(boolean useLookAheadComboBoxEditor) {
    this.useLookAheadComboBoxEditor = useLookAheadComboBoxEditor;
  }
  
  public boolean isUseLookAheadComboBoxEditor() {
    return useLookAheadComboBoxEditor;
  }
  
  protected boolean isShowTable() {
    return myComboBox != null
        && myComboBox.getModel() instanceof JdbComboBox.DBComboBoxModel;
  }
  
  public class WindowsJdbComboPopup extends BasicComboPopup implements
      JdbComboPopupGetter {
    protected JdbTable table;
    protected Timer autoScrollTimer;
    protected boolean hasEntered = false;
    protected boolean isAutoScrolling = false;
    // protected int scrollDirection = BasicComboPopup.SCROLL_UP;
    protected int scrollDirection = 0;
    
    public WindowsJdbComboPopup(JComboBox cBox) {
      super(cBox);
      /**
       * This is a workaround for a Java bug.(BTS 71921) Overloaded function
       * createKeyListener() is renamed to createKeyListener1(),
       * createListSelectionListener() to createListSelectionListener1(). Defer
       * installation of some listeners until super's construction is completed.
       */
      keyListener = createKeyListener1();
      listSelectionListener = createListSelectionListener1();
      cBox.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (e.getActionCommand().equals("comboBoxEdited")) {
            if (isShowTable()) {
              comboBox.setSelectedIndex(getTable().getSelectedRow());
            } else {
              comboBox.setSelectedIndex(getList().getSelectedIndex());
            }
          }
        }
      });
    }
    
    /**
     * (SS) show zeigt die Tabelle an als deprecated gekennzeichnet um
     * compiler-Warnings zu vermeiden
     * 
     * @deprecated
     */
    @Deprecated
    @Override
    public void show() {
      Dimension popupSize = comboBox.getSize();
      int popupWidth = popupSize.width;
      if (dropDownWidth != -1) {
        popupWidth = dropDownWidth;
      } else {
        if (isShowTable()) {
          table.createDefaultColumnsFromModel();
          popupWidth = table.getColumnModel().getTotalColumnWidth();
          Insets insets = scroller.getInsets();
          popupWidth += insets.left + insets.right + 16;
          if (popupWidth < comboBox.getWidth()) {
            popupWidth = comboBox.getWidth();
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
          }
          table.setEditable(false);
          table.setCellSelectionEnabled(false);
          table.setColumnSelectionAllowed(false);
          table.setRowSelectionAllowed(true);
        }
      }
      
      popupSize.setSize(popupWidth,
          getPopupHeightForRowCount(comboBox.getMaximumRowCount()));
      Rectangle popupBounds = computePopupBounds(0,
          comboBox.getBounds().height, popupSize.width, popupSize.height);
      scroller
          .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      scroller.setMaximumSize(popupBounds.getSize());
      scroller.setPreferredSize(popupBounds.getSize());
      scroller.setMinimumSize(popupBounds.getSize());
      // if (isShowTable()) {
      // table.invalidate();
      // syncTableSelectionWithComboBoxSelection();
      // }
      // else {
      // list.invalidate();
      syncListSelectionWithComboBoxSelection();
      // }
      
      setLightWeightPopupEnabled(comboBox.isLightWeightPopupEnabled());
      
      initialIndex = comboBox.getSelectedIndex();
      show(comboBox, popupBounds.x, popupBounds.y);
    }
    
    void syncListSelectionWithComboBoxSelection() {
      int selectedIndex = comboBox.getSelectedIndex();
      
      int maxIndex;
      if (isShowTable()) {
        maxIndex = table.getRowCount() - 1;
      } else {
        maxIndex = getJList().getMaxSelectionIndex();
      }
      if (selectedIndex == -1 || selectedIndex > maxIndex) {
        if (isShowTable()) {
          table.getSelectionModel().clearSelection();
        } else {
          getJList().clearSelection();
        }
      } else {
        if (isShowTable()) {
          table.invalidate();
          table.setRowSelectionInterval(selectedIndex, selectedIndex);
          table.ensureRowIsVisible(selectedIndex, first);
          // Beim ersten mal stimmt seltsamerweise der Index nie, somit an den
          // Anfang scrollen
          first = false;
        } else {
          getJList().invalidate();
          getJList().setSelectedIndex(selectedIndex);
          getJList().ensureIndexIsVisible(getJList().getSelectedIndex());
        }
      }
    }
    
    @Override
    protected int getPopupHeightForRowCount(int maxRowCount) {
      int currentSize = comboBox.getModel().getSize();
      int headerHeight = 0;
      
      if (isShowTable()) {
        headerHeight = table.getTableHeader().getHeight();
        if (headerHeight == 0) {
          headerHeight = fixedCellHeight + 3;
        }
        headerHeight += table.getRowMargin() * 2;
      }
      
      if (fixedCellHeight != -1 && currentSize > 0) {
        if (isShowTable()) {
          table.setRowHeight(fixedCellHeight);
        } else {
          getJList().setFixedCellHeight(fixedCellHeight);
        }
        
        if (maxRowCount < currentSize) {
          return (fixedCellHeight * maxRowCount) + headerHeight;
        } else {
          return (fixedCellHeight * currentSize) + headerHeight;
        }
      } else {
        if (currentSize > 0) {
          int height;
          if (isShowTable()) {
            height = table.getRowHeight();
          } else {
            height = getJList().getCellBounds(0, 0).height;
          }
          
          if (maxRowCount < currentSize) {
            return (height * maxRowCount) + headerHeight;
          } else {
            return (height * currentSize) + headerHeight;
          }
        } else {
          return 100;
        }
      }
    }
    
    protected void configureTable() {
      table.setFont(comboBox.getFont());
      if (comboBox.getRenderer() == null) {
        table.setForeground(comboBox.getForeground());
        table.setBackground(comboBox.getBackground());
        table.setSelectionForeground(UIManager
            .getColor("ComboBox.selectionForeground"));
        table.setSelectionBackground(UIManager
            .getColor("ComboBox.selectionBackground"));
      }
      table.setRowHeaderVisible(false);
      // table.setColumnHeaderVisible(false);
      table.setRowSelectionAllowed(true);
      table.setAutoSelection(false);
      table.setBorder(null);
      table.setPopupMenuEnabled(false);
      table.setColumnSortEnabled(false);
      table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
      // table.setShowHorizontalLines(false);
      // table.setCellRenderer(comboBox.getRenderer());
      table.setRequestFocusEnabled(false);
      table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      if (myComboBox.isPopupVisible())
        syncListSelectionWithComboBoxSelection();
      JdbComboBox.DBComboBoxModel tableModel = (JdbComboBox.DBComboBoxModel) table
          .getModel();
      PickListDescriptor pickList = tableModel.getPickListDescriptor();
      // DataSet pickListDataSet = pickList.getPickListDataSet();
      String[] displayColumns = pickList.getPickListDisplayColumns();
      ArrayList<String> hiddenColumns = new ArrayList<String>();
      String columnName;
      boolean visible;
      for (int colNo = 0; colNo < tableModel.getColumnCount(); colNo++) {
        columnName = tableModel.getColumn(colNo).getColumnName();
        visible = false;
        for (String displayColumn : displayColumns) {
          if (displayColumn.equalsIgnoreCase(columnName)) {
            visible = true;
            break;
          }
        }
        if (!visible)
          hiddenColumns.add(columnName);
      }
      table.setHiddenColumns(hiddenColumns.toArray(new String[hiddenColumns
          .size()]));
      installTableListeners();
    }
    
    protected void installTableListeners() {
      // In case they've already been added.
      table.getSelectionModel().removeListSelectionListener(
          listSelectionListener);
      table.removeMouseMotionListener(listMouseMotionListener);
      table.removeMouseListener(listMouseListener);
      
      table.getSelectionModel().addListSelectionListener(listSelectionListener);
      table.addMouseMotionListener(listMouseMotionListener);
      table.addMouseListener(listMouseListener);
    }
    
    protected void setValueIsAdjusting(boolean valueIsAdjusting) {
      this.valueIsAdjusting = valueIsAdjusting;
    }
    
    protected boolean isValueIsAdjusting() {
      return valueIsAdjusting;
    }
    
    protected class JdbSelectionHandler extends
        BasicComboPopup.ListSelectionHandler {
      boolean lightNav = false;
      
      public JdbSelectionHandler() {
        Object keyNav = getComboBox().getClientProperty(
            "JComboBox.lightweightKeyboardNavigation");
        // Object keyNav =
        // WindowsJdbComboBoxUI.this.myComboBox.getClientProperty("JComboBox.lightweightKeyboardNavigation");
        if (keyNav != null) {
          // if (keyNav.equals("Lightweight")) {
          // lightNav = true;
          // }
          // else if (keyNav.equals("Heavyweight")) {
          if (keyNav.equals("Heavyweight")) {
            lightNav = false;
          }
        }
      }
      
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (isShowTable()) {
          if (!lightNav && !isValueIsAdjusting() && !e.getValueIsAdjusting()
              && table.getSelectedRow() != getComboBox().getSelectedIndex()
              && table.getSelectedRow() < getComboBox().getItemCount() &&
              // table.getSelectedRow() !=
              // WindowsJdbComboBoxUI.this.myComboBox.getSelectedIndex() &&
              // table.getSelectedRow() <
              // WindowsJdbComboBoxUI.this.myComboBox.getItemCount() &&
              table.getSelectedRow() >= -1) {
            
            setValueIsAdjusting(true);
            JComboBox comboBox = getComboBox();
            if (comboBox instanceof JdbComboBox) {
              JdbComboBox dbComboBox = (JdbComboBox) comboBox;
              dbComboBox.setValueIsAdjusting(true);
              try {
                JdbComboBox.DBComboBoxModel comboBoxModel = (JdbComboBox.DBComboBoxModel) comboBox
                    .getModel();
                comboBoxModel.setSelectedIndex(table.getSelectedRow());
                // comboBox.setSelectedIndex(table.getSelectedRow());
                // WindowsJdbComboBoxUI.this.myComboBox.setSelectedIndex(table.getSelectedRow());
                table.ensureRowIsVisible(table.getSelectedRow());
              } finally {
                dbComboBox.setValueIsAdjusting(false);
                setValueIsAdjusting(false);
              }
            } else {
              comboBox.getModel().setSelectedItem(table.getSelectedRow());
            }
          }
        } else {
          super.valueChanged(e);
        }
      }
    }
    
    protected class JdbMouseHandler extends MouseAdapter {
      @Override
      public void mousePressed(MouseEvent e) {
      }
      
      @Override
      public void mouseReleased(MouseEvent anEvent) {
        if (isShowTable()) {
          getComboBox().setSelectedIndex(table.getSelectedRow());
        } else {
          getComboBox().setSelectedIndex(getJList().getSelectedIndex());
        }
        initialIndex = -1;
        hide();
      }
    }
    
    protected class JdbMouseMotionHandler extends MouseMotionAdapter {
      // public void mouseDragged(MouseEvent anEvent) {
      // mouseMoved(anEvent);
      // }
      
      @Override
      public void mouseMoved(MouseEvent anEvent) {
        Point location = anEvent.getPoint();
        Rectangle r = new Rectangle();
        if (isShowTable()) {
          table.computeVisibleRect(r);
        } else {
          getJList().computeVisibleRect(r);
        }
        if (r.contains(location)) {
          setValueIsAdjusting(true);
          updateListBoxSelectionForEvent(anEvent, false);
          setValueIsAdjusting(false);
        }
      }
    }
    
    protected class JdbItemHandler implements ItemListener {
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED && !isValueIsAdjusting()) {
          if (myComboBox.isPopupVisible()) {
            setValueIsAdjusting(true);
            syncListSelectionWithComboBoxSelection();
            setValueIsAdjusting(false);
          }
          // not necessary, because done in
          // syncListSelectionWithComboBoxSelection()
          // list.ensureIndexIsVisible(comboBox.getSelectedIndex());
        }
      }
    }
    
    protected class JdbInvocationMouseHandler extends MouseAdapter {
      @Override
      public void mousePressed(MouseEvent e) {
        // Rectangle r;
        
        if (!SwingUtilities.isLeftMouseButton(e)) {
          return;
        }
        
        if (!getComboBox().isEnabled()) {
          // if (!WindowsJdbComboBoxUI.this.myComboBox.isEnabled()) {
          return;
        }
        
        jdbDelegateFocus(e);
        
        jdbTogglePopup();
      }
      
      @Override
      public void mouseReleased(MouseEvent e) {
        Component source = (Component) e.getSource();
        Dimension size = source.getSize();
        Rectangle bounds = new Rectangle(0, 0, size.width - 1, size.height - 1);
        if (!bounds.contains(e.getPoint())) {
          MouseEvent newEvent = convertMouseEvent(e);
          Point location = newEvent.getPoint();
          Rectangle r = new Rectangle();
          if (isShowTable()) {
            table.computeVisibleRect(r);
          } else {
            getJList().computeVisibleRect(r);
          }
          if (r.contains(location)) {
            updateListBoxSelectionForEvent(newEvent, false);
            if (isShowTable()) {
              getComboBox().setSelectedIndex(table.getSelectedRow());
            } else {
              getComboBox().setSelectedIndex(getJList().getSelectedIndex());
            }
          }
          initialIndex = -1;
          hide();
        }
        hasEntered = false;
        WindowsJdbComboPopup.this.stopAutoScrolling();
      }
    }
    
    protected void jdbDelegateFocus(MouseEvent e) {
      if (getComboBox().isEditable()) {
        // if (WindowsJdbComboBoxUI.this.myComboBox.isEditable()) {
        getComboBox().getEditor().getEditorComponent().requestFocus();
        // WindowsJdbComboBoxUI.this.myComboBox.getEditor().getEditorComponent().requestFocus();
      } else {
        getComboBox().requestFocus();
        // WindowsJdbComboBoxUI.this.myComboBox.requestFocus();
      }
    }
    
    protected void jdbTogglePopup() {
      if (isVisible()) {
        hide();
      } else {
        show();
      }
    }
    
    protected class JdbInvocationMouseMotionHandler extends MouseMotionAdapter {
      @Override
      public void mouseDragged(MouseEvent e) {
        if (isVisible()) {
          MouseEvent newEvent = convertMouseEvent(e);
          Rectangle r = new Rectangle();
          if (isShowTable()) {
            table.computeVisibleRect(r);
          } else {
            getJList().computeVisibleRect(r);
          }
          
          if (newEvent.getPoint().y >= r.y
              && newEvent.getPoint().y <= r.y + r.height - 1) {
            hasEntered = true;
            if (isAutoScrolling) {
              WindowsJdbComboPopup.this.stopAutoScrolling();
            }
            Point location = newEvent.getPoint();
            if (r.contains(location)) {
              setValueIsAdjusting(true);
              updateListBoxSelectionForEvent(newEvent, false);
              setValueIsAdjusting(false);
            }
          } else {
            if (hasEntered) {
              int directionToScroll = newEvent.getPoint().y < r.y ? SCROLL_UP
                  : SCROLL_DOWN;
              if (isAutoScrolling && scrollDirection != directionToScroll) {
                WindowsJdbComboPopup.this.stopAutoScrolling();
                WindowsJdbComboPopup.this.startAutoScrolling(directionToScroll);
              } else if (!isAutoScrolling) {
                WindowsJdbComboPopup.this.startAutoScrolling(directionToScroll);
              }
            } else {
              if (e.getPoint().y < 0) {
                hasEntered = true;
                // WindowsJdbComboPopup.this.startAutoScrolling(BasicComboPopup.SCROLL_UP);
                WindowsJdbComboPopup.this.startAutoScrolling(0);
              }
            }
          }
        }
      }
    }
    
    @Override
    protected MouseEvent convertMouseEvent(MouseEvent e) {
      Point convertedPoint;
      if (isShowTable()) {
        convertedPoint = SwingUtilities.convertPoint((Component) e.getSource(),
            e.getPoint(), table);
      } else {
        convertedPoint = SwingUtilities.convertPoint((Component) e.getSource(),
            e.getPoint(), list);
      }
      MouseEvent newEvent = new MouseEvent((Component) e.getSource(),
          e.getID(), e.getWhen(), e.getModifiers(), convertedPoint.x,
          convertedPoint.y, e.getModifiers(), e.isPopupTrigger());
      return newEvent;
    }
    
    public class JdbInvocationKeyHandler extends KeyAdapter {
      boolean lightNav = false;
      
      public JdbInvocationKeyHandler() {
        Object keyNav = getComboBox().getClientProperty(
            "JComboBox.lightweightKeyboardNavigation");
        // Object keyNav =
        // WindowsJdbComboBoxUI.this.myComboBox.getClientProperty("JComboBox.lightweightKeyboardNavigation");
        if (keyNav != null) {
          // if (keyNav.equals("Lightweight")) {
          // lightNav = true;
          // }
          // else if (keyNav.equals("Heavyweight")) {
          if (keyNav.equals("Heavyweight")) {
            lightNav = false;
          }
        }
      }
      
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && !e.isAltDown()) {
          if (isVisible()) {
            autoScrollUp();
          } else {
            if (isShowTable()) {
              if (table.getSelectedRow() > 0) {
                getComboBox().setSelectedIndex(table.getSelectedRow() - 1);
              }
            } else {
              if (getJList().getSelectedIndex() > 0) {
                getComboBox().setSelectedIndex(
                    getJList().getSelectedIndex() - 1);
              }
            }
          }
          e.consume();
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && !e.isAltDown()) {
          if (isVisible()) {
            autoScrollDown();
          } else {
            if (isShowTable()) {
              if (table.getSelectedRow() < (table.getRowCount() - 1)) {
                getComboBox().setSelectedIndex(table.getSelectedRow() + 1);
              }
            } else {
              if (getJList().getSelectedIndex() < (getJList().getModel()
                  .getSize() - 1)) {
                getComboBox().setSelectedIndex(
                    getJList().getSelectedIndex() + 1);
              }
            }
          }
          e.consume();
        } else if (!comboBox.isEditable() && popup.isVisible()) {
          if (e.getKeyCode() == KeyEvent.VK_SPACE
              || e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (lightNav) {
              if (isShowTable()) {
                getComboBox().setSelectedIndex(table.getSelectedRow());
              } else {
                getComboBox().setSelectedIndex(getJList().getSelectedIndex());
              }
            } else {
              if (isShowTable()) {
                getComboBox().setSelectedIndex(table.getSelectedRow());
              } else {
                getComboBox().setSelectedIndex(getJList().getSelectedIndex());
              }
              hide();
            }
            initialIndex = -1;
            if (e.getKeyCode() != KeyEvent.VK_ENTER)
              e.consume();
          }
        }
        
      }
      
      @Override
      public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F4) {
          if (isVisible()) {
            initialIndex = -1;
            if (isShowTable()) {
              getComboBox().setSelectedIndex(table.getSelectedRow());
            } else {
              getComboBox().setSelectedIndex(getJList().getSelectedIndex());
            }
            hide();
          } else {
            show();
          }
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
          if (initialIndex != -1) {
            getComboBox().setSelectedIndex(initialIndex);
            if (myComboBox.isPopupVisible())
              syncListSelectionWithComboBoxSelection();
            initialIndex = -1;
          }
          if (isVisible()) {
            hide();
          }
        } /*
           * else if (e.isAltDown() && e.getKeyCode() != KeyEvent.VK_ALT) { if
           * (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() ==
           * KeyEvent.VK_DOWN) { if (isVisible()) { initialIndex = -1; if
           * (isShowTable()) {
           * getComboBox().setSelectedIndex(table.getSelectedRow()); } else {
           * getComboBox().setSelectedIndex(getJList().getSelectedIndex()); }
           * hide(); } else { show(); } } }
           */else if (comboBox.isEditable()
            && !isVisible()
            && (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN)) {
          show();
        }
      }
    }
    
    @Override
    protected MouseListener createMouseListener() {
      return new JdbInvocationMouseHandler();
    }
    
    @Override
    protected MouseMotionListener createMouseMotionListener() {
      return new JdbInvocationMouseMotionHandler();
    }
    
    // protected KeyListener createKeyListener() {
    // Renamed for Bug 71921
    protected KeyListener createKeyListener1() {
      return new JdbInvocationKeyHandler();
    }
    
    // protected ListSelectionListener createListSelectionListener() {
    // Renamed for Bug 71921
    protected ListSelectionListener createListSelectionListener1() {
      return new JdbSelectionHandler();
    }
    
    @Override
    protected MouseListener createListMouseListener() {
      return new JdbMouseHandler();
    }
    
    @Override
    protected MouseMotionListener createListMouseMotionListener() {
      return new JdbMouseMotionHandler();
    }
    
    @Override
    protected PropertyChangeListener createPropertyChangeListener() {
      return new JdbComboPopupPropertyChangeHandler();
    }
    
    @Override
    protected ItemListener createItemListener() {
      return new JdbItemHandler();
    }
    
    @Override
    protected void updateListBoxSelectionForEvent(MouseEvent anEvent,
        boolean shouldScroll) {
      Point location = anEvent.getPoint();
      int index = 0;
      if (isShowTable()) {
        if (table == null) {
          return;
        }
        index = table.rowAtPoint(location);
      } else {
        if (getJList() == null) {
          return;
        }
        index = getJList().locationToIndex(location);
      }
      
      if (index == -1) {
        if (location.y < 0) {
          index = 0;
        } else {
          index = getComboBox().getModel().getSize() - 1;
          // index = WindowsJdbComboBoxUI.this.myComboBox.getModel().getSize() -
          // 1;
        }
      }
      
      if (isShowTable()) {
        if (table.getSelectedRow() != index) {
          table.setRowSelectionInterval(index, index);
          if (shouldScroll) {
            table.ensureRowIsVisible(index);
          }
        }
        table.repaint();
      } else {
        if (getJList().getSelectedIndex() != index) {
          getJList().setSelectedIndex(index);
          if (shouldScroll) {
            getJList().ensureIndexIsVisible(index);
          }
        }
      }
    }
    
    @Override
    protected void startAutoScrolling(int direction) {
      if (isAutoScrolling) {
        autoScrollTimer.stop();
      }
      
      isAutoScrolling = true;
      
      // if (direction == BasicComboPopup.SCROLL_UP) {
      // scrollDirection = BasicComboPopup.SCROLL_UP;
      if (direction == 0) {
        scrollDirection = 0;
        Point convertedPoint;
        int top;
        if (isShowTable()) {
          convertedPoint = SwingUtilities.convertPoint(scroller,
              new Point(1, 1), table);
          top = table.rowAtPoint(convertedPoint);
          if (top != -1) {
            setValueIsAdjusting(true);
            table.setRowSelectionInterval(top, top);
            setValueIsAdjusting(false);
          }
        } else {
          convertedPoint = SwingUtilities.convertPoint(scroller,
              new Point(1, 1), list);
          top = getJList().locationToIndex(convertedPoint);
          setValueIsAdjusting(true);
          getJList().setSelectedIndex(top);
          setValueIsAdjusting(false);
        }
        
        AbstractAction timerAction = new AbstractAction() {
          public void actionPerformed(ActionEvent e) {
            autoScrollUp();
          }
          
          @Override
          public boolean isEnabled() {
            return true;
          }
        };
        
        autoScrollTimer = new Timer(100, timerAction);
      }
      // else if (direction == BasicComboPopup.SCROLL_DOWN) {
      // scrollDirection = BasicComboPopup.SCROLL_DOWN;
      else if (direction == 1) {
        scrollDirection = 1;
        Dimension size = scroller.getSize();
        if (isShowTable()) {
          Point convertedPoint = SwingUtilities.convertPoint(scroller,
              new Point(1, (size.height - 1) - 2), list);
          int bottom = table.rowAtPoint(convertedPoint);
          if (bottom != -1) {
            setValueIsAdjusting(true);
            table.setRowSelectionInterval(bottom, bottom);
            setValueIsAdjusting(false);
          }
        } else {
          Point convertedPoint = SwingUtilities.convertPoint(scroller,
              new Point(1, (size.height - 1) - 2), list);
          int bottom = getJList().locationToIndex(convertedPoint);
          setValueIsAdjusting(true);
          getJList().setSelectedIndex(bottom);
          setValueIsAdjusting(false);
        }
        
        AbstractAction timerAction = new AbstractAction() {
          public void actionPerformed(ActionEvent e) {
            autoScrollDown();
          }
          
          @Override
          public boolean isEnabled() {
            return true;
          }
        };
        
        autoScrollTimer = new Timer(100, timerAction);
      }
      autoScrollTimer.start();
    }
    
    @Override
    protected void stopAutoScrolling() {
      isAutoScrolling = false;
      
      if (autoScrollTimer != null) {
        autoScrollTimer.stop();
        autoScrollTimer = null;
      }
    }
    
    @Override
    protected void autoScrollUp() {
      if (isShowTable()) {
        int index = table.getSelectedRow();
        if (index > 0) {
          table.setRowSelectionInterval(index - 1, index - 1);
          table.ensureRowIsVisible(index - 1);
        }
      } else {
        int index = getJList().getSelectedIndex();
        if (index > 0) {
          getJList().setSelectedIndex(index - 1);
          getJList().ensureIndexIsVisible(index - 1);
        }
      }
    }
    
    @Override
    protected void autoScrollDown() {
      if (isShowTable()) {
        int index = table.getSelectedRow();
        int lastItem = table.getRowCount() - 1;
        if (index < lastItem) {
          table.setRowSelectionInterval(index + 1, index + 1);
          table.ensureRowIsVisible(index + 1);
        }
      } else {
        int index = getJList().getSelectedIndex();
        int lastItem = getJList().getModel().getSize() - 1;
        if (index < lastItem) {
          getJList().setSelectedIndex(index + 1);
          getJList().ensureIndexIsVisible(index + 1);
        }
      }
    }
    
    public JdbTable getTable() {
      return table;
    }
    
    protected JComboBox getComboBox() {
      return myComboBox;
      // return WindowsJdbComboBoxUI.this.myComboBox;
    }
    
    protected JList getJList() {
      return list;
    }
    
    protected void replaceScroller(JScrollPane scroller) {
      remove(this.scroller);
      this.scroller.removeKeyListener(keyListener);
      this.scroller = scroller;
      scroller.addKeyListener(keyListener);
      add(scroller);
    }
    
    // public class PropertyChangeHandler implements PropertyChangeListener {
    public class JdbComboPopupPropertyChangeHandler implements
        PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent e) {
        String propertyName = e.getPropertyName();
        
        if (propertyName.equals("model")) {
          if (((ComboBoxModel) e.getNewValue()) instanceof JdbComboBox.DBComboBoxModel) {
            if (table == null) {
              table = new JdbTable();
              table.setEditable(false);
            }
            table.setModel((TableModel) getComboBox().getModel());
            // table.setModel((TableModel)
            // WindowsJdbComboBoxUI.this.myComboBox.getModel());
            configureTable();
            replaceScroller(new TableScrollPane(table,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                // ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));
          }
        }
      }
    }
    
    /**
     * Creates the JList used in the popup to display the items in the combo box
     * model.
     * 
     * @return a <code>JList</code> used to display the combo box items
     */
    @Override
    protected JList createList() {
      return new JdbComboPopupListAdapter(this);
    }
    
    /**
     * Creates the JList used in the popup to display the items in the combo box
     * model. (SS) Es wird eine JDBListTableAdapter erzeugt, damit der Index
     * korrekt gesetzt wird
     * 
     * @return a <code>JList</code> used to display the combo box items
     */
    // protected JList createList()
    // {
    // return new JDBListTableAdapter(this);
    // }
  }
  
  public void uninstallUI(JComboBox comboBox) {
    comboBox.removePropertyChangeListener(this);
    if (old != null) {
      comboBox.removePropertyChangeListener(old);
      old = null;
    }
  }
  
  protected PropertyChangeListener createPropertyChangeListener(
      PropertyChangeListener old) {
    this.old = old;
    return this;
  }
  
  PropertyChangeListener old;
  
  public void propertyChange(PropertyChangeEvent e) {
    if (e.getPropertyName().equals("fixedCellHeight")) {
      fixedCellHeight = ((Integer) e.getNewValue()).intValue();
    } else if (e.getPropertyName().equals("dropDownWidth")) {
      dropDownWidth = ((Integer) e.getNewValue()).intValue();
    } else {
      if (old != null)
        old.propertyChange(e);
    }
  }
  
  protected Dimension getDisplaySize() {
    Dimension size;
    if (fixedCellHeight == -1 || dropDownWidth == -1) {
      return null;
    } else {
      size = new Dimension();
    }
    if (fixedCellHeight != -1) {
      size.height = fixedCellHeight;
    }
    if (dropDownWidth != -1) {
      size.width = dropDownWidth;
    }
    return size;
  }
  
  /**
   * Selects the next item in the list.
   */
  protected boolean selectNextPossibleValue() {
    if (popup instanceof JdbNextPrevItemInterface) {
      ((JdbNextPrevItemInterface) popup).selectNextPossibleValue();
      return true;
    } else if (popup instanceof WindowsJdbComboPopup) {
      WindowsJdbComboPopup p = (WindowsJdbComboPopup) popup;
      p.autoScrollDown();
      return true;
    } else
      return false;
  }
  
  /**
   * Selects the previous item in the list.
   */
  protected boolean selectPreviousPossibleValue() {
    if (popup instanceof JdbNextPrevItemInterface) {
      ((JdbNextPrevItemInterface) popup).selectPreviousPossibleValue();
      return true;
    } else if (popup instanceof WindowsJdbComboPopup) {
      WindowsJdbComboPopup p = (WindowsJdbComboPopup) popup;
      p.autoScrollUp();
      return true;
    } else
      return false;
  }
  
  private boolean useLookAheadComboBoxEditor = true;
  private int fixedCellHeight = -1;
  private int dropDownWidth = -1;
  private ComboPopup popup;
  protected JComboBox myComboBox; // workaround for package-protected JDK
  // 1.2.2 problem
  private int initialIndex = -1;
  private boolean first = true;
}
