package at.itec.fbacher.flowsim.extensions.app.tg.popularity;

import at.itec.fbacher.flowsim.extensions.app.tg.content.ContentInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by florian on 12/06/15.
 */
public class ZipfPopularityDistribution extends PopularityDistribution {

    private double alpha;

    public ZipfPopularityDistribution(List<ContentInfo> nrContentItems, double alpha) {
        super(nrContentItems);
        this.alpha = alpha;
    }

    @Override
    public List<PopularityItem> generatePopularities() {

        List<PopularityItem> popularities = new ArrayList<>();

        double div = IntStream.range(1, contentItems.size())
                .mapToDouble(n -> 1 / Math.pow(n, alpha))
                .sum();

        for (int i = 1; i < contentItems.size() + 1; i++) {
            double popularity = (1 / (Math.pow(i, alpha))) / div;
            PopularityItem f = new PopularityItem("content" + i, popularity);
            popularities.add(f);
        }
        return popularities;
    }
}
