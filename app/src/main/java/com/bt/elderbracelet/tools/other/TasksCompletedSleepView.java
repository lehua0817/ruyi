package com.bt.elderbracelet.tools.other;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.bttow.elderbracelet.R;

public class TasksCompletedSleepView extends View {
	// 画实心圆的画笔
	private Paint mCirclePaint;
	// 画圆环的画笔
	private Paint mRingPaint;
	// 画字体的画笔
	private Paint mTextPaint;
	// 画圆环背景的画笔 或者说 画实际的弧度(30%,60%....)
	private Paint mRingPaint2;
	// 圆形颜色
	private int mCircleColor;
	// 圆环颜色
	private int mRingColor;
	// 半径
	private float mRadius;
	// 圆环半径
	private float mRingRadius;
	// 圆环宽度
	private float mStrokeWidth;
	// 圆心x坐标
	private int mXCenter;
	// 圆心y坐标
	private int mYCenter;
	// 字的长度
	private float mTxtWidth;
	// 字的高度
	private float mTxtHeight;
	// 总进度
	private int mTotalProgress = 100;
	// 当前进度
	private int mProgress;

	public TasksCompletedSleepView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 获取自定义的属性
		initAttrs(context, attrs);
		initVariable();
	}

	/**
	 *设置个变量的值
	 * @param context
	 * @param attrs
	 */
	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.TasksCompletedView, 0, 0);
		mRadius = typeArray.getDimension(R.styleable.TasksCompletedView_radius, 80);
		mStrokeWidth = typeArray.getDimension(R.styleable.TasksCompletedView_strokeWidth, 10);
		mCircleColor = typeArray.getColor(R.styleable.TasksCompletedView_circleColor, 0xFFFFFFFF);
		mRingColor = typeArray.getColor(R.styleable.TasksCompletedView_ringColor, 0xFFFFFFFF);

		mRingRadius = mRadius + mStrokeWidth / 2;
	}

	/**
	 * new一些对象并为其设置相应的属性值
	 */
	private void initVariable() {
		mCirclePaint = new Paint();
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setColor(mCircleColor);
		mCirclePaint.setStyle(Paint.Style.FILL);
		mCirclePaint.setStyle(Paint.Style.STROKE);// STROKE为空心，fill为实心
		mCirclePaint.setStrokeWidth(0);

		mRingPaint = new Paint();
		mRingPaint.setAntiAlias(true);
		mRingPaint.setColor(getResources().getColor(R.color.sleep_circle_bg));
		mRingPaint.setStyle(Paint.Style.STROKE);
		//  mRingPaint.setStyle(Paint.Style.FILL);
		mRingPaint.setStrokeWidth(mStrokeWidth - 1 + 40);



		mRingPaint2 = new Paint();
		mRingPaint2.setAntiAlias(true);
		mRingPaint2.setColor(getResources().getColor(R.color.sleep_circle_data_bg));
		mRingPaint2.setStyle(Paint.Style.STROKE);
		//	mRingPaint2.setStyle(Paint.Style.FILL);
		mRingPaint2.setStrokeWidth(mStrokeWidth +1 + 40);

		//画字体
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setARGB(255, 255, 255, 255);
		mTextPaint.setTextSize(mRadius / 2);
		Paint.FontMetrics fm = mTextPaint.getFontMetrics();
		mTxtHeight = (int) Math.ceil(fm.descent - fm.ascent);

	}

	@Override
	protected void onDraw(Canvas canvas) {

		mXCenter = getWidth() / 2;
		mYCenter = getHeight() / 2;

		//    canvas.drawCircle(mXCenter, mYCenter, mRadius, mCirclePaint);

			RectF oval = new RectF();
			oval.left = (mXCenter - mRingRadius);
			oval.top = (mYCenter - mRingRadius);
			oval.right = mRingRadius * 2 + (mXCenter - mRingRadius);
			oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
			//    canvas.drawArc(oval, -90, ((float)mProgress / mTotalProgress) * 360, false, mRingPaint);


			int n = 360;
			//for (int i = 0; i < n; i += 3) {
				canvas.drawArc(oval, -90 , 360, false, mRingPaint);
			//}

		if (mProgress > 0 ) {
			canvas.drawArc(oval, -90, ((float)mProgress / mTotalProgress) * 360, false, mRingPaint2);
		//	canvas.drawRoundRect(oval, -90, ((float)mProgress / mTotalProgress) * 360, false, mRingPaint2);
//                        canvas.drawCircle(mXCenter, mYCenter, mRadius + mStrokeWidth / 2, mRingPaint);
           /* String txt = mProgress + "%";
            mTxtWidth = mTextPaint.measureText(txt, 0, txt.length());
            canvas.drawText(txt, mXCenter - mTxtWidth / 2, mYCenter + mTxtHeight / 4, mTextPaint);*/
		}
	}

	public void setProgress(int progress) {
		mProgress = progress;
//                invalidate();
		postInvalidate();
	}

}