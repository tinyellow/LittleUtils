package com.littleyellow.demo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.littleyellow.utils.common.ResultHelper;
import com.littleyellow.utils.fragment.TabFragmentHelper;
import com.littleyellow.utils.statebar.StatusBarColor;
import com.squareup.leakcanary.LeakCanary;

import static com.littleyellow.demo.H5web2Activity.BUNDLE_PARAM_URL;
import static com.littleyellow.utils.statebar.StatusBarUtil.DARK_WHITE_AOTO;

public class TestMainActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, TestMainActivity.class);
        context.startActivity(starter);
    }

    TextView updateTv;
    TextView tv_common_start;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LeakCanary.install(getApplication());
        setContentView(R.layout.activity_test);
        StatusBarColor.setColor(this, Color.GREEN,DARK_WHITE_AOTO, Color.RED,Color.WHITE);
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
        updateTv = (TextView) findViewById(R.id.tv_update);
        updateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                H5web2Activity.start(v.getContext(),"http://sitxqy.bndxqc.com/a/login?ht=CCCHT201908070111");
//                tabFragmentHelper.getAdapter().notifyDataSetChanged();
                updateTv.setText(null);
                Intent starter = new Intent(TestMainActivity.this, H5web2Activity.class);
                starter.putExtra(BUNDLE_PARAM_URL,"");
                ResultHelper.get().start(TestMainActivity.this, starter, 22, new ResultHelper.ResultListener() {
                    @Override
                    public void onResult(int requestCode, int resultCode, Intent data) {
                        updateTv.setText(requestCode+":"+resultCode);
                    }
                });
            }
        });

        tv_common_start = (TextView) findViewById(R.id.tv_common_start);
        tv_common_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_common_start.setText(null);
                H5web2Activity.start(TestMainActivity.this,"",11);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this,requestCode+":"+resultCode,Toast.LENGTH_LONG).show();
        tv_common_start.setText(requestCode+":"+resultCode);
    }
}
