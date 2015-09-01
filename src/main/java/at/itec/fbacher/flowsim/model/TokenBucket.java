package at.itec.fbacher.flowsim.model;

import at.itec.fbacher.flowsim.sim.Scheduler;
import at.itec.fbacher.flowsim.sim.Simulator;

/**
 * Created by florian on 12.08.2015.
 */
public class TokenBucket {

    private static final int TOKEN_GEN_INTERVAL = 10;

    private double tokenGenRate;
    private int bandwidth;
    private double tokens = 0;

    public TokenBucket(int bandwidth) {
        this.bandwidth = bandwidth;
        tokenGenRate = (bandwidth + 0.0) / (Simulator.SIMULATION_TICKS_PER_SECOND / TOKEN_GEN_INTERVAL);
        tokens = bandwidth;
        Scheduler.getInstance().scheduleEventIn(TOKEN_GEN_INTERVAL, this::produceToken);
    }

    private void produceToken() {
        tokens = Math.min(tokens + tokenGenRate, bandwidth);
        Scheduler.getInstance().scheduleEventIn(TOKEN_GEN_INTERVAL, this::produceToken);
    }

    public boolean consumeToken(Packet packet) {
        if (tokens - (packet.getSize() * 8) > 0) {
            tokens -= (packet.getSize() * 8);
            return true;
        } else {
            return false;
        }
    }
}
