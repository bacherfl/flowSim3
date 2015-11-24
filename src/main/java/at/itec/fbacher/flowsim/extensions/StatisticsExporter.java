package at.itec.fbacher.flowsim.extensions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by florian on 16.11.2015.
 */
public abstract class StatisticsExporter {

    protected String name;
    protected List<String> properties;

    public StatisticsExporter(String name, List<String> properties) {
        this.name = name;
        this.properties = properties;
    }

    public StatisticsExporter(String name) {
        this.name = name;
        properties = new ArrayList<>();
    }

    public void addProperty(String property) {
        properties.add(property);
    }

    public abstract void exportStatistic(List<Double> values);
}
