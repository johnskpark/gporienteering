package gporienteering.decisionprocess;

import gporienteering.core.PlaceOfInterest;
import gporienteering.decisionprocess.poolfilter.IdentityPoolFilter;
import gporienteering.decisionprocess.reactive.ReactiveDecisionSituation;
import gporienteering.decisionprocess.tiebreaker.SimpleTieBreaker;

import java.util.List;

public abstract class Policy {

    protected String name;
    protected PoolFilter poolFilter;
    protected TieBreaker tieBreaker;

    public Policy(PoolFilter poolFilter, TieBreaker tieBreaker) {
        this.poolFilter = poolFilter;
        this.tieBreaker = tieBreaker;
    }

    public Policy(PoolFilter poolFilter) {
        this(poolFilter, new SimpleTieBreaker());
    }

    public Policy(TieBreaker tieBreaker) {
        this(new IdentityPoolFilter(), tieBreaker);
    }

    public String getName() {
        return name;
    }

    public PoolFilter getPoolFilter() {
        return poolFilter;
    }

    public TieBreaker getTieBreaker() {
        return tieBreaker;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Given the current decison process state,
     * select the next candidate to be added.
     * @param rds the reactive decision situation.
     * @return the next candidate.
     */
    public PlaceOfInterest next(ReactiveDecisionSituation rds) {
        List<PlaceOfInterest> pool = rds.getPool();
        DecisionProcessState state = rds.getState();

        List<PlaceOfInterest> filteredPool = poolFilter.filter(pool, state);

        if (filteredPool.isEmpty())
            return null;

        PlaceOfInterest next = filteredPool.get(0);
        next.setPriority(priority(next, state));

        for (int i = 1; i < filteredPool.size(); i++) {
            PlaceOfInterest tmp = filteredPool.get(i);
            tmp.setPriority(priority(tmp, state));

            if (Double.compare(tmp.getPriority(), next.getPriority()) < 0 ||
                    (Double.compare(tmp.getPriority(), next.getPriority()) == 0 &&
                            tieBreaker.breakTie(tmp, next) < 0))
                next = tmp;
        }

        return next;
    }

    /**
     * Calculate the priority of a candidate given a state.
     * @param candidate the candidate.
     * @param state the state.
     * @return the priority of the candidate.
     */
    public abstract double priority(PlaceOfInterest candidate,
                                    DecisionProcessState state);
}
