package at.itec.fbacher.flowsim.model;

import at.itec.fbacher.flowsim.log.Logger;
import at.itec.fbacher.flowsim.model.app.App;
import at.itec.fbacher.flowsim.model.cs.ContentStore;
import at.itec.fbacher.flowsim.model.fw.ForwardingStrategy;
import at.itec.fbacher.flowsim.model.pit.PendingInterestTable;
import at.itec.fbacher.flowsim.model.pit.PitEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by florian on 12.08.2015.
 */
public class Node {

    private static int nextNodeId = 0;
    private int nextFaceId = 0;

    private String id;

    private List<Face> faces = new ArrayList<>();

    private App app;
    private ForwardingStrategy forwardingStrategy;
    private ContentStore contentStore = new ContentStore();
    private PendingInterestTable pit = new PendingInterestTable();

    public Node() {
        id = "" + nextNodeId++;
        pit.setNode(this);
        Face appFace = new Face();
        addFace(appFace);
    }

    public Node(String id) {
        this.id = id;
        pit.setNode(this);
        Face appFace = new Face();
        addFace(appFace);
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
        this.app.setNode(this);
        new Link(getAppFace(), app.getAppFace(), -1, 0, 1.0);
    }

    public ForwardingStrategy getForwardingStrategy() {
        return forwardingStrategy;
    }

    public void setForwardingStrategy(ForwardingStrategy forwardingStrategy) {
        this.forwardingStrategy = forwardingStrategy;
        this.forwardingStrategy.setNode(this);
        faces.forEach(forwardingStrategy::addFace);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Face> getFaces() {
        return faces;
    }

    public void setFaces(List<Face> faces) {
        this.faces = faces;
    }

    public synchronized void onReceiveInterest(Interest interest, Face inFace) {
        //Logger.getInstance().log("Node " + getId() + " received Interest " + interest.getName());
        PitEntry pitEntry = pit.addPitEntry(interest, inFace);
        if (pitEntry == null) {
            return;
        }

        Data data = contentStore.getItem(interest);
        if (data != null) {
            sendOutData(data);
            return;
        }
        forwardingStrategy.onInterest(interest, inFace, pitEntry);
    }

    public synchronized void onReceiveData(Data data, Face inFace) {
        Logger.getInstance().log("Node " + getId() + " received Data " + data.getName());
        contentStore.onData(data);
        PitEntry pitEntry = pit.getPit().get(data.getName());
        if (pitEntry != null) {
            forwardingStrategy.onData(data, inFace, pitEntry);
            sendOutData(data);
        }
    }

    private synchronized void sendOutData(Data data) {
        pit.getPit().get(data.getName()).getInRecords().keySet().forEach(face -> face.sendData(data));
        pit.clearPitEntry(data.getName());
    }

    public void addFace(Face face) {
        face.setFaceId(nextFaceId++);
        face.setNode(this);
        faces.add(face);
        if (forwardingStrategy != null)
            forwardingStrategy.addFace(face);
    }

    public synchronized void sendInterest(Interest interest, Face outFace) {
        if (pit.getPit().get(interest.getName()) != null) { //check if interest has been satisfied
            pit.sentInterest(interest, outFace);
            if (outFace.isAppFace()) {
                if (app != null)
                    app.onInterest(interest);
            } else {
                outFace.sendInterest(interest);
            }
        }
    }

    public synchronized void sendData(Data data, Face outFace) {
        outFace.sendData(data);
    }

    public void onDroppedPacket(Packet packet, Face face) {
        System.out.println("Dropped packet");
        forwardingStrategy.onDroppedPacket(packet, face);
    }

    public void onTimedOutInterest(Interest interest, PitEntry pitEntry) {
        forwardingStrategy.onTimedOutInterest(interest, pitEntry);
    }

    public Face getAppFace() {
        return faces.stream().filter(face -> face.getFaceId() == 0).findFirst().get();
    }
}
