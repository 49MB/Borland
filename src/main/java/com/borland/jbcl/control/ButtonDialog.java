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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JPanel;

import com.borland.jbcl.util.ActionMulticaster;

@SuppressWarnings("serial")
public class ButtonDialog extends JDialog implements ActionListener,
    KeyListener, java.io.Serializable {
  public static final String NONE_COMMAND = "none";
  public static final String OK_COMMAND = "ok";
  public static final String YES_COMMAND = "yes";
  public static final String NO_COMMAND = "no";
  public static final String CANCEL_COMMAND = "cancel";
  public static final String DONE_COMMAND = "done";
  public static final String HELP_COMMAND = "help";
  public static final String APPLY_COMMAND = "apply";
  public static final String NEXT_COMMAND = "next";
  public static final String PREVIOUS_COMMAND = "previous";
  public static final String DETAILS_COMMAND = "details";
  
  // buttons to be passed in buttonSet & available in result
  public static final int NONE = 0x000; // until something else is set, or used
  // by external code to veto a close
  public static final int OK = 0x001; // these five will close the dialog
  public static final int YES = 0x002;
  public static final int NO = 0x004;
  public static final int CANCEL = 0x008;
  public static final int DONE = 0x010;
  public static final int HELP = 0x020;
  public static final int APPLY = 0x040;
  public static final int NEXT = 0x080;
  public static final int PREVIOUS = 0x100;
  public static final int DETAILS = 0x200;
  
  // pre-defined button sets
  public static final int OK_CANCEL = OK | CANCEL;
  public static final int YES_NO = YES | NO;
  public static final int YES_NO_CANCEL = YES | NO | CANCEL;
  public static final int OK_CANCEL_APPLY = OK | CANCEL | APPLY;
  public static final int APPLY_DONE = APPLY | DONE;
  public static final int ALL = OK | YES | NO | CANCEL | DONE | HELP | APPLY
      | NEXT | PREVIOUS | DETAILS;
  
  protected Container buttonPanel;
  protected transient ActionMulticaster actionMulticaster;
  
  protected int buttonSet;
  protected boolean centered = true; // centered on screen
  protected boolean enterOK = true; // allow the enter key to generate an OK
  // command
  protected boolean escapeCancel = true; // allow the escape key to generate a
  // CANCEL command
  protected ButtonDescriptor[] buttonDescriptors;
  protected String[] labels;
  protected JPanel buttonPanelHolder; // so we can have same size buttons
  protected int buttonAlignment = FlowLayout.CENTER;
  protected ButtonDescriptor result;
  protected Component centerPanel;
  protected java.util.Hashtable<Component, Component> listeningTo = new java.util.Hashtable<Component, Component>();
  
  protected static final ButtonDescriptor[] buttonSetToButtonDescriptors(
      int buttonSet) {
    Vector<ButtonDescriptor> descr = new Vector<ButtonDescriptor>();
    
    if ((buttonSet & PREVIOUS) != 0)
      descr.addElement(new ButtonDescriptor(PREVIOUS_COMMAND, Res._Previous,
          PREVIOUS, false));
    if ((buttonSet & NEXT) != 0)
      descr.addElement(new ButtonDescriptor(NEXT_COMMAND, Res._NextButton,
          NEXT, false));
    if ((buttonSet & DETAILS) != 0)
      descr.addElement(new ButtonDescriptor(DETAILS_COMMAND, Res._Details,
          DETAILS, false));
    
    if ((buttonSet & OK) != 0)
      descr.addElement(new ButtonDescriptor(OK_COMMAND, Res._OK, OK, true));
    if ((buttonSet & YES) != 0)
      descr.addElement(new ButtonDescriptor(YES_COMMAND, Res._Yes1, YES, true));
    if ((buttonSet & NO) != 0)
      descr.addElement(new ButtonDescriptor(NO_COMMAND, Res._No1, NO, true));
    if ((buttonSet & CANCEL) != 0)
      descr.addElement(new ButtonDescriptor(CANCEL_COMMAND, Res._Cancel1,
          CANCEL, true));
    if ((buttonSet & DONE) != 0)
      descr
          .addElement(new ButtonDescriptor(DONE_COMMAND, Res._Done, DONE, true));
    if ((buttonSet & HELP) != 0)
      descr.addElement(new ButtonDescriptor(HELP_COMMAND, Res._Help, HELP,
          false));
    if ((buttonSet & APPLY) != 0)
      descr.addElement(new ButtonDescriptor(APPLY_COMMAND, Res._Apply, APPLY,
          false));
    
    ButtonDescriptor[] list = new ButtonDescriptor[descr.size()];
    descr.copyInto(list);
    return list;
  }
  
  protected ButtonDialog(Frame frame, String title, boolean modal,
      Component centerPanel, Container buttonPanel,
      ButtonDescriptor[] buttonDescriptors) {
    super(frame, title, modal);
    
    this.buttonPanel = buttonPanel;
    
    if (buttonPanel == null) {
      buttonPanelHolder = new JPanel();
      buttonPanelHolder.setLayout(new FlowLayout(FlowLayout.CENTER));
      this.buttonPanel = new JPanel();
      this.buttonPanel.setLayout(new GridLayout(1, 0, 6, 0));
      buttonPanelHolder.add(this.buttonPanel);
    }
    
    if (centerPanel != null) {
      getContentPane().setLayout(new BorderLayout());
      setBackground(SystemColor.control);
      getContentPane().add(centerPanel, BorderLayout.CENTER);
      if (buttonPanelHolder != null)
        getContentPane().add(buttonPanelHolder, BorderLayout.SOUTH);
      else
        getContentPane().add(this.buttonPanel, BorderLayout.SOUTH);
    }
    
    this.buttonSet = -1;
    setButtonSet(buttonDescriptors);
    
    // add ourselves as key listener to EVERY child!
    listenForKeys(this);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    
    pack();
  }
  
  protected ButtonDialog(Frame frame, String title, Component centerPanel,
      int buttonSet) {
    this(frame, title, true, centerPanel, null,
        buttonSetToButtonDescriptors(buttonSet));
  }
  
  public ButtonDialog(Frame frame, String title, Component centerPanel) {
    this(frame, title, centerPanel, OK);
  }
  
  public Component getCenterPanel() {
    return centerPanel;
  }
  
  public void setCenterPanel(Component panel) {
    if (centerPanel != null) {
      dropKeyListeners(this);
    }
    centerPanel = panel;
    if (centerPanel != null) {
      getContentPane().setLayout(new BorderLayout());
      setBackground(SystemColor.control);
      getContentPane().add(centerPanel, BorderLayout.CENTER);
      if (buttonPanelHolder != null)
        getContentPane().add(buttonPanelHolder, BorderLayout.SOUTH);
      else
        getContentPane().add(this.buttonPanel, BorderLayout.SOUTH);
      // re-scan to listen for keys
      listenForKeys(this);
      
      pack();
    }
  }
  
  protected ButtonDialog(Frame frame, String title) {
    this(frame, title, null, OK);
  }
  
  protected ButtonDialog(Frame frame) {
    this(frame, "", null, OK);
  }
  
  /**
   * defines the set of buttons to be displayed as a set of or'ed button bits
   * sample: setButtonSet(ButtonDialog.OK | ButtonDialog.CANCEL |
   * ButtonDialog.HELP) or: setButtonSet(ButtonDialog.OK_CANCEL)
   */
  public void setButtonSet(int bs) {
    // System.err.println("ButtonDialog.setButtonSet " + bs);
    if ((bs & ~ALL) != 0)
      throw new IllegalArgumentException(java.text.MessageFormat.format(
          Res._InvalidButtons, new Object[] { Integer.toString(bs & ~ALL) }));
    if (bs != buttonSet) {
      buttonSet = bs;
      setButtonSet(buttonSetToButtonDescriptors(bs));
    }
  }
  
  private void setButtonSet(ButtonDescriptor[] buttonDescriptors) {
    if (this.buttonDescriptors != null) {
      for (int i = 0; i < this.buttonDescriptors.length; i++)
        this.buttonDescriptors[i].button.removeActionListener(this);
    }
    this.buttonDescriptors = buttonDescriptors;
    
    buttonPanel.removeAll();
    
    for (int i = 0; i < buttonDescriptors.length; i++) {
      ButtonControl b = new ButtonControl();
      buttonDescriptors[i].button = b;
      b.setActionCommand(buttonDescriptors[i].command);
      b.addActionListener(this);
      buttonPanel.add(b);
    }
    
    setDefaultLabels();
    setupButtonLabels();
  }
  
  public int getButtonSet() {
    return buttonSet;
  }
  
  public void setCentered(boolean c) {
    centered = c;
  }
  
  public boolean isCentered() {
    return centered;
  }
  
  public void setEnterOK(boolean e) {
    enterOK = e;
  }
  
  public boolean isEnterOK() {
    return enterOK;
  }
  
  public void setEscapeCancel(boolean c) {
    escapeCancel = c;
  }
  
  public boolean isEscapeCancel() {
    return escapeCancel;
  }
  
  public void setButtonAlignment(int alignment) {
    if (alignment == buttonAlignment || buttonPanelHolder == null)
      return;
    
    FlowLayout flow = (FlowLayout) buttonPanelHolder.getLayout();
    
    if (alignment == FlowLayout.LEFT || alignment == FlowLayout.CENTER
        || alignment == FlowLayout.RIGHT)
      flow.setAlignment(alignment);
    else
      throw new IllegalArgumentException();
    
    buttonAlignment = alignment;
    invalidate();
    repaint(100);
  }
  
  public int getButtonAlignment() {
    return buttonAlignment;
  }
  
  public synchronized void addActionListener(ActionListener l) {
    // Diagnostic.println("ButtonDialog.addActionListener(" +
    // l.getClass().getName() + ")");
    if (actionMulticaster == null)
      actionMulticaster = new ActionMulticaster();
    actionMulticaster.add(l);
  }
  
  public synchronized void removeActionListener(ActionListener l) {
    if (actionMulticaster != null)
      actionMulticaster.remove(l);
  }
  
  protected void processActionEvent(ActionEvent e) {
    // System.err.println("ButtonDialog: processActionEvent " + e);
    result = buttonFromCommand(e.getActionCommand());
    if (actionMulticaster != null) {
      try {
        actionMulticaster.dispatch(e);
      } catch (Exception ex) {
        if (result != null && result.closeDialog)
          return;
      }
    }
    // these buttons will close the dialog. listeners can set result to null to
    // abort
    if (result != null && result.closeDialog
        || e.getActionCommand().equals(CANCEL_COMMAND)) {
      setVisible(false);
      dispose();
    }
  }
  
  /**
   * set the labels for the buttons actually used, or the whole button set.
   * setting all buttons signifies a label list for the whole set
   */
  public void setLabels(String[] l) {
    if (l != null)
      for (int i = 0; i < labels.length && i < l.length; i++)
        labels[i] = l[i];
    else
      setDefaultLabels();
    setupButtonLabels();
  }
  
  private void setDefaultLabels() {
    labels = new String[buttonDescriptors.length];
    for (int i = 0; i < labels.length; ++i)
      labels[i] = buttonDescriptors[i].label;
  }
  
  protected void setupButtonLabels() {
    if (labels.length == buttonDescriptors.length) {
      for (int i = 0; i < labels.length; i++)
        // label array is for whole set
        buttonDescriptors[i].button.setLabel(labels[i]);
    } else {
      for (int i = 0; i < buttonPanel.getComponentCount(); i++)
        // label array is for visible button set
        ((Button) buttonPanel.getComponent(i)).setLabel(labels[i]);
    }
  }
  
  public String[] getLabels() {
    return labels;
  }
  
  public void setResult(int id) {
    result = buttonFromID(id);
  }
  
  public int getResult() {
    return result != null ? result.getID() : NONE;
  }
  
  public ButtonDescriptor buttonFromID(int id) {
    for (int i = 0; i < buttonDescriptors.length; ++i)
      if (buttonDescriptors[i].id == id)
        return buttonDescriptors[i];
    return null;
  }
  
  public ButtonDescriptor buttonFromCommand(String actionCommand) {
    for (int i = 0; i < buttonDescriptors.length; ++i)
      if (buttonDescriptors[i].command.equals(actionCommand))
        return buttonDescriptors[i];
    return null;
  }
  
  public void enableButton(String actionCommand, boolean enableState) {
    ButtonDescriptor bd = buttonFromCommand(actionCommand);
    if (bd != null)
      bd.button.setEnabled(enableState);
  }
  
  public void show() {
    result = null;
    
    // pack();
    
    if (centered)
      centerOnScreen();
    else
      assureOnScreen();
    super.setVisible(true);
  }
  
  public void actionPerformed(ActionEvent e) {
    processActionEvent(e);
  }
  
  public Dimension getPreferredSize() {
    Dimension ps = super.getPreferredSize();
    // if (ps.width < 300)
    // ps.width = 300;
    // if (ps.height < 150)
    // ps.height = 150;
    return ps;
  }
  
  protected void processWindowEvent(WindowEvent e) {
    // System.err.println("processWindowEvent " + e);
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING)
      processActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
          CANCEL_COMMAND));
    else if (e.getID() == WindowEvent.WINDOW_OPENED) {
      // simulate tab from last to wrap around to first & properly set initial
      // focus
      if (getComponentCount() > 0)
        getComponent(getComponentCount() - 1).transferFocus();
    }
  }
  
  protected void centerOnScreen() {
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension s = getSize();
    setLocation((d.width - s.width) / 2, (d.height - s.height) / 2);
  }
  
  protected void assureOnScreen() {
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    Rectangle b = getBounds();
    if (b == null)
      return;
    if (b.x + b.width > d.width || b.y + b.height > d.height) {
      if (b.x + b.width > d.width)
        b.x -= b.x + b.width - d.width;
      if (b.y + b.height > d.height)
        b.y -= b.y + b.height - d.height;
      setLocation(b.x, b.y);
    }
  }
  
  protected void dropKeyListeners(Component comp) {
    for (Enumeration<Component> e = listeningTo.elements(); e.hasMoreElements();) {
      Component c = (Component) e.nextElement();
      c.removeKeyListener(this);
    }
    listeningTo = new Hashtable<Component, Component>();
    /*
     * comp.removeKeyListener(this); if (comp instanceof Container) { Container
     * c = (Container)comp; int count = c.getComponentCount(); for (int i = 0; i
     * < count; i++) dropKeyListeners(c.getComponent(i)); }
     */
  }
  
  protected void listenForKeys(Component comp) {
    // comp.removeKeyListener(this);
    if (listeningTo.get(comp) == null) {
      comp.addKeyListener(this);
      listeningTo.put(comp, comp);
    }
    if (comp instanceof Container) {
      Container c = (Container) comp;
      int count = c.getComponentCount();
      for (int i = 0; i < count; i++)
        listenForKeys(c.getComponent(i));
    }
  }
  
  public void dispose() {
    
    getContentPane().removeAll();
    if (centerPanel != null) {
      dropKeyListeners(centerPanel);
      centerPanel = null;
    }
    if (buttonDescriptors != null) {
      for (int i = 0; i < buttonDescriptors.length; i++)
        buttonDescriptors[i].button.removeActionListener(this);
    }
    super.dispose();
  }
  
  // KeyListener interface
  
  /**
   * Process standard dialog accelerators for help,ok,cancel
   */
  public void keyPressed(KeyEvent e) {
    if (e.isConsumed())
      return;
    if (e.getKeyCode() == KeyEvent.VK_ESCAPE
        && escapeCancel
        && (e.getModifiers() & (InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK | InputEvent.ALT_MASK)) == 0)

      processActionEvent(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, CANCEL_COMMAND));
    else if (e.getKeyCode() == KeyEvent.VK_ENTER
        && (e.getModifiers() & (InputEvent.SHIFT_MASK | InputEvent.ALT_MASK)) == 0) {

      boolean isCtrl = (e.getModifiers() & InputEvent.CTRL_MASK) != 0;
      boolean needsCtrl = !enterOK || e.getSource() instanceof TextArea;
      if (isCtrl == needsCtrl)
        processActionEvent(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, OK_COMMAND));

    } else if (e.getKeyCode() == KeyEvent.VK_F1
        && (e.getModifiers() & (InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK | InputEvent.ALT_MASK)) == 0)

      processActionEvent(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, HELP_COMMAND));
  }
  
  /**
   *
   */
  public void keyTyped(KeyEvent e) {
  }
  
  /**
   *
   */
  public void keyReleased(KeyEvent e) {
  }
  
}
