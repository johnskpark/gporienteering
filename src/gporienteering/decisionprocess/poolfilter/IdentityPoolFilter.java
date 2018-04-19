package gporienteering.decisionprocess.poolfilter;

import gporienteering.core.PlaceOfInterest;
import gporienteering.decisionprocess.DecisionProcessState;
import gporienteering.decisionprocess.PoolFilter;

import java.util.List;

/**
 * The identity pool filter does nothing, but simply returns the pool.
 * It is called "identity" since the filtered pool is the same as the given pool.
 */

public class IdentityPoolFilter extends PoolFilter {

    @Override
    public List<PlaceOfInterest> filter(List<PlaceOfInterest> pool,
                                        DecisionProcessState state) {
        return pool;
    }
}
