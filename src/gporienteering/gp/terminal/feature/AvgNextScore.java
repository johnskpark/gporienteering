package gporienteering.gp.terminal.feature;

import gporienteering.core.Instance;
import gporienteering.core.PlaceOfInterest;
import gporienteering.core.Tour;
import gporienteering.core.Visit;
import gporienteering.gp.CalcPriorityProblem;
import gporienteering.gp.terminal.FeatureGPNode;

public class AvgNextScore extends FeatureGPNode {

    public AvgNextScore() {
        super();
        name = "ANS";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Instance instance = calcPriorityProblem.getState().getInstance();
        PlaceOfInterest candidate = calcPriorityProblem.getCandidate();
        Tour tour = calcPriorityProblem.getState().getTour();
        Visit currVisit = tour.getCurrentVisit();
        double currTime = currVisit.getDepartTime();

        currTime += candidate.getTimeToFinishVisit();

        double value = 0;
        int n = 0;

        for (PlaceOfInterest poi : calcPriorityProblem.getState().getFeasiblePOIs()) {
            if (poi.equals(candidate))
                continue;

            // check if it is expected to be feasible or not
            double timeToArrive = instance.travelTimeDepartAt(candidate, poi, currTime);
            double visitStartTime = currTime + timeToArrive;

            if (visitStartTime > poi.getCloseTime()) // cannot meet the time window
                continue;

            if (visitStartTime < poi.getOpenTime())
                visitStartTime = poi.getOpenTime();

            double visitFinishTime = visitStartTime + poi.getDuration();

            double timeToReturn = instance.travelTimeDepartAt(poi, instance.getEndPOI(), visitFinishTime);

            if (visitFinishTime + timeToReturn > instance.getEndTime()) // cannot return in time
                continue;

            value += poi.getScore(0);
            n ++;
        }

        return value / n;
    }
}
