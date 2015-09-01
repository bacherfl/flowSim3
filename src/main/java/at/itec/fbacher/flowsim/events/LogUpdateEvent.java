package at.itec.fbacher.flowsim.events;

/**
 * Created by florian on 31.08.2015.
 */
public class LogUpdateEvent extends ApplicationEvent {

    private String message;

    public LogUpdateEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
