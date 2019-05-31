package com.bt.elderbracelet.tools.other;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.bttow.elderbracelet.R;

public class TasksCompletedView extends View {

	private Paint mRingPaint;    //没有数据时 默认显示的画笔
	private Paint mRingPaint2;   //运动数据来了以后，显示的好看的画笔
	// 圆形颜色
	private int mCircleColor;
	// 圆环颜色
	private int mRingColor;
	private float mRadius;   //内部最小圆的半径
	private float mRingRadius;  //加了圆环之后，大圆的半径
	private float mStrokeWidth;  // 圆环宽度，实际上就是画笔的宽度
	private int mXCenter; 	// 圆心x坐标
	private int mYCenter;  // 圆心y坐标

	// 总进度
	private int mTotalProgress = 100;
	// 当前进度
	private int mProgress;


	/**获取自定义的属性
	 * @param context
	 * @param attrs
	 */
	public TasksCompletedView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttrs(context, attrs);
		initVariable();
	}

	/**
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
	 * 初始化圆环的属性
	 */
	private void initVariable() {

		mRingPaint = new Paint();
		mRingPaint.setAntiAlias(true);
		mRingPaint.setColor(getResources().getColor(R.color.circle_step));
		mRingPaint.setStyle(Paint.Style.STROKE);
		mRingPaint.setStrokeWidth(mStrokeWidth - 1 + 5);

		mRingPaint2 = new Paint();
		mRingPaint2.setAntiAlias(true);
		mRingPaint2.setColor(getResources().getColor(R.color.circle_step_gra));
		mRingPaint2.setStyle(Paint.Style.STROKE);
		mRingPaint2.setStrokeWidth(mStrokeWidth +1 + 5);

	}

	/**
	 * @param canvas 当该组件将要绘制它的内容时回调该方法
	 *                  刷新计步动画效果
	 *                 计步当前进度圆弧
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		mXCenter = getWidth() / 2;
		mYCenter = getHeight() / 2;
			RectF oval = new RectF();       //RectF对象
			oval.left = (mXCenter - mRingRadius);
			oval.top = (mYCenter - mRingRadius);
			oval.right = mRingRadius * 2 + (mXCenter - mRingRadius);
			oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);

			int n = 360;
			for (int i = 0; i < n; i += 3) {
				canvas.drawArc(oval, -90 + i, 2, false, mRingPaint);
			}

		if (mProgress > 0 ) {
			canvas.drawArc(oval, -90, ((float)mProgress / mTotalProgress) * 360, false, mRingPaint2);
		}
	}

	/**
	 * @param progress   当前进度
	 */
	public void setProgress(int progress) {
		mProgress = progress;
		postInvalidate();
	}

}