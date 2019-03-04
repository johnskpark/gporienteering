package gporienteering.gp.terminal.feature;

import gporienteering.gp.CalcPriorityProblem;
import gporienteering.gp.terminal.FeatureGPNode;

/**
 * Feature: the score of the candidate.
 */
public class Score2 extends FeatureGPNode {
    public Score2() {
        super();
        name = "SCORE2";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        return calcPriorityProblem.getCandidate().getScore(1);
    }
}
