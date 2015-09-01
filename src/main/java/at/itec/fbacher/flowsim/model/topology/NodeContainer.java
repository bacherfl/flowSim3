package at.itec.fbacher.flowsim.model.topology;


import at.itec.fbacher.flowsim.model.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by florian on 13.08.2015.
 */
public class NodeContainer {

    List<Node> nodes = new ArrayList<>();

    public NodeContainer(int nrNodes) {
        for (int i = 0; i < nrNodes; i++) {
            Node n = new Node();
            nodes.add(n);
        }
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
}
