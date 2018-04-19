package gporienteering.decisionprocess;

/**
 * An abstract decision process event.
 * It has a time that the event occurs.
 * Natural comparison prefers the earlier event.
 *
 * Created by gphhucarp on 28/08/17.
 */
public abstract class DecisionProcessEvent implements Comparable<DecisionProcessEvent> {
    protected double time;

    public DecisionProcessEvent(double time) {
        this.time = time;
    }

    public double getTime() {
        return time;
    }

    public abstract void trigger(DecisionProcess decisionProcess);

    @Override
    public int compareTo(DecisionProcessEvent o) {
        if (time < o.time)
            return -1;

        if (time > o.time)
            return 1;

        return 0;
    }
}
