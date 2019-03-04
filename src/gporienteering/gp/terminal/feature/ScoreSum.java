package gporienteering.gp.terminal.feature;

import gporienteering.gp.CalcPriorityProblem;
import gporienteering.gp.terminal.FeatureGPNode;

/**
 * Feature: the score of the candidate.
 */
public class ScoreSum extends FeatureGPNode {
    public ScoreSum() {
        super();
        name = "ScoreSum";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        double[] scores = calcPriorityProblem.getCandidate().getScores();
        double sum = scores[0];
        for (int i = 1; i < scores.length; i++) {
            sum += scores[i];
        }
        return sum;
    }
}
