package at.itec.fbacher.flowsim.extensions.app.tg.content;

/**
 * Created by florian on 12/06/15.
 */
public class ContentInfo {

    private String contentName;

    private double sizeInMB;

    public ContentInfo(String contentName, double sizeInMB) {
        this.contentName = contentName;
        this.sizeInMB = sizeInMB;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public double getSizeInMB() {
        return sizeInMB;
    }

    public double getSizeInBytes() {
        return sizeInMB * 1024 * 1024;
    }

    public void setSizeInMB(double sizeInMB) {
        this.sizeInMB = sizeInMB;
    }
}
