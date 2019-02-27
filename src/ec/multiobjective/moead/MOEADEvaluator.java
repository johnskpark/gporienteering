/*
  Copyright 2010 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ec.multiobjective.moead;

import ec.*;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.SimpleEvaluator;
import ec.util.Parameter;
import ec.util.QuickSort;
import ec.util.SortComparator;

import java.util.ArrayList;

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
 * to determine whether to force fitness reevaluation.
 *
 */

// TODO this is just a copy of the NSGA-II evaluator file, rewrite later down the line.
public class MOEADEvaluator extends SimpleEvaluator {
    /**
     * The original population size is stored here so NSGA2 knows how large to create the archive
     * (it's the size of the original population -- keep in mind that MOEADBreeder had made the
     * population larger to include the children.
     */
    public int originalPopSize[];

    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);

        Parameter p = new Parameter(Initializer.P_POP);
        int subpopsLength = state.parameters.getInt(p.push(Population.P_SIZE), null, 1);
        Parameter p_subpop;
        originalPopSize = new int[subpopsLength];
        for (int i = 0; i < subpopsLength; i++) {
            p_subpop = p.push(Population.P_SUBPOP).push("" + i).push(Subpopulation.P_SUBPOPSIZE);
            originalPopSize[i] = state.parameters.getInt(p_subpop, null, 1);
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


    /**
     * Build the auxiliary fitness data and reduce the subpopulation to just the archive, which is
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
//                        return (((MOEADMultiObjectiveFitness) i1.fitness).fitness > ((MOEADMultiObjectiveFitness) i2.fitness).fitness);
//                        }
//
//                    public boolean gt(Object a, Object b)
//                        {
//                        Individual i1 = (Individual) a;
//                        Individual i2 = (Individual) b;
//                        return (((MOEADMultiObjectiveFitness) i1.fitness).fitness < ((MOEADMultiObjectiveFitness) i2.fitness).fitness);
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
//                ((MOEADMultiObjectiveFitness)(((Individual)(front.get(ind))).fitness)).fitness = rank;
//            }
//        return frontsByRank;
//        }


    /**
     * Computes and assigns the sparsity values of a given front.
     */
//    public void assignSparsity(Individual[] front)
//        {
//        int numObjectives = ((MOEADMultiObjectiveFitness) front[0].fitness).getObjectives().length;
//
//        for (int i = 0; i < front.length; i++)
//            ((MOEADMultiObjectiveFitness) front[i].fitness).sparsity = 0;
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
//                    return (((MOEADMultiObjectiveFitness) i1.fitness).getObjective(o) < ((MOEADMultiObjectiveFitness) i2.fitness).getObjective(o));
//                    }
//
//                public boolean gt(Object a, Object b)
//                    {
//                    Individual i1 = (Individual) a;
//                    Individual i2 = (Individual) b;
//                    return (((MOEADMultiObjectiveFitness) i1.fitness).getObjective(o) > ((MOEADMultiObjectiveFitness) i2.fitness).getObjective(o));
//                    }
//                });
//
//            // Compute and assign sparsity.
//            // the first and last individuals are the sparsest.
//            ((MOEADMultiObjectiveFitness) front[0].fitness).sparsity = Double.POSITIVE_INFINITY;
//            ((MOEADMultiObjectiveFitness) front[front.length - 1].fitness).sparsity = Double.POSITIVE_INFINITY;
//            double minObjValue = ((MOEADMultiObjectiveFitness) front[0].fitness).getObjective(o);
//            double maxObjValue = ((MOEADMultiObjectiveFitness) front[front.length - 1].fitness).getObjective(o);
//            for (int j = 1; j < front.length - 1; j++)
//                {
//                MOEADMultiObjectiveFitness f_j = (MOEADMultiObjectiveFitness) (front[j].fitness);
//                MOEADMultiObjectiveFitness f_jplus1 = (MOEADMultiObjectiveFitness) (front[j+1].fitness);
//                MOEADMultiObjectiveFitness f_jminus1 = (MOEADMultiObjectiveFitness) (front[j-1].fitness);
//
//                // store the NSGA2Sparsity in sparsity
//                f_j.sparsity += (f_jplus1.getObjective(o) - f_jminus1.getObjective(o)) / (maxObjValue - minObjValue);
////                f_j.sparsity += (f_jplus1.getObjective(o) - f_jminus1.getObjective(o)) / (f_j.maxObjective[o] - f_j.minObjective[o]);
//                }
//            }
//        }
//    }
}