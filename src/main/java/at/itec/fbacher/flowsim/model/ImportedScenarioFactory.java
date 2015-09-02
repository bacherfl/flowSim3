package at.itec.fbacher.flowsim.model;

import at.itec.fbacher.flowsim.model.Scenario;
import at.itec.fbacher.flowsim.model.ScenarioFactory;
import at.itec.fbacher.flowsim.scenarios.ImportedScenario;

import java.io.File;

/**
 * Created by florian on 02.09.2015.
 */
public class ImportedScenarioFactory extends ScenarioFactory {

    public Scenario createScenario(File scenarioFile) {
        Scenario scenario = null;

        if(scenarioFile.getName().endsWith(".dat")) {
            scenario = new ImportedScenario(new DatFileParser(scenarioFile));
        }

        return scenario;
    }
}
