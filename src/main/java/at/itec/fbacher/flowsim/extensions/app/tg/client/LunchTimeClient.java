package at.itec.fbacher.flowsim.extensions.app.tg.client;


import at.itec.fbacher.flowsim.extensions.app.tg.popularity.PopularitySequence;

/**
 * Created by florian on 12/06/15.
 */
public class LunchTimeClient extends ClientState {
    public LunchTimeClient(DummyClient context) {
        super(context);
        popularities = PopularitySequence.getInstance()
                .getPopularitiesForPhaseOfDay(PopularitySequence.PhaseOfDay.LUNCHTIME);
    }

    @Override
    public void nextState() {
        context.setState(new AfternoonClient(context));
        context.getState().requestContent();
    }
}
