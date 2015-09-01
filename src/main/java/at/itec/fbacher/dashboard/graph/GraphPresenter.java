package at.itec.fbacher.dashboard.graph;

import at.itec.fbacher.flowsim.events.*;
import at.itec.fbacher.flowsim.model.Interest;
import at.itec.fbacher.flowsim.sim.FormattedTime;
import at.itec.fbacher.flowsim.sim.Simulator;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

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

    Map<String, AtomicInteger> sentInterests = new HashMap<>();
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
        else if (evt instanceof ScenarioStartedEvent) {
            handleScenarioStartedEvent();
        }
    }

    private void handleScenarioStartedEvent() {
        simulationRunning = true;
        updateGraphTask = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                while (simulationRunning) {
                    Platform.runLater(() -> {
                        sentInterests.keySet().forEach(id -> {
                            String[] split = id.split("-");
                            double bandwidth = (sentInterests.get(id).get() * 5 * Interest.INTEREST_SIZE * 8) /
                                    Simulator.getInstance().getSpeedupFactor();
                            double scaleFactor = (bandwidth + 0.0) / (1000000);

                            String jsCmd = "updateInterests(" + split[0] + ", " + split[1] + ", " + scaleFactor + ")";
                            web.getEngine().executeScript(jsCmd);
                            sentInterests.get(id).set(0);
                        });

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

    private void handleFinishedInterestTransmissionEvent(FinishedInterestTransmissionEvent evt) {
        /*
        String id = new StringBuilder().append(evt.getFrom()).append("-").append(evt.getTo()).toString();
        GraphEdge edge = new GraphEdge(evt.getFrom(), evt.getTo(), id);
        Gson gson = new Gson();
        String jsCmd = "onInterestFinished('" + gson.toJson(edge) + "')";
        Platform.runLater(() -> web.getEngine().executeScript(jsCmd));
        */
    }

    private void handleStartedInterestTransmissionEvent(StartedInterestTransmissionEvent evt) {
        String id = new StringBuilder().append(evt.getFrom()).append("-").append(evt.getTo()).toString();
        sentInterests.get(id).getAndIncrement();
        /*
        GraphEdge edge = new GraphEdge(evt.getFrom(), evt.getTo(), id);
        Gson gson = new Gson();
        String jsCmd = "onInterest('" + gson.toJson(edge) + "')";
        Platform.runLater(() -> web.getEngine().executeScript(jsCmd));
        */
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

    class GraphNode {
        private int id;
        private String label;

        public GraphNode(int id, String label) {
            this.id = id;
            this.label = label;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    class GraphEdge {
        private int from;
        private int to;
        private String id;

        public GraphEdge(int from, int to, String id) {
            this.from = from;
            this.to = to;
            this.id = id;
        }

        public int getFrom() {
            return from;
        }

        public void setFrom(int from) {
            this.from = from;
        }

        public int getTo() {
            return to;
        }

        public void setTo(int to) {
            this.to = to;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}


