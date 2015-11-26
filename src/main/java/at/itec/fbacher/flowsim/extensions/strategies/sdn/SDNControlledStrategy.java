package at.itec.fbacher.flowsim.extensions.strategies.sdn;

import at.itec.fbacher.flowsim.extensions.SDNController;
import at.itec.fbacher.flowsim.model.*;
import at.itec.fbacher.flowsim.model.fw.ForwardingStrategy;
import at.itec.fbacher.flowsim.model.pit.PitEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by florian on 26.11.2015.
 */
public class SDNControlledStrategy extends ForwardingStrategy {

    FlowTableManager flowTableManager;

    Map<String, FlowEntry> flowTable;

    Map<Integer, Map<String, TokenBucket>> qosQueues;
    Map<Integer, TokenBucket> aggregateQosQueues;
    Map<Integer, Boolean> qosQueueInitialized;
    Map<String, Integer> pitTable;


    boolean initialized;
    boolean useAggregateQueuesPerFace;

    ReentrantLock lock;

    @Override
    public void onInterest(Interest interest, Face inFace, PitEntry pitEntry) {
        List<Integer> exclude = new ArrayList<>();
        exclude.add(inFace.getFaceId());
        Face outFace = selectFaceFromLocalFib(interest, exclude);

        if (outFace == null) {
            outFace = getFaceFromSDNController(interest, inFace.getFaceId());
        }

        if (outFace != null) {
            String prefix = interest.getPrefix();
            if (outFace.getFaceId() == fib.getFaces().size() - 1) {
                sendInterest(interest, outFace);
            }

            else if (hasQueue(outFace.getFaceId(), prefix)) {
                if (tryConsumeQueueToken(outFace.getFaceId(), prefix)) {
                    sendInterest(interest, outFace);
                }
                else {
                    boolean tryForwarding = true;
                    while (tryForwarding) {
                        exclude.add(outFace.getFaceId());
                        outFace = selectFaceFromLocalFib(interest, exclude);
                        if (outFace == null) {
                            break;
                        } else {
                            if (outFace.getFaceId() == fib.getFaces().size() - 1) {
                                tryForwarding = false;
                                sendInterest(interest, outFace);
                            }

                            else if (hasQueue(outFace.getFaceId(), prefix)) {
                                if (tryConsumeQueueToken(outFace.getFaceId(), prefix)) {
                                    tryForwarding = false;
                                    sendInterest(interest, outFace);
                                }
                            }
                        }
                    }
                }
            }
            else sendInterest(interest, outFace);
        }
        //we're on the target node where the prefix is available --> forward to app face
        else {
            sendInterest(interest, node.getAppFace());
        }
    }

    @Override
    public void OnData(Data data, Face inFace, PitEntry pitEntry) {
        String prefix = data.getPrefix();
        LinkRepairAction action = flowTableManager.interestSatisfied(prefix, inFace.getFaceId());
        if (action.isRepair())
        {
            SDNController.linkRecovered(node.getId(), inFace.getFaceId(), prefix, action.getFailRate());
        }
    }

    @Override
    public void onDroppedPacket(Packet packet, Face face) {
        logDroppedInterest(packet.getPrefix(), face.getFaceId());
    }

    @Override
    public void onTimedOutInterest(Interest interest, PitEntry pitEntry) {

    }

    public void assignBandwidth(String prefix, int faceId, int bitrate)
    {
        //qosQueues[faceId][prefix] = new ns3::ndn::utils::QoSQueue(bitrate);
        lock.lock();

        if (aggregateQosQueues.get(faceId) == null) {
            aggregateQosQueues.put(faceId, new TokenBucket(bitrate));
        }

        qosQueues.keySet().forEach(face -> {
            List<String> flows = flowTableManager.getFlowsOfFace(face);
            int nrFlows = flows.size();
            nrFlows = nrFlows > 0 ? nrFlows : 1;

            int bitRateLimit = bitrate / nrFlows;
            flows.forEach(flow -> qosQueues.get(face).put(flow, new TokenBucket(bitRateLimit)));
        });

        lock.unlock();
    }

    public void pushRule(String prefix, int faceId, int cost)
    {
        flowTableManager.pushRule(prefix, faceId, cost);
    }

    private Face getFaceFromSDNController(Interest interest, int inFaceId)
    {
        String prefix = interest.getPrefix();

        //let the controller calculate the route and push the rules to all nodes on the path to the target
        SDNController.calculateRoutesForPrefix(node.getId(), prefix);

        List<Integer> exclude = new ArrayList<>();
        exclude.add(inFaceId);
        return selectFaceFromLocalFib(interest, exclude);
    }

    private Face selectFaceFromLocalFib(Interest interest, List<Integer> exclude) {
        return flowTableManager.getFaceForPrefix(interest.getPrefix(), exclude);
    }

    private void logDroppedInterest(String prefix, int faceId)
    {
        LinkRepairAction action = flowTableManager.interestUnsatisfied(prefix, faceId);

        if (action.isRepair())
        {
            SDNController.linkFailure(node.getId(), faceId, prefix, action.getFailRate());
        }
    }

    boolean hasQueue(int faceId, String prefix)
    {
        if (useAggregateQueuesPerFace) {
            return qosQueueInitialized.get(faceId);
        } else {
            return qosQueues.get(faceId).size() > 0 && qosQueues.get(faceId).get(prefix) != null;
        }
    }

    boolean tryConsumeQueueToken(int faceId, String prefix)
    {
        if (useAggregateQueuesPerFace) {
            //return aggregateQosQueues.get(faceId)
            return false;
        } else {
            Packet p = new Packet();
            p.setSize(Interest.INTEREST_SIZE + Data.DATA_SIZE);
            return qosQueues.get(faceId).get(prefix).consumeToken(new Packet());
        }
    }
}
