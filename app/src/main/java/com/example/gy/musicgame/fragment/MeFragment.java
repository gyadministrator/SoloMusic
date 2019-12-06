package com.example.gy.musicgame.fragment;

import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.adapter.MyListAdapter;
import com.example.gy.musicgame.model.ItemModel;
import com.example.gy.musicgame.utils.DataCleanManager;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MeFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private MeViewModel mViewModel;
    private Activity mActivity;
    private ListView listView;
    private MyListAdapter listAdapter;
    private CircleImageView ivUser;
    private TextView tvName;
    private List<ItemModel> itemList=new ArrayList<>();

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

    }

    private void initData() throws Exception {
        initListView();
    }

    private void initView(View view) {
        listView=view.findViewById(R.id.listView);
    }

    @SuppressLint("InflateParams")
    private void initListView() throws Exception {
        itemList.add(new ItemModel(R.mipmap.logo, getString(R.string.scan), null));
        itemList.add(new ItemModel(R.mipmap.logo, getString(R.string.collect), null));
        itemList.add(new ItemModel(R.mipmap.logo, getString(R.string.about), null));
        itemList.add(new ItemModel(R.mipmap.logo, getString(R.string.setting), DataCleanManager.getTotalCacheSize(mActivity)));
        itemList.add(new ItemModel(R.mipmap.logo, getString(R.string.update), "v" + AppUtils.getAppVersionName()));
        itemList.add(new ItemModel(R.mipmap.logo, getString(R.string.zan), null));
        itemList.add(new ItemModel(R.mipmap.logo, getString(R.string.logout), null));
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
        switch (v.getId()){
            case R.id.iv_code:
                break;
            case R.id.tv_modify:
                break;
            case R.id.iv_refresh:
                break;
            case R.id.iv_user:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
