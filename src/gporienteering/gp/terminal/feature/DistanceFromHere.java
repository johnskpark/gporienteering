package gporienteering.gp.terminal.feature;

import gporienteering.core.Instance;
import gporienteering.core.PlaceOfInterest;
import gporienteering.core.Tour;
import gporienteering.gp.CalcPriorityProblem;
import gporienteering.gp.terminal.FeatureGPNode;

/**
 * Feature: the distance from here (the current node).
 */
public class DistanceFromHere extends FeatureGPNode {

    public DistanceFromHere() {
        super();
        name = "DFH";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Instance instance = calcPriorityProblem.getState().getInstance();
        PlaceOfInterest candidate = calcPriorityProblem.getCandidate();
        Tour tour = calcPriorityProblem.getState().getTour();
        return instance.getDistance(tour.getCurrentVisit().getPlaceOfInterest(), candidate);
    }
}
