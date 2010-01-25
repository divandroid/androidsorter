package eu.danielwhite.sorter.algos;

import eu.danielwhite.sorter.R;

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
	
	private void qSort(int left, int right) {
		if(right > left) {
			int pivotIndex = (left+right)/2;
			pivotIndex = partition(left, right, pivotIndex);
			qSort(left, pivotIndex-1);
			qSort(pivotIndex+1, right);
		}
	}

	private int partition(int left, int right, int pivotIndex) {
		T pivotVal = getDataVal(pivotIndex);
		swap(pivotIndex, right);
		int storeIndex = left;
		
		for(int i = left; i < right; i++) {
			if(compareData(getDataVal(i), pivotVal) <= 0) {
				swap(i, storeIndex);
				storeIndex++;
			}
		}
		swap(storeIndex, right);
		return storeIndex;
	}
	
}
