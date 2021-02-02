package com.snap.lib;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by  : blank
 * Create one  : 2021/1/22 at  1:27
 * Name        :
 * Description : 每一个Activty对应一个inflater，每一个activity对应一个SkinAttribute，
 * 每个SkinAttribute持有当前layout的所有view；
 * 每生成一个view，就hook它的属性。
 */
class SkinLayoutInflaterFactory implements LayoutInflater.Factory2, Observer {
    private static final String[] CLASSP_REFIXES = {
            "android.widget.",
            "android.webkit.",
            "android.app.",
            "android.view."
    };

    //记录对应VIEW的构造函数
    private static final Class<?>[] CONSTRUCTOR_SIGNATURE = new Class[]{
            Context.class, AttributeSet.class};

    private static final HashMap<String, Constructor<? extends View>> CONSTRUCTOR_MAP =
            new HashMap<String, Constructor<? extends View>>();

    private LayoutSkinAttribute mLayoutSkinAttribute;
    private Activity mActivity;

    public SkinLayoutInflaterFactory(Activity activity) {
        this.mActivity = activity;
        mLayoutSkinAttribute = new LayoutSkinAttribute();
    }

    /**
     * 创建每一个具体的view
     *
     * @param parent
     * @param name
     * @param context
     * @param attrs
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {

        View view = createSDKView(name, context, attrs);
        if (view == null) {
            view = createView(name, context, attrs);
        }
        //hoot每一个view的属性
        if (view != null) {
            mLayoutSkinAttribute.hook(view, attrs);
        }
        return view;
    }

    private View createSDKView(String name, Context context, AttributeSet attrs) {
        //自定义view或support库中的view
        if (name.contains(".")) {
            return null;
        }
        for (String classpRefix : CLASSP_REFIXES) {
            View view = createView(classpRefix + name, context, attrs);
            if (view != null) {
                return view;
            }
        }

        return null;
    }

    private View createView(String name, Context context, AttributeSet attrs) {
        Constructor<? extends View> constructor = findConstructor(context, name);
        try {
            return constructor.newInstance(context, attrs);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 找到View的构造函数
     *
     * @param context
     * @param name
     * @return
     */
    private Constructor<? extends View> findConstructor(Context context, String name) {
        Constructor<? extends View> constructor = CONSTRUCTOR_MAP.get(name);
        if (constructor == null) {
            try {
                Class<? extends View> clazz = context.getClassLoader().loadClass(name).asSubclass(View.class);
                constructor = clazz.getConstructor(CONSTRUCTOR_SIGNATURE);
                if (constructor != null) {
                    CONSTRUCTOR_MAP.put(name, constructor);
                }
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return constructor;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
        SkinThemeUtils.updateStatusBarColor(mActivity);
        mLayoutSkinAttribute.applySkin();
    }
}
