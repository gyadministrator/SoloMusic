package com.example.gy.musicgame.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.activity.ChangePasswordActivity;
import com.example.gy.musicgame.activity.CodeActivity;
import com.example.gy.musicgame.activity.SettingActivity;
import com.example.gy.musicgame.api.Api;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.helper.LoadingDialogHelper;
import com.example.gy.musicgame.helper.RetrofitHelper;
import com.example.gy.musicgame.model.BottomBarVo;
import com.example.gy.musicgame.model.FileVo;
import com.example.gy.musicgame.model.UserInfoVo;
import com.example.gy.musicgame.utils.HandlerUtils;
import com.example.gy.musicgame.utils.LogUtils;
import com.example.gy.musicgame.utils.SharedPreferenceUtil;
import com.example.gy.musicgame.utils.UserManager;
import com.example.gy.musicgame.view.BottomBarView;
import com.example.gy.musicgame.view.TitleView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
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

public class MeFragment extends Fragment implements View.OnClickListener {
    private MeViewModel mViewModel;
    private Activity mActivity;
    private CircleImageView ivUser;
    private TextView tvName;
    private static final int REQUEST_CODE_SELECT = 100;
    private ArrayList<ImageItem> images = null;
    private static final int REQUEST_CODE_PREVIEW = 101;
    private String token;
    private TitleView titleView;
    private BottomBarView bottomBarView;

    public static MeFragment newInstance() {
        return new MeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.me_fragment, container, false);
        initView(view);
        initAction();
        return view;
    }

    private void initAction() {
        SharedPreferenceUtil<String> preferenceUtil = new SharedPreferenceUtil<>();
        token = preferenceUtil.getObject(mActivity, Constants.CURRENT_TOKEN);
        getUserInfo(token);
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

    private void initView(View view) {
        ImageView iv_code = view.findViewById(R.id.iv_code);
        ivUser = view.findViewById(R.id.iv_user);
        TextView tv_modify = view.findViewById(R.id.tv_modify);
        tvName = view.findViewById(R.id.tv_name);
        ImageView iv_refresh = view.findViewById(R.id.iv_refresh);
        titleView = view.findViewById(R.id.titleView);
        bottomBarView = view.findViewById(R.id.bottom_bar_view);
        titleView.setRightClickListener(new TitleView.OnRightClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void clickRight(View view) {
                SettingActivity.startActivity(mActivity, token);
            }

            @Override
            public void clickLeft(View view) {

            }
        });
        ivUser.setOnClickListener(this);
        iv_code.setOnClickListener(this);
        tv_modify.setOnClickListener(this);
        iv_refresh.setOnClickListener(this);
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
}
