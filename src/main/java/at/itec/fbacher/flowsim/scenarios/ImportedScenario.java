package at.itec.fbacher.flowsim.scenarios;

import at.itec.fbacher.flowsim.events.EventPublisher;
import at.itec.fbacher.flowsim.events.TopologyFinishedEvent;
import at.itec.fbacher.flowsim.model.Scenario;
import at.itec.fbacher.flowsim.model.ScenarioFileParser;
import at.itec.fbacher.flowsim.sim.Simulator;

/**
 * Created by florian on 02.09.2015.
 */
public class ImportedScenario implements Scenario {

    private final ScenarioFileParser scenarioFileParser;

    public ImportedScenario(ScenarioFileParser scenarioFileParser) {
        this.scenarioFileParser = scenarioFileParser;
    }

    @Override
    public void run() {
        Simulator.getInstance().start();
    }

    @Override
    public void initialize() {
        scenarioFileParser.parseFile();
        EventPublisher.getInstance().publishEvent(new TopologyFinishedEvent());
        Simulator.getInstance().setSimulationLengthInSeconds(1000);
    }
}
