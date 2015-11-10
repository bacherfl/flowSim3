package at.itec.fbacher.flowsim.extensions.app.tg.popularity;


import at.itec.fbacher.flowsim.extensions.app.tg.content.ContentInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by florian on 12/06/15.
 */
public class PopularitySequence {

    private static PopularitySequence instance;

    public enum PhaseOfDay {MORNING, LUNCHTIME, AFTERNOON, PRIMETIME, NIGHT};

    private Map<PhaseOfDay, List<PopularityItem>> popularitiesDuringPhaseOfDay;

    private PopularitySequence() {
        popularitiesDuringPhaseOfDay = new HashMap<>();
    }

    public static PopularitySequence getInstance() {
        if (instance == null) {
            instance = new PopularitySequence();
        }
        return instance;
    }

    public void initialize(List<ContentInfo> contentItems) {
        ZipfPopularityDistribution zd1 = new ZipfPopularityDistribution(contentItems, 0.3);
        popularitiesDuringPhaseOfDay.put(PhaseOfDay.MORNING, zd1.generatePopularities());

        Collections.shuffle(contentItems);
        ZipfPopularityDistribution zd2 = new ZipfPopularityDistribution(contentItems, 0.6);
        popularitiesDuringPhaseOfDay.put(PhaseOfDay.LUNCHTIME, zd2.generatePopularities());

        Collections.shuffle(contentItems);
        UniformPopularityDistribution ud1 = new UniformPopularityDistribution(contentItems);
        popularitiesDuringPhaseOfDay.put(PhaseOfDay.AFTERNOON, ud1.generatePopularities());

        Collections.shuffle(contentItems);
        ZipfPopularityDistribution zd3 = new ZipfPopularityDistribution(contentItems, 1.0);
        popularitiesDuringPhaseOfDay.put(PhaseOfDay.PRIMETIME, zd3.generatePopularities());

        RandomPopularityDistribution rd1 = new RandomPopularityDistribution(contentItems);
        popularitiesDuringPhaseOfDay.put(PhaseOfDay.NIGHT, rd1.generatePopularities());
    }

    public List<PopularityItem> getPopularitiesForPhaseOfDay(PhaseOfDay phaseOfDay) {
        return popularitiesDuringPhaseOfDay.get(phaseOfDay);
    }
}
