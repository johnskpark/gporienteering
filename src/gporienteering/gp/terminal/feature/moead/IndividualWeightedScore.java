package gporienteering.gp.terminal.feature.moead;

import java.io.PrintWriter;

import ec.EvolutionState;
import ec.gp.GPTree;
import ec.multiobjective.moead.MOEADMultiObjectiveFitness;
import gporienteering.gp.CalcPriorityProblem;
import gporienteering.gp.terminal.FeatureGPNode;
import gporienteering.gp.terminal.feature.Score;
import gporienteering.gp.terminal.feature.Score2;

/**
 * Feature: a weighted sum of the scores of the candidate. Weights are extracted from individual.
 */
public class IndividualWeightedScore extends FeatureGPNode {

    private MOEADMultiObjectiveFitness fitness;
    private double[] weights;

    public IndividualWeightedScore() {
        super();
        weights = new double[2];
    }

    @Override
    public int printNode(final EvolutionState state, final PrintWriter writer) {
        String n = "(+ (* " + weights[0] + " " + (new Score()).getName() + " ) (* " + weights[1] + " " + (new Score2()).getName() + "))"; // Only supports two score values for now.
        writer.print(n);
        return n.length();
    }

    @Override
    public String toStringForHumans() {
        return "(+ (* " + weights[0] + " " + (new Score()).getName() + ") (* " + weights[1] + " " + (new Score2()).getName() + "))"; // Only supports two score values for now.
    }

    @Override
    public String toString() {
        return "IndividualWeightedScore";
    }

    @Override
    public double value(CalcPriorityProblem calcPriorityProblem) {
        GPTree root = (GPTree) rootParent();
        fitness = (MOEADMultiObjectiveFitness) root.owner.fitness;
        weights = fitness.getWeightVector(null);
        
        double[] scores = calcPriorityProblem.getCandidate().getScores();
        double sum = scores[0] * weights[0];
        for (int i = 1; i < scores.length; i++) {
            sum += scores[i] * weights[i];
        }
        return sum;
    }

}
