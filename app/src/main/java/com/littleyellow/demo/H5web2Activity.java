package com.littleyellow.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by xjm on 2019/2/12.
 */
public class H5web2Activity extends AppCompatActivity {

    public static final String BUNDLE_PARAM_URL = "url";

    WebView webView;

    private String url;

    public static void start(Activity context, String title,int requestCode) {
        Intent starter = new Intent(context, H5web2Activity.class);
        starter.putExtra(BUNDLE_PARAM_URL,title);
        context.startActivityForResult(starter,requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        if (getIntent() != null) {
            url = getIntent().getStringExtra(BUNDLE_PARAM_URL);
        }
        initWeb();
    }

    private void initWeb() {
        webView = findViewById(R.id.h5web);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.startsWith("http")) {
                    try {
                        // 以下固定写法
                        final Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(url));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    } catch (Exception e) {
                        // 防止没有安装的情况
                        e.printStackTrace();
                    }
                }else {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        });
        webView.loadUrl(url);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                setResult(666);
                return false;
            }
        });
//        if(!TextUtils.isEmpty(thirdAccessToken)){
//            Map<String,String> headMap = new HashMap<>();
//            headMap.put("thirdAccessToken",thirdAccessToken);
//            webView.loadUrl(url,headMap);
//            thirdAccessToken = null;
//        }else {
//            webView.loadUrl(url);
//        }

        //脑残前端非要我去拿证书，我没办法，只有先写这么一个方法过度下咯
//        CordovaHttp.acceptAllCerts(true);
//        webView.setWebViewClient(new MyWebViewClient(webView.getParentEngine()));
//        webView.setWebChromeClient(new MyWebChromeClient(webView.getParentEngine()));

//        PEWebProxyBridgeWrap peBridge = new PEWebProxyBridgeWrap(this, webView);
//        webView.addJavascriptInterface(peBridge, "ProxyBridge");
//        xqcH5Javascript = new XqcH5Javascript(this, webView);
//        webView.addJavascriptInterface(xqcH5Javascript, JS_EVENT_XqcH5JS);
    }
}
