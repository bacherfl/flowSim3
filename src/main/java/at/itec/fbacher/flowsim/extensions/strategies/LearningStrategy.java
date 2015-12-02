package at.itec.fbacher.flowsim.extensions.strategies;

import at.itec.fbacher.flowsim.model.Data;
import at.itec.fbacher.flowsim.model.Face;
import at.itec.fbacher.flowsim.model.Interest;
import at.itec.fbacher.flowsim.model.fw.FibEntry;
import at.itec.fbacher.flowsim.model.pit.PitEntry;

import java.util.List;

/**
 * Created by florian on 25.08.2015.
 */
public class LearningStrategy extends BroadcastStrategy {

    @Override
    public void onInterest(Interest interest, Face inFace, PitEntry pitEntry) {
        List<FibEntry> matchingFibEntries = fib.getMatchingFibEntries(interest.getName());
        if (matchingFibEntries != null) {
            FibEntry fibEntry = matchingFibEntries.get(0);
            node.sendInterest(interest, fibEntry.getFace());
        } else {
            super.onInterest(interest, inFace, pitEntry);
        }
    }

    @Override
    public void onData(Data data, Face inFace, PitEntry pitEntry) {
        super.onData(data, inFace, pitEntry);
        fib.addFibEntry(data.getPrefix(), inFace, 0.0);
    }

    @Override
    public void onTimedOutInterest(Interest interest, PitEntry pitEntry) {
        System.out.println("Interest timeout");
        fib.deleteFibEntry(interest.getPrefix());
    }
}
