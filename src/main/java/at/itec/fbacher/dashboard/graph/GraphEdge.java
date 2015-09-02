package at.itec.fbacher.dashboard.graph;

/**
 * Created by florian on 02.09.2015.
 */
class GraphEdge {
    private String from;
    private String to;
    private String id;

    public GraphEdge(String from, String to, String id) {
        this.from = from;
        this.to = to;
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
