package gporienteering.core;

import gporienteering.decisionprocess.Policy;
import gporienteering.decisionprocess.policy.ScoreOverDistPolicy;

import java.util.HashMap;
import java.util.Map;

/**
 * An enumeration for all the possible objectives.
 *
 * Created by gphhucarp on 31/08/17.
 */
public enum Objective {

    TOTAL_SCORE("total-score"),
    TOTAL_SCORE_2("total-score-2");

    private final String name;

    Objective(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Reverse-lookup map
    private static final Map<String, Objective> lookup = new HashMap<>();

    static {
        for (Objective a : Objective.values()) {
            lookup.put(a.getName(), a);
        }
    }

    public static Objective get(String name) {
        return lookup.get(name);
    }

    /**
     * The reference reactive routing policy to calculate the reference objective values.
     * @return the reference reactive routing policy.
     */
    public static Policy refReactiveRoutingPolicy() {
        return new ScoreOverDistPolicy();
    }
}
