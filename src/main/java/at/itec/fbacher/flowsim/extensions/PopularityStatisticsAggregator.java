package at.itec.fbacher.flowsim.extensions;

import at.itec.fbacher.flowsim.events.*;
import at.itec.fbacher.flowsim.sim.Simulator;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instance;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by florian on 25.11.2015.
 */
public class PopularityStatisticsAggregator implements EventSubscriber {

    private Map<String, PopularityStatisticsEntry> statisticsEntryMap = new HashMap<>();
    private Map<String, AtomicInteger> nRequestsMap = new HashMap<>();

    private ReentrantLock lock = new ReentrantLock();

    private int statisticIntervalInSeconds = 10;    //make statistic entry every 10 seconds

    public PopularityStatisticsAggregator(int statisticIntervalInSeconds) {
        EventPublisher.getInstance().register(this, ContentRequestedEvent.class);
        EventPublisher.getInstance().register(this, SimulationProceededEvent.class);
        initArffFile();
        this.statisticIntervalInSeconds = statisticIntervalInSeconds;
    }

    private void initArffFile() {
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(Paths.get("statistics/popularityModel.arff"));
            writeHeader(writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleEvent(ApplicationEvent evt) {
        if (evt instanceof ContentRequestedEvent) {
            String contentId = ((ContentRequestedEvent) evt).getContentId();
            if (nRequestsMap.get(contentId) == null) {
                nRequestsMap.put(contentId, new AtomicInteger(1));
            } else {
                nRequestsMap.get(contentId).incrementAndGet();
            }
        } else if (evt instanceof SimulationProceededEvent) {
            if (Simulator.getInstance().getCurrentTimeInSeconds() % statisticIntervalInSeconds == 0) {
                calculatePopularityValues();
            }
        }
    }

    private void calculatePopularityValues() {
        int totalRequests = nRequestsMap.values().stream().mapToInt(AtomicInteger::get).sum();
        nRequestsMap.keySet().forEach(contentId -> {
            double popularity = (nRequestsMap.get(contentId).get() + 0.0) / totalRequests;

            PopularityStatisticsEntry statisticsEntry;
            if (statisticsEntryMap.get(contentId) == null) {
                statisticsEntry = new PopularityStatisticsEntry();
                statisticsEntry.pushPopularityValue(popularity, () -> writeArffLine(statisticsEntry));
                statisticsEntryMap.put(contentId, statisticsEntry);
            } else {
                statisticsEntry = statisticsEntryMap.get(contentId);
                statisticsEntry.pushPopularityValue(popularity, () -> writeArffLine(statisticsEntry));
            }
        });

        nRequestsMap.clear();
    }

    public double predictContentPopularity(String contentId) {
        lock.lock();
        PopularityStatisticsEntry statisticsEntry = statisticsEntryMap.get(contentId);

        double popularity = 0.0;
        try {
            Files.copy(Paths.get("statistics/popularityModel.arff"), Paths.get("statistics/popularityModelTmp.arff"));

            BufferedWriter writer = null;
            writer = Files.newBufferedWriter(
                    Paths.get("statistics/popularityModelTmp.arff"),
                    StandardOpenOption.APPEND);
            writer.write(
                    statisticsEntry.getPopularityTMinus1() + "," +
                            statisticsEntry.getPopularityT() + ",?");
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Instances data = null;
        try {
            data = new Instances(new BufferedReader(new
                    FileReader("statistics/popularityModelTmp.arff")));

            data.setClassIndex(data.numAttributes() - 1);
            //build model
            LinearRegression model = new LinearRegression();
            model.buildClassifier(data); //the last instance with missing class is not used
            System.out.println(model);
            //classify the last instance
            Instance myPopularity = data.lastInstance();
            popularity = model.classifyInstance(myPopularity);

            Files.delete(Paths.get("statistics/popularityModelTmp.arff"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        lock.unlock();
        return popularity;
    }

    private void writeArffLine(PopularityStatisticsEntry statisticsEntry) {
        try {
            lock.lock();
            BufferedWriter writer = Files.newBufferedWriter(
                    Paths.get("statistics/popularityModel.arff"),
                    StandardOpenOption.APPEND);

            writer.write(
                    statisticsEntry.getPopularityTMinus2() + "," +
                            statisticsEntry.getPopularityTMinus1() + "," + statisticsEntry.getPopularityT());
            writer.newLine();
            writer.close();
            lock.unlock();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeHeader(BufferedWriter writer) throws IOException {
        writer.write("@RELATION popularities");
        writer.write("\n");
        writer.write("\n");
        writer.write("@ATTRIBUTE t-2 NUMERIC");
        writer.write("\n");
        writer.write("@ATTRIBUTE t-1 NUMERIC");
        writer.write("\n");
        writer.write("@ATTRIBUTE prediction NUMERIC");
        writer.write("\n");
        writer.write("\n");
        writer.write("@DATA");
        writer.write("\n");
    }
}
