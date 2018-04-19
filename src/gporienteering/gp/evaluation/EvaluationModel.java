package gporienteering.gp.evaluation;

import ec.EvolutionState;
import ec.Fitness;
import ec.multiobjective.MultiObjectiveFitness;
import ec.util.Parameter;
import gporienteering.core.Instance;
import gporienteering.core.Objective;
import gporienteering.core.TDOPInstance;
import gporienteering.core.Tour;
import gporienteering.decisionprocess.DecisionProcess;
import gporienteering.decisionprocess.Policy;
import gporienteering.decisionprocess.reactive.ReactiveDecisionProcess;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The evaluation model for evaluating individuals in GPHH.
 */

public abstract class EvaluationModel {
    public static final long SEED_GAP_INSTANCE = 935627; // seed gap between instances
    public static final long SEED_GAP_ROTATION = 6125; // seed gap for each rotation

    public static final String P_OBJECTIVES = "objectives";
    public static final String P_INSTANCES = "instances";
    public static final String P_SAMPLES = "samples"; // the number of samples
    public static final String P_DAYS = "days"; // the number of days
    public static final String P_TIME_ULEVEL = "time-ulevel"; // travel time uncertainty level
    public static final String P_DURATION_ULEVEL = "duration-ulevel"; // visit duration uncertainty level
    public static final String P_SEED = "seed"; // the seed for the first instance

    protected List<Objective> objectives;
    protected List<Instance> instances; // the instances used for evaluation
    protected List<DecisionProcess> evalDecisionProcesses; // decision processes for evaluating individuals
    protected List<DecisionProcess> refDecisionProcesses; // decision processes for calculating objective reference values
    protected Map<Pair<Integer, Objective>, Double> objRefValueMap;

    public List<Objective> getObjectives() {
        return objectives;
    }

    public List<Instance> getInstances() {
        return instances;
    }

    public List<DecisionProcess> getEvalDecisionProcesses() {
        return evalDecisionProcesses;
    }

    public List<DecisionProcess> getRefDecisionProcesses() {
        return refDecisionProcesses;
    }

    /**
     * Get the objective reference value of a particular decision process and an objective.
     * @param index the index of the decision process.
     * @param objective the objective.
     * @return the corresponding objective reference value.
     */
    public double getObjRefValue(int index, Objective objective) {
        return objRefValueMap.get(Pair.of(index, objective));
    }

    public void setup(final EvolutionState state, final Parameter base) {
        // get the objectives
        Parameter p = base.push(P_OBJECTIVES);
        int numObjectives = state.parameters.getIntWithDefault(p, null, 0);

        if (numObjectives == 0) {
            System.err.println("ERROR:");
            System.err.println("No objective is specified.");
            System.exit(1);
        }

        objectives = new ArrayList<>();
        for (int i = 0; i < numObjectives; i++) {
            p = base.push(P_OBJECTIVES).push("" + i);
            String objectiveName = state.parameters.getStringWithDefault(p, null, "");
            Objective objective = Objective.get(objectiveName);

            objectives.add(objective);
        }
    }

    /**
     * Initialise the seeds of the instances.
     * The first instance has the initSed, and the seed for next instances
     * will be incremented by SEED_GAP_INSTANCE.
     * @param initSeed the initial seed for the first instance.
     */
    public void initSeeds(long initSeed) {
        long seed = initSeed;
        for (Instance instance : instances) {
            instance.setSeed(seed);
            // increment the seed across instances
            seed += SEED_GAP_INSTANCE;
        }

        initRefDecisionProcesses();
        initEvalDecisionProcesses();
    }

    /**
     * Rotate the seeds of the instances.
     * For each instance, the seed is incremented by SEED_GAP_ROTATION.
     */
    public void rotateSeeds() {
        for (Instance instance : instances) {
            long seed = instance.getSeed();
            instance.setSeed(seed + SEED_GAP_ROTATION);
        }

        rotateRefDecisionProcesses();
    }

