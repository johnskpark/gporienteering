package gporienteering.gp.terminal.feature;

import gporienteering.gp.CalcPriorityProblem;
import gporienteering.gp.terminal.FeatureGPNode;

/**
 * Feature: the score of the candidate.
 */
public class ScoreMin extends FeatureGPNode {
    public ScoreMin() {
        super();
        name = "ScoreMin";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        double[] scores = calcPriorityProblem.getCandidate().getScores();
        double min = scores[0];
        for (int i = 1; i < scores.length; i++) {
            min = (min > scores[i]) ? scores[i] : min;
        }
        return min;
    }
}
