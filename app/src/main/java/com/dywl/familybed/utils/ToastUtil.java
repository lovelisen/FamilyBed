package com.dywl.familybed.utils;

import android.widget.Toast;

import com.dywl.familybed.MyApp;

public class ToastUtil  {

    private static Toast mToast;

    public static void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(MyApp.instance, text, Toast.LENGTH_SHORT);//Toast.LENGTH_LONG（2秒）
        }
        mToast.setText(text);
        mToast.show();
    }


    public static void showLongToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(MyApp.instance, text, Toast.LENGTH_LONG);//Toast.LENGTH_LONG（3.5秒）
        }
        mToast.setText(text);
        mToast.show();
    }

}
