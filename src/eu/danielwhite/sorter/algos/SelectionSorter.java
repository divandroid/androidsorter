package eu.danielwhite.sorter.algos;

import eu.danielwhite.sorter.R;

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
