package at.itec.fbacher.flowsim.extensions.app.tg.client;

import at.itec.fbacher.flowsim.extensions.app.tg.SimulationSchedule;
import at.itec.fbacher.flowsim.extensions.app.tg.content.ContentInfo;
import at.itec.fbacher.flowsim.model.Data;
import at.itec.fbacher.flowsim.model.Interest;
import at.itec.fbacher.flowsim.model.app.App;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by florian on 12/06/15.
 */
public class DummyClient extends App implements Runnable, Observer {

    public static final int TOTAL_REQUESTS = 1000;

    private int currentHour = 0;

    private int availableRequests;

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    private ScheduledFuture<?> finishRequestScheduledFuture;

    private int bandwidth;

    private ClientState state;

    private List<ContentInfo> contentRepository;
    private String id;

    public DummyClient(List<ContentInfo> contentRepository, int bandwidth, String id) {
        this.contentRepository = contentRepository;
        this.bandwidth = bandwidth;
        this.id = id;
        this.setState(new MorningClient(this));
    }

    @Override
    public void run() {
        refillAvailableRequests();
        state.requestContent();
    }

    public List<ContentInfo> getContentRepository() {
        return contentRepository;
    }

    public ClientState getState() {
        return state;
    }

    public void setState(ClientState state) {
        this.state = state;
    }

    public void sendRequest(ContentInfo contentInfo) {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getForObject(
                "http://localhost:8080/location/resolve?contentName=" + contentInfo.getContentName() + "&client=" + id,
                String.class
        );

        availableRequests--;

        //NdnFileRequester fileRequester = new NdnFileRequester(this, contentInfo, () -> requestFinished());
        //fileRequester.doRequest();

        //Scheduler.getInstance().scheduleEventInMilliSeconds((long) ((1000 * ((contentInfo.getSizeInMB() * 8) / bandwidth))), () -> requestFinished());
    }

    public int getAvailableRequests() {
        return availableRequests;
    }

    public void setAvailableRequests(int availableRequests) {
        this.availableRequests = availableRequests;
    }

    private void refillAvailableRequests() {
        availableRequests = calculateRequestsForHour();
        if (state.isInactive()) {
            state.requestContent();
        }
        currentHour = (currentHour+1) % 24;
        scheduler.schedule((Runnable) () -> refillAvailableRequests(),
                (long) ((3600 * 1000) / SimulationSchedule.SPEEDUP_FACTOR),
                TimeUnit.MILLISECONDS);
    }

    private int calculateRequestsForHour() {
        double x = currentHour;
        double f = 1/842.637 * (
                30.063198955265648 - 26.8925597611135 * x +
                25.850185410635614 * Math.pow(x,2) - 15.876489602335202 * Math.pow(x,3) +
                5.2354821820381545 * Math.pow(x,4) - 0.9746273346822218 * Math.pow(x,5) +
                0.1098946059898637 * Math.pow(x,6) - 0.007802048509068102 * Math.pow(x,7) +
                0.0003507867809324308 * Math.pow(x,8) - 9.677541616284627* Math.pow(10,-6) * Math.pow(x,9) +
                1.491502549293605 * Math.pow(10, -7) * Math.pow(x, 10) - 9.804228970768698 * Math.pow(10, -10) * Math.pow(x, 11));

        return (int) (f * TOTAL_REQUESTS);
    }

    public void requestFinished() {
        state.requestFinished();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof SimulationSchedule) {
            finishRequestScheduledFuture.cancel(true);
            state.nextState();
        }
    }

    @Override
    public void onStartApplication() {
        refillAvailableRequests();
        state.requestContent();
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

    public int getBandwidth() {
        return bandwidth;
    }
}
