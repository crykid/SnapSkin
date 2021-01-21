package com.snap.lib;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorRes;
import android.text.TextUtils;


public class SkinResources {

    private String mSkinPkgName;
    private boolean isDefaultSkin = true;

    private Resources mAppResources;
    private Resources mSkinResources;
    private boolean mIsInitialized = false;
    private final static SkinResources INSTANCE = new SkinResources();

    private SkinResources() {
    }


    public void init(Context context) {
        mAppResources = context.getResources();
        mIsInitialized = true;
    }

    public void unInit() {
        mSkinResources = null;
        mAppResources = null;

    }

    public static SkinResources getInstance() {
        return INSTANCE;
    }


    public void applySkin(Resources resources, String packageName) {
        mSkinResources = resources;
        mSkinPkgName = packageName;
        isDefaultSkin = TextUtils.isEmpty(packageName) || resources == null;
    }

    /**
     * 根据app的资源ID找到资源包中对应的资源；
     *
     * @param resId 资源  ID
     * @return
     */
    public int getIdentifier(int resId) {
        if (isDefaultSkin) {
            return resId;
        }
        final String resourceEntryName = mAppResources.getResourceEntryName(resId);
        final String resourceTypeName = mAppResources.getResourceTypeName(resId);
        return mSkinResources.getIdentifier(resourceEntryName, resourceTypeName, mSkinPkgName);
    }

    /**
     * 根据主app的color id 找到资源包中对应的颜色值
     *
     * @param resId
     * @return
     */
    public int getColor(@ColorRes int resId) {
        if (isDefaultSkin) {
            return mAppResources.getColor(resId);
        }
        int identifier = getIdentifier(resId);
        if (identifier == 0) {
            return mAppResources.getColor(resId);
        }
        return mSkinResources.getColor(identifier);
    }

    public ColorStateList getColorStateList(int resId) {
        if (isDefaultSkin) {
            return mAppResources.getColorStateList(resId);
        }
        int skinId = getIdentifier(resId);
        if (skinId == 0) {
            return mAppResources.getColorStateList(resId);
        }
        return mSkinResources.getColorStateList(skinId);
    }

    public Drawable getDrawable(int resId) {
        if (isDefaultSkin) {
            return mAppResources.getDrawable(resId);
        }
        //通过 app的resource 获取id 对应的 资源名 与 资源类型
        //找到 皮肤包 匹配 的 资源名资源类型 的 皮肤包的 资源 ID
        int skinId = getIdentifier(resId);
        if (skinId == 0) {
            return mAppResources.getDrawable(resId);
        }
        return mSkinResources.getDrawable(skinId);
    }

    /**
     * 可能是Color 也可能是drawable
     *
     * @return
     */
    public Object getBackground(int resId) {
        String resourceTypeName = mAppResources.getResourceTypeName(resId);

        if ("color".equals(resourceTypeName)) {
            return getColor(resId);
        } else {
            // drawable
            return getDrawable(resId);
        }
    }


    public void reset() {
        mSkinResources = null;
        mSkinPkgName = "";
        isDefaultSkin = true;
    }
}
