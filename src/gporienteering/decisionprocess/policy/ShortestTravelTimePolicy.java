package gporienteering.decisionprocess.policy;

import gporienteering.core.PlaceOfInterest;
import gporienteering.core.Visit;
import gporienteering.decisionprocess.DecisionProcessState;
import gporienteering.decisionprocess.Policy;
import gporienteering.decisionprocess.PoolFilter;
import gporienteering.decisionprocess.TieBreaker;
import gporienteering.decisionprocess.poolfilter.ExpFeasiblePoolFilter;
import gporienteering.decisionprocess.tiebreaker.SimpleTieBreaker;

public class ShortestTravelTimePolicy extends Policy {

    public ShortestTravelTimePolicy(PoolFilter poolFilter, TieBreaker tieBreaker) {
        super(poolFilter, tieBreaker);
        name = "\"STT\"";
    }

    public ShortestTravelTimePolicy(TieBreaker tieBreaker) {
        this(new ExpFeasiblePoolFilter(), tieBreaker);
    }

    public ShortestTravelTimePolicy() {
        this(new SimpleTieBreaker());
    }

    @Override
    public double priority(PlaceOfInterest candidate, DecisionProcessState state) {
        Visit currVisit = state.getTour().getCurrentVisit();

        return state.getInstance().travelTimeDepartAt(
                currVisit.getPlaceOfInterest(), candidate, currVisit.getDepartTime());
    }
}
