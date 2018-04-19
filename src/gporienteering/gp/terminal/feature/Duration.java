package gporienteering.gp.terminal.feature;

import gporienteering.core.PlaceOfInterest;
import gporienteering.gp.CalcPriorityProblem;
import gporienteering.gp.terminal.FeatureGPNode;

public class Duration extends FeatureGPNode {

    public Duration() {
        super();
        name = "DUR";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        PlaceOfInterest candidate = calcPriorityProblem.getCandidate();

        return candidate.getDuration();
    }
}
