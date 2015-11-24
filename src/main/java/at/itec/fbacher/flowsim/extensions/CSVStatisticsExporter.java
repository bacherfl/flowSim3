package at.itec.fbacher.flowsim.extensions;

import at.itec.fbacher.flowsim.events.ApplicationEvent;
import at.itec.fbacher.flowsim.events.EventPublisher;
import at.itec.fbacher.flowsim.events.EventSubscriber;
import at.itec.fbacher.flowsim.events.SimulationFinishedEvent;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by florian on 16.11.2015.
 */
public class CSVStatisticsExporter extends StatisticsExporter implements EventSubscriber {

    private BufferedWriter writer;

    public CSVStatisticsExporter(String name, List<String> properties) {
        super(name, properties);
        initialize();
    }

    public CSVStatisticsExporter(String name) {
        super(name);
        initialize();
    }

    private void initialize() {
        EventPublisher.getInstance().register(this, SimulationFinishedEvent.class);
        try {
            if (!Files.isDirectory(Paths.get("output")))
                Files.createDirectory(Paths.get("output"));
            writer = Files.newBufferedWriter(Paths.get("output/" + name + ".csv"));
            properties.forEach(property -> {
                try {
                    writer.write(property + ";");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exportStatistic(List<Double> values) {
        values.forEach(value -> {
            try {
                writer.write(value + ";");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        try {
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleEvent(ApplicationEvent evt) {
        if (evt instanceof SimulationFinishedEvent) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
