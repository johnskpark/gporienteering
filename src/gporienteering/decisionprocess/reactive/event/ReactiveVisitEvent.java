package gporienteering.decisionprocess.reactive.event;

import gporienteering.core.Instance;
import gporienteering.core.PlaceOfInterest;
import gporienteering.core.Tour;
import gporienteering.core.Visit;
import gporienteering.decisionprocess.DecisionProcess;
import gporienteering.decisionprocess.DecisionProcessEvent;
import gporienteering.decisionprocess.DecisionProcessState;
import gporienteering.decisionprocess.Policy;
import gporienteering.decisionprocess.reactive.ReactiveDecisionSituation;

/**
 * The reactive visit event occurs when the tourist finishes visiting the current POI,
 * and wants to decide the next POI to go.
 */

public class ReactiveVisitEvent extends DecisionProcessEvent {

    public ReactiveVisitEvent(double time) {
        super(time);
    }

    @Override
    public void trigger(DecisionProcess decisionProcess) {
        Policy policy = decisionProcess.getPolicy();
        DecisionProcessState state = decisionProcess.getState();
        Tour tour = state.getTour();
        Visit currVisit = tour.getCurrentVisit();
        PlaceOfInterest currPOI = currVisit.getPlaceOfInterest();
        double currTime = currVisit.getDepartTime();
        Instance instance = state.getInstance();

        ReactiveDecisionSituation rds = new ReactiveDecisionSituation(
                state.getFeasiblePOIs(), state);

        PlaceOfInterest next = policy.next(rds);

        if (next == null) {
            // go to the end POI and finish the tour of the day
            double time = instance.travelTimeDepartAt(currPOI,
                    instance.getEndPOI(), currTime);
            time *= instance.getTimeDeviation(currPOI, instance.getEndPOI());

            double finishTime = currTime + time;

            tour.addVisit(new Visit(instance.getEndPOI(), finishTime, instance.getStartTime()));

            // go to the next day
            state.goToNextDay();

            if (state.getCurrDay() == instance.getNumDays())
                return;

            decisionProcess.getEventQueue().add(
                    new ReactiveVisitEvent(instance.getStartTime()));
        }
        else {
            double timeToArrive = instance.travelTimeDepartAt(currPOI, next, currTime);
            timeToArrive *= instance.getTimeDeviation(currPOI, next);

            double visitStartTime = currTime + timeToArrive;
            if (visitStartTime < next.getOpenTime()) // arrive too early, wait
                visitStartTime = next.getOpenTime();

            double visitFinishTime = visitStartTime;
            if (visitStartTime > next.getCloseTime()) {
                // too late, skip visiting the POI
                tour.addVisit(new Visit(next, visitStartTime, visitFinishTime));
            }
            else {
                double duration = next.getDuration() * instance.getDurationDeviation(next);
                visitFinishTime = visitStartTime + duration;

                tour.addVisit(new Visit(next, visitStartTime, visitFinishTime));
            }

            state.removeUnvisitedPOI(next);
            state.removeFeasiblePOI(next);

            // update the feasible POIs
            state.updateFeasiblePOIs();

            decisionProcess.getEventQueue().add(
                    new ReactiveVisitEvent(visitFinishTime));
        }
    }

}
