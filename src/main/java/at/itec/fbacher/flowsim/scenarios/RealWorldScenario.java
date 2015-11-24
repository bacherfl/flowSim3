package at.itec.fbacher.flowsim.scenarios;

import at.itec.fbacher.flowsim.events.*;
import at.itec.fbacher.flowsim.extensions.app.Client;
import at.itec.fbacher.flowsim.extensions.app.Producer;
import at.itec.fbacher.flowsim.extensions.app.SimpleProducer;
import at.itec.fbacher.flowsim.extensions.app.tg.ContentRepositoryManager;
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
public class RealWorldScenario implements Scenario, EventSubscriber {

    long startTime;

    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        Simulator s = Simulator.getInstance();
        s.start();
    }

    @Override
    public void initialize() {
        Simulator s = Simulator.getInstance();

        NodeContainer clients =     new NodeContainer(10);
        NodeContainer clients2 =    new NodeContainer(15);
        NodeContainer routers =     new NodeContainer(10);
        NodeContainer servers =     new NodeContainer(10);

        clients.getNodes().forEach(clientNode -> {
            Client client = new Client();
            client.setBandwidth(10 * 1024);
            clientNode.setApp(client);
            clientNode.getApp().startAt(1);
            clientNode.setForwardingStrategy(new LearningStrategy());
        });

        clients2.getNodes().forEach(clientNode -> {
            Client client = new Client();
            client.setBandwidth(50 * 1024);
            clientNode.setApp(client);
            clientNode.getApp().startAt(1);
            clientNode.setForwardingStrategy(new LearningStrategy());
        });

        ContentRepositoryManager repositoryManager = new ContentRepositoryManager();

        servers.getNodes().forEach(serverNode -> {
            serverNode.setApp(new Producer());
            serverNode.getApp().startAt(1);
            serverNode.setForwardingStrategy(new LearningStrategy());
            repositoryManager.getProducers().add((Producer) serverNode.getApp());
        });

        repositoryManager.assignContentItems();

        routers.getNodes().forEach(routerNode -> routerNode.setForwardingStrategy(new LearningStrategy()));


        //build topology
        TopologyHelper th = new TopologyHelper();

        clients.getNodes().forEach(clientNode -> th.addLink(clientNode, routers.getNodes().get((int) (Math.random() * routers.getNodes().size()))));
        clients2.getNodes().forEach(clientNode -> th.addLink(clientNode, routers.getNodes().get((int) (Math.random() * routers.getNodes().size()))));

        //random node connections
        clients.getNodes().forEach(client -> {
                    if (Math.random() > 0.5) {
                        th.addLink(client, clients2.getNodes().get((int) (Math.random() * clients2.getNodes().size())));
                    }
                }
        );

        th.addLink(routers.getNodes().get(0), routers.getNodes().get(1));
        th.addLink(routers.getNodes().get(0), routers.getNodes().get(2));
        th.addLink(routers.getNodes().get(2), routers.getNodes().get(1));

        servers.getNodes().forEach(serverNode -> th.addLink(
                serverNode,
                routers.getNodes().get((int) (Math.random() * routers.getNodes().size()))));

        routers.getNodes().forEach(router -> th.addLink(
                router,
                routers.getNodes().get((int) (Math.random() * routers.getNodes().size()))));


        //tell the simulator about the finished topology
        EventPublisher.getInstance().publishEvent(new TopologyFinishedEvent());

        s.setSimulationLengthInSeconds(3600);

        EventPublisher.getInstance().register(this, SimulationFinishedEvent.class);

        //trigger the traffic statistics component
        Scheduler.getInstance().scheduleEventInSeconds(30, () -> TrafficStatistics.nextHour());
    }

    @Override
    public void handleEvent(ApplicationEvent evt) {
        long duration = (System.currentTimeMillis() - startTime) / 1000;
        System.out.println("Simulation duration: " + duration + "s");
    }
}
