package gporienteering.gp.terminal.feature;

import gporienteering.core.Instance;
import gporienteering.core.PlaceOfInterest;
import gporienteering.core.Tour;
import gporienteering.core.Visit;
import gporienteering.gp.CalcPriorityProblem;
import gporienteering.gp.terminal.FeatureGPNode;

public class Slack extends FeatureGPNode {

    public Slack() {
        super();
        name = "SL";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        PlaceOfInterest candidate = calcPriorityProblem.getCandidate();
        Tour tour = calcPriorityProblem.getState().getTour();
        double currTime = tour.getCurrentVisit().getDepartTime();

        return candidate.getCloseTime() - currTime - candidate.getTimeToArrive();
    }
}
