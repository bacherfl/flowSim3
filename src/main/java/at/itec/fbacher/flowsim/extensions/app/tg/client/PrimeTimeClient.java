package at.itec.fbacher.flowsim.extensions.app.tg.client;

import at.itec.fbacher.flowsim.extensions.app.tg.popularity.PopularitySequence;

/**
 * Created by florian on 12/06/15.
 */
public class PrimeTimeClient extends ClientState {
    public PrimeTimeClient(DummyClient context) {
        super(context);
        popularities = PopularitySequence.getInstance()
                .getPopularitiesForPhaseOfDay(PopularitySequence.PhaseOfDay.PRIMETIME);
    }

    @Override
    public void nextState() {
        context.setState(new NightTimeClient(context));
        context.getState().requestContent();
    }
}
