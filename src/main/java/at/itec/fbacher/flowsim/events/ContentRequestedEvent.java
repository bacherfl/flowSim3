package at.itec.fbacher.flowsim.events;

/**
 * Created by florian on 25.11.2015.
 */
public class ContentRequestedEvent extends ApplicationEvent{

    private String contentId;

    public ContentRequestedEvent(String contentId) {
        this.contentId = contentId;
    }

    public String getContentId() {
        return contentId;
    }
}
