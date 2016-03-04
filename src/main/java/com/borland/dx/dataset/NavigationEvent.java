//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.dataset;

import java.awt.Component;
import java.util.EventListener;

import com.borland.dbswing.DBUtilities;

/**
 * This class is used to provide notification that the DataSet's cursor position
 * has changed. This class is used by data-aware controls so that the UI can
 * respond to the change in cursor location.
 * <p>
 * This event is sent out any time the current row position changes due to
 * navigation operations like first, last, next, prior, and for editing
 * operations like deleteRow and insertRow. Also note that post() can cause
 * navigation since post() will navigate the newly posted row to its correct
 * position in the DataSet. For sorted DataSets the posted position is
 * determined by sort order. For non-sorted DataSets the posted position is at
 * the end of the DataSet.
 */
public class NavigationEvent extends com.borland.jb.util.DispatchableEvent {
  
  public static final int MOVE_FIRST = 1;
  public static final int MOVE_NEXT = 2;
  public static final int MOVE_PRIOR = 3;
  public static final int MOVE_LAST = 4;
  
  public static final int MOVE_LOCATE = 5;
  
  public static final int BEFORE_INSERT = 10;
  public static final int AFTER_POST = 11;
  public static final int MOVE_BY_STATUS = 12;
  public static final int MOVE_GOTO = 13;
  public static final int MOVE_DELETED = 14;
  
  public static final int REFRESH = 20;
  
  private final int reason;
  
  /**
   * Creates a NavigationEvent object.
   * 
   * @param source
   */
  public NavigationEvent(Object source, int reason) {
    super(source);
    this.reason = reason;
  }
  
  public int getReason() {
    return reason;
  }
  
  /**
   * This method is an implementation of DispatchableEvent that an
   * EventMulticaster uses to dispatch an event of this type to the listener.
   * 
   * @param listener
   *          The listener to dispatch this event to.
   * @see com.borland.jb.util.DispatchableEvent
   * @see com.borland.jb.util.EventMulticaster
   */
  @Override
  public void dispatch(final EventListener listener) {
    if (listener instanceof Component || listener instanceof DataSetAware
        || listener instanceof SwingAware)
      DBUtilities.invokeOnSwingThread(new Runnable() {
        @Override
        public void run() {
          ((NavigationListener) listener).navigated(NavigationEvent.this);
        }
      });
    else
      ((NavigationListener) listener).navigated(this);
  }
  
}
