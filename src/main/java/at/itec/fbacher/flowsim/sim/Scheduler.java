package at.itec.fbacher.flowsim.sim;

import java.util.*;

/**
 * Created by florian on 12.08.2015.
 */
public class Scheduler implements Observer {

    private static Scheduler instance;

    private TreeMap<Long, List<SimulationEvent>> scheduledEvents;

    private Long nextEventTstamp;


    private Scheduler() {
        scheduledEvents = new TreeMap<>();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Simulator) {
            long currentTime = ((Simulator)o).getCurrentTime();
            if (currentTime == nextEventTstamp) {
                executeScheduledEvents(currentTime);
            }
        }
    }

    private void executeScheduledEvents(Long currentTime) {
        List<SimulationEvent> events = scheduledEvents.firstEntry().getValue();

        events.stream().forEach(SimulationEvent::execute);

        scheduledEvents.remove(currentTime);
        if (scheduledEvents.size() > 0)
            nextEventTstamp = scheduledEvents.firstKey();
    }

    public static Scheduler getInstance() {
        if (instance == null) {
            instance = new Scheduler();
        }
        return instance;
    }

    public SimulationEvent scheduleEventAt(long when, SimulationEvent evt) {
        addSimulationEvent(when, evt);
        return evt;
    }

    private void addSimulationEvent(long when, SimulationEvent evt) {
        if (scheduledEvents.get(when) == null) {
            scheduledEvents.put(when, new ArrayList<>());
        }
        scheduledEvents.get(when).add(evt);
        nextEventTstamp = scheduledEvents.firstKey();
    }

    public SimulationEvent scheduleEventIn(long in, SimulationEvent evt) {
        long when = Simulator.getInstance().getCurrentTime() + in;
        addSimulationEvent(when, evt);
        return evt;
    }

    public void cancelEvent(SimulationEvent evt) {
        Optional<List<SimulationEvent>> simulationEvents = scheduledEvents
                .values()
                .stream()
                .filter(events -> events.contains(evt))
                .findFirst();

        if (simulationEvents.isPresent())
            simulationEvents.get().remove(evt);
    }
}
