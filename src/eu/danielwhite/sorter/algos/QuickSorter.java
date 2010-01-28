package eu.danielwhite.sorter.algos;

import eu.danielwhite.sorter.R;

/**
 * Implementation of quick sort. A very fast sorting algorithm.
 * @author dan
 *
 * @param <T> The type of data to be sorted.
 */
public class QuickSorter<T extends Comparable<T>> extends Sorter<T> {

	public QuickSorter(T[] data) {
		super(data);
	}
	
	@Override
	public int getSorterName() {
		return R.string.quick_sort;
	}
	
	@Override
	public void run() {
		qSort(0, getDataLength()-1);
		fireSorterFinished();
	}
	
	/**
	 * Quick sort is recursive in this implementation. Sort the array between the two indices (inclusive)
	 * by dividing it into two groups - one which is the values <= pivot and the other which is the rest.
	 * @param left The low end of the range.
	 * @param right The high end of the range.
	 */
	private void qSort(int left, int right) {
		// only do something if [left.. right] is a valid range.
		if(right > left) {
			int pivotIndex = (left+right)/2;
			// partition separates the two groups, and returns the new position of the pivot.
			pivotIndex = partition(left, right, pivotIndex);
			// apply this method to the two groups separately.
			qSort(left, pivotIndex-1);
			qSort(pivotIndex+1, right);
		}
	}

	private int partition(int left, int right, int pivotIndex) {
		T pivotVal = getDataVal(pivotIndex);
		// place the pivot at the right end of the array.
		swap(pivotIndex, right);
		// index points to the left end of the array.
		int storeIndex = left;
		
		// go from left up until just before the new position of the pivot.
		for(int i = left; i < right; i++) {
			// if the current data is <= the pivot, swap it with our pointer index.
			if(compareData(getDataVal(i), pivotVal) <= 0) {
				swap(i, storeIndex);
				// after this, the index points at the location of the split.
				storeIndex++;
			}
		}
		// put the pivot into its rightful place (this is its final position in a sorted array).
		swap(storeIndex, right);
		// return the location of the pivot once the partition is completed.
		return storeIndex;
	}
	
}
