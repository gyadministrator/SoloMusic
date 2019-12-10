package com.example.gy.musicgame.activity;

import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.ToastUtils;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.adapter.SearchAdapter;
import com.example.gy.musicgame.api.Api;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.helper.LoadingDialogHelper;
import com.example.gy.musicgame.helper.RetrofitHelper;
import com.example.gy.musicgame.model.BottomBarVo;
import com.example.gy.musicgame.model.SearchMusicModel;
import com.example.gy.musicgame.utils.SharedPreferenceUtil;
import com.example.gy.musicgame.view.BottomBarView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SearchActivity extends BaseActivity implements View.OnClickListener, TextWatcher {
    private EditText etSearch;
    private TextView tvSearch;
    private RecyclerView recyclerView;
    private BottomBarView bottomBarView;

    @Override
    protected void initView() {
        etSearch = fd(R.id.et_search);
        tvSearch = fd(R.id.tv_search);
        recyclerView = fd(R.id.recyclerView);
        bottomBarView = fd(R.id.bottom_bar_view);
    }

    @Override
    protected void initData() {
        tvSearch.setText("返回");
        etSearch.addTextChangedListener(this);
        tvSearch.setOnClickListener(this);

        setBottomBarData();
    }

    @Override
    protected void initAction() {

    }

    private void setBottomBarData() {
        SharedPreferenceUtil<BottomBarVo> preferenceUtil = new SharedPreferenceUtil<>();
        String json = preferenceUtil.getObjectJson(mActivity, Constants.CURRENT_BOTTOM_VO);
        Type type = new TypeToken<BottomBarVo>() {
        }.getType();
        BottomBarVo bottomBarVo = new Gson().fromJson(json, type);
        bottomBarView.setBottomBarVo(bottomBarVo);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_search;
    }

    @Override
    public void onClick(View view) {
        String s = tvSearch.getText().toString();
        String key = etSearch.getText().toString();
        if ("返回".equals(s)) {
            onBackPressed();
        } else if ("搜索".equals(s)) {
            if ("".equals(key)) {
                ToastUtils.showShort("请输入内容");
                return;
            }
            //搜索
            search(key);
        }
    }

    @Override
    protected void hasNet() {
        super.hasNet();
        search(etSearch.getText().toString());
    }

    private void search(String key) {
        LoadingDialogHelper.show(mActivity, "搜索中...");
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.BASE_URL);
        Map<String, Object> params = retrofitHelper.getmParams();
        params.put("method", Constants.METHOD_SEARCH);
        params.put("query", key);
        Observable<SearchMusicModel> observable = api.search(params);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SearchMusicModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SearchMusicModel searchMusicModel) {
                        List<SearchMusicModel.SongBean> song = searchMusicModel.getSong();
                        if (song != null && song.size() > 0) {
                            SearchAdapter searchAdapter = new SearchAdapter(mActivity, song);
                            recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
                            recyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
                            recyclerView.setAdapter(searchAdapter);
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onError(Throwable e) {
                        LoadingDialogHelper.dismiss();
                        ToastUtils.showShort(Objects.requireNonNull(e.getMessage()));
                    }

                    @Override
                    public void onComplete() {
                        LoadingDialogHelper.dismiss();
                    }
                });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        tvSearch.setText("返回");
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() == 0) {
            tvSearch.setText("返回");
        } else {
            tvSearch.setText("搜索");
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.toString().length() == 0) {
            tvSearch.setText("返回");
        } else {
            tvSearch.setText("搜索");
        }
    }
}
