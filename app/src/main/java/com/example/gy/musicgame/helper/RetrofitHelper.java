package com.example.gy.musicgame.helper;

import android.text.TextUtils;

import com.example.gy.musicgame.api.Api;
import com.example.gy.musicgame.constant.Constants;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/11/2 15:48
 */
public class RetrofitHelper {
    private static volatile RetrofitHelper instance;
    private OkHttpClient mOkHttpClient;
    private final int TIMEOUT = 60;
    private Map<String, Object> mParams = new HashMap<>();
    private List<Integer> types = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private static String mBaseUrl;

    private RetrofitHelper() {
        initParams();
        initTypes();
        initTitles();
    }

    private void initTitles() {
        titles.add("新歌榜");
        titles.add("热歌榜");
        titles.add("摇滚榜");
        titles.add("爵士");
        titles.add("流行");
        titles.add("欧美金曲榜");
        titles.add("经典老歌榜");
        titles.add("情歌对唱榜");
        titles.add("影视金曲榜");
        titles.add("网络歌曲榜");
    }

    public List<String> getTitles() {
        return titles;
    }

    private void initTypes() {
        /**
         * type值
         * 1-新歌榜,
         * 2-热歌榜,
         * 11-摇滚榜,
         * 12-爵士,
         * 16-流行,
         * 21-欧美金曲榜,
         * 22-经典老歌榜,
         * 23-情歌对唱榜,
         * 24-影视金曲榜,
         * 25-网络歌曲榜
         */
        types.add(1);
        types.add(2);
        types.add(11);
        types.add(12);
        types.add(16);
        types.add(21);
        types.add(22);
        types.add(23);
        types.add(24);
        types.add(25);
    }

    public List<Integer> getTypes() {
        return types;
    }

    public Map<String, Object> getmParams() {
        return mParams;
    }

    private void initParams() {
        mParams.put("format", "json");
        mParams.put("calback", "");
        mParams.put("from", "webapp_music");
    }

    public static RetrofitHelper getInstance() {
        instance = null;
        if (instance == null) {
            synchronized (RetrofitHelper.class) {
                if (instance == null) {
                    instance = new RetrofitHelper();
                }
            }
        }
        return instance;
    }

    public Api initRetrofit(String baseUrl) {
        mBaseUrl = baseUrl;
        initOKHttp();
        Retrofit retrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(mBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(Api.class);
    }

    public void destroy() {
        instance = null;
    }

    /**
     * 全局httpclient
     *
     * @return
     */
    private void initOKHttp() {
        if (mOkHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
                    .readTimeout(TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                    .writeTimeout(TIMEOUT, TimeUnit.SECONDS);//设置写入超时时间
            builder.proxy(Proxy.NO_PROXY);
            if (mBaseUrl.equals(Constants.BASE_URL)) {
                builder.addInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public Response intercept(@NotNull Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .removeHeader("User-Agent")
                                .addHeader("User-Agent", "Mozilla/5.0 (" +
                                        "                        Windows; U; Windows NT 5.1; en-US; rv:0.9.4" +
                                        "                    )").build();
                        return chain.proceed(request);
                    }
                });
            }
            mOkHttpClient = builder.build();
        }
    }
}
