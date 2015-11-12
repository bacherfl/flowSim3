package at.itec.fbacher.flowsim.sim;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by florian on 12.08.2015.
 */
public class Scheduler implements Observer {

    private static Scheduler instance;

    private TreeMap<Long, List<SimulationEvent>> scheduledEvents;

    private Long nextEventTstamp;

    private ReentrantLock lock = new ReentrantLock();


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
        lock.lock();
        List<SimulationEvent> events = scheduledEvents.firstEntry().getValue();

        if (events != null) {
            for (int i = 0; i < events.size(); i++) {
                events.get(i).execute();
            }
            /*
            events.stream().forEach(event -> {
                try {
                    if (event != null)
                        event.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            */
        }

        scheduledEvents.remove(currentTime);
        if (scheduledEvents.size() > 0)
            nextEventTstamp = scheduledEvents.firstKey();
        lock.unlock();
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
        lock.lock();
        if (scheduledEvents.get(when) == null) {
            scheduledEvents.put(when, new ArrayList<>());
        }
        scheduledEvents.get(when).add(evt);
        nextEventTstamp = scheduledEvents.firstKey();
        lock.unlock();
    }

    public SimulationEvent scheduleEventIn(long in, SimulationEvent evt) {
        long when = Simulator.getInstance().getCurrentTime() + in;
        addSimulationEvent(when, evt);
        return evt;
    }

    public SimulationEvent scheduleEventInSeconds(long in, SimulationEvent evt) {
        long when = Simulator.getInstance().getCurrentTime() + in * Simulator.SIMULATION_TICKS_PER_SECOND;
        addSimulationEvent(when, evt);
        return evt;
    }

    public SimulationEvent scheduleEventInMilliSeconds(long in, SimulationEvent evt) {
        long when = Simulator.getInstance().getCurrentTime() + in * (Simulator.SIMULATION_TICKS_PER_SECOND / 1000);
        addSimulationEvent(when, evt);
        return evt;
    }

    public void cancelEvent(SimulationEvent evt) {
        lock.lock();
        Optional<List<SimulationEvent>> simulationEvents = scheduledEvents
                .values()
                .stream()
                .filter(events -> events.contains(evt))
                .findFirst();

        if (simulationEvents.isPresent())
            simulationEvents.get().remove(evt);
        lock.unlock();
    }
}
