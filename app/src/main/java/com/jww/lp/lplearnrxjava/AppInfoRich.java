package com.jww.lp.lplearnrxjava;

/**
 * Created by lipeng on 2016 3-21.
 */
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import java.util.Locale;

public class AppInfoRich implements Comparable<Object> {

    private String name = null;

    private Context context;

    private ResolveInfo resolveInfo;

    private ComponentName componentName = null;

    private PackageInfo pi = null;

    private Drawable icon = null;

    public AppInfoRich(Context ctx, ResolveInfo ri) {
        context = ctx;
        resolveInfo = ri;

        componentName =
                new ComponentName(ri.activityInfo.applicationInfo.packageName, ri.activityInfo.name);

        try {
            pi = ctx.getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
        }
    }

    public String getName() {
        if (name != null) {
            return name;
        } else {
            try {
                return getNameFromResolveInfo(resolveInfo);
            } catch (NameNotFoundException e) {
                return getPackageName();
            }
        }
    }

    public String getActivityName() {
        return resolveInfo.activityInfo.name;
    }

    public String getPackageName() {
        return resolveInfo.activityInfo.packageName;
    }

    public ComponentName getComponentName() {
        return componentName;
    }

    public String getComponentInfo() {
        if (getComponentName() != null) {
            return getComponentName().toString();
        } else {
            return "";
        }
    }

    public ResolveInfo getResolveInfo() {
        return resolveInfo;
    }

    public PackageInfo getPackageInfo() {
        return pi;
    }

    public String getVersionName() {
        PackageInfo pi = getPackageInfo();
        if (pi != null) {
            return pi.versionName;
        } else {
            return "";
        }
    }

    public int getVersionCode() {
        PackageInfo pi = getPackageInfo();
        if (pi != null) {
            return pi.versionCode;
        } else {
            return 0;
        }
    }

    public Drawable getIcon() {
        if (icon == null) {
            icon = getResolveInfo().loadIcon(context.getPackageManager());
            /*
            Drawable dr = getResolveInfo().loadIcon(context.getPackageManager());
            Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
            icon = new BitmapDrawable(context.getResources(), AppHelper.getResizedBitmap(bitmap, 144, 144));
            */
        }
        return icon;
    }

    @SuppressLint("NewApi") public long getFirstInstallTime() {
        PackageInfo pi = getPackageInfo();
        if (pi != null
                && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            return pi.firstInstallTime;
        } else {
            return 0;
        }
    }

    @SuppressLint("NewApi") public long getLastUpdateTime() {
        PackageInfo pi = getPackageInfo();
        if (pi != null
                && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            return pi.lastUpdateTime;
        } else {
            return 0;
        }
    }

    @Override public int compareTo(Object o) {
        AppInfoRich f = (AppInfoRich) o;
        return getName().compareTo(f.getName());
    }

    @Override public String toString() {
        return getName();
    }

    /**
     * Helper method to get an applications name!
     */

    public String getNameFromResolveInfo(ResolveInfo ri) throws NameNotFoundException {
        String name = ri.resolvePackageName;
        if (ri.activityInfo != null) {
            Resources res =
                    context.getPackageManager().getResourcesForApplication(ri.activityInfo.applicationInfo);
            Resources engRes = getEnglishRessources(res);

            if (ri.activityInfo.labelRes != 0) {
                name = engRes.getString(ri.activityInfo.labelRes);

                if (name == null || name.equals("")) {
                    name = res.getString(ri.activityInfo.labelRes);
                }
            } else {
                name = ri.activityInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
            }
        }
        return name;
    }

    public Resources getEnglishRessources(Resources standardResources) {
        AssetManager assets = standardResources.getAssets();
        DisplayMetrics metrics = standardResources.getDisplayMetrics();
        Configuration config = new Configuration(standardResources.getConfiguration());
        config.locale = Locale.US;
        return new Resources(assets, metrics, config);
    }
}
