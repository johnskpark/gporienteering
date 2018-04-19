package gporienteering.core;

/**
 * A visit of a place of interest.
 */

public class Visit {
    private PlaceOfInterest placeOfInterest;
    private double arriveTime;
    private double departTime;

    public Visit(PlaceOfInterest placeOfInterest, double arriveTime, double departTime) {
        this.placeOfInterest = placeOfInterest;
        this.arriveTime = arriveTime;
        this.departTime = departTime;
    }

    public PlaceOfInterest getPlaceOfInterest() {
        return placeOfInterest;
    }

    public double getArriveTime() {
        return arriveTime;
    }

    public double getDepartTime() {
        return departTime;
    }

    public boolean isVisited() {
        return departTime > arriveTime;
    }

    public double[] scores() {
        if (isVisited()) {
            return placeOfInterest.getScores();
        }
        else {
            return new double[placeOfInterest.getScores().length];
        }
    }

    @Override
    public String toString() {
        return "[" + placeOfInterest.toString() + ", " + arriveTime + ", " + departTime + "]";
    }
}
