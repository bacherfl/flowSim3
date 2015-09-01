package at.itec.fbacher.flowsim.model.pit;

import at.itec.fbacher.flowsim.model.Interest;

/**
 * Created by florian on 13.08.2015.
 */
public class InRecord {

    private int lastNonce;
    private long lastRenewed;
    private Interest interest;

    public InRecord(int lastNonce, long lastRenewed, Interest interest) {
        this.lastNonce = lastNonce;
        this.lastRenewed = lastRenewed;
        this.interest = interest;
    }

    public int getLastNonce() {
        return lastNonce;
    }

    public void setLastNonce(int lastNonce) {
        this.lastNonce = lastNonce;
    }

    public long getLastRenewed() {
        return lastRenewed;
    }

    public void setLastRenewed(long lastRenewed) {
        this.lastRenewed = lastRenewed;
    }

    public Interest getInterest() {
        return interest;
    }

    public void setInterest(Interest interest) {
        this.interest = interest;
    }
}
