package tue.algorithms.viewer;

import tue.algorithms.implementation.concrete.NetworkRMST;
import tue.algorithms.implementation.general.MultipleImplementation;
import tue.algorithms.implementation.general.NetworkImplementation;
import tue.algorithms.implementation.general.SingleImplementation;
import tue.algorithms.test.FakeInputReader;

public abstract class SimulationSettings {
	
	/**
     * Get an instance of the class that is chosen to provide input.
     *
     * @return An instance of a class that extends FakeInputReader.
     */
    public static FakeInputReader getFakeInputReader() {
        /* TODO Choose an implementation */
        //throw new UnsupportedOperationException("getFakeInputReader() not implemented.");
        return new tue.algorithms.test.CaseEmpty();
    }

    /**
     * Get an instance of the class that is chosen to solve single-curve problem
     * cases.
     *
     * @return An instance of a class that extends SingleImplementation.
     */
    public static SingleImplementation getSingleImplementation() {
        /* TODO Choose an implementation */
        //throw new UnsupportedOperationException("getSingleImplementation() not implemented.");
        return new tue.algorithms.implementation.concrete.SingleSpider();
    }

    /**
     * Get an instance of the class that is chosen to solve multiple-curve
     * problem cases.
     *
     * @return An instance of a class that extends MultipleImplementation.
     */
    public static MultipleImplementation getMultipleImplementation() {
        /* TODO Choose an implementation */
        throw new UnsupportedOperationException("getMultipleImplementation() not implemented.");
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
	
}
