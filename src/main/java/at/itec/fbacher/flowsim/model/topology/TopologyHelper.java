package at.itec.fbacher.flowsim.model.topology;

import at.itec.fbacher.flowsim.events.EventPublisher;
import at.itec.fbacher.flowsim.events.LinkCreatedEvent;
import at.itec.fbacher.flowsim.model.Face;
import at.itec.fbacher.flowsim.model.Link;
import at.itec.fbacher.flowsim.model.Node;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by florian on 12.08.2015.
 */
public class TopologyHelper {

    private List<Link> links = new ArrayList<>();

    public TopologyHelper() {
    }

    public void addLink(Node n1, Node n2) {
        Face f1 = new Face();
        Face f2 = new Face();
        Link link = new Link(f1, f2);
        n1.addFace(f1);
        n2.addFace(f2);
        links.add(link);
        EventPublisher.getInstance().publishEvent(new LinkCreatedEvent(link));
    }

    public void addLink(Node n1, Node n2, int bandwidth, int delay, double reliability) {
        Face f1 = new Face();
        Face f2 = new Face();
        Link link = new Link(f1, f2, bandwidth, delay, reliability);
        n1.addFace(f1);
        n2.addFace(f2);
        links.add(link);
        EventPublisher.getInstance().publishEvent(new LinkCreatedEvent(link));
    }
}
