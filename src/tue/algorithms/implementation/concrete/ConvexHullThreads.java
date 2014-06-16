package tue.algorithms.implementation.concrete;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import tue.algorithms.other.Debug;
import tue.algorithms.other.Pair;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Segment;

/**
 * Under development. Testing ground here.
 * @author Martijn
 */
public abstract class ConvexHullThreads {
	
	public static HashSet<Segment> getConvexHull(final Node[] input) {
		final ArrayList<Segment> allSegments = new ArrayList<Segment>();
		for (Node a : input) {
			for (Node b : input) {
				if (a.getId() < b.getId()) {
					allSegments.add(new Segment(a, b));
				}
			}
		}
		final HashSet<Segment> boundarySegments = new HashSet<Segment>();
		final Counter had = new Counter(0, allSegments.size());
		List<Pair<Integer, Integer>> xs = new ArrayList<Pair<Integer, Integer>>();
		if (allSegments.size() < 8) {
			xs.add(new Pair<Integer, Integer>(0, allSegments.size()));
		} else {
			int[] b = new int[7];
			b[0] = (int) (allSegments.size()*(1f/8));
			b[1] = (int) (allSegments.size()*(2f/8));
			b[2] = (int) (allSegments.size()*(3f/8));
			b[3] = (int) (allSegments.size()*(4f/8));
			b[4] = (int) (allSegments.size()*(5f/8));
			b[5] = (int) (allSegments.size()*(6f/8));
			b[6] = (int) (allSegments.size()*(7f/8));
			xs.add(new Pair<Integer, Integer>(0, b[0]));
			for (int i = 0; i < 6; i++) {
				xs.add(new Pair<Integer, Integer>(b[i], b[i+1]));
			}
			xs.add(new Pair<Integer, Integer>(b[6], allSegments.size()));
		}
		Thread[] ts = new Thread[xs.size()];
		int ti = 0;
		for (Pair<Integer, Integer> p : xs) {
			final int x1 = p.first();
			final int x2 = p.second();
			System.out.println("Starting runnable from " + x1 + " till " + (x2-1));
		}
		for (Pair<Integer, Integer> p : xs) {
			final int x1 = p.first();
			final int x2 = p.second();
			Runnable r = new Runnable() {
				
				@Override
				public void run() {
					for (int pos = x1; pos < x2; pos++) {
						Segment segment = allSegments.get(pos);
						boolean success = true;
						Side foundSide = null;
						for (Node n : input) {
							if (!n.equals(segment.getNode1())) {
								if (!n.equals(segment.getNode2())) {
									Side side = getSide(n, segment);
									if (side == Side.ON) {
										success = false;
										break;
									}
									if (side == Side.ON_EXTENDED) {
										continue;
									}
									if (foundSide == null) {
										foundSide = side;
									} else {
										if (side != foundSide) {
											success = false;
											break;
										}
									}
								}
							}
						}
						if (success) {
							boundarySegments.add(segment);
						}
						had.increase();
						if (had.hasNewPromille()) {
							System.out.println("Convex hull estimate promille: " + had.getPromille());
						}
					}
				}
				
			};
			Thread t = new Thread(r);
			t.start();
			ts[ti] = t;
			ti++;
		}
		for (int i = 0; i < ts.length; i++) {
			try {
				ts[i].join();
				Debug.log("Convex hull thread " + i + " finished!");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return boundarySegments;
	}
	
	public static Side getSide(Node point, Segment line) {
		if (line.getX1() == line.getX2()) {
			if (point.getX() < line.getX1()) {
				return Side.TOP_LEFT;
			} else if (point.getX() > line.getX1()) {
				return Side.BOTTOM_RIGHT;
			}
			float minLineY = Math.min(line.getY1(), line.getY2());
			float maxLineY = Math.max(line.getY1(), line.getY2());
			if (point.getY() >= minLineY && point.getY() <= maxLineY) {
				return Side.ON;
			}
			return Side.ON_EXTENDED;
		}
		float supposedY = line.getSlope()*(point.getX()-line.getX1())+line.getY1();
		if (point.getY() < supposedY) {
			return Side.TOP_LEFT;
		}
		if (point.getY() > supposedY) {
			return Side.BOTTOM_RIGHT;
		}
		float minLineY = Math.min(line.getY1(), line.getY2());
		float maxLineY = Math.max(line.getY1(), line.getY2());
		float minLineX = Math.min(line.getX1(), line.getX2());
		float maxLineX = Math.max(line.getX1(), line.getX2());
		if (point.getY() >= minLineY && point.getY() <= maxLineY && point.getX() >= minLineX && point.getX() <= maxLineX) {
			return Side.ON;
		}
		return Side.ON_EXTENDED;
	}
	
	public static enum Side {
		
		TOP_LEFT,
		BOTTOM_RIGHT,
		ON,
		ON_EXTENDED
		
	}
	
	public static class Counter {
		
		private int value;
		private int total;
		private int promille;
		private int oldPromille;
		
		public Counter(int value, int total) {
			this.value = value;
			this.promille = 0;
			this.total = total;
		}
		
		public boolean hasNewPromille() {
			return (promille > oldPromille);
		}
		
		public int getPromille() {
			return promille;
		}
		
		public void increase() {
			oldPromille = promille;
			value++;
			promille = (int) (value/((float) total)*1000);
		}
		
	}
	
}
