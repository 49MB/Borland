//--------------------------------------------------------------------------------------------------
// $Header$
// Copyright (c) 1996-2002 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------

package com.borland.dx.sql.dataset;

import com.borland.jb.util.Diagnostic;

class TaskRunner extends java.lang.Thread {

  Task    task;
  static  int count;
  boolean completed;
  boolean hasWaiter;

  TaskRunner(Task task) {
    super("TaskRunner-"+count++);  //NORES
    this.task = task;
  }

  public void run() {
    try {
      task.executeTask();
      synchronized(this) {
        completed = true;
        if (hasWaiter)
          notifyAll();
      }
    }
    catch (Throwable ex) {
      Diagnostic.printStackTrace(ex);
    }
  }

  final synchronized void waitFor() {
    if (!completed) {
      hasWaiter = true;
      try {
        wait();
      }
      catch(Exception ex){
        Diagnostic.printStackTrace();
      }
    }
  }
}
