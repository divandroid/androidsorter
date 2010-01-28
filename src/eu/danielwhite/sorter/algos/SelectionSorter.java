package eu.danielwhite.sorter.algos;

import eu.danielwhite.sorter.R;

/**
 * Implementation of selection sort.
 * @author dan
 *
 * @param <T> The type of data to be sorted.
 */
public class SelectionSorter<T extends Comparable<T>> extends Sorter<T> {

	public SelectionSorter(T[] data) {
		super(data);
	}
	
	@Override
	public int getSorterName() {
		return R.string.selection_sort;
	}

	@Override
	public void run() {
		// this is basically an optimisation of bubble sort.
		// repeatedly scan the array, finding the minimum value from the unsorted portion of the data.
		// Once done, swap it with whatever is already there. Repeat until the array is sorted.
		int length = getDataLength();
		for(int i = 0; i < length-1; i++) {
			int min = i;
			for(int j = i+1; j< length; j++) {
				if(compareData(j, min) < 0) min = j;
			}
			
			if(i != min) swap(i, min);
		}
		fireSorterFinished();
	}

}
