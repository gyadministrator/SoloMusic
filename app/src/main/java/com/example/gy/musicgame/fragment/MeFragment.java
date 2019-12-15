package com.example.gy.musicgame.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.activity.AlbumLoveActivity;
import com.example.gy.musicgame.activity.ChangePasswordActivity;
import com.example.gy.musicgame.activity.CodeActivity;
import com.example.gy.musicgame.activity.LocalMusicActivity;
import com.example.gy.musicgame.activity.SettingActivity;
import com.example.gy.musicgame.activity.SongAlbumActivity;
import com.example.gy.musicgame.adapter.AlbumItemAdapter;
import com.example.gy.musicgame.api.Api;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.helper.DialogHelper;
import com.example.gy.musicgame.helper.LoadingDialogHelper;
import com.example.gy.musicgame.helper.RetrofitHelper;
import com.example.gy.musicgame.listener.SheetDialogListener;
import com.example.gy.musicgame.model.AlbumVo;
import com.example.gy.musicgame.model.FileVo;
import com.example.gy.musicgame.model.UserInfoVo;
import com.example.gy.musicgame.utils.HandlerUtils;
import com.example.gy.musicgame.utils.LogUtils;
import com.example.gy.musicgame.utils.SharedPreferenceUtil;
import com.example.gy.musicgame.utils.UserManager;
import com.example.gy.musicgame.utils.Utility;
import com.example.gy.musicgame.view.GlideImageLoader;
import com.example.gy.musicgame.view.TitleView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hb.dialog.dialog.ConfirmDialog;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

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

import static android.content.Context.MODE_PRIVATE;

public class MeFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, OnRefreshListener, OnLoadMoreListener {
    private MeViewModel mViewModel;
    private Activity mActivity;
    private CircleImageView ivUser;
    private TextView tvName;
    private static final int REQUEST_CODE_SELECT = 100;
    private ArrayList<ImageItem> images = null;
    private static final int REQUEST_CODE_PREVIEW = 101;
    private String token;
    private TitleView titleView;
    private SharedPreferences preferences;
    private LinearLayout llLocalMusic;
    private TextView tvLocalMusic;
    private ImageView ivAdd;
    private TextView tvNoData;
    private ListView listView;
    private int currentPage = 1;
    private int PageSize = 20;
    private List<AlbumVo> albumVoList = new ArrayList<>();
    private AlbumItemAdapter itemAdapter;
    private boolean isAlbum = false;
    private String albumPath;
    private ImageView albumIcon;
    private TextView tvAlbumTitle;
    private TextView tvLoveNum;
    private LinearLayout llLove;
    private boolean isLoad = false;
    private SmartRefreshLayout refreshLayout;
    private int albumSize = 0;

