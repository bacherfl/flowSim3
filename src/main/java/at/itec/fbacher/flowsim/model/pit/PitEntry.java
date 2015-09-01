package at.itec.fbacher.flowsim.model.pit;

import at.itec.fbacher.flowsim.model.Face;
import at.itec.fbacher.flowsim.model.Interest;
import at.itec.fbacher.flowsim.sim.Simulator;

import java.util.*;

/**
 * Created by florian on 13.08.2015.
 */
public class PitEntry {

    Map<Face, InRecord> inRecords = new HashMap<>();
    Map<Face, OutRecord> outRecords = new HashMap<>();

    List<Integer> nonces = new ArrayList<>();


    public Map<Face, InRecord> getInRecords() {
        return inRecords;
    }

    public void setInRecords(Map<Face, InRecord> inRecords) {
        this.inRecords = inRecords;
    }

    public Map<Face, OutRecord> getOutRecords() {
        return outRecords;
    }

    public void setOutRecords(Map<Face, OutRecord> outRecords) {
        this.outRecords = outRecords;
    }

    public List<Integer> getNonces() {
        return nonces;
    }

    public void setNonces(List<Integer> nonces) {
        this.nonces = nonces;
    }

    public boolean onInterest(Interest interest, Face inFace) {
        Optional<Integer> exists = nonces.stream().filter(n -> interest.getNonce() == n).findAny();
        if (exists.isPresent()) {
            return false;
        }
        nonces.add(interest.getNonce());
        InRecord inRecord = new InRecord(interest.getNonce(), Simulator.getInstance().getCurrentTime(), interest);
        inRecords.put(inFace, inRecord);
        return true;
    }

    public void addOutRecord(Interest interest, Face outFace) {
        OutRecord outRecord = new OutRecord(outFace, interest.getNonce(), Simulator.getInstance().getCurrentTime());
        outRecords.put(outFace, outRecord);
    }
}
