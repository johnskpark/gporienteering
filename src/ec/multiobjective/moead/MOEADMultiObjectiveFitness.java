/*
  Copyright 2010 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ec.multiobjective.moead;

import ec.EvolutionState;
import ec.Fitness;
import ec.multiobjective.MultiObjectiveFitness;
import ec.util.Code;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/*
 * MOEADMultiObjectiveFitness.java
 *
 * Created: Thu Feb 04 2010
 * By: Faisal Abidi and Sean Luke
 */

/**
 * MOEADMultiObjectiveFitness is a subclass of MultiObjeciveFitness which
 * adds auxiliary fitness measures (sparsity, rank) largely used by MultiObjectiveStatistics.
 * It also redefines the comparison measures to compare based on rank, and break ties
 * based on sparsity. 
 *
 */

// TODO this is just a copy of the NSGA-II fitness file, rewrite later down the line.
public class MOEADMultiObjectiveFitness extends MultiObjectiveFitness {

    public static final String MOEAD_RANK_PREAMBLE = "ScalarFitness: ";
    public static final String MOEAD_SPARSITY_PREAMBLE = "WeightVector: ";

    public String[] getAuxilliaryFitnessNames() { return new String[] { "ScalarFitness" }; }
    public double[] getAuxilliaryFitnessValues() { return new double[] { fitness }; }

    // Scalar fitness calculated from the weight vector.
    public double fitness;

    // The weight vector assigned to the individual.
    public List<Double> weights;

    public String fitnessToString() {
        return super.fitnessToString() + "\n" + MOEAD_RANK_PREAMBLE + Code.encode(fitness) + "\n" + MOEAD_SPARSITY_PREAMBLE + Code.encode(weights.toString());
    }

    public String fitnessToStringForHumans() {
        return super.fitnessToStringForHumans() + "\n" + MOEAD_RANK_PREAMBLE + fitness + "\n" + MOEAD_SPARSITY_PREAMBLE + weights.toString();
    }

    public void readFitness(final EvolutionState state, final LineNumberReader reader) throws IOException {
        super.readFitness(state, reader);
        fitness = Code.readDoubleWithPreamble(MOEAD_RANK_PREAMBLE, state, reader);

        String weightsStr = Code.readStringWithPreamble(MOEAD_SPARSITY_PREAMBLE, state, reader);
        weights = convertWeightStr(weightsStr);
    }

    public void writeFitness(final EvolutionState state, final DataOutput dataOutput) throws IOException {
        super.writeFitness(state, dataOutput);
        dataOutput.writeDouble(fitness);
        dataOutput.writeChars(weights.toString());
        writeTrials(state, dataOutput);
    }

    public void readFitness(final EvolutionState state, final DataInput dataInput) throws IOException {
        super.readFitness(state, dataInput);

        fitness = dataInput.readDouble();

        String weightsStr = "";
        char nextChar;
        while ((nextChar = dataInput.readChar()) != ']') {
            weightsStr += nextChar;
        }
        weights = convertWeightStr(weightsStr);

        readTrials(state, dataInput);
    }

    private List<Double> convertWeightStr(String weightsStr) {
        weightsStr = weightsStr.replaceAll("[\\[\\] ]", "");
        List<String> split = Arrays.asList(weightsStr.split(","));

        return split.stream().map(x -> Double.parseDouble(x)).collect(Collectors.toList());
    }

    // TODO need to check this against the actual algorithm.
    public boolean equivalentTo(Fitness _fitness) {
        MOEADMultiObjectiveFitness other = (MOEADMultiObjectiveFitness) _fitness;
        return (this.fitness == ((MOEADMultiObjectiveFitness) _fitness).fitness);
    }

    /**
     * We specify the tournament selection criteria, Rank (lower
     * values are better) and Sparsity (higher values are better)
     */
    // TODO need to check this against the actual algorithm.
    public boolean betterThan(Fitness _fitness) {
        MOEADMultiObjectiveFitness other = (MOEADMultiObjectiveFitness) _fitness;

        // Fitness should be minimised.
        if (this.fitness < other.fitness) {
            return true;
        } else {
            return false;
        }
    }
}
