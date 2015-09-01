package at.itec.fbacher.flowsim.extensions.app;

import at.itec.fbacher.flowsim.model.app.App;
import at.itec.fbacher.flowsim.model.Data;
import at.itec.fbacher.flowsim.model.Interest;
import at.itec.fbacher.flowsim.sim.Simulator;

/**
 * Created by florian on 24.08.2015.
 */
public class SimpleProducer extends App {

    private String prefix;

    public SimpleProducer(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void onStartApplication() {

    }

    @Override
    public void onInterest(Interest interest) {
        System.out.println(Simulator.getInstance().getCurrentTime() + " [App] Node " + node.getId() + " received interest " + interest.getName());
        if (interest.getName().startsWith(prefix)) {
            Data data = new Data();
            data.setSize(4096);
            data.setName(interest.getName());
            appFace.sendData(data);
        }
    }

    @Override
    public void onData(Data data) {

    }

    @Override
    public void onStopApplication() {

    }
}
