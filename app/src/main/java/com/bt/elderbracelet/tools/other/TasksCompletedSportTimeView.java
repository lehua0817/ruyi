package com.bt.elderbracelet.tools.other;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.bttow.elderbracelet.R;

public class TasksCompletedSportTimeView extends View {
	// 画实心圆的画笔
	private Paint mCirclePaint;
	// 画圆环的画笔
	private Paint mRingPaint;
	private Paint mPaint; //时钟画笔
	// 画字体的画笔
	private Paint mTextPaint;
	// 画圆环背景的画笔 或者说 画实际的弧度(30%,60%....)
	private Paint mRingPaint2;
	// 圆形颜色
	private int mCircleColor;

	private int timeColor;
	// 圆环颜色
	private int mRingColor;
	// 半径
	private float mRadius;
	// 圆环半径
	private float mRingRadius;
	private float r;
	private float bigRadius = 10;
	private float smallRadius = 5;
	// 圆环宽度
	private float mStrokeWidth;
	private float bordWidth;
	private float timeLineB_H;
	private float bordColor;
	// 圆环宽度
	private float timeLineB_W;
	private float timeLineS_W;
	private float timeLineS_H;
	// 圆心x坐标
	private int mXCenter;
	private int circleX;
	private int circleY;
	// 圆心y坐标
	private int mYCenter;
	// 字的长度
	private float mTxtWidth;
	// 字的高度
	private float mTxtHeight;
	// 总进度
	private int mTotalProgress = 60;
	// 当前进度
	private int mProgress;

