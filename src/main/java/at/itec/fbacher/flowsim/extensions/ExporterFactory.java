package at.itec.fbacher.flowsim.extensions;

/**
 * Created by florian on 23.11.2015.
 */
public class ExporterFactory {
    public ExporterFactory() {

    }

    public StatisticsExporter createExporter(String name, String exporterType) {
        StatisticsExporter exporter = null;

        if (exporterType.equalsIgnoreCase("csv")) {
            return new CSVStatisticsExporter(name);
        }

        return exporter;
    }
}
