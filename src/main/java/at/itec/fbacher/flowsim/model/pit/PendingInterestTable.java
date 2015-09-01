package at.itec.fbacher.flowsim.model.pit;

import at.itec.fbacher.flowsim.model.Face;
import at.itec.fbacher.flowsim.model.Interest;
import at.itec.fbacher.flowsim.model.Node;
import at.itec.fbacher.flowsim.sim.Scheduler;
import at.itec.fbacher.flowsim.sim.SimulationEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by florian on 13.08.2015.
 */
public class PendingInterestTable {

    private Node node;

    private Map<String, PitEntry> pit = new HashMap<>();

    private Map<PitEntry, SimulationEvent> timeoutEvents = new HashMap<>();

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Map<String, PitEntry> getPit() {
        return pit;
    }

    public void setPit(Map<String, PitEntry> pit) {
        this.pit = pit;
    }

    public PitEntry addPitEntry(Interest interest, Face inFace) {
        PitEntry pitEntry;
        if (!pit.containsKey(interest.getName())) {
            pitEntry = new PitEntry();
            pit.put(interest.getName(), pitEntry);
            scheduleInterestTimeout(interest, pitEntry);
        } else {
            pitEntry = pit.get(interest.getName());

            //cancel unsatisfy timer
            SimulationEvent unsatisfyEvent = timeoutEvents.get(pitEntry);
            Scheduler.getInstance().cancelEvent(unsatisfyEvent);
            scheduleInterestTimeout(interest, pitEntry);
        }
        if (pitEntry.onInterest(interest, inFace))
            return pitEntry;
        else return null;
    }

    private void scheduleInterestTimeout(Interest interest, PitEntry pitEntry) {
        SimulationEvent simulationEvent = Scheduler.getInstance()
                .scheduleEventIn(interest.getTimeout(), () -> clearTimedOutInterests(interest));
        timeoutEvents.put(pitEntry, simulationEvent);
    }

    public void clearTimedOutInterests(Interest interest) {
        node.onTimedOutInterest(interest, pit.get(interest.getName()));
    }

    public void sentInterest(Interest interest, Face outFace) {
        PitEntry pitEntry = pit.get(interest.getName());

        if (pitEntry != null)
            pitEntry.addOutRecord(interest, outFace);
    }

    public void clearPitEntry(String name) {
        pit.remove(name);
    }
}
