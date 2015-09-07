package at.itec.fbacher.flowsim.extensions.app;

import at.itec.fbacher.flowsim.log.Logger;
import at.itec.fbacher.flowsim.model.app.App;
import at.itec.fbacher.flowsim.model.Data;
import at.itec.fbacher.flowsim.model.Interest;
import at.itec.fbacher.flowsim.sim.Simulator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by florian on 24.08.2015.
 */
public class SimpleProducer extends App {

    private List<String> prefixes = new ArrayList<>();


    public SimpleProducer() {
    }

    public SimpleProducer(List<String> prefixes) {
        this.prefixes = prefixes;
    }

    @Override
    public void onStartApplication() {

    }

    @Override
    public void onInterest(Interest interest) {
        //Logger.getInstance().log("[App] Node " + node.getId() + " received interest " + interest.getName());
        prefixes.forEach(prefix -> {
            if (interest.getName().startsWith(prefix)) {
                Data data = new Data();
                data.setSize(4096);
                data.setName(interest.getName());
                appFace.sendData(data);
            }
        });
    }

    public List<String> getPrefixes() {
        return prefixes;
    }

    @Override
    public void onData(Data data) {

    }

    @Override
    public void onStopApplication() {

    }
}
