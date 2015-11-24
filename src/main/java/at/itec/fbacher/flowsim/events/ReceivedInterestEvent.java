package at.itec.fbacher.flowsim.events;

import at.itec.fbacher.flowsim.sim.Simulator;

/**
 * Created by florian on 16.11.2015.
 */
public class ReceivedInterestEvent extends ReceivedPacketEvent {


    public ReceivedInterestEvent(int nodeId, int faceId) {
        super(faceId, nodeId);
    }


}
