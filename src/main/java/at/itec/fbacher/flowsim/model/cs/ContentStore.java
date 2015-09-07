package at.itec.fbacher.flowsim.model.cs;

import at.itec.fbacher.flowsim.model.Data;
import at.itec.fbacher.flowsim.model.Interest;
import at.itec.fbacher.flowsim.sim.Simulator;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by florian on 13.08.2015.
 */
public class ContentStore {

    private int maxSize = 10; //100 items per CS
    private Map<String, ContentStoreEntry> contentStore = new TreeMap<>();

    public Data getItem(Interest interest) {
        ContentStoreEntry entry = contentStore.get(interest.getName());
        if (entry != null) {
            entry.setLastAccess(Simulator.getInstance().getCurrentTime());
            return entry.getData();
        }
        return null;
    }

    public boolean onData(Data data) {
        boolean inserted;
        if (contentStore.keySet().size() < maxSize) {
            contentStore.put(data.getName(), new ContentStoreEntry(data));
            inserted = true;
        } else {
            String keyToReplace = contentStore.keySet().stream().min((o1, o2) -> {
                long lastAccess1 = contentStore.get(o1).getLastAccess();
                long lastAccess2 = contentStore.get(o2).getLastAccess();
                if (lastAccess1 < lastAccess2)
                    return -1;
                else if (lastAccess1 == lastAccess2)
                    return 0;
                else return 1;
            }).get();

            contentStore.remove(keyToReplace);
            contentStore.put(data.getName(), new ContentStoreEntry(data));
            inserted = true;
        }
        return inserted;
    }
}
