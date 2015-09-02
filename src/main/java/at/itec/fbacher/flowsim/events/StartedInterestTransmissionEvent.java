package at.itec.fbacher.flowsim.events;

/**
 * Created by florian on 01.09.2015.
 */
public class StartedInterestTransmissionEvent extends ApplicationEvent {

    private String from;
    private String to;

    public StartedInterestTransmissionEvent(String from, String to) {
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
