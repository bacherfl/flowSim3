package at.itec.fbacher.flowsim.extensions;

import at.itec.fbacher.flowsim.sim.SimulationEvent;

/**
 * Created by florian on 25.11.2015.
 */
public class PopularityStatisticsEntry {

    private final static double NOT_INITIALIZED = -1;

    /**
     * 0...t
     * 1...t-1
     * 2...t-2
     */
    private double[] popularityValues = {NOT_INITIALIZED, NOT_INITIALIZED, NOT_INITIALIZED};


    public PopularityStatisticsEntry() {

    }

    public void pushPopularityValue(double value, SimulationEvent onFinishedEntryCallback) {
        double tmp = popularityValues[0];
        double tmp2 = popularityValues[1];
        popularityValues[0] = value;
        popularityValues[1] = tmp;
        popularityValues[2] = tmp2;

        boolean finished = true;
        for (int i = 0; i < popularityValues.length; i++) {
            if (popularityValues[i] == NOT_INITIALIZED) {
                finished = false;
                continue;
            }
        }

        if (finished) {
            onFinishedEntryCallback.execute();
        }
    }

    public double getPopularityTMinus2() {
        return popularityValues[2];
    }

    public double getPopularityTMinus1() {
        return popularityValues[1];
    }


    public double getPopularityT() {
        return popularityValues[0];
    }

}
