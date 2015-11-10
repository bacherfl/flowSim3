package at.itec.fbacher.flowsim.extensions.app.tg.popularity;

import at.itec.fbacher.flowsim.extensions.app.tg.content.ContentInfo;

import java.util.List;

/**
 * Created by florian on 12/06/15.
 */
public abstract class PopularityDistribution {

    protected List<ContentInfo> contentItems;

    public PopularityDistribution(List<ContentInfo> contentItems) {
        this.contentItems = contentItems;
    }

    public abstract List<PopularityItem> generatePopularities();
}
