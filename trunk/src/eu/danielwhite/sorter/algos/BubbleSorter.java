package eu.danielwhite.sorter.algos;

import eu.danielwhite.sorter.R;

public class BubbleSorter<T extends Comparable<T>> extends Sorter<T> {

	public BubbleSorter(T[] data) {
		super(data);
	}
	
	@Override
	public int getSorterName() {
		return R.string.bubble_sort;
	}

	@Override
	public void run() {
		int iLimit = getDataLength();
    	for(int i = iLimit-1; i >= 0; i--) {
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
    	fireSorterFinished();
		
	}

}
