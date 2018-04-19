package gporienteering.decisionprocess.tiebreaker;

import gporienteering.core.PlaceOfInterest;
import gporienteering.decisionprocess.TieBreaker;

/**
 * A simple tie breaker between two arcs uses the natural comparator.
 */

public class SimpleTieBreaker extends TieBreaker {

    @Override
    public int breakTie(PlaceOfInterest poi1, PlaceOfInterest poi2) {
        return poi1.compareTo(poi2);
    }
}
