package com.laomachaguan.Common;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.laomachaguan.R;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.StatusBarCompat;


/**
 * Created by Administrator on 2016/10/11.
 */
public class AD extends AppCompatActivity {
    private WebView webView;
    private ImageView back;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, (Color.parseColor("#90000000")));

        setContentView(R.layout.enter_wrap);
        webView= (WebView) findViewById(R.id.enter_wrap_web);
        webView.loadUrl(getIntent().getStringExtra("url"));
        WebSettings webSettings=webView.getSettings();
        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        ProgressUtil.show(AD.this,"","正在加载....");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                ProgressUtil.dismiss();
            }

        });
        findViewById(R.id.enter_wrap_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView=null;
                finish();
            }
        });
    }
    @Override
    //设置回退
    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack(); //goBack()表示返回WebView的上一页面
            return true;
        }
        webView=null;
        finish();//结束退出程序
        return false;
    }

    @Override
    protected void onPause() {
        if(webView!=null)
        webView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(webView!=null)
        webView.onResume();
        super.onResume();
    }
}
