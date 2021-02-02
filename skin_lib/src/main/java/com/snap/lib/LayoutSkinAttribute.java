package com.snap.lib;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.view.ViewCompat;

import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

/**
 *Created by  : blank
 *Create one  : 2021/2/2 at  22:41
 *Name        :
 *Description : 每一个layout对应的所有的view
 */
public class LayoutSkinAttribute {
    private final static List<String> ATTRIBUTES = new ArrayList<>();

    static {
        ATTRIBUTES.add(SkinAttrConst.ATTRIBUTE_BACKGROUND);
        ATTRIBUTES.add(SkinAttrConst.ATTRIBUTE_SRC);
        ATTRIBUTES.add(SkinAttrConst.ATTRIBUTE_TEXTCOLOR);
        ATTRIBUTES.add(SkinAttrConst.ATTRIBUTE_TEXTCOLORHINT);
        ATTRIBUTES.add(SkinAttrConst.ATTRIBUTE_DRAWABLELEFT);
        ATTRIBUTES.add(SkinAttrConst.ATTRIBUTE_DRAWABLETOP);
        ATTRIBUTES.add(SkinAttrConst.ATTRIBUTE_DRAWABLERIGHT);
        ATTRIBUTES.add(SkinAttrConst.ATTRIBUTE_DRAWABLEBOTTOM);

    }

    //记录换肤需要操作的View与属性信息
    private List<SkinView> mSkinViews = new ArrayList<>();

    /**
     * 获取View中需要更改的属性
     *
     * @param view
     * @param attrs
     */
    public void hook(View view, AttributeSet attrs) {
        List<SkinPair> skinParis = new ArrayList<>();
        final int attributeCount = attrs.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            //获得属性名  textColor/background
            String attributeName = attrs.getAttributeName(i);
            if (ATTRIBUTES.contains(attributeName)) {
                //  eg：background： #
                //  eg：background：?722727272 ， 这个是资源id
                //  eg：background：@722727272
                String attributeValue = attrs.getAttributeValue(i);
                // 比如color 以#开头表示写死的颜色 不可用于换肤
                if (attributeValue.startsWith("#")) {
                    continue;
                }
                int resId;
                // 以 ？开头的表示使用 属性
                if (attributeValue.startsWith("?")) {
                    int attrId = Integer.parseInt(attributeValue.substring(1));
                    resId = SkinThemeUtils.getResId(view.getContext(), new int[]{attrId})[0];
                } else {
                    // 正常以 @ 开头
                    resId = Integer.parseInt(attributeValue.substring(1));
                }

                SkinPair skinPair = new SkinPair(attributeName, resId);
                skinParis.add(skinPair);
            }
        }

        if (!skinParis.isEmpty() || view instanceof ISkinViewSupport) {
            SkinView skinView = new SkinView(view, skinParis);
            skinView.applySkin();
            mSkinViews.add(skinView);
        }
    }


    /**
     * 应用皮肤
     */
    public void applySkin() {
        for (SkinView skinView : mSkinViews) {
            skinView.applySkin();
        }
    }


    /**
     * 每一个view的属性-id集合；
     */
    final static class SkinView {
        View mView;
        //View的所有属性
        List<SkinPair> mSkinPairs;

        public SkinView(View view, List<SkinPair> skinPairs) {
            this.mView = view;
            this.mSkinPairs = skinPairs;
        }

        public void applySkin() {
            applySkinSupport();
            for (SkinPair p : mSkinPairs) {
                Drawable left = null, top = null, right = null, bottom = null;
                switch (p.attributeName) {
                    case SkinAttrConst.ATTRIBUTE_BACKGROUND:
                        Object background = SkinResources.getInstance().getBackground(p.resId);
                        if (background instanceof Integer) {
                            mView.setBackgroundColor((Integer) background);
                        } else {
                            ViewCompat.setBackground(mView, (Drawable) background);
                        }
                        break;
                    case SkinAttrConst.ATTRIBUTE_SRC:
                        background = SkinResources.getInstance().getBackground(p.resId);
                        if (background instanceof Integer) {
                            ((ImageView) mView).setImageDrawable(new ColorDrawable((Integer) background));
                        } else {
                            ((ImageView) mView).setImageDrawable((Drawable) background);
                        }
                        break;
                    case SkinAttrConst.ATTRIBUTE_TEXTCOLOR:
                        ((TextView) mView).setTextColor(SkinResources.getInstance().getColorStateList(p.resId));
                        break;
                    case SkinAttrConst.ATTRIBUTE_TEXTCOLORHINT:
                        ((TextView) mView).setHintTextColor(SkinResources.getInstance().getColorStateList(p.resId));
                        break;
                    case SkinAttrConst.ATTRIBUTE_DRAWABLELEFT:
                        left = SkinResources.getInstance().getDrawable(p.resId);
                        break;
                    case SkinAttrConst.ATTRIBUTE_DRAWABLETOP:
                        top = SkinResources.getInstance().getDrawable(p.resId);
                        break;
                    case SkinAttrConst.ATTRIBUTE_DRAWABLERIGHT:
                        right = SkinResources.getInstance().getDrawable(p.resId);
                        break;
                    case SkinAttrConst.ATTRIBUTE_DRAWABLEBOTTOM:
                        bottom = SkinResources.getInstance().getDrawable(p.resId);
                        break;
                }
                if (null != left || null != top || null != right || null != bottom) {
                    ((TextView) mView).setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
                }
            }
        }

        /**
         * 用于支持自定义view
         */
        private void applySkinSupport() {
            if (mView instanceof ISkinViewSupport) {
                ((ISkinViewSupport) mView).applySkin();
            }
        }
    }

    /**
     * 属性-id
     */
    final static class SkinPair {
        //属性名称
        String attributeName;
        //属性资源id
        int resId;

        public SkinPair(String attributeName, int resId) {
            this.attributeName = attributeName;
            this.resId = resId;
        }
    }


}
