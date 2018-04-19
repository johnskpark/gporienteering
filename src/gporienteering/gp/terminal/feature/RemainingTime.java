package gporienteering.gp.terminal.feature;

import gporienteering.core.Instance;
import gporienteering.core.Tour;
import gporienteering.gp.CalcPriorityProblem;
import gporienteering.gp.terminal.FeatureGPNode;

/**
 * Feature: the remaining time of the journey.
 */
public class RemainingTime extends FeatureGPNode {

    public RemainingTime() {
        super();
        name = "RemT";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        Instance instance = calcPriorityProblem.getState().getInstance();
        Tour tour = calcPriorityProblem.getState().getTour();
        int currDay = calcPriorityProblem.getState().getCurrDay();
        int remainingDays = instance.getNumDays() - 1 - currDay;
        double value = remainingDays * (instance.getEndTime() - instance.getStartTime());

        return value + instance.getEndTime() - tour.getCurrentVisit().getDepartTime();
    }
}
