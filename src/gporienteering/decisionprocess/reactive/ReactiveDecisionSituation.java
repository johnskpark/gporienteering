package gporienteering.decisionprocess.reactive;

import gporienteering.core.PlaceOfInterest;
import gporienteering.decisionprocess.DecisionProcessState;
import gporienteering.decisionprocess.DecisionSituation;

import java.util.LinkedList;
import java.util.List;

/**
 * The decision situation for reactive decision process.
 */

public class ReactiveDecisionSituation extends DecisionSituation {

    private List<PlaceOfInterest> pool;
    private DecisionProcessState state;

    public ReactiveDecisionSituation(List<PlaceOfInterest> pool,
                                     DecisionProcessState state) {
        this.pool = pool;
        this.state = state;
    }

    public List<PlaceOfInterest> getPool() {
        return pool;
    }

    public DecisionProcessState getState() {
        return state;
    }

    public ReactiveDecisionSituation clone() {
        List<PlaceOfInterest> clonedPool = new LinkedList<>(pool);
        DecisionProcessState clonedState = state.clone();

        return new ReactiveDecisionSituation(clonedPool, clonedState);
    }
}
