package eu.danielwhite.sorter.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import eu.danielwhite.sorter.algos.Sorter;

public class SortView extends View {
	
	private int mWidth = -1, mHeight = -1;
	private Sorter<Integer> mSorter = null;
	private Paint mFillPaint, mStrokePaint;
	private boolean mLeft = true;
	
	public SortView(Context context) {
		super(context);
		initView();
	}
	
	public SortView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	
	public SortView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}
	
	private void initView() {
		mFillPaint = new Paint();
		mFillPaint.setColor(0xFF00AA00);
		mFillPaint.setAntiAlias(true);
		
		mStrokePaint = new Paint();
		mStrokePaint.setAntiAlias(true);
		mStrokePaint.setColor(0xFF000000);
		mStrokePaint.setStrokeWidth(0);
		mStrokePaint.setStyle(Paint.Style.STROKE);
	}
	
	private Shader makeShader(int barHeight, float pos) {
		return new LinearGradient(
				0f, pos, 0f, pos+barHeight, new int[] {0xFF00AA00, 0xFF00DD00, 0xFF00AA00}, null, Shader.TileMode.CLAMP
		);
	}
	
	public void setSorter(Sorter<Integer> sorter) {
		mSorter = sorter;
	}
	
	public void setLeft(boolean val) {
		this.mLeft = val;
	}
	
	public boolean isLeft() {
		return mLeft;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Sorter<Integer> sorter = mSorter;
		if(sorter == null) return;
		
		int height = mHeight;
		int width = mWidth;
		
		Integer[] data = sorter.getData();
		int dataLength = data.length;
		
		int barHeight = height/dataLength;
		for(int i = 0; i < dataLength; i++) {
			int barWidth = (int) ((data[i].doubleValue()/dataLength)*width);
			Rect r;
			int yTop = barHeight*i;
			if(!mLeft) {
				r = new Rect(0, yTop, barWidth, yTop+barHeight);
			} else {
				r = new Rect(width-barWidth, yTop, width, yTop+barHeight);
			}
			mFillPaint.setShader(makeShader(barHeight, yTop));
			canvas.drawRect(r, mFillPaint);
			canvas.drawRect(r, mStrokePaint);
		}
		
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mWidth = getMeasureSize(widthMeasureSpec);
		mHeight = getMeasureSize(heightMeasureSpec);
		setMeasuredDimension(mWidth, mHeight);
	}
	
	private int getMeasureSize(int measureSpec) {
		//int result = 0;
		// mode can be EXACTLY, AT_MOST or UNSPECIFIED. 
		//int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		
		return specSize;
	}
}
