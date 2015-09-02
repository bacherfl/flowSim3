package at.itec.fbacher.dashboard.graph;

/**
 * Created by florian on 02.09.2015.
 */
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
