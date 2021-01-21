package com.snap.skin;


import android.Manifest;
import android.os.Bundle;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;

import com.snap.lib.SkinManager;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TestMethodUtils.find(this, R.id.cl_content);
    }

    @ClickTest("changeSkin")
    public void a() {
        SkinManager.getInstance().loadSkin("/data/data/com.snap.skin/skin_dark-debug.apk");
    }

    @ClickTest("restore")
    public void r() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        SkinManager.getInstance().loadSkin(null);
    }
}
