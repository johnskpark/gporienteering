package gporienteering.gp.terminal.feature;

import gporienteering.core.Instance;
import gporienteering.core.PlaceOfInterest;
import gporienteering.core.Tour;
import gporienteering.core.Visit;
import gporienteering.gp.CalcPriorityProblem;
import gporienteering.gp.terminal.FeatureGPNode;

public class TimeToArrive extends FeatureGPNode {

    public TimeToArrive() {
        super();
        name = "TA";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        PlaceOfInterest candidate = calcPriorityProblem.getCandidate();

        return candidate.getTimeToArrive();
    }
}
