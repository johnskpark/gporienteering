package gporienteering.gp.evaluation;

import ec.EvolutionState;
import ec.util.Parameter;
import gporienteering.core.Instance;
import gporienteering.core.MOTOPTWInstance;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

// TODO modify this to work with the multiobjective optimisation.
public class MOTOPTWEvaluationModel extends EvaluationModel {

    public static final String P_DATASET = "dataset";
    public static final String P_FILE_NAME = "file-name";

    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);

        // setup the instances
        Parameter p = base.push(P_INSTANCES);
        int numInstances = state.parameters.getIntWithDefault(p, null, 0);

        if (numInstances == 0) {
            System.err.println("ERROR:");
            System.err.println("No instance is provided.");
            System.exit(1);
        }

        instances = new ArrayList<>();
        for (int i = 0; i < numInstances; i++) {
            Parameter b = base.push(P_INSTANCES).push("" + i);
            // the file of the instance
            p = b.push(P_DATASET);
            String dataset = state.parameters.getStringWithDefault(p, null, "");
            p = b.push(P_FILE_NAME);
            String filename = state.parameters.getStringWithDefault(p, null, null);
            // the number of samples for this instance
            p = b.push(P_SAMPLES);
            int samples = state.parameters.getIntWithDefault(p, null, 1);
            // the number of days
            p = b.push(P_DAYS);
            int numDays = state.parameters.getIntWithDefault(p, null, 1);
            // the travel time uncertainty level
            p = b.push(P_TIME_ULEVEL);
            double timeULevel = state.parameters.getDoubleWithDefault(p, null, 0);
            // the visit duration uncertainty level
            p = b.push(P_DURATION_ULEVEL);
            double durationULevel = state.parameters.getDoubleWithDefault(p, null, 0);

            File file = new File("data/motoptw/" + dataset + "/" + filename + ".txt");

            MOTOPTWInstance baseInstance = new MOTOPTWInstance();
            baseInstance.readFromFile(file);

            for (int s = 0; s < samples; s++) {
                Instance instance = baseInstance.clone();
                instance.setNumDays(numDays);
                instance.setTimeULevel(timeULevel);
                instance.setDurationULevel(durationULevel);
                instance.setName(filename);
                instances.add(instance);
            }
        }

        // the seed for the first instance
        p = base.push(P_SEED);
        long initSeed = state.parameters.getLongWithDefault(p, null, 0);

        evalDecisionProcesses = new ArrayList<>();
        refDecisionProcesses = new ArrayList<>();
        objRefValueMap = new HashMap<>();

        // set the seeds for the instances
        initSeeds(initSeed);
    }
}
