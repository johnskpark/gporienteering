package ec.multiobjective.moead;

import ec.EvolutionState;
import ec.Individual;
import ec.select.RandomSelection;

/**
 * TODO Javadoc for MOEADTournamentSelection
 *
 */
// Wait, I don't think its tournament selection... 
public class MOEADRandomSelection extends RandomSelection {

	private static final long serialVersionUID = 8673832232021030397L;

	private MOEADBreeder breeder;

	@Override
	public void prepareToProduce(final EvolutionState s, final int subpopulation, final int thread) {
		super.prepareToProduce(s, subpopulation, thread);
		this.breeder = (MOEADBreeder) s.breeder;
	}
	
	@Override
	public int produce(final int min,
			final int max,
			final int start,
			final int subpopulation,
			final Individual[] inds,
			final EvolutionState state,
			final int thread) {
		int n = INDS_PRODUCED;
		if (n < min) { n = min; }
		if (n > max) { n = max; }

		for(int i = 0; i < n; i++) {
			// Start the tournament selection.
			int parentIndex = produce(subpopulation,state,thread);
			Individual parentInd =  state.population.subpops[subpopulation].individuals[parentIndex];
			MOEADMultiObjectiveFitness fitness = (MOEADMultiObjectiveFitness) parentInd.fitness;
			
			int from = breeder.getBreedIndex(state, subpopulation);
			breeder.assignWeightIndex(state, subpopulation, from + i, fitness.getWeightIndex(state)); 

			inds[start + i] = parentInd;
		}
		return n;
	}

}
