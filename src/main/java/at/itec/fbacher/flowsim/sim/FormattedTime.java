package at.itec.fbacher.flowsim.sim;

/**
 * Created by florian on 01.09.2015.
 */
public class FormattedTime {
    private long seconds;
    private double milliSeconds;

    public long getSeconds() {
        return seconds;
    }

    public double getMilliSeconds() {
        return milliSeconds;
    }

    public FormattedTime invoke() {
        long currentTime = Simulator.getInstance().getCurrentTime();
        seconds = currentTime / Simulator.SIMULATION_TICKS_PER_SECOND;
        milliSeconds = (((currentTime % Simulator.SIMULATION_TICKS_PER_SECOND) * 1000) + 0.0) / Simulator.SIMULATION_TICKS_PER_SECOND;
        return this;
    }
}
