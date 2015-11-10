package at.itec.fbacher.flowsim.extensions.app.tg.popularity;

/**
 * Created by florian on 12/06/15.
 */
public class PopularityItem {

    private String contentName;
    private double popularity;

    public PopularityItem(String contentName, double popularity) {
        this.contentName = contentName;
        this.popularity = popularity;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }
}
