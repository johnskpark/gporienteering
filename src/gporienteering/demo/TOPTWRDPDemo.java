package gporienteering.demo;

import ec.gp.GPTree;
import gporienteering.core.TDOPInstance;
import gporienteering.core.TOPTWInstance;
import gporienteering.decisionprocess.DecisionProcess;
import gporienteering.decisionprocess.Policy;
import gporienteering.decisionprocess.policy.GPPolicy;
import gporienteering.decisionprocess.policy.ScoreOverSlackPolicy;
import gporienteering.decisionprocess.policy.ScoreOverTimePolicy;
import gporienteering.decisionprocess.policy.ShortestTravelTimePolicy;
import gporienteering.decisionprocess.reactive.ReactiveDecisionProcess;
import gporienteering.gp.OrienteeringPrimitiveSet;
import gputils.LispUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import util.Timer;

import java.io.File;

/**
 * A demo for a reactive decision process.
 * First, an instances is read from a data file.
 * Created by gphhucarp on 29/08/17.
 */
public class TOPTWRDPDemo {

    public static void main(String[] args) {
        RandomDataGenerator rdg = new RandomDataGenerator();
        rdg.reSeed(6125);
        double uLevel = 0;

        File file = new File("data/toptw/c_r_rc_200_100/r204.txt");

        TOPTWInstance instance = new TOPTWInstance();
        instance.readFromFile(file);
        instance.setDurationULevel(uLevel);
        instance.setNumDays(3);

        instance.sample();

        // specify a policy
//        Policy policy = new NearestNeighbourPolicy();
//        Policy policy = new ScoreOverDistPolicy();
//        Policy policy = new ScoreOverTimePolicy();
//        Policy policy = new ScoreOverSlackPolicy();
//        Policy policy = new ShortestTravelTimePolicy();

        String expression = "(- (- (max (max (max SL TC) (max SCORE RemT)) TO) (+ (* (- RemT RemT) (/ TR DUR)) (+ (max TFV TFV) (- TR 0.263230234052514)))) (- (/ (+ (/ TA 0.6992252580811819) (min TSV TFV)) (/ (+ (min TO TO) (- TSV TFV)) 0.5795178640382137)) (/ (+ (+ 0.21109401733160438 0.531332941863405) (max TSV TA)) (/ (/ 0.0036845810798300516 DUR) (max TC TA)))))";

        expression = LispUtils.simplifyExpression(expression);
        System.out.println(expression);

        GPTree tree = LispUtils.parseExpression(expression, OrienteeringPrimitiveSet.wholePrimitiveSet());
        Policy policy = new GPPolicy(tree);

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
