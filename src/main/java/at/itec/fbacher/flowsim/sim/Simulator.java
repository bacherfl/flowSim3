package at.itec.fbacher.flowsim.sim;


import at.itec.fbacher.flowsim.events.EventPublisher;
import at.itec.fbacher.flowsim.events.SimulationProceededEvent;

import java.util.Observable;

/**
 * Created by florian on 12.08.2015.
 */
public class Simulator extends Observable {

    public static final int SIMULATION_TICKS_PER_SECOND = 10000;
    public static final int SLEEP_INTERVAL = 10000;   //evaluate if waiting is necessary every 1000 ticks

    long lastSleepCheckpoint;

    private long currentTime = 0L;
    private boolean running = false;
    private long simulationLength;  //1 tick := 0.1ms


    private double speedupFactor = 1.0; //10 times faster than realtime

    private static Simulator instance;

    private OnStopApplicationCallback onStopApplicationCallback;

    private Simulator() {

    }

    public void setSpeedupFactor(double speedupFactor) {
        this.speedupFactor = speedupFactor;
    }

    public void start() {
        running = true;
        lastSleepCheckpoint = System.currentTimeMillis();
        proceed();
    }

    public void stop() {
        running = false;
        if (onStopApplicationCallback != null)
            onStopApplicationCallback.execute();
    }

    public void setOnStopApplicationCallback(OnStopApplicationCallback onStopApplicationCallback) {
        this.onStopApplicationCallback = onStopApplicationCallback;
    }

    public void proceed() {
        do {
            currentTime++;
            EventPublisher.getInstance().publishEvent(new SimulationProceededEvent());
            setChanged();
            notifyObservers();
            if (currentTime % SLEEP_INTERVAL == 0) {
                sleep();
            }

            if (simulationLength <= currentTime)
                stop();
        } while (running);
    }

    private void sleep() {
        long currentTime = System.currentTimeMillis();
        long actualTimePassed = currentTime - lastSleepCheckpoint;  //milliseconds that have passed since last checkpoint
        double targetTime = (SLEEP_INTERVAL / 10) / speedupFactor;
        if (actualTimePassed < targetTime) {
            double difference = targetTime - actualTimePassed;
            try {
                Thread.sleep((long) difference);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        lastSleepCheckpoint = currentTime;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public long getSimulationLengthInSeconds() {
        return simulationLength / SIMULATION_TICKS_PER_SECOND;
    }

    public void setSimulationLengthInSeconds(long simulationLengthInSeconds) {
        this.simulationLength = simulationLengthInSeconds * SIMULATION_TICKS_PER_SECOND;
    }

    public static Simulator getInstance() {
        if (instance == null) {
            instance = new Simulator();
            instance.addObserver(Scheduler.getInstance());
        }
        return instance;
    }

    public void setSimulationLengthInTenthMilliSeconds(Long simulationLength) {
        this.simulationLength = simulationLength;
    }

    public FormattedTime getCurrentTimeFormatted() {
        return new FormattedTime().invoke();
    }

    public double getSpeedupFactor() {
        return speedupFactor;
    }
}
