package at.itec.fbacher.flowsim.model;

/**
 * Created by florian on 12.08.2015.
 */
public class Interest extends Packet {

    public static final int INTEREST_SIZE = 50;
    private long timeout;
    private int nonce;

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    @Override
    public int getSize() {
        return INTEREST_SIZE;
    }
}
