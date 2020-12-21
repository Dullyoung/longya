package com.longya.player;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dl7.player.media.IjkPlayerView;
import com.dl7.player.utils.SoftInputUtils;
import com.dl7.playerdemo.R;
import com.longya.player.utils.StatusBarUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

public class IjkPlayerActivity extends AppCompatActivity {

//    private static final String VIDEO_URL = "rtmp://bo.gxdqa.cn/live/aaaa12";
//    private static final String VIDEO_HD_URL = "rtmp://bo.gxdqa.cn/live/aaaa12";

//    private static final String IMAGE_URL = "http://vimg2.ws.126.net/image/snapshot/2016/11/I/M/VC62HMUIM.jpg";

    Toolbar mToolbar;
    private IjkPlayerView mPlayerView;
    private View mEtLayout;
    private EditText mEditText;
    private Button mIvSend;
    private boolean mIsFocus;
    private ImageView mShareIv;

    private WebView webView;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.transparencyBar(IjkPlayerActivity.this); //设置状态栏全透明
        StatusBarUtil.StatusBarLightMode(IjkPlayerActivity.this); //设置白底黑字
        setContentView(R.layout.activity_ijk_player);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mPlayerView = (IjkPlayerView) findViewById(R.id.player_view);
        mEtLayout = findViewById(R.id.ll_layout);
        mEditText = (EditText) findViewById(R.id.et_content);
        mIvSend = (Button) findViewById(R.id.btn_send);
        mShareIv = (ImageView) findViewById(R.id.iv_share);

        mShareIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });


        String str = getIntent().getStringExtra("event");
        Integer index = str.indexOf('|');

        String rtmp = str.substring(0, index);

        String next = str.substring(index + 1, str.length());
        Integer nextIndex = next.indexOf('|');
        String chat = next.substring(0, nextIndex);
        title = next.substring(nextIndex + 1, next.length());


        mToolbar.setTitle(title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        webView = (WebView) findViewById(R.id.webview);
        webView.loadUrl(chat);
        webView.addJavascriptInterface(new IjkPlayerActivity.JsObject(this), "android");//添加js监听 这样html就能调用客户端
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(webViewClient);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);//允许使用js
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存，只从网络获取数据.
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);


//        Glide.with(this).load(IMAGE_URL).fitCenter().into(mPlayerView.mPlayerThumb);
        mPlayerView.init()
                .setTitle(title)
//                .setSkipTip(1000*60*1)
//                .enableDanmaku()
//                .setDanmakuSource(getResources().openRawResource(R.raw.bili))
                .setVideoSource(null, rtmp, rtmp, null, null)
                .setMediaQuality(IjkPlayerView.MEDIA_QUALITY_HIGH)
                .start();

        mIvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerView.sendDanmaku(mEditText.getText().toString(), false);
                mEditText.setText("");
                _closeSoftInput();
            }
        });
        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {
                if (isFocus) {
                    mPlayerView.editVideo();
                }
                mIsFocus = isFocus;
            }
        });

    }

    private void share() {
        UMImage umImage = new UMImage(this, R.mipmap.ic_launcher);
        UMWeb web = new UMWeb("https://www.baidu.com");
        web.setTitle("好的好的分享");//标题
        web.setThumb(umImage);  //缩略图
        web.setDescription("66666666");//描述
        new ShareAction(this)
                .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .withMedia(web)
                .setCallback(new MyUmengListener())
                .open();
    }

    class MyUmengListener implements UMShareListener {

        @Override
        public void onStart(SHARE_MEDIA share_media) {
            Toast.makeText(IjkPlayerActivity.this, "开始分享" + share_media.getName(), Toast.LENGTH_SHORT).show();
            Log.i("aaaaaa", "onStart: " + share_media.getName());
        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            Toast.makeText(IjkPlayerActivity.this, "分享成功" + share_media.getName(), Toast.LENGTH_SHORT).show();
            Log.i("aaaaaa", "onResult: " + share_media.getName());
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            Toast.makeText(IjkPlayerActivity.this, "分享失败" + share_media.getName() + "-"
                    + throwable.getMessage(), Toast.LENGTH_LONG).show();
            Log.i("aaaaaa", "onError: " + share_media.getName() + throwable.getMessage());
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            Log.i("aaaaaa", "onCancel: " + share_media.getName());
            Toast.makeText(IjkPlayerActivity.this, "分享取消" + share_media.getName(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mPlayerView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayerView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayerView.onDestroy();
        UMShareAPI.get(this).release();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mPlayerView.configurationChanged(newConfig);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mPlayerView.handleVolumeKey(keyCode)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (mPlayerView.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (_isHideSoftInput(view, (int) ev.getX(), (int) ev.getY())) {
            _closeSoftInput();
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void _closeSoftInput() {
        mEditText.clearFocus();
        SoftInputUtils.closeSoftInput(this);
        mPlayerView.recoverFromEditVideo();
    }

    private boolean _isHideSoftInput(View view, int x, int y) {
        if (view == null || !(view instanceof EditText) || !mIsFocus) {
            return false;
        }
        return x < mEtLayout.getLeft() ||
                x > mEtLayout.getRight() ||
                y < mEtLayout.getTop();
    }


    //WebViewClient主要帮助WebView处理各种通知、请求事件
    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {//页面加载完成
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
    }
}
