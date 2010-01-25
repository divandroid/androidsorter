package eu.danielwhite.sorter.algos;

import eu.danielwhite.sorter.R;

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
