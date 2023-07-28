package com.example.router_api;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class BundleManager {

    private Bundle mBundle;

    public BundleManager() {
        this.mBundle = new Bundle();
    }

    public void navigation(Context context) {
        RouterManger.getInstance().navigation(context, this);
    }

    public BundleManager withBundle(Bundle bundle) {
        mBundle = bundle;
        return this;
    }

    public BundleManager withBoolean(String key, boolean value) {
        mBundle.putBoolean(key, value);
        return this;
    }

    public BundleManager withString(String key, String value) {
        mBundle.putString(key, value);
        return this;
    }

    public BundleManager withInt(String key, int value) {
        mBundle.putInt(key, value);
        return this;
    }

    public Bundle getBundle() {
          return mBundle;
    }
}
