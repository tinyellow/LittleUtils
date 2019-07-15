package com.littleyellow.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
;
import com.littleyellow.utils.TabFragmentHelper;

;

public class TestMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        TabLayout tableLayout = findViewById(R.id.table_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);
        TabFragmentHelper.newBuilder()
                .fm(getSupportFragmentManager())
                .mMainViewPager(viewPager)
                .addTab("0",LazyFragmentT.class,LazyFragmentT.getBundle(0))
                .addTab("1",LazyFragmentT.class,LazyFragmentT.getBundle(1))
                .addTab("2",LazyFragmentT.class,LazyFragmentT.getBundle(2))
                .addTab("4",LazyFragmentT.class,LazyFragmentT.getBundle(3))
//                .pageLimit(1)
                .build()
                .setupViewPager(tableLayout);

    }
}
