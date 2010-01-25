package eu.danielwhite.sorter.algos;

public interface SorterListener<T extends Comparable<T>> {
	
		public void sorterFinished(SorterEvent<T> e);
		
		public void sorterDataChange(SorterEvent<T> e);
}
