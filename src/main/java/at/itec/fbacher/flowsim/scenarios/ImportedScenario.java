package at.itec.fbacher.flowsim.scenarios;

import at.itec.fbacher.flowsim.model.DatFileParser;
import at.itec.fbacher.flowsim.model.Scenario;
import at.itec.fbacher.flowsim.model.ScenarioFileParser;
import at.itec.fbacher.flowsim.sim.Simulator;

import java.io.File;

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
    }
}
