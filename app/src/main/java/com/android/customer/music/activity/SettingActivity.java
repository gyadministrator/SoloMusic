package com.android.customer.music.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.CleanUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.android.customer.music.R;
import com.android.customer.music.adapter.MyListAdapter;
import com.android.customer.music.api.Api;
import com.android.customer.music.constant.Constants;
import com.android.customer.music.helper.DialogHelper;
import com.android.customer.music.helper.LoadingDialogHelper;
import com.android.customer.music.helper.RetrofitHelper;
import com.android.customer.music.listener.DialogListener;
import com.android.customer.music.listener.InputDialogListener;
import com.android.customer.music.model.ApkModel;
import com.android.customer.music.model.ItemModel;
import com.android.customer.music.utils.DataCleanManager;
import com.android.customer.music.utils.HandlerUtils;
import com.android.customer.music.utils.UpdateManager;
import com.android.customer.music.view.GlideImageLoader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.friendship.TIMFriendRequest;
import com.tencent.imsdk.friendship.TIMFriendResult;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class SettingActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListView listView;
    private MyListAdapter listAdapter;
    private List<ItemModel> itemList = new ArrayList<>();
    private static final int REQUEST_CODE = 1002;
    private static final int REQUEST_CODE_SCAN = 1003;
    private String token;
    private ApkModel apkModel;
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void initView() {
        listView = fd(R.id.listView);
    }

    public static void startActivity(Activity activity, String token) {
        Intent intent = new Intent(activity, SettingActivity.class);
        intent.putExtra("token", token);
        activity.startActivity(intent);
    }

    @Override
    protected void initData() {
        initImagePicker();
        try {
            initListView();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        token = intent.getStringExtra("token");
    }

    @Override
    protected void initAction() {

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
                        int loginStatus = TIMManager.getInstance().getLoginStatus();
                        if (loginStatus == TIMManager.TIM_STATUS_LOGINED) {
                            TIMManager.getInstance().logout(new TIMCallBack() {
                                @Override
                                public void onError(int i, String s) {
                                    ToastUtil.toastShortMessage("退出失败：" + i + " " + s);
                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                    }

                    @Override
                    public void clickCancel() {

                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                //扫一扫
                checkPermission();
                break;
            case 1:
                //关于
                startActivity(new Intent(mActivity, AboutActivity.class));
                break;
            case 2:
                //清除缓存
                clean();
                break;
            case 3:
                //检测更新
                checkUpdate();
                break;
            case 4:
                //赞助开发者
                startActivity(new Intent(mActivity, DevelopActivity.class));
                break;
            case 5:
                //分享APP
                startActivity(new Intent(mActivity, ShareActivity.class));
                break;
            case 6:
                //通知
                startActivity(new Intent(mActivity, NoticeActivity.class));
                break;
            case 7:
                //退出登录
                logout();
                break;
        }
    }

    private void checkUpdate() {
        LoadingDialogHelper.show(mActivity, "检测更新中...");
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
                                ToastUtils.showShort("没有版本可更新！");
                            }
                        } else {
                            ToastUtils.showShort("没有版本可更新！");
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showNotice(ApkModel apkModel) {
        // 这里来检测版本是否需要更新
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
        } else {
            UpdateManager mUpdateManager = new UpdateManager(mActivity);
            mUpdateManager.checkUpdateInfo(apkModel);
        }
    }

    /**
     * 检测权限
     */
    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(mActivity, PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(mActivity, PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, PERMISSIONS, REQUEST_CODE);
        } else {
            openScan();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                if (content != null) {
                    if (!content.contains("gy")) {
                        ToastUtils.showShort("二维码不合法！");
                        return;
                    }
                    content = content.substring(2);
                    addFriend(content);
                }
            }
        }
    }

    private void addFriend(final String username) {
        DialogHelper dialogHelper = DialogHelper.getInstance();
        dialogHelper.showInputDialog(mActivity, "请输入添加" + username + "的理由", new InputDialogListener() {
            @Override
            public void sure(String result) {
                //参数为要添加的好友的username和添加理由
                /*try {
                    EMClient.getInstance().contactManager().addContact(username, result);
                    ToastUtils.showShort("发送请求成功");
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    ToastUtils.showShort("添加好友失败：" + e.getErrorCode() + e.getMessage());
                }*/
                TIMFriendRequest request = new TIMFriendRequest(username);
                request.setAddWording(result);
                TIMFriendshipManager.getInstance().addFriend(request, new TIMValueCallBack<TIMFriendResult>() {
                    @Override
                    public void onError(int i, String s) {
                        ToastUtil.toastShortMessage("添加好友失败：" + i + " " + s);
                    }

                    @Override
                    public void onSuccess(TIMFriendResult timFriendResult) {
                        ToastUtil.toastShortMessage("发送请求成功");
                    }
                });
            }
        });
    }

    private void clean() {
        DialogHelper.getInstance().showSureDialog(mActivity, "温馨提示", "你确定要清理缓存吗？",
                new DialogListener() {
                    @Override
                    public void clickSure() {
                        //计算缓存
                        DataCleanManager.clearAllCache(mActivity);
                        listAdapter.setData(2, "0M");
                        ToastUtils.showShort("清理成功");
                    }

                    @Override
                    public void clickCancel() {

                    }
                });
    }

    private void openScan() {
        Intent intent = new Intent(mActivity, CaptureActivity.class);
        /*ZxingConfig是配置类  可以设置是否显示底部布局，闪光灯，相册，是否播放提示音  震动等动能
         * 也可以不传这个参数
         * 不传的话  默认都为默认不震动  其他都为true
         * */

        //ZxingConfig config = new ZxingConfig();
        //config.setShowbottomLayout(true);//底部布局（包括闪光灯和相册）
        //config.setPlayBeep(true);//是否播放提示音
        //config.setShake(true);//是否震动
        //config.setShowAlbum(true);//是否显示相册
        //config.setShowFlashLight(true);//是否显示闪光灯
        //intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
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

    @SuppressLint("InflateParams")
    private void initListView() throws Exception {
        itemList.add(new ItemModel(R.mipmap.scan, getString(R.string.scan), null));
        itemList.add(new ItemModel(R.mipmap.about, getString(R.string.about), null));
        itemList.add(new ItemModel(R.mipmap.clean, getString(R.string.setting), DataCleanManager.getTotalCacheSize(mActivity)));
        itemList.add(new ItemModel(R.mipmap.update, getString(R.string.update), "v" + AppUtils.getAppVersionName()));
        itemList.add(new ItemModel(R.mipmap.develop, getString(R.string.zan), null));
        itemList.add(new ItemModel(R.mipmap.share_normal, getString(R.string.share_app), null));
        itemList.add(new ItemModel(R.mipmap.notice, getString(R.string.notice), null));
        itemList.add(new ItemModel(R.mipmap.exit, getString(R.string.logout), null));
        listAdapter = new MyListAdapter(itemList, mActivity);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            UpdateManager mUpdateManager = new UpdateManager(mActivity);
            mUpdateManager.checkUpdateInfo(apkModel);
        } else if (requestCode == REQUEST_CODE) {
            openScan();
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_setting;
    }
}
