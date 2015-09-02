package at.itec.fbacher.flowsim.events;

/**
 * Created by florian on 02.09.2015.
 */
public class PacketDroppedEvent extends ApplicationEvent {

    private final int node;
    private final int faceId;

    public PacketDroppedEvent(int node, int faceId) {
        this.node = node;
        this.faceId = faceId;
    }

    public int getNode() {
        return node;
    }

    public int getFaceId() {
        return faceId;
    }
}
