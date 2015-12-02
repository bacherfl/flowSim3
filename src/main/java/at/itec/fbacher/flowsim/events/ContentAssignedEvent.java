package at.itec.fbacher.flowsim.events;

import at.itec.fbacher.flowsim.extensions.app.Producer;
import at.itec.fbacher.flowsim.extensions.app.tg.content.ContentInfo;

/**
 * Created by florian on 02.12.2015.
 */
public class ContentAssignedEvent extends ApplicationEvent {
    private Producer producer;
    private ContentInfo contentInfo;

    public ContentAssignedEvent(Producer producer, ContentInfo contentInfo) {
        super();
        this.producer = producer;
        this.contentInfo = contentInfo;
    }

    public Producer getProducer() {
        return producer;
    }

    public ContentInfo getContentInfo() {
        return contentInfo;
    }
}
