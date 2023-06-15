package com.dywl.familybed;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dywl.familybed.utils.ActivityCollector;

public class BaseActivity extends AppCompatActivity {

    //region 创建activity时添加activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }
    //endregion

    //region 销毁activity时移除activity
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
    //endregion

}