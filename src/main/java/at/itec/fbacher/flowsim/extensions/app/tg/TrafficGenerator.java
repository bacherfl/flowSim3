package at.itec.fbacher.flowsim.extensions.app.tg;

import at.itec.fbacher.flowsim.extensions.app.tg.client.DummyClient;
import at.itec.fbacher.flowsim.extensions.app.tg.content.DummyContentGenerator;
import at.itec.fbacher.flowsim.extensions.app.tg.content.ContentInfo;
import at.itec.fbacher.flowsim.extensions.app.tg.popularity.PopularitySequence;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by florian on 11/06/15.
 */
public class TrafficGenerator {

    public static void main(String[] args) {
        List<ContentInfo> contentInfoList = new DummyContentGenerator().generateDummyContent(100);

        PopularitySequence.getInstance().initialize(contentInfoList);

        SimulationSchedule schedule = new SimulationSchedule(2);

        List<Thread> clientThreads = new ArrayList<>();

        IntStream.range(0, 3).forEach(index -> {
            DummyClient dummyClient = new DummyClient(contentInfoList, 2000, "client" + index);
            schedule.addObserver(dummyClient);
            clientThreads.add(new Thread(dummyClient));
        });

        Thread scheduleThread = new Thread(schedule);
        scheduleThread.start();


        clientThreads.parallelStream().forEach(client -> client.start());

        clientThreads.parallelStream().forEach(client -> {
            try {
                client.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}
