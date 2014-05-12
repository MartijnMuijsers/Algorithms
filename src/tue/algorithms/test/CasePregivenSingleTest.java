package tue.algorithms.test;

import tue.algorithms.implementation.general.ProblemType;
import tue.algorithms.other.Pair;
import tue.algorithms.utility.Node;

public class CasePregivenSingleTest extends FakeInputReader {
	
	private static Node[] nodes;
	
	static {
		Node node;
		nodes = new Node[51];
		int i = -1;
		node = new Node(i, 0.04166597f, 1.00000000f); i++; nodes[i] = node;
		node = new Node(i, 0.10577014f, 1.00000000f); i++; nodes[i] = node;
		node = new Node(i, 0.16452708f, 1.00000000f); i++; nodes[i] = node;
		node = new Node(i, 0.22329097f, 1.00000000f); i++; nodes[i] = node;
		node = new Node(i, 0.30876319f, 1.00000000f); i++; nodes[i] = node;
		node = new Node(i, 0.37820764f, 1.00000000f); i++; nodes[i] = node;
		node = new Node(i, 0.47970069f, 1.00000000f); i++; nodes[i] = node;
		node = new Node(i, 0.57051319f, 1.00000000f); i++; nodes[i] = node;
		node = new Node(i, 0.63995764f, 1.00000000f); i++; nodes[i] = node;
		node = new Node(i, 0.73611042f, 1.00000000f); i++; nodes[i] = node;
		node = new Node(i, 0.80021458f, 1.00000000f); i++; nodes[i] = node;
		node = new Node(i, 0.85897153f, 1.00000000f); i++; nodes[i] = node;
		node = new Node(i, 0.97115208f, 1.00000000f); i++; nodes[i] = node;
		node = new Node(i, 0.99961042f, 0.93770833f); i++; nodes[i] = node;
		node = new Node(i, 0.97818681f, 0.82929167f); i++; nodes[i] = node;
		node = new Node(i, 0.93525625f, 0.74453472f); i++; nodes[i] = node;
		node = new Node(i, 0.88861042f, 0.68019444f); i++; nodes[i] = node;
		node = new Node(i, 0.82885347f, 0.60820833f); i++; nodes[i] = node;
		node = new Node(i, 0.73812431f, 0.50188889f); i++; nodes[i] = node;
		node = new Node(i, 0.68018681f, 0.40197917f); i++; nodes[i] = node;
		node = new Node(i, 0.72313125f, 0.25985417f); i++; nodes[i] = node;
		node = new Node(i, 0.68806875f, 0.33135417f); i++; nodes[i] = node;
		node = new Node(i, 0.76704792f, 0.18609028f); i++; nodes[i] = node;
		node = new Node(i, 0.83081181f, 0.08719444f); i++; nodes[i] = node;
		node = new Node(i, 0.86645069f, 0.03365278f); i++; nodes[i] = node;
		node = new Node(i, 0.81623542f, 0.00000000f); i++; nodes[i] = node;
		node = new Node(i, 0.74145069f, 0.00000000f); i++; nodes[i] = node;
		node = new Node(i, 0.63995764f, 0.00000000f); i++; nodes[i] = node;
		node = new Node(i, 0.53845764f, 0.00000000f); i++; nodes[i] = node;
		node = new Node(i, 0.44230486f, 0.00000000f); i++; nodes[i] = node;
		node = new Node(i, 0.37820764f, 0.00000000f); i++; nodes[i] = node;
		node = new Node(i, 0.32478403f, 0.00000000f); i++; nodes[i] = node;
		node = new Node(i, 0.23931875f, 0.00000000f); i++; nodes[i] = node;
		node = new Node(i, 0.19658264f, 0.00000000f); i++; nodes[i] = node;
		node = new Node(i, 0.13247847f, 0.00000000f); i++; nodes[i] = node;
		node = new Node(i, 0.13995764f, 0.04327083f); i++; nodes[i] = node;
		node = new Node(i, 0.16908264f, 0.08703472f); i++; nodes[i] = node;
		node = new Node(i, 0.20999236f, 0.14975694f); i++; nodes[i] = node;
		node = new Node(i, 0.25863125f, 0.22829861f); i++; nodes[i] = node;
		node = new Node(i, 0.28829097f, 0.28077778f); i++; nodes[i] = node;
		node = new Node(i, 0.31009653f, 0.32661806f); i++; nodes[i] = node;
		node = new Node(i, 0.32098542f, 0.36503472f); i++; nodes[i] = node;
		node = new Node(i, 0.30895069f, 0.43246528f); i++; nodes[i] = node;
		node = new Node(i, 0.27042292f, 0.49109028f); i++; nodes[i] = node;
		node = new Node(i, 0.22772847f, 0.54276389f); i++; nodes[i] = node;
		node = new Node(i, 0.17556181f, 0.60307639f); i++; nodes[i] = node;
		node = new Node(i, 0.11415208f, 0.67670139f); i++; nodes[i] = node;
		node = new Node(i, 0.06579792f, 0.74290972f); i++; nodes[i] = node;
		node = new Node(i, 0.02735972f, 0.81504167f); i++; nodes[i] = node;
		node = new Node(i, 0.00903750f, 0.87220833f); i++; nodes[i] = node;
		node = new Node(i, 0.00000000f, 0.95995139f); i++; nodes[i] = node;
	}
	
	@Override
	public Pair<ProblemType, Node[]> readInput() {
		return new Pair<ProblemType, Node[]>(ProblemType.SINGLE, nodes);
	}
	
}
