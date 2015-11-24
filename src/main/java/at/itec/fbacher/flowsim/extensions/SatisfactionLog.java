package at.itec.fbacher.flowsim.extensions;

/**
 * Created by florian on 24.11.2015.
 */
public class SatisfactionLog {

    private int sentInterests = 0;
    private int satisfiedInterests = 0;

    public int getSentInterests() {
        return sentInterests;
    }

    public void incrementSentInterests() {
        this.sentInterests++;
    }

    public int getSatisfiedInterests() {
        return satisfiedInterests;
    }

    public void incrementSatisfiedInterests() {
        this.satisfiedInterests++;
    }

    public double getSatisfactionRate() {
        return (satisfiedInterests + 0.0) / sentInterests;
    }
}
