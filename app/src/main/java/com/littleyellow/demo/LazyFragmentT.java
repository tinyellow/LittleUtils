package com.littleyellow.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.littleyellow.utils.fragment.LazyFragment;

public class LazyFragmentT extends LazyFragment {

    TextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, container, false);
        textView = view.findViewById(R.id.tv);
        return view;
    }

    public static Bundle getBundle(int position) {
        Bundle args = new Bundle();
        args.putInt("position",position);
        return args;
    }

    @Override
    protected void lazyLoad() {
        int position = getArguments().getInt("position",-1);
        Log.d("Fragment","lazyLoad:"+position);
    }

    @Override
    protected void onVisibleChange(boolean isVisible) {
        int position = getArguments().getInt("position",-1);
        if(isVisible) {
            Log.d("Fragment", "onVisible:" + position);
        }else {
            Log.d("Fragment","onInvisible:"+position);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        textView.setText(requestCode+":"+resultCode);
    }
}
