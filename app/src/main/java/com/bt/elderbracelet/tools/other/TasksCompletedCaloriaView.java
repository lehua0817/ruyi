package com.bt.elderbracelet.tools.other;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.bttow.elderbracelet.R;

public class TasksCompletedCaloriaView extends View {
	// 画实心圆的画笔
	private Paint mCirclePaint;
	// 画圆环的画笔
	private Paint mRingPaint;
	private Paint mPaint; //时钟画笔
	private Paint mPaint_current_caloria; //时钟画笔
	// 画字体的画笔
	private Paint mTextPaint;
	// 画圆环背景的画笔 或者说 画实际的弧度(30%,60%....)
	private Paint mRingPaint2;
	// 圆形颜色
	private int mCircleColor;
    //时间颜色
	private int timeColor;
	// 圆环颜色
	private int mRingColor;
	// 半径
	private float mRadius;
	// 圆环半径
	private float mRingRadius;
	//圆的半径
	private float r;
	// 圆环宽度
	private float mStrokeWidth;
	private float bordWidth;
	private float timeLineB_H;
	//背景颜色
	private float bordColor;
	// 时间条长宽
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
	private int mTotalProgress = 100;
	// 当前进度
	private int mProgress;

	public TasksCompletedCaloriaView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 获取自定义的属性
		initAttrs(context, attrs);
		initVariable();
	}


	/**
	 * 初始化各变量的值
	 * @param context
	 * @param attrs
	 */
	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.TasksCompletedView, 0, 0);
		mRadius = typeArray.getDimension(R.styleable.TasksCompletedView_radius, 80);
		mStrokeWidth = typeArray.getDimension(R.styleable.TasksCompletedView_strokeWidth, 10);
		bordWidth = typeArray.getDimension(R.styleable.TasksCompletedView_strokeWidth, 10);
		timeLineB_H = typeArray.getDimension(R.styleable.TasksCompletedView_strokeWidth, 2);
		bordColor  =  typeArray.getColor(R.styleable.TasksCompletedView_circleColor, 0xFFFFFFFF);
		mCircleColor = typeArray.getColor(R.styleable.TasksCompletedView_circleColor, 0xFFFFFFFF);
		mRingColor = typeArray.getColor(R.styleable.TasksCompletedView_ringColor, 0xFFFFFFFF);
		timeColor = typeArray.getColor(R.styleable.TasksCompletedView_ringColor, 0xFFFFFFFF);
		timeLineB_W = 1;
		timeLineS_H = 1;
		timeLineS_W = 1;
		mRingRadius = mRadius + mStrokeWidth / 2;
		r = mRadius + mStrokeWidth / 2;
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
		mRingPaint.setColor(getResources().getColor(R.color.sport_time_color));
		mRingPaint.setStyle(Paint.Style.STROKE);
		//  mRingPaint.setStyle(Paint.Style.FILL);
		mRingPaint.setStrokeWidth(mStrokeWidth - 1);



		mPaint = new Paint();

		mPaint.setAntiAlias(true);
		mPaint.setColor(getResources().getColor(R.color.gray_color));
		mPaint.setStyle(Paint.Style.STROKE);
		//  mRingPaint.setStyle(Paint.Style.FILL);
		mPaint.setStrokeWidth(12); // 设置圆环的宽度

		mPaint_current_caloria = new Paint();
		mPaint_current_caloria.setAntiAlias(true);
		mPaint_current_caloria.setColor(getResources().getColor(R.color.caloria_color));
		mPaint_current_caloria.setStyle(Paint.Style.STROKE);
		//  mRingPaint.setStyle(Paint.Style.FILL);
		mPaint_current_caloria.setStrokeWidth(12); // 设置圆环的宽度

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
	float data = 0 ;//宽度分为5段
	float current_progress = 0;

	/**
	 * 刷新动画记步效果
	 * @param canvas
	 */
	@Override
	protected void onDraw(Canvas canvas) {

		Log.e("TAG", "刷新计步动画效果**************");
		mXCenter = getWidth() / 2;
		mYCenter = getHeight() / 2;

		circleX = getWidth() / 2;
		circleY = getHeight() / 2;
		Log.e("TAG","getWidth***********"+getWidth() + "getHeight()========"+getHeight()+"current_progress"+current_progress);
		if(mProgress > 0 ){
			current_progress = ((float)mProgress / mTotalProgress) * getWidth();//总长度 1440
			data = getWidth() / 5;
		}
		//画圆心
		Paint paintCenter = new Paint();
		paintCenter.setColor(getResources().getColor(R.color.sport_time_color));
		paintCenter.setStyle(Paint.Style.STROKE);
		paintCenter.setStrokeWidth(5);
		canvas.drawLine(0, getHeight() / 2, getWidth() / 5, getHeight() / 2, mPaint);// 画线 想象成X Y轴坐标轴,如果是一条直线

		canvas.drawCircle(getWidth() / 5 + 15 , getHeight() / 2, 15, paintCenter);//倒数第三个参数是半径

		canvas.drawLine(getWidth() / 5 + 30, getHeight() / 2, (getWidth() / 5)*2 + 30, getHeight() / 2, mPaint);// 画线 想象成X Y轴坐标轴,如果是一条直线

		canvas.drawCircle((getWidth() / 5)*2 + 30 + 15 , getHeight() / 2, 15, paintCenter);//倒数第三个参数是半径

		canvas.drawLine((getWidth() / 5 + 30)*2, getHeight() / 2, (getWidth() / 5)*3 + 30*2, getHeight() / 2, mPaint);

		canvas.drawCircle((getWidth() / 5)*3 + 30*2 + 15 , getHeight() / 2, 15, paintCenter);//倒数第三个参数是半径

		canvas.drawLine((getWidth() / 5 + 30)*3, getHeight() / 2, (getWidth() / 5)*4 + 30*3, getHeight() / 2, mPaint);

		canvas.drawCircle((getWidth() / 5)*4 + 30*3+ 15 , getHeight() / 2, 15, paintCenter);//倒数第三个参数是半径

		canvas.drawLine((getWidth() / 5 + 30)*4, getHeight() / 2, (getWidth() / 5)*5 + 30*4, getHeight() / 2, mPaint);




		//画圆心
		Paint paint = new Paint();
		paint.setColor(getResources().getColor(R.color.caloria_color));
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		Log.e("TAG","current_progress***************************"+current_progress);
		if(current_progress == 0.0) return;
		if(current_progress < data){

			canvas.drawLine(0, getHeight() / 2, current_progress, getHeight() / 2, mPaint_current_caloria);// 画线 想象成X Y轴坐标轴,如果是一条直线
			Bitmap rawBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.distance_sport_icon);
			canvas.drawBitmap(rawBitmap,current_progress - 30  ,getHeight() / 10 -15 ,mPaint_current_caloria);

		}else if(current_progress >= data && current_progress < data*2){
			canvas.drawLine(0, getHeight() / 2, getWidth() / 5, getHeight() / 2, mPaint_current_caloria);// 画线 想象成X Y轴坐标轴,如果是一条直线
			canvas.drawCircle(getWidth() / 5 + 15 , getHeight() / 2, 15, paint);//倒数第三个参数是半径
			canvas.drawLine(getWidth() / 5 + 30, getHeight() / 2, current_progress, getHeight() / 2, mPaint_current_caloria);
			Bitmap rawBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.distance_sport_icon);
			canvas.drawBitmap(rawBitmap,current_progress - 30,getHeight() / 10  -15,mPaint_current_caloria);


		}else if(current_progress >= data*2 && current_progress < data*3){

			canvas.drawLine(0, getHeight() / 2, getWidth() / 5, getHeight() / 2, mPaint_current_caloria);// 画线 想象成X Y轴坐标轴,如果是一条直线
			canvas.drawCircle(getWidth() / 5 + 15 , getHeight() / 2, 15, paint);//倒数第三个参数是半径
			canvas.drawLine(getWidth() / 5 + 30, getHeight() / 2, (getWidth() / 5)*2 + 30, getHeight() / 2, mPaint_current_caloria);
			canvas.drawCircle((getWidth() / 5)*2 + 30 + 15 , getHeight() / 2, 15, paint);//倒数第三个参数是半径

			canvas.drawLine((getWidth() / 5 + 30)*2, getHeight() / 2, current_progress, getHeight() / 2, mPaint_current_caloria);
			Bitmap rawBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.distance_sport_icon);
			canvas.drawBitmap(rawBitmap,current_progress - 30,getHeight() / 10  -15,mPaint_current_caloria);


		}else if(current_progress >= data*3 && current_progress < data*4){

			canvas.drawLine(0, getHeight() / 2, getWidth() / 5, getHeight() / 2, mPaint_current_caloria);// 画线 想象成X Y轴坐标轴,如果是一条直线
			canvas.drawCircle(getWidth() / 5 + 15 , getHeight() / 2, 15, paint);//倒数第三个参数是半径
			canvas.drawLine(getWidth() / 5 + 30, getHeight() / 2, (getWidth() / 5)*2 + 30, getHeight() / 2, mPaint_current_caloria);
			canvas.drawCircle((getWidth() / 5)*2 + 30 + 15 , getHeight() / 2, 15, paint);//倒数第三个参数是半径

			canvas.drawLine((getWidth() / 5 + 30)*2, getHeight() / 2, (getWidth() / 5)*3 + 30*2, getHeight() / 2, mPaint_current_caloria);
			canvas.drawCircle((getWidth() / 5)*3 + 30*2 + 15 , getHeight() / 2, 15, paint);//倒数第三个参数是半径
			canvas.drawLine((getWidth() / 5 + 30)*3, getHeight() / 2, current_progress, getHeight() / 2, mPaint_current_caloria);

			Bitmap rawBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.distance_sport_icon);
			canvas.drawBitmap(rawBitmap,current_progress - 30,getHeight() / 10  -15,mPaint_current_caloria);


		}else if(current_progress >= data*4 && current_progress < data*5){
			canvas.drawLine(0, getHeight() / 2, getWidth() / 5, getHeight() / 2, mPaint_current_caloria);// 画线 想象成X Y轴坐标轴,如果是一条直线

			canvas.drawCircle(getWidth() / 5 + 15 , getHeight() / 2, 15, paintCenter);//倒数第三个参数是半径

			canvas.drawLine(getWidth() / 5 + 30, getHeight() / 2, (getWidth() / 5)*2 + 30, getHeight() / 2, mPaint_current_caloria);// 画线 想象成X Y轴坐标轴,如果是一条直线

			canvas.drawCircle((getWidth() / 5)*2 + 30 + 15 , getHeight() / 2, 15, paintCenter);//倒数第三个参数是半径

			canvas.drawLine((getWidth() / 5 + 30)*2, getHeight() / 2, (getWidth() / 5)*3 + 30*2, getHeight() / 2, mPaint_current_caloria);

			canvas.drawCircle((getWidth() / 5)*3 + 30*2 + 15 , getHeight() / 2, 15, paintCenter);//倒数第三个参数是半径

			canvas.drawLine((getWidth() / 5 + 30)*3, getHeight() / 2, (getWidth() / 5)*4 + 30*3, getHeight() / 2, mPaint_current_caloria);

			canvas.drawCircle((getWidth() / 5)*4 + 30*3+ 15 , getHeight() / 2, 15, paintCenter);//倒数第三个参数是半径

			canvas.drawLine((getWidth() / 5 + 30)*4, getHeight() / 2, current_progress, getHeight() / 2, mPaint_current_caloria);

			Bitmap rawBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.distance_sport_icon);
			canvas.drawBitmap(rawBitmap,current_progress - 30,getHeight() / 10  -15,mPaint_current_caloria);
		}else if(current_progress >= data*5){ //画满了
			canvas.drawLine(0, getHeight() / 2, getWidth() / 5, getHeight() / 2, mPaint_current_caloria);// 画线 想象成X Y轴坐标轴,如果是一条直线

			canvas.drawCircle(getWidth() / 5 + 15 , getHeight() / 2, 15, paintCenter);//倒数第三个参数是半径

			canvas.drawLine(getWidth() / 5 + 30, getHeight() / 2, (getWidth() / 5)*2 + 30, getHeight() / 2, mPaint_current_caloria);// 画线 想象成X Y轴坐标轴,如果是一条直线

			canvas.drawCircle((getWidth() / 5)*2 + 30 + 15 , getHeight() / 2, 15, paintCenter);//倒数第三个参数是半径

			canvas.drawLine((getWidth() / 5 + 30)*2, getHeight() / 2, (getWidth() / 5)*3 + 30*2, getHeight() / 2, mPaint_current_caloria);

			canvas.drawCircle((getWidth() / 5)*3 + 30*2 + 15 , getHeight() / 2, 15, paintCenter);//倒数第三个参数是半径

			canvas.drawLine((getWidth() / 5 + 30)*3, getHeight() / 2, (getWidth() / 5)*4 + 30*3, getHeight() / 2, mPaint_current_caloria);

			canvas.drawCircle((getWidth() / 5)*4 + 30*3+ 15 , getHeight() / 2, 15, paintCenter);//倒数第三个参数是半径

			canvas.drawLine((getWidth() / 5 + 30)*4, getHeight() / 2, (getWidth() / 5)*5 + 30*4, getHeight() / 2, mPaint_current_caloria);

			Bitmap rawBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.distance_sport_icon);
			canvas.drawBitmap(rawBitmap, getWidth() - 50 - 30,getHeight() / 10  -15,mPaint_current_caloria);
		}

	}
//保存进度
	public void setProgress(int progress) {
		mProgress = progress;
		postInvalidate();
	}

}