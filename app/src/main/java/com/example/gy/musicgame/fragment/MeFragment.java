package com.example.gy.musicgame.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.CleanUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.activity.AboutActivity;
import com.example.gy.musicgame.activity.ChangePasswordActivity;
import com.example.gy.musicgame.activity.CodeActivity;
import com.example.gy.musicgame.activity.DevelopActivity;
import com.example.gy.musicgame.activity.LoginActivity;
import com.example.gy.musicgame.adapter.MyListAdapter;
import com.example.gy.musicgame.api.Api;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.helper.DialogHelper;
import com.example.gy.musicgame.helper.LoadingDialogHelper;
import com.example.gy.musicgame.helper.RetrofitHelper;
import com.example.gy.musicgame.listener.DialogListener;
import com.example.gy.musicgame.model.ApkModel;
import com.example.gy.musicgame.model.FileVo;
import com.example.gy.musicgame.model.ItemModel;
import com.example.gy.musicgame.model.UserInfoVo;
import com.example.gy.musicgame.utils.DataCleanManager;
import com.example.gy.musicgame.utils.HandlerUtils;
import com.example.gy.musicgame.utils.LogUtils;
import com.example.gy.musicgame.utils.SharedPreferenceUtil;
import com.example.gy.musicgame.utils.UpdateManager;
import com.example.gy.musicgame.utils.UserManager;
import com.example.gy.musicgame.view.GlideImageLoader;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MeFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private MeViewModel mViewModel;
    private Activity mActivity;
    private ListView listView;
    private MyListAdapter listAdapter;
    private CircleImageView ivUser;
    private TextView tvName;
    private List<ItemModel> itemList = new ArrayList<>();
    private static final int REQUEST_CODE_SELECT = 100;
    private ArrayList<ImageItem> images = null;
    private static final int REQUEST_CODE = 1002;
    private static final int REQUEST_CODE_SCAN = 1003;
    private static final int REQUEST_CODE_PREVIEW = 101;
    private String token;
    private ApkModel apkModel;
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};

    public static MeFragment newInstance() {
        return new MeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.me_fragment, container, false);
        initView(view);
        try {
            initData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initAction();
        return view;
    }

    private void initAction() {
        SharedPreferenceUtil<String> preferenceUtil = new SharedPreferenceUtil<>();
        token = preferenceUtil.getObject(mActivity, Constants.CURRENT_TOKEN);
        getUserInfo(token);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            UpdateManager mUpdateManager = new UpdateManager(mActivity);
            mUpdateManager.checkUpdateInfo(apkModel.getDownloadUrl(), apkModel.getContent());
        } else if (requestCode == REQUEST_CODE) {
            openScan();
        }
    }

    private void getUserInfo(String token) {
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.SERVER_URL);
        Observable<Map> observable = api.userInfo(token);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Map map) {
                        boolean handler = HandlerUtils.isHandler(map, mActivity);
                        if (!handler) {
                            Map<String, Object> data = (Map<String, Object>) map.get("data");
                            UserInfoVo userInfoVo = new UserInfoVo();
                            if (data != null) {
                                if (data.containsKey("username")) {
                                    String username = (String) data.get("username");
                                    userInfoVo.setUserName(username);
                                }
                                if (data.containsKey("avatar")) {
                                    String avatar = (String) data.get("avatar");
                                    userInfoVo.setAvatarUrl(avatar);
                                }
                                if (data.containsKey("mobile")) {
                                    String mobile = (String) data.get("mobile");
                                    userInfoVo.setMobile(mobile);
                                }
                            }
                            tvName.setText(userInfoVo.getUserName());
                            if (!TextUtils.isEmpty(userInfoVo.getAvatarUrl())) {
                                Glide.with(mActivity).load(userInfoVo.getAvatarUrl()).into(ivUser);
                            }
                            UserManager.setUserInfoVo(userInfoVo, mActivity);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void initData() throws Exception {
        initListView();
        initImagePicker();
    }

    private void initView(View view) {
        listView = view.findViewById(R.id.listView);
    }

    @SuppressLint("InflateParams")
    private void initListView() throws Exception {
        itemList.add(new ItemModel(R.mipmap.scan, getString(R.string.scan), null));
        itemList.add(new ItemModel(R.mipmap.about, getString(R.string.about), null));
        itemList.add(new ItemModel(R.mipmap.clean, getString(R.string.setting), DataCleanManager.getTotalCacheSize(mActivity)));
        itemList.add(new ItemModel(R.mipmap.update, getString(R.string.update), "v" + AppUtils.getAppVersionName()));
        itemList.add(new ItemModel(R.mipmap.develop, getString(R.string.zan), null));
        itemList.add(new ItemModel(R.mipmap.exit, getString(R.string.logout), null));
        listAdapter = new MyListAdapter(itemList, mActivity);
        listView.setAdapter(listAdapter);
        View view = LayoutInflater.from(mActivity).inflate(R.layout.my_header, null);
        ImageView iv_code = view.findViewById(R.id.iv_code);
        ivUser = view.findViewById(R.id.iv_user);
        TextView tv_modify = view.findViewById(R.id.tv_modify);
        tvName = view.findViewById(R.id.tv_name);
        ImageView iv_refresh = view.findViewById(R.id.iv_refresh);
        iv_code.setOnClickListener(this);
        tv_modify.setOnClickListener(this);
        iv_refresh.setOnClickListener(this);
        ivUser.setOnClickListener(this);
        listView.addHeaderView(view);
        TextView footer = new TextView(mActivity);
        footer.setHeight(40);
        footer.setGravity(Gravity.CENTER);
        listView.addFooterView(footer);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MeViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_code:
                startActivity(new Intent(mActivity, CodeActivity.class));
                break;
            case R.id.tv_modify:
                ChangePasswordActivity.startActivity(mActivity, token);
                break;
            case R.id.iv_refresh:
                initAction();
                break;
            case R.id.iv_user:
                showBottom();
                break;
        }
    }

    private void showBottom() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mActivity);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(mActivity).inflate(R.layout.me_picture_selector, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        TextView tv_take_photo = view.findViewById(R.id.tv_take_photo);
        TextView tv_select_album = view.findViewById(R.id.tv_select_album);
        TextView tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        tv_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                //拍照
                takePhoto();
            }
        });

        tv_select_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                //相册
                selectAlbum();
            }
        });
    }

    private void selectAlbum() {
        //打开选择,本次允许选择的数量
        ImagePicker.getInstance().setSelectLimit(1);
        Intent intent1 = new Intent(mActivity, ImageGridActivity.class);
        /* 如果需要进入选择的时候显示已经选中的图片，
         * 详情请查看ImagePickerActivity
         * */
        //intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
        startActivityForResult(intent1, REQUEST_CODE_SELECT);
    }

    private void takePhoto() {
        /**
         * 0.4.7 目前直接调起相机不支持裁剪，如果开启裁剪后不会返回图片，请注意，后续版本会解决
         *
         * 但是当前直接依赖的版本已经解决，考虑到版本改动很少，所以这次没有上传到远程仓库
         *
         * 如果实在有所需要，请直接下载源码引用。
         */
        //打开选择,本次允许选择的数量
        ImagePicker.getInstance().setSelectLimit(1);
        Intent intent = new Intent(mActivity, ImageGridActivity.class);
        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
        startActivityForResult(intent, REQUEST_CODE_SELECT);
    }

    private void logout() {
        DialogHelper.getInstance().showSureDialog(mActivity, "温馨提示"
                , "你确定要退出登录吗？退出登录后，当前用户信息将被清空",
                new DialogListener() {
                    @Override
                    public void clickSure() {
                        CleanUtils.cleanInternalSp();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position - 1) {
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
                            if (apkModel != null) {
                                showNotice(apkModel);
                            } else {
                                ToastUtils.showShort("获取更新数据异常");
                            }
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
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
        } else {
            UpdateManager mUpdateManager = new UpdateManager(mActivity);
            mUpdateManager.checkUpdateInfo(apkModel.getDownloadUrl(), apkModel.getContent());
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
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
                    LogUtils.d("images", "onActivityResult:拍照返回 " + images.get(0).path);
                    uploadImage(images.get(0).path);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {
                    LogUtils.d("images", "onActivityResult:相机 " + images.get(0).path);
                    uploadImage(images.get(0).path);
                }
            }
        }// 扫描二维码/条码回传
        else if (requestCode == REQUEST_CODE_SCAN) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                if (content != null) {
                    ToastUtils.showShort(content);
                }
            }
        }
    }

    /**
     * 上传头像
     *
     * @param path 路径
     */
    private void uploadImage(String path) {
        final File file = new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.SERVER_URL);
        Observable<Map> observable = api.uploadFile(token, body);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Map map) {
                        boolean handler = HandlerUtils.isHandler(map, mActivity);
                        if (!handler) {
                            Gson gson = new Gson();
                            String json = gson.toJson(map.get("data"));
                            Type type = new TypeToken<FileVo>() {
                            }.getType();
                            FileVo fileVo = gson.fromJson(json, type);
                            if (fileVo != null) {
                                if (!TextUtils.isEmpty(fileVo.getUrl())) {
                                    Glide.with(mActivity).load(fileVo.getUrl()).into(ivUser);

                                    //更新用户信息
                                    updateUserInfo(fileVo.getUrl());
                                }
                            }
                        }
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

    private void updateUserInfo(String url) {
        LoadingDialogHelper.show(mActivity, "更新用户信息中...");
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.SERVER_URL);
        Observable<Map> observable = api.changeImage(token, url);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Map map) {
                        HandlerUtils.isHandler(map, mActivity);
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
}
