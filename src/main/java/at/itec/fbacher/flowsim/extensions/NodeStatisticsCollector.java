package at.itec.fbacher.flowsim.extensions;

import at.itec.fbacher.flowsim.events.*;
import at.itec.fbacher.flowsim.sim.Simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by florian on 16.11.2015.
 */
public class NodeStatisticsCollector implements EventSubscriber{

    private long lastCheckpoint = 0;
    private long updateInterval;

    private Map<Integer, FaceStatisticEntry> faceStatistics = new HashMap<>();
    private Map<Integer, StatisticsExporter> statisticsExporters = new HashMap<>();

    private String nodeId;

    public NodeStatisticsCollector(String nodeId, long updateIntervalInSeconds) {
        EventPublisher.getInstance().register(this, ReceivedInterestEvent.class);
        EventPublisher.getInstance().register(this, ReceivedDataEvent.class);
        EventPublisher.getInstance().register(this, SentInterestEvent.class);
        EventPublisher.getInstance().register(this, SentDataEvent.class);
        EventPublisher.getInstance().register(this, SimulationProceededEvent.class);

        this.updateInterval = updateIntervalInSeconds * Simulator.SIMULATION_TICKS_PER_SECOND;
        this.nodeId = nodeId;
    }

    @Override
    public void handleEvent(ApplicationEvent evt) {
        if (evt instanceof ReceivedInterestEvent) {
            handleReceivedInterestEvent((ReceivedInterestEvent) evt);
        } else if (evt instanceof ReceivedDataEvent) {
            handleReceivedDataEvent((ReceivedDataEvent) evt);
        } else if (evt instanceof SentInterestEvent) {
            handleSentInterestEvent((SentInterestEvent) evt);
        } else if (evt instanceof SentDataEvent) {
            handleSentDataEvent((SentDataEvent) evt);
        } else if (evt instanceof SimulationProceededEvent) {
            handleSimulationProceededEvent((SimulationProceededEvent) evt);
        }
    }

    private void handleSimulationProceededEvent(SimulationProceededEvent evt) {
        long timePassed = Simulator.getInstance().getCurrentTime() - lastCheckpoint;
        if (timePassed >= updateInterval) {
            //TODO export the gathered values

            lastCheckpoint = Simulator.getInstance().getCurrentTime();
        }
    }

    private void ensureKeyExists(int faceId) {
        if (faceStatistics.get(faceId) == null) {
            faceStatistics.put(faceId, new FaceStatisticEntry());
            List<String> properties = new ArrayList<>();
            properties.add("ReceivedInterests");
            properties.add("ReceivedDataPackets");
            properties.add("SentInterests");
            properties.add("SentDataPackets");
            statisticsExporters.put(faceId, new CSVStatisticsExporter(nodeId + "-" + faceId, properties));
        }
    }

    private void handleSentDataEvent(SentDataEvent evt) {
        ensureKeyExists(evt.getFaceId());
        faceStatistics.get(evt.getFaceId()).incrementSentDataPackets();
    }

    private void handleSentInterestEvent(SentInterestEvent evt) {
        ensureKeyExists(evt.getFaceId());
        faceStatistics.get(evt.getFaceId()).incrementSentInterests();
    }

    private void handleReceivedDataEvent(ReceivedDataEvent evt) {
        ensureKeyExists(evt.getFaceId());
        faceStatistics.get(evt.getFaceId()).incrementReceivedDataPackets();
    }

    private void handleReceivedInterestEvent(ReceivedInterestEvent evt) {
        ensureKeyExists(evt.getFaceId());
        faceStatistics.get(evt.getFaceId()).incrementReceivedInterests();
    }
}
