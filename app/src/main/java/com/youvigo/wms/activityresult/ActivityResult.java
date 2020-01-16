/*
 * Copyright (c) 2020. komamj
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.youvigo.wms.activityresult;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;


/**
 * A helper class to provider a simplified startActivityForResult()
 * and an api to check interceptors.
 */
public class ActivityResult {
    private static final String TAG = ActivityResult.class.getSimpleName();

    private FragmentManager mFragmentManager;
    private List<Interceptor> mInterceptors = new ArrayList<>();
    private Lazy<ResultFragment> mResultFragment;
    private FragmentActivity mFragmentActivity;

    public ActivityResult(FragmentActivity activity) {
        mFragmentActivity = activity;
        mFragmentManager = activity.getSupportFragmentManager();
        mResultFragment = getLazySingleton();
        findInterceptors(activity);
    }

    public ActivityResult(Fragment fragment) {
        mFragmentActivity = fragment.getActivity();
        mFragmentManager = fragment.getChildFragmentManager();
        mResultFragment = getLazySingleton();
        findInterceptors(fragment);
    }

    /**
     * Convenient method to start activity for result.
     */
    public void startActivityForResult(Intent intent, OnResultCallback callback) {
        mResultFragment.get().startActivityForResult(intent, callback);
    }

    /**
     * Check if interceptors specified with annotation {@link InterceptWith} are valid or not.
     */
    public void intercept(final OnInterceptResult onInterceptResult) {
        boolean isNewInstance = mFragmentManager.findFragmentByTag(TAG) == null;

        OnResultCallback onResultCallback = new OnResultCallback() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                for (Interceptor interceptor : mInterceptors) {
                    if (requestCode == interceptor.getRequestCode()){
                        if (resultCode == Activity.RESULT_OK) {
                            verifyInterceptors(true, onInterceptResult, this);
                            break;
                        } else if (resultCode == Activity.RESULT_CANCELED) {
                            mFragmentActivity.finish();
                            break;
                        }
                    }
                }
            }
        };

        mResultFragment.get().setResultCallback(onResultCallback);

        // verify interceptors
        if (!mInterceptors.isEmpty()) {
            verifyInterceptors(isNewInstance, onInterceptResult, onResultCallback);
        }
    }

    private void findInterceptors(Object object) {
        mInterceptors.clear();
        InterceptWith annotation = object.getClass().getAnnotation(InterceptWith.class);
        if (annotation != null) {
            Class<? extends Interceptor>[] classes = annotation.value();
            for (Class<? extends Interceptor> clazz : classes) {
                try {
                    mInterceptors.add(clazz.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * new instance need to invoke process
     */
    private void verifyInterceptors(boolean isNewInstance, OnInterceptResult onInterceptResult, OnResultCallback onResultCallback) {
        if (mInterceptors.isEmpty()) {
            return;
        }

        for (int i = 0; i < mInterceptors.size(); i++) {
            Interceptor interceptor = mInterceptors.get(i);
            if (interceptor.isValid(mResultFragment.get().getContext())) {
                // invoke callback if all validations pass
                if (i == mInterceptors.size() - 1) {
                    onInterceptResult.invoke();
                    break;
                }
            } else if (isNewInstance){
                Intent intent = interceptor.getTargetIntent(mFragmentActivity);
                int requestCode = mResultFragment.get().startActivityForResult(intent, onResultCallback);
                interceptor.setRequestCode(requestCode);
                break;
            }
        }
    }

    @NonNull
    private Lazy<ResultFragment> getLazySingleton() {
        return new Lazy<ResultFragment>() {
            private ResultFragment permissionsFragment;

            @Override
            public synchronized ResultFragment get() {
                if (permissionsFragment == null) {
                    permissionsFragment = getResultFragment();
                    permissionsFragment.setLogging(true);
                }
                return permissionsFragment;
            }
        };
    }

    private ResultFragment getResultFragment() {
        ResultFragment permissionsFragment = (ResultFragment) mFragmentManager.findFragmentByTag(TAG);
        boolean isNewInstance = permissionsFragment == null;
        if (isNewInstance) {
            permissionsFragment = new ResultFragment();
            mFragmentManager
                    .beginTransaction()
                    .add(permissionsFragment,TAG)
                    .commitNowAllowingStateLoss();
        }
        return permissionsFragment;
    }

    @FunctionalInterface
    interface Lazy<V> {
        V get();
    }
}
