package com.android.customer.music.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.customer.music.R;
import com.android.customer.music.adapter.MainAdapter;
import com.android.customer.music.adapter.RecyclerAdapter;
import com.android.customer.music.api.Api;
import com.android.customer.music.constant.Constants;
import com.android.customer.music.event.CustomEvent;
import com.android.customer.music.helper.DialogHelper;
import com.android.customer.music.helper.LoadingDialogHelper;
import com.android.customer.music.helper.RetrofitHelper;
import com.android.customer.music.listener.DialogListener;
import com.android.customer.music.listener.OnItemClickListener;
import com.android.customer.music.model.ApkModel;
import com.android.customer.music.model.BottomBarVo;
import com.android.customer.music.model.RecommendMusicModel;
import com.android.customer.music.model.UserInfoVo;
import com.android.customer.music.presenter.MainPresenter;
import com.android.customer.music.topmessage.utils.FloatWindowManager;
import com.android.customer.music.utils.DataCleanManager;
import com.android.customer.music.utils.HandlerUtils;
import com.android.customer.music.utils.NotificationPermissionUtil;
import com.android.customer.music.utils.SharedPreferenceUtil;
import com.android.customer.music.utils.UpdateManager;
import com.android.customer.music.utils.UserManager;
import com.android.customer.music.view.BottomBarView;
import com.android.customer.music.view.GlideImageLoader;
import com.android.customer.music.view.MainView;
import com.android.customer.music.view.RecyclerDecoration;
import com.android.customer.music.view.TitleView;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.CleanUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.bugly.beta.Beta;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DrawerActivity extends BaseActivity implements OnRefreshListener, TitleView.OnRightClickListener, MainView, MainAdapter.OnMainAdapterListener, NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView rv_recommend;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView recyclerView;
    private MainAdapter mainAdapter;
    private MainPresenter<MainView> mainPresenter;
    private SmartRefreshLayout refreshLayout;
    private ShimmerRecyclerView mShimmerRecyclerView;
    private ShimmerRecyclerView gridShimmerRecyclerView;
    private TitleView titleView;
    private String token;
    private ApkModel apkModel;
    private boolean isRefresh = false;
    private BottomBarView bottomBarView;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.CAMERA};
    private MyMusicReceiver musicReceiver;
    private static final int REQUEST_CODE = 1002;

    @Override
    protected void initView() {
        rv_recommend = fd(R.id.rv_recommend);
        recyclerView = fd(R.id.recyclerView);
        refreshLayout = fd(R.id.refreshLayout);
        navigationView = fd(R.id.navigation_view);
        drawerLayout = fd(R.id.drawer_layout);
        refreshLayout.setOnRefreshListener(this);
        mShimmerRecyclerView = fd(R.id.shimmer_recycler_view);
        gridShimmerRecyclerView = fd(R.id.grid_shimmer_recycler_view);
        titleView = fd(R.id.navigation);
        bottomBarView = fd(R.id.bottom_bar_view);
        titleView.setRightClickListener(this);
    }

    @Override
    protected void initData() {
        initImagePicker();
        setSwipeBackEnable(false);
        gridShimmerRecyclerView.showShimmerAdapter();
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        CircleImageView imageView = headerView.findViewById(R.id.iv_icon);
        TextView textView = headerView.findViewById(R.id.tv_name);
        UserInfoVo userInfoVo = UserManager.getUserInfoVo(mActivity);
        if (userInfoVo != null) {
            titleView.setSrcBack(userInfoVo.getAvatarUrl());
            Glide.with(mActivity).load(userInfoVo.getAvatarUrl()).into(imageView);
            textView.setText(userInfoVo.getNickName());
        }
        musicReceiver = new MyMusicReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("mainMusic");
        registerReceiver(musicReceiver, filter);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(mActivity, MyActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (musicReceiver != null) {
            unregisterReceiver(musicReceiver);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void checkNoticePermission() {
        NotificationPermissionUtil.checkNotificationEnable(mActivity);
        FloatWindowManager.getInstance().applyOrShowFloatWindow(this);
    }

    public void play(BottomBarVo bottomBarVo) {
        if (bottomBarView != null) {
            bottomBarView.play(bottomBarVo);
        }
    }

    @Override
    public void onEvent(Object object) {
        super.onEvent(object);
        if (object instanceof CustomEvent) {
            setBottomBarData();
        }
    }

    private void requestPermission() {
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, PERMISSIONS, REQUEST_CODE);
            }
        }
    }

    @Override
    protected void musicStop() {
        super.musicStop();
        bottomBarView.close();
    }

    private void setBottomBarData() {
        SharedPreferenceUtil preferenceUtil = new SharedPreferenceUtil();
        String json = preferenceUtil.getObject(mActivity, Constants.CURRENT_BOTTOM_VO);
        Type type = new TypeToken<BottomBarVo>() {
        }.getType();
        BottomBarVo bottomBarVo = new Gson().fromJson(json, type);
        bottomBarView.setBottomBarVo(bottomBarVo);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void initAction() {
        mainPresenter = new MainPresenter<>(this);
        mainPresenter.getRecommendList();
        mShimmerRecyclerView.showShimmerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setNestedScrollingEnabled(false);
        mainAdapter = new MainAdapter(mActivity, recyclerView, mainPresenter.getTitles(), mainPresenter.getTypes(), bottomBarView);
        mainAdapter.setOnMainAdapterListener(this);
        recyclerView.setAdapter(mainAdapter);

        SharedPreferenceUtil preferenceUtil = new SharedPreferenceUtil();
        token = preferenceUtil.getObject(mActivity, Constants.CURRENT_TOKEN);


        requestPermission();
        //检测通知栏权限
        checkNoticePermission();
        setBottomBarData();

        if (!isRefresh) {
            checkUpdate(false);
        }
    }

    private void checkUpdate(boolean flag) {
        LoadingDialogHelper.show(mActivity, "检查更新中...");
        String packageName = AppUtils.getAppPackageName();
        Integer versionCode = AppUtils.getAppVersionCode();
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        final Api api = retrofitHelper.initRetrofit(Constants.SERVER_URL);
        Observable<Map> observable = api.apkUpdate(token, packageName, versionCode);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onNext(Map map) {
                        boolean handler = HandlerUtils.isHandler(map, mActivity);
                        if (!handler) {
                            Type type = new TypeToken<ApkModel>() {
                            }.getType();
                            Gson gson = new Gson();
                            apkModel = gson.fromJson(Objects.requireNonNull(gson.toJson(map.get("data"))), type);
                            int appVersionCode = AppUtils.getAppVersionCode();
                            if (apkModel != null && apkModel.getApkCode() > appVersionCode) {
                                showNotice(apkModel);
                            } else {
                                if (!flag) return;
                                ToastUtils.showShort("没有版本可更新！");
                            }
                        } else {
                            if (!flag) return;
                            ToastUtils.showShort("没有版本可更新！");
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onError(Throwable e) {
                        if (flag) {
                            LoadingDialogHelper.dismiss();
                        }
                        Beta.checkUpgrade();
                    }

                    @Override
                    public void onComplete() {
                        if (flag) {
                            LoadingDialogHelper.dismiss();
                        }
                    }
                });
    }

    private void showNotice(ApkModel apkModel) {
        // 这里来检测版本是否需要更新
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
        } else {
            UpdateManager mUpdateManager = new UpdateManager(mActivity);
            mUpdateManager.checkUpdateInfo(apkModel);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_drawer;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        isRefresh = true;
        initAction();
        gridShimmerRecyclerView.showShimmerAdapter();
    }

    @Override
    public void clickRight(View view) {
        Intent intent = new Intent(mActivity, SearchActivity.class);
        startActivity(intent);
    }

    @Override
    public void clickLeft(View view) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void success(RecommendMusicModel result) {
        gridShimmerRecyclerView.hideShimmerAdapter();
        setGridData(result);
        refreshLayout.finishRefresh(2000);
    }

    private void setGridData(RecommendMusicModel result) {
        rv_recommend.setLayoutManager(new GridLayoutManager(mActivity, 3));
        rv_recommend.addItemDecoration(new RecyclerDecoration(getResources().getDimensionPixelSize(R.dimen.itemWidth)));
        rv_recommend.setNestedScrollingEnabled(false);
        recyclerAdapter = new RecyclerAdapter(mActivity, result.getResult().getList());
        rv_recommend.setAdapter(recyclerAdapter);
        recyclerAdapter.setOnItemClickListener(new OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void play(BottomBarVo bottomBarVo) {
                bottomBarView.play(bottomBarVo);
            }
        });
    }

    @Override
    public void fail(String msg) {
        LoadingDialogHelper.dismiss();
        gridShimmerRecyclerView.hideShimmerAdapter();
        ToastUtils.showShort(msg);
        refreshLayout.finishRefresh(false);//传入false表示刷新失败
    }

    @Override
    public void showLoading(String msg) {
    }

    @Override
    public void dismissLoading() {
    }

    @Override
    public void success() {
        mShimmerRecyclerView.hideShimmerAdapter();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_item_about:
                //关于
                startActivity(new Intent(mActivity, AboutActivity.class));
                break;
            case R.id.navigation_item_clean:
                //清除缓存
                clean();
                break;
            case R.id.navigation_item_update:
                //检测更新
                checkUpdate(true);
                break;
            case R.id.navigation_item_develop:
                //赞助开发者
                startActivity(new Intent(mActivity, DevelopActivity.class));
                break;
            case R.id.navigation_item_share:
                //分享APP
                startActivity(new Intent(mActivity, ShareActivity.class));
                break;
            case R.id.navigation_item_notice:
                //通知
                startActivity(new Intent(mActivity, NoticeActivity.class));
                break;
            case R.id.navigation_item_logout:
                //退出登录
                logout();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void clean() {
        DialogHelper.getInstance().showSureDialog(mActivity, "温馨提示", "你确定要清理缓存吗？",
                new DialogListener() {
                    @Override
                    public void clickSure() {
                        //计算缓存
                        DataCleanManager.clearAllCache(mActivity);
                        ToastUtils.showShort("清理成功");
                    }

                    @Override
                    public void clickCancel() {

                    }
                });
    }

    private void logout() {
        DialogHelper.getInstance().showSureDialog(mActivity, "温馨提示"
                , "你确定要退出登录吗？退出登录后，当前用户信息将被清空",
                new DialogListener() {
                    @Override
                    public void clickSure() {
                        //CleanUtils.cleanInternalSp();
                        CleanUtils.cleanInternalFiles();
                        CleanUtils.cleanExternalCache();
                        CleanUtils.cleanInternalCache();
                        LoginActivity.startActivity(mActivity);
                    }

                    @Override
                    public void clickCancel() {

                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            UpdateManager mUpdateManager = new UpdateManager(mActivity);
            mUpdateManager.checkUpdateInfo(apkModel);
        }
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(true);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(1);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    private class MyMusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if ("mainMusic".equals(action)) {
                    setBottomBarData();
                }
            }
        }
    }
}
