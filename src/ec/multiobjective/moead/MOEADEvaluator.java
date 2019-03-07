
package ec.multiobjective.moead;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.util.Pair;

import ec.EvolutionState;
import ec.Individual;
import ec.Initializer;
import ec.Population;
import ec.Subpopulation;
import ec.multiobjective.MultiObjectiveDefaults;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.SimpleEvaluator;
import ec.util.Parameter;

/**
 * TODO javadoc for MOEADEvaluator
 *
 */
public class MOEADEvaluator extends SimpleEvaluator {

	private static final long serialVersionUID = -3594476343773566711L;
	
	public static final String P_WEIGHT_DISTRIBUTION = "weight-distribution";
    public static final String P_NUM_NEIGHBOURS = "num-neighbours";
    public static final String P_SCALAR_FITNESS = "scalar-fitness";

    public static final String V_UNIFORM_WEIGHT = "uniform";
    public static final String V_WEIGHTED_SUM = "weighted-sum";
    public static final String V_TCHEBYCHEFF = "tchebycheff";

    /**
     * The original population size is stored here so NSGA2 knows how large to create the archive
     * (it's the size of the original population -- keep in mind that MOEADBreeder had made the
     * population larger to include the children.
     */
    private int originalPopSizes[];
    private int numObjectives;
    private double[][][] popWeightVectors;

    private int numNeighbours;
    private int[][][] neighbourhood;

