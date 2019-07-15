package com.littleyellow.utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public abstract class LazyFragment extends Fragment {

    protected boolean isVisible;
    // 标志位，标志已经初始化完成。
    private boolean isPrepared;

    private boolean isLazyLoad;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isPrepared = true;
        visible();
    }

    /**
     * 在这里实现Fragment数据的缓加载.
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()) {
            isVisible = true;
            visible();
        } else {
            isVisible = false;
            if(isPrepared) {
                onVisibleChange(isVisible);
            }
        }
    }

    private void visible(){
        if(!isPrepared || !isVisible) {
            return;
        }
        if(!isLazyLoad){
            isLazyLoad = true;
            lazyLoad();
        }else{
            onVisibleChange(true);
        }
    }

    protected void onVisibleChange(boolean isVisible){}

    protected abstract void lazyLoad();
}
