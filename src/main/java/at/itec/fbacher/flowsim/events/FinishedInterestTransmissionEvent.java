package at.itec.fbacher.flowsim.events;

import at.itec.fbacher.flowsim.model.Link;

/**
 * Created by florian on 01.09.2015.
 */
public class FinishedInterestTransmissionEvent extends ApplicationEvent {

    private int from;
    private int to;

    public FinishedInterestTransmissionEvent(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }
}
