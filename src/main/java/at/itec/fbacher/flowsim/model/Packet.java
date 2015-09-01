package at.itec.fbacher.flowsim.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by florian on 12.08.2015.
 */
public class Packet {

    private int size;
    private List<Event> trace;
    private String name;

    public Packet() {
        trace = new ArrayList<>();
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<Event> getTrace() {
        return trace;
    }

    public void setTrace(List<Event> trace) {
        this.trace = trace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        if (name.contains("%")) {
            return name.split("%")[0];
        }
        return name;
    }
}
