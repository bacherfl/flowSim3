package at.itec.fbacher.flowsim.model;

import at.itec.fbacher.flowsim.extensions.app.SimpleConsumer;
import at.itec.fbacher.flowsim.extensions.app.SimpleProducer;
import at.itec.fbacher.flowsim.extensions.strategies.BroadcastStrategy;
import at.itec.fbacher.flowsim.extensions.strategies.LearningStrategy;
import at.itec.fbacher.flowsim.model.topology.TopologyHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by florian on 02.09.2015.
 */
public class DatFileParser implements ScenarioFileParser {

    private final File scenarioFile;

    private final static int PARSE_CLIENTS_STATE = 0;
    private final static int PARSE_SERVER_STATE = 1;
    private final static int PARSE_ROUTER_STATE = 2;
    private final static int PARSE_CONTENT_STATE = 3;
    private final static int PARSE_CONNECTIONS_STATE = 4;
    private final static int PARSE_DEMAND_STATE = 5;

    private int state;

    List<Node> nodes = new ArrayList<>();
    TopologyHelper topologyHelper;

    public DatFileParser(File scenarioFile) {
        this.scenarioFile = scenarioFile;
    }

    @Override
    public void parseFile() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(scenarioFile));
            if (br == null)
                return;

            topologyHelper = new TopologyHelper();

            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty())
                    continue;
                if (line.equals("Client:")) {
                    state = PARSE_CLIENTS_STATE;
                    continue;
                }
                else if (line.equals("Server:")) {
                    state = PARSE_SERVER_STATE;
                    continue;
                }
                else if (line.equals("Router:")) {
                    state = PARSE_ROUTER_STATE;
                    continue;
                }
                else if (line.equals("Content:")) {
                    state = PARSE_CONTENT_STATE;
                    continue;
                }
                else if (line.equals("Edge:")) {
                    state = PARSE_CONNECTIONS_STATE;
                    continue;
                }
                else if (line.equals("Demand:")) {
                    state = PARSE_DEMAND_STATE;
                    continue;
                }

                parseLine(line);
            }
            nodes.forEach(node -> {
                if (node.getApp() == null) {
                    node.setApp(new SimpleConsumer());
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseLine(String line) {
        if (state == PARSE_CLIENTS_STATE || state == PARSE_SERVER_STATE || state == PARSE_ROUTER_STATE) {
            Node node = new Node(line);
            node.setForwardingStrategy(new LearningStrategy());
            nodes.add(node);
        }

        else if (state == PARSE_CONNECTIONS_STATE) {
            parseConnectionLine(line);
        }

        else if (state == PARSE_DEMAND_STATE) {
            parseDemandLine(line);
        }
    }

    private void parseDemandLine(String line) {
        String[] split = line.split(",");

        String clientId = split[0].trim();
        String content = "/" + split[1].trim();
        int bandwidth = Integer.parseInt(split[2].trim());

        SimpleConsumer simpleConsumer = new SimpleConsumer(true, content, bandwidth);
        Node clientNode = nodes.stream().filter(node -> node.getId().equals(clientId)).findFirst().get();
        if (clientNode != null) {
            clientNode.setApp(simpleConsumer);
            simpleConsumer.startAt(1);
        }
    }

    private void parseConnectionLine(String line) {
        String[] split = line.split(",");
        if (!line.contains("Content")) {
            Node node1 = nodes.stream().filter(node -> node.getId().equals(split[0].trim())).findFirst().get();
            Node node2 = nodes.stream().filter(node -> node.getId().equals(split[1].trim())).findFirst().get();

            if (node1 != null && node2 != null) {
                int bandwidth = Integer.parseInt(split[2].trim());
                topologyHelper.addLink(node1, node2, bandwidth, 10, 1.0);
            }
        } else {

            Node serverNode = nodes.stream().filter(node -> node.getId().equals(split[1].trim())).findFirst().get();
            if (serverNode != null) {
                SimpleProducer producer;
                if (serverNode.getApp() == null) {
                    producer = new SimpleProducer();
                    serverNode.setApp(producer);
                    producer.startAt(1);
                } else {
                    producer = (SimpleProducer) serverNode.getApp();
                }
                producer.getPrefixes().add("/" + split[0].trim());
            }
        }
    }
}