    /**
     * Initialise the reference decision processes by the instances.
     * Calculated the objective reference values from the decision proceses.
     */
    public void initRefDecisionProcesses() {
        refDecisionProcesses.clear();
        objRefValueMap.clear();

        for (int i = 0; i < instances.size(); i++) {
            // create a new reactive decision process from the intance.
            Instance instance = instances.get(i);
            DecisionProcess dp =
                    DecisionProcess.initReactive(instance, Objective.refReactiveRoutingPolicy());

            refDecisionProcesses.add(dp);

            // get the reference objective values by applying the reference policy.
            dp.run();
            Tour tour = dp.getState().getTour();
            for (Objective objective : objectives) {
                double objValue = tour.objValue(objective);
                objRefValueMap.put(Pair.of(i, objective), objValue);
            }
            dp.reset();
        }
    }

    /**
     * Rotated the reference decision processes after the seeds of the instances are rotated.
     *
     */
    public void rotateRefDecisionProcesses() {
        objRefValueMap.clear();

        for (int i = 0; i < refDecisionProcesses.size(); i++) {
            DecisionProcess dp = refDecisionProcesses.get(i);

            // get the objective reference values.
            dp.run();
            Tour tour = dp.getState().getTour();
            for (Objective objective : objectives) {
                double objValue = tour.objValue(objective);
                objRefValueMap.put(Pair.of(i, objective), objValue);
            }
            dp.reset();
        }
    }

    /**
     * Evaluate an individual (a policy) using this evaluation model.
     * @param policy the policy to be evaluated.
     * @param fitness the fitness of the individual.
     * @param state the evolution state.
     */
    public void evaluate(Policy policy, Fitness fitness, EvolutionState state) {
        double[] fitnesses = new double[objectives.size()];

        for (int i = 0; i < evalDecisionProcesses.size(); i++) {
            DecisionProcess dp = evalDecisionProcesses.get(i);
            dp.setPolicy(policy);

            dp.run();
            Tour tour = dp.getState().getTour();
            for (int j = 0; j < fitnesses.length; j++) {
                Objective objective = objectives.get(j);
                double normObjValue = - tour.objValue(objective); // / getObjRefValue(i, objective);
                fitnesses[j] += normObjValue;
            }
            dp.reset();
        }

        for (int j = 0; j < fitnesses.length; j++) {
            fitnesses[j] /= evalDecisionProcesses.size();
        }

        MultiObjectiveFitness f = (MultiObjectiveFitness)fitness;
        f.setObjectives(state, fitnesses);
    }

    /**
     * Evaluate an individual (a policy) using this evaluation model.
     * The fitness is original --- without normalisation.
     * @param policy the policy to be evaluated.
     * @param fitness the fitness of the individual.
     * @param state the evolution state.
     */
    public void evaluateOriginal(Policy policy, Fitness fitness, EvolutionState state) {
        double[] fitnesses = new double[objectives.size()];

        for (int i = 0; i < evalDecisionProcesses.size(); i++) {
            DecisionProcess dp = evalDecisionProcesses.get(i);
            dp.setPolicy(policy);

            dp.run();
            Tour tour = dp.getState().getTour();
            for (int j = 0; j < fitnesses.length; j++) {
                Objective objective = objectives.get(j);
                double objValue = - tour.objValue(objective);
                fitnesses[j] += objValue;
            }
            dp.reset();
        }

        for (int j = 0; j < fitnesses.length; j++) {
            fitnesses[j] /= evalDecisionProcesses.size();
        }

        MultiObjectiveFitness f = (MultiObjectiveFitness)fitness;
        f.setObjectives(state, fitnesses);
    }

    /**
     * Initialise the evaluation decision processes from the instances.
     */
    public void initEvalDecisionProcesses() {
        for (int i = 0; i < instances.size(); i++) {
            Instance instance = instances.get(i);
            ReactiveDecisionProcess rdp =
                    DecisionProcess.initReactive(instance, Objective.refReactiveRoutingPolicy());

            evalDecisionProcesses.add(rdp);
        }
    }
}
