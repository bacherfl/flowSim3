package at.itec.fbacher.flowsim.scenarios;

import at.itec.fbacher.flowsim.extensions.app.SimpleConsumer;
import at.itec.fbacher.flowsim.extensions.app.SimpleProducer;
import at.itec.fbacher.flowsim.extensions.strategies.LearningStrategy;
import at.itec.fbacher.flowsim.model.app.App;
import at.itec.fbacher.flowsim.model.topology.NodeContainer;
import at.itec.fbacher.flowsim.model.topology.TopologyHelper;
import at.itec.fbacher.flowsim.sim.Simulator;

/**
 * Created by florian on 12.08.2015.
 */
public class Hello {

    public static void main(String[] args) {
        Simulator s = Simulator.getInstance();

        NodeContainer nc = new NodeContainer(4);
        nc.getNodes().forEach(node -> {
            App app;
            if (node.getId() == 0) {
                app = new SimpleConsumer(true, "/name", 1000000);
            } else if (node.getId() == 2) {
                app = new SimpleConsumer(true, "/name");
            } else if(node.getId() == 1) {
                app = new SimpleProducer("/name");
            }
            else {
                app = new SimpleConsumer();
            }
            if (node.getId() == 2)
                app.startAt(450);
            else
                app.startAt(1);
            node.setApp(app);
            node.setForwardingStrategy(new LearningStrategy());
        });

        TopologyHelper th = new TopologyHelper();

        th.addLink(nc.getNodes().get(0), nc.getNodes().get(1));
        //th.addLink(nc.getNodes().get(0), nc.getNodes().get(2));
        //th.addLink(nc.getNodes().get(1), nc.getNodes().get(3));
        //th.addLink(nc.getNodes().get(1), nc.getNodes().get(2));

        System.out.println("------------------------------");
        s.setOnStopApplicationCallback(() -> nc.getNodes().forEach(n -> {
            System.out.println("node " + n.getId() + "stats:");
            n.getFaces().forEach(face -> System.out.println("Face " + face.getFaceId() + ": dropped " + face.getLink().getDroppedPackets() + " packets"));
        }));

        s.setSimulationLengthInTenthMilliSeconds(40000000L);
        s.start();
    }
}
