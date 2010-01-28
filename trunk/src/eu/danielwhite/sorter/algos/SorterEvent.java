package eu.danielwhite.sorter.algos;

/**
 * Event object handed to a SorterListener implementation to let the know the source
 * of the SorterEvent.
 * @author dan
 *
 * @param <T> The type of data that the sorter is sorting.
 */
public class SorterEvent<T extends Comparable<T>> {
	private final Sorter<T> source;
	public SorterEvent(Sorter<T> source) {
		this.source = source;
	}
	
	/**
	 * The source of the event.
	 * @return Reference to the sorter object.
	 */
	public Sorter<T> getSource() {
		return source;
	}
}
