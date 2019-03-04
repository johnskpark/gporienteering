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

/*
 * MOEADWeightedSumFitness.java
 *
 * Created: Thu Feb 04 2010
 * By: Faisal Abidi and Sean Luke
 */

/**
 * MOEADWeightedSumFitness is a subclass of MultiObjeciveFitness which
 * adds auxiliary scalarFitness measures (sparsity, rank) largely used by MultiObjectiveStatistics.
 * It also redefines the comparison measures to compare based on rank, and break ties
 * based on sparsity.
 *
 */

// TODO this is just a copy of the NSGA-II scalarFitness file, rewrite later down the line.
public class MOEADMultiObjectiveFitness extends MultiObjectiveFitness {

    public static final String MOEAD_FITNESS_PREAMBLE = "ScalarFitness: ";
//    public static final String MOEAD_WEIGHT_PREAMBLE = "WeightVector: ";
    public static final String MOEAD_INDEX_PREAMBLE = "WeightIndex: ";

    public String[] getAuxilliaryFitnessNames() { return new String[] { "ScalarFitness", "WeightVectorIndex" }; }
    public double[] getAuxilliaryFitnessValues() { return new double[] { scalarFitness, weightIndex }; }

    // Scalar scalarFitness calculated from the weight vector.
    private double scalarFitness;

    // The weight vector assigned to the individual.
//    private double[] weightVector;
    private int weightIndex;

    public String fitnessToString() {
//        return super.fitnessToString() + "\n" + MOEAD_INDEX_PREAMBLE + Code.encode(weightIndex);
        return super.fitnessToString() + "\n" + MOEAD_FITNESS_PREAMBLE + Code.encode(scalarFitness) + "\n" + MOEAD_INDEX_PREAMBLE + Code.encode(weightIndex);
    }

    public String fitnessToStringForHumans() {
//        return super.fitnessToStringForHumans() + "\n" + MOEAD_INDEX_PREAMBLE + weightIndex;
        return super.fitnessToStringForHumans() + "\n" + MOEAD_FITNESS_PREAMBLE + scalarFitness + "\n" + MOEAD_INDEX_PREAMBLE + weightIndex;
    }

    public void readFitness(final EvolutionState state, final LineNumberReader reader) throws IOException {
        super.readFitness(state, reader);
        scalarFitness = Code.readDoubleWithPreamble(MOEAD_FITNESS_PREAMBLE, state, reader);
//
//        String weightsStr = Code.readStringWithPreamble(MOEAD_WEIGHT_PREAMBLE, state, reader);
//        weightVector = convertWeightStr(weightsStr);
        weightIndex = Code.readIntegerWithPreamble(MOEAD_INDEX_PREAMBLE, state, reader);
    }

    public void writeFitness(final EvolutionState state, final DataOutput dataOutput) throws IOException {
        super.writeFitness(state, dataOutput);
        dataOutput.writeDouble(scalarFitness);
//        dataOutput.writeChars(weightVector.toString());
        dataOutput.writeInt(weightIndex);
        writeTrials(state, dataOutput);
    }

    public void readFitness(final EvolutionState state, final DataInput dataInput) throws IOException {
        super.readFitness(state, dataInput);

        scalarFitness = dataInput.readDouble();
//
//        String weightsStr = "";
//        char nextChar;
//        while ((nextChar = dataInput.readChar()) != ']') {
//            weightsStr += nextChar;
//        }
//        weightVector = convertWeightStr(weightsStr);
        weightIndex = dataInput.readInt();

        readTrials(state, dataInput);
    }

//    private double[] convertWeightStr(String weightsStr) {
//        weightsStr = weightsStr.replaceAll("[\\[\\] ]", "");
//        List<String> split = Arrays.asList(weightsStr.split(","));
//
//        double[] vector = new double[split.size()];
//        for (int i = 0; i < split.size(); i++) {
//            vector[i] = Double.parseDouble(split.get(i));
//        }
//
//        return vector;
//    }

    public double getScalarFitness() {
        return scalarFitness;
    }

//    public double[] getWeightVector() {
//        return weightVector;
//    }

    public int getWeightIndex(final EvolutionState state) {
        return weightIndex;
    }

//    public void setScalarFitness(final EvolutionState state, double scalarFitness) {
//        this.scalarFitness = scalarFitness;
//    }
//
//    public void setWeightVector(final EvolutionState state, double[] weightVector, int weightIndex) {
//        this.weightVector = weightVector;
//        this.weightIndex = weightIndex;
//    }

    public void assignScalarFitness(final EvolutionState state, double[] weightVector) {
        scalarFitness = 0.0;
        for (int i = 0; i < getNumObjectives(); i++) {
            scalarFitness += getObjective(i) * weightVector[i];
        }
    }

    public void setWeightIndex(final EvolutionState state, int weightIndex) {
        this.weightIndex = weightIndex;
    }

    /* FIXME Test against the actual algorithm */
    public boolean equivalentTo(Fitness _fitness) {
        MOEADMultiObjectiveFitness other = (MOEADMultiObjectiveFitness) _fitness;
        return (this.scalarFitness == ((MOEADMultiObjectiveFitness) _fitness).scalarFitness);
    }

    /**
     * We specify the tournament selection criteria, Rank (lower
     * values are better) and Sparsity (higher values are better)
     */
    /* FIXME Test against the actual algorithm */
    public boolean betterThan(Fitness _fitness) {
        MOEADMultiObjectiveFitness other = (MOEADMultiObjectiveFitness) _fitness;

        // Fitness should be minimised.
        if (this.scalarFitness < other.scalarFitness) {
            return true;
        } else {
            return false;
        }
    }

    /* FIXME test to make sure that cloning is done properly. */
    @Override
    public Object clone() {
        MOEADMultiObjectiveFitness clone = (MOEADMultiObjectiveFitness) super.clone();
        clone.scalarFitness = this.scalarFitness;
//        clone.weightVector = new double[this.weightVector.length];
//        System.arraycopy(this.weightVector, 0, clone.weightVector, 0, this.weightVector.length);
        clone.weightIndex = this.weightIndex;
        return clone;
    }
}
