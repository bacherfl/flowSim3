package at.itec.fbacher.flowsim.extensions;

import at.itec.fbacher.flowsim.events.*;
import at.itec.fbacher.flowsim.extensions.strategies.sdn.SDNControlledStrategy;
import at.itec.fbacher.flowsim.model.Node;
import org.neo4j.graphalgo.impl.util.PathImpl;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by florian on 16.11.2015.
 */
public class SDNController implements EventSubscriber {

    private TrafficPredictor trafficPredictor;
    private PopularityPredictor popularityPredictor;

    private Map<String, SDNControlledStrategy> forwarders = new HashMap<>();

    private static SDNController instance;

    private GraphDatabaseService db;

    private SDNController() {
        EventPublisher.getInstance().register(this, LinkCreatedEvent.class);
        EventPublisher.getInstance().register(this, ContentAssignedEvent.class);
        EventPublisher.getInstance().register(this, SimulationFinishedEvent.class);
        db = new GraphDatabaseFactory().newEmbeddedDatabase("flowsim.graphdb");
    }

    public static SDNController getInstance() {
        if (instance == null) {
            instance = new SDNController();
        }
        return instance;
    }

    public void registerForwarder(SDNControlledStrategy forwarder) {
        forwarders.put(forwarder.getNode().getId(), forwarder);
    }

    public void calculateRoutesForPrefix(String startNodeId, String prefix) {
        try (Transaction tx = db.beginTx()) {
            StringBuilder statementBuilder = new StringBuilder();

            statementBuilder.append("MATCH (requester:Node{nodeId:'" + startNodeId + "'}), (server:Node)," +
                    "p = allShortestPaths((requester)-[:LINK*]->(server)) WHERE '" + prefix +
                    "' in server.prefixes AND NOT server.nodeId = '" + startNodeId + "' return p ORDER BY length(p) ASC");

            Result data = performNeo4jTrx(statementBuilder.toString());

            List<Path> paths = parsePaths(data);

            if (paths.size() == 0) {
                statementBuilder = new StringBuilder();
                statementBuilder.append("MATCH (requester:Node{nodeId:'" + startNodeId + "'}), (server:Node)," +
                        "p = allShortestPaths((requester)-[*]->(server)) WHERE '" + prefix +
                        "' in server.prefixes AND NOT server.nodeId = '" + startNodeId +
                        "' return p ORDER BY length(p) ASC LIMIT 1");

                data = performNeo4jTrx(statementBuilder.toString());
                paths = parsePaths(data);
                for (int i = 0; i < paths.size(); i++)
                {
                    pushPath(paths.get(i), prefix);
                }
            } else {
                for (int i = 0; i < paths.size(); i++)
                {
                    pushPath(paths.get(i), prefix);
                }
            }
            tx.success();
        }
    }

    private void pushPath(Path p, String prefix) {
        int cost = p.pathEntries.size();
        for (int i = 0; i < p.pathEntries.size(); i++)
        {
            PathEntry pe = p.pathEntries.get(i);
            SDNControlledStrategy strategy = forwarders.get(pe.start + "");
            strategy.pushRule(prefix, pe.face, cost);
            if (i < p.pathEntries.size() - 1) {
                if (pe.bandwidth > 0) {
                    strategy.assignBandwidth(
                            prefix,
                            pe.face,
                            (int) (pe.bandwidth * 0.9)
                            //pe->bandwidth / (strategy->getFlowsOfFace(pe->face).size() + 1)
                    );
                }
                else {
                    strategy.assignBandwidth(
                            prefix,
                            pe.face,
                            1000000);
                }
            }
            cost--;
        }
    }

    private Result performNeo4jTrx(String statement) {
        Result rs;

        try (Transaction tx = db.beginTx()) {

            rs = db.execute(statement.toString());
            tx.success();
        }
        return rs;
        /*
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
        */
    }

    public void linkFailure(String id, int faceId, String prefix, double failRate) {

    }

    public void linkRecovered(String id, int faceId, String prefix, double failRate) {

    }

    public List<Path> parsePaths(Result data) {
        System.out.println(data.toString());

        List<Path> paths = new ArrayList<>();

        while(data.hasNext()) {
            //Object object = data.getObject(0);
            Map<String, Object> row = data.next();

            PathImpl p = (PathImpl) row.get("p");
            Path path = new Path();
            final int[] t = {0};
            final PathEntry[] pe = new PathEntry[1];
            p.iterator().forEachRemaining(propertyContainer -> {
                if (t[0] == 0) {
                    pe[0] = new PathEntry();
                    pe[0].start = Integer.parseInt(propertyContainer.getProperty("nodeId").toString());
                } else if (t[0] == 1) {
                    pe[0].face = Integer.parseInt(propertyContainer.getProperty("startFace").toString());
                    pe[0].bandwidth = Integer.parseInt(propertyContainer.getProperty("bandwidth").toString());
                } else if (t[0] == 2) {
                    pe[0].end = Integer.parseInt(propertyContainer.getProperty("nodeId").toString());
                    path.pathEntries.add(pe[0]);
                }
                t[0] = t[0] + 1 % 3;
            });

            if (path.pathEntries.size() > 0) {
                PathEntry pe2 = new PathEntry();
                pe2.start = path.pathEntries.get(path.pathEntries.size() - 1).end;
                pe2.face = getNumberOfFacesForNode(pe2.start);
                pe2.end = -1;   //App Face
                path.pathEntries.add(pe2);
            }
            paths.add(path);

        }

        return paths;
    }

    private int getNumberOfFacesForNode(int nodeId) {
        return forwarders.get(nodeId + "").getNode().getFaces().size() - 1;
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

        int faceIdA = a.getFaces().size() - 1;
        int faceIdB = b.getFaces().size() - 1;

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
        } else if (evt instanceof SimulationFinishedEvent) {
            db.shutdown();
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

    private class Path {
        List<PathEntry> pathEntries = new ArrayList<>();
    }

    private class PathEntry {
        int start;
        int face;
        int end;
        int bandwidth;
    }
}
