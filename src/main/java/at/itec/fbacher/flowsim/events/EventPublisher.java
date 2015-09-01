package at.itec.fbacher.flowsim.events;

import java.util.*;

/**
 * Created by florian on 31.08.2015.
 */
public class EventPublisher {

    private static EventPublisher instance;

    private Map<Class<? extends ApplicationEvent>, List<EventSubscriber>> eventListeners = new HashMap<>();

    private EventPublisher() {
    }

    public static EventPublisher getInstance() {
        if (instance == null)
            instance = new EventPublisher();

        return instance;
    }

    public void register(EventSubscriber observer, Class<? extends ApplicationEvent> clazz) {
        if (!eventListeners.containsKey(clazz)) {
            eventListeners.put(clazz, new ArrayList<>());
        }
        eventListeners.get(clazz).add(observer);
    }

    public void publishEvent(ApplicationEvent event) {
        if (eventListeners.containsKey(event.getClass()))
            eventListeners.get(event.getClass()).forEach(eventListener -> eventListener.handleEvent(event));
    }
}
