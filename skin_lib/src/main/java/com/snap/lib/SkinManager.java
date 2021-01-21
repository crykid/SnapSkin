package com.snap.lib;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.Observable;

public class SkinManager extends Observable {
    private Context mContext;

    private SkinManager() {
    }

    private static class Holder {
        private final static SkinManager INSTANCE = new SkinManager();
    }

    public static SkinManager getInstance() {
        return Holder.INSTANCE;
    }

    private AppActivityLifeCycle mLifecycler;

    public void init(Application application) {
        mContext = application;
        SkinResources.getInstance().init(application);
        mLifecycler = new AppActivityLifeCycle(this);
        application.registerActivityLifecycleCallbacks(mLifecycler);
        loadSkin(SnapPreference.INSTANCE.getSkin());
    }

    public void loadSkin(String skin) {
        if (TextUtils.isEmpty(skin)) {
            SnapPreference.INSTANCE.setSkin("");
            SkinResources.getInstance().reset();
        } else {

            Resources appResources = mContext.getResources();
            try {
                //创建新的AssetManager，加载apk资源
                AssetManager assetManager = AssetManager.class.newInstance();
                Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
                addAssetPath.invoke(assetManager, skin);

                //根据当前的设备显示器信息与配置（横竖屏、语言等）创建Resources
                Resources skinResources = new Resources(assetManager, appResources.getDisplayMetrics(),
                        appResources.getConfiguration());
                PackageManager packageManager = mContext.getPackageManager();
                PackageInfo packageArchiveInfo =
                        packageManager.getPackageArchiveInfo(skin, PackageManager.GET_ACTIVITIES);
                String packageName = packageArchiveInfo.packageName;
                SkinResources.getInstance().applySkin(skinResources, packageName);

                SnapPreference.INSTANCE.setSkin(skin);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        updateSkin();
    }

    private void updateSkin() {
        setChanged();
        notifyObservers(null);
    }

}
