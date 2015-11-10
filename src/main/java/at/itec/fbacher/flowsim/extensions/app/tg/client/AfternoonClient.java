package at.itec.fbacher.flowsim.extensions.app.tg.client;


import at.itec.fbacher.flowsim.extensions.app.tg.popularity.PopularitySequence;

/**
 * Created by florian on 12/06/15.
 */
public class AfternoonClient extends ClientState {
    public AfternoonClient(DummyClient context) {
        super(context);
        popularities = PopularitySequence.getInstance()
                .getPopularitiesForPhaseOfDay(PopularitySequence.PhaseOfDay.AFTERNOON);
    }

    @Override
    public void nextState() {
        context.setState(new PrimeTimeClient(context));
        context.getState().requestContent();
    }
}
