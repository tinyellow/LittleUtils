package com.littleyellow.utils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.littleyellow.utils.adapter.Adaptor;
import com.littleyellow.utils.adapter.HolderClass;
import com.littleyellow.utils.adapter.ViewBinder;
import com.littleyellow.utils.loadmore.MoreLoader;
import com.littleyellow.utils.player.IPlayer;
import com.littleyellow.utils.player.PLPlayer;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;
import static com.pili.pldroid.player.PLOnInfoListener.MEDIA_INFO_AUDIO_RENDERING_START;


public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView tv;
    LinearLayoutManager layoutManager;

    ArrayList data;

    Adaptor adaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RelativeLayout main = (RelativeLayout) findViewById(R.id.activity_main);
        Log.e("App",getPackageName()+"+++++++++++");
//        main.post(new Runnable() {
//            @Override
//            public void run() {
//                StringBuilder sb = new StringBuilder();
//                int size = main.getChildCount();
//                for(int i=0;i<size;i++){
//                    View view = main.getChildAt(i);
//                    if(view instanceof Button){
//                        sb.append(((Button) view).getText()).append("l=").append(view.getLeft()).append("r=").append(view.getRight())
//                                .append("t").append(view.getTop()).append("b").append(view.getBottom()).append("\n");
//                    }
//                    tv.setText(sb.toString());
//                }
//            }
//        });


        tv = (TextView) findViewById(R.id.tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View contentView = LayoutInflater.from(view.getContext()).inflate(
                        R.layout.item_dialog, null);
                PopupWindow popupWindow = new Dialog(contentView);
                popupWindow.showAsDropDown(view);
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        data = new ArrayList<>();
        for(int i=0;i<50;i++){
//            data.add(i+"########");
            data.add(new Bean(i+""));
        }

//        mRecycleAdapter2 adapter2 = new mRecycleAdapter2(data)
//                .register(new ViewBinder<TestFactory.ViewHolder, String>() {
//                    @Override
//                    public void onBind(TestFactory.ViewHolder holder, String data, int position) {
//                        holder.mName.setText(data);
//                    }
//                })
//                .register(new ViewBinder<TestFactory.ViewHolder2, String>() {
//                    @Override
//                    public void onBind(TestFactory.ViewHolder2 holder, String data, int position) {
//                        holder.mName.setText(data);
//                    }
//                })
//                .register(new ViewBinder<TestFactory.ViewHolder3, String>() {
//                    @Override
//                    public void onBind(TestFactory.ViewHolder3 holder, String data, int position) {
//                        holder.mName.setText(data);
//                    }
//                });

        mRecycleAdapter ffdafsd = new mRecycleAdapter(data);

        adaptor = Adaptor.builder()
                .creator(new TestHolderCreator())
                .holderClass(new HolderClass() {
                    @Override
                    public Class getHolderClass(int position) {
                        if(data.get(position) instanceof Bean){
                            return TestHolderCreator.ViewHolder2.class;
                        }else {
                            return TestHolderCreator.ViewHolder.class;
                        }
                    }
                })
//                .binder(new ViewBinder<TestHolderCreator.ViewHolder, Integer>() {
//                    @Override
//                    public void onBind(TestHolderCreator.ViewHolder holder, Integer data2) {
//                        int width = recyclerView.getWidth();
//                        holder.mName.getLayoutParams().width = width-100;
//                        holder.mName.setText(data2+"");
//                        if(data2%2==0){
//                            holder.mName.setBackgroundColor(Color.parseColor("#eeeeee"));
//                        }else{
//                            holder.mName.setBackgroundColor(Color.parseColor("#ffffff"));
//                        }
//                    }
//                })
                .binder(new ViewBinder<TestHolderCreator.ViewHolder2, Bean>() {
                    @Override
                    public void onBind(TestHolderCreator.ViewHolder2 holder, Bean data) {
//                        holder.mName.setText(data.getName());
//                        holder.mName.setOnClickListener();
                    }
                })
//                .binder(new ViewBinder<TestFactory.ViewHolder3, String>() {
//                    @Override
//                    public void onBind(TestFactory.ViewHolder3 holder, String data) {
//                        holder.mName.setText(data);
//                    }
//                })
                .moreLoader(new MoreLoader() {

                    int i = 0;
                    @Override
                    protected void onLoadMore(final LoadCallback callback) {
                        recyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                i++;
                                if(i==2){
                                    callback.error();
                                    return;
                                }

                                List data = getList();
                                callback.loadCompleted(data);
                            }
                        },3000);
//                        callback.error();
                    }

                    @Override
                    protected boolean hasMore() {
                        return adaptor.getData().size()==200?false:true;
                    }
                })
                .enableDiff()
                .build();
        adaptor.setDatas(data);

        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setAdapter(ffdafsd);

