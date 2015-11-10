package at.itec.fbacher.flowsim.extensions.app.tg.client;


import at.itec.fbacher.flowsim.extensions.app.tg.popularity.PopularitySequence;

/**
 * Created by florian on 12/06/15.
 */
public class NightTimeClient extends ClientState {
    public NightTimeClient(DummyClient context) {
        super(context);
        popularities = PopularitySequence.getInstance()
                .getPopularitiesForPhaseOfDay(PopularitySequence.PhaseOfDay.NIGHT);
    }

    @Override
    public void nextState() {
        context.setState(new MorningClient(context));
        context.getState().requestContent();
    }
}
