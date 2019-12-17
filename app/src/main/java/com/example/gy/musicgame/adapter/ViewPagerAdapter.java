package com.example.gy.musicgame.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
	List<Fragment> fragmentList;
	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}
	public ViewPagerAdapter(FragmentManager fm,List<Fragment> listFragment) {
		super(fm);
		this.fragmentList=listFragment;
		
	}
	
	@NonNull
	@Override
	public Fragment getItem(int arg0) {
		return fragmentList.get(arg0);
	}

	@Override
	public int getCount() {
		return fragmentList.size();
	}

}
