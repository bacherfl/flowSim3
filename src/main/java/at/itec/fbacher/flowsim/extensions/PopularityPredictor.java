package at.itec.fbacher.flowsim.extensions;

/**
 * Created by florian on 16.11.2015.
 */
public interface PopularityPredictor {

    double getPrediction(String contentId, int when);
}
