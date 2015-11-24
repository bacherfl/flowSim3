package at.itec.fbacher.flowsim.events;

/**
 * Created by florian on 16.11.2015.
 */
public class ReceivedDataEvent extends ReceivedPacketEvent {
    public ReceivedDataEvent(int faceId, int nodeId) {
        super(faceId, nodeId);
    }
}
