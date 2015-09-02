package at.itec.fbacher.flowsim.events;

/**
 * Created by florian on 02.09.2015.
 */
public class FinishedDataTransmissionEvent extends ApplicationEvent {

    private int from;
    private int to;

    public FinishedDataTransmissionEvent(int from, int to) {
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
