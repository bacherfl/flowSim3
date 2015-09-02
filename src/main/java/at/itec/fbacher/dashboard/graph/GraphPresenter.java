package at.itec.fbacher.dashboard.graph;

import at.itec.fbacher.flowsim.events.*;
import at.itec.fbacher.flowsim.model.Data;
import at.itec.fbacher.flowsim.model.Interest;
import at.itec.fbacher.flowsim.sim.Simulator;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by florian on 28.08.2015.
 */
public class GraphPresenter implements Initializable, EventSubscriber {

    @FXML
    WebView web;

    List<GraphNode> graphNodes = new ArrayList<>();
    List<GraphEdge> graphEdges = new ArrayList<>();

    private Map<String, AtomicInteger> sentInterests = new HashMap<>();
    private Map<String, AtomicInteger> sentData = new HashMap<>();
    private boolean simulationRunning = false;
    private Task<Void> updateGraphTask;
    private Thread updateGraphThread;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EventPublisher.getInstance().register(this, LinkCreatedEvent.class);
        EventPublisher.getInstance().register(this, TopologyFinishedEvent.class);
        EventPublisher.getInstance().register(this, ScenarioSelectedEvent.class);
        EventPublisher.getInstance().register(this, ScenarioLoadedEvent.class);
        EventPublisher.getInstance().register(this, StartedInterestTransmissionEvent.class);
        EventPublisher.getInstance().register(this, FinishedInterestTransmissionEvent.class);
        EventPublisher.getInstance().register(this, StartedDataTransmissionEvent.class);
        EventPublisher.getInstance().register(this, FinishedDataTransmissionEvent.class);
        EventPublisher.getInstance().register(this, ScenarioStartedEvent.class);
        WebEngine webEngine = web.getEngine();
        String resource = getClass().getClassLoader().getResource("graph.html").toExternalForm();
        webEngine.load(resource);
        JSObject jsobj = (JSObject) webEngine.executeScript("window");
        jsobj.setMember("java", new JSBridge());
        /*
        File f = new File("graph.html");
        try {
            webEngine.load(f.toURI().toURL().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        */
    }

    @Override
    public void handleEvent(ApplicationEvent evt) {
        if (evt instanceof LinkCreatedEvent) {
            handleLinkCreatedEvent((LinkCreatedEvent) evt);
        }
        else if (evt instanceof TopologyFinishedEvent) {
            handleTopologyFinishedEvent();
        }
        else if (evt instanceof ScenarioSelectedEvent) {
            handleScenarioSelectedEvent();
        }
        else if (evt instanceof StartedInterestTransmissionEvent) {
            handleStartedInterestTransmissionEvent((StartedInterestTransmissionEvent) evt);
        }
        else if (evt instanceof FinishedInterestTransmissionEvent) {
            handleFinishedInterestTransmissionEvent((FinishedInterestTransmissionEvent) evt);
        }
        else if (evt instanceof StartedDataTransmissionEvent) {
            handleStartedDataTransmissionEvent((StartedDataTransmissionEvent) evt);
        }
        else if (evt instanceof FinishedDataTransmissionEvent) {
            handleFinishedDataTransmissionEvent((FinishedDataTransmissionEvent) evt);
        }
        else if (evt instanceof PacketDroppedEvent) {
            handlePacketDroppedEvent((PacketDroppedEvent) evt);
        }
        else if (evt instanceof ScenarioStartedEvent) {
            handleScenarioStartedEvent();
        }
    }

    private void handlePacketDroppedEvent(PacketDroppedEvent evt) {
    }

    private void handleFinishedDataTransmissionEvent(FinishedDataTransmissionEvent evt) {

    }

    private void handleStartedDataTransmissionEvent(StartedDataTransmissionEvent evt) {
        String id = new StringBuilder().append(evt.getFrom()).append("-").append(evt.getTo()).toString();
        sentData.get(id).getAndIncrement();
    }

    private void handleScenarioStartedEvent() {
        simulationRunning = true;
        updateGraphTask = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                while (simulationRunning) {
                    Platform.runLater(() -> {

                        updateBandwidthOnGraphEdges();

                    });
                    Thread.sleep(200);
                }
                return null;
            }
        };
        updateGraphThread = new Thread(updateGraphTask);
        updateGraphThread.setDaemon(true);
        updateGraphThread.start();
    }

    private void updateBandwidthOnGraphEdges() {
        sentInterests.keySet().forEach(id -> {
            String[] split = id.split("-");

            double dataBandwidth = (sentData.get(id).get() * 5 * Data.DATA_SIZE * 8) /
                    Simulator.getInstance().getSpeedupFactor();
            double interestBandwidth = (sentInterests.get(id).get() * 5 * Interest.INTEREST_SIZE * 8) /
                    Simulator.getInstance().getSpeedupFactor();

            double bandwidth = dataBandwidth + interestBandwidth;

            double scaleFactor = (bandwidth + 0.0) / (1000000);

            String jsCmd = "updateInterests(" + split[0] + ", " + split[1] + ", " + scaleFactor + ")";
            web.getEngine().executeScript(jsCmd);
            sentInterests.get(id).set(0);
            sentData.get(id).set(0);
        });
    }

    private void updateDroppedPacketsOnNodes() {

    }

    private void handleFinishedInterestTransmissionEvent(FinishedInterestTransmissionEvent evt) {
    }

    private void handleStartedInterestTransmissionEvent(StartedInterestTransmissionEvent evt) {
        String id = new StringBuilder().append(evt.getFrom()).append("-").append(evt.getTo()).toString();
        sentInterests.get(id).getAndIncrement();
    }

    private void handleScenarioSelectedEvent() {
        web.getEngine().executeScript("scenarioSelected()");
        graphNodes.clear();
        graphEdges.clear();
    }

    private void handleTopologyFinishedEvent() {
        Gson gson = new Gson();
        String jsCmd = "drawGraph('" + gson.toJson(graphNodes) + "', '" + gson.toJson(graphEdges) + "')";
        System.out.println(jsCmd);
        web.getEngine().executeScript(jsCmd);
    }

    private void handleLinkCreatedEvent(LinkCreatedEvent evt) {
        LinkCreatedEvent lce = evt;
        int from = lce.getLink().getF1().getNode().getId();
        int to = lce.getLink().getF2().getNode().getId();
        String id = new StringBuilder().append(from).append("-").append(to).toString();
        String id2 = new StringBuilder().append(to).append("-").append(from).toString();
        graphEdges.add(new GraphEdge(from, to, id));
        graphEdges.add(new GraphEdge(to, from, id2));
        sentInterests.put(id, new AtomicInteger(0));
        sentInterests.put(id2, new AtomicInteger(0));
        sentData.put(id, new AtomicInteger(0));
        sentData.put(id2, new AtomicInteger(0));

        final boolean[] contains = {false, false};
        graphNodes.stream().forEach(node -> {
            if (node.getId() == from)
                contains[0] = true;
            if (node.getId() == to) {
                contains[1] = true;
            }
        });
        if (contains[0] == false) {
            graphNodes.add(new GraphNode(from, "" + from));
        }
        if (contains[1] == false) {
            graphNodes.add(new GraphNode(to, "" + to));
        }
    }

    public class JSBridge {
        public void call(Object param) {
            System.out.println("hello " + param.toString());
        }
    }

}


