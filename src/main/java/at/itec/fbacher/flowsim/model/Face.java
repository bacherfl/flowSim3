package at.itec.fbacher.flowsim.model;

import at.itec.fbacher.flowsim.model.app.App;

/**
 * Created by florian on 12.08.2015.
 */
public class Face {

    private int faceId;
    private long bytesSent;
    private long bytesReceived;

    private Node node;
    private App app;
    private Link link;

    public int getFaceId() {
        return faceId;
    }

    public void setFaceId(int faceId) {
        this.faceId = faceId;
    }

    public long getBytesSent() {
        return bytesSent;
    }

    public void setBytesSent(long bytesSent) {
        this.bytesSent = bytesSent;
    }

    public long getBytesReceived() {
        return bytesReceived;
    }

    public void setBytesReceived(long bytesReceived) {
        this.bytesReceived = bytesReceived;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public void receiveInterest(Interest interest) {
        node.onReceiveInterest(interest, this);
    }

    public void receiveData(Data data) {
        if (app != null)
            app.onData(data);
        else if (node != null)
            node.onReceiveData(data, this);
    }

    public void sendInterest(Interest interest) {
        link.transmitPacket(interest, this);
    }

    public void sendData(Data data) {
        link.transmitPacket(data, this);
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public void droppedPacket(Packet packet) {
        node.onDroppedPacket(packet, this);
    }

    public boolean isAppFace() {
        return faceId == 0;
    }
}
