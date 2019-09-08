package com.littleyellow.utils.common;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.io.Serializable;
import java.util.Random;
import java.util.WeakHashMap;

public class ResultHelper {

    private ResultHelper(){}

    private static ResultHelper get(){
        return SinletonHolder.instance;
    }

    private static class SinletonHolder{
        private static final ResultHelper instance = new ResultHelper();
    }

    private final WeakHashMap<Integer,ResultListener> hashMap = new WeakHashMap();

    public ResultListener getListner(int requestCode){
        return hashMap.get(requestCode);
    }

    public void addListner(int requestCode,ResultListener listener){
        hashMap.put(requestCode,listener);
    }

    public void removeListner(int requestCode){
        hashMap.remove(requestCode);
    }

    public boolean containsCode(int requestCode){
        return hashMap.containsKey(requestCode);
    }


    public static class ResultHandlerFragment extends Fragment {


        public static final int REQUEST_CODE = 0x00100;

        private  Action action;

        public static ResultHandlerFragment newInstance() {
            return new ResultHandlerFragment();
        }

        @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            ResultListener listener = ResultHelper.get().getListner(requestCode);
            if (null != listener) {
                listener.onResult(requestCode,resultCode,data);
                ResultHelper.get().removeListner(requestCode);
            }
        }

        public void setListener(Action action) {
            this.action = action;
        }

        @TargetApi(23) @Override public void onAttach(Context context) {
            super.onAttach(context);
            if(null != action){
                action.onAttach();
            }
        }

        @SuppressWarnings("deprecation")
        @Override public void onAttach(Activity activity) {
            super.onAttach(activity);
            if (Build.VERSION.SDK_INT < 23) {
                if(null != action){
                    action.onAttach();
                }
            }
        }

        public interface Action extends Serializable {

            void onAttach();

            void onResult(int requestCode, int resultCode, Intent data);
        }
    }

    public static void start(Activity activity, final Intent intent, final ResultListener listener){
        Random ran=new Random();
        int requestCode ;
        do{
            requestCode = ran.nextInt(100);
        } while (get().containsCode(requestCode));
        get().start(activity.getFragmentManager(),intent,requestCode,listener);
    }

    public static void start(Activity activity, Intent intent, int requestCode, ResultListener listener){
        get().start(activity.getFragmentManager(),intent,requestCode,listener);
    }

    public static void start(Fragment fragment, final Intent intent, final ResultListener listener){
        Random ran=new Random();
        int requestCode ;
        do{
            requestCode = ran.nextInt(100);
        } while (get().containsCode(requestCode));
        get().start(fragment.getFragmentManager(),intent,requestCode,listener);
    }

    public static void start(Fragment fragment, Intent intent, int requestCode, ResultListener listener){
        get().start(fragment.getFragmentManager(),intent,requestCode,listener);
    }

    private void start(FragmentManager fragmentManager, final Intent intent, final int requestCode, final ResultListener listener){
        ResultHandlerFragment fragment = (ResultHandlerFragment) fragmentManager.findFragmentByTag(
                ResultHandlerFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = ResultHandlerFragment.newInstance();
//            Bundle args = new Bundle();
            final ResultHandlerFragment finalFragment = fragment;
//            args.putSerializable("action",new ResultHandlerFragment.Action(){
//
//                @Override
//                public void onAttach() {
//                    ResultHelper.get().addListner(requestCode,listener);
//                    finalFragment.startActivityForResult(intent, requestCode);
//                }
//
//                @Override
//                public void onResult(int requestCode, int resultCode, Intent data) {
//
//                }
//            });
//            fragment.setArguments(args);
            fragment.setListener(new ResultHandlerFragment.Action(){

                @Override
                public void onAttach() {
                    ResultHelper.get().addListner(requestCode,listener);
                    finalFragment.startActivityForResult(intent, requestCode);
                }

                @Override
                public void onResult(int requestCode, int resultCode, Intent data) {

                }
            });
            fragmentManager.beginTransaction()
                    .add(fragment, fragment.getClass().getSimpleName())
                    .commit();
        } else if (fragment.isDetached()) {
            fragmentManager.beginTransaction().attach(fragment).commit();
        } else {
            ResultHelper.get().addListner(requestCode,listener);
            fragment.startActivityForResult(intent, requestCode);
        }

    }

    public interface ResultListener {
        void onResult(int requestCode, int resultCode, Intent data);
    }

}
