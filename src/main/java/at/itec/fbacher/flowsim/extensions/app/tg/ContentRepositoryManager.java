package at.itec.fbacher.flowsim.extensions.app.tg;

import at.itec.fbacher.flowsim.events.ContentAssignedEvent;
import at.itec.fbacher.flowsim.events.EventPublisher;
import at.itec.fbacher.flowsim.extensions.app.Producer;
import at.itec.fbacher.flowsim.extensions.app.tg.content.ContentInfo;
import at.itec.fbacher.flowsim.model.topology.NodeContainer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by florian on 10.11.2015.
 */
public class ContentRepositoryManager {

    List<Producer> producers = new ArrayList<>();
    private Set<ContentInfo> contentInfos = new HashSet<>();

    public ContentRepositoryManager() {
        initialize();
    }

    private void initialize() {
        try {
            Files.list(Paths.get("statistics")).filter(file -> file.getFileName().toString().endsWith("top30.csv"))
                    .forEach(file -> {
                        try {
                            BufferedReader reader = Files.newBufferedReader(file.toAbsolutePath());

                            String line;
                            while ((line = reader.readLine()) != null) {
                                String contentName = line.split(";")[0];
                                contentInfos.add(new ContentInfo(contentName, 10));
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void assignContentItems() {
        final int[] i = {0};
        contentInfos.forEach(contentInfo -> {
            Producer producer = producers.get(i[0]);
            producer.getPrefixes().add(contentInfo.getContentName());
            EventPublisher.getInstance().publishEvent(new ContentAssignedEvent(producer, contentInfo));
            i[0] = (i[0] + 1) % producers.size();
        });
    }

    public List<Producer> getProducers() {
        return producers;
    }

    public void setProducers(List<Producer> producers) {
        this.producers = producers;
    }
}
