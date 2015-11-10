import java.io.*;
import java.math.BigInteger;
import java.nio.Buffer;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by florian on 09.11.2015.
 */
public class Stats {

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("popularities.txt"));
            BufferedWriter writer = null;
            String line;
            int nrEntriesForPeriod = 0;
            String period = "";
            boolean start = true;
            while ((line = reader.readLine()) != null) {

                if (line.startsWith("Hour")) {
                    if (start == false) {
                        System.out.println(period + ": " + nrEntriesForPeriod + " Entries");
                        writer.close();
                        processPopularities(period);
                    } else {
                        start = false;
                    }
                    period = line;
                    nrEntriesForPeriod = 0;
                    writer = new BufferedWriter(new FileWriter(line + ".csv"));
                } else {
                    String[] split = line.split(" ");
                    String id = split[0];
                    Integer views = Integer.parseInt(split[1]);
                    writer.write(id + ";" + views + "\n");
                    nrEntriesForPeriod++;
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processPopularities(String line) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(line + ".csv"));
            TreeMap<Integer, String> sortedViews = new TreeMap();

            String line2;
            while ((line2 = reader.readLine()) != null) {
                String[] split = line2.split(";");
                sortedViews.put(Integer.parseInt(split[1]), split[0]);
            }

            Map<Double, String> topTenPopularities = new TreeMap<>();
            final double[] totalViews = {0};
            sortedViews.descendingKeySet().stream().limit(30).forEach(key -> {
                totalViews[0] += key + 0.0;
                topTenPopularities.put(key + 0.0, sortedViews.get(key));
            });
            BufferedWriter writer = new BufferedWriter(new FileWriter(line + "-top30.csv"));
            topTenPopularities.keySet().forEach(key -> {
                try {
                    writer.write(topTenPopularities.get(key) + ";" + key / totalViews[0] + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
