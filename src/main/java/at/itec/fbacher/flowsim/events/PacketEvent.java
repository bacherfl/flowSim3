package at.itec.fbacher.flowsim.events;

import at.itec.fbacher.flowsim.sim.Simulator;

/**
 * Created by florian on 16.11.2015.
 */
public class PacketEvent extends ApplicationEvent {
    protected int nodeId;
    protected int faceId;
    protected long tstamp;

    public PacketEvent(int faceId, int nodeId) {
        this.faceId = faceId;
        tstamp = Simulator.getInstance().getCurrentTime();
        this.nodeId = nodeId;
    }

    public int getNodeId() {
        return nodeId;
    }

    public int getFaceId() {
        return faceId;
    }

    public long getTstamp() {
        return tstamp;
    }
}
