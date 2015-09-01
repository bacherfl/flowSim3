package at.itec.fbacher.flowsim.model.pit;

import at.itec.fbacher.flowsim.model.Face;

/**
 * Created by florian on 13.08.2015.
 */
public class OutRecord {
    private Face face;
    private int lastNonce;
    private long lastRenewed;

    public OutRecord(Face face, int lastNonce, long lastRenewed) {
        this.face = face;
        this.lastNonce = lastNonce;
        this.lastRenewed = lastRenewed;
    }

    public Face getFace() {
        return face;
    }

    public void setFace(Face face) {
        this.face = face;
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
}
