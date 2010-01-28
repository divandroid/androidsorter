package eu.danielwhite.sorter.algos;

/**
 * Interface for classes to implement in order to receive events from Sorter objects.
 * @author dan
 *
 * @param <T> The type of data the sorters will be operating upon.
 */
public interface SorterListener<T extends Comparable<T>> {
	
		public void sorterFinished(SorterEvent<T> e);
		
		public void sorterDataChange(SorterEvent<T> e);
}
