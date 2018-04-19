package gporienteering.core;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An instance of an orienteering problem.
 */

public abstract class Instance {

    protected String name = null; // the name of the instance

    protected List<PlaceOfInterest> placeOfInterests;
    protected PlaceOfInterest startPOI;
    protected PlaceOfInterest endPOI;
    protected double startTime;
    protected double endTime;
    protected int numDays; // the number of days for the trip

    protected Map<Pair<PlaceOfInterest, PlaceOfInterest>, Double> distMap;
    protected Map<Pair<PlaceOfInterest, PlaceOfInterest>, Double> timeDeviationMap;
    protected Map<PlaceOfInterest, Double> durationDeviationMap;


    protected double timeULevel;
    protected double durationULevel;

    protected long seed;
    protected RandomDataGenerator rdg = new RandomDataGenerator();

    public Instance(List<PlaceOfInterest> placeOfInterests,
                    PlaceOfInterest startPOI,
                    PlaceOfInterest endPOI,
                    double startTime, double endTime,
                    Map<Pair<PlaceOfInterest, PlaceOfInterest>, Double> distMap,
                    Map<Pair<PlaceOfInterest, PlaceOfInterest>, Double> timeDeviationMap,
                    Map<PlaceOfInterest, Double> durationDeviationMap,
                    double timeULevel, double durationULevel, int numDays) {
        this.placeOfInterests = placeOfInterests;
        this.startPOI = startPOI;
        this.endPOI = endPOI;
        this.startTime = startTime;
        this.endTime = endTime;
        this.distMap = distMap;
        this.timeDeviationMap = timeDeviationMap;
        this.durationDeviationMap = durationDeviationMap;
        this.timeULevel = timeULevel;
        this.durationULevel = durationULevel;
        this.numDays = numDays;
    }

    public Instance() {
        this(new ArrayList<>(), null, null, 0, 0, new HashMap<>(), new HashMap<>(), new HashMap<>(), 0, 0, 1);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PlaceOfInterest> getPlaceOfInterests() {
        return placeOfInterests;
    }

    public PlaceOfInterest getStartPOI() {
        return startPOI;
    }

    public PlaceOfInterest getEndPOI() {
        return endPOI;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public Map<Pair<PlaceOfInterest, PlaceOfInterest>, Double> getDistMap() {
        return distMap;
    }

    public double getDistance(PlaceOfInterest from, PlaceOfInterest to) {
        return distMap.get(Pair.of(from, to));
    }

    public Map<Pair<PlaceOfInterest, PlaceOfInterest>, Double> getTimeDeviationMap() {
        return timeDeviationMap;
    }

    public double getTimeDeviation(PlaceOfInterest from, PlaceOfInterest to) {
        return timeDeviationMap.get(Pair.of(from, to));
    }

    public Map<PlaceOfInterest, Double> getDurationDeviationMap() {
        return durationDeviationMap;
    }

    public double getDurationDeviation(PlaceOfInterest poi) {
        return durationDeviationMap.get(poi);
    }

    public double getTimeULevel() {
        return timeULevel;
    }

    public void setTimeULevel(double timeULevel) {
        this.timeULevel = timeULevel;
    }

    public double getDurationULevel() {
        return durationULevel;
    }

    public void setDurationULevel(double durationULevel) {
        this.durationULevel = durationULevel;
    }

    public int getNumDays() {
        return numDays;
    }

    public void setNumDays(int numDays) {
        this.numDays = numDays;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
        rdg.reSeed(seed);
        sample();
    }

    /**
     * Calculate the distance map between POIs.
     */
    public void calcDistMap() {
        for (int i = 0; i < placeOfInterests.size()-1; i++) {
            PlaceOfInterest poi1 = placeOfInterests.get(i);
            for (int j = i+1; j < placeOfInterests.size(); j++) {
                PlaceOfInterest poi2 = placeOfInterests.get(j);

                double dist = poi1.distanceBetween(poi2);

                distMap.put(Pair.of(poi1, poi2), dist);
                distMap.put(Pair.of(poi2, poi1), dist);

                timeDeviationMap.put(Pair.of(poi1, poi2), 1d);
                timeDeviationMap.put(Pair.of(poi2, poi1), 1d);
            }
        }

        for (PlaceOfInterest poi : placeOfInterests) {
            durationDeviationMap.put(poi, 1d);
        }
    }

    public Tour initTour() {
        Tour tour = new Tour(startPOI.getScores().length);
        tour.addVisit(new Visit(startPOI, startTime, startTime));

        return tour;
    }

    public void sample() {
        if (timeULevel != 0) {
            for (Pair<PlaceOfInterest, PlaceOfInterest> key : timeDeviationMap.keySet()) {
                double deviation = rdg.nextGaussian(1, timeULevel);
                timeDeviationMap.put(key, deviation);
            }
        }

        if (durationULevel != 0) {
            for (PlaceOfInterest key : durationDeviationMap.keySet()) {
                double deviation = rdg.nextGaussian(1, durationULevel);
                durationDeviationMap.put(key, deviation);
            }
        }
    }

    /**
     * Calculate the travel time to travel an arc departing at a certain time.
     * @param from the node to start from.
     * @param to the node to end with.
     * @param departTime the departure time.
     * @return the travel time.
     */
    public abstract double travelTimeDepartAt(PlaceOfInterest from, PlaceOfInterest to, double departTime);

    /**
     * Calculate the travel time to travel an arc arriving at a certain time
     * @param from the node to start from.
     * @param to the node to end with.
     * @param arriveTime the arrival time.
     * @return the travel time.
     */
    public abstract double travelTimeArriveAt(PlaceOfInterest from, PlaceOfInterest to, double arriveTime);

}
