package eu.danielwhite.sorter.algos;

import eu.danielwhite.sorter.R;

/**
 * Implementation of insertion sort.
 * http://en.wikipedia.org/wiki/Insertion_sort
 * @author Dan
 *
 * @param <T> The type of data to be sorted.
 */
public class InsertionSorter<T extends Comparable<T>> extends Sorter<T> {
	
	public InsertionSorter(T[] data) {
		super(data);
	}

	@Override
	public int getSorterName() {
		return R.string.insertion_sort;
	}

	@Override
	public void run() {
		// on each pass through the list, sweep through the previously sorted items making room for the new item, until you find the right position. 
		for(int i = 1; i < getDataLength(); i++) {
			T val = getDataVal(i);
			int j = i - 1;
			while(j >= 0) {
				T jVal = getDataVal(j);
				if(compareData(jVal, val) > 0) {
					setDataVal(j+1, jVal, false);
					j--;
				} else {
					break;
				}
			}
			setDataVal(j+1, val, true);
		}
		fireSorterFinished();
	}
}
