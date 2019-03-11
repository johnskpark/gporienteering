
package ec.multiobjective.moead;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.Subpopulation;
import ec.simple.SimpleBreeder;
import ec.util.Parameter;

/**
 * TODO javadoc for MOEADBreeder
 */
public class MOEADBreeder extends SimpleBreeder {

	private static final long serialVersionUID = 7156212177540138653L;

	private int[] breedIndices;
	private int[][] indWeightIndices;

	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		// make sure SimpleBreeder's elites facility isn't being used
		for (int i = 0; i < elite.length; i++) { // we use elite.length here instead of pop.subpops.length because the population hasn't been made yet.
			if (usingElitism(i)) {
				state.output.warning("You're using elitism with MOEADBreeder, which is not permitted and will be ignored.  However the reevaluate-elites parameter *will* bre recognized by NSGAEvaluator.",
						base.push(P_ELITE).push("" + i));
			}
		}

		if (sequentialBreeding) { // uh oh, haven't tested with this
			state.output.fatal("MOEADBreeder does not support sequential evaluation.", base.push(P_SEQUENTIAL_BREEDING));
		}

		if (!clonePipelineAndPopulation) {
			state.output.fatal("clonePipelineAndPopulation must be true for MOEADBreeder.");
		}
	}

	/**
	 * Override breedPopulation(). We take the result from the super method in
	 * SimpleBreeder and append it to the old population. Hence, after
	 * generation 0, every subsequent call to
	 * <code>MOEADEvaluator.evaluatePopulation()</code> will be passed a
	 * population of 2x<code>originalPopSize</code> individuals.
	 */
	public Population breedPopulation(EvolutionState state) {
		Population oldPop = (Population) state.population;
		Subpopulation[] subpops = oldPop.subpops;
		int subpopLength = subpops.length;

		indWeightIndices = new int[subpopLength][];
		for (int i = 0; i < subpopLength; i++) {
			indWeightIndices[i] = new int[oldPop.subpops[i].individuals.length];
		}
		
		breedIndices = new int[subpopLength];
		Population newPop = super.breedPopulation(state);
		MOEADEvaluator eval = (MOEADEvaluator) state.evaluator;
		for (int i = 0; i < subpopLength; i++) {
			Individual[] newInds = newPop.subpops[i].individuals;
			for (int j = 0; j < newInds.length; j++) {
				MOEADMultiObjectiveFitness fitness = (MOEADMultiObjectiveFitness) newInds[j].fitness;
				
				int index = indWeightIndices[i][j];
				fitness.setWeightIndex(state, eval.getMOEADWeights()[i][index], index);
			}
		}

		Individual[] combinedInds;
		Subpopulation oldSubpop;
		Subpopulation newSubpop;

		for (int i = 0; i < subpopLength; i++) {
			oldSubpop = oldPop.subpops[i];
			newSubpop = newPop.subpops[i];

			combinedInds = new Individual[oldSubpop.individuals.length + newSubpop.individuals.length];
			System.arraycopy(newSubpop.individuals, 0, combinedInds, 0,  newSubpop.individuals.length);
			System.arraycopy(oldSubpop.individuals, 0, combinedInds,  newSubpop.individuals.length, oldSubpop.individuals.length);
			newSubpop.individuals = combinedInds;
		}
		return newPop;
	}

	@Override
	protected void breedPopChunk(Population newpop, 
			EvolutionState state, 
			int[] numinds, 
			int[] from, 
			int threadnum)  {
		for (int subpop = 0; subpop < newpop.subpops.length; subpop++) {
			// if it's subpop's turn and we're doing sequential breeding...
			if (!shouldBreedSubpop(state, subpop, threadnum)) {
				// instead of breeding, we should just copy forward this subpopulation.  We'll copy the part we're assigned
				for (int ind = from[subpop]; ind < numinds[subpop] - from[subpop]; ind++) {
					newpop.subpops[subpop].individuals[ind] = state.population.subpops[subpop].individuals[ind];
				}
			} else  {
				// do regular breeding of this subpopulation
				BreedingPipeline bp = null;
				if (clonePipelineAndPopulation) {
					bp = (BreedingPipeline)newpop.subpops[subpop].species.pipe_prototype.clone();
				} else {
					bp = (BreedingPipeline)newpop.subpops[subpop].species.pipe_prototype;
				}

				// check to make sure that the breeding pipeline produces
				// the right kind of individuals.  Don't want a mistake there! :-)
				int x;
				if (!bp.produces(state,newpop,subpop,threadnum)) {
					state.output.fatal("The Breeding Pipeline of subpopulation " + subpop + " does not produce individuals of the expected species " + newpop.subpops[subpop].species.getClass().getName() + " or fitness " + newpop.subpops[subpop].species.f_prototype );
				}
				bp.prepareToProduce(state,subpop,threadnum);

				x = from[subpop];
				int upperbound = from[subpop]+numinds[subpop];
				while (x < upperbound) {
					breedIndices[subpop] = x;
					
					x += bp.produce(1, upperbound - x, x, subpop, newpop.subpops[subpop].individuals, state, threadnum);
				}
				if (x>upperbound) { // uh oh!  Someone blew it!
					state.output.fatal("Whoa! A breeding pipeline overwrote the space of another pipeline in subpopulation " + subpop + ".  You need to check your breeding pipeline code (in produce() ).");
				}

				bp.finishProducing(state,subpop,threadnum);
			}
		}
	}

	/**
	 * Assign the individual in the specified position with the specified weight index to evaluate the individual with.
	 * @param state
	 * @param subpopIndex
	 * @param indIndex
	 * @param weightIndex
	 */
	public void assignWeightIndex(final EvolutionState state, int subpopIndex, int indIndex, int weightIndex) {
		if (indIndex < indWeightIndices[subpopIndex].length) {
			indWeightIndices[subpopIndex][indIndex] = weightIndex;
		} else {
			state.output.warning("Ignoring attempt to assign weight to individual out of range: " + indIndex);
		}
	}
	
	/**
	 * Returns the current index of breeding for the particular thread.
	 * @param state
	 * @param subpopIndex
	 * @return
	 */
	public int getBreedIndex(final EvolutionState state, int subpopIndex) {
		return breedIndices[subpopIndex];
	}

}
