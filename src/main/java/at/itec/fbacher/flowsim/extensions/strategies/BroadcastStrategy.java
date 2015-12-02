package at.itec.fbacher.flowsim.extensions.strategies;

import at.itec.fbacher.flowsim.model.*;
import at.itec.fbacher.flowsim.model.fw.ForwardingStrategy;
import at.itec.fbacher.flowsim.model.pit.PitEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by florian on 13.08.2015.
 */
public class BroadcastStrategy extends ForwardingStrategy {

    public BroadcastStrategy() {
        super();
    }

    public BroadcastStrategy(Node node) {
        super(node);
    }

    @Override
    public void onInterest(Interest interest, Face inFace, PitEntry pitEntry) {
        List<Face> outFaces = new ArrayList<>();
        fib.getFaces().forEach(face -> {
            if(!pitEntry.getInRecords().keySet().contains(face)) {
                outFaces.add(face);
            }
        });
        outFaces.forEach(outFace -> node.sendInterest(interest, outFace));
    }

    @Override
    public void onData(Data data, Face inFace, PitEntry pitEntry) {

    }

    @Override
    public void onDroppedPacket(Packet packet, Face face) {

    }

    @Override
    public void onTimedOutInterest(Interest interest, PitEntry pitEntry) {

    }
}
