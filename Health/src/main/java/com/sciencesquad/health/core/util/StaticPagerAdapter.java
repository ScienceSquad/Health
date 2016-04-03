package com.sciencesquad.health.core.util;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class StaticPagerAdapter extends PagerAdapter {
	private int _resIds[];

	public static void install(ViewPager pager) {
		pager.setAdapter(new StaticPagerAdapter(pager));
	}

	public StaticPagerAdapter(int resIds[]) {
		_resIds = resIds;
	}

	public StaticPagerAdapter(ViewPager pager) {
		int count = 0;
		for(int i = 0; i < pager.getChildCount(); i++) {
			View next = pager.getChildAt(i);
			if(next.getId() != View.NO_ID)
				count++;
		}

		_resIds = new int[count];
		for(int i = 0, j = 0; i < pager.getChildCount() && j < count; i++) {
			View next = pager.getChildAt(i);
			if(next.getId() != View.NO_ID)
				_resIds[j++] = next.getId();
		}

		pager.setOffscreenPageLimit(count);
	}

	public Object instantiateItem(ViewGroup container, int position) {
		return container.findViewById(_resIds[position]);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {

	}

	@Override
	public int getCount() {
		return _resIds.length;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
}