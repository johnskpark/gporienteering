package gporienteering.gp.terminal.feature;

import gporienteering.core.PlaceOfInterest;
import gporienteering.gp.CalcPriorityProblem;
import gporienteering.gp.terminal.FeatureGPNode;

/**
 * Feature: the score of the candidate.
 */
public class ScoreMax extends FeatureGPNode {
    public ScoreMax() {
        super();
        name = "ScoreMax";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        double[] scores = calcPriorityProblem.getCandidate().getScores();
        double max = scores[0];
        for (int i = 1; i < scores.length; i++) {
            max = (max < scores[i]) ? scores[i] : max;
        }
        return max;
    }
}
