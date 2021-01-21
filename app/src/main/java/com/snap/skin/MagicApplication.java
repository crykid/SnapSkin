package com.snap.skin;

import android.app.Application;

import com.snap.lib.MagicPreference;
import com.snap.lib.SkinManager;


/**
 * Created at: 2021/1/20 at 22:57
 * Created by: blank
 * Description:
 */
public class MagicApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MagicPreference.INSTANCE.init(this, "nimo");
        SkinManager.getInstance().init(this);
    }
}
