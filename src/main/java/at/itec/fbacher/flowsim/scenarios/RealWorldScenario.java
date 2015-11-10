package at.itec.fbacher.flowsim.scenarios;

import at.itec.fbacher.flowsim.events.EventPublisher;
import at.itec.fbacher.flowsim.events.TopologyFinishedEvent;
import at.itec.fbacher.flowsim.extensions.app.Client;
import at.itec.fbacher.flowsim.extensions.app.Producer;
import at.itec.fbacher.flowsim.extensions.app.SimpleProducer;
import at.itec.fbacher.flowsim.extensions.app.tg.SimulationSchedule;
import at.itec.fbacher.flowsim.extensions.app.tg.traffic.TrafficStatistics;
import at.itec.fbacher.flowsim.extensions.strategies.LearningStrategy;
import at.itec.fbacher.flowsim.model.Scenario;
import at.itec.fbacher.flowsim.model.topology.NodeContainer;
import at.itec.fbacher.flowsim.model.topology.TopologyHelper;
import at.itec.fbacher.flowsim.sim.Scheduler;
import at.itec.fbacher.flowsim.sim.Simulator;

/**
 * Created by florian on 05.11.2015.
 */
public class RealWorldScenario implements Scenario {
    @Override
    public void run() {
        Simulator s = Simulator.getInstance();
        s.start();
    }

    @Override
    public void initialize() {
        Simulator s = Simulator.getInstance();

        NodeContainer clients = new NodeContainer(10);
        NodeContainer routers = new NodeContainer(4);
        NodeContainer servers = new NodeContainer(3);

        clients.getNodes().forEach(clientNode -> {
            clientNode.setApp(new Client());
            clientNode.getApp().startAt(1);
            clientNode.setForwardingStrategy(new LearningStrategy());
        });

        servers.getNodes().forEach(serverNode -> {
            serverNode.setApp(new Producer());
            serverNode.getApp().startAt(1);
            serverNode.setForwardingStrategy(new LearningStrategy());
        });

        routers.getNodes().forEach(routerNode -> routerNode.setForwardingStrategy(new LearningStrategy()));


        //build topology
        TopologyHelper th = new TopologyHelper();

        clients.getNodes().forEach(clientNode -> th.addLink(clientNode, routers.getNodes().get(0)));

        th.addLink(routers.getNodes().get(0), routers.getNodes().get(1));

        servers.getNodes().forEach(serverNode -> th.addLink(serverNode, routers.getNodes().get(1)));


        //tell the simulator about the finished topology
        EventPublisher.getInstance().publishEvent(new TopologyFinishedEvent());

        s.setSimulationLengthInSeconds(3600 * 24);

        //trigger the traffic statistics component
        Scheduler.getInstance().scheduleEventInSeconds(3600, () -> TrafficStatistics.nextHour());
    }
}
