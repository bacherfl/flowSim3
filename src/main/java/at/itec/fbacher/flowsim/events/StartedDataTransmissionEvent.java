package at.itec.fbacher.flowsim.events;

/**
 * Created by florian on 02.09.2015.
 */
public class StartedDataTransmissionEvent extends ApplicationEvent {

    private int from;
    private int to;

    public StartedDataTransmissionEvent(int from, int to) {
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
