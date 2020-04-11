package com.aib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aib.adapter.BaseAdapter;
import com.aib.library.R;

public class TurntableView extends ViewGroup {

    private PanView panView;

    public TurntableView(Context context) {
        this(context, null);
    }

    public TurntableView(Context context, AttributeSet attrs) {
        super(context, attrs);

        panView = new PanView(context, attrs);
        addView(panView);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TurntableView);
        //指针图片
        int pointView = typedArray.getResourceId(R.styleable.TurntableView_pointerIcon, R.drawable.ic_point);
        ImageView ivPoint = new ImageView(context, attrs);
        ivPoint.setImageResource(pointView);
        addView(ivPoint);
        typedArray.recycle();
    }

    public void setAdapter(BaseAdapter adapter) {
        panView.setAdapter(adapter);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //测量子控件
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            int maxWidth = 0;
            int maxHeight = 0;
            for (int i = 0; i < getChildCount(); i++) {
                View childAt = getChildAt(i);
                int measuredWidth = childAt.getMeasuredWidth();
                int measuredHeight = childAt.getMeasuredHeight();
                if (measuredWidth > maxWidth) {
                    maxWidth = measuredWidth;
                }
                if (measuredHeight > maxHeight) {
                    maxHeight = measuredHeight;
                }
            }
            setMeasuredDimension(maxWidth, maxHeight);
        } else if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(widthSize, heightSize);
        } else if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.AT_MOST) {
            int maxHeight = 0;
            for (int i = 0; i < getChildCount(); i++) {
                View childAt = getChildAt(i);
                int measuredHeight = childAt.getMeasuredHeight();

                if (measuredHeight > maxHeight) {
                    maxHeight = measuredHeight;
                }
            }
            setMeasuredDimension(widthSize, maxHeight);
        } else if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.EXACTLY) {
            int maxWidth = 0;
            for (int i = 0; i < getChildCount(); i++) {
                View childAt = getChildAt(i);
                int measuredWidth = childAt.getMeasuredWidth();
                if (measuredWidth > maxWidth) {
                    maxWidth = measuredWidth;
                }
            }
            setMeasuredDimension(maxWidth, heightSize);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int parentWidth = getMeasuredWidth();
        int parentHeight = getMeasuredHeight();
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            int childWidth = childAt.getMeasuredWidth();
            int childHeight = childAt.getMeasuredHeight();

            if (parentWidth > childWidth && parentHeight > childHeight) {
                int left = parentWidth / 2 - childWidth / 2;
                int top = parentHeight / 2 - childHeight / 2;
                childAt.layout(left, top, childWidth + left, childHeight + top);
            } else if (parentWidth == childWidth && parentHeight == childHeight) {
                childAt.layout(0, 0, childWidth, childHeight);
            } else if (parentWidth > childWidth && parentHeight == childHeight) {
                int left = parentWidth / 2 - childWidth / 2;
                childAt.layout(left, 0, left + childWidth, childHeight);
            } else if (parentHeight > childHeight && parentWidth == childWidth) {
                int top = parentHeight - childHeight;
                childAt.layout(0, top, childWidth, top + childHeight);
            }
        }
    }
}
