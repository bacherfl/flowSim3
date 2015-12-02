package at.itec.fbacher.flowsim.extensions.strategies.sdn;

import at.itec.fbacher.flowsim.model.Face;
import at.itec.fbacher.flowsim.sim.Scheduler;
import at.itec.fbacher.flowsim.sim.Simulator;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Created by florian on 26.11.2015.
 */
public class FlowTableManager {

    List<Face> faces = new ArrayList<>();
    Map<String, List<FlowEntry>> flowTable = new HashMap<>();

    static final double MIN_SAT_RATIO = 0.95;
    static final double SHORTEST_PATH_FRACTION = 0.85;
    static final int INTEREST_INTERVAL = 50;
    static final int FACE_STATUS_GREEN = 0;
    static final int FACE_STATUS_YELLOW = 1;
    static final int FACE_STATUS_RED = 2;

    ReentrantLock lock = new ReentrantLock();

    public void addFace(Face face) {
        faces.add(face);
    }

    public List<String> getFlowsOfFace(Integer face) {

        return flowTable.keySet().stream().filter(
                prefix -> flowTable.get(prefix).stream().filter(
                        fe -> fe.faceId == face).findFirst().isPresent())
                .collect(Collectors.toList());

    }

    public void pushRule(String prefix, final int faceId, int cost) {
        lock.lock();
        List<FlowEntry> flowEntries = flowTable.get(prefix);

        boolean found = flowEntries.stream()
                .filter(flowEntry -> flowEntry.faceId == faceId)
                .findFirst()
                .isPresent();

        if (!found)
        {
            FlowEntry fe = new FlowEntry();
            fe.bytesReceived = 0;
            fe.faceId = faceId;
            fe.receivedInterests = 0;
            fe.satisfiedInterests = 0;
            fe.unsatisfiedInterests = 0;
            fe.status = FACE_STATUS_GREEN;
            fe.probability = 0.0;
            fe.cost = cost;
            addFlowEntry(prefix, fe);
            //flowTable[prefix].push_back(fe);
        }
        if (flowTable.get(prefix).size() == 1) {
            //Simulator::Schedule(Seconds(10.0), &FlowTableManager::ClearTimedOutFlowEntry, this, prefix);
            Scheduler.getInstance().scheduleEventInSeconds(10, () -> clearTimedOutFlowEntry(prefix));
        }
        lock.unlock();
    }

    private void clearTimedOutFlowEntry(String prefix) {
        lock.lock();
        flowTable.get(prefix).clear();
        lock.lock();
    }

    private void addFlowEntry(String prefix, FlowEntry fe) {
        flowTable.get(prefix).add(fe);
    }

    boolean tryUpdateFaceProbabilities(String prefix)
    {
        List<FlowEntry> flowEntries = flowTable.get(prefix);
        final double[] fractionToShift = new double[1];
        final double[] shifted = new double[1];
        final boolean[] success = {true};

        flowEntries.forEach(fe -> {
            double successRate = calculateSuccessRate(fe);
            if (successRate < MIN_SAT_RATIO) {
                fractionToShift[0] = MIN_SAT_RATIO - successRate;
                shifted[0] = 0;

                flowEntries.forEach(fe2 -> {
                    double successRate2 = calculateSuccessRate(fe2);

                    if (successRate2 > MIN_SAT_RATIO) {
                        double shift = Math.min(fractionToShift[0], successRate2 - MIN_SAT_RATIO);
                        shift = Math.min(shift, 1 - fe2.probability);
                        fe.probability -= shift;
                        fe2.probability += shift;
                        fractionToShift[0] -= shift;
                        shifted[0] += shift;
                    }
                });

                if (fractionToShift[0] - shifted[0] > 0)
                    success[0] = false;
            }
        });


        return success[0];
    }

    private double calculateSuccessRate(FlowEntry fe) {
        double successRate =
                fe.satisfiedInterests + fe.unsatisfiedInterests == 0 ? 1 :
                        (double)fe.satisfiedInterests / (fe.satisfiedInterests + fe.unsatisfiedInterests);

        return successRate;
    }

