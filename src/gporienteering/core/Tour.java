package gporienteering.core;

import java.util.LinkedList;
import java.util.List;

public class Tour {
    private List<Visit> visits;
    private double[] totalScores;

    public Tour(List<Visit> visits, double[] totalScores) {
        this.visits = visits;
        this.totalScores = totalScores;
    }

    public Tour(int numScores) {
        this(new LinkedList<>(), new double[numScores]);
    }

    public Tour() {
        this(1);
    }

    /**
     * Construct an initial tour from an instance. It starts from the starting POI
     * of the instance.
     * @param instance the instance.
     */
    public Tour(Instance instance) {
        int numScores = instance.getStartPOI().getScores().length;
        visits = new LinkedList<>();
        totalScores = new double[numScores];

        addVisit(new Visit(instance.startPOI, instance.startTime, instance.startTime));
    }

    public List<Visit> getVisits() {
        return visits;
    }

    public double[] getTotalScores() {
        return totalScores;
    }

    public Visit getCurrentVisit() {
        return visits.get(visits.size()-1);
    }

    public void addVisit(Visit visit) {
        visits.add(visit);

        double[] scores = visit.scores();
        for (int i = 0; i < totalScores.length; i++)
            totalScores[i] += scores[i];
    }

    public void removeVisit(Visit visit) {
        visits.remove(visit);

        for (int i = 0; i < totalScores.length; i++)
            totalScores[i] -= visit.getPlaceOfInterest().getScore(i);
    }

    public void removeVisit(int index) {
        Visit removedVisit = visits.get(index);

        visits.remove(index);

        for (int i = 0; i < totalScores.length; i++)
            totalScores[i] -= removedVisit.getPlaceOfInterest().getScore(i);
    }

    /**
     * Return the value of an objective, NaN if the objective cannot calculated.
     * @param objective the objective.
     * @return the objective value of the solution.
     */
    public double objValue(Objective objective) {
        switch (objective) {
            case TOTAL_SCORE:
                return totalScores[0];
            default:
                return Double.NaN;
        }
    }

    /**
     * Clear the tour and the reset the total scores to zeros.
     */
    public void clear() {
        visits.clear();
        for (int i = 0; i < totalScores.length; i++)
            totalScores[i] = 0;
    }

    /**
     * Reset the tour from an instance. The tour starts from the starting POI
     * of the instance.
     * @param instance the intance.
     */
    public void reset(Instance instance) {
        clear();
        addVisit(new Visit(instance.startPOI, instance.startTime, instance.startTime));
    }

    @Override
    public String toString() {
        String str = "";
        for (Visit visit : visits) {
            str += visit.getPlaceOfInterest().toString() + ", ";
        }
        str += "total score = " + totalScores[0];

        return str;
    }

    public Tour clone() {
        List<Visit> clonedVisits = new LinkedList<>(visits);
        double[] clonedTotalScores = new double[totalScores.length];
        for (int i = 0; i < totalScores.length; i++)
            clonedTotalScores[i] = totalScores[i];

        return new Tour(clonedVisits, clonedTotalScores);
    }
}
