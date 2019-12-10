package com.example.gy.musicgame.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.blankj.utilcode.util.ToastUtils;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.activity.DetailActivity;
import com.example.gy.musicgame.adapter.RecyclerRecipeAdapter;
import com.example.gy.musicgame.api.Api;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.helper.RetrofitHelper;
import com.example.gy.musicgame.model.BottomBarVo;
import com.example.gy.musicgame.model.RecipeSearchModel;
import com.example.gy.musicgame.model.RecipeTypeModel;
import com.example.gy.musicgame.utils.SharedPreferenceUtil;
import com.example.gy.musicgame.view.BottomBarView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.lqr.dropdownLayout.LQRDropdownLayout;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RecipeFragment extends Fragment implements RecyclerRecipeAdapter.OnRecyclerViewListener, XRecyclerView.LoadingListener, LQRDropdownLayout.OnDropdownListListener {

    private RecipeViewModel mViewModel;
    LQRDropdownLayout dl;
    ShimmerRecyclerView shimmerRecyclerView;

    private List<String> headers = new ArrayList<>();
    private int page = 1;
    private int pageSize = 20;
    private List<RecipeSearchModel.ResultBean.ListBean> list = new ArrayList<>();
    private String cid = "";
    private String recipeName = "";
    private XRecyclerView xRecyclerView;
    private boolean flag = true;
    private int total;
    private RecyclerRecipeAdapter adapter;
    private List<Map<String, String>> mapList = new ArrayList<>();
    private Activity mActivity;
    private BottomBarView bottomBarView;
    private List<Map<String, List<RecipeTypeModel.ResultBean.ChildsBeanX.ChildsBean.CategoryInfoBeanXX>>> maps = new ArrayList<>();
    private ArrayList<RecipeTypeModel.ResultBean.ChildsBeanX.ChildsBean.CategoryInfoBeanXX> categoryInfoBeans;


    public static RecipeFragment newInstance() {
        return new RecipeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipe_fragment, container, false);
        initView(view);
        initData();
        initAction();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setBottomBarData();
                }
            }, 1200);
        }
    }

    private void setBottomBarData() {
        SharedPreferenceUtil<BottomBarVo> preferenceUtil = new SharedPreferenceUtil<>();
        String json = preferenceUtil.getObjectJson(mActivity, Constants.CURRENT_BOTTOM_VO);
        Type type = new TypeToken<BottomBarVo>() {
        }.getType();
        BottomBarVo bottomBarVo = new Gson().fromJson(json, type);
        bottomBarView.setBottomBarVo(bottomBarVo);
    }

    private void initAction() {
        getRecipe();
        shimmerRecyclerView.setVisibility(View.VISIBLE);
        shimmerRecyclerView.showShimmerAdapter();
    }

    private void getRecipe() {
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.RECIPE_URL);
        Observable<RecipeTypeModel> observable = api.getRecipeType(Constants.MOB_APP_KEY);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RecipeTypeModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RecipeTypeModel recipeTypeModel) {
                        setData(recipeTypeModel);
                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showShort(Objects.requireNonNull(e.getMessage()));
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void setData(RecipeTypeModel recipeTypeModel) {
        if (recipeTypeModel.getRetCode().equals("200")) {
            dl.setVisibility(View.VISIBLE);
            if (recipeTypeModel.getResult().getChilds().size() == 0) {
                dl.setVisibility(View.GONE);
            } else {
                List<RecipeTypeModel.ResultBean.ChildsBeanX> childs = recipeTypeModel.getResult().getChilds();
                Map<String, List<RecipeTypeModel.ResultBean.ChildsBeanX.ChildsBean.CategoryInfoBeanXX>> map = new HashMap<>();
                for (RecipeTypeModel.ResultBean.ChildsBeanX c : childs) {
                    RecipeTypeModel.ResultBean.ChildsBeanX.CategoryInfoBeanX categoryInfo = c.getCategoryInfo();
                    String name = categoryInfo.getName();
                    headers.add(name);
                    List<RecipeTypeModel.ResultBean.ChildsBeanX.ChildsBean> cChilds = c.getChilds();
                    categoryInfoBeans = new ArrayList<>();
                    for (RecipeTypeModel.ResultBean.ChildsBeanX.ChildsBean childsBean : cChilds) {
                        RecipeTypeModel.ResultBean.ChildsBeanX.ChildsBean.CategoryInfoBeanXX childsBeanCategoryInfo = childsBean.getCategoryInfo();
                        categoryInfoBeans.add(childsBeanCategoryInfo);
                    }

                    map.put(name, categoryInfoBeans);
                    maps.add(map);
                }

                for (int i = 0; i < headers.size(); i++) {
                    final List<RecipeTypeModel.ResultBean.ChildsBeanX.ChildsBean.CategoryInfoBeanXX> categoryInfoBeanXXES = map.get(headers.get(i));
                    Map<String, String> mapString = new LinkedHashMap<>();
                    assert categoryInfoBeanXXES != null;
                    for (int j = 0; j < categoryInfoBeanXXES.size(); j++) {
                        mapString.put(categoryInfoBeanXXES.get(j).getName(), categoryInfoBeanXXES.get(j).getCtgId());
                    }
                    mapList.add(mapString);
                }
                ArrayList<Integer> list = new ArrayList<>();
                list.add(6);
                list.add(0);
                list.add(7);
                int position = (int) (Math.random() * (list.size() - 1));
                //设置默认值
                cid = categoryInfoBeans.get(list.get(position)).getCtgId();
                recipeName = categoryInfoBeans.get(list.get(position)).getName();
                //初始化数据
                initContent(cid, recipeName);
            }
        }
    }

    private void initContent(String cid, String recipeName) {
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.RECIPE_URL);
        Observable<RecipeSearchModel> observable = api.getRecipeSearchMenu(Constants.MOB_APP_KEY, cid, recipeName, page, pageSize);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<RecipeSearchModel>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(RecipeSearchModel recipeSearchModel) {
                setContent(recipeSearchModel);
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onError(Throwable e) {
                ToastUtils.showShort(Objects.requireNonNull(e.getMessage()));
            }

            @Override
            public void onComplete() {
            }
        });
    }

    private void setContent(RecipeSearchModel recipeSearchModel) {
        shimmerRecyclerView.setVisibility(View.GONE);
        if (recipeSearchModel.getRetCode().trim().equals("20201")) {
            if (list.size() != 0) {
                xRecyclerView.setNoMore(true);
            }
        } else {
            if (recipeSearchModel.getRetCode().equals("200")) {
                list = recipeSearchModel.getResult().getList();
                if (list.size() == 0) {
                    dl.setVisibility(View.GONE);
                    return;
                }
                total = recipeSearchModel.getResult().getTotal();
                if (flag) {
                    if (xRecyclerView == null) {
                        xRecyclerView = new XRecyclerView(getActivity());
                    }
                    xRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
                    xRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
                    xRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    xRecyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    adapter = new RecyclerRecipeAdapter(list, getActivity());
                    adapter.setOnRecyclerViewListener(RecipeFragment.this);
                    xRecyclerView.setAdapter(adapter);
                    flag = false;
                }
                if (page > 1) {
                    list.addAll(recipeSearchModel.getResult().getList());
                }
                xRecyclerView.setLoadingListener(RecipeFragment.this);

                dl.setCols(headers.size());
                if (dl.getChildCount() == 0) {
                    dl.setListMaxHeight(ViewGroup.LayoutParams.MATCH_PARENT);
                    dl.init(xRecyclerView, mapList);
                }
                dl.setOnDropdownListListener(RecipeFragment.this);
            }
        }
    }

    private void initData() {

    }

    private void initView(View view) {
        dl = view.findViewById(R.id.dl);
        shimmerRecyclerView = view.findViewById(R.id.shimmer_recycler_view);
        bottomBarView = view.findViewById(R.id.bottom_bar_view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void click(String cid) {
        DetailActivity.startActivity(getActivity(), cid, null);
    }

    @Override
    public void onRefresh() {
        page = 1;
        initContent(cid, recipeName);
        adapter.setList(list);
        adapter.notifyDataSetChanged();
        xRecyclerView.refreshComplete();
        xRecyclerView.setNoMore(false);
        adapter.setOnRecyclerViewListener(this);
    }

    @Override
    public void onLoadMore() {
        if (total % pageSize == 0) {
            total = total / pageSize;
        } else {
            total = total / pageSize + 1;
        }
        if (page == total) {
            xRecyclerView.setNoMore(true);
            page = total;
        } else {
            ++page;
            initContent(cid, recipeName);
            adapter.setList(list);
            adapter.notifyDataSetChanged();
            xRecyclerView.loadMoreComplete();
        }
        adapter.setOnRecyclerViewListener(this);
    }

    @Override
    public void OnDropdownListSelected(int indexOfButton, int indexOfList, String textOfList, String valueOfList) {
        cid = valueOfList;
        recipeName = textOfList;
        page = 1;
        initContent(cid, recipeName);
        adapter.setList(list);
        adapter.notifyDataSetChanged();
        adapter.setOnRecyclerViewListener(this);
    }

    @Override
    public void onDropdownListOpen() {

    }

    @Override
    public void onDropdownListClosed() {

    }
}
