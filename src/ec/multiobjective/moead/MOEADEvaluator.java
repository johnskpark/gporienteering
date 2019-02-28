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
import ec.util.Parameter;

import java.util.ArrayList;
import java.util.List;

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
 * sparsity values stored in the MOEADMultiObjectiveFitness class and used largely
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
    public static final String P_FITNESS = "scalarFitness";

    public static final String V_UNIFORM_WEIGHT = "uniform";
    public static final String V_WEIGHTED_SUM = "weighted-sum";
    public static final String V_TCHEBYCHEFF = "tchebycheff";

    /**
     * The original population size is stored here so NSGA2 knows how large to create the archive
     * (it's the size of the original population -- keep in mind that MOEADBreeder had made the
     * population larger to include the children.
     */
    private int originalPopSize[];
    private int numObjectives;
    private List<List<Double>> weights;

    private String fitnessMethod;

    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);

        String distribution = state.parameters.getString(base.push(P_WEIGHT_DISTRIBUTION), null);
        if (distribution.equals(V_UNIFORM_WEIGHT)) {
            uniformWeightDistribution(state);
        } else {
            state.output.warning("Unrecognised weight distribution: " + distribution + " defaulting to uniform distribution.");
            uniformWeightDistribution(state);
        }

        fitnessMethod = state.parameters.getString(base.push(P_FITNESS), null);
        if (!(fitnessMethod.equals(V_WEIGHTED_SUM) || !fitnessMethod.equals(V_TCHEBYCHEFF))) {
            state.output.warning("Unrecognised scalarFitness decomposition method: " + fitnessMethod + "defaulting to weighted sum.");
            fitnessMethod = V_WEIGHTED_SUM;
        }
    }

    private void uniformWeightDistribution(final EvolutionState state) {
        weights = new ArrayList<>();

        Parameter p = new Parameter(Initializer.P_POP);
        int subpopsLength = state.parameters.getInt(p.push(Population.P_SIZE), null, 1);
        originalPopSize = new int[subpopsLength];

        Parameter m = MultiObjectiveDefaults.base().push(MultiObjectiveFitness.P_FITNESS);
        numObjectives = state.parameters.getInt(m.push(MultiObjectiveFitness.P_NUMOBJECTIVES), null, 2);

        for (int i = 0; i < subpopsLength; i++) {
            weights.add(new ArrayList<>());
            originalPopSize[i] = state.parameters.getInt(p.push(""+i).push(Subpopulation.P_SUBPOPSIZE), null, 1);

            /* TODO uniformly generate the weights based on the number of objectives and the population size */
        }
    }

    /**
     * Evaluates the population, then builds the archive and reduces the population to just the archive.
     */
    public void evaluatePopulation(final EvolutionState state) {
        super.evaluatePopulation(state);
        for (int x = 0; x < state.population.subpops.length; x++) {
            state.population.subpops[x].individuals = buildArchive(state, x);
        }
    }

    public void assignScalarFitness(Subpopulation subpop) {
        for (int i = 0; i < subpop.individuals.length; i++) {
            MOEADMultiObjectiveFitness fitness = (MOEADMultiObjectiveFitness) subpop.individuals[i].fitness;

            if (fitnessMethod.equals(V_WEIGHTED_SUM)) {
                fitness.scalarFitness = 0; // TODO
            } else if (fitnessMethod.equals(V_TCHEBYCHEFF)) {
                fitness.scalarFitness = 0; // TODO
            }
        }
    }


    /**
     * Build the auxiliary scalarFitness data and reduce the subpopulation to just the archive, which is
     * returned.
     */
    public Individual[] buildArchive(EvolutionState state, int subpop) {
        throw new RuntimeException("Not yet implemented.");
    }

//    public Individual[] buildArchive(EvolutionState state, int subpop)
//        {
//        Individual[] dummy = new Individual[0];
//        ArrayList ranks = assignFrontRanks(state.population.subpops[subpop]);
//
//        ArrayList newSubpopulation = new ArrayList();
//        int size = ranks.size();
//        for(int i = 0; i < size; i++)
//            {
//            Individual[] rank = (Individual[])((ArrayList)(ranks.get(i))).toArray(dummy);
//            assignSparsity(rank);
//            if (rank.length + newSubpopulation.size() >= originalPopSize[subpop])
//                {
//                // first sort the rank by sparsity
//                QuickSort.qsort(rank, new SortComparator()
//                    {
//                    public boolean lt(Object a, Object b)
//                        {
//                        Individual i1 = (Individual) a;
//                        Individual i2 = (Individual) b;
//                        return (((MOEADMultiObjectiveFitness) i1.scalarFitness).scalarFitness > ((MOEADMultiObjectiveFitness) i2.scalarFitness).scalarFitness);
//                        }
//
//                    public boolean gt(Object a, Object b)
//                        {
//                        Individual i1 = (Individual) a;
//                        Individual i2 = (Individual) b;
//                        return (((MOEADMultiObjectiveFitness) i1.scalarFitness).scalarFitness < ((MOEADMultiObjectiveFitness) i2.scalarFitness).scalarFitness);
//                        }
//                    });
//
//                // then put the m sparsest individuals in the new population
//                int m = originalPopSize[subpop] - newSubpopulation.size();
//                for(int j = 0 ; j < m; j++)
//                    newSubpopulation.add(rank[j]);
//
//                // and bail
//                break;
//                }
//            else
//                {
//                // dump in everyone
//                for(int j = 0 ; j < rank.length; j++)
//                    newSubpopulation.add(rank[j]);
//                }
//            }
//
//        Individual[] archive = (Individual[])(newSubpopulation.toArray(dummy));
//
//        // maybe force reevaluation
//        MOEADBreeder breeder = (MOEADBreeder)(state.breeder);
//        if (breeder.reevaluateElites[subpop])
//            for(int i = 0 ; i < archive.length; i++)
//                archive[i].evaluated = false;
//
//        return archive;
//        }


    /** Divides inds into ranks and assigns each individual's rank to be the rank it was placed into.
     Each front is an ArrayList. */