//        new LinearSnapHelper().attachToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                int  FirstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
//                int  FirstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
//                int  LastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
//                int  LastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
//                View view = layoutManager.findViewByPosition(FirstVisibleItemPosition);
//                tv.setText("FirstVisibleItemPosition："+FirstVisibleItemPosition+"=="+view.getLeft()
//                        +"\nFirstCompletelyVisibleItemPosition:"+FirstCompletelyVisibleItemPosition
//                        +"\nLastVisibleItemPosition:"+LastVisibleItemPosition
//                        +"\nLastCompletelyVisibleItemPosition:"+LastCompletelyVisibleItemPosition);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d("newState",newState+"");
                if(newState==SCROLL_STATE_IDLE||newState==SCROLL_STATE_SETTLING){
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {

//                            scroll();
                        }
                    });
                }

            }
        });
//        recyclerView.setOnTouchListener(new View.OnTouchListener() {
//
//            float preX;
//            long preTime=0;
//
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
//                    preX =motionEvent.getX();
//                    preTime = currentTimeMillis();
//                }else if(motionEvent.getAction()==MotionEvent.ACTION_MOVE){
//                    return false;
//                } else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
//                    float diff = motionEvent.getX()-preX;
//                    final long diffTime = System.currentTimeMillis() - preTime;
//                    if(diff<0){
//                        view.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                if(diffTime>200){
//                                    autoScroll();
//                                }else {
//                                    scrollToNext();
//                                }
//
//
//                            }
//                        });
//                    }else{
//                        view.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                if(diffTime>200){
//                                    autoScroll();
//                                }else {
//                                    scrollToLast();
//                                }
//                            }
//                        });
//                    }
//
//                }
//                return true;
//            }
//        });
//        tv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                autoScroll();
//            }
//        });
        PLPlayer plPlayer = new PLPlayer();
        plPlayer.init(this);
        plPlayer.setPlayListener(new IPlayer.Listener() {
            @Override
            public void onBufferingUpdate(IPlayer player, int percent) {

            }

            @Override
            public void onPrepared(IPlayer player) {
                player.start();
            }

            @Override
            public void onCompletion(IPlayer player) {

            }

            @Override
            public boolean onError(IPlayer player, int what, int extra) {
                return false;
            }

            @Override
            public boolean onInfo(IPlayer player, int what, int extra) {
                if(MEDIA_INFO_AUDIO_RENDERING_START == what){
                    player.seekTo(206212);
                }
                return false;
            }

            @Override
            public void onSeekComplete(IPlayer player) {

            }

            @Override
            public void onVideoSizeChanged(IPlayer player, int width, int height, int sar_num, int sar_den) {

            }
        });
        plPlayer.play("/storage/6AB8-10FD/Android/data/com.teamnet.hula/download/比起脱单，现在90后更关心脱发.mp3");
    }

    private void autoScroll(){
        int  FirstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        View vieww = layoutManager.findViewByPosition(FirstVisibleItemPosition);
        int left = -vieww.getLeft();
        int with = vieww.getWidth()/2;
        if(left<with){
            scrollToLast();
        }else{
            scrollToNext();
        }
    }

    private void scrollToLast(){
        int  FirstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        View vieww = layoutManager.findViewByPosition(FirstVisibleItemPosition);
        recyclerView.smoothScrollBy(vieww.getLeft(),0);
    }

    private void scrollToNext(){
        int  FirstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        View view = layoutManager.findViewByPosition(FirstVisibleItemPosition);
        recyclerView.smoothScrollBy(view.getWidth()+view.getLeft(),0);
    }

    public List getList(){
        int lastId  =50;// data.size();
        List data2 = new ArrayList<>();
        for(int i=lastId;i<lastId+50;i++){
            data2.add(new Bean(i+""));
        }
        return data2;
    }







}
