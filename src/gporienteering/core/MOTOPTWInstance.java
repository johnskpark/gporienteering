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
 * The multi-objective team orienteering problem with time windows.
 */

// TODO need to fix this to be multi-objective as well.
public class MOTOPTWInstance extends Instance {

    private int numObjs;

    public MOTOPTWInstance(List<PlaceOfInterest> placeOfInterests,
                           PlaceOfInterest startPOI,
                           PlaceOfInterest endPOI,
                           double startTime, double endTime,
                           Map<Pair<PlaceOfInterest, PlaceOfInterest>, Double> distMap,
                           Map<Pair<PlaceOfInterest, PlaceOfInterest>, Double> timeDeviationMap,
                           Map<PlaceOfInterest, Double> durationDeviationMap,
                           double timeULevel, double durationULevel, int numDays) {
        super(placeOfInterests, startPOI, endPOI, startTime, endTime, distMap, timeDeviationMap, durationDeviationMap, timeULevel, durationULevel, numDays);
    }

    public MOTOPTWInstance() {
        super();
    }

    /**
     * Read the instance from a file.
     * The file description can be found from
     * https://www.mech.kuleuven.be/en/cib/op/instances/TOPTWformat/view.
     * @param file the file.
     */
    public void readFromFile(File file) {
        String line;
        String[] segments;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            line = reader.readLine(); // k v N t m
            segments = line.split("\\s+");
            int numPOIs = Integer.valueOf(segments[2]);
            numObjs = Integer.valueOf(segments[4]);
            reader.readLine(); // D Q, not relevant

            // read the POIs
            for (int i = 0; i < numPOIs; i++) {
                line = reader.readLine();
                segments = line.split("\\s+");

                double x = Double.valueOf(segments[2]);
                double y = Double.valueOf(segments[3]);
                double duration = Double.valueOf(segments[4]);
                double[] scores = new double[numObjs];
                for (int j = 5; j < 5 + numObjs; j++) {
                    scores[j-5] = Double.valueOf(segments[j]);
                }
                double openTime = Double.valueOf(segments[segments.length-2]);
                double closeTime = Double.valueOf(segments[segments.length-1]);

                PlaceOfInterest poi = new PlaceOfInterest(x, y, scores,
                        duration, openTime, closeTime);
                placeOfInterests.add(poi);
            }

            startPOI = placeOfInterests.get(0);
            endPOI = placeOfInterests.get(0);
            startTime = startPOI.getOpenTime();
            endTime = endPOI.getCloseTime();

            calcDistMap();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public double travelTimeDepartAt(PlaceOfInterest from, PlaceOfInterest to, double departTime) {
        return getDistance(from, to);
    }

    @Override
    public double travelTimeArriveAt(PlaceOfInterest from, PlaceOfInterest to, double arriveTime) {
        return getDistance(from, to);
    }

    public Instance clone() {
        Map<Pair<PlaceOfInterest, PlaceOfInterest>, Double> clonedTimeDevMap = new HashMap<>(timeDeviationMap);
        Map<PlaceOfInterest, Double> clonedDurationDevMap = new HashMap<>(durationDeviationMap);

        return new MOTOPTWInstance(placeOfInterests, startPOI, endPOI, startTime, endTime,
                distMap, clonedTimeDevMap, clonedDurationDevMap,
                timeULevel, durationULevel, numDays);
    }
}
