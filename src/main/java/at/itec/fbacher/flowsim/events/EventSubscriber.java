package at.itec.fbacher.flowsim.events;

/**
 * Created by florian on 31.08.2015.
 */
public interface EventSubscriber {
    void handleEvent(ApplicationEvent evt);
}
