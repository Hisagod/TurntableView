package com.aib.library;

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

import java.util.List;

import androidx.annotation.Nullable;

public class TurntableView extends View implements View.OnClickListener {
    //默认宽度
    private int defaultWidth = 400;

    //默认高度
    private int defaultHeight = 400;

    //宽度
    private int width;

    //高度
    private int height;

    //多少片扇形
    private int sectorNum;

    //开始角度
    private float startAngle = 0;

    //扇形划过角度
    private int sweepAngle;

    //圆的总度数
    private int roundAngle = 360;

    //半径
    private int mRadius;

    //圆心坐标
    private int mCenter;

    //第几个显示礼品Icon
    private int showGiftIcon;
    //画笔
    private Paint paint;
    //第一种颜色
    private int firstColor;
    //第二种颜色
    private int secondColor;
    private int textColor;
    //文本大小
    private float textSize;
    //设置文本
    private List<String> texts;
    //指定转角度数
    private float angle;

    public TurntableView(Context context) {
        this(context, null);
    }


    public TurntableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setOnClickListener(this);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TurntableView, 0, 0);
        firstColor = typedArray.getColor(R.styleable.TurntableView_firstColor, Color.RED);
        secondColor = typedArray.getColor(R.styleable.TurntableView_secondColor, Color.BLUE);
        textColor = typedArray.getColor(R.styleable.TurntableView_textColor, Color.WHITE);
        textSize = typedArray.getDimension(R.styleable.TurntableView_textSize, 25);
        int getShowGiftIcon = typedArray.getInteger(R.styleable.TurntableView_showGiftIcon, 1);
        showGiftIcon = --getShowGiftIcon;
        typedArray.recycle();

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMod = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMod = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        switch (widthMod) {
            case MeasureSpec.AT_MOST:
                width = defaultWidth;
                break;
            case MeasureSpec.EXACTLY:
                width = widthSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                width = widthSize;
                break;
        }

        switch (heightMod) {
            case MeasureSpec.AT_MOST:
                height = defaultHeight;
                break;
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                height = heightSize;
                break;
        }

        //圆心坐标
        mCenter = width / 2;
        //半径
        mRadius = mCenter;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (texts == null) {
            throw new NullPointerException("显示数据不能为Null");
        } else {
            //平均每个扇形扫的角度
            sweepAngle = roundAngle / sectorNum;

            for (int i = 0; i < sectorNum; i++) {
                if ((i & 1) == 1) {
                    //奇数
                    paint.setColor(firstColor);

                } else {
                    //偶数
                    paint.setColor(secondColor);
                }

                RectF rectF = new RectF(0, 0, width, height);
                canvas.drawArc(rectF, startAngle, sweepAngle, true, paint);

                //绘制文字
                if (i != showGiftIcon) {
                    paint.setColor(textColor);
                    paint.setTextSize(textSize);
                    paint.setLetterSpacing((float) 0.5);
                    Path path = new Path();
                    path.addArc(rectF, startAngle, sweepAngle);
                    float textWidth = paint.measureText(texts.get(i));
                    float hOffset = (float) (width * Math.PI / sectorNum / 2 - textWidth / 2);
                    float vOffset = width / 8;
                    canvas.drawTextOnPath(texts.get(i), path, hOffset, vOffset, paint);
                }

                //绘制Icon
                if (showGiftIcon > sectorNum) {
                    throw new IndexOutOfBoundsException("礼物Icon显示位置不能大于扇形个数");
                } else {
                    if (i == showGiftIcon) {
                        int imageWidth = mRadius / 10;
                        //计算扇形扫过后的角度
                        float angle = (float) Math.toRadians(startAngle + sweepAngle / 2);
                        //计算中心点的坐标，可以控制icon距离圆心的距离
                        int r = mRadius - width / 8;
                        float x = (float) (mCenter + r * Math.cos(angle));
                        float y = (float) (mCenter + r * Math.sin(angle));
                        //设置绘制图片的范围
                        RectF imageRectF = new RectF(x - imageWidth, y - imageWidth, x + imageWidth, y + imageWidth);
                        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_gift), null, imageRectF, null);
                    }
                }

                startAngle += sweepAngle;
            }
        }
    }

    //上次指定角度
    float lastAngle = 0f;

    @Override
    public void onClick(final View v) {
        if (angle == 0F) {
            throw new NullPointerException("转动角度为Null");
        } else {
            //需要转的角度
            final float endAngle = (roundAngle * 6 + angle);

            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(v, "rotation", lastAngle, endAngle);
            objectAnimator.setDuration(5000);
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    setClickable(false);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    lastAngle = endAngle % 360;
                    int position = (int) (endAngle % 360 / 45);
                    Log.e("HLP", position + "");
                    setClickable(true);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            objectAnimator.start();
        }
    }

    /**
     * 设置文本
     *
     * @param texts
     */
    public void setText(List<String> texts) {
        this.texts = texts;
        sectorNum = texts.size();
        invalidate();
    }

    /**
     * 制定转到某个位置
     */
    public void pointPosition(float angle) {
        this.angle = angle;
    }
}
