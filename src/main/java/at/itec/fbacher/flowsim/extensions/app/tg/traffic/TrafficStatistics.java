package at.itec.fbacher.flowsim.extensions.app.tg.traffic;

import at.itec.fbacher.flowsim.events.EventPublisher;
import at.itec.fbacher.flowsim.events.NextHourEvent;
import at.itec.fbacher.flowsim.extensions.app.tg.popularity.PopularityItem;
import at.itec.fbacher.flowsim.sim.Scheduler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by florian on 05.11.2015.
 */
public class TrafficStatistics {

    private static int currentHour = 0;

    public static double getTrafficLoadForHour() {
        double x = currentHour;
        double f = 1/842.637 * (
                30.063198955265648 - 26.8925597611135 * x +
                        25.850185410635614 * Math.pow(x,2) - 15.876489602335202 * Math.pow(x,3) +
                        5.2354821820381545 * Math.pow(x,4) - 0.9746273346822218 * Math.pow(x,5) +
                        0.1098946059898637 * Math.pow(x,6) - 0.007802048509068102 * Math.pow(x,7) +
                        0.0003507867809324308 * Math.pow(x,8) - 9.677541616284627* Math.pow(10,-6) * Math.pow(x,9) +
                        1.491502549293605 * Math.pow(10, -7) * Math.pow(x, 10) - 9.804228970768698 * Math.pow(10, -10) * Math.pow(x, 11));

        return f;
    }

    public static void nextHour() {
        currentHour = (currentHour + 1) % 24;
        EventPublisher.getInstance().publishEvent(new NextHourEvent());
        Scheduler.getInstance().scheduleEventInSeconds(10, TrafficStatistics::nextHour);
    }

    public static List<PopularityItem> getPopularitiesForHour() {
        List<PopularityItem> result = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("statistics/Hour-" + currentHour + "-top30.csv"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(";");
                PopularityItem popularityItem = new PopularityItem(split[0], Double.parseDouble(split[1]));
                result.add(popularityItem);
            }
            result.remove(result.size() - 1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
