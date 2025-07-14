package com.droidlogic.setup.provider;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.IActivityManager;
import android.app.ActivityTaskManager.RootTaskInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ComponentName;

import android.view.View;
import android.widget.Button;
import android.content.Context;
import com.droidlogic.app.SystemControlManager;

/** Hook Post welcome. */
public class HookWelcomeActivity extends Activity {
    private static final String TAG = "HookWelcomeActivity";

    Button btn_business ;
    Button btn_consumer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hookbegin_layout_aml);

        btn_business = findViewById(R.id.button_business);
        btn_consumer = findViewById(R.id.button_consumer);
        btn_consumer.setOnClickListener(mTvModeListener);
        btn_business.setOnClickListener(mTvModeListener);
        btn_consumer.setSelected(true);

        btn_business.setVisibility(View.VISIBLE);
        btn_consumer.setVisibility(View.VISIBLE);
    }

    public static SystemControlManager getSystemControlManager() {
         return SystemControlManager.getInstance();
    }

    private View.OnClickListener mTvModeListener = new View.OnClickListener() {
          public void onClick(View view) {
              switch (view.getId()) {
              case R.id.button_business:
                  Log.d(TAG, "onClick button_business");
                  getSystemControlManager().setProperty("persist.vendor.sys.tvmode", "business");
                  Log.d(TAG, "persist.vendor.tvmode is :" + getSystemControlManager().getPropertyString("persist.vendor.sys.tvmode", "consumer"));
                  break;
              case R.id.button_consumer:
                  Log.d(TAG, "onClick button_consumer");
                  getSystemControlManager().setProperty("persist.vendor.sys.tvmode", "consumer");
                  Log.d(TAG, "persist.vendor.tvmode is :" + getSystemControlManager().getPropertyString("persist.vendor.sys.tvmode", "consumer"));
                  break;
             }
             setResult(RESULT_OK);
             finish();
         }
     };


}
