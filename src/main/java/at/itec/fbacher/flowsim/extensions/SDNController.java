package at.itec.fbacher.flowsim.extensions;

import at.itec.fbacher.flowsim.events.*;
import at.itec.fbacher.flowsim.extensions.strategies.sdn.SDNControlledStrategy;
import at.itec.fbacher.flowsim.model.Node;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by florian on 16.11.2015.
 */
public class SDNController implements EventSubscriber {

    private TrafficPredictor trafficPredictor;
    private PopularityPredictor popularityPredictor;

    private Map<String, SDNControlledStrategy> forwarders = new HashMap<>();

    private static SDNController instance;

    private SDNController() {
        EventPublisher.getInstance().register(this, LinkCreatedEvent.class);
        EventPublisher.getInstance().register(this, ContentAssignedEvent.class);
    }

    public static SDNController getInstance() {
        if (instance == null) {
            instance = new SDNController();
        }
        return instance;
    }

    public void calculateRoutesForPrefix(String startNodeId, String prefix) {
        StringBuilder statementBuilder = new StringBuilder();

        statementBuilder.append("MATCH (requester:Node{nodeId:'" + startNodeId + "'}), (server:Node)," +
                "p = allShortestPaths((requester)-[:LINK*]->(server)) WHERE '" + prefix +
                "' in server.prefixes AND NOT server.nodeId = '" + startNodeId + "' return p ORDER BY length(p) ASC");

        ResultSet data = performNeo4jTrx(statementBuilder.toString());

        /*
        std::stringstream statement;

        statement + "MATCH (requester:Node{nodeId:'" + startNodeId + "'}), (server:Node)," +
                "p = allShortestPaths((requester)-[:LINK*]->(server)) WHERE '" + prefix + "' in server.prefixes AND NOT server.nodeId = '" + startNodeId + "' return p ORDER BY length(p) ASC";

        std::string data = PerformNeo4jTrx(statement.toString(), curlCallback);

        vector<Path *> paths = ParsePaths(data);

        if (paths.size() == 0) {
            statement.str("");
            statement + "MATCH (requester:Node{nodeId:'" + startNodeId + "'}), (server:Node)," +
                    "p = allShortestPaths((requester)-[*]->(server)) WHERE '" + prefix + "' in server.prefixes AND NOT server.nodeId = '" + startNodeId + "' return p ORDER BY length(p) ASC LIMIT 1";

            std::string data = PerformNeo4jTrx(statement.toString(), curlCallback);
            vector<Path *> paths = ParsePaths(data);
            for (int i = 0; i < paths.size(); i++)
            {
                PushPath(paths.at(i), prefix);
            }
        }
        else {
            for (int i = 0; i < paths.size(); i++)
            {
                PushPath(paths.at(i), prefix);
            }
        }
        */
    }

    private ResultSet performNeo4jTrx(String statement) {
        ResultSet rs = null;
        // Make sure Neo4j Driver is registered
        try {
            Class.forName("org.neo4j.jdbc.Driver");

            // Connect
            Connection con = DriverManager.getConnection("jdbc:neo4j://localhost:7474/", "neo4j", "1234");

            // Querying
            try(Statement stmt = con.createStatement())
            {
                rs = stmt.executeQuery(statement);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public void linkFailure(String id, int faceId, String prefix, double failRate) {

    }

    public void linkRecovered(String id, int faceId, String prefix, double failRate) {

    }

    public void parsePaths(ResultSet data) {
        System.out.println(data.toString());
    }

    public void addLink(Node a, Node b,
                                Map<String, String> channelAttributes,
                                Map<String, String> deviceAttributes) {

        StringBuilder statement = new StringBuilder();
        StringBuilder attributes = new StringBuilder();

        channelAttributes.keySet()
                .forEach(
                        channelAttribute ->
                                attributes.append(channelAttribute + ":" + channelAttributes.get(channelAttribute)));

        statement.append("MATCH (a:Node {nodeId:'" + a.getId() + "'}), " +
                "(b:Node {nodeId:'" + b.getId() + "'}), (a)-[l]-(b) DELETE l;");

        performNeo4jTrx(statement.toString());

        statement = new StringBuilder();

        int faceIdA = a.getFaces().size();
        int faceIdB = b.getFaces().size();

        statement.append("MERGE (a:Node {nodeId:'" + a.getId() + "'}) " +
                "MERGE (b:Node {nodeId:'" + b.getId() + "'}) " +
                "CREATE (a)-[:LINK {startFace:"
                + faceIdA + ", endFace:" + faceIdB + "," + attributes.toString() + ", failureRate: 0.0"
                + "} ]->(b) " +
                "CREATE (a)<-[:LINK {startFace:"
                + faceIdB + ", endFace:" + faceIdA + "," + attributes.toString() + ", failureRate: 0.0"
                + "} ]-(b) RETURN a");

        performNeo4jTrx(statement.toString());
    }

    public void clearGraphDb() {
        StringBuilder statement = new StringBuilder();
        statement.append("MATCH (n)-[r]-() DELETE n, r");
        performNeo4jTrx(statement.toString());
    }

    public void addForwarder(SDNControlledStrategy forwarder) {
        forwarders.put(forwarder.getNode().getId(), forwarder);
    }

    @Override
    public void handleEvent(ApplicationEvent evt) {
        if (evt instanceof LinkCreatedEvent) {
            LinkCreatedEvent lce = (LinkCreatedEvent) evt;
            Map<String, String> properties = new HashMap<>();
            properties.put("bandwidth", lce.getLink().getBandwidth() + "");
            addLink(lce.getLink().getF1().getNode(), lce.getLink().getF2().getNode(), properties, null);
        } else if (evt instanceof ContentAssignedEvent) {
            ContentAssignedEvent cae = (ContentAssignedEvent) evt;
            addOrigins(cae.getContentInfo().getContentName(), cae.getProducer().getNode().getId());
        }
    }

    private void addOrigins(String contentName, String prodId) {
        StringBuilder statement = new StringBuilder();

        statement.append("MATCH (n:Node) WHERE n.nodeId='" + prodId + "' "
                + "SET n.prefixes = CASE WHEN NOT (HAS (n.prefixes)) "
                + "THEN ['" + contentName + "'] "
                + "ELSE n.prefixes + ['" + contentName + "'] "
                + "END");

        performNeo4jTrx(statement.toString());
    }
}
