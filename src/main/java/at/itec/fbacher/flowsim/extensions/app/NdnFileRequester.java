package at.itec.fbacher.flowsim.extensions.app;

import at.itec.fbacher.flowsim.extensions.app.tg.content.ContentInfo;
import at.itec.fbacher.flowsim.model.Data;
import at.itec.fbacher.flowsim.model.Interest;
import at.itec.fbacher.flowsim.sim.Scheduler;
import at.itec.fbacher.flowsim.sim.SimulationEvent;
import at.itec.fbacher.flowsim.sim.Simulator;

/**
 * Created by florian on 10.09.2015.
 */
public class NdnFileRequester {
    private final Client client;
    private ContentInfo contentInfo;
    private SimulationEvent finishedCallback;
    private final long intervalBetweenInterests;
    private int chunkNr = 0;
    private int nInterests;

    public NdnFileRequester(Client client, ContentInfo contentInfo, SimulationEvent finishedCallback) {
        this.client = client;
        this.contentInfo = contentInfo;
        this.finishedCallback = finishedCallback;

        double interestsPerSecond = (client.getBandwidth() + 0.0) / ((Interest.INTEREST_SIZE + Data.DATA_SIZE) * 8);
        intervalBetweenInterests = (long) (Simulator.SIMULATION_TICKS_PER_SECOND / interestsPerSecond);
        nInterests = (int) (contentInfo.getSizeInBytes() / Data.DATA_SIZE);
    }

    public void doRequest() {
        Interest interest = new Interest();
        interest.setNonce((int) (Math.random() * 1000));
        interest.setName(contentInfo.getContentName() + "%" + chunkNr++);
        interest.setTimeout(500000);
        interest.setSize(50);

        client.sendInterest(interest);
        if (chunkNr < nInterests) {
            Scheduler.getInstance().scheduleEventIn(intervalBetweenInterests, this::doRequest);
        } else {
            finishedCallback.execute();
        }
    }

    public boolean isFinished() {
        return chunkNr >= nInterests;
    }

    public SimulationEvent getFinishedCallback() {
        return finishedCallback;
    }

    public void setFinishedCallback(SimulationEvent finishedCallback) {
        this.finishedCallback = finishedCallback;
    }
}
