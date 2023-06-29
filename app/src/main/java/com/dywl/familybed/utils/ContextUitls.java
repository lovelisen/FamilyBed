package com.dywl.familybed.utils;

import android.content.Context;

public class ContextUitls {

    private static Context context;

    public ContextUitls(){
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     * @param context 上下文
     */
    public static void init(Context context){
        ContextUitls.context = context.getApplicationContext();
    }

    public static Context getContext(){
        if (context != null) return context;
        throw new NullPointerException("u should init first");
    }

}
