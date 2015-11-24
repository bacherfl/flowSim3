package at.itec.fbacher.flowsim.events;

/**
 * Created by florian on 16.11.2015.
 */
public class ReceivedPacketEvent extends PacketEvent {
    public ReceivedPacketEvent(int faceId, int nodeId) {
        super(faceId, nodeId);
    }
}
