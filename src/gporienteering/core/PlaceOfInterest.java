package gporienteering.core;

/**
 * A place of interest has x and y coordinates, and a score.
 */

public class PlaceOfInterest implements Comparable<PlaceOfInterest> {

    private double x;
    private double y;
    private double[] scores;

    private double duration; // the duration of the visit [optional]
    private double openTime; // open time of the time window [optional]
    private double closeTime; // close time of the time window [optional]

    // the features used for decision making
    private double timeToArrive; // time to arrive the place
    private double timeToStartVisit; // time to start visiting the place
    private double timeToFinishVisit; // time to finish visiting the place
    private double timeToReturn; // time to return to the end place
    private double priority; // the priority for decision making process

    public PlaceOfInterest(double x, double y, double[] scores,
                           double duration, double openTime, double closeTime) {
        this.x = x;
        this.y = y;
        this.scores = scores;
        this.duration = duration;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public PlaceOfInterest(double x, double y, double[] scores) {
        this(x, y, scores, 0, -999999, 999999);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double[] getScores() {
        return scores;
    }

    public double getScore(int index) {
        return scores[index];
    }

    public double getDuration() {
        return duration;
    }

    public double getOpenTime() {
        return openTime;
    }

    public double getCloseTime() {
        return closeTime;
    }

    public double getTimeToArrive() {
        return timeToArrive;
    }

    public void setTimeToArrive(double timeToArrive) {
        this.timeToArrive = timeToArrive;
    }

    public double getTimeToStartVisit() {
        return timeToStartVisit;
    }

    public void setTimeToStartVisit(double timeToStartVisit) {
        this.timeToStartVisit = timeToStartVisit;
    }

    public double getTimeToFinishVisit() {
        return timeToFinishVisit;
    }

    public void setTimeToFinishVisit(double timeToFinishVisit) {
        this.timeToFinishVisit = timeToFinishVisit;
    }

    public double getTimeToReturn() {
        return timeToReturn;
    }

    public void setTimeToReturn(double timeToReturn) {
        this.timeToReturn = timeToReturn;
    }

    public double getPriority() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    /**
     * The distance between another POI (symmatric graph is assumed).
     * @param other another POI.
     * @return the distance.
     */
    public double distanceBetween(PlaceOfInterest other) {
        double xdiff = x - other.x;
        double ydiff = y - other.y;

        return Math.sqrt(xdiff * xdiff + ydiff * ydiff);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public int compareTo(PlaceOfInterest o) {
        if (x < o.x)
            return -1;
        if (x > o.x)
            return 1;
        if (y < o.y)
            return -1;
        if (y > o.y)
            return 1;
        return 0;
    }
}
