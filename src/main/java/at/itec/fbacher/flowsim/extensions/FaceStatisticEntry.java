package at.itec.fbacher.flowsim.extensions;

/**
 * Created by florian on 16.11.2015.
 */
public class FaceStatisticEntry {

    private int sentInterests = 0;
    private int sentDataPackets = 0;
    private int receivedInterests = 0;
    private int receivedDataPackets = 0;

    public int getSentInterests() {
        return sentInterests;
    }

    public void incrementSentInterests() {
        this.sentInterests++;
    }

    public int getSentDataPackets() {
        return sentDataPackets;
    }

    public void incrementSentDataPackets() {
        this.sentDataPackets++;
    }

    public int getReceivedInterests() {
        return receivedInterests;
    }

    public void incrementReceivedInterests() {
        this.receivedInterests++;
    }

    public int getReceivedDataPackets() {
        return receivedDataPackets;
    }

    public void incrementReceivedDataPackets() {
        this.receivedDataPackets++;
    }

}
