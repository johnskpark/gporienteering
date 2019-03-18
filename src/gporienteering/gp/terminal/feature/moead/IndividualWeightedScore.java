package gporienteering.gp.terminal.feature.moead;

import ec.EvolutionState;
import ec.multiobjective.moead.MOEADEvaluator;
import ec.multiobjective.moead.MOEADMultiObjectiveFitness;
import gporienteering.gp.CalcPriorityProblem;
import gporienteering.gp.terminal.FeatureGPNode;
import gporienteering.gp.terminal.feature.Score;
import gporienteering.gp.terminal.feature.Score2;

import java.io.PrintWriter;

/**
 * Feature: a weighted sum of the scores of the candidate. Weights are extracted from individual.
 */
public class IndividualWeightedScore extends FeatureGPNode {

    private double[] weights;

    public IndividualWeightedScore() {
        super();

//        weights = new double[2];
//        updateName();
        name = "IndividualWeightedScore";
    }

    @Override
    public void resetNode(final EvolutionState state, final int thread) {
        super.resetNode(state, thread);


    }

    @Override
    public int printNode(final EvolutionState state, final PrintWriter writer) {
        String n = "(+ (* " + weights[0] + " " + (new Score()).getName() + " ) (* " + weights[1] + " " + (new Score2()).getName() + "))"; // Only supports two score values for now.
        writer.print(n);
        return n.length();
    }


    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
    	// Also, the GP tree in evaluation doesn't contain the individual either, so I'm not sure how this will work at all.
//        if (individual != null && individual.fitness != null) {
//            MOEADMultiObjectiveFitness fitness = (MOEADMultiObjectiveFitness) individual.fitness;
//            double[] weights = fitness.getWeightVector(null);
//        } else {
//            throw new RuntimeException("This should not be called outside of evolution and without defined individual.");
//        }

        double[] scores = calcPriorityProblem.getCandidate().getScores();
        double sum = scores[0] * weights[0];
        for (int i = 1; i < scores.length; i++) {
            sum += scores[i] * weights[i];
        }
        return sum;
    }

//    private void updateName() {
//        name = "(+ (* " + weights[0] + " " + (new Score()).getName() + " ) (* " + weights[1] + " " + (new Score2()).getName() + "))";
//    }
}
