package at.itec.fbacher.flowsim.events;

/**
 * Created by florian on 16.11.2015.
 */
public class SentInterestEvent extends SentPacketEvent {
    public SentInterestEvent(int faceId, int nodeId) {
        super(faceId, nodeId);
    }
}
