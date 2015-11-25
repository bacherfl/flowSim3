import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Created by florian on 25.11.2015.
 */
public class ArffFileGenerator {

    public static void main (String[] args) {
        try {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get("statistics/popularityModel.arff"));
            writeHeader(writer);

            final int[] index = {1};
            Files.list(Paths.get("statistics"))
                    .filter(file -> (file.toString().endsWith("-top30.csv")) && !(file.toString().endsWith("0-top30.csv")))
                    .forEach(file -> {
                        if (index[0] == 1) {
                            extractFile(file, writer, index[0], 1);
                        } else {
                            extractFile(file, writer, index[0], 2);
                        }
                        index[0]++;
                    });
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void extractFile(Path file, BufferedWriter writer, int index, int goBack) {
        try {
            BufferedReader reader = Files.newBufferedReader(file);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(";");
                String contentId = split[0];
                String popularity = split[1];
                String[] previousValues = {"?", "?"};
                for (int i = goBack; i >= 1; i--) {
                    previousValues[i-1] = getPreviousPopularity(contentId, index, i);
                }
                writer.write(previousValues[0] + "," + previousValues[1] + "," + popularity);
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getPreviousPopularity(String contentId, int index, int delta) {
        int newIndex = index - delta;
        try {
            Path statistics = Files.list(Paths.get("statistics")).filter(file -> file.toString().endsWith(newIndex + "-top30.csv"))
                    .findFirst().get();

            BufferedReader reader = Files.newBufferedReader(statistics);

            Optional<String> previousLine = reader.lines().filter(line -> line.toString().startsWith(contentId))
                    .findFirst();

            if (!previousLine.isPresent()) {
                return "?";
            }

            return previousLine.get().split(";")[1];

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "?";
    }

    private static void writeHeader(BufferedWriter writer) throws IOException {
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
