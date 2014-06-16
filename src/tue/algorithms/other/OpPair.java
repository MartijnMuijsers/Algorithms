package tue.algorithms.other;

public class OpPair<A, B> {
	
	public A first;
	public B second;
	private int hashCode;
	
	public OpPair(A first, B second) {
		this.first = first;
		this.second = second;
		int sh = second.hashCode()+1001;
		hashCode = first.hashCode()+sh*sh;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OpPair<?, ?>) {
			OpPair<?, ?> other = (OpPair<?, ?>) obj;
			return (other.first.equals(first) && other.second.equals(second));
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
}
