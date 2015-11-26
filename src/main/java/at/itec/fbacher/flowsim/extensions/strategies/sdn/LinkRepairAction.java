package at.itec.fbacher.flowsim.extensions.strategies.sdn;

/**
 * Created by florian on 26.11.2015.
 */
public class LinkRepairAction {
    private boolean repair;
    private double failRate;

    public boolean isRepair() {
        return repair;
    }

    public void setRepair(boolean repair) {
        this.repair = repair;
    }

    public double getFailRate() {
        return failRate;
    }

    public void setFailRate(double failRate) {
        this.failRate = failRate;
    }
}