    private String fitnessMethod;

    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);

        String distribution = state.parameters.getString(base.push(P_WEIGHT_DISTRIBUTION), null);
        if (distribution.equals(V_UNIFORM_WEIGHT)) {
            initUniform(state);
        } else {
            state.output.warning("Unrecognised weight distribution: " + distribution + " defaulting to uniform distribution.");
            initUniform(state);
        }

        numNeighbours = state.parameters.getInt(base.push(P_NUM_NEIGHBOURS), null);
        initNeighbour(state);

        fitnessMethod = state.parameters.getString(base.push(P_SCALAR_FITNESS), null);
        if (fitnessMethod.equals(V_WEIGHTED_SUM)) {
            /* Do nothing for the weighted sum */
        } else if (fitnessMethod.equals(V_TCHEBYCHEFF)) {
            /* Initialise a reference point for the tchebycheff's approach */
            initRefPoint(state);
        } else {
            state.output.warning("Unrecognised scalarFitness decomposition method: " + fitnessMethod + "defaulting to weighted sum.");
            fitnessMethod = V_WEIGHTED_SUM;
        }
    }

    private void initUniform(final EvolutionState state) {
        Parameter p = new Parameter(Initializer.P_POP);
        int subpopsLength = state.parameters.getInt(p.push(Population.P_SIZE), null, 1);
        originalPopSizes = new int[subpopsLength];

        popWeightVectors = new double[subpopsLength][][];

        Parameter m = MultiObjectiveDefaults.base().push(MultiObjectiveFitness.P_FITNESS);
        numObjectives = state.parameters.getInt(m.push(MultiObjectiveFitness.P_NUMOBJECTIVES), null, 2);

        /* For now, we've only got the code implemented for 2 objectives. */
        if (numObjectives != 2) {
            state.output.fatal("Other methods of initialising reference vectors not yet implemented.");
        }

        for (int s = 0; s < subpopsLength; s++) {
        	Parameter subpopParam = p.push("subpop").push(""+s);
        	
            originalPopSizes[s] = state.parameters.getInt(subpopParam.push(Subpopulation.P_SUBPOPSIZE), null, 1);
            popWeightVectors[s] = new double[originalPopSizes[s]][numObjectives];

            /* FIXME make this work with more than two dimensions later down the line. */
            double partitionSize = 1.0 / (originalPopSizes[s] - 1.0);
            for (int i = 0; i < originalPopSizes[s]; i++) {
                popWeightVectors[s][i][0] = i * partitionSize;
                popWeightVectors[s][i][1] = 1.0 - i * partitionSize;
            }
        }
    }

    private void initNeighbour(final EvolutionState state) {
        neighbourhood = new int[popWeightVectors.length][][];

        for (int s = 0; s < popWeightVectors.length; s++) {
            neighbourhood[s] = new int[popWeightVectors[s].length][numNeighbours];

            for (int i = 0; i < popWeightVectors[s].length; i++) {
                List<Pair<Integer, Double>> indexDistancePair = new ArrayList<>();

                for (int j = 0; j < popWeightVectors[s].length; j++) {
                    double[] vector1 = popWeightVectors[s][i];
                    double[] vector2 = popWeightVectors[s][j];

                    if (vector1.length != vector2.length) {
                        state.output.fatal("Weight vectors at indices " + i + " and " + j + " in subpop " + s + " do not match in lengths.");
                    }

                    /* Calculate the Euclidean distances */
                    double distance = 0;
                    for (int k = 0; k < vector1.length; k++) {
                        distance += (vector1[k] - vector2[k]) * (vector1[k] - vector2[k]);
                    }
                    distance = Math.sqrt(distance);
                    indexDistancePair.add(new Pair<>(j, distance));
                }

                Collections.sort(indexDistancePair, new Comparator<Pair<Integer, Double>>() {
                    public int compare(Pair<Integer, Double> o1, Pair<Integer, Double> o2) {
                        if (o1.getSecond() < o2.getSecond()) {
                            return -1;
                        } else if (o1.getSecond() > o2.getSecond()) {
                            return 1;
                        }
                        return 0;
                    }
                });

                for (int j = 0; j < numNeighbours; j++) {
                    neighbourhood[s][i][j] = indexDistancePair.get(j).getFirst();
                }
            }
        }
    }

    private void initRefPoint(final EvolutionState state) {
        state.output.fatal("Tchebycheff fitness decomposition method is not yet implemented"); // FIXME need to implement later down the line.
    }

    /**
     * Evaluates the population, then builds the archive and reduces the population to just the archive.
     */
    public void evaluatePopulation(final EvolutionState state) {
        int numSubpops = state.population.subpops.length;
    	if (state.generation == 0) {
    		for (int s = 0; s < numSubpops; s++) {
    			assignWeightVectors(state, s);
    		}
    	} 
    	
    	super.evaluatePopulation(state);
    	
    	if (state.generation != 0) {
	        for (int s = 0; s < numSubpops; s++) {
	            state.population.subpops[s].individuals = updatePopulation(state, s);
	        }
    	}
    }

    /**
     * Randomly assigns weights to the individuals in the population.
     * @param state
     * @param subpopIndex
     */
    public void assignWeightVectors(final EvolutionState state, final int subpopIndex) {
        Subpopulation subpop = state.population.subpops[subpopIndex];
        int subpopSize = subpop.individuals.length;

        for (int i = 0; i < subpopSize; i++) {
            MOEADMultiObjectiveFitness fitness = (MOEADMultiObjectiveFitness) subpop.individuals[i].fitness;

            fitness.setWeightIndex(state, i);
        }
    }


    /**
     * Scale the subpopulation down to the original size by updating the parent individuals with the child individuals.
     * @param state
     * @param subpopIndex
     */
    public Individual[] updatePopulation(final EvolutionState state, int subpopIndex) {
        // Only use the child individuals
        Subpopulation subpop = state.population.subpops[subpopIndex];
        int subpopSize = originalPopSizes[subpopIndex];

        Individual[] newInds = new Individual[subpopSize];
        System.arraycopy(subpop.individuals, 0, newInds, 0, newInds.length);

        // Replace the parent individuals with the offspring if they dominate them in terms of their respective fitness.
        for (int i = 0; i < subpop.individuals.length - subpopSize; i++) {
            MOEADMultiObjectiveFitness childFitness = (MOEADMultiObjectiveFitness) subpop.individuals[i].fitness;

            int weightIndex = childFitness.getWeightIndex(state);
            for (int n = 0; n < neighbourhood[subpopIndex][weightIndex].length; n++) {
                int index = neighbourhood[subpopIndex][weightIndex][n];
                double[] weightVector = popWeightVectors[subpopIndex][index];
                MOEADMultiObjectiveFitness parentFitness = (MOEADMultiObjectiveFitness) subpop.individuals[index].fitness;

                childFitness.assignScalarFitness(state, weightVector);
                parentFitness.assignScalarFitness(state, weightVector);
                if (childFitness.equivalentTo(parentFitness) || childFitness.betterThan(parentFitness)) {
                    newInds[index] = (Individual) subpop.individuals[i].clone();
                }
            }
        }
        
        for (int i = 0; i < subpopSize; i++) {
            ((MOEADMultiObjectiveFitness) newInds[i].fitness).setWeightIndex(state, i);
        }

        return newInds;
    }

}