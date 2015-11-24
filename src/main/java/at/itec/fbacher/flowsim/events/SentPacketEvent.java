package at.itec.fbacher.flowsim.events;

/**
 * Created by florian on 16.11.2015.
 */
public class SentPacketEvent extends PacketEvent {
    public SentPacketEvent(int faceId, int nodeId) {
        super(faceId, nodeId);
    }
}
