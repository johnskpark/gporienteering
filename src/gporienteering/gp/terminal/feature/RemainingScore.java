package gporienteering.gp.terminal.feature;

import gporienteering.core.Instance;
import gporienteering.core.PlaceOfInterest;
import gporienteering.core.Tour;
import gporienteering.gp.CalcPriorityProblem;
import gporienteering.gp.terminal.FeatureGPNode;

public class RemainingScore extends FeatureGPNode {

    public RemainingScore() {
        super();
        name = "RS";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        double value = 0;

        for (PlaceOfInterest poi : calcPriorityProblem.getState().getFeasiblePOIs())
            value += poi.getScore(0);

        return value;
    }
}
