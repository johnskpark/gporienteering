package gporienteering.decisionprocess.policy;

import gporienteering.core.PlaceOfInterest;
import gporienteering.decisionprocess.DecisionProcessState;
import gporienteering.decisionprocess.Policy;
import gporienteering.decisionprocess.PoolFilter;
import gporienteering.decisionprocess.TieBreaker;
import gporienteering.decisionprocess.poolfilter.ExpFeasiblePoolFilter;
import gporienteering.decisionprocess.tiebreaker.SimpleTieBreaker;

public class ScoreOverDistPolicy extends Policy {

    public ScoreOverDistPolicy(PoolFilter poolFilter, TieBreaker tieBreaker) {
        super(poolFilter, tieBreaker);
        name = "\"SOD\"";
    }

    public ScoreOverDistPolicy(TieBreaker tieBreaker) {
        this(new ExpFeasiblePoolFilter(), tieBreaker);
    }

    public ScoreOverDistPolicy() {
        this(new SimpleTieBreaker());
    }

    @Override
    public double priority(PlaceOfInterest candidate, DecisionProcessState state) {
        return state.getInstance().getDistance(
                state.getTour().getCurrentVisit().getPlaceOfInterest(), candidate) / candidate.getScore(0);
    }
}
