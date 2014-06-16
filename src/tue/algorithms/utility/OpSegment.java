package tue.algorithms.utility;

public class OpSegment extends OpLine {
	
	public final OpNode node1;
	public final OpNode node2;
	
	public OpSegment(OpNode node1, OpNode node2) {
		super(node1, node2);
		this.node1 = node1;
		this.node2 = node2;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OpSegment) {
			OpSegment other = (OpSegment) obj;
			return (other.node1.id == node1.id && other.node2.id == node2.id);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return node1.id * 12000 + node2.id;
	}
	
	/**
	 * Use minimally!!!
	 */
	public Segment toSegment() {
		return new Segment(node1.id, node2.id);
	}
	
}
