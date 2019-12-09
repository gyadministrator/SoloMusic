package com.example.gy.musicgame.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.ToastUtils;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.adapter.ListRecipeDetailAdapter;
import com.example.gy.musicgame.api.Api;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.helper.LoadingDialogHelper;
import com.example.gy.musicgame.helper.RetrofitHelper;
import com.example.gy.musicgame.model.RecipeDetailModel;
import com.example.gy.musicgame.model.StepModel;
import com.example.gy.musicgame.view.TitleView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DetailActivity extends BaseActivity {
    private TitleView titleView;
    private ListView listView;
    private String cid = "";
    private String name = "";
    private String material = "";
    private List<StepModel> list = new ArrayList<>();

    @Override
    protected void initView() {
        titleView = fd(R.id.titleView);
        listView = fd(R.id.listView);
    }

    @Override
    protected void initData() {
        cid = getIntent().getStringExtra("cid");
        String url = getIntent().getStringExtra("url");
        getData(cid);
    }

    private void getData(String cid) {
        LoadingDialogHelper.show(mActivity, "获取详情中...");
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.RECIPE_URL);
        Observable<RecipeDetailModel> observable = api.getRecipeDetail(Constants.MOB_APP_KEY, cid);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<RecipeDetailModel>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(RecipeDetailModel recipeDetailModel) {
                setData(recipeDetailModel);
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

    private void setData(RecipeDetailModel recipeDetailModel) {
        if (recipeDetailModel != null && recipeDetailModel.getRetCode().equals("200")) {
            if (recipeDetailModel.getResult() != null) {
                if (recipeDetailModel.getResult().getRecipe() != null) {
                    name = recipeDetailModel.getResult().getRecipe().getTitle();
                    material = recipeDetailModel.getResult().getRecipe().getIngredients();
                    if (material != null) {
                        material = material.substring(material.indexOf("[") + 1, material.indexOf("]") - 1);
                    }
                    String method = recipeDetailModel.getResult().getRecipe().getMethod();
                    if (!TextUtils.isEmpty(method)) {
                        initJson(method);
                    }
                }
            }
            if (list.size() != 0) {
                View view = LayoutInflater.from(DetailActivity.this).inflate(R.layout.activity_detail_header, null);
                TextView tvName = view.findViewById(R.id.tv_name);
                TextView tvMaterial = view.findViewById(R.id.tv_gredient);
                tvName.setText(name);
                tvMaterial.setText(material);
                listView.addHeaderView(view);
                ListRecipeDetailAdapter listAdapter = new ListRecipeDetailAdapter(DetailActivity.this, list);
                listView.setAdapter(listAdapter);
            }
        }
    }

    private void initJson(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String img = jsonObject.getString("img");
                String step = jsonObject.getString("step");
                StepModel s = new StepModel();
                s.setImg(img);
                s.setStep(step);

                list.add(s);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void startActivity(Activity activity, String cid, String url) {
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra("cid", cid);
        intent.putExtra("url", url);
        activity.startActivity(intent);
    }

    @Override
    protected void initAction() {
       /* titleView.setRightClickListener(new TitleView.OnRightClickListener() {
            @Override
            public void clickRight(View view) {

            }

            @Override
            public void clickLeft(View view) {

            }
        });*/
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_detail;
    }
}
