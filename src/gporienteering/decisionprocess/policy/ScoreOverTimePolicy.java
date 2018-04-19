package gporienteering.decisionprocess.policy;

import gporienteering.core.PlaceOfInterest;
import gporienteering.core.Visit;
import gporienteering.decisionprocess.DecisionProcessState;
import gporienteering.decisionprocess.Policy;
import gporienteering.decisionprocess.PoolFilter;
import gporienteering.decisionprocess.TieBreaker;
import gporienteering.decisionprocess.poolfilter.ExpFeasiblePoolFilter;
import gporienteering.decisionprocess.tiebreaker.SimpleTieBreaker;

public class ScoreOverTimePolicy extends Policy {

    public ScoreOverTimePolicy(PoolFilter poolFilter, TieBreaker tieBreaker) {
        super(poolFilter, tieBreaker);
        name = "\"SOT\"";
    }

    public ScoreOverTimePolicy(TieBreaker tieBreaker) {
        this(new ExpFeasiblePoolFilter(), tieBreaker);
    }

    public ScoreOverTimePolicy() {
        this(new SimpleTieBreaker());
    }

    @Override
    public double priority(PlaceOfInterest candidate, DecisionProcessState state) {
        Visit currVisit = state.getTour().getCurrentVisit();

        return state.getInstance().travelTimeDepartAt(
                currVisit.getPlaceOfInterest(), candidate, currVisit.getDepartTime()) / candidate.getScore(0);
    }
}
