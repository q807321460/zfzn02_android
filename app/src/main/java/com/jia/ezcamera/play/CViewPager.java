package com.jia.ezcamera.play;


import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CViewPager extends ViewPager {

	private int m_fourscreenRightViewFlag = 0;
	private int m_onescreenleftViewFlag = 1;
	private int m_curpage = -1;

	private float xLast;// yLast;

	private boolean willIntercept = true;

	private boolean scrollble = true;

	public void setTouchIntercept(boolean value) {
		willIntercept = value;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if (willIntercept) {
					return super.onInterceptTouchEvent(arg0);
		} else {
			return false;
		}
	}

	public CViewPager(Context context) {
		super(context);
	}

	public CViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void setfourscreenRightViewFlag(int page) {
		m_fourscreenRightViewFlag = page;
	}

	public void setonescreenleftViewFlag(int page) {
		m_onescreenleftViewFlag = page;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (willIntercept == false)
			return super.dispatchTouchEvent(ev);
		m_curpage = getCurrentItem();
		if (m_curpage != m_fourscreenRightViewFlag
				&& m_curpage != m_onescreenleftViewFlag) {
			return super.dispatchTouchEvent(ev);
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xLast = ev.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			if (m_curpage == m_fourscreenRightViewFlag) {
				if (curX < xLast) {
					return true;
				}
			} else if (m_curpage == m_onescreenleftViewFlag) {
				if (curX > xLast) {
					return true;
				}

			}
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (!scrollble) {
			return true;
		}
		return super.onTouchEvent(ev);
	}

	public boolean isScrollble() {
		return scrollble;
	}

	public void setScrollble(boolean scrollble) {
		this.scrollble = scrollble;
	}
}
