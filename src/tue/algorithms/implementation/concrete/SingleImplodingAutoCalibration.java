package tue.algorithms.implementation.concrete;

import java.util.HashSet;

import tue.algorithms.implementation.concrete.SingleImploding.CurveType;
import tue.algorithms.implementation.general.SingleImplementation;
import tue.algorithms.other.Conversion;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Segment;

public class SingleImplodingAutoCalibration implements SingleImplementation {
	
	/* -- START Parameters -- */
	
	public final float[] factors = {
			1.0f,
			3.0f,
			5.0f,
			8.0f,
			15.0f,
	};
	
	public final boolean stopAfterNumberOfBestScores = false;
	public final int numberOfBestScoresToStopAfter = 50;
	
	/* -- END Parameters -- */
	
	public class SingleImplodingCalibratable extends SingleImploding {
	
		/** Where x = distance impact, and y = angle impact (expected), and z = angle impact (to each other), and d = total distance impact **/
		@Override
		public float likelihoodFormula(float x, float y, float z, float d) {
			//return 1.0f*x+1.0f*y+5f*z+8f*d;//werkt voor bla
			//return 1.0f*x+3.0f*y+3.0f*z+15.0f*d;//werkt voor bla EN tree
			//return 1.0f*x+5.0f*y+3.0f*z+15.0f*d;//werkt voor M
			return getDistanceImpactFactor()*x+getAngleDifferenceToExpectedImpactFactor()*y+getAngleDifferenceToEachOtherImpactFactor()*z+getTotalDistanceImpactFactor()*d;
		}
		
		public float getDistanceImpactFactor() {
			return distanceImpactFactor;
		}
		
		public float getAngleDifferenceToExpectedImpactFactor() {
			return angleDifferenceToExpectedImpactFactor;
		}
		
		public float getAngleDifferenceToEachOtherImpactFactor( ){
			return angleDifferenceToEachOtherImpactFactor;
		}
		
		public float getTotalDistanceImpactFactor() {
			return totalDistanceImpactFactor;
		}
		
		private float distanceImpactFactor;
		private float angleDifferenceToExpectedImpactFactor;
		private float angleDifferenceToEachOtherImpactFactor;
		private float totalDistanceImpactFactor;
		
		public SingleImplodingCalibratable(float distanceImpactFactor, float angleDifferenceToExpectedImpactFactor, float angleDifferenceToEachOtherImpactFactor, float totalDistanceImpactFactor) {
			super();
			this.distanceImpactFactor = distanceImpactFactor;
			this.angleDifferenceToExpectedImpactFactor = angleDifferenceToExpectedImpactFactor;
			this.angleDifferenceToEachOtherImpactFactor = angleDifferenceToEachOtherImpactFactor;
			this.totalDistanceImpactFactor = totalDistanceImpactFactor;
		}
		
		/** Disables Mozes **/
		public SingleImplodingCalibratable(boolean thisMustBeFalse) {
			super();
			if (thisMustBeFalse) {
				throw new IllegalArgumentException("Boolean in SingleImplodingCalibratable constructor must be false to disable Mozes!");
			}
			mozesEnabled = false;
		}
		
	}
	
	@Override
	public Segment[] getOutput(Node[] input) {
		HashSet<Segment> convexHull = ConvexHull.getConvexHull(input);
		Segment[] rmst = new RMST().getOutput(input).first();
		System.out.println(input.length + " " + rmst.length);
		int todo = factors.length*factors.length*factors.length*factors.length;
		int prevPerc = 0;
		int had = 0;
		SingleImplodingCalibratable singleImplodingCalibratableWithoutMozes = new SingleImplodingCalibratable(false);
		Segment[] bestTrialOutput = singleImplodingCalibratableWithoutMozes.getOutput(input, convexHull);
		float bestScore = getScore(bestTrialOutput, singleImplodingCalibratableWithoutMozes);
		int bestScoreOccurences = 1;
		HashSet<String> goodCalibrationParameters = new HashSet<String>();
		goodCalibrationParameters.add("noMozes");
		for (float distanceImpactFactor : factors) {
			for (float angleDifferenceToExpectedImpactFactor : factors) {
				for (float angleDifferenceToEachOtherImpactFactor : factors) {
					for (float totalDistanceImpactFactor : factors) {
						SingleImplodingCalibratable singleImplodingCalibratable = new SingleImplodingCalibratable(distanceImpactFactor, angleDifferenceToExpectedImpactFactor, angleDifferenceToEachOtherImpactFactor, totalDistanceImpactFactor);
						had++;
						int perc = (int) (((double) had)/todo*100);
						if (perc != prevPerc) {
							System.out.println(perc + " %");
						}
						prevPerc = perc;
						Segment[] trialOutput = singleImplodingCalibratable.getOutput(input, convexHull);
						HashSet<Segment> trialOutputSet = Conversion.toHashSet(trialOutput);
						boolean hasRMST = true;
						for (Segment segment : rmst) {
							if (!(trialOutputSet.contains(segment) || trialOutputSet.contains(segment.invertDirection()))) {
								hasRMST = false;
								break;
							}
						}
						if (hasRMST) {
							//return trialOutput;//TODO uncomment, just for calibration testing was commented
						}
						float score = getScore(trialOutput, singleImplodingCalibratable);
						System.out.println("Score: " + score + " (factors " + distanceImpactFactor + " & " + angleDifferenceToExpectedImpactFactor + " & " + angleDifferenceToEachOtherImpactFactor + " & " + totalDistanceImpactFactor + " )");
						if (score < bestScore) {
							bestScore = score;
							bestTrialOutput = trialOutput;
							bestScoreOccurences = 1;
							goodCalibrationParameters.clear();
							goodCalibrationParameters.add(distanceImpactFactor + " " + angleDifferenceToExpectedImpactFactor + " " + angleDifferenceToEachOtherImpactFactor + " " + totalDistanceImpactFactor);
						} else if (score == bestScore) {
							if (stopAfterNumberOfBestScores) {
								bestScoreOccurences++;
								if (bestScoreOccurences >= numberOfBestScoresToStopAfter) {
									return bestTrialOutput;
								}
							}
							goodCalibrationParameters.add(distanceImpactFactor + " " + angleDifferenceToExpectedImpactFactor + " " + angleDifferenceToEachOtherImpactFactor + " " + totalDistanceImpactFactor);
						}
					}
				}
			}
		}
		System.out.println("Auto calibration successfully terminated");
		System.out.println("Good calibration parameters:");
		for (String goodCalibrationParameter : goodCalibrationParameters) {
			System.out.println(goodCalibrationParameter);
		}
		return bestTrialOutput;
	}
	
	public float getScore(Segment[] output, SingleImplodingCalibratable producer) {
		if (producer.type == CurveType.CLOSED) {
			float totalLength = 0;
			for (Segment segment : output) {
				totalLength += segment.length();
			}
			return totalLength;
		}
		float totalLength = 0;
		float maxLength = 0;
		for (Segment segment : output) {
			float length = segment.length();
			totalLength += length;
			if (length > maxLength) {
				maxLength = length;
			}
		}
		totalLength += maxLength;
		return totalLength;
	}
	
}