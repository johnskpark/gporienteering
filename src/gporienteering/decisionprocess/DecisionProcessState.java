package gporienteering.decisionprocess;

import gporienteering.core.Instance;
import gporienteering.core.PlaceOfInterest;
import gporienteering.core.Tour;
import gporienteering.core.Visit;

import java.util.*;

/**
 * The state for the decision making process of a reactive solution builder.
 * It includes
 *  - the given instance, including relevant information,
 *  - a list of unvisited POIs,
 *  - a (multi-day) partial tour,
 *  - the current day (0 if a single-day tour).
 */
public class DecisionProcessState {

    private Instance instance;
    private List<PlaceOfInterest> unvisitedPOIs;
    private List<PlaceOfInterest> feasiblePOIs;
    private Tour tour;
    private int currDay;

    public DecisionProcessState(Instance instance, List<PlaceOfInterest> unvisitedPOIs, List<PlaceOfInterest> feasiblePOIs, Tour tour, int currDay) {
        this.instance = instance;
        this.unvisitedPOIs = unvisitedPOIs;
        this.feasiblePOIs = feasiblePOIs;
        this.tour = tour;
        this.currDay = currDay;
    }

    public DecisionProcessState(Instance instance) {
        this.instance = instance;
        unvisitedPOIs = new LinkedList<>(instance.getPlaceOfInterests());
        unvisitedPOIs.remove(instance.getStartPOI());
        unvisitedPOIs.remove(instance.getEndPOI());

        // initially, all the unvisited POIs should be feasible.
        feasiblePOIs = new LinkedList<>(unvisitedPOIs);
        feasiblePOIs.remove(instance.getStartPOI());
        feasiblePOIs.remove(instance.getEndPOI());

        tour = new Tour(instance);
        currDay = 0;

        // update features for the feasible POIs
        for (PlaceOfInterest poi : feasiblePOIs)
            updateFeatures(poi);
    }

    public Instance getInstance() {
        return instance;
    }

    public List<PlaceOfInterest> getUnvisitedPOIs() {
        return unvisitedPOIs;
    }

    public List<PlaceOfInterest> getFeasiblePOIs() {
        return feasiblePOIs;
    }

    public Tour getTour() {
        return tour;
    }

    public int getCurrDay() {
        return currDay;
    }

    public void setCurrDay(int currDay) {
        this.currDay = currDay;
    }

    /**
     * Remove a place of insterest from the unvisited list.
     * @param placeOfInterest the removed unvisited POI.
     */
    public void removeUnvisitedPOI(PlaceOfInterest placeOfInterest) {
        unvisitedPOIs.remove(placeOfInterest);
    }

    /**
     * Remove a place of interest from the feasible list.
     * @param placeOfInterest
     */
    public void removeFeasiblePOI(PlaceOfInterest placeOfInterest) {
        feasiblePOIs.remove(placeOfInterest);
    }
    /**
     * Reset a decision process state.
     */
    public void reset() {
        unvisitedPOIs = new LinkedList<>(instance.getPlaceOfInterests());
        unvisitedPOIs.remove(instance.getStartPOI());
        unvisitedPOIs.remove(instance.getEndPOI());

        // initially, all the unvisited POIs should be feasible.
        feasiblePOIs = new LinkedList<>(unvisitedPOIs);
        feasiblePOIs.remove(instance.getStartPOI());
        feasiblePOIs.remove(instance.getEndPOI());

        tour.reset(instance);
        currDay = 0;

        // update features for the feasible POIs
        for (PlaceOfInterest poi : feasiblePOIs)
            updateFeatures(poi);
    }

    /**
     * Go to the next day.
     */
    public void goToNextDay() {
        currDay ++;

        // at the beginning of a new day, all the unvisited POIs should be feasible
        feasiblePOIs = new LinkedList<>(unvisitedPOIs);

        // update features for the feasible POIs
        for (PlaceOfInterest poi : feasiblePOIs)
            updateFeatures(poi);
    }

    /**
     * Update the features of a place of interest.
     * @param poi the place of interest.
     */
    public void updateFeatures(PlaceOfInterest poi) {
        PlaceOfInterest currPOI = tour.getCurrentVisit().getPlaceOfInterest();
        double currTime = tour.getCurrentVisit().getDepartTime();

        double timeToArrive = instance.travelTimeDepartAt(currPOI, poi, currTime);
        poi.setTimeToArrive(timeToArrive);

        double visitStartTime = currTime + timeToArrive;
        if (visitStartTime < poi.getOpenTime())
            visitStartTime = poi.getOpenTime();

        poi.setTimeToStartVisit(visitStartTime - currTime);

        double visitFinishTime = visitStartTime + poi.getDuration();
        poi.setTimeToFinishVisit(visitFinishTime - currTime);

        double timeToReturn = instance.travelTimeDepartAt(poi, instance.getEndPOI(), visitFinishTime);
        poi.setTimeToReturn(timeToReturn);
    }

    /**
     * Update the feasible POIs when the tour is changed.
     */
    public void updateFeasiblePOIs() {
        PlaceOfInterest currPOI = tour.getCurrentVisit().getPlaceOfInterest();
        double currTime = tour.getCurrentVisit().getDepartTime();

        for (int i = feasiblePOIs.size()-1; i >= 0; i--) {
            PlaceOfInterest poi = feasiblePOIs.get(i);

            double timeToArrive = instance.travelTimeDepartAt(currPOI, poi, currTime);
            poi.setTimeToArrive(timeToArrive);

            double visitStartTime = currTime + timeToArrive;

            if (visitStartTime > poi.getCloseTime()) {
                feasiblePOIs.remove(i); // cannot meet the time window, infeasible
                continue;
            }

            if (visitStartTime < poi.getOpenTime())
                visitStartTime = poi.getOpenTime();

            poi.setTimeToStartVisit(visitStartTime - currTime);

            double visitFinishTime = visitStartTime + poi.getDuration();
            poi.setTimeToFinishVisit(visitFinishTime - currTime);

            double timeToReturn = instance.travelTimeDepartAt(poi, instance.getEndPOI(), visitFinishTime);
            poi.setTimeToReturn(timeToReturn);

            if (visitFinishTime + timeToReturn > instance.getEndTime()) {
                feasiblePOIs.remove(i); // cannot return in time, infeasible
                continue;
            }
        }
    }

    public DecisionProcessState clone() {
        List<PlaceOfInterest> clonedUnvisitedPOIs = new LinkedList<>(unvisitedPOIs);
        List<PlaceOfInterest> clonedFeasiblePOIs = new LinkedList<>(feasiblePOIs);
        Tour clonedTour = tour.clone();

        return new DecisionProcessState(instance, clonedUnvisitedPOIs, clonedFeasiblePOIs, clonedTour, currDay);
    }
}
