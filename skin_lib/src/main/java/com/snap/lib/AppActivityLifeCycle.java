package com.snap.lib;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.support.v4.view.LayoutInflaterCompat;

import java.lang.reflect.Field;
import java.util.Observable;

/**
 * Created at: 2021/1/20 at 22:25
 * Created by: blank
 * Description:
 */
public class AppActivityLifeCycle implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "AppActivityLifeCycle";
    private Observable mObservable;
    private ArrayMap<Activity, SkinLayoutInflaterFactory> mLayoutInflacterFactories = new ArrayMap<>();

    public AppActivityLifeCycle(Observable observable) {
        this.mObservable = observable;
    }

    @Override
    public void onActivityCreated(Activity activity,Bundle savedInstanceState) {

        //更新状态栏
        SkinThemeUtils.updateStatusBarColor(activity);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        SkinLayoutInflaterFactory skinLayoutInflaterFactory = new SkinLayoutInflaterFactory(activity);

        //android 9.0 不允许反射mFactorySet
        Class<LayoutInflater> layoutInflaterClass = LayoutInflater.class;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
//                Field mFactoryField = layoutInflaterClass.getDeclaredField("mFactory");
//                mFactoryField.setAccessible(true);

                Field mFactory2Field = layoutInflaterClass.getDeclaredField("mFactory2");
                mFactory2Field.setAccessible(true);
                mFactory2Field.set(layoutInflater, skinLayoutInflaterFactory);

            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            try {

                Field field = layoutInflaterClass.getDeclaredField("mFactorySet");
                field.setAccessible(true);
                field.set(layoutInflater, false);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                Log.d(TAG, "onActivityCreated reflex mFactorySet error :" + e);
            }
            LayoutInflaterCompat.setFactory2(layoutInflater, skinLayoutInflaterFactory);
        }
        mLayoutInflacterFactories.put(activity, skinLayoutInflaterFactory);
        mObservable.addObserver(skinLayoutInflaterFactory);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        SkinLayoutInflaterFactory observer = mLayoutInflacterFactories.remove(activity);
        SkinManager.getInstance().deleteObserver(observer);
    }
}
