/*
  Copyright 2010 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ec.multiobjective.moead;

import ec.*;
import ec.multiobjective.MultiObjectiveDefaults;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.SimpleEvaluator;
import ec.util.MersenneTwister;
import ec.util.Parameter;
import org.apache.commons.math3.util.Pair;

import java.util.*;

/*
 * MOEADEvaluator.java
 *
 * Created: Sat Oct 16 00:19:57 EDT 2010
 * By: Faisal Abidi and Sean Luke
 */


/**
 * The MOEADEvaluator is a simple generational evaluator which
 * evaluates every single member of the population (in a multithreaded fashion).
 * Then it reduces the population size to an <i>archive</i> consisting of the
 * best front ranks.  When there isn't enough space to fit another front rank,
 * individuals in that final front rank vie for the remaining slots in the archive
 * based on their sparsity.
 *
 * <p>The evaluator is also responsible for calculating the rank and
 * sparsity values stored in the MOEADWeightedSumFitness class and used largely
 * for statistical information.
 *
 * <p>NSGA-II has fixed archive size (the population size), and so ignores the 'elites'
 * declaration.  However it will adhere to the 'reevaluate-elites' parameter in SimpleBreeder
 * to determine whether to force scalarFitness reevaluation.
 *
 */

// TODO this is just a copy of the NSGA-II evaluator file, rewrite later down the line.
public class MOEADEvaluator extends SimpleEvaluator {

    public static final String P_WEIGHT_DISTRIBUTION = "weight-distribution";
    public static final String P_NUM_NEIGHBOURS = "num-neighbours";
    public static final String P_SCALAR_FITNESS = "scalar-fitness";
    public static final String P_SEED = "seed";

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

    private long initSeed;
    private MersenneTwister random;

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

        initSeed = state.parameters.getLongWithDefault(base.push(P_SEED), null, 0);
        random = new MersenneTwister(initSeed);
        state.output.message("Seed for initial weight assignment: " + initSeed);
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
            originalPopSizes[s] = state.parameters.getInt(p.push(""+s).push(Subpopulation.P_SUBPOPSIZE), null, 1);

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
            neighbourhood[s] = new int [popWeightVectors[s].length][numNeighbours];

            for (int i = 0; i < popWeightVectors[s].length; i++) {
                List<Pair<Integer, Double>> indexDistancePair = new ArrayList<>();

                for (int j = i; j < popWeightVectors[s].length; j++) {
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

        super.evaluatePopulation(state);
        for (int s = 0; s < numSubpops; s++) {
            state.population.subpops[s].individuals = updatePopulation(state, s);
        }
    }

    /**
     * Randomly assigns weights to the individuals in the population.
     * @param state
     * @param subpopIndex
     */
    // TODO not sure if this is required.
    public void assignWeightVectors(final EvolutionState state, final int subpopIndex) {
        Subpopulation subpop = state.population.subpops[subpopIndex];
        int subpopSize = subpop.individuals.length;

//        List<Integer> indices = new ArrayList<>();
//        for (int i = 0; i < subpopSize; i++) { indices.add(i); }

        for (int i = 0; i < subpopSize; i++) {
            MOEADMultiObjectiveFitness fitness = (MOEADMultiObjectiveFitness) subpop.individuals[i].fitness;

//            int randIndex = indices.get(i);
            double[] vector = popWeightVectors[subpopIndex][i];
            double[] copy = new double[vector.length];
            System.arraycopy(vector, 0, copy, 0, vector.length);

//            fitness.setWeightVector(state, copy, randIndex);
            fitness.setWeightIndex(state, i);
        }

        // TODO see if this works with the other part, maybe it doesn't.
//        popWeightAssign[subpopIndex] = new int[subpopSize];
//        for (int i = 0; i < subpopSize; i++) {
//            popWeightAssign[subpopIndex][i] = indices.get(i);
//        }
    }

    /**
     * Assigns the scalar fitness to the individuals in the subpopulation bsaed on the assigned weight and the decomposition method.
     * @param state
     * @param subpop
     */
    // TODO not sure if this is required.
//    public void assignScalarFitness(final EvolutionState state, final Subpopulation subpop) {
//        for (int i = 0; i < subpop.individuals.length; i++) {
//            MOEADMultiObjectiveFitness fitness = (MOEADMultiObjectiveFitness) subpop.individuals[i].fitness;
//
//            double[] weights = fitness.getWeightVector();
//
//            if (fitnessMethod.equals(V_WEIGHTED_SUM)) {
//                double scalarFitness = 0;
//                for (int f = 0; f < fitness.getNumObjectives(); f++) {
//                    scalarFitness += fitness.getObjective(f) * weights[f];
//                }
//                fitness.setScalarFitness(state, scalarFitness);
//            } else if (fitnessMethod.equals(V_TCHEBYCHEFF)) {
//                state.output.fatal("Tchebycheff fitness decomposition method is not yet implemented"); // FIXME need to implement later down the line.
//            } else {
//                state.output.fatal("Undefined fitness decomposition method: " + fitnessMethod);
//            }
//        }
//    }


    /**
     * Scale the subpopulation down to the original size by updating the parent individuals with the child individuals.
     * TODO this is broken at the moment.
     * @param state
     * @param subpopIndex
     */
    public Individual[] updatePopulation(final EvolutionState state, int subpopIndex) {
        // Only use the child individuals
        Subpopulation subpop = state.population.subpops[subpopIndex];
        int popSize = originalPopSizes[subpopIndex];

        Individual[] newInds = new Individual[popSize];
        System.arraycopy(subpop.individuals, 0, newInds, 0, newInds.length);

        // Replace the parent individuals with the offspring if they dominate them in terms of their respective fitness.
        for (int i = popSize; i < subpop.individuals.length; i++) {
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

        for (int i = 0; i < popSize; i++) {
            ((MOEADMultiObjectiveFitness) newInds[i].fitness).setWeightIndex(state, i);
        }

        return newInds;
    }

//    protected boolean betterOrEqual(MOEADMultiObjectiveFitness fitness1, MOEADMultiObjectiveFitness fitness2, double[] weightVector) {
//        double scalarFitness1 = 0.0;
//        double scalarFitness2 = 0.0;
//
//        for (int i = 0; i < weightVector.length; i++) {
//            scalarFitness1 += weightVector[i] * fitness1.getObjective(i);
//            scalarFitness2 += weightVector[i] * fitness2.getObjective(i);
//        }
//
//        if (scalarFitness1 <= scalarFitness2) {
//            return true;
//        } else {
//            return false;
//        }
//    }

}