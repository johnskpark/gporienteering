package gporienteering.gp.terminal.feature.moead;

import ec.multiobjective.moead.MOEADEvaluator;
import ec.multiobjective.moead.MOEADMultiObjectiveFitness;
import gporienteering.gp.CalcPriorityProblem;
import gporienteering.gp.terminal.FeatureGPNode;

/**
 * Feature: the score of the candidate.
 */
public class ScoreWeightedSum extends FeatureGPNode {
    public ScoreWeightedSum() {
        super();
        name = "ScoreWeightedSum";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        // TODO this code doesn't work. 
//        MOEADEvaluator evaluator = (MOEADEvaluator) state.evaluator;
//        MOEADMultiObjectiveFitness fitness = (MOEADMultiObjectiveFitness) individual.fitness;

//        double[][][] allWeights = evaluator.getMOEADWeights();
//        int weightIndex = fitness.getWeightIndex(state);

        double[] scores = calcPriorityProblem.getCandidate().getScores();
//        double[] weights = allWeights[0][weightIndex];
        double[] weights = new double[scores.length];

        double sum = scores[0] * weights[0];
        for (int i = 1; i < scores.length; i++) {
            sum += scores[i] * weights[i];
        }
        return sum;
    }
}
