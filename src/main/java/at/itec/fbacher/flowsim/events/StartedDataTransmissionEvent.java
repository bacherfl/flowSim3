package at.itec.fbacher.flowsim.events;

/**
 * Created by florian on 02.09.2015.
 */
public class StartedDataTransmissionEvent extends ApplicationEvent {

    private String from;
    private String to;

    public StartedDataTransmissionEvent(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
}