    public static MeFragment newInstance() {
        return new MeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.me_fragment, container, false);
        initView(view);
        initData();
        initAction();
        return view;
    }

    private void initData() {
        initImagePicker();
        preferences = mActivity.getSharedPreferences("myFragment", MODE_PRIVATE);
        int localMusicSize = preferences.getInt("localMusicSize", 0);
        tvLocalMusic.setText(String.valueOf(localMusicSize));
        int loveAlbum = preferences.getInt("loveAlbum", 0);
        tvLoveNum.setText(String.valueOf(loveAlbum));
        setUserInfo();
    }

    private void setUserInfo() {
        UserInfoVo userInfoVo = UserManager.getUserInfoVo(mActivity);
        if (userInfoVo != null) {
            Glide.with(mActivity).load(userInfoVo.getAvatarUrl()).into(ivUser);
            tvName.setText(userInfoVo.getNickName());
        }
    }

    private void initAction() {
        SharedPreferenceUtil preferenceUtil = new SharedPreferenceUtil();
        token = preferenceUtil.getObject(mActivity, Constants.CURRENT_TOKEN);
        getAlbumList(token);
    }

    private void getAlbumList(String token) {
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.SERVER_URL);
        Observable<Map> observable = api.albumList(token, currentPage, PageSize);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onNext(Map map) {
                        refreshLayout.finishLoadMore(1500);
                        refreshLayout.finishRefresh(1500);
                        boolean handler = HandlerUtils.isHandler(map, mActivity);
                        if (!handler) {
                            Gson gson = new Gson();
                            String json = gson.toJson(map.get("data"));
                            Type type = new TypeToken<List<AlbumVo>>() {
                            }.getType();
                            albumVoList = gson.fromJson(json, type);
                            if (albumVoList != null && albumVoList.size() > 0) {
                                albumSize = albumVoList.size();
                                tvAlbumTitle.setText("我的歌单(" + albumSize + ")");
                                if (isLoad) {
                                    albumSize += albumVoList.size();
                                    itemAdapter.addLoad(albumVoList);
                                } else {
                                    setAlbumData(albumVoList);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        refreshLayout.finishLoadMore(false);
                        refreshLayout.finishRefresh(false);
                        ToastUtils.showShort(Objects.requireNonNull(e.getMessage()));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void setAlbumData(List<AlbumVo> albumVoList) {
        if (albumVoList == null || albumVoList.size() == 0) {
            tvNoData.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return;
        }
        //设置adapter
        tvNoData.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        itemAdapter = new AlbumItemAdapter(albumVoList, mActivity);
        listView.setAdapter(itemAdapter);
        //重新计算ListView的高度
        Utility.setListViewHeightBasedOnChildren(listView);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
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
                            Gson gson = new Gson();
                            String json = gson.toJson(map.get("data"));
                            Type type = new TypeToken<UserInfoVo>() {
                            }.getType();
                            UserInfoVo userInfoVo = gson.fromJson(json, type);
                            UserManager.setUserInfoVo(json, mActivity);
                            if (userInfoVo != null) {
                                tvName.setText(userInfoVo.getNickName());
                            }
                            if (userInfoVo != null && !TextUtils.isEmpty(userInfoVo.getAvatarUrl())) {
                                Glide.with(mActivity).load(userInfoVo.getAvatarUrl()).into(ivUser);
                            }
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

    private void initView(View view) {
        ImageView iv_code = view.findViewById(R.id.iv_code);
        ivUser = view.findViewById(R.id.iv_user);
        TextView tv_modify = view.findViewById(R.id.tv_modify);
        tvName = view.findViewById(R.id.tv_name);
        tvLoveNum = view.findViewById(R.id.tv_love_num);
        llLove = view.findViewById(R.id.ll_love);
        tvAlbumTitle = view.findViewById(R.id.tv_album_title);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        ImageView iv_refresh = view.findViewById(R.id.iv_refresh);
        titleView = view.findViewById(R.id.titleView);
        llLocalMusic = view.findViewById(R.id.ll_local_music);
        tvLocalMusic = view.findViewById(R.id.tv_local_music);
        tvNoData = view.findViewById(R.id.tv_no_data);
        listView = view.findViewById(R.id.listView);
        ivAdd = view.findViewById(R.id.iv_add);
        ivAdd.setOnClickListener(this);
        llLocalMusic.setOnClickListener(this);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
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
        llLove.setOnClickListener(this);
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
                getUserInfo(token);
                break;
            case R.id.iv_user:
                showBottom();
                break;
            case R.id.ll_local_music:
                //本地音乐
                startActivity(new Intent(mActivity, LocalMusicActivity.class));
                break;
            case R.id.iv_add:
                addAlbum();
                break;
            case R.id.ll_love:
                AlbumLoveActivity.startActivity(mActivity, token);
                break;
        }
    }

    private void showBottom() {
        List<String> items = new ArrayList<>();
        items.add("拍照");
        items.add("相册");
        DialogHelper.getInstance().showBottomDialog(mActivity, items, new SheetDialogListener() {
            @Override
            public void selectPosition(int position) {
                switch (position) {
                    case 0:
                        //拍照
                        takePhoto();
                        break;
                    case 1:
                        //相册
                        selectAlbum();
                        break;
                }
            }
        });
    }

    private void addAlbum() {
        isAlbum = true;
        ConfirmDialog confirmDialog = new ConfirmDialog(mActivity);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(mActivity).inflate(R.layout.add_album, null);
        EditText etAlbum = view.findViewById(R.id.et_album);
        albumIcon = view.findViewById(R.id.iv_icon);
        TextView tvSave = view.findViewById(R.id.tv_save);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        confirmDialog.setContentView(view);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog.dismiss();
            }
        });
        albumIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottom();
            }
        });

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etAlbum.getText().toString())) {
                    ToastUtils.showShort("请输入内容");
                } else {
                    confirmDialog.dismiss();
                    albumSave(etAlbum.getText().toString());
                }
            }
        });
        confirmDialog.setCanceledOnTouchOutside(false);
        confirmDialog.setCancelable(false);
        confirmDialog.show();
    }

    private void albumSave(String result) {
        LoadingDialogHelper.show(mActivity, "保存歌单中...");
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.SERVER_URL);
        Observable<Map> observable = api.albumAdd(token, result, albumPath);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Map map) {
                        HandlerUtils.isHandler(map, mActivity);
                        if (map.containsKey("errno")) {
                            double code = (double) map.get("errno");
                            if (code == 0) {
                                albumPath = "";
                                getAlbumList(token);
                            }
                        }
                    }

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
                                    if (isAlbum) {
                                        Glide.with(mActivity).load(fileVo.getUrl()).into(albumIcon);
                                        albumPath = fileVo.getUrl();
                                    } else {
                                        Glide.with(mActivity).load(fileVo.getUrl()).into(ivUser);
                                        //更新用户信息
                                        updateUserInfo(fileVo.getUrl());
                                    }
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (albumVoList != null && albumVoList.size() > 0) {
            AlbumVo albumVo = albumVoList.get(i);
            SongAlbumActivity.startActivity(mActivity, token, albumVo.getId(), albumVo.getAlbum());
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (albumVoList != null && albumVoList.size() > 0) {
            AlbumVo albumVo = albumVoList.get(i);
            deleteAlbum(albumVo.getId());
        }
        return true;
    }

    private void deleteAlbum(Integer id) {
        List<String> items = new ArrayList<>();
        items.add("删除歌单");
        DialogHelper.getInstance().showBottomDialog(mActivity, items, new SheetDialogListener() {
            @Override
            public void selectPosition(int position) {
                if (position == 0) {
                    deleteAlbumById(id);
                }
            }
        });
    }

    private void deleteAlbumById(Integer id) {
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.SERVER_URL);
        Observable<Map> observable = api.albumDelete(token, id);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Map map) {
                        HandlerUtils.isHandler(map, mActivity);
                        if (map.containsKey("errno")) {
                            double code = (double) map.get("errno");
                            if (code == 0) {
                                getAlbumList(token);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showShort(Objects.requireNonNull(e.getMessage()));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        isLoad = false;
        currentPage = 1;
        albumVoList.clear();
        getAlbumList(token);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        isLoad = true;
        currentPage += 1;
        getAlbumList(token);
    }
}
