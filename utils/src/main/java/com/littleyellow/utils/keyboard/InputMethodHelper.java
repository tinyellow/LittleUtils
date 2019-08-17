package com.littleyellow.utils.keyboard;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * 软键盘帮助类
 * Created by HT on 2018/2/12.
 */
@TargetApi(14)
public class InputMethodHelper {
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private OnInputMethodListener onInputMethodListener;
    private Rect windowContentRect;
    private Rect keyboardRect;
    private InputMethodHelper(OnInputMethodListener listener) {
        this.onInputMethodListener = listener;
    }
    private void onAttach(Activity activity) {
        final View decorView = activity.getWindow().getDecorView();
        windowContentRect = getDisplayVisibleFrameHeight(decorView);
        onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                Rect displayVisibleFrame = getDisplayVisibleFrameHeight(decorView);
                if (keyboardRect == null) {
                    keyboardRect = new Rect(displayVisibleFrame);
                }
                keyboardRect.top = displayVisibleFrame.bottom;
                keyboardRect.bottom = windowContentRect.bottom;
                int height = keyboardRect.height();
                int barHeight = getStatusBarHeight(decorView.getContext());
                if (onInputMethodListener != null) {
                    onInputMethodListener.onInputMethodStatusChanged(keyboardRect, keyboardRect.height() > 0);
                }
            }
        };
        decorView
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(onGlobalLayoutListener);
    }
    @RequiresApi(16)
    private void onDetach(Activity activity) {
        if (onInputMethodListener != null) {
            activity.getWindow()
                    .getDecorView()
                    .getViewTreeObserver()
                    .removeOnGlobalLayoutListener(onGlobalLayoutListener);
        }
    }
    public static void assistActivity(final Activity host, OnInputMethodListener onInputMethodListener) {
        if (host == null) {
            return;
        }
        final InputMethodHelper methodHelper = new InputMethodHelper(onInputMethodListener);
        host.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }
            @Override
            public void onActivityStarted(Activity activity) {
                if (host == activity && methodHelper.onGlobalLayoutListener == null) {
                    methodHelper.onAttach(activity);
                }
            }
            @Override
            public void onActivityResumed(Activity activity) {
                if (host == activity && methodHelper.onGlobalLayoutListener == null) {
                    throw new IllegalStateException("assistActivity() must be called before onStart() called!");
                }
            }
            @Override
            public void onActivityPaused(Activity activity) {
            }
            @Override
            public void onActivityStopped(Activity activity) {
            }
            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }
            @Override
            public void onActivityDestroyed(Activity activity) {
                if (activity == host) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        methodHelper.onDetach(activity);
                    }
                    activity.getApplication().unregisterActivityLifecycleCallbacks(this);
                }
            }
        });
    }
    public static void assistFragment(final Fragment fragment, OnInputMethodListener onInputMethodListener) {
        if (fragment == null) {
            return;
        }
        final InputMethodHelper methodHelper = new InputMethodHelper(onInputMethodListener);
        fragment.getFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
                if (f == fragment) {
                    methodHelper.onAttach(f.getActivity());
                }
            }
            @Override
            public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                if (f == fragment) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        methodHelper.onDetach(f.getActivity());
                    }
                    f.getFragmentManager().unregisterFragmentLifecycleCallbacks(this);
                }
            }
        }, false);
    }
    public static Rect getDisplayVisibleFrameHeight(View view) {
        Rect r = new Rect();
        view.getWindowVisibleDisplayFrame(r);
        return r;
    }


    public interface OnInputMethodListener {
        /**
         * 软键盘弹出/收起监听
         * @param keyboardRect 键盘弹出区域，宽，高
         *                     left = keyboardRect.left
         *                     top = keyboardRect.top
         *                     right = keyboardRect.right
         *                     bottom = keyboardRect.bottom
         *                     width = keyboardRect.width()
         *                     height = keyboardRect.height()
         * @param show true 显示，false 隐藏
         */
        void onInputMethodStatusChanged(Rect keyboardRect, boolean show);
    }

    /**
     * 获取状态栏高度
     * @param context context
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }
}