package com.jww.lp.lplearnrxjava;

/**
 * Created by lipeng on 2016 3-21.
 */
public class AppInfo implements Comparable<Object> {

   private long lastUpdateTime;
   private String name;
   private String icon;

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public AppInfo(String name, String icon, long lastUpdateTime) {
        this.name = name;
        this.icon = icon;
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppInfo appInfo = (AppInfo) o;

        if (lastUpdateTime != appInfo.lastUpdateTime) return false;
        if (name != null ? !name.equals(appInfo.name) : appInfo.name != null) return false;
        return icon != null ? icon.equals(appInfo.icon) : appInfo.icon == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (lastUpdateTime ^ (lastUpdateTime >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (icon != null ? icon.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Object another) {
        AppInfo ai = (AppInfo) another;
        return getName().compareTo(ai.getName());
    }
}
