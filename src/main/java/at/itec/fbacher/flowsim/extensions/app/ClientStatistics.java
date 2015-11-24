package at.itec.fbacher.flowsim.extensions.app;

import at.itec.fbacher.flowsim.events.ApplicationEvent;
import at.itec.fbacher.flowsim.events.EventPublisher;
import at.itec.fbacher.flowsim.events.EventSubscriber;
import at.itec.fbacher.flowsim.events.SimulationProceededEvent;
import at.itec.fbacher.flowsim.extensions.ExporterFactory;
import at.itec.fbacher.flowsim.extensions.SatisfactionLog;
import at.itec.fbacher.flowsim.extensions.StatisticsExporter;
import at.itec.fbacher.flowsim.model.Data;
import at.itec.fbacher.flowsim.model.Interest;
import at.itec.fbacher.flowsim.sim.Simulator;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by florian on 23.11.2015.
 */
public class ClientStatistics implements EventSubscriber {
    private double satisfactionRate;
    private DescriptiveStatistics delay;
    private SummaryStatistics bandwidth;

    private int nSentInterests = 0;
    private int nSatisfiedInterests = 0;

    private Map<String, SatisfactionLog> satsfactionRateMap = new HashMap<>();

    private double lastCheckpoint = 0;
    private int nSatisfiedInterestsTmp = 0;
    private int bandwidthCalculationInterval = 1;   //calculate bandwidth every second per default

    private Map<String, Double> sentInterests = new HashMap<>();

    private StatisticsExporter aggregateExporter;
    private StatisticsExporter bandwidthSeriesExporter;
    private int nextExportId = 0;

    public ClientStatistics(String name, String exporterType) {
        initialize(name, exporterType);
    }

    public ClientStatistics(String name, String exporterType, int bandwidthCalculationInterval) {
        this.bandwidthCalculationInterval = bandwidthCalculationInterval;
        initialize(name, exporterType);
    }

    private void initialize(String name, String exporterType) {
        delay = new DescriptiveStatistics();
        bandwidth = new SummaryStatistics();
        EventPublisher.getInstance().register(this, SimulationProceededEvent.class);

        ExporterFactory exporterFactory = new ExporterFactory();
        aggregateExporter = exporterFactory.createExporter(name + "-satRate-delay-" + nextExportId, exporterType);
        aggregateExporter.addProperty("satRate");
        aggregateExporter.addProperty("delayAvg");
        aggregateExporter.addProperty("delayMedian");
        aggregateExporter.addProperty("delayStdDev");
        aggregateExporter.addProperty("delayMin");
        aggregateExporter.addProperty("delayMax");
        aggregateExporter.addProperty("delay90Percentile");
        aggregateExporter.addProperty("bandwidthAvg");
        aggregateExporter.addProperty("bandwidthStdDev");

        bandwidthSeriesExporter = exporterFactory.createExporter(name + "-bandwidth", exporterType);
        bandwidthSeriesExporter.addProperty("bandwidth");
    }

    public double getSatisfactionRate() {
        return satisfactionRate;
    }

    public DescriptiveStatistics getDelay() {
        return delay;
    }

    public SummaryStatistics getBandwidth() {
        return bandwidth;
    }

    public void sentInterest(Interest interest) {
        sentInterests.put(interest.getName(), Simulator.getInstance().getCurrentTimeInSeconds());

        SatisfactionLog satisfactionLog = satsfactionRateMap.get(interest.getName());
        if (satisfactionLog == null) {
            satisfactionLog = new SatisfactionLog();
            satisfactionLog.incrementSentInterests();
            satsfactionRateMap.put(interest.getName(), satisfactionLog);
        } else {
            satisfactionLog.incrementSentInterests();
        }
    }

    public void onData(Data data) {
        Double sentTstamp = sentInterests.get(data.getName());
        if (sentTstamp == null) {
            return;
        }
        Double delay = Simulator.getInstance().getCurrentTimeInSeconds() - sentTstamp;
        this.delay.addValue(delay);

        if (satsfactionRateMap.get(data.getName()) != null)
            satsfactionRateMap.get(data.getName()).incrementSatisfiedInterests();

        nSatisfiedInterestsTmp++;   //for bandwidth calculation
        satisfactionRate = (nSatisfiedInterests + 0.0) / nSentInterests;

        if (satisfactionRate > 1) {
            System.out.println("Something smells wrong here...");
        }
    }

    public void addBandwidthEntry() {
        double bandwidth = (nSatisfiedInterestsTmp * Data.DATA_SIZE) /
                (Simulator.getInstance().getCurrentTimeInSeconds() - lastCheckpoint);

        this.bandwidth.addValue(bandwidth);

        //bandwidthSeriesExporter.exportStatistic()

        lastCheckpoint = Simulator.getInstance().getCurrentTimeInSeconds();
        nSatisfiedInterestsTmp = 0;
    }

    @Override
    public void handleEvent(ApplicationEvent evt) {
        if (evt instanceof SimulationProceededEvent) {
            if (Simulator.getInstance().getCurrentTime()
                    % (Simulator.SIMULATION_TICKS_PER_SECOND * bandwidthCalculationInterval) == 0) {
                addBandwidthEntry();
            }
        }
    }

    public void export() {
        List<Double> values = new ArrayList<>();
        values.add(satsfactionRateMap.values().stream().mapToDouble(SatisfactionLog::getSatisfactionRate).average().getAsDouble());
        values.add(delay.getMean());
        values.add(delay.getPercentile(50));
        values.add(delay.getStandardDeviation());
        values.add(delay.getMin());
        values.add(delay.getMax());
        values.add(delay.getPercentile(90));
        values.add(bandwidth.getMean());
        values.add(bandwidth.getStandardDeviation());

        aggregateExporter.exportStatistic(values);
    }

    public void reset() {
        //nSentInterests = 0;
        //nSatisfiedInterests = 0;
        delay = new DescriptiveStatistics();
    }
}