	public TasksCompletedSportTimeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 获取自定义的属性
		initAttrs(context, attrs);
		initVariable();
	}

	/**
	 * 设置各变量的值
	 * @param context
	 * @param attrs
	 */
	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.TasksCompletedView, 0, 0);
		mRadius = typeArray.getDimension(R.styleable.TasksCompletedView_radius, 80);
		mStrokeWidth = typeArray.getDimension(R.styleable.TasksCompletedView_strokeWidth, 10);
		bordWidth = typeArray.getDimension(R.styleable.TasksCompletedView_strokeWidth, 2);
		timeLineB_H = typeArray.getDimension(R.styleable.TasksCompletedView_strokeWidth, 2);
		bordColor  =  typeArray.getColor(R.styleable.TasksCompletedView_circleColor, 0x000000);
		mCircleColor = typeArray.getColor(R.styleable.TasksCompletedView_circleColor, 0x000000);
		mRingColor = typeArray.getColor(R.styleable.TasksCompletedView_ringColor, 0x000000);
		timeColor = typeArray.getColor(R.styleable.TasksCompletedView_ringColor, 0x000000);
		timeLineB_W = 5;//12点
		timeLineS_H = 18; //小齿轮
		timeLineS_W = 5;//12点
		mRingRadius = mRadius + mStrokeWidth / 2;
		r = mRadius + mStrokeWidth / 2 + 4;
	}

	/**
	 * new一些对象并为其设置相应的属性值
	 */
	private void initVariable() {
		mCirclePaint = new Paint();
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setColor(getResources().getColor(R.color.sleep_circle_data_bg));
		mCirclePaint.setStyle(Paint.Style.FILL);
	//	mCirclePaint.setStyle(Paint.Style.STROKE);// STROKE为空心，fill为实心
	//	mCirclePaint.setStrokeWidth(0);

		/*mRingPaint = new Paint();
		mRingPaint.setAntiAlias(true);
		mRingPaint.setColor(getResources().getColor(R.color.circle_step));
		mRingPaint.setStyle(Paint.Style.STROKE);
		//  mRingPaint.setStyle(Paint.Style.FILL);
		mRingPaint.setStrokeWidth(mStrokeWidth - 1);*/





		mRingPaint2 = new Paint();
		mRingPaint2.setAntiAlias(true);
		mRingPaint2.setColor(getResources().getColor(R.color.circle_step_gra));
		mRingPaint2.setStyle(Paint.Style.STROKE);
		//	mRingPaint2.setStyle(Paint.Style.FILL);
		mRingPaint2.setStrokeWidth(mStrokeWidth +1);

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

		Log.e("TAG", "刷新计步动画效果**************");
		mXCenter = getWidth() / 2;
		mYCenter = getHeight() / 2;

		circleX = getWidth() / 2;
		circleY = getHeight() / 2;

		//    canvas.drawCircle(mXCenter, mYCenter, mRadius, mCirclePaint);

		/*if (mProgress > 0 ) {
			RectF oval = new RectF();
			oval.left = (mXCenter - mRingRadius);
			oval.top = (mYCenter - mRingRadius);
			oval.right = mRingRadius * 2 + (mXCenter - mRingRadius);
			oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
			//    canvas.drawArc(oval, -90, ((float)mProgress / mTotalProgress) * 360, false, mRingPaint);

			int n = 360;
			for (int i = 0; i < n; i += 3) {
				canvas.drawArc(oval, -90 + i, 2, false, mRingPaint);
			}
			canvas.drawArc(oval, -90, ((float)mProgress / mTotalProgress) * 360, false, mRingPaint2);
//                        canvas.drawCircle(mXCenter, mYCenter, mRadius + mStrokeWidth / 2, mRingPaint);

*/
		//画圆
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(getResources().getColor(R.color.sport_time_color));
		mPaint.setStyle(Paint.Style.STROKE);
		//  mRingPaint.setStyle(Paint.Style.FILL);
		mPaint.setStrokeWidth(15); // 设置圆环的宽度
		canvas.drawCircle(getWidth() / 2, getWidth() / 2, r, mPaint);

		//画刻度
		//12
		mPaint.setStrokeWidth(timeLineB_W);
	//	mPaint.setColor(timeColor);
		canvas.drawLine(circleX, bordWidth, circleX, bordWidth + timeLineB_H, mPaint);
	//	canvas.drawCircle(circleX, bordWidth + 35, bigRadius, mCirclePaint);
		//12-1
		mPaint.setStrokeWidth(timeLineS_W);
		for (int i = 1; i < 6; i++) {
			canvas.drawLine((float) (circleX + r * Math.cos(Math.toRadians(90 - i * 6))), (float) (circleX - r * Math.cos(Math.toRadians(i * 6))), (float) (circleX + (r - timeLineS_H) * Math.cos(Math.toRadians(90 - i * 6))), (float) (circleX - (r - timeLineS_H) * Math.cos(Math.toRadians(i * 6))), mPaint);
		}
		//1
		mPaint.setStrokeWidth(timeLineB_W);
		canvas.drawLine((float) (circleX + r * Math.cos(Math.toRadians(60))), (float) (circleX - r * Math.cos(Math.toRadians(30))), (float) (circleX + (r - timeLineB_H) * Math.cos(Math.toRadians(60))), (float) (circleX - (r - timeLineB_H) * Math.cos(Math.toRadians(30))), mPaint);
		//1-2
		mPaint.setStrokeWidth(timeLineS_W);
		for (int i = 1; i < 6; i++) {
			canvas.drawLine((float) (circleX + r * Math.cos(Math.toRadians(90 - 30 - i * 6))), (float) (circleX - r * Math.cos(Math.toRadians(30 + i * 6))), (float) (circleX + (r - timeLineS_H) * Math.cos(Math.toRadians(90 - 30 - i * 6))), (float) (circleX - (r - timeLineS_H) * Math.cos(Math.toRadians(30 + i * 6))), mPaint);
		}
		//2
		mPaint.setStrokeWidth(timeLineB_W);
		canvas.drawLine((float) (circleX + r * Math.cos(Math.toRadians(30))), (float) (circleX - r * Math.cos(Math.toRadians(60))), (float) (circleX + (r - timeLineB_H) * Math.cos(Math.toRadians(30))), (float) (circleX - (r - timeLineB_H) * Math.cos(Math.toRadians(60))), mPaint);
		//2-3
		mPaint.setStrokeWidth(timeLineS_W);
		for (int i = 1; i < 6; i++) {
			canvas.drawLine((float) (circleX + r * Math.cos(Math.toRadians(90 - 60 - i * 6))), (float) (circleX - r * Math.cos(Math.toRadians(60 + i * 6))), (float) (circleX + (r - timeLineS_H) * Math.cos(Math.toRadians(90 - 60 - i * 6))), (float) (circleX - (r - timeLineS_H) * Math.cos(Math.toRadians(60 + i * 6))), mPaint);
		}
		//3
		mPaint.setStrokeWidth(timeLineB_W);
		canvas.drawLine(getWidth() - bordWidth, circleX, getWidth() - bordWidth - timeLineB_H, circleX, mPaint);
		//3-4
		mPaint.setStrokeWidth(timeLineS_W);
		for (int i = 1; i < 6; i++) {
			canvas.drawLine((float) (circleX + r * Math.cos(Math.toRadians(i * 6))), (float) (circleX + r * Math.cos(Math.toRadians(90 - i * 6))), (float) (circleX + (r - timeLineS_H) * Math.cos(Math.toRadians(i * 6))), (float) (circleX + (r - timeLineS_H) * Math.cos(Math.toRadians(90 - i * 6))), mPaint);
		}
		//4
		mPaint.setStrokeWidth(timeLineB_W);
		canvas.drawLine((float) (circleX + r * Math.cos(Math.toRadians(30))), (float) (circleX + r * Math.cos(Math.toRadians(60))), (float) (circleX + (r - timeLineB_H) * Math.cos(Math.toRadians(30))), (float) (circleX + (r - timeLineB_H) * Math.cos(Math.toRadians(60))), mPaint);
		//4-5
		mPaint.setStrokeWidth(timeLineS_W);
		for (int i = 1; i < 6; i++) {
			canvas.drawLine((float) (circleX + r * Math.cos(Math.toRadians(30 + i * 6))), (float) (circleX + r * Math.cos(Math.toRadians(90 - 30 - i * 6))), (float) (circleX + (r - timeLineS_H) * Math.cos(Math.toRadians(30 + i * 6))), (float) (circleX + (r - timeLineS_H) * Math.cos(Math.toRadians(90 - 30 - i * 6))), mPaint);
		}
		//5
		mPaint.setStrokeWidth(timeLineB_W);
		canvas.drawLine((float) (circleX + r * Math.cos(Math.toRadians(60))), (float) (circleX + r * Math.cos(Math.toRadians(30))), (float) (circleX + (r - timeLineB_H) * Math.cos(Math.toRadians(60))), (float) (circleX + (r - timeLineB_H) * Math.cos(Math.toRadians(30))), mPaint);
		//5-6
		mPaint.setStrokeWidth(timeLineS_W);
		for (int i = 1; i < 6; i++) {
			canvas.drawLine((float) (circleX + r * Math.cos(Math.toRadians(60 + i * 6))), (float) (circleX + r * Math.cos(Math.toRadians(90 - 60 - i * 6))), (float) (circleX + (r - timeLineS_H) * Math.cos(Math.toRadians(60 + i * 6))), (float) (circleX + (r - timeLineS_H) * Math.cos(Math.toRadians(90 - 60 - i * 6))), mPaint);
		}
		//6
		mPaint.setStrokeWidth(timeLineB_W);
		canvas.drawLine(circleX, getWidth() - bordWidth, circleX, getWidth() - bordWidth - timeLineB_H, mPaint);

		//6-7
		mPaint.setStrokeWidth(timeLineS_W);
		for (int i = 1; i < 6; i++) {
			canvas.drawLine((float) (circleX - r * Math.cos(Math.toRadians(90 - i * 6))), (float) (circleX + r * Math.cos(Math.toRadians(i * 6))), (float) (circleX - (r - timeLineS_H) * Math.cos(Math.toRadians(90 - i * 6))), (float) (circleX + (r - timeLineS_H) * Math.cos(Math.toRadians(i * 6))), mPaint);
		}
		//7
		mPaint.setStrokeWidth(timeLineB_W);
		canvas.drawLine((float) (circleX - r * Math.cos(Math.toRadians(60))), (float) (circleX + r * Math.cos(Math.toRadians(30))), (float) (circleX - (r - timeLineB_H) * Math.cos(Math.toRadians(60))), (float) (circleX + (r - timeLineB_H) * Math.cos(Math.toRadians(30))), mPaint);
		//7-8
		mPaint.setStrokeWidth(timeLineS_W);
		for (int i = 1; i < 6; i++) {
			canvas.drawLine((float) (circleX - r * Math.cos(Math.toRadians(90 - 30 - i * 6))), (float) (circleX + r * Math.cos(Math.toRadians(30 + i * 6))), (float) (circleX - (r - timeLineS_H) * Math.cos(Math.toRadians(90 - 30 - i * 6))), (float) (circleX + (r - timeLineS_H) * Math.cos(Math.toRadians(30 + i * 6))), mPaint);
		}
		//8
		mPaint.setStrokeWidth(timeLineB_W);
		canvas.drawLine((float) (circleX - r * Math.cos(Math.toRadians(30))), (float) (circleX + r * Math.cos(Math.toRadians(60))), (float) (circleX - (r - timeLineB_H) * Math.cos(Math.toRadians(30))), (float) (circleX + (r - timeLineB_H) * Math.cos(Math.toRadians(60))), mPaint);
		//8-9
		mPaint.setStrokeWidth(timeLineS_W);
		for (int i = 1; i < 6; i++) {
			canvas.drawLine((float) (circleX - r * Math.cos(Math.toRadians(90 - 60 - i * 6))), (float) (circleX + r * Math.cos(Math.toRadians(60 + i * 6))), (float) (circleX - (r - timeLineS_H) * Math.cos(Math.toRadians(90 - 60 - i * 6))), (float) (circleX + (r - timeLineS_H) * Math.cos(Math.toRadians(60 + i * 6))), mPaint);
		}
		//9
		mPaint.setStrokeWidth(timeLineB_W);
		canvas.drawLine(bordWidth, circleX, bordWidth + timeLineB_H, circleX, mPaint);
		//9-10
		mPaint.setStrokeWidth(timeLineS_W);
		for (int i = 1; i < 6; i++) {
			canvas.drawLine((float) (circleX - r * Math.cos(Math.toRadians(i * 6))), (float) (circleX - r * Math.cos(Math.toRadians(90 - i * 6))), (float) (circleX - (r - timeLineS_H) * Math.cos(Math.toRadians(i * 6))), (float) (circleX - (r - timeLineS_H) * Math.cos(Math.toRadians(90 - i * 6))), mPaint);
		}
		//10
		mPaint.setStrokeWidth(timeLineB_W);
		canvas.drawLine((float) (circleX - r * Math.cos(Math.toRadians(30))), (float) (circleX - r * Math.cos(Math.toRadians(60))), (float) (circleX - (r - timeLineB_H) * Math.cos(Math.toRadians(30))), (float) (circleX - (r - timeLineB_H) * Math.cos(Math.toRadians(60))), mPaint);
		//10-11
		mPaint.setStrokeWidth(timeLineS_W);
		for (int i = 1; i < 6; i++) {
			canvas.drawLine((float) (circleX - r * Math.cos(Math.toRadians(30 + i * 6))), (float) (circleX - r * Math.cos(Math.toRadians(90 - 30 - i * 6))), (float) (circleX - (r - timeLineS_H) * Math.cos(Math.toRadians(30 + i * 6))), (float) (circleX - (r - timeLineS_H) * Math.cos(Math.toRadians(90 - 30 - i * 6))), mPaint);
		}
		//11
		mPaint.setStrokeWidth(timeLineB_W);
		canvas.drawLine((float) (circleX - r * Math.cos(Math.toRadians(60))), (float) (circleX - r * Math.cos(Math.toRadians(30))), (float) (circleX - (r - timeLineB_H) * Math.cos(Math.toRadians(60))), (float) (circleX - (r - timeLineB_H) * Math.cos(Math.toRadians(30))), mPaint);
		//11-12
		mPaint.setStrokeWidth(timeLineS_W);
		for (int i = 1; i < 6; i++) {
			canvas.drawLine((float) (circleX - r * Math.cos(Math.toRadians(60 + i * 6))), (float) (circleX - r * Math.cos(Math.toRadians(90 - 60 - i * 6))), (float) (circleX - (r - timeLineS_H) * Math.cos(Math.toRadians(60 + i * 6))), (float) (circleX - (r - timeLineS_H) * Math.cos(Math.toRadians(90 - 60 - i * 6))), mPaint);
		}


		//写时刻数字
		mPaint.setTextSize(26);
		mPaint.setStrokeWidth(2);
		mPaint.setColor(Color.BLACK);
		canvas.drawText(12+"", circleX - timeLineB_W, bordWidth + timeLineB_H + 30, mPaint);
		canvas.drawText(1+"", (float) (circleX + (r - timeLineB_H) * Math.cos(Math.toRadians(60))) - 20, (float) (circleX - (r - timeLineB_H) * Math.cos(Math.toRadians(30))) + 20, mPaint);
		canvas.drawText(2+"", (float) (circleX + (r - timeLineB_H) * Math.cos(Math.toRadians(30))) - 20, (float) (circleX - (r - timeLineB_H) * Math.cos(Math.toRadians(60))) + 20, mPaint);
		canvas.drawText(3+"", getWidth() - bordWidth - timeLineB_H - 20, circleX + 10, mPaint);
		canvas.drawText(4+"", (float) (circleX + (r - timeLineB_H) * Math.cos(Math.toRadians(30))) - 20, (float) (circleX + (r - timeLineB_H) * Math.cos(Math.toRadians(60))), mPaint);
		canvas.drawText(5+"", (float) (circleX + (r - timeLineB_H) * Math.cos(Math.toRadians(60))) - 15, (float) (circleX + (r - timeLineB_H) * Math.cos(Math.toRadians(30))) - 5, mPaint);
		canvas.drawText(6+"", circleX - 10, getWidth() - bordWidth - timeLineB_H - 10, mPaint);
		canvas.drawText(7+"", (float) (circleX - (r - timeLineB_H) * Math.cos(Math.toRadians(60))), (float) (circleX + (r - timeLineB_H) * Math.cos(Math.toRadians(30))) - 10, mPaint);
		canvas.drawText(8+"", (float) (circleX - (r - timeLineB_H) * Math.cos(Math.toRadians(30))) + 10, (float) (circleX + (r - timeLineB_H) * Math.cos(Math.toRadians(60))) + 5, mPaint);
		canvas.drawText(9+"", bordWidth + timeLineB_H + 10, circleX + 10, mPaint);
		canvas.drawText(10+"", (float) (circleX - (r - timeLineB_H) * Math.cos(Math.toRadians(30))) + 10, (float) (circleX - (r - timeLineB_H) * Math.cos(Math.toRadians(60))) + 20, mPaint);
		canvas.drawText(11+"", (float) (circleX - (r - timeLineB_H) * Math.cos(Math.toRadians(60))), (float) (circleX - (r - timeLineB_H) * Math.cos(Math.toRadians(30))) + 25, mPaint);


			//画圆心
		Paint paintCenter = new Paint();
		paintCenter.setColor(getResources().getColor(R.color.sport_time_color));
		canvas.drawCircle(circleX, circleY, 10, paintCenter);

		RectF oval2 = new RectF(circleX - mRadius + 50, circleX - mRadius + 50, circleX
				+ mRadius - 50, circleX + mRadius - 50);
		//RectF oval2 = new RectF(240, 400, 800, 960);// 设置个新的长方形，扫描测量 float left, float top, float right, float bottom
		canvas.drawArc(oval2, -90, ((float)mProgress / mTotalProgress) * 360, true, mCirclePaint);

	}

	public void setProgress(int progress) {
		mProgress = progress;
//                invalidate();
		postInvalidate();
	}

}