package eu.danielwhite.sorter.algos;

import java.util.ArrayList;
import java.util.List;

import eu.danielwhite.sorter.R;

/**
 * A non-recursive implementation of merge-sort. This is probably the best general purpose
 * sorting algorithm.
 * http://en.wikipedia.org/wiki/Merge_sort
 * 
 * @author dan
 *
 * @param <T> The type of the data to be sorted.
 */
public class MergeSorter<T extends Comparable<T>> extends Sorter<T> {

	// controls whether we draw intermediate states while the lists are being merged.
	private static final boolean FULLY_ANIMATE = true;
	
	public MergeSorter(T[] data) {
		super(data);
	}
	
	@Override
	public int getSorterName() {
		return R.string.merge_sort;
	}

	@Override
	public void run() {	
		simplerMergeSort();
		fireSorterFinished();
	}
	
	private void simplerMergeSort() {
		// keep going through the data, doubling the size of the lists to be merged each time. Starting with lists of one member each time.
		int m = 1;
		int length = getDataLength();
		while(m <= length) {
			int i = 0;
			while(i < length-m) {
				mergeSubArray(i, i+m-1, i+m,Math.min(i+2*m-1, length-1));
				i += 2*m;
			}
			m *= 2;
		}
	}
	
	private void mergeSubArray(int xLow, int xHigh, int yLow, int yHigh) {
		// read the data into lists
		List<T> x = subList(xLow,xHigh);
		List<T> y = subList(yLow, yHigh);
		// add on some cost for the extra overhead of space/time to duplicate data. Not sure if this is the right thing to do,
		// but it feels like cheating if we don't?
		doSleepDelay(x.size() * WRITE_DELAY);
		
		// merge lists by taking the smaller top element and placing it into the data array
		int ptr = xLow;
		while(x.size() > 0 && y.size() > 0) {
			doSleepDelay(COMPARE_DELAY);
			if(x.get(0).compareTo(y.get(0)) > 0) {
				setDataVal(ptr++, y.get(0), FULLY_ANIMATE);
				y.remove(0);
			} else {
				setDataVal(ptr++, x.get(0), FULLY_ANIMATE);
				x.remove(0);
			}
		}
		// when only one list has data left, append it to the data array.
		while(x.size() > 0) {
			setDataVal(ptr++, x.get(0), FULLY_ANIMATE);
			x.remove(0);
		}
		while(y.size() > 0) {
			setDataVal(ptr++, y.get(0), FULLY_ANIMATE);
			y.remove(0);
		}
		fireSorterDataChange();
	}
	
	// read the data from the array in a given range. Return a list in the same order as the data array.
	private List<T> subList(int low, int high) {
		List<T> retVal = new ArrayList<T>(high-low);
		for(int i = low; i <= high; i++) {
			retVal.add(getDataVal(i));
		}
		return retVal;
	}

}
