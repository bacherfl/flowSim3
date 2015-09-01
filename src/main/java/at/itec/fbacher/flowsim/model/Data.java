package at.itec.fbacher.flowsim.model;

/**
 * Created by florian on 12.08.2015.
 */
public class Data extends Packet {
    public static final int DATA_SIZE = 4096;

    @Override
    public int getSize() {
        return DATA_SIZE;
    }
}
