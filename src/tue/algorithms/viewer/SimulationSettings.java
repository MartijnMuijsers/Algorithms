package tue.algorithms.viewer;

import tue.algorithms.implementation.concrete.RMST;
import tue.algorithms.implementation.concrete.SingleImplodingTryingFaster;
import tue.algorithms.implementation.concrete.SingleMultipleConvexHull;
import tue.algorithms.implementation.general.MultipleImplementation;
import tue.algorithms.implementation.general.NetworkImplementation;
import tue.algorithms.implementation.general.ProblemType;
import tue.algorithms.implementation.general.SingleImplementation;
import tue.algorithms.test.CaseEmpty;
import tue.algorithms.test.FakeInputReader;

/**
 * Settings for the simulation.
 * 
 * @author Martijn
 */
public abstract class SimulationSettings {
	
	/**
	 * Get the problem type that the simulation wll start with.
	 * 
	 * @return A problem type.
	 */
	public static ProblemType getInitialProblemType() {
		return ProblemType.SINGLE;
	}
	
	/**
     * Get an instance of the class that is chosen to solve single-curve problem
     * cases.
     *
     * @return An instance of a class that extends SingleImplementation.
     */
    public static SingleImplementation getSingleImplementation() {
        return new SingleImplodingTryingFaster();
    }

    /**
     * Get an instance of the class that is chosen to solve multiple-curve
     * problem cases.
     *
     * @return An instance of a class that extends MultipleImplementation.
     */
    public static MultipleImplementation getMultipleImplementation() {
        return new SingleMultipleConvexHull();
    }

    /**
     * Get an instance of the class that is chosen to solve network problem
     * cases.
     *
     * @return An instance of a class that extends NetworkImplementation.
     */
    public static NetworkImplementation getNetworkImplementation() {
        return new RMST();
    }
	
	/**
     * Get an instance of the class that is chosen to provide input.
     *
     * @return An instance of a class that extends FakeInputReader.
     */
    public static FakeInputReader getFakeInputReader() {
        return new CaseEmpty();
    }
	
}
