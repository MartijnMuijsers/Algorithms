package tue.algorithms.viewer;

import tue.algorithms.implementation.concrete.MultipleCurves;
import tue.algorithms.implementation.concrete.NetworkRMST;
import tue.algorithms.implementation.concrete.SingleImploding;
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
        return new SingleImploding();
    }

    /**
     * Get an instance of the class that is chosen to solve multiple-curve
     * problem cases.
     *
     * @return An instance of a class that extends MultipleImplementation.
     */
    public static MultipleImplementation getMultipleImplementation() {
        return new MultipleCurves();
    }

    /**
     * Get an instance of the class that is chosen to solve network problem
     * cases.
     *
     * @return An instance of a class that extends NetworkImplementation.
     */
    public static NetworkImplementation getNetworkImplementation() {
        return new NetworkRMST();
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
