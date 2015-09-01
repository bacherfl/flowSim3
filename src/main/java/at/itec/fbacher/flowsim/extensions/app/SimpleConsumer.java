package at.itec.fbacher.flowsim.extensions.app;


import at.itec.fbacher.flowsim.model.Data;
import at.itec.fbacher.flowsim.model.Interest;
import at.itec.fbacher.flowsim.model.app.App;
import at.itec.fbacher.flowsim.sim.Scheduler;
import at.itec.fbacher.flowsim.sim.Simulator;

/**
 * Created by florian on 13.08.2015.
 */
public class SimpleConsumer extends App {

    private boolean consume = false;
    private String prefix;

    private int chunkNr = 0;

    private long interval = 10000;  // 1 Interest/s
    private int nInterests = 100000;

    public SimpleConsumer() {
        prefix = "/name";
    }

    public SimpleConsumer(boolean consume, String prefix) {
        this.consume = consume;
        this.prefix = prefix;
    }

    public SimpleConsumer(boolean consume, String prefix, int bitrate) {
        this.consume = consume;
        this.prefix = prefix;
        double interestsPerSecond = (bitrate + 0.0) / ((Interest.INTEREST_SIZE + Data.DATA_SIZE) * 8);
        interval = (long) (Simulator.SIMULATION_TICKS_PER_SECOND / interestsPerSecond);
    }

    @Override
    public void onStartApplication() {
        if (consume) {
            sendNextInterest();
        }
    }

    private void sendNextInterest() {
        Interest interest = new Interest();
        interest.setNonce((int) (Math.random() * 1000));
        interest.setName(prefix + "%" + chunkNr++);
        interest.setTimeout(5000);
        interest.setSize(50);
        System.out.println(Simulator.getInstance().getCurrentTime() + " [App] Node " + node.getId() + " sending interest " + interest.getName());
        appFace.sendInterest(interest);
        if (chunkNr < nInterests)
            Scheduler.getInstance().scheduleEventIn(interval, this::sendNextInterest);
    }

    @Override
    public void onInterest(Interest interest) {
        System.out.println(Simulator.getInstance().getCurrentTime() + " [App] Node " + node.getId() + " received interest " + interest.getName());
        // Data data = new Data();
        // data.setSize(4096);
        // data.setName(interest.getName());
        // appFace.sendData(data);
    }

    @Override
    public void onData(Data data) {
        System.out.println(Simulator.getInstance().getCurrentTime() + " [App] Node " + node.getId() + " received data " + data.getName());
    }

    @Override
    public void onStopApplication() {

    }

    public boolean isConsume() {
        return consume;
    }

    public void setConsume(boolean consume) {
        this.consume = consume;
    }
}
