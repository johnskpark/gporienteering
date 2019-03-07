
package ec.multiobjective.moead;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.LineNumberReader;

import ec.EvolutionState;
import ec.Fitness;
import ec.multiobjective.MultiObjectiveFitness;
import ec.util.Code;

/**
 * TODO javadoc for MOEADMultiObjectiveFitness
 *
 */
public class MOEADMultiObjectiveFitness extends MultiObjectiveFitness {

	private static final long serialVersionUID = 288892384459659876L;
	
	public static final String MOEAD_FITNESS_PREAMBLE = "ScalarFitness: ";
    public static final String MOEAD_INDEX_PREAMBLE = "WeightIndex: ";

    public String[] getAuxilliaryFitnessNames() { return new String[] { "ScalarFitness", "WeightVectorIndex" }; }
    public double[] getAuxilliaryFitnessValues() { return new double[] { scalarFitness, weightIndex }; }

    // Scalar scalarFitness calculated from the weight vector.
    private double scalarFitness;

    // The weight vector assigned to the individual.
    private int weightIndex;

    public String fitnessToString() {
        return super.fitnessToString() + "\n" + MOEAD_FITNESS_PREAMBLE + Code.encode(scalarFitness) + "\n" + MOEAD_INDEX_PREAMBLE + Code.encode(weightIndex);
    }

    public String fitnessToStringForHumans() {
        return super.fitnessToStringForHumans() + "\n" + MOEAD_FITNESS_PREAMBLE + scalarFitness + "\n" + MOEAD_INDEX_PREAMBLE + weightIndex;
    }

    public void readFitness(final EvolutionState state, final LineNumberReader reader) throws IOException {
        super.readFitness(state, reader);
        scalarFitness = Code.readDoubleWithPreamble(MOEAD_FITNESS_PREAMBLE, state, reader);

        weightIndex = Code.readIntegerWithPreamble(MOEAD_INDEX_PREAMBLE, state, reader);
    }

    public void writeFitness(final EvolutionState state, final DataOutput dataOutput) throws IOException {
        super.writeFitness(state, dataOutput);
        dataOutput.writeDouble(scalarFitness);
        dataOutput.writeInt(weightIndex);
        writeTrials(state, dataOutput);
    }

    public void readFitness(final EvolutionState state, final DataInput dataInput) throws IOException {
        super.readFitness(state, dataInput);

        scalarFitness = dataInput.readDouble();
        weightIndex = dataInput.readInt();

        readTrials(state, dataInput);
    }

    public double getScalarFitness() {
        return scalarFitness;
    }

    public int getWeightIndex(final EvolutionState state) {
        return weightIndex;
    }

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
        return (this.scalarFitness == other.scalarFitness);
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

    @Override
    public Object clone() {
        MOEADMultiObjectiveFitness clone = (MOEADMultiObjectiveFitness) super.clone();
        clone.scalarFitness = this.scalarFitness;
        clone.weightIndex = this.weightIndex;
        return clone;
    }
}
