package com.littleyellow.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LogcatFragmentT extends Fragment {

    public static LogcatFragmentT newInstance(String tag) {

        Bundle args = new Bundle();
        args.putString("tag",tag);
        LogcatFragmentT fragment = new LogcatFragmentT();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, container, false);
        String tag = getArguments().getString("tag");
        TextView textView = view.findViewById(R.id.tv);
        textView.setText(tag);
        return view;
    }

}
