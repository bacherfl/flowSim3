package at.itec.fbacher.flowsim.extensions.app.tg.client;

import at.itec.fbacher.flowsim.extensions.app.tg.popularity.PopularityItem;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by florian on 12/06/15.
 */
public abstract class ClientState {

    protected DummyClient context;
    protected List<PopularityItem> popularities;
    protected boolean inactive = false;

    public ClientState(DummyClient context) {
        this.context = context;
    }

    public abstract void nextState();

    protected void requestContent() {
        String content = selectContent();
        context.sendRequest(
                context.getContentRepository().stream()
                        .filter(ci -> ci.getContentName().equalsIgnoreCase(content))
                        .findFirst().get()
        );
    }

    protected String selectContent() {
        double random = Math.random();
        String contentName = "";
        double tmp = 0.0;
        for (int i = 0; i < popularities.size(); i++) {
            PopularityItem popularityItem = popularities.get(i);
            tmp += popularityItem.getPopularity();
            if (random <= tmp + popularityItem.getPopularity()) {
                contentName = popularityItem.getContentName();
                break;
            }
        }

        return contentName;
    }

    public void requestFinished() {
        if (context.getAvailableRequests() > 0) {
            requestContent();
        } else {
            inactive = true;
        }
    }

    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }
}
