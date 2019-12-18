package com.android.customer.music.presenter;

import com.android.customer.music.helper.RetrofitHelper;

import java.util.List;
import java.util.Map;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/11/2 18:57
 */
public class BasePresenter {
    private RetrofitHelper mRetrofitHelper;
    protected List<String> mTitle;
    protected List<Integer> mType;
    protected Map<String, Object> mParams;

    public BasePresenter() {
        mRetrofitHelper = RetrofitHelper.getInstance();
        mTitle = mRetrofitHelper.getTitles();
        mType = mRetrofitHelper.getTypes();
        mParams = mRetrofitHelper.getmParams();
    }
}
