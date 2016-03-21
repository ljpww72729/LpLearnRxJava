package com.jww.lp.lplearnrxjava;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {


    private ApplicationAdapter mAdapter;
    private File mFilesDir;
    @Bind(R.id.fragment_first_example_list)  RecyclerView mRecyclerView;
    @Bind(R.id.fragment_first_example_swipe_container) SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new ApplicationAdapter(new ArrayList<>(), R.layout.applications_list_item);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mSwipeRefreshLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,
                        getResources().getDisplayMetrics()));

        // Progress
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setRefreshing(true);
        mRecyclerView.setVisibility(View.GONE);

        getFileDir().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(file -> {
                    mFilesDir = file;
                    refreshTheList();
                });
    }

    private Observable<File> getFileDir() {
        return Observable.create(subscriber -> {
            subscriber.onNext(App.instance.getFilesDir());
            subscriber.onCompleted();
        });
    }

    private void refreshTheList() {
        getApps().toSortedList().subscribe(new Observer<List<AppInfo>>() {
            @Override public void onCompleted() {
                Toast.makeText(MainActivity.this, "Here is the list!", Toast.LENGTH_LONG).show();
            }

            @Override public void onError(Throwable e) {
                Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override public void onNext(List<AppInfo> appInfos) {
                mRecyclerView.setVisibility(View.VISIBLE);
                mAdapter.addApplications(appInfos);
                mSwipeRefreshLayout.setRefreshing(false);
                storeList(appInfos);
            }
        });
    }

    private void storeList(List<AppInfo> appInfos) {
        ApplicationsList.getInstance().setList(appInfos);

        Schedulers.io().createWorker().schedule(() -> {
            SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
            Type appInfoType = new TypeToken<List<AppInfo>>() {
            }.getType();
            sharedPref.edit().putString("APPS", new Gson().toJson(appInfos, appInfoType)).apply();
        });
    }

    private Observable<AppInfo> getApps() {
        return Observable.create(subscriber -> {
            List<AppInfoRich> apps = new ArrayList<>();

            final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            List<ResolveInfo> infos =
                    MainActivity.this.getPackageManager().queryIntentActivities(mainIntent, 0);
            for (ResolveInfo info : infos) {
                apps.add(new AppInfoRich(MainActivity.this, info));
            }

            for (AppInfoRich appInfo : apps) {
                Bitmap icon = Utils.drawableToBitmap(appInfo.getIcon());
                String name = appInfo.getName();
                String iconPath = mFilesDir + "/" + name;
                Utils.storeBitmap(App.instance, icon, name);

                if (subscriber.isUnsubscribed()) {
                    return;
                }
                subscriber.onNext(new AppInfo(name, iconPath, appInfo.getLastUpdateTime()));
            }
            if (!subscriber.isUnsubscribed()) {
                subscriber.onCompleted();
            }
        });
    }

    @Override public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
