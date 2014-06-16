package tue.algorithms.utility;


public class OpNode extends OpPoint {
	
	public final int id;
	
	public OpNode(int id, float x, float y) {
		super(x, y);
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OpNode) {
			OpNode other = (OpNode) obj;
			return (other.id == id);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	/**
	 * Use minimally!!!
	 */
	public Node toNode() {
		return new Node(id, x, y);
	}
	
}
