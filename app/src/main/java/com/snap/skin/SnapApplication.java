package com.snap.skin;

import android.app.Application;

import com.snap.lib.SnapPreference;
import com.snap.lib.SkinManager;


/**
 * Created at: 2021/1/20 at 22:57
 * Created by: blank
 * Description:
 */
public class SnapApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SnapPreference.INSTANCE.init(this, "nimo");
        SkinManager.getInstance().init(this);
    }
}
