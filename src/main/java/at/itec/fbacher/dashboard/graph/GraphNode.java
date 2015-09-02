package at.itec.fbacher.dashboard.graph;

/**
 * Created by florian on 02.09.2015.
 */
class GraphNode {
    private String id;
    private String label;

    public GraphNode(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
