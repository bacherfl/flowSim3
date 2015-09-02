package at.itec.fbacher.dashboard.graph;

/**
 * Created by florian on 02.09.2015.
 */
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