    public Face getFaceForPrefix(String prefix, List<Integer> exclude) {
        Face face = null;

        lock.lock();
        //PrintFlowTableForPrefix(prefix);
        if (flowTable.get(prefix) != null && flowTable.get(prefix).size() > 0)
        {
            double p = Math.random();

            if (p < SHORTEST_PATH_FRACTION) {
                face = getFaceForPrefixBasedOnCost(prefix, exclude);
                //return GetFaceForPrefixBasedOnReliability(prefix, inFaceId);
            }
            else {
                face = getRandomFaceForPrefix(prefix, exclude);
            }
            /*
            if (face == null) {
                face = getRandomFaceForPrefix(prefix, exclude);
            }
            */
        }
        lock.unlock();
        return face;
    }

    private Face getRandomFaceForPrefix(String prefix, List<Integer> exclude) {
        List<FlowEntry> candidates = getCandidateFaces(prefix, exclude);
        FlowEntry randomEntry = candidates.get((int) (Math.random() % candidates.size()));
        updateSelectedFlowEntry(randomEntry);

        Optional<Face> outFace = faces.stream().filter(face -> face.getFaceId() == randomEntry.faceId).findFirst();
        if (outFace.isPresent())
            return outFace.get();
        else return null;
    }

    private Face getFaceForPrefixBasedOnCost(String prefix, List<Integer> exclude) {
        List<FlowEntry> candidates = getCandidateFaces(prefix, exclude);

        if (candidates.size() == 0)
            return null;

        FlowEntry minCostFlowEntry = Collections.min(candidates, Comparator.comparing(candidate -> candidate.cost));

        updateSelectedFlowEntry(minCostFlowEntry);

        return faces.stream().filter(face -> face.getFaceId() == minCostFlowEntry.faceId).findFirst().get();
    }

    private void updateSelectedFlowEntry(FlowEntry flowEntry) {
        flowEntry.receivedInterests++;

        if (flowEntry.receivedInterests >= INTEREST_INTERVAL) {
            flowEntry.receivedInterests = 0;
            flowEntry.satisfiedInterests = 0;
            flowEntry.unsatisfiedInterests = 0;
        }
    }

    private List<FlowEntry> getCandidateFaces(String prefix, List<Integer> exclude) {
        return flowTable.get(prefix).stream()
                .filter(fe -> !exclude.stream().filter(excl -> excl == fe.faceId).findFirst().isPresent())
                .collect(Collectors.toList());
    }

    public LinkRepairAction interestUnsatisfied(String prefix, int faceId) {
        List<FlowEntry> flowEntries = flowTable.get(prefix);
        LinkRepairAction action = new LinkRepairAction();
        action.setRepair(false);

        flowEntries.forEach(fe -> {
            if (fe.faceId == faceId)
            {
                lock.lock();
                fe.unsatisfiedInterests++;
                //TryUpdateFaceProbabilities(prefix);
                //check if ratio of unsatisfied to satisfied requests exceeds some limit and tell the controller
                double successRate = calculateSuccessRate(fe);

                lock.unlock();
                if ((successRate < MIN_SAT_RATIO) && (fe.status == FACE_STATUS_GREEN))
                {
                    fe.status = FACE_STATUS_RED;
                    action.setRepair(true);
                    action.setFailRate(1 - successRate);
                }
                else {
                    action.setRepair(false);
                }
            }
        });

        return action;
    }

    public LinkRepairAction interestSatisfied(String prefix, int faceId) {
        List<FlowEntry> flowEntries = flowTable.get(prefix);
        LinkRepairAction action = new LinkRepairAction();
        action.setRepair(false);

        flowEntries.forEach(fe -> {
            if (fe.faceId == faceId) {
                lock.lock();
                fe.unsatisfiedInterests++;
                //TryUpdateFaceProbabilities(prefix);
                //check if ratio of unsatisfied to satisfied requests exceeds some limit and tell the controller
                double successRate = calculateSuccessRate(fe);

                lock.unlock();
                if ((successRate >= MIN_SAT_RATIO) && (fe.status == FACE_STATUS_RED)) {
                    fe.status = FACE_STATUS_GREEN;
                    action.setRepair(true);
                    action.setFailRate(1 - successRate);
                } else {
                    action.setRepair(false);
                }
            }
        });

        return action;
    }
}
