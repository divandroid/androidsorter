package eu.danielwhite.sorter.algos;

public class SorterEvent<T extends Comparable<T>> {
	private final Sorter<T> source;
	public SorterEvent(Sorter<T> source) {
		this.source = source;
	}
	
	public Sorter<T> getSource() {
		return source;
	}
}
