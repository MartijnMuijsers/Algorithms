package tue.algorithms.other;


/**
 * A class to provide basic pair functionality.
 * @author Martijn
 * @param <A> The type of the first element of the pair.
 * @param <B> The type of the second element of the pair.
 */
public class Pair<A, B> {
	
	/* -- START Private final fields -- */
	
	/**
	 * The first element of the pair.
	 */
	private A first;
	/**
	 * The second element of the pair.
	 */
	private B second;
	
	/* -- END Private final fields -- */
	
	/* -- START Constructors -- */
	
	/**
	 * Create a pair with two elements.
	 * @param first The first element.
	 * @param second The second element.
	 */
	public Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}
	
	/* -- END Constructors -- */
	
	/* -- START Public getters for private fields -- */
	
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
	
	/* -- END Public getters for private fields -- */
	
	/* -- START Override equals(), hashCode() and toString() -- */
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pair<?, ?>) {
			Pair<?, ?> other = (Pair<?, ?>) obj;
			return (other.first().equals(first()) && other.second().equals(second()));
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return first().hashCode()
			+ 101
			+ second().hashCode() * second().hashCode();
	}
	
	@Override
	public String toString() {
		return super.toString() + "["
			+ "first=" + first() + ", "
			+ "second=" + second()
			+ "]";
	}
	
	/* -- END Override equals(), hashCode() and toString() -- */
	
}
