package com.example.gy.musicgame.chatui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

/**
 * 作者：Rance on 2016/11/29 10:47
 * 邮箱：rance935@163.com
 */
public class EmotionPagerAdapter extends PagerAdapter {

	private List<GridView> gvs;

	public EmotionPagerAdapter(List<GridView> gvs) {
		this.gvs = gvs;
	}

	@Override
	public int getCount() {
		return gvs.size();
	}

	@Override
	public boolean isViewFromObject(@NonNull View arg0, @NonNull Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
		container.removeView(gvs.get(position));
	}

	@NonNull
	@Override
	public Object instantiateItem(@NonNull ViewGroup container, int position) {
		container.addView(gvs.get(position));
		return gvs.get(position);
	}

}
