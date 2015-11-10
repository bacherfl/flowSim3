package at.itec.fbacher.flowsim.extensions.app.tg.content;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by florian on 12/06/15.
 */
public class DummyContentGenerator {

    public List<ContentInfo> generateDummyContent(int nrContentItems) {
        List<ContentInfo> contentInfos = new ArrayList<>();
        
        for (int i = 0; i < nrContentItems; i++)
        {
            ContentInfo info = new ContentInfo("/content" + i, Math.random() * 1000);
            contentInfos.add(info);
        }
        return contentInfos;
    }
}
