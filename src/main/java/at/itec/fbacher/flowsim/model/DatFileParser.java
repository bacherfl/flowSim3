package at.itec.fbacher.flowsim.model;

import java.io.File;

/**
 * Created by florian on 02.09.2015.
 */
public class DatFileParser implements ScenarioFileParser {

    private final File scenarioFile;

    public DatFileParser(File scenarioFile) {
        this.scenarioFile = scenarioFile;
    }

    @Override
    public void parseFile() {

    }
}
