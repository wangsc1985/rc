package com.wang17.religiouscalendar.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by �����ӷ� on 2015/6/23.
 */
public class UserGridView extends GridView {
    public UserGridView(Context context) {
        super(context);
    }

    public UserGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserGridView(Context context, AttributeSet attrs, int defstyle) {
        super(context, attrs, defstyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandspec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandspec);
    }
}
