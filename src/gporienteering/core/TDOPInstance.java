package gporienteering.core;


import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A time dependent orienteering instance.
 * It contains the elements of a basic orienteering instance, as well as
 *  - A speed matrix,
 *  - An arc category matrix.
 *  - A period time list to specify period range.
 *
 * The data file description can be seen from
 * https://www.mech.kuleuven.be/en/cib/op/td-op-format.txt.
 */

public class TDOPInstance extends Instance {

    public static final int NUM_PERIODS = 4;
    public static final int NUM_CATEGORIES = 5;
    public final static double[] PERIOD_TIMES = new double[]{7d, 9d, 17d, 19d, 24d};

    private double[][] speedMatrix = new double[NUM_CATEGORIES][NUM_PERIODS];
    private Map<Pair<PlaceOfInterest, PlaceOfInterest>, Integer> arcCategoryMap;

    public TDOPInstance(List<PlaceOfInterest> placeOfInterests,
                        PlaceOfInterest startPOI,
                        PlaceOfInterest endPOI,
                        double startTime, double endTime,
                        Map<Pair<PlaceOfInterest, PlaceOfInterest>, Double> distMap,
                        Map<Pair<PlaceOfInterest, PlaceOfInterest>, Double> timeDeviationMap,
                        Map<PlaceOfInterest, Double> durationDeviationMap,
                        double timeULevel, double durationULevel, int numDays,
                        double[][] speedMatrix,
                        Map<Pair<PlaceOfInterest, PlaceOfInterest>, Integer> arcCategoryMap) {
        super(placeOfInterests, startPOI, endPOI, startTime, endTime, distMap, timeDeviationMap, durationDeviationMap, timeULevel, durationULevel, numDays);
        this.speedMatrix = speedMatrix;
        this.arcCategoryMap = arcCategoryMap;
    }

    public TDOPInstance() {
        super();
    }

    public double[][] getSpeedMatrix() {
        return speedMatrix;
    }

    public Map<Pair<PlaceOfInterest, PlaceOfInterest>, Integer> getArcCategoryMap() {
        return arcCategoryMap;
    }

    public double getSpeed(int category, int period) {
        return speedMatrix[category][period];
    }

    public int getCategory(PlaceOfInterest from, PlaceOfInterest to) {
        return arcCategoryMap.get(Pair.of(from, to));
    }

