package com.aib.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.aib.adapter.BaseAdapter;
import com.aib.library.R;

import androidx.annotation.Nullable;

import listener.AnimationListener;

public class PanView extends View {
    //转盘大小(因为是圆的，当然宽=高)
    private int panSize = 400;
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
    //文本大小
    private int textColor;
    //文本大小
    private float textSize;
    //扇形个数,默认是2个
    private int count = 2;
    //适配器数据
    private BaseAdapter adapter;

    public PanView(Context context) {
        this(context, null);
    }

    public PanView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PanView, 0, 0);
        firstColor = typedArray.getColor(R.styleable.PanView_firstColor, Color.RED);
        secondColor = typedArray.getColor(R.styleable.PanView_secondColor, Color.BLUE);
        textColor = typedArray.getColor(R.styleable.PanView_textColor, Color.WHITE);
        textSize = typedArray.getDimension(R.styleable.PanView_textSize, 25);

        typedArray.recycle();

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(panSize, panSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mRadius = panSize / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (adapter == null) {
            return;
        }

        //默认指针指向第1个扇形
        canvas.rotate(roundAngle - (90 + sweepAngle / 2), mRadius, mRadius);

        for (int i = 0; i < count; i++) {
            if ((i & 1) == 1) {
                //奇数
                paint.setColor(firstColor);
            } else {
                //偶数
                paint.setColor(secondColor);
            }

            //分块绘制圆盘
            RectF rectF = new RectF(0, 0, panSize, panSize);
            canvas.drawArc(rectF, startAngle, sweepAngle, true, paint);

            //绘制文字
            paint.setColor(textColor);
            paint.setTextSize(textSize);
            paint.setLetterSpacing((float) 0.5);
            Path path = new Path();
            path.addArc(rectF, startAngle, sweepAngle);
            float textWidth = paint.measureText(adapter.getData().get(i));
            float hOffset = (float) (panSize * Math.PI / count / 2 - textWidth / 2);
            float vOffset = panSize / 8;
            canvas.drawTextOnPath(adapter.getData().get(i), path, hOffset, vOffset, paint);

            startAngle += sweepAngle;
        }
    }

    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
        //计算多少个扇形
        count = adapter.getCount();
        //平均每个扇形扫的角度
        sweepAngle = roundAngle / count;
        invalidate();
    }

    /**
     * @param pos 旋转到指定位置
     */
    public void startPosition(final int pos, final AnimationListener listener) {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(v, "rotation", 0, sweepAngle * pos + roundAngle * 5);
                animator.setRepeatMode(ValueAnimator.RESTART);
                animator.setDuration(2000);
                animator.setInterpolator(null);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        listener.onAnimationStart();
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        listener.onAnimationEnd();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
                animator.start();
            }
        });
    }
}
