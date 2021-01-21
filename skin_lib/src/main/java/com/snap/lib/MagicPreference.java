package com.snap.lib;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created at: 2021/1/20 at 22:46
 * Created by: blank
 * Description:
 */
public enum MagicPreference {
    INSTANCE;
    private final static String SHARED_PREFERENCE_NAME = "com.magic.skin";
    private static final String KEY_SKIN_PATH = "key_skin_path";
    private SharedPreferences mPreferences;

    public void init(Context context, String user) {
        String name = SHARED_PREFERENCE_NAME;
        if (!TextUtils.isEmpty(user)) {
            name = name + "." + user;
        }
        mPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public void setSkin(String path) {
        put(KEY_SKIN_PATH, path);
    }

    public String getSkin() {
        return mPreferences.getString(KEY_SKIN_PATH, null);
    }

    public void put(String key, String value) {
        mPreferences.edit().putString(key, value).apply();
    }
}
