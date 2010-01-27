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

/**
 * The one and only activity class in the application. This draws/updates the UI and does the event handling to
 * choose sorting algorithm/play the animation.
 * 
 * @author dan
 *
 */
public class SortCompare extends Activity {
	/**
	 * How many bars are drawn on screen.
	 */
	private final static int DATA_SET_SIZE = 30;
	
	/**
	 * A list of available sorters, all of which get initialised to the same starting state.
	 */
	private List<Sorter<Integer>> mAllSorters = new ArrayList<Sorter<Integer>>();  
	
	private Button mButton;
	private Sorter<Integer> mLeftSort, mRightSort;
	private SortView mLeftList, mRightList;
	private Spinner mLeftSpinner, mRightSpinner;
	private boolean mNeedsInit = false;
	
	/**
	 * These Runnables are used to do GUI calls from non-GUI threads.
	 * 
	 * The Sorter objects run in a thread each, firing off calls to the GUI. These
	 * calls have to be put in Runnables and posted to the event thread, as it's
	 * not recommended/possible to make GUI changes from outside that thread.
	 */
	private Runnable mPlayEnabler = new Runnable() {
		@Override
		public void run() {
			// whether to re-enable the play button, which happens when both sorters complete.
			boolean e = mLeftSort.isFinished() && mRightSort.isFinished();
			mButton.setEnabled(e);
			mLeftSpinner.setEnabled(e);
			mRightSpinner.setEnabled(e);
		}
	};
	/**
	 * Update to the left list.
	 */
	private Runnable mLeftUpdate = new Runnable() {
		@Override
		public void run() {
			refreshList(mLeftList, mLeftSort);
		}
	};
	/**
	 * Update to the right list.
	 */
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
        // set up the GUI from the resource layout file.
        setContentView(R.layout.sortcompare);
        
        // pull out the components we need to do updates/event-handling for.
        mLeftList = (SortView) findViewById(R.id.leftList);
        mLeftList.setFocusable(false);
        mRightList = (SortView) findViewById(R.id.rightList);
        mRightList.setFocusable(false);
        mRightList.setLeft(false);
        
        // initialise sorters.
        initStartingState();
        
        // spinners (combo boxes in Swing) need adapters to populate their contents and listeners to deal with the events they generate.
        mLeftSpinner = (Spinner) findViewById(R.id.leftSpinner);
        mLeftSpinner.setAdapter(new SorterSpinnerAdapter(this, R.layout.spinnertext, R.id.spinnerText, (Sorter<Integer>[]) mAllSorters.toArray(new Sorter[0])));
        mLeftSpinner.setSelection(0);
        mLeftSpinner.setOnItemSelectedListener(new SpinnerListener(true));
        
        mRightSpinner = (Spinner) findViewById(R.id.rightSpinner);
        mRightSpinner.setAdapter(new SorterSpinnerAdapter(this, R.layout.spinnertext, R.id.spinnerText, (Sorter<Integer>[]) mAllSorters.toArray(new Sorter[0])));
        mRightSpinner.setSelection(1);
        mRightSpinner.setOnItemSelectedListener(new SpinnerListener(false));
              
        // redraw the lists.
        refreshList(mLeftList, mLeftSort);
        refreshList(mRightList, mRightSort);
        
        // event handling for the play button.
        Button play = (Button) findViewById(R.id.startbutton);
        mButton = play;
        play.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// disable GUI controls.
				mButton.setEnabled(false);
				mLeftSpinner.setEnabled(false);
				mRightSpinner.setEnabled(false);
				
