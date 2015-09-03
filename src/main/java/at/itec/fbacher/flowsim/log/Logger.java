package at.itec.fbacher.flowsim.log;


import at.itec.fbacher.flowsim.events.EventPublisher;
import at.itec.fbacher.flowsim.events.LogUpdateEvent;
import at.itec.fbacher.flowsim.sim.FormattedTime;
import at.itec.fbacher.flowsim.sim.Simulator;

/**
 * Created by florian on 31.08.2015.
 */
public class Logger {

    public enum LogLevel { VERBOSE, DEBUG, WARNING, ERROR }

    private static Logger instance;

    private Logger() {}

    public static Logger getInstance() {
        if (instance == null)
            instance = new Logger();
        return instance;
    }

    public void log(String message) {
        log(message, LogLevel.DEBUG);
    }

    public void log(String message, LogLevel level) {
        StringBuilder logMessageBuilder = new StringBuilder();
        FormattedTime formattedTime = new FormattedTime().invoke();
        long seconds = formattedTime.getSeconds();
        double milliSeconds = formattedTime.getMilliSeconds();
        logMessageBuilder.append("[" + seconds + ":" + milliSeconds + "] - ").append(message);
        LogUpdateEvent logUpdateEvent = new LogUpdateEvent(logMessageBuilder.toString());
        //EventPublisher.getInstance().publishEvent(logUpdateEvent);
    }
}
