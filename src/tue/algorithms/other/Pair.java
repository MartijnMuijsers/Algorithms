package tue.algorithms.other;

/**
 * A class to provide basic pair functionality.
 * @author Martijn
 * @param <A> The type of the first element of the pair.
 * @param <B> The type of the second element of the pair.
 */
public class Pair<A, B> {
	
	/**
	 * The first element of the pair.
	 */
	private A first;
	/**
	 * The second element of the pair.
	 */
	private B second;
	
	/**
	 * Get the first element of the pair.
	 * @return The first element.
	 */
	public A first() {
		return first;
	}
	
	/**
	 * Get the second element of the pair.
	 * @return The second element.
	 */
	public B second() {
		return second;
	}
	
	/**
	 * Create a pair with two elements.
	 * @param first The first element.
	 * @param second The second element.
	 */
	public Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}
	
}
