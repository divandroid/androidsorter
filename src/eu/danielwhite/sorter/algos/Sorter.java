package eu.danielwhite.sorter.algos;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for sorter algorithms.
 * @author dan
 *
 * @param <T> The type of data to be sorted. I made the class generic but the only instantiations all use Integer.
 */
public abstract class Sorter<T extends Comparable<T>> implements Runnable {
	/**
	 * Delay in milliseconds for every comparison between two items. These delays slow the sorting down so
	 * that it is visible to users.
	 */
	protected static final long COMPARE_DELAY = 60;
	/**
	 * Delay in milliseconds for every write into the data array.
	 */
	protected static final long WRITE_DELAY = 30;
	
	/**
	 * Listeners on the class. I made it a List but there should only ever be one at the moment.
	 */
	private List<SorterListener<T>> mListeners = new ArrayList<SorterListener<T>>();
	
	/**
	 * The data the object will be/is sorting.
	 */
	private T[] mData;
	/**
	 * An event to hand out to listeners. It contains no contextual information so may as well just re-use the same object all the time.
	 */
	protected final SorterEvent<T> mEvt = new SorterEvent<T>(this);
	/**
	 * Whether the sorter has finished sorting.
	 */
	protected boolean mFinished = false;
	
	/**
	 * Called by subclasses
	 * @param data The data to be sorted, in its initial state.
	 */
	protected Sorter(T[] data) {
		this.mData = data;
	}
	
	/**
	 * Called by GUI components, should return an int reference for a String.
	 * @return
	 */
	public abstract int getSorterName();
	
	/**
	 * Get the current data array. This returns a reference to the actual array, rather than a clone, so
	 * any changes would be reflected in this object - possibly causing CATASTROPHE!
	 * @return The data array.
	 */
	public T[] getData() {
		return mData;
	}
	
	/**
	 * Report on whether the object has finished sorting.
	 * @return true iff the thread has completed.
	 */
	public boolean isFinished() {
		return mFinished;
	}
	
	/**
	 * Convenience method - causes the thread to sleep for the specified delay.
	 * @param delay The delay, in milliseconds.
	 */
	protected synchronized void doSleepDelay(long delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			// do nothing.
		}
	}
	
	/**
	 * Convenience method. Compare the two objects and return the result. Ensures that the right delay occurs.
	 * @param x An object.
	 * @param y Another object.
	 * @return Less than zero if x < y, more than zero if x > y - other wise returns zero.
	 */
	protected int compareData(T x, T y) {
		doSleepDelay(COMPARE_DELAY);
		return x.compareTo(y);
	}
	
	/**
	 * Compare the two objects at the given positions in the array.
	 * @param x An array index.
	 * @param y Another array index.
	 * @return Less than zero if x < y, more than zero if x > y - other wise returns zero.
	 */
	protected int compareData(int x, int y) {
		return compareData(getDataVal(x), getDataVal(y));
	}
	
	/**
	 * Returns the data at the given index. I used a getter, primarily so I could introduce delays on array reads if I wanted to.
	 * @param x An array index.
	 * @return The object at that position in the data array.
	 */
	protected T getDataVal(int x) {
		return mData[x];
	}
	
	/**
	 * Put the given object into the data array at the specified position.
	 * Ensures that a delay occurs.
	 * @param x An array index.
	 * @param t An object to place into the array.
	 */
	protected void setDataVal(int x, T t) {
		setDataVal(x, t, true);
	}
	
	/**
	 * Put the given object into the data array at the specified position.
	 * Ensures that a delay occurs.
	 * @param x An array index.
	 * @param t An object to place into the array.
	 * @param fireEvent Whether to fire a data change event.
	 */
	protected void setDataVal(int x, T t, boolean fireEvent) {
		doSleepDelay(WRITE_DELAY);
		mData[x] = t;
		if(fireEvent) fireSorterDataChange();
	}
	
	/**
	 * Data array length.
	 * @return Number of items in the data array.
	 */
	protected int getDataLength() {
		return mData.length;
	}
	
	/**
	 * Convenience method to swap the data at the given positions.
	 * @param x An array index.
	 * @param y An array index.
	 */
	protected void swap(int x, int y) {
		T t = getDataVal(x);
		setDataVal(x, getDataVal(y), false);
		setDataVal(y, t, true);
	}
	
	/**
	 * Set up a listener to respond to sorting events.
	 * @param l The listener to remove.
	 */
	public void addSorterListener(SorterListener<T> l) {
		mListeners.add(l);
	}
	
	/**
	 * Remove the given listener so that it no longer receives events.
	 * @param l The listener to remove.
	 */
	public void removeSorterListener(SorterListener<T> l) {
		mListeners.remove(l);
	}
	
	/**
	 * Remove all sorter listeners from this object.	
	 */
	public void removeSorterListeners() {
		mListeners.clear();
	}
	
	/**
	 * Notifies listeners when the thread has finished sorting the array.
	 */
	protected void fireSorterFinished() {
    	synchronized (this) {
        	mFinished = true;
    	}
		SorterEvent<T> evt = mEvt;
		for(SorterListener<T> sl : mListeners) {
			sl.sorterFinished(evt);
		}
	}
	
	/**
	 * Notifies listeners that the data order has changed, so that they can redraw.
	 */
	protected void fireSorterDataChange() {
		SorterEvent<T> evt = mEvt;
		for(SorterListener<T> sl : mListeners) {
			sl.sorterDataChange(evt);
		}
	}	
}
