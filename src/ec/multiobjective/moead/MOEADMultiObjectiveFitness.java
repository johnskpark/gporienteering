
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
	public static final String MOEAD_WEIGHT_PREAMBLE = "WeightVector: ";
    public static final String MOEAD_INDEX_PREAMBLE = "WeightIndex: ";

    public String[] getAuxilliaryFitnessNames() { return new String[] { "ScalarFitness" }; }
    public double[] getAuxilliaryFitnessValues() { return new double[] { scalarFitness }; }

    // Scalar scalarFitness calculated from the weight vector.
    private double scalarFitness;

    // The weight vector assigned to the individual along with its index.
    private double[] weightVector;
    private int weightIndex;

    public String fitnessToString() {
        return super.fitnessToString() + "\n" + MOEAD_FITNESS_PREAMBLE + Code.encode(scalarFitness) + "\n" + MOEAD_WEIGHT_PREAMBLE + Code.encode(convertToStr(weightVector)) + "\n" + MOEAD_INDEX_PREAMBLE + Code.encode(weightIndex);
    }

    public String fitnessToStringForHumans() {
        return super.fitnessToStringForHumans() + "\n" + MOEAD_FITNESS_PREAMBLE + scalarFitness + "\n" + MOEAD_WEIGHT_PREAMBLE + Code.encode(convertToStr(weightVector)) + "\n" + MOEAD_INDEX_PREAMBLE + weightIndex;
    }

    public void readFitness(final EvolutionState state, final LineNumberReader reader) throws IOException {
        super.readFitness(state, reader);
        scalarFitness = Code.readDoubleWithPreamble(MOEAD_FITNESS_PREAMBLE, state, reader);

        weightVector = convertToArray(Code.readStringWithPreamble(MOEAD_WEIGHT_PREAMBLE, state, reader));
        weightIndex = Code.readIntegerWithPreamble(MOEAD_INDEX_PREAMBLE, state, reader);
    }

    public void writeFitness(final EvolutionState state, final DataOutput dataOutput) throws IOException {
        super.writeFitness(state, dataOutput);
        dataOutput.writeDouble(scalarFitness);
        dataOutput.writeChars(convertToStr(weightVector) + "\n");
        dataOutput.writeInt(weightIndex);
        writeTrials(state, dataOutput);
    }

    public void readFitness(final EvolutionState state, final DataInput dataInput) throws IOException {
        super.readFitness(state, dataInput);
        scalarFitness = dataInput.readDouble();
        weightVector = convertToArray(dataInput.readLine());
        weightIndex = dataInput.readInt();
        readTrials(state, dataInput);
    }

    public double getScalarFitness() {
        return scalarFitness;
    }
    
    public double[] getWeightVector(final EvolutionState state) {
    	return weightVector;
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

    public void setWeightIndex(final EvolutionState state, double[] weightVector, int weightIndex) {
    	this.weightVector = weightVector;
        this.weightIndex = weightIndex;
    }

    public boolean equivalentTo(Fitness _fitness) {
        MOEADMultiObjectiveFitness other = (MOEADMultiObjectiveFitness) _fitness;
        return (this.scalarFitness == other.scalarFitness);
    }

    /**
     * We specify the tournament selection criteria, Rank (lower
     * values are better) and Sparsity (higher values are better)
     */
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
    
    private String convertToStr(double[] weight) {
    	String s = "[" + weight[0];
    	for (int i = 1; i < weight.length; i++) {
    		s += " " + weight[i];
    	}
    	s += "]";
    	return s;
    }
    
    private double[] convertToArray(String weight) {
    	String trim = weight.replaceAll("[^\\[\\]]", "");
    	String[] split = trim.split("\\s+");
    	double[] array = new double[split.length];
    	for (int i = 0; i < array.length; i++) {
    		array[i] = Double.parseDouble(split[i]);
    	}
    	return array;
    }
}
