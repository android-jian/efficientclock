package com.jian.android.efficientclock;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by M_fly on 2017/12/14.
 */

public class EfficientClock extends View{

    private static final int DEFAULT_OUT_RADIUS=100;
    private static final int DEFAULT_INNER_RADIUS=60;
    private static final int DEFAULT_DEVIDER_LINE=10;

    private int mOutRadius;
    private int mInnerRadius;
    private int mDeviderLine;
    private Paint mRingPaint;
    private RectF mRect;
    private Paint mLinePaint;
    private Paint mTextPaint;
    private Paint.FontMetrics fontMetrics;
    private Paint mDownTextPaint;
    private int currentValue;

    public EfficientClock(Context context) {
        this(context,null);
    }

    public EfficientClock(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public EfficientClock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttrs(context,attrs);
        initPaint();
    }

    private void initAttrs(Context context,AttributeSet attrs){
        final TypedArray attributes=context.obtainStyledAttributes(attrs,R.styleable.EfficientClock);
        mOutRadius= (int) attributes.getDimension(R.styleable.EfficientClock_outRadius,DEFAULT_OUT_RADIUS);
        mInnerRadius= (int) attributes.getDimension(R.styleable.EfficientClock_innerRadius,DEFAULT_INNER_RADIUS);
        mDeviderLine= (int) attributes.getDimension(R.styleable.EfficientClock_dividerLine,DEFAULT_DEVIDER_LINE);

        attributes.recycle();
    }

    private void initPaint(){
        mRingPaint = new Paint();
        mRingPaint.setStyle(Paint.Style.FILL);
        mRingPaint.setAntiAlias(true);

        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(Color.WHITE);
        mLinePaint.setStrokeWidth(4);

        mTextPaint = new Paint();
        mTextPaint.setTextSize(40);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        fontMetrics = mTextPaint.getFontMetrics();

        mDownTextPaint = new Paint();
        mDownTextPaint.setTextSize(80);
        mDownTextPaint.setColor(Color.parseColor("#ff9933"));
        mDownTextPaint.setAntiAlias(true);
        mDownTextPaint.setTextAlign(Paint.Align.CENTER);
        mDownTextPaint.setStrokeWidth(10);
        mDownTextPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode=MeasureSpec.getMode(widthMeasureSpec);
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
        int heightMode=MeasureSpec.getMode(heightMeasureSpec);
        int heightSize=MeasureSpec.getSize(heightMeasureSpec);

        int mWidth,mHeight;
        if (widthMode==MeasureSpec.EXACTLY){
            mWidth=widthSize;
        }else {
            mWidth=getPaddingLeft()+mOutRadius*2+getPaddingRight();
            if (widthMode==MeasureSpec.AT_MOST){
                mWidth=Math.min(mWidth,widthSize);
            }
        }

        if (heightMode==MeasureSpec.EXACTLY){
            mHeight=heightSize;
        }else {
            mHeight=getPaddingTop()+mOutRadius*2+getPaddingBottom();
            if (heightMode==MeasureSpec.AT_MOST){
                mHeight=Math.max(mHeight,heightSize);
            }
        }
        setMeasuredDimension(mWidth,mHeight);
        mOutRadius=Math.min((getMeasuredWidth()-getPaddingLeft()-getPaddingRight()),getMeasuredHeight()-getPaddingTop()-getPaddingBottom())/2;
        mRect = new RectF(-mOutRadius,-mOutRadius,mOutRadius,mOutRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(getMeasuredWidth()/2,getMeasuredHeight()/2);
        mRingPaint.setColor(Color.RED);
        canvas.drawArc(mRect,120,120,true,mRingPaint);
        mRingPaint.setColor(Color.parseColor("#ff9933"));
        canvas.drawArc(mRect,240,60,true,mRingPaint);
        mRingPaint.setColor(Color.YELLOW);
        canvas.drawArc(mRect,300,60,true,mRingPaint);
        mRingPaint.setColor(Color.GREEN);
        canvas.drawArc(mRect,0,60,true,mRingPaint);
        mRingPaint.setColor(Color.WHITE);
        canvas.drawCircle(0,0,mOutRadius*2/3,mRingPaint);

        canvas.rotate(120);
        for (int i=0; i<51;i++){
            if (i%5==0){
                canvas.drawLine(mOutRadius,0,mOutRadius*2/3,0,mLinePaint);
                canvas.save();
                canvas.translate(mOutRadius*2/3-mTextPaint.measureText(String.valueOf(i*2)),0);
                canvas.rotate(240-i*6);
                canvas.drawText(String.valueOf(i*2),0,String.valueOf(i*2).length(),0,(fontMetrics.bottom-fontMetrics.top)/2-fontMetrics.bottom,mTextPaint);
                canvas.restore();
            }else {
                canvas.drawLine(mOutRadius,0,mOutRadius-30,0,mLinePaint);
            }
            canvas.rotate(6);
        }

        if (currentValue<=40){
            mDownTextPaint.setColor(Color.RED);
        }else if (currentValue<=60){
            mDownTextPaint.setColor(Color.parseColor("#ff9933"));
        }else if (currentValue<=80){
            mDownTextPaint.setColor(Color.YELLOW);
        }else {
            mDownTextPaint.setColor(Color.GREEN);
        }

        canvas.rotate(-66);
        canvas.drawText(currentValue+"%",0,mOutRadius*4/5,mDownTextPaint);

        canvas.rotate(120+currentValue*3);
        canvas.drawLine(0,0,160,0,mDownTextPaint);
    }

    public void setCurrentData(int data){

        ValueAnimator animator=ValueAnimator.ofInt(0,data);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentValue = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(2500);
        animator.start();
    }
}
