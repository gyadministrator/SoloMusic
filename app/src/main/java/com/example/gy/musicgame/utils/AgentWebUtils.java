package com.example.gy.musicgame.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.gy.musicgame.R;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.plugin.NativePlugin;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebConfig;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebViewClient;

/**
 * Date:2019/12/10
 * TIME:11:27
 * author:fldserver
 * email:1984629668@qq.com
 **/
public class AgentWebUtils {

    @SuppressLint("StaticFieldLeak")
    private static AgentWeb mAgentWeb;

    /**
     * 打开网页
     *
     * @param activity  活动
     * @param container 装网页的容器
     * @param url       链接地址
     * @return AgentWeb
     */
    public static AgentWeb openWeb(Activity activity, LinearLayout container, String url, WebChromeClient webChromeClient, WebViewClient webViewClient) {
        mAgentWeb = AgentWeb.with(activity)
                .setAgentWebParent(container, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                .closeIndicator()
                .setWebChromeClient(webChromeClient)
                .setWebViewClient(webViewClient)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)
                .interceptUnkownUrl()
                .createAgentWeb()
                .ready()
                .go(url);
        operationAndroidFunction();
        return mAgentWeb;
    }

    /**
     * 打开网页
     *
     * @param fragment  fragment
     * @param container 装网页的容器
     * @param url       链接地址
     * @return AgentWeb
     */
    public static AgentWeb openWeb(Fragment fragment, LinearLayout container, String url, WebChromeClient webChromeClient, WebViewClient webViewClient) {
        mAgentWeb = AgentWeb.with(fragment)
                .setAgentWebParent(container, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                .useDefaultIndicator(-1, 3)
                .setWebChromeClient(webChromeClient)
                .setWebViewClient(webViewClient)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)
                .interceptUnkownUrl()
                .createAgentWeb()
                .ready()
                .go(url);
        operationAndroidFunction();
        return mAgentWeb;
    }

    /**
     * Android执行JS的方法
     *
     * @param method 方法名
     * @param params 参数
     */
    public static void operationJavaScriptFunction(String method, String... params) {
        if (mAgentWeb != null) {
            mAgentWeb.getJsAccessEntrace().quickCallJs(method, params);
        }
    }

    /**
     * 网页执行Android的方法
     */
    public static void operationAndroidFunction() {
        if (mAgentWeb != null) {
            mAgentWeb.getJsInterfaceHolder().addJavaObject(Constants.PLUGIN_NAME, new NativePlugin());
            //window.android.方法名;
        }
    }

    public static void onDestroy(Activity activity) {
        AgentWebConfig.clearDiskCache(activity);
        mAgentWeb = null;
    }
}
