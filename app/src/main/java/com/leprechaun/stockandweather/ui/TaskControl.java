package com.leprechaun.stockandweather.ui;

/**
 * Created by oborghi on 26/03/16 - 13:39.
 */

public abstract class TaskControl {

     private TaskStatus status = TaskStatus.Pending;

     protected abstract void executeTask();

     public TaskStatus getStatus() {
          return status;
     }

     public void setStatus(TaskStatus status) {
          this.status = status;
     }
}
