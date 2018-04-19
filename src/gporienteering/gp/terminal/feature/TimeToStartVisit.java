package gporienteering.gp.terminal.feature;

import gporienteering.core.Instance;
import gporienteering.core.PlaceOfInterest;
import gporienteering.core.Tour;
import gporienteering.core.Visit;
import gporienteering.gp.CalcPriorityProblem;
import gporienteering.gp.terminal.FeatureGPNode;

/**
 * Feature: the time to finish the visit (travel, wait to open, and finish the visit.
 */
public class TimeToStartVisit extends FeatureGPNode {

    public TimeToStartVisit() {
        super();
        name = "TSV";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        PlaceOfInterest candidate = calcPriorityProblem.getCandidate();

        return candidate.getTimeToStartVisit();
    }
}
