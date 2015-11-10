package at.itec.fbacher.flowsim.extensions.app.tg;


import at.itec.fbacher.flowsim.extensions.app.tg.popularity.PopularitySequence;
import at.itec.fbacher.flowsim.sim.Scheduler;

import java.util.Observable;

/**
 * Created by florian on 12/06/15.
 */
public class SimulationSchedule extends Observable implements Runnable {

    public static final double SPEEDUP_FACTOR = 180.0;

    private PopularitySequence.PhaseOfDay phaseOfDay;
    int dayNr;
    int maxDays;

    public SimulationSchedule(int maxDays) {
        this.maxDays = maxDays;
    }

    @Override
    public void run() {
        initialize();
        do {
            nextPhase();
        } while (dayNr <= maxDays);
    }

    private void initialize() {
        dayNr = 0;
        phaseOfDay = PopularitySequence.PhaseOfDay.MORNING;
    }

    private void nextPhase() {
        if (phaseOfDay == PopularitySequence.PhaseOfDay.MORNING) {
            Scheduler.getInstance().scheduleEventInMilliSeconds((long) (1000 * 3 * 3600 / SPEEDUP_FACTOR), () -> {
                phaseOfDay = PopularitySequence.PhaseOfDay.LUNCHTIME;
                nextPhase();
            });

        } else if (phaseOfDay == PopularitySequence.PhaseOfDay.LUNCHTIME) {
            Scheduler.getInstance().scheduleEventInMilliSeconds((long) (1000 * 3600 * 2 / SPEEDUP_FACTOR), () -> {
                phaseOfDay = PopularitySequence.PhaseOfDay.AFTERNOON;
                nextPhase();
            });
        } else if (phaseOfDay == PopularitySequence.PhaseOfDay.AFTERNOON) {
            Scheduler.getInstance().scheduleEventInMilliSeconds((long) (1000 * 3600 * 7 / SPEEDUP_FACTOR), () -> {
                phaseOfDay = PopularitySequence.PhaseOfDay.PRIMETIME;
                nextPhase();
            });
        } else if(phaseOfDay == PopularitySequence.PhaseOfDay.PRIMETIME) {
            Scheduler.getInstance().scheduleEventInMilliSeconds((long) (1000 * 3600 * 3 / SPEEDUP_FACTOR), () -> {
                phaseOfDay = PopularitySequence.PhaseOfDay.NIGHT;
                nextPhase();
            });
        } else if (phaseOfDay == PopularitySequence.PhaseOfDay.NIGHT) {
            Scheduler.getInstance().scheduleEventInMilliSeconds((long) (1000 * 3600 * 9 / SPEEDUP_FACTOR), () -> {
                phaseOfDay = PopularitySequence.PhaseOfDay.MORNING;
                nextPhase();
            });
            dayNr++;
        }
        setChanged();
        notifyObservers();
    }


    public PopularitySequence.PhaseOfDay getPhaseOfDay() {
        return phaseOfDay;
    }
}
