package com.littleyellow.utils.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class LazyFragment extends Fragment {

    protected boolean isVisible;
    // 标志位，标志已经初始化完成。
    private boolean isPrepared;
    // 标志已经懒加载过，下次不用再加载。
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
