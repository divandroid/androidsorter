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

/**
 * The component that handles the drawing of the data onto the screen.
 * 
 * In order to get the app going, I just used a list of progress bar components initially. Just prior to release
 * I replaced it with a custom component to make it look better.
 * 
 * @author dan
 *
 */
public class SortView extends View {
	/**
	 * The width and height of the component.
	 */
	private int mWidth = -1, mHeight = -1;
	/**
	 * The sorter that this view currently represents.
	 */
	private Sorter<Integer> mSorter = null;
	/**
	 * The paints used for the contents of each bar, and their outlines.
	 */
	private Paint mFillPaint, mStrokePaint;
	/**
	 * Whether the component is on the left or right hand side of the screen.
	 */
	private boolean mLeft = true;
	
	// standard view constructors so the object can be built programatically or by inflating from layout XML.
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
	
	/**
	 * Initialise the paint objects - the gradient is dealt with while the drawing occurs.
	 */
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
	
	/**
	 * Make a gradient between 3 colours, across the given height and starting at the given y co-ordinate.
	 * @param barHeight The height of each bar on screen.
	 * @param pos The y co-ordinate of the top of the bar.
	 * @return A horizontal LinearGradient going from dark green to a lighter green and back again.
	 */
	private Shader makeShader(int barHeight, float pos) {
		return new LinearGradient(
				0f, pos, 0f, pos+barHeight, new int[] {0xFF00AA00, 0xFF00DD00, 0xFF00AA00}, null, Shader.TileMode.CLAMP
		);
	}
	
	/**
	 * Change/initialise the sorter that the view will use as its data source.
	 * @param sorter The new data source.
	 */
	public void setSorter(Sorter<Integer> sorter) {
		mSorter = sorter;
	}
	
	/**
	 * Tell the view which side of the screen it will be on. This affects which edge the bars are drawn on.
	 * @param val true == left.
	 */
	public void setLeft(boolean val) {
		this.mLeft = val;
	}
	
	/**
	 * Whether the view is on the left-hand side of the screen.
	 * @return true == left.
	 */
	public boolean isLeft() {
		return mLeft;
	}
	
	/**
	 * Called whenever the components needs to be redrawn.
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// localise the references to speed things up.
		Sorter<Integer> sorter = mSorter;
		// only draw if a data source has been set.
		if(sorter == null) return;
		
		int height = mHeight;
		int width = mWidth;
		
		Integer[] data = sorter.getData();
		int dataLength = data.length;
		
		// compute a height for each data bar.
		int barHeight = height/dataLength;
		for(int i = 0; i < dataLength; i++) {
			// width of the bar depends on the data value at this position.
			int barWidth = (int) ((data[i].doubleValue()/dataLength)*width);
			Rect r;
			// compute the y co-ordinate for the top of the bar.
			int yTop = barHeight*i;
			// the edge of the rectangle is on a different side of the view, so that we make a pyramid when both are shown.
			if(!mLeft) {
				r = new Rect(0, yTop, barWidth, yTop+barHeight);
			} else {
				r = new Rect(width-barWidth, yTop, width, yTop+barHeight);
			}
			// get a gradient so each bar has the same shading.
			mFillPaint.setShader(makeShader(barHeight, yTop));
			// draw the fill first
			canvas.drawRect(r, mFillPaint);
			// draw an outline around the fill.
			canvas.drawRect(r, mStrokePaint);
		}
		
	}
	
	/**
	 * Overridden so we can get the width and height. This view only works if it is told
	 * to occupy all its available space, it has no capability of computing a space requirement at the moment.
	 */
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
