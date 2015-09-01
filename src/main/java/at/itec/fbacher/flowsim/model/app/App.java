package at.itec.fbacher.flowsim.model.app;


import at.itec.fbacher.flowsim.model.Data;
import at.itec.fbacher.flowsim.model.Face;
import at.itec.fbacher.flowsim.model.Interest;
import at.itec.fbacher.flowsim.model.Node;
import at.itec.fbacher.flowsim.sim.Scheduler;

/**
 * Created by florian on 13.08.2015.
 */
public abstract class App {

    protected Face appFace = new Face();
    protected Node node;

    public App() {
        appFace.setApp(this);
    }

    public void startAt(long when) {
        Scheduler.getInstance().scheduleEventAt(when, this::onStartApplication);
    }

    public void stopAt(long when) {
        Scheduler.getInstance().scheduleEventAt(when, this::onStopApplication);
    }

    public abstract void onStartApplication();
    public abstract void onInterest(Interest interest);
    public abstract void onData(Data data);
    public abstract void onStopApplication();

    public Face getAppFace() {
        return appFace;
    }

    public void setAppFace(Face appFace) {
        this.appFace = appFace;
        this.appFace.setApp(this);
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}
