package gporienteering.gp;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleProblemForm;
import gporienteering.core.PlaceOfInterest;
import gporienteering.decisionprocess.DecisionProcessState;

/**
 * The problem for calculating the priority of a candidate.
 *
 * Created by YiMei on 27/09/16.
 */
public class CalcPriorityProblem extends Problem implements SimpleProblemForm {

    private PlaceOfInterest candidate;
    private DecisionProcessState state;

    public CalcPriorityProblem(PlaceOfInterest candidate,
                               DecisionProcessState state) {
        this.candidate = candidate;
        this.state = state;
    }

    public PlaceOfInterest getCandidate() {
        return candidate;
    }

    public DecisionProcessState getState() {
        return state;
    }

    @Override
    public void evaluate(EvolutionState state, Individual ind,
                         int subpopulation, int threadnum) {
    }
}
