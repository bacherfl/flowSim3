package at.itec.fbacher.flowsim.model.fw;

import at.itec.fbacher.flowsim.model.*;
import at.itec.fbacher.flowsim.model.pit.PitEntry;

/**
 * Created by florian on 13.08.2015.
 */
public abstract class ForwardingStrategy {

    protected Node node;
    protected ForwardingInformationBase fib = new ForwardingInformationBase();

    public ForwardingStrategy() {

    }

    public ForwardingStrategy(Node node) {
        this.node = node;
    }

    public void sendInterest(Interest interest, Face outFace) {
        node.sendInterest(interest, outFace);
    }

    public void sendData(Data data, Face outFace) {
        node.sendData(data, outFace);
    }

    public abstract void onInterest(Interest interest, Face inFace, PitEntry pitEntry);
    public abstract void onData(Data data, Face inFace, PitEntry pitEntry);
    public abstract void onDroppedPacket(Packet packet, Face face);

    public abstract void onTimedOutInterest(Interest interest, PitEntry pitEntry);

    public void addFace(Face face) {
        fib.getFaces().add(face);
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }
}
