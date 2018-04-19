package gporienteering.decisionprocess;

import gporienteering.core.Instance;
import gporienteering.decisionprocess.reactive.ReactiveDecisionProcess;
import gporienteering.decisionprocess.reactive.event.ReactiveVisitEvent;

import java.util.PriorityQueue;

/**
 * An abstract of a decision process.
 * It includes
 *  - A decision process state: the state of the environment
 *  - An event queue: the events to happen
 *  - A policy that makes decisions during the decision process.
 */

public abstract class DecisionProcess {
    protected DecisionProcessState state; // the state
    protected PriorityQueue<DecisionProcessEvent> eventQueue;
    protected Policy policy;

    public DecisionProcess(DecisionProcessState state,
                           PriorityQueue<DecisionProcessEvent> eventQueue,
                           Policy policy) {
        this.state = state;
        this.eventQueue = eventQueue;
        this.policy = policy;
    }

    public DecisionProcessState getState() {
        return state;
    }

    public PriorityQueue<DecisionProcessEvent> getEventQueue() {
        return eventQueue;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    /**
     * Initialise a reactive decision process from an instance and a policy.
     * @param instance the given instance.
     * @param policy the given policy.
     * @return the initial reactive decision process.
     */
    public static ReactiveDecisionProcess initReactive(Instance instance,
                                                       Policy policy) {
        DecisionProcessState state = new DecisionProcessState(instance);
        PriorityQueue<DecisionProcessEvent> eventQueue = new PriorityQueue<>();
        eventQueue.add(new ReactiveVisitEvent(instance.getStartTime()));

        return new ReactiveDecisionProcess(state, eventQueue, policy);
    }

    /**
     * Run the decision process.
     */
    public void run() {
        while (!eventQueue.isEmpty()) {
            DecisionProcessEvent event = eventQueue.poll();
            event.trigger(this);
        }
    }

    /**
     * Reset the decision process.
     * This is done by reseting the decision process state and event queue.
     */
    public abstract void reset();
}
