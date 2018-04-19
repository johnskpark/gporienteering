package gporienteering.decisionprocess.reactive;

import gporienteering.decisionprocess.DecisionProcess;
import gporienteering.decisionprocess.DecisionProcessEvent;
import gporienteering.decisionprocess.DecisionProcessState;
import gporienteering.decisionprocess.Policy;
import gporienteering.decisionprocess.reactive.event.ReactiveVisitEvent;

import java.util.PriorityQueue;

/**
 * The reactive decision process builds the solution in real time.
 */
public class ReactiveDecisionProcess extends DecisionProcess {

    public ReactiveDecisionProcess(DecisionProcessState state,
                                   PriorityQueue<DecisionProcessEvent> eventQueue,
                                   Policy policy) {
        super(state, eventQueue, policy);
    }

    @Override
    public void reset() {
        state.reset();
        eventQueue.clear();
        eventQueue.add(new ReactiveVisitEvent(state.getInstance().getStartTime()));
    }
}
