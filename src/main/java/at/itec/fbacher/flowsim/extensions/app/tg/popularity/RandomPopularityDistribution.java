package at.itec.fbacher.flowsim.extensions.app.tg.popularity;

import at.itec.fbacher.flowsim.extensions.app.tg.content.ContentInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by florian on 12/06/15.
 */
public class RandomPopularityDistribution extends PopularityDistribution {

    public RandomPopularityDistribution(List<ContentInfo> nrContentItems) {
        super(nrContentItems);
    }

    @Override
    public List<PopularityItem> generatePopularities() {
        List<PopularityItem> popularities = new ArrayList<>();

        double remains = 1.0;

        do {
            double rnd = Math.random() * remains;
            PopularityItem popularity = new PopularityItem("content" + popularities.size(), rnd);
            popularities.add(popularity);
            remains -= rnd;
        } while (popularities.size() < contentItems.size());
        return null;
    }
}
