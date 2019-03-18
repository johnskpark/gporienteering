package gporienteering.gp.terminal.feature.moead;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.*;
import ec.multiobjective.MultiObjectiveDefaults;
import ec.multiobjective.MultiObjectiveFitness;
import ec.multiobjective.moead.MOEADMultiObjectiveFitness;
import ec.util.Code;
import ec.util.DecodeReturn;
import ec.util.Parameter;
import gporienteering.gp.CalcPriorityProblem;
import gporienteering.gp.terminal.FeatureGPNode;
import gporienteering.gp.terminal.feature.Score;
import gporienteering.gp.terminal.feature.Score2;
import gputils.DoubleData;
import gputils.terminal.DoubleERC;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import static ec.Fitness.P_FITNESS;
import static ec.multiobjective.MultiObjectiveFitness.P_NUMOBJECTIVES;

/**
 * Feature: a weighted sum of the scores of the candidate. Weights are generated randomly.
 */
public class RandomWeightedScore extends ERC {

    private double[] weights;
    private String name;

    public RandomWeightedScore() {
        super();
        children = new GPNode[0];
        name = "RandomWeightedScore";
    }

    @Override
    public void resetNode(final EvolutionState state, final int thread) {
        weights = new double[2];
        double sum = 0.0;
        for (int i = 0; i < 2; i++) {
            weights[i] = state.random[thread].nextDouble();
            sum += weights[i];
        }

        for (int i = 0; i < 2; i++) {
            weights[i] /= sum;
        }
    }

    @Override
    public boolean nodeEquals(final GPNode node) {
        if (this.getClass() != node.getClass()) { return false; }

        RandomWeightedScore other = (RandomWeightedScore) node;

        if (this.weights.length != other.weights.length) { return false; }
        for (int i = 0; i < this.weights.length; i++) {
            if (this.weights[i] != other.weights[i]) {
                return false;
            }
        }
        return true;
    }

    public void readNode(final EvolutionState state, final DataInput dataInput) throws IOException {
        weights[0] = dataInput.readDouble();
        weights[1] = dataInput.readDouble();
    }

    public void writeNode(final EvolutionState state, final DataOutput dataOutput) throws IOException {
        dataOutput.writeDouble(weights[0]);
        dataOutput.writeDouble(weights[0]);
    }

    public String encode() {
        return Code.encode(weights[0]) + ":" + Code.encode(weights[1]);
    }

    public boolean decode(DecodeReturn dret) {
        // store the position and the string in case they
        // get modified by Code.java
        int pos = dret.pos;
        String data = dret.data;
        String[] split = data.split(":");

        for (int i = 0; i < split.length; i++) {
            // decode
            Code.decode(dret);

            if (dret.type != DecodeReturn.T_DOUBLE) // uh oh!
            {
                // restore the position and the string; it was an error
                dret.data = data;
                dret.pos = pos;
                return false;
            }

            // store the data
            weights[i] = dret.d;
        }
        return true;
    }



    @Override
    public int printNode(final EvolutionState state, final PrintWriter writer) {
        String n = "(+ (* " + weights[0] + " " + (new Score()).getName() + " ) (* " + weights[1] + " " + (new Score2()).getName() + "))"; // Only supports two score values for now.
        writer.print(n);
        return n.length();
    }

    @Override
    public String toStringForHumans() {
//        return "(+ (* " + weights[0] + " " + (new Score()).getName() + " ) (* " + weights[1] + " " + (new Score2()).getName() + "))"; // Only supports two score values for now.
        return name;
    }

    @Override
    public String toString() {
        return toStringForHumans();
    }

    @Override
    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem) {
        // The problem is essentially a priority calculation.
        CalcPriorityProblem calcPrioProb = ((CalcPriorityProblem)problem);

        DoubleData data = ((DoubleData)input);

        double[] scores = calcPrioProb.getCandidate().getScores();
        double sum = scores[0] * weights[0];
        for (int i = 1; i < scores.length; i++) {
            sum += scores[i] * weights[i];
        }
        data.value = sum;
    }
}
