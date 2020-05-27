package com.littleyellow.utils.webview;

import android.webkit.WebView;

public class WebCommon {

//    public static void setWeb(final Activity activity, final WebView benWEB, final LoaingView loading){
//        benWEB.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String captureType) {
//                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//                i.addCategory(Intent.CATEGORY_OPENABLE);
//                i.setType("*/*");
//                activity.startActivityForResult(Intent.createChooser(i, "test"), 0);
//            }
//        });
//        benWEB.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
//                //打开客服、下载链接用系统组件打开
//                if (s.startsWith("tel:") || s.endsWith(".apk")) {
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
//                    activity.startActivity(intent);
//                    return true;
//                }
//                //不是http/https协议的不打开
//                if (!s.startsWith("http")) {
//                    return true;
//                }
//                webView.loadUrl(s);
//                return true;
//            }
//
//            @Override
//            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
//                if (null != loading) {
//                    webView.setTag(R.id.tag_load_error, false);
//                    loading.showLoading();
//                }
//                super.onPageStarted(webView, s, bitmap);
//            }
//
//            @Override
//            public void onPageFinished(WebView webView, String s) {
//                if (null != loading) {
//                    boolean loadError = (boolean) webView.getTag(R.id.tag_load_error);
//                    if (!Utils.isNetworkAvailable(activity)) {
//                        loading.showError(webView.getContext().getString(R.string.net_web_error));
//                    } else if (loadError) {
//                        loading.showError("");
//                    } else {
//                        loading.showContent();
//                        if (activity instanceof WebViewActivity) {
//                            ((WebViewActivity) activity).isShowDelete(webView.canGoBack());
//                        }
//                    }
//                }
//                super.onPageFinished(webView, s);
//            }
//
//            @Override
//            public void onReceivedError(WebView webView, int i, String s, String s1) {
//                webView.setTag(R.id.tag_load_error,true);
//                webView.stopLoading();
//                super.onReceivedError(webView, i, s, s1);
//            }
//
//            @Override
//            public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
//                //super.onReceivedSslError(webView, sslErrorHandler, sslError);
//                sslErrorHandler.proceed();
//            }
//        });
//
//        benWEB.setWebChromeClient(new WebChromeClient() {
//
//            @Override
//            public boolean onJsConfirm(com.tencent.smtt.sdk.WebView arg0, String arg1, String arg2, JsResult arg3) {
//                return super.onJsConfirm(arg0, arg1, arg2, arg3);
//            }
//
//            View myVideoView;
//            IX5WebChromeClient.CustomViewCallback callback;
//
//            ///////////////////////////////////////////////////////////
//            //
//
//            /**
//             * 全屏播放配置
//             */
//            @Override
//            public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback customViewCallback) {
//                ViewGroup decorView = (ViewGroup) activity.findViewById(android.R.id.content);
//                int size = decorView.getChildCount();
//                for (int i = 0; i < size; i++) {
//                    decorView.getChildAt(i).setVisibility(View.GONE);
//                }
//                decorView.addView(view);
//                myVideoView = view;
//                callback = customViewCallback;
//                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            }
//
//            @Override
//            public void onHideCustomView() {
//                if (callback != null) {
//                    callback.onCustomViewHidden();
//                    callback = null;
//                }
//                if (myVideoView != null) {
//                    ViewGroup decorView = (ViewGroup) activity.findViewById(android.R.id.content);
//                    decorView.removeView(myVideoView);
//                    int size = decorView.getChildCount();
//                    for (int i = 0; i < size; i++) {
//                        decorView.getChildAt(i).setVisibility(View.VISIBLE);
//                    }
//                    myVideoView = null;
//                }
//                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            }
//
//            @Override
//            public boolean onShowFileChooser(com.tencent.smtt.sdk.WebView arg0,
//                                             ValueCallback<Uri[]> arg1, FileChooserParams arg2) {
//                // TODO Auto-generated method stub
//                return super.onShowFileChooser(arg0, arg1, arg2);
//            }
//
//            @Override
//            public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String captureType) {
//                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//                i.addCategory(Intent.CATEGORY_OPENABLE);
//                i.setType("*/*");
//                activity.startActivityForResult(Intent.createChooser(i, "test"), 0);
//            }
//
//
//            @Override
//            public boolean onJsAlert(com.tencent.smtt.sdk.WebView arg0, String arg1, String arg2, JsResult arg3) {
//                /**
//                 * 这里写入你自定义的window alert
//                 */
//                // AlertDialog.Builder builder = new Builder(getContext());
//                // builder.setTitle("X5内核");
//                // builder.setPositiveButton("确定", new
//                // DialogInterface.OnClickListener() {
//                //
//                // @Override
//                // public void onClick(DialogInterface dialog, int which) {
//                // // TODO Auto-generated method stub
//                // dialog.dismiss();
//                // }
//                // });
//                // builder.show();
//                // arg3.confirm();
//                // return true;
//                return super.onJsAlert(null, "www.baidu.com", "aa", arg3);
//            }
//
//            /**
//             * 对应js 的通知弹框 ，可以用来实现js 和 android之间的通信
//             */
//            public void onReceivedTitle(com.tencent.smtt.sdk.WebView arg0, final String arg1) {
//                if (-1 != TextUtils.indexOf(arg1, "404")
//                        || -1 != TextUtils.indexOf(arg1, "500")
//                        || -1 != TextUtils.indexOf(arg1, "Error")
//                        || -1 != TextUtils.indexOf(arg1, "找不到网页")) {
//                    arg0.setTag(R.id.tag_load_error, true);
//                    arg0.stopLoading();
//                }
//                super.onReceivedTitle(arg0, arg1);
//
//            }
//        });
//
//        benWEB.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                Log.e("f返回", benWEB.copyBackForwardList() + "----");
//                if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                    if (keyCode == KeyEvent.KEYCODE_BACK && benWEB.canGoBack()) {
//                        benWEB.goBack();
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });
//        benWEB.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//        benWEB.getSettings().setDefaultTextEncodingName("utf-8");
//        benWEB.getSettings().setJavaScriptEnabled(true);
//        benWEB.getSettings().setDomStorageEnabled(true);
//        benWEB.getSettings().setBlockNetworkImage(false);
//
//        benWEB.setTag(R.id.tag_load_error,false);
//    }

    public static String getHtmlData(String bodyHTML) {
        String head = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
                "<style>img{max-width: 100%; width:100%; height:auto;}</style>" +
                "</head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }

    public static void loadHtmlContent(WebView webView,String content){
        webView.loadDataWithBaseURL(null, getHtmlData(content), "text/html", "UTF-8", null);
    }

}
