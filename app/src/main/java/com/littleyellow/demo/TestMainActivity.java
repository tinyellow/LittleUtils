package com.littleyellow.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.littleyellow.utils.fragment.TabFragmentHelper;

public class TestMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        TabLayout tableLayout = findViewById(R.id.table_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);
        final TabFragmentHelper tabFragmentHelper = TabFragmentHelper.newBuilder()
                .fm(getSupportFragmentManager())
                .mMainViewPager(viewPager)
                .addTab("0",LazyFragmentT.class,LazyFragmentT.getBundle(0))
                .addTab("1",LazyFragmentT.class,LazyFragmentT.getBundle(1))
                .addTab("2",LazyFragmentT.class,LazyFragmentT.getBundle(2))
                .addTab("4",LazyFragmentT.class,LazyFragmentT.getBundle(3))
                .pageLimit(1)
                .build()
                .setupViewPager(tableLayout);
        TextView updateTv = (TextView) findViewById(R.id.tv_update);
        updateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabFragmentHelper.getAdapter().notifyDataSetChanged();
            }
        });

        String master = "M"+1+2;

        String test = "A"+1;
    }
}