    /**
     * Read the speed matrix from a file.
     * @param file the input file.
     */
    public void readSpeedMatrixFromFile(File file) {
        String line;
        String[] segments;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i < NUM_CATEGORIES; i++) {
                line = reader.readLine();
                segments = line.split("\\s+");

                for (int j = 0; j < NUM_PERIODS; j++)
                    speedMatrix[i][j] = Double.valueOf(segments[j]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read the arc category from a file.
     * @param file the input file.
     */
    public void readArcCategoryFromFile(File file) {
        int numPOIs = placeOfInterests.size();
        arcCategoryMap = new HashMap<>();

        String line;
        String[] segments;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            for (int i = 0; i < numPOIs; i++) {
                line = reader.readLine();
                segments = line.split("\\s+");

                for (int j = 0; j < numPOIs; j++)
                    arcCategoryMap.put(
                            Pair.of(placeOfInterests.get(i), placeOfInterests.get(j)),
                            Integer.valueOf(segments[j]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read the placeOfInterests, startPOI, endPOI, and maxTime from a Verbeeck file.
     * @param file the input file.
     */
    public void readFromVerbeeckFile(File file) {
        String line;
        String[] segments;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            line = reader.readLine(); // n N
            segments = line.split("\\s+");
            int numPOIs = Integer.valueOf(segments[1]);
            reader.readLine(); // m 1
            line = reader.readLine(); // tmax Tmax
            segments = line.split("\\s+");
            startTime = 7; // always start at 7am
            double maxTime = Double.valueOf(segments[1]);
            endTime = startTime + maxTime;

            for (int i = 0; i < numPOIs; i++) {
                line = reader.readLine();
                segments = line.split("\\s+");

                int numScores = segments.length - 2;
                double x = Double.valueOf(segments[0]);
                double y = Double.valueOf(segments[1]);
                double[] scores = new double[numScores];
                for (int j = 0; j < numScores; j++)
                    scores[j] = Double.valueOf(segments[j+2]);

                PlaceOfInterest poi = new PlaceOfInterest(x, y, scores);
                placeOfInterests.add(poi);
            }

            startPOI = placeOfInterests.get(0);
            endPOI = placeOfInterests.get(placeOfInterests.size()-1);

            calcDistMap();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculate the distance map between POIs from Verbeeck.
     * Distances divided by 5.
     */
    @Override
    public void calcDistMap() {
        distMap = new HashMap<>();
        timeDeviationMap = new HashMap<>();

        for (int i = 0; i < placeOfInterests.size()-1; i++) {
            PlaceOfInterest poi1 = placeOfInterests.get(i);
            for (int j = i+1; j < placeOfInterests.size(); j++) {
                PlaceOfInterest poi2 = placeOfInterests.get(j);

                double dist = poi1.distanceBetween(poi2);
                dist /= 5;

                distMap.put(Pair.of(poi1, poi2), dist);
                distMap.put(Pair.of(poi2, poi1), dist);

                timeDeviationMap.put(Pair.of(poi1, poi2), 1d);
                timeDeviationMap.put(Pair.of(poi2, poi1), 1d);
            }
        }
    }

    /**
     * The start time of a period.
     * @param period the period.
     * @return the start time of the period.
     */
    public double periodStartTime(int period) {
        return PERIOD_TIMES[period];
    }

    /**
     * The end time of a period.
     * @param period the period.
     * @return the end time of the period.
     */
    public double periodEndTime(int period) {
        return PERIOD_TIMES[period+1];
    }

    /**
     * Calculate the period that a time belongs to.
     * @param time the given time.
     * @return the period that the time belongs to.
     */
    public int periodOfTime(double time) {
        int period = 0;
        while (time > periodEndTime(period))
            period ++;

        return period;
    }

    /**
     * Calculate the speed for traveling an arc in a certain period.
     * @param from the start node of the arc.
     * @param to the end node of the arc.
     * @param period the period.
     * @return the speed.
     */
    public double periodSpeed(PlaceOfInterest from, PlaceOfInterest to, int period) {
        return getSpeed(getCategory(from, to), period);
    }

    @Override
    public double travelTimeDepartAt(PlaceOfInterest from, PlaceOfInterest to, double departTime) {
        double remainingDist = getDistance(from, to);
        double spentTime = 0;

        double currTime = departTime;
        int currPeriod = periodOfTime(currTime);
        double currPeriodSpeed = periodSpeed(from, to, currPeriod);

        double currPeriodTravelTime = periodEndTime(currPeriod) - currTime;
        double currPeriodTravelDist = currPeriodTravelTime * currPeriodSpeed;

        while (currPeriodTravelDist < remainingDist) {
            // go ahead to the next period
            spentTime += currPeriodTravelTime;
            remainingDist -= currPeriodTravelDist;
            currTime = periodEndTime(currPeriod);

            currPeriod ++;

            if (currPeriod >= NUM_PERIODS) {
                currPeriodSpeed = 0.000001; // set to a very low speed, cause failure.
                break;
            }

            // calculate for the new period
//            System.out.println(from.toString() + " -> " + to.toString() + ", period " + currPeriod);
            currPeriodSpeed = periodSpeed(from, to, currPeriod);
            currPeriodTravelTime = periodEndTime(currPeriod) - currTime;
            currPeriodTravelDist = currPeriodTravelTime * currPeriodSpeed;
        }

        // currPeriodTravelDist >= remainingDist, will arrive in the current period
        spentTime += remainingDist / currPeriodSpeed;

        return spentTime;
    }

    @Override
    public double travelTimeArriveAt(PlaceOfInterest from, PlaceOfInterest to, double arriveTime) {
        double remainingDist = getDistance(from, to);
        double spentTime = 0;

        double currTime = arriveTime;
        int currPeriod = periodOfTime(currTime);
        double currPeriodSpeed = periodSpeed(from, to, currPeriod);

        double currPeriodTravelTime = currTime - periodStartTime(currPeriod);
        double currPeriodTravelDist = currPeriodTravelTime * currPeriodSpeed;

        while (currPeriodTravelDist < remainingDist) {
            // go backward to the previous period
            spentTime += currPeriodTravelTime;
            remainingDist -= currPeriodTravelDist;
            currTime = periodStartTime(currPeriod);

            currPeriod --;

            if (currPeriod < 0) {
                currPeriodSpeed = 0.000001; // set to a very low speed, cause failure.
                break;
            }

            // calculate for the new period
            currPeriodSpeed = periodSpeed(from, to, currPeriod);
            currPeriodTravelTime = currTime - periodStartTime(currPeriod);
            currPeriodTravelDist = currPeriodTravelTime * currPeriodSpeed;
        }

        // currPeriodTravelDist >= remainingDist, will arrive in the current period
        spentTime += remainingDist / currPeriodSpeed;

        return spentTime;
    }

    public Instance clone() {
        Map<Pair<PlaceOfInterest, PlaceOfInterest>, Double> clonedTimeDevMap = new HashMap<>(timeDeviationMap);
        Map<PlaceOfInterest, Double> clonedDurationDevMap = new HashMap<>(durationDeviationMap);

        return new TDOPInstance(placeOfInterests, startPOI, endPOI, startTime, endTime,
                distMap, clonedTimeDevMap, clonedDurationDevMap,
                timeULevel, durationULevel, numDays, speedMatrix, arcCategoryMap);
    }
}
