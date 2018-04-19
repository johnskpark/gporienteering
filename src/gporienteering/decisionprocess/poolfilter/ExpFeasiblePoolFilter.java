package gporienteering.decisionprocess.poolfilter;

import gporienteering.core.Instance;
import gporienteering.core.PlaceOfInterest;
import gporienteering.core.Visit;
import gporienteering.decisionprocess.DecisionProcessState;
import gporienteering.decisionprocess.PoolFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * A feasible pool filter filters the candidates from the pool by selecting
 * only the candidates that are expected to be feasible to be added.
 */

public class ExpFeasiblePoolFilter extends PoolFilter {

    @Override
    public List<PlaceOfInterest> filter(List<PlaceOfInterest> pool,
                                        DecisionProcessState state) {
        Instance instance = state.getInstance();
        Visit currVisit = state.getTour().getCurrentVisit();
        PlaceOfInterest currPOI = currVisit.getPlaceOfInterest();
        double currTime = currVisit.getDepartTime();

        List<PlaceOfInterest> filtered = new ArrayList<>();
        for (PlaceOfInterest candidate : pool) {
            // check if it is expected to be feasible or not
            double time1 = instance.travelTimeDepartAt(currPOI, candidate, currTime);
            double arrTime1 = currTime + time1;

            if (arrTime1 > candidate.getCloseTime()) // cannot meet the time window
                continue;

            double visitStartTime = arrTime1;
            if (visitStartTime < candidate.getOpenTime())
                visitStartTime = candidate.getOpenTime();

            double depTime = visitStartTime + candidate.getDuration();

            double time2 = instance.travelTimeDepartAt(candidate, instance.getEndPOI(), depTime);
            double arrTime2 = depTime + time2;

            if (arrTime2 > instance.getEndTime()) // cannot come back in time
                continue;

            filtered.add(candidate);
        }

        return filtered;
    }
}
