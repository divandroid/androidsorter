package eu.danielwhite.sorter.algos;

import java.util.ArrayList;
import java.util.List;

public abstract class Sorter<T extends Comparable<T>> implements Runnable {
	protected static final long COMPARE_DELAY = 60;
	protected static final long WRITE_DELAY = 30;
	
	private List<SorterListener<T>> mListeners = new ArrayList<SorterListener<T>>();
	
	private T[] mData;
	protected final SorterEvent<T> mEvt = new SorterEvent<T>(this);
	protected boolean mFinished = false;
	
	protected Sorter(T[] data) {
		this.mData = data;
	}
	
	public abstract int getSorterName();
	
	public T[] getData() {
		return mData;
	}
	
	public boolean isFinished() {
		return mFinished;
	}
	
	protected synchronized void doSleepDelay(long delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			// do nothing.
		}
	}
	
	protected int compareData(T x, T y) {
		doSleepDelay(COMPARE_DELAY);
		return x.compareTo(y);
	}
	
	protected int compareData(int x, int y) {
		return compareData(getDataVal(x), getDataVal(y));
	}
	
	protected T getDataVal(int x) {
		return mData[x];
	}
	
	protected void setDataVal(int x, T t) {
		setDataVal(x, t, true);
	}
	
	protected void setDataVal(int x, T t, boolean fireEvent) {
		doSleepDelay(WRITE_DELAY);
		mData[x] = t;
		if(fireEvent) fireSorterDataChange();
	}
	
	protected int getDataLength() {
		return mData.length;
	}
	

	protected void swap(int x, int y) {
		T t = getDataVal(x);
		setDataVal(x, getDataVal(y), false);
		setDataVal(y, t, true);
	}
	
	public void addSorterListener(SorterListener<T> l) {
		mListeners.add(l);
	}
	
	public void removeSorterListener(SorterListener<T> l) {
		mListeners.remove(l);
	}
	
	public void removeSorterListeners() {
		mListeners.clear();
	}
	
	protected void fireSorterFinished() {
    	synchronized (this) {
        	mFinished = true;
    	}
		SorterEvent<T> evt = mEvt;
		for(SorterListener<T> sl : mListeners) {
			sl.sorterFinished(evt);
		}
	}
	
	protected void fireSorterDataChange() {
		SorterEvent<T> evt = mEvt;
		for(SorterListener<T> sl : mListeners) {
			sl.sorterDataChange(evt);
		}
	}	
}