//    public ArrayList assignFrontRanks(Subpopulation subpop) {
//        Individual[] inds = subpop.individuals;
//        ArrayList frontsByRank = MultiObjectiveFitness.partitionIntoRanks(inds);
//
//        int numRanks = frontsByRank.size();
//        for(int rank = 0; rank < numRanks; rank++)
//            {
//            ArrayList front = (ArrayList)(frontsByRank.get(rank));
//            int numInds = front.size();
//            for(int ind = 0; ind < numInds; ind++) {
//                ((MOEADMultiObjectiveFitness)(((Individual)(front.get(ind))).scalarFitness)).scalarFitness = rank;
//            }
//        return frontsByRank;
//        }


    /**
     * Computes and assigns the sparsity values of a given front.
     */
//    public void assignSparsity(Individual[] front)
//        {
//        int numObjectives = ((MOEADMultiObjectiveFitness) front[0].scalarFitness).getObjectives().length;
//
//        for (int i = 0; i < front.length; i++)
//            ((MOEADMultiObjectiveFitness) front[i].scalarFitness).sparsity = 0;
//
//        for (int i = 0; i < numObjectives; i++)
//            {
//            final int o = i;
//            // 1. Sort front by each objective.
//            // 2. Sum the manhattan distance of an individual's neighbours over
//            // each objective.
//            // NOTE: No matter which objectives objective you sort by, the
//            // first and last individuals will always be the same (they maybe
//            // interchanged though). This is because a Pareto front's
//            // objective values are strictly increasing/decreasing.
//            QuickSort.qsort(front, new SortComparator()
//                {
//                public boolean lt(Object a, Object b)
//                    {
//                    Individual i1 = (Individual) a;
//                    Individual i2 = (Individual) b;
//                    return (((MOEADMultiObjectiveFitness) i1.scalarFitness).getObjective(o) < ((MOEADMultiObjectiveFitness) i2.scalarFitness).getObjective(o));
//                    }
//
//                public boolean gt(Object a, Object b)
//                    {
//                    Individual i1 = (Individual) a;
//                    Individual i2 = (Individual) b;
//                    return (((MOEADMultiObjectiveFitness) i1.scalarFitness).getObjective(o) > ((MOEADMultiObjectiveFitness) i2.scalarFitness).getObjective(o));
//                    }
//                });
//
//            // Compute and assign sparsity.
//            // the first and last individuals are the sparsest.
//            ((MOEADMultiObjectiveFitness) front[0].scalarFitness).sparsity = Double.POSITIVE_INFINITY;
//            ((MOEADMultiObjectiveFitness) front[front.length - 1].scalarFitness).sparsity = Double.POSITIVE_INFINITY;
//            double minObjValue = ((MOEADMultiObjectiveFitness) front[0].scalarFitness).getObjective(o);
//            double maxObjValue = ((MOEADMultiObjectiveFitness) front[front.length - 1].scalarFitness).getObjective(o);
//            for (int j = 1; j < front.length - 1; j++)
//                {
//                MOEADMultiObjectiveFitness f_j = (MOEADMultiObjectiveFitness) (front[j].scalarFitness);
//                MOEADMultiObjectiveFitness f_jplus1 = (MOEADMultiObjectiveFitness) (front[j+1].scalarFitness);
//                MOEADMultiObjectiveFitness f_jminus1 = (MOEADMultiObjectiveFitness) (front[j-1].scalarFitness);
//
//                // store the NSGA2Sparsity in sparsity
//                f_j.sparsity += (f_jplus1.getObjective(o) - f_jminus1.getObjective(o)) / (maxObjValue - minObjValue);
////                f_j.sparsity += (f_jplus1.getObjective(o) - f_jminus1.getObjective(o)) / (f_j.maxObjective[o] - f_j.minObjective[o]);
//                }
//            }
//        }
//    }
}