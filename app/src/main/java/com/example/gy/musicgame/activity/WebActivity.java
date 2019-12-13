package com.example.gy.musicgame.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.example.gy.musicgame.R;
import com.example.gy.musicgame.helper.LoadingDialogHelper;
import com.example.gy.musicgame.utils.AgentWebUtils;
import com.example.gy.musicgame.utils.ShareUtils;
import com.example.gy.musicgame.view.TitleView;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

public class WebActivity extends BaseActivity implements OnRefreshListener {
    private TitleView titleView;
    private LinearLayout container;
    private String url;
    private AgentWeb agentWeb;
    private WebView webView;
    private String title;
    private SmartRefreshLayout refreshLayout;

    @Override
    protected void initView() {
        titleView = fd(R.id.titleView);
        container = fd(R.id.ll_container);
        refreshLayout = fd(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void initData() {
        LoadingDialogHelper.show(mActivity, "加载中...");
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        agentWeb = AgentWebUtils.openWeb(mActivity, container, url, mWebChromeClient, mWebViewClient);
        webView = agentWeb.getWebCreator().getWebView();
    }

    public static void startActivity(Activity activity, String url) {
        Intent intent = new Intent(activity, WebActivity.class);
        intent.putExtra("url", url);
        activity.startActivity(intent);
    }

    @Override
    protected void initAction() {
        titleView.setRightClickListener(new TitleView.OnRightClickListener() {
            @Override
            public void clickRight(View view) {
                ShareUtils.showShare(mActivity, title, title, url);
            }

            @Override
            public void clickLeft(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_web;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (agentWeb == null) return super.onKeyDown(keyCode, event);
        if (agentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AgentWebUtils.onDestroy(mActivity);
        if (agentWeb == null) return;
        agentWeb.getWebLifeCycle().onDestroy();
    }

    @Override
    protected void onPause() {
        if (agentWeb == null) return;
        agentWeb.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        if (agentWeb == null) return;
        agentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            LoadingDialogHelper.dismiss();
            title = view.getTitle();
            titleView.setTitle(view.getTitle());
            refreshLayout.finishRefresh(1500);
        }
    };
    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
        }
    };

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        webView.reload();
    }
}