				// if we need to, generate a random starting state and redraw the lists.
				if(mNeedsInit) {
					initStartingState();

					
			        refreshList(mLeftList, mLeftSort);
			        refreshList(mRightList, mRightSort);
				}
				mNeedsInit = true;
				// start the animations.
				kickOffSorter(mLeftSort);
				kickOffSorter(mRightSort);
			}
			
			/**
			 * Runs the sorter in a separate thread.
			 * @param s The sorter to run.
			 */
			private void kickOffSorter(Sorter<Integer> s) {
				Thread t = new Thread(s);
				// set to daemon so the JVM can close without waiting for the thread to finish.
				t.setDaemon(true);
				t.start();
			}
		});
    }
    
    private void refreshList(SortView list, Sorter<Integer> sorter) {
    	// link the GUI component to its data source.
    	list.setSorter(sorter);
    	// invalidate, which forces the component to redraw.
    	list.invalidate();
    }
    
    private void initStartingState() {
    	// start off with a sorted array of all integers in the range [1..DATA_SET_SIZE]
    	Integer[] startState = new Integer[DATA_SET_SIZE];
    	for(int i = 0; i < DATA_SET_SIZE; i++) {
    		startState[i] = i+1;
    	} 
    	// make a random permutation.
    	randomlyPermute(startState);
    	
    	// clear the list of available sorters and add one instance of each sub-class of Sorter.
    	// These are the available algorithms shown to users.
    	List<Sorter<Integer>> allSorters = mAllSorters;
    	// note how the reference is brought into a local variable, which is much quicker to access in the Dalvik VM.
    	// I don't know how much time is saved here (compared to the time taken for the rest of the method)
    	// but this method could be run fairly often.
    	allSorters.clear();
    	allSorters.add(new BubbleSorter<Integer>(startState));
    	allSorters.add(new InsertionSorter<Integer>(cloneArray(startState)));
    	allSorters.add(new MergeSorter<Integer>(cloneArray(startState)));
    	allSorters.add(new QuickSorter<Integer>(cloneArray(startState)));
    	allSorters.add(new SelectionSorter<Integer>(cloneArray(startState)));
    	    	
    	// figure out which sorters are displayed, using the selected items from the spinners.
        mLeftSort = mAllSorters.get(mLeftSpinner == null ? 0 : mLeftSpinner.getSelectedItemPosition());
		mRightSort = mAllSorters.get(mRightSpinner == null ? 1 : mRightSpinner.getSelectedItemPosition());
		
		// remove existing listeners and add in a listener to handle events from each sorter.
		mLeftSort.removeSorterListeners();
		mLeftSort.addSorterListener(new EventListener(mLeftUpdate));
		mRightSort.removeSorterListeners();
		mRightSort.addSorterListener(new EventListener(mRightUpdate));
		
		mNeedsInit = false;
    }
    
    /**
     * Copies the contents of the given array into a different one. Order remains the same.
     * @param in The source array.
     * @return A clone of the source array.
     */
    private Integer[] cloneArray(Integer[] in) {
    	Integer[] out = new Integer[in.length];
    	System.arraycopy(in, 0, out, 0, in.length);
    	return out;
    }
    
    /**
     * To get a random order, swap each member of the array with a randomly chosen index.
     * @param state array to be permutated.
     */
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
    
    /**
     * Event handling for changes to the spinner's selected values.
     * @author dan
     *
     */
    private class SpinnerListener implements OnItemSelectedListener {
    	/**
    	 * Whether this spinner is on the left-hand side of the screen.
    	 */
    	private boolean mLeft;
    	public SpinnerListener(boolean left) {
    		this.mLeft = left;
    	}
    	/**
    	 * Called when the user makes a selection within the spinner.
    	 */
		@Override
		public void onItemSelected(AdapterView parent, View v, int position, long id) {
			Spinner thisOne = mLeft ? mLeftSpinner : mRightSpinner;
			Spinner thatOne = mLeft ? mRightSpinner : mLeftSpinner;
			
			// ensure that the spinners never have the same selected sorting algorithm.
			// TODO: Would be better to just not display the algorithm that is selected in the other spinner.
			if(position == thatOne.getSelectedItemPosition()) {
				// setting a new position will call this method again as part of the component's event handling.
				if(position > 0) {
					thisOne.setSelection(position-1);
				} else {
					thisOne.setSelection(position+1);
				}
			} else { 
				// if the spinners have different values, get a starting state and update the display.
				initStartingState();
				refreshList(mLeftList, mLeftSort);
		        refreshList(mRightList, mRightSort);
			}			
		}
		@Override
		public void onNothingSelected(AdapterView parent) {
			// do nothing. This isn't possible.
		}
    }
    
    /**
     * Implementation of the listener interface defined for Sorter classes.
     * @author dan
     *
     */
    private class EventListener implements SorterListener<Integer> {
    	/**
    	 * The runnable that will be called to update the view of this sorter.
    	 */
    	Runnable mListRefresh;
    	EventListener(Runnable listRefresh) {
    		mListRefresh = listRefresh;
    	}
    	/**
    	 * Posts the update to the GUI thread so the view can be updated with changed data.
    	 */
		@Override
		public void sorterDataChange(SorterEvent<Integer> e) {
			runOnUiThread(mListRefresh);
		}
		/**
		 * When the sorter finishes, check if we should enable the GUI controls again.
		 * This happens when both sorters have finished. We don't need any synchronization
		 * in the mPlayEnabler runnable, because we know the code will never be run
		 * concurrently in different threads.
		 */
		@Override
		public void sorterFinished(SorterEvent<Integer> e) {
			runOnUiThread(mPlayEnabler);
		}
    	
    	
    }
    
    /**
     * Deals with converting the array of sorters into a list of strings for users
     * to choose between in the spinner.
     * @author dan
     *
     */
    private class SorterSpinnerAdapter extends ArrayAdapter<Sorter<Integer>> {
    	private int mResource;
    	private int mTextViewId;
    	
    	public SorterSpinnerAdapter(Context context, int resource, int textViewResourceId, Sorter<Integer>[] data) {
    		super(context, resource, textViewResourceId, data);
    		this.mResource = resource;
    		this.mTextViewId = textViewResourceId;
    	}
    	
    	/**
    	 * Get the view shown on the main activity, displaying the chosen value.
    	 */
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent) {
    		return getView(position, convertView, 5);
    	}
    	
    	/**
    	 * The view used for each row of the list shown when the user choosing a value.
    	 */
    	@Override
    	public View getDropDownView(int position, View convertView, ViewGroup parent) {
    		return getView(position, convertView, 10);
			
    	}
    	
    	/**
    	 * We use the same logic for both kinds of view a spinner needs, but with different padding to reflect the different sizing
    	 * we want.
    	 * @param position The position in the list.
    	 * @param convertView The view to use, can be null - in which case build a view to use.
    	 * @param padding The padding to put around the text.
    	 * @return The view to use on screen.
    	 */
    	private View getView(int position, View convertView, int padding) {
    		View retVal = null;
    		// check that convertView is available and represents the right component.
			if(convertView != null && convertView.getId() == mResource) {
				retVal = convertView;
			} else {
				// if it's not right, build a view to use instead.
				LayoutInflater inflater = SortCompare.this.getLayoutInflater();
				retVal = inflater.inflate(mResource, null);
			}
			// get the TextView label from within the main view and set its text and padding appropriately.
			TextView text = (TextView) retVal.findViewById(mTextViewId);
			text.setText(getItem(position).getSorterName());
			text.setPadding(padding/2,padding,padding/2,padding);
			return retVal;
    	}
    }
}