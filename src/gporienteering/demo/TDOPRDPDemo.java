package gporienteering.demo;

import gporienteering.core.TDOPInstance;
import gporienteering.decisionprocess.DecisionProcess;
import gporienteering.decisionprocess.Policy;
import gporienteering.decisionprocess.policy.ShortestTravelTimePolicy;
import gporienteering.decisionprocess.reactive.ReactiveDecisionProcess;
import org.apache.commons.math3.random.RandomDataGenerator;
import util.Timer;

import java.io.File;

/**
 * A demo for a reactive decision process.
 * First, an instances is read from a data file, e.g. data/gdb/gdb23.dat.
 * Then, given a routing policy
 * Created by gphhucarp on 29/08/17.
 */
public class TDOPRDPDemo {

    public static void main(String[] args) {
        RandomDataGenerator rdg = new RandomDataGenerator();
        rdg.reSeed(10);
        double uLevel = 0.05;

        File speedFile = new File("data/tdop/speedmatrix.txt");
        File categoryFile = new File("data/tdop/dataset 1/arc_cat_1.txt");
        File file = new File("data/tdop/dataset 1/OP_instances/p1.1.a.txt");

        TDOPInstance instance = new TDOPInstance();
        instance.readFromVerbeeckFile(file);
        instance.readSpeedMatrixFromFile(speedFile);
        instance.readArcCategoryFromFile(categoryFile);

        instance.sample();

        // specify a policy
//        Policy policy = new NearestNeighbourPolicy();
//        Policy policy = new ScoreOverDistPolicy();
//        Policy policy = new ScoreOverTimePolicy();
        Policy policy = new ShortestTravelTimePolicy();

        // initialise a reactive decision process
        ReactiveDecisionProcess rdp = DecisionProcess.initReactive(instance, policy);

        // run the decision process
        // these should give the same results
        long start = Timer.getCpuTime();
        rdp.run();
        long end = Timer.getCpuTime();
        double duration = (end - start) / 1000000;

        System.out.println(rdp.getState().getTour().toString());
        System.out.println("elapsed " + duration + " ms.");

        // rerun the decision process for a number of times.
        // the instance and routing policy do not change,
        // so all the reruns will give the same results.
        int maxReruns = 10;
        for (int rerun = 0; rerun < maxReruns; rerun++) {
            // before rerunning, need to reset the decision process
            rdp.reset();
            start = Timer.getCpuTime();
            rdp.run();
            end = Timer.getCpuTime();
            duration = (end - start) / 1000000;

            System.out.println(rdp.getState().getTour().toString());
            System.out.println("elapsed " + duration + " ms.");
        }
    }
}
