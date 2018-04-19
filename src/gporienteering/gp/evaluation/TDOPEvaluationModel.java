package gporienteering.gp.evaluation;

import ec.EvolutionState;
import ec.util.Parameter;
import gporienteering.core.Instance;
import gporienteering.core.TDOPInstance;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class TDOPEvaluationModel extends EvaluationModel {

    public static final String P_DATASET = "dataset";
    public static final String P_FILE_ID = "file-id"; // the file id

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
            int dataset = state.parameters.getIntWithDefault(p, null, 0);
            p = b.push(P_FILE_ID);
            String fileId = state.parameters.getStringWithDefault(p, null, null);
            // the number of samples for this instance
            p = b.push(P_SAMPLES);
            int samples = state.parameters.getIntWithDefault(p, null, 1);
            // the travel time uncertainty level
            p = b.push(P_TIME_ULEVEL);
            double timeULevel = state.parameters.getDoubleWithDefault(p, null, 0);
            // the visit duration uncertainty level
            p = b.push(P_DURATION_ULEVEL);
            double durationULevel = state.parameters.getDoubleWithDefault(p, null, 0);

            File speedFile = new File("data/tdop/speedmatrix.txt");
            File categoryFile = new File("data/tdop/dataset " + dataset + "/arc_cat_" + dataset + ".txt");
            File file = new File("data/tdop/dataset " + dataset + "/OP_instances/p" + dataset + ".1." + fileId + ".txt");

            TDOPInstance baseInstance = new TDOPInstance();
            baseInstance.readFromVerbeeckFile(file);
            baseInstance.readSpeedMatrixFromFile(speedFile);
            baseInstance.readArcCategoryFromFile(categoryFile);

            for (int s = 0; s < samples; s++) {
                Instance instance = baseInstance.clone();
                instance.setTimeULevel(timeULevel);
                instance.setDurationULevel(durationULevel);
                instance.setName(file.getName());
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
