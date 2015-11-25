package at.itec.fbacher.flowsim.extensions.app;

import at.itec.fbacher.flowsim.events.ApplicationEvent;
import at.itec.fbacher.flowsim.events.ContentRequestedEvent;
import at.itec.fbacher.flowsim.events.EventPublisher;
import at.itec.fbacher.flowsim.events.NextHourEvent;
import at.itec.fbacher.flowsim.extensions.app.tg.content.ContentInfo;
import at.itec.fbacher.flowsim.extensions.app.tg.popularity.PopularityItem;
import at.itec.fbacher.flowsim.extensions.app.tg.traffic.TrafficStatistics;
import at.itec.fbacher.flowsim.model.Data;
import at.itec.fbacher.flowsim.model.Interest;
import at.itec.fbacher.flowsim.model.app.App;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by florian on 05.11.2015.
 */
public class Client extends App {
    private double bandwidth = 1 * 1024*1024;
    private boolean active = false;
    private NdnFileRequester ndnFileRequester;

    private ClientStatistics statistics;

    private ReentrantLock lock = new ReentrantLock();

    public Client() {
        EventPublisher.getInstance().register(this, NextHourEvent.class);
    }

    @Override
    public void onStartApplication() {
        try {
            if (!Files.isDirectory(Paths.get("output"))) {
                Files.createDirectory(Paths.get("output"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        proceed();
    }

    @Override
    public void onInterest(Interest interest) {

    }

    @Override
    public void onData(Data data) {
        if (statistics != null) {
            lock.lock();
            statistics.onData(data);
            lock.unlock();
        }
    }

    @Override
    public void onStopApplication() {

    }

    @Override
    public void sendInterest(Interest interest) {
        super.sendInterest(interest);
        if (statistics != null) {
            lock.lock();
            statistics.sentInterest(interest);
            lock.unlock();
        }
    }

    public double getBandwidth() {
        return bandwidth;
    }

    @Override
    public void handleEvent(ApplicationEvent evt) {
        super.handleEvent(evt);
        if (evt instanceof NextHourEvent) {
            handleNextHourEvent();
        }
    }

    private void handleNextHourEvent() {
        proceed();
    }

    private void logStats() {
        //BufferedWriter writer = Files.newBufferedWriter(Paths.get())
        if (statistics != null) {
            lock.lock();
            statistics.export();
            statistics.reset();
            lock.unlock();
        }
    }

    private void proceed() {
        double trafficLoadForHour = TrafficStatistics.getTrafficLoadForHour() + 0.2;
        double random = Math.random();
        if (random <= trafficLoadForHour) {
            active = true;
        } else {
            active = false;
        }

        if (appFace.getLink() == null) {
            System.out.println("error");
        }

        if (active) {
            if ((ndnFileRequester == null) || ndnFileRequester.isFinished()) {
                requestContent();
            }
        }
    }

    private void requestContent() {
        PopularityItem contentItem = requestNextContentItem();
        if (contentItem == null) {
            active = false; return;
        }
        if (statistics == null) {
            statistics = new ClientStatistics("client-" + getNode().getId(), "csv", 5);  //measure bandwidth every 5s
        }
        ContentInfo contentInfo = new ContentInfo(contentItem.getContentName(), 0.5 * Math.random());
        ndnFileRequester = new NdnFileRequester(this, contentInfo,
                () -> requesterFinished());
        ndnFileRequester.doRequest();
        EventPublisher.getInstance().publishEvent(new ContentRequestedEvent(contentInfo.getContentName()));
    }

    private void requesterFinished() {
        logStats();
        proceed();
    }

    private PopularityItem requestNextContentItem() {
        PopularityItem contentItem = null;
        List<PopularityItem> popularitiesForHour = TrafficStatistics.getPopularitiesForHour();

        double random = Math.random();
        double tmp = 0.0;

        for (int i = 0; i < popularitiesForHour.size(); i++) {
            PopularityItem item = popularitiesForHour.get(i);
            tmp += item.getPopularity();
            if (random <= tmp) {
                contentItem = item;
                continue;
            }
        }
        return contentItem;
    }

    public void setBandwidth(double bandwidth) {
        this.bandwidth = bandwidth;
    }
}
