package eu.danielwhite.sorter.algos;

import eu.danielwhite.sorter.R;

/**
 * Bubble sort implementation.
 * 
 * http://en.wikipedia.org/wiki/Bubble_sort
 * 
 * @author dan
 *
 * @param <T> The type of the array to be sorted.
 */
public class BubbleSorter<T extends Comparable<T>> extends Sorter<T> {

	public BubbleSorter(T[] data) {
		super(data);
	}
	
	@Override
	public int getSorterName() {
		return R.string.bubble_sort;
	}

	/**
	 * The thread that is executed.
	 */
	@Override
	public void run() {
		int iLimit = getDataLength();
		// Go through the array, swapping each element with the one next to it if they are out of order.
    	for(int i = iLimit-1; i >= 0; i--) {
    		// Keep going until no items have been swapped on a single pass through the array.
    		boolean swapped = false;
    		for(int j = 0; j < i; j++) {

    			if(compareData(getDataVal(j), getDataVal(j+1)) > 0) {
    				swap(j, j+1);
    				swapped = true;
    			}
    		}
    		// if no swaps occurred, then stop as it's finished.
    		if(!swapped) break;
    	}
    	// let everybody know we've finished.
    	fireSorterFinished();
		
	}

}
