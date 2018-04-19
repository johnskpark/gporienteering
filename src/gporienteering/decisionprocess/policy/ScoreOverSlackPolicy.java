package gporienteering.decisionprocess.policy;

import gporienteering.core.PlaceOfInterest;
import gporienteering.core.Visit;
import gporienteering.decisionprocess.DecisionProcessState;
import gporienteering.decisionprocess.Policy;
import gporienteering.decisionprocess.PoolFilter;
import gporienteering.decisionprocess.TieBreaker;
import gporienteering.decisionprocess.poolfilter.ExpFeasiblePoolFilter;
import gporienteering.decisionprocess.tiebreaker.SimpleTieBreaker;

public class ScoreOverSlackPolicy extends Policy {

    public ScoreOverSlackPolicy(PoolFilter poolFilter, TieBreaker tieBreaker) {
        super(poolFilter, tieBreaker);
        name = "\"SOS\"";
    }

    public ScoreOverSlackPolicy(TieBreaker tieBreaker) {
        this(new ExpFeasiblePoolFilter(), tieBreaker);
    }

    public ScoreOverSlackPolicy() {
        this(new SimpleTieBreaker());
    }

    @Override
    public double priority(PlaceOfInterest candidate, DecisionProcessState state) {
        Visit currVisit = state.getTour().getCurrentVisit();
        PlaceOfInterest currPOI = currVisit.getPlaceOfInterest();
        double currTime = currVisit.getDepartTime();

        double arrTime = state.getInstance().travelTimeDepartAt(currPOI,
                candidate, currTime);

        double slack = currPOI.getCloseTime() - arrTime;

        return slack / candidate.getScore(0);
    }
}
