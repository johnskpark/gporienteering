package gporienteering.gp.terminal.feature;

import gporienteering.gp.CalcPriorityProblem;
import gporienteering.gp.terminal.FeatureGPNode;

public class RemainingNumPOIs extends FeatureGPNode {

    public RemainingNumPOIs() {
        super();
        name = "RNP";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        return calcPriorityProblem.getState().getFeasiblePOIs().size();
    }
}
