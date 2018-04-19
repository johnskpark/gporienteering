package gporienteering.decisionprocess.policy;

import gporienteering.core.PlaceOfInterest;
import gporienteering.decisionprocess.DecisionProcessState;
import gporienteering.decisionprocess.Policy;
import gporienteering.decisionprocess.PoolFilter;
import gporienteering.decisionprocess.TieBreaker;
import gporienteering.decisionprocess.poolfilter.ExpFeasiblePoolFilter;
import gporienteering.decisionprocess.tiebreaker.SimpleTieBreaker;

public class NearestNeighbourPolicy extends Policy {

    public NearestNeighbourPolicy(PoolFilter poolFilter, TieBreaker tieBreaker) {
        super(poolFilter, tieBreaker);
        name = "\"NN\"";
    }

    public NearestNeighbourPolicy(TieBreaker tieBreaker) {
        this(new ExpFeasiblePoolFilter(), tieBreaker);
    }

    public NearestNeighbourPolicy() {
        this(new SimpleTieBreaker());
    }

    @Override
    public double priority(PlaceOfInterest candidate, DecisionProcessState state) {
        return state.getInstance().getDistance(
                state.getTour().getCurrentVisit().getPlaceOfInterest(), candidate);
    }
}
