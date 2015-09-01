package at.itec.fbacher.flowsim.model.fw;

import at.itec.fbacher.flowsim.model.Face;

/**
 * Created by florian on 25.08.2015.
 */
public class FibEntry {

    Face face;
    double cost = 0.0;

    public FibEntry(Face face, double cost) {
        this.face = face;
        this.cost = cost;
    }

    public Face getFace() {
        return face;
    }

    public void setFace(Face face) {
        this.face = face;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
