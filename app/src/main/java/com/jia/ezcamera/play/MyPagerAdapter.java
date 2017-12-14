package com.jia.ezcamera.play;


import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class MyPagerAdapter extends PagerAdapter {
	List<View> viewList;

	public MyPagerAdapter(List<View> viewList) {
		this.viewList = viewList;
		Log.i("info", "list.size=" + viewList.size());
	}

	@Override
	public int getCount() {
		return viewList.size();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		Log.i("info", "------remove");
		container.removeView(viewList.get(position));
	}

	// @Override
	// public Object instantiateItem(ViewGroup container, int position) {
	// Log.i("info", "------add");
	// container.addView(viewList.get(position));
	// return viewList.get(position); // ���ص�ǰҪ��ʾ��view
	// }
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		container.addView(viewList.get(position));
		return viewList.get(position);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1; // ������д����ʾ���� view
	}

}
