package tue.algorithms.peach;


import java.util.Scanner;

import tue.algorithms.implementation.general.ProblemType;
import tue.algorithms.other.Pair;
import tue.algorithms.utility.Node;

/**
 * <p>
 * Class that reads Peach input.
 * </p>
 * @author Martijn
 */
public abstract class PeachInputReader {
	
	/**
	 * Reads the Peach input from System.in and returns a problem description.
	 * @return The problem description as a pair of the type and the given nodes.
	 */
	public static Pair<ProblemType, Node[]> readInput() {
		Scanner scanner = new Scanner(System.in);
		String line = scanner.nextLine();
		ProblemType problemType = ProblemType.valueOf(line.substring(12).toUpperCase());
		line = scanner.nextLine();
		int numberOfNodes = Integer.parseInt(line.substring(0, line.indexOf(' ')));
		Node[] nodes = new Node[numberOfNodes];
		for (int i = 0; i < numberOfNodes; i++) {
			nodes[i] = new Node(scanner.nextInt(), scanner.nextFloat(), scanner.nextFloat());
		}
		scanner.close();
		return new Pair<ProblemType, Node[]>(problemType, nodes);
	}
	
}
