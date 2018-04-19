package gporienteering.decisionprocess;

import gporienteering.core.PlaceOfInterest;

/**
 * A tie breaker breaks the tie between two candidates when they have the same priority.
 */

public abstract class TieBreaker {

    public abstract int breakTie(PlaceOfInterest poi1, PlaceOfInterest poi2);
}
