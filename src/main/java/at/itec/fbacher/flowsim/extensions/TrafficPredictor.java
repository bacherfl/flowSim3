package at.itec.fbacher.flowsim.extensions;

import at.itec.fbacher.flowsim.model.Link;

/**
 * Created by florian on 16.11.2015.
 */
public interface TrafficPredictor {

    double getPrediction(Link link, int when);
}
