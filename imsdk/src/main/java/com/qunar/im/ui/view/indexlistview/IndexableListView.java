/*
 * Copyright 2011 woozzu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qunar.im.ui.view.indexlistview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;

public class IndexableListView extends ListView {

	private IndexScroller mScroller = null;

	public IndexableListView(Context context) {
		this(context, null);
	}

	public IndexableListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public IndexableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mScroller = new IndexScroller(getContext(), this);
		mScroller.show();
	}

	public void alwaysShowScroll(boolean enabled) {
		mScroller.isAlwaysShown(enabled);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		// Overlay index bar
		if (mScroller != null)
			mScroller.draw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// Intercept ListView's touch event
		if (mScroller != null && mScroller.onTouchEvent(ev))
			return true;
		return super.onTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(mScroller.contains(ev.getX(), ev.getY()))
			return true;
		
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		if (mScroller != null)
			mScroller.setAdapter(adapter);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mScroller != null)
			mScroller.onSizeChanged(w, h, oldw, oldh);
	}

}
