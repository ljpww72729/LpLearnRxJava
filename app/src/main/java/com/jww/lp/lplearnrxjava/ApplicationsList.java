package com.jww.lp.lplearnrxjava;

import java.util.List;

 public class ApplicationsList {

  private static ApplicationsList ourInstance = new ApplicationsList();

   public List<AppInfo> getList() {
     return list;
   }

   public void setList(List<AppInfo> list) {
     this.list = list;
   }

   private List<AppInfo> list;

  private ApplicationsList() {
  }

  public static ApplicationsList getInstance() {
    return ourInstance;
  }
}
