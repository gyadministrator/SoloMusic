package com.android.recipe.chatui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * 作者：Rance on 2016/11/25 16:36
 * 邮箱：rance935@163.com
 */
public class CommonFragmentPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> list;

    public CommonFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
        super(fm);
        this.list = list;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }
}
