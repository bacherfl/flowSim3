package at.itec.fbacher.flowsim.events;

import at.itec.fbacher.flowsim.model.Link;

/**
 * Created by florian on 31.08.2015.
 */
public class LinkCreatedEvent extends ApplicationEvent {

    private final Link link;

    public LinkCreatedEvent(final Link link) {
        this.link = link;
    }

    public Link getLink() {
        return link;
    }
}
