package gporienteering.gp.terminal.feature;

import gporienteering.gp.CalcPriorityProblem;
import gporienteering.gp.terminal.FeatureGPNode;

/**
 * Feature: the score of the candidate.
 */
public class Score extends FeatureGPNode {
    public Score() {
        super();
        name = "SCORE";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        return calcPriorityProblem.getCandidate().getScore(0);
    }
}
