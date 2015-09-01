package at.itec.fbacher.flowsim.model.cs;

import at.itec.fbacher.flowsim.model.Data;
import at.itec.fbacher.flowsim.sim.Simulator;

/**
 * Created by florian on 24.08.2015.
 */
public class ContentStoreEntry {

    private Data data;

    private long lastAccess;
    private int nrAccesses;

    public ContentStoreEntry(Data data) {
        this.data = data;
        lastAccess = Simulator.getInstance().getCurrentTime();
        nrAccesses = 0;
    }

    public Data getData() {
        nrAccesses++;
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public long getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(long lastAccess) {
        this.lastAccess = lastAccess;
    }

    public int getNrAccesses() {
        return nrAccesses;
    }

    public void setNrAccesses(int nrAccesses) {
        this.nrAccesses = nrAccesses;
    }
}
