package at.itec.fbacher.flowsim.extensions.strategies.sdn;

/**
 * Created by florian on 26.11.2015.
 */
public class FlowEntry {
    int faceId;
    long receivedInterests;
    long satisfiedInterests;
    long unsatisfiedInterests;
    long bytesReceived;
    long status;
    double probability;
    int cost;
    long timeout;
}
