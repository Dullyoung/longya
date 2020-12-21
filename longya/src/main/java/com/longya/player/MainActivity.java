package com.longya.player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.dl7.playerdemo.R;
import com.longya.player.utils.StatusBarUtil;

public class MainActivity extends AppCompatActivity {


    private WebView webView;
    private ProgressBar progressBar;
    private ImageView imageView;

    private FrameLayout flWebViewLoading;
//    private ImageView imageLoading;

    private FrameLayout flSplash;
    private TextView tvTimeJump;
    private int i = 3;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //展示的数据
            tvTimeJump.setText(i + "秒|跳过");
            if ((Integer) msg.obj == 0) {
                //跳转
                flSplash.setVisibility(View.GONE);

//                Intent intent = new Intent(MainActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
            }
        }
    };

    private void refresh() {
        String url = "http://www.360jrs.net/index.html?ue=android";
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
        String userName = sharedPreferences.getString("longya_username", "");
        String password = sharedPreferences.getString("longya_password", "");
        String action = sharedPreferences.getString("action", "");
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            if (action.equals("logout")) {
                url += "&action=logout";
            }
            webView.loadUrl(url);
            Toast.makeText(this, "当前加载网址：" + url, Toast.LENGTH_LONG).show();
        } else {
            webView.loadUrl(url + "&username=" + userName + "&password=" + password + "&action=" + action);
            Log.i("aaaa", "url: " + url + "&username=" + userName + "&password=" + password+ "&action=" + action);
            Toast.makeText(this, "当前加载网址：" + url + "&username=" + userName + "&password=" + password+ "&action=" + action, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StatusBarUtil.transparencyBar(MainActivity.this); //设置状态栏全透明
        StatusBarUtil.StatusBarLightMode(MainActivity.this); //设置白底黑字

        setContentView(R.layout.activity_main);

        flSplash = (FrameLayout) findViewById(R.id.fl_splash);
        tvTimeJump = (TextView) findViewById(R.id.tv_time_jump);
        tvTimeJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转
                flSplash.setVisibility(View.GONE);
            }
        });

        flWebViewLoading = (FrameLayout) findViewById(R.id.fl_webviewLoading);
        flWebViewLoading.setVisibility(View.VISIBLE);

        webView = (WebView) findViewById(R.id.webview);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        imageView = (ImageView) findViewById(R.id.img_splash);
//        scaleImage(this, imageView, R.drawable.guide);
        imageView.setVisibility(View.VISIBLE);

        //webView.loadUrl("file:///android_asset/protocol.htm");//加载asset文件夹下html

        refresh();


        //使用webview显示html代码
//        webView.loadDataWithBaseURL(null,"<html><head><title> 欢迎您 </title></head>" +
//                "<body><h2>使用webview显示 html代码</h2></body></html>", "text/html" , "utf-8", null);

        webView.addJavascriptInterface(new JsObject(this), "android");//添加js监听 这样html就能调用客户端
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(webViewClient);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);//允许使用js


        /**
         * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
         * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
         * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
         * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
         */
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存，只从网络获取数据.

        //支持屏幕缩放
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);

        //不显示webview缩放按钮
//        webSettings.setDisplayZoomControls(false);


        //TEST
//        findViewById(R.id.btn_video).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, IjkPlayerActivity.class));
//            }
//        });
//        findViewById(R.id.btn_fullscreen_video).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, IjkFullscreenActivity.class));
//            }
//        });
//        findViewById(R.id.btn_test_aspect).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, TestAspectActivity.class));
//            }
//        });
//        findViewById(R.id.btn_custom_danmaku).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, CustomDanmakuActivity.class));
//            }
//        });
//        findViewById(R.id.btn_switch_video).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, SwitchVideoActivity.class));
//            }
//        });

        //在子线程中做耗时操作
        new Thread() {
            @Override
            public void run() {
                super.run();
                //执行三秒跳转
                while (i <= 3) {
                    i--;
                    Message msg = new Message();
                    msg.obj = i;
                    handler.sendMessage(msg);
                    try {
                        //跳转
                        sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }


    //WebViewClient主要帮助WebView处理各种通知、请求事件
    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {//页面加载完成
            progressBar.setVisibility(View.GONE);
            flWebViewLoading.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("ansen", "拦截url:" + url);
            if (url.equals("http://www.google.com/")) {
                //Toast.makeText(MainActivity.this,"国内不能访问google,拦截该url",Toast.LENGTH_LONG).show();
                return true;//表示我已经处理过了
            }
            return super.shouldOverrideUrlLoading(view, url);
        }


    };

    //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
    private WebChromeClient webChromeClient = new WebChromeClient() {
        //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
            localBuilder.setMessage(message).setPositiveButton("确定", null);
            localBuilder.setCancelable(false);
            localBuilder.create().show();

            //注意:
            //必须要这一句代码:result.confirm()表示:
            //处理结果为确定状态同时唤醒WebCore线程
            //否则不能继续点击按钮
            result.confirm();
            return true;
        }

        //获取网页标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            Log.i("ansen", "网页标题:" + title);
        }

        //加载进度回调
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBar.setProgress(newProgress);
        }
    };


    public class JsObject {

        private Context context;

        public JsObject(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void postMessage(String str) {
            Log.i("postMessage", "str:" + str);
        }

        @JavascriptInterface
        public void postEventMessage(String str) {
            Log.i("postMessage", "str:" + str);

            Intent intent = new Intent(this.context, IjkPlayerActivity.class);
            intent.putExtra("event", (String) str);
            startActivity(intent);
        }

        @JavascriptInterface
        public void postLoginMessage(String str) {
            Log.i("postMessage", "str:" + str);
            ActionBean actionBean = null;
            try {
                actionBean = JSONObject.parseObject(str, ActionBean.class);
            } catch (Exception e) {
                e.printStackTrace();
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "参数转换错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (actionBean != null) {
                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("longya_username", actionBean.getLongya_username());
                editor.putString("longya_password", actionBean.getLongya_password());
                editor.putString("action", actionBean.getAction());
                editor.apply();
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                });

            }

        }

        @JavascriptInterface
        public void postLogoutMessage(String str) {
            Log.i("postMessage", "str:" + str);
            Log.i("postMessage", "str:" + str);
            ActionBean actionBean = null;
            try {
                actionBean = JSONObject.parseObject(str, ActionBean.class);
            } catch (Exception e) {
                e.printStackTrace();
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "参数转换错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (actionBean != null) {
                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("longya_username", "");
                editor.putString("longya_password", "");
                editor.putString("action", actionBean.getAction());
                editor.apply();
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                });
            }

        }

    }

}
