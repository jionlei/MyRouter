package com.example.router_api;

import android.app.Activity;
import android.util.Log;
import android.util.LruCache;

import com.example.annotation_processor.utils.ProcessorUtils;

import java.util.Collections;

public class ParameterManger {
    private static final String TAG = "ParameterManger";
    private static volatile ParameterManger mParameterManger = null;
    private LruCache<String, IMyRouterParameter> parameterLruCache;

    private ParameterManger() {
        parameterLruCache = new LruCache<>(128);
    }

    public static ParameterManger getInstance() {
        if (mParameterManger == null) {
            synchronized (ParameterManger.class) {
                if (mParameterManger == null) {
                    mParameterManger = new ParameterManger();
                }
            }
        }
        return mParameterManger;
    }


    public void loadParameter(Activity activity) {
        // 找到对应的类  类名是固定的 都有统一的方法
        // 注解生成的类名 Com_xxx_activity$$MyRouterParameter
        // 本质上也是反射获取的
        // 1. 获取类名
        String handleClassName = getPackageName(activity) + "." + ProcessorUtils.handleClassName(activity.getClass().getCanonicalName());
//        if(handleClassName == null) {
//            Log.e(TAG, "loadParameter: 跳转失败");
//            return;
//        }
        IMyRouterParameter parameterRouter = parameterLruCache.get(handleClassName);
        if (parameterRouter != null) {
            parameterRouter.setParameter(activity);
            return;
        }
        try {
            Class<?> aClass = Class.forName(handleClassName);
            IMyRouterParameter parameterClass = (IMyRouterParameter) aClass.newInstance();
            parameterClass.setParameter(activity);
            parameterLruCache.put(handleClassName, parameterClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getPackageName(Activity activity) {
        Class<?> clazz = activity.getClass();
        int lastDot = activity.getClass().getName().lastIndexOf('.');
        return clazz.getName().substring(0, lastDot);
    }

}
