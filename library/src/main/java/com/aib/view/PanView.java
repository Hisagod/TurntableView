package com.aib.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.aib.adapter.BaseAdapter;
import com.aib.library.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

import listener.AnimationListener;

public class PanView extends View {
    //默认大小
    private int defaultSize = 400;

    //转盘大小
    private int size;

    //开始角度
    private float startAngle = 0;

    //扇形划过角度
    private int sweepAngle;

    //圆的总度数
    private int roundAngle = 360;

    //半径
    private int mRadius;

    //画笔
    private Paint paint;
    //第一种颜色
    private int firstColor;
    //第二种颜色
    private int secondColor;
    private int textColor;
    //文本大小
    private float textSize;
    //单个扇形的角度
    private float singleAngle;
    private float animLast = 0f;

    //扇形个数,默认是2个
    private int count = 2;
    //适配器数据
    private BaseAdapter adapter;

    public PanView(Context context) {
        this(context, null);
    }


    public PanView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TurntableView, 0, 0);
        size = typedArray.getDimensionPixelSize(R.styleable.TurntableView_panSize, defaultSize);
        firstColor = typedArray.getColor(R.styleable.TurntableView_firstColor, Color.RED);
        secondColor = typedArray.getColor(R.styleable.TurntableView_secondColor, Color.BLUE);
        textColor = typedArray.getColor(R.styleable.TurntableView_textColor, Color.WHITE);
        textSize = typedArray.getDimension(R.styleable.TurntableView_textSize, 25);

        typedArray.recycle();

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (adapter == null) {
            return;
        }
        //半径
        mRadius = size / 2;

        //默认指针指向第1个扇形
        //2  180

        //3  210

        //4  225

        //5  236
        canvas.rotate(236, mRadius, mRadius);

        //平均每个扇形扫的角度
        sweepAngle = roundAngle / count;

        for (int i = 0; i < count; i++) {
//            if ((i & 1) == 1) {
//                //奇数
//                paint.setColor(firstColor);
//            } else {
//                //偶数
//                paint.setColor(secondColor);
//            }
            if (i == 0) {
                paint.setColor(Color.RED);
            } else if (i == 1) {
                paint.setColor(Color.GREEN);
            } else {
                paint.setColor(Color.BLUE);
            }

            //分块绘制圆盘
            RectF rectF = new RectF(0, 0, size, size);
            canvas.drawArc(rectF, startAngle, sweepAngle, true, paint);

            //绘制文字
            paint.setColor(textColor);
            paint.setTextSize(textSize);
            paint.setLetterSpacing((float) 0.5);
            Path path = new Path();
            path.addArc(rectF, startAngle, sweepAngle);
            float textWidth = paint.measureText(adapter.getData().get(i));
            float hOffset = (float) (size * Math.PI / count / 2 - textWidth / 2);
            float vOffset = size / 8;
            canvas.drawTextOnPath(adapter.getData().get(i), path, hOffset, vOffset, paint);

            startAngle += sweepAngle;
        }
    }

    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
        //计算多少个扇形
        count = adapter.getCount();
        //计算单个扇形角度
        singleAngle = roundAngle / count;
        invalidate();
    }

    /**
     * @param rotationNum 开始转的时候，转多少圈，再转到固定的位置
     * @param second      转圈动画执行多长时间
     * @param pos
     */
    public void startPosition(int rotationNum, long second, int pos, final AnimationListener listener) {
        Log.e("HLP", "扇形位置：" + pos);

        final float endAngle = roundAngle * rotationNum + pos * singleAngle;
        Log.e("HLP", "旋转角度：" + endAngle);

//        switch (num) {
//
//        }

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "rotation", animLast, endAngle);
        objectAnimator.setDuration(second * 1000);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (listener != null)
                    listener.onAnimationStart();
                setClickable(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener != null)
                    listener.onAnimationEnd();

                animLast = endAngle % 360;
                Log.e("HLP", "当前角度：" + animLast);

                setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.setStartDelay(1000);
        objectAnimator.start();
    }
}
