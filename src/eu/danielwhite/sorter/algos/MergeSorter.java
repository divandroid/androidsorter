package eu.danielwhite.sorter.algos;

import java.util.ArrayList;
import java.util.List;

import eu.danielwhite.sorter.R;

public class MergeSorter<T extends Comparable<T>> extends Sorter<T> {

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
		List<T> x = subList(xLow,xHigh);
		List<T> y = subList(yLow, yHigh);
		doSleepDelay(x.size() * WRITE_DELAY);
		
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
	
	private List<T> subList(int low, int high) {
		List<T> retVal = new ArrayList<T>(high-low);
		for(int i = low; i <= high; i++) {
			retVal.add(getDataVal(i));
		}
		return retVal;
	}

}
