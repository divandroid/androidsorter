package eu.danielwhite.sorter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import eu.danielwhite.sorter.algos.BubbleSorter;
import eu.danielwhite.sorter.algos.InsertionSorter;
import eu.danielwhite.sorter.algos.MergeSorter;
import eu.danielwhite.sorter.algos.QuickSorter;
import eu.danielwhite.sorter.algos.SelectionSorter;
import eu.danielwhite.sorter.algos.Sorter;
import eu.danielwhite.sorter.algos.SorterEvent;
import eu.danielwhite.sorter.algos.SorterListener;
import eu.danielwhite.sorter.components.SortView;

public class SortCompare extends Activity {
	private final static int DATA_SET_SIZE = 30;
	
	private List<Sorter<Integer>> mAllSorters = new ArrayList<Sorter<Integer>>();  
	
	private Button mButton;
	private Sorter<Integer> mLeftSort, mRightSort;
	private SortView mLeftList, mRightList;
	private Spinner mLeftSpinner, mRightSpinner;
	private boolean mNeedsInit = false;
	
	private Runnable mPlayEnabler = new Runnable() {
		@Override
		public void run() {
			boolean e = mLeftSort.isFinished() && mRightSort.isFinished();
			mButton.setEnabled(e);
			mLeftSpinner.setEnabled(e);
			mRightSpinner.setEnabled(e);
		}
	};
	private Runnable mLeftUpdate = new Runnable() {
		@Override
		public void run() {
			refreshList(mLeftList, mLeftSort);
		}
	};
	private Runnable mRightUpdate = new Runnable() {
		@Override
		public void run() {
			refreshList(mRightList, mRightSort);
		}
	};
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sortcompare);
        
        mLeftList = (SortView) findViewById(R.id.leftList);
        mLeftList.setFocusable(false);
        mRightList = (SortView) findViewById(R.id.rightList);
        mRightList.setFocusable(false);
        mRightList.setLeft(false);
        
        initStartingState();
        
        mLeftSpinner = (Spinner) findViewById(R.id.leftSpinner);
        mLeftSpinner.setAdapter(new SorterSpinnerAdapter(this, R.layout.spinnertext, R.id.spinnerText, (Sorter<Integer>[]) mAllSorters.toArray(new Sorter[0])));
        mLeftSpinner.setSelection(0);
        mLeftSpinner.setOnItemSelectedListener(new SpinnerListener(true));
        
        mRightSpinner = (Spinner) findViewById(R.id.rightSpinner);
        mRightSpinner.setAdapter(new SorterSpinnerAdapter(this, R.layout.spinnertext, R.id.spinnerText, (Sorter<Integer>[]) mAllSorters.toArray(new Sorter[0])));
        mRightSpinner.setSelection(1);
        mRightSpinner.setOnItemSelectedListener(new SpinnerListener(false));
              

		
        refreshList(mLeftList, mLeftSort);
        refreshList(mRightList, mRightSort);
        
        Button play = (Button) findViewById(R.id.startbutton);
        mButton = play;
        play.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mButton.setEnabled(false);
				mLeftSpinner.setEnabled(false);
				mRightSpinner.setEnabled(false);
				if(mNeedsInit) {
					initStartingState();

					
			        refreshList(mLeftList, mLeftSort);
			        refreshList(mRightList, mRightSort);
				}
				mNeedsInit = true;
				kickOffSorter(mLeftSort);
				kickOffSorter(mRightSort);
			}
			
			private void kickOffSorter(Sorter<Integer> s) {
				Thread t = new Thread(s);
				t.setDaemon(true);
				t.start();
			}
		});
    }
    
    private void refreshList(SortView list, Sorter<Integer> sorter) {
//    	ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, R.layout.sortrow, R.id.rowValue, sorter.getData()) {
//
//			@Override
//			public View getView(int position, View convertView, ViewGroup parent) {
//				View retVal = null;
//				if(convertView != null && convertView.getId() == R.layout.sortrow) {
//					retVal = convertView;
//				} else {
//					LayoutInflater inflater = SortCompare.this.getLayoutInflater();
//					retVal = inflater.inflate(R.layout.sortrow, null);
//				}
//				
//				ProgressBar value = (ProgressBar) retVal.findViewById(R.id.rowValue);
//				value.setProgress(this.getItem(position)*(100/DATA_SET_SIZE));
//				value.setFocusable(false);
//				retVal.setFocusable(false);
//				
//				return retVal;
//			}
//        	
//        };
//        list.setAdapter(adapter);
    	list.setSorter(sorter);
    	list.invalidate();
    }
    
    private void initStartingState() {
    	Integer[] startState = new Integer[DATA_SET_SIZE];
    	for(int i = 0; i < DATA_SET_SIZE; i++) {
    		startState[i] = i+1;
    	} 
    	randomlyPermute(startState);
    	
    	mAllSorters.clear();
    	mAllSorters.add(new BubbleSorter<Integer>(startState));
    	mAllSorters.add(new InsertionSorter<Integer>(cloneArray(startState)));
    	mAllSorters.add(new MergeSorter<Integer>(cloneArray(startState)));
    	mAllSorters.add(new QuickSorter<Integer>(cloneArray(startState)));
    	mAllSorters.add(new SelectionSorter<Integer>(cloneArray(startState)));
    	    	
        mLeftSort = mAllSorters.get(mLeftSpinner == null ? 0 : mLeftSpinner.getSelectedItemPosition());
		mRightSort = mAllSorters.get(mRightSpinner == null ? 1 : mRightSpinner.getSelectedItemPosition());
		mLeftSort.removeSorterListeners();
		mLeftSort.addSorterListener(new EventListener(mLeftUpdate));
		mRightSort.removeSorterListeners();
		mRightSort.addSorterListener(new EventListener(mRightUpdate));
		
		mNeedsInit = false;
    }
    
    private Integer[] cloneArray(Integer[] in) {
    	Integer[] out = new Integer[in.length];
    	System.arraycopy(in, 0, out, 0, in.length);
    	return out;
    }
    
    private static void randomlyPermute(Integer[] state) {
    	for(int i = 0; i < state.length; i++) {
    		int swapWith = (int)(Math.random()*DATA_SET_SIZE-0.5);
    		swapWith = Math.max(0,Math.min(DATA_SET_SIZE-1,swapWith));
    		if(swapWith != i) {
    			int x = state[i];
    			state[i] = state[swapWith];
    			state[swapWith] = x;
    		}
    	}
    }
    
    private class SpinnerListener implements OnItemSelectedListener {
    	private boolean mLeft;
    	public SpinnerListener(boolean left) {
    		this.mLeft = left;
    	}
		@Override
		public void onItemSelected(AdapterView parent, View v, int position, long id) {
			Spinner thisOne = mLeft ? mLeftSpinner : mRightSpinner;
			Spinner thatOne = mLeft ? mRightSpinner : mLeftSpinner;
			
			if(position == thatOne.getSelectedItemPosition()) {
				if(position > 0) {
					thisOne.setSelection(position-1);
				} else {
					thisOne.setSelection(position+1);
				}
			} else {
				initStartingState();
				refreshList(mLeftList, mLeftSort);
		        refreshList(mRightList, mRightSort);
			}			
		}
		@Override
		public void onNothingSelected(AdapterView parent) {
			// do nothing.
		}
    }
    
    private class EventListener implements SorterListener<Integer> {
    	Runnable mListRefresh;
    	EventListener(Runnable listRefresh) {
    		mListRefresh = listRefresh;
    	}
		@Override
		public void sorterDataChange(SorterEvent<Integer> e) {
			runOnUiThread(mListRefresh);
		}
		@Override
		public void sorterFinished(SorterEvent<Integer> e) {
			runOnUiThread(mPlayEnabler);
		}
    	
    	
    }
    
    private class SorterSpinnerAdapter extends ArrayAdapter<Sorter<Integer>> {
    	private int mResource;
    	private int mTextViewId;
    	
    	public SorterSpinnerAdapter(Context context, int resource, int textViewResourceId, Sorter<Integer>[] data) {
    		super(context, resource, textViewResourceId, data);
    		this.mResource = resource;
    		this.mTextViewId = textViewResourceId;
    	}
    	
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		return getView(position, convertView, 5);
    	}
    	
    	@Override
    	public View getDropDownView(int position, View convertView, ViewGroup parent) {
    		return getView(position, convertView, 10);
			
    	}
    	
    	private View getView(int position, View convertView, int padding) {
    		View retVal = null;
			if(convertView != null && convertView.getId() == mResource) {
				retVal = convertView;
			} else {
				LayoutInflater inflater = SortCompare.this.getLayoutInflater();
				retVal = inflater.inflate(mResource, null);
			}
			TextView text = (TextView) retVal.findViewById(mTextViewId);
			text.setText(getItem(position).getSorterName());
			text.setPadding(padding/2,padding,padding/2,padding);
			return retVal;
    	}
    }
}