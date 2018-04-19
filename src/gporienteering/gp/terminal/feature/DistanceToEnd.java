package gporienteering.gp.terminal.feature;

import gporienteering.core.Instance;
import gporienteering.core.PlaceOfInterest;
import gporienteering.gp.CalcPriorityProblem;
import gporienteering.gp.terminal.FeatureGPNode;

/**
 * Feature: the distance to the end POI.
 *
 * Created by gphhucarp on 31/08/17.
 */
public class DistanceToEnd extends FeatureGPNode {

    public DistanceToEnd() {
        super();
        name = "DTE";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Instance instance = calcPriorityProblem.getState().getInstance();
        PlaceOfInterest candidate = calcPriorityProblem.getCandidate();

        return instance.getDistance(candidate, instance.getEndPOI());
    }
}
