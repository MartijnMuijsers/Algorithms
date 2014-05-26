package tue.algorithms.implementation.concrete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import tue.algorithms.implementation.general.SingleImplementation;
import tue.algorithms.other.Conversion;
import tue.algorithms.other.Pair;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Segment;

public class SingleRails implements SingleImplementation {
	
	@Override
	public Segment[] getOutput(Node[] input) {
		HashSet<Segment> foundSegments = new HashSet<Segment>();
		HashMap<Node, Pair<Node, Node>> closest = new HashMap<Node, Pair<Node, Node>>();
		for (Node n : input) {
			Node closestNode = null;
			Node oneToClosestNode = null;
			float closestDistance = 1000000000;
			float oneToClosestDistance = 1000000000;
			for (Node m : input) {
				if (!m.equals(n)) {
					float distance = m.getDistanceTo(n);
					if (distance < closestDistance) {
						oneToClosestDistance = closestDistance;
						oneToClosestNode = closestNode;
						closestDistance = distance;
						closestNode = m;
					} else if (distance < oneToClosestDistance) {
						oneToClosestDistance = distance;
						oneToClosestNode = m;
					}
				}
			}
			closest.put(n, new Pair<Node, Node>(closestNode, oneToClosestNode));
			//foundSegments.add(new Segment(n, closestNode));
			//foundSegments.add(new Segment(n, oneToClosestNode));
		}
		HashSet<Node> nodesToDo = Conversion.toHashSet(input);
		for (Node node : input) {
			if (nodesToDo.contains(node)) {
				Pair<Node, Node> pair = closest.get(node);
				Node n1 = pair.first();
				Node n2 = pair.second();
				ArrayList<Node> l1 = new ArrayList<Node>();
				l1.add(node);
				ArrayList<Node> l2 = new ArrayList<Node>();
				l2.add(node);
				boolean a1 = true;
				boolean a2 = true;
				while (a1 || a2) {
					if (l1.get(l1.size()-1).equals(l2.get(l2.size()-1)) && (!l1.get(l1.size()-1).equals(node))) {
						break;
					}
					if (a1) {
						if (nodesToDo.contains(n1)) {
							Pair<Node, Node> thisPair = closest.get(n1);
							Node thisN1 = thisPair.first();
							Node thisN2 = thisPair.second();
							Node prevNode = l1.get(l1.size()-1);
							if (thisN1.equals(prevNode) || thisN2.equals(prevNode)) {
								Node other = (thisN1.equals(prevNode))?(thisN2):(thisN1);
								l1.add(n1);
								n1 = other;
							} else {
								a1 = false;
							}
						} else {
							a1 = false;
						}
					}
					if (l1.get(l1.size()-1).equals(l2.get(l2.size()-1)) && (!l1.get(l1.size()-1).equals(node))) {
						break;
					}
					if (a2) {
						if (nodesToDo.contains(n2)) {
							Pair<Node, Node> thisPair = closest.get(n2);
							Node thisN1 = thisPair.first();
							Node thisN2 = thisPair.second();
							Node prevNode = l2.get(l2.size()-1);
							if (thisN1.equals(prevNode) || thisN2.equals(prevNode)) {
								Node other = (thisN1.equals(prevNode))?(thisN2):(thisN1);
								l2.add(n2);
								n2 = other;
							} else {
								a2 = false;
							}
						} else {
							a2 = false;
						}
					}
				}
				int length = l1.size()+l2.size()-2;
				if (length >= 7) {
					System.out.println("length = " + length);
					for (int i = 1; i < l1.size(); i++) {
						foundSegments.add(new Segment(l1.get(i-1), l1.get(i)));
						nodesToDo.remove(l1.get(i-1));
					}
					for (int i = 1; i < l2.size(); i++) {
						foundSegments.add(new Segment(l2.get(i-1), l2.get(i)));
						nodesToDo.remove(l2.get(i-1));
					}
				}
			}
		}
		return Conversion.toArray(foundSegments, new Segment[foundSegments.size()]);
	}
}
