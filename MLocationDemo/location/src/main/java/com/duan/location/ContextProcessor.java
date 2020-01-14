package com.duan.location;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.lang.ref.WeakReference;

/** Context 处理器 */
public class ContextProcessor {

    private Context applicationContext;
    private WeakReference<Activity> weakActivity;  // Activity 弱引用
    private WeakReference<Fragment> weakFragment;  // Fragment 弱引用

    public ContextProcessor(Context context) {
        if (!(context instanceof Application)) {
            throw new IllegalArgumentException("ContextProcessor can only be initialized with Application!");
        }

        applicationContext = context;
        weakActivity = new WeakReference<>(null);
        weakFragment = new WeakReference<>(null);
    }

    /**
     * In order to use in Activity or Service
     */
    public ContextProcessor setActivity(Activity activity) {
        weakActivity = new WeakReference<>(activity);
        weakFragment = new WeakReference<>(null);
        return this;
    }

    /**
     * In order to use in Fragment
     */
    public ContextProcessor setFragment(Fragment fragment) {
        weakActivity = new WeakReference<>(null);
        weakFragment = new WeakReference<>(fragment);
        return this;
    }

    @Nullable
    public Fragment getFragment() {
        return weakFragment.get();
    }

    @Nullable
    public Activity getActivity() {
        if (weakActivity.get() != null) return weakActivity.get();
        if (weakFragment.get() != null && weakFragment.get().getActivity() != null) return weakFragment.get().getActivity();
        return null;
    }

    public Context getContext() {
        return applicationContext;
    }

}
