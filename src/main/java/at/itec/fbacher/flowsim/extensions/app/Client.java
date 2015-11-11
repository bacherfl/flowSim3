package at.itec.fbacher.flowsim.extensions.app;

import at.itec.fbacher.flowsim.events.ApplicationEvent;
import at.itec.fbacher.flowsim.events.EventPublisher;
import at.itec.fbacher.flowsim.events.NextHourEvent;
import at.itec.fbacher.flowsim.extensions.app.tg.content.ContentInfo;
import at.itec.fbacher.flowsim.extensions.app.tg.popularity.PopularityItem;
import at.itec.fbacher.flowsim.extensions.app.tg.traffic.TrafficStatistics;
import at.itec.fbacher.flowsim.model.Data;
import at.itec.fbacher.flowsim.model.Interest;
import at.itec.fbacher.flowsim.model.app.App;

import java.util.List;

/**
 * Created by florian on 05.11.2015.
 */
public class Client extends App {
    private double bandwidth = 10 * 1024*1024;
    private boolean active = false;
    private NdnFileRequester ndnFileRequester;

    public Client() {
        EventPublisher.getInstance().register(this, NextHourEvent.class);
    }

    @Override
    public void onStartApplication() {
        proceed();
    }

    @Override
    public void onInterest(Interest interest) {

    }

    @Override
    public void onData(Data data) {

    }

    @Override
    public void onStopApplication() {

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

    private void proceed() {
        double trafficLoadForHour = TrafficStatistics.getTrafficLoadForHour() + 0.2;
        double random = Math.random();
        if (random <= trafficLoadForHour) {
            active = true;
        } else {
            active = false;
        }

        System.out.println("Client " + getNode().getId() + " is now " + active);

        if (appFace.getLink() == null) {
            System.out.println("error");
        }

        if (active) {
            if ((ndnFileRequester != null) && ndnFileRequester.isFinished()) {
                ndnFileRequester.setFinishedCallback(() -> proceed());
            } else {
                PopularityItem contentItem = requestNextContentItem();
                ContentInfo contentInfo = new ContentInfo(contentItem.getContentName(), 30);
                ndnFileRequester = new NdnFileRequester(this, contentInfo, () -> { });
                ndnFileRequester.doRequest();
            }
        }
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
