package at.itec.fbacher.flowsim.model;


import at.itec.fbacher.flowsim.scenarios.HelloScenario;
import at.itec.fbacher.flowsim.scenarios.RealWorldScenario;
import org.reflections.Reflections;

/**
 * Created by florian on 02.09.2015.
 */
public class ScenarioFactory {

    public Scenario createScenario(String scenarioName) {
        Scenario scenario = null;
        if ("HelloScenario".equals(scenarioName)) {
            scenario = new HelloScenario();
        } else if("RealWorldScenario".equals(scenarioName)) {
            scenario = new RealWorldScenario();
        }
        return scenario;
    }
}
