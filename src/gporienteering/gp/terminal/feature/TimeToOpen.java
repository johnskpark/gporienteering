package gporienteering.gp.terminal.feature;

import gporienteering.core.PlaceOfInterest;
import gporienteering.core.Tour;
import gporienteering.core.Visit;
import gporienteering.gp.CalcPriorityProblem;
import gporienteering.gp.terminal.FeatureGPNode;

public class TimeToOpen extends FeatureGPNode {

    public TimeToOpen() {
        super();
        name = "TO";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        PlaceOfInterest candidate = calcPriorityProblem.getCandidate();
        Tour tour = calcPriorityProblem.getState().getTour();
        Visit currVisit = tour.getCurrentVisit();

        double currTime = currVisit.getDepartTime();

        return candidate.getOpenTime() - currTime;
    }
}
