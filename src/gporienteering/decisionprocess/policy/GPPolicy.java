package gporienteering.decisionprocess.policy;

import ec.gp.GPTree;
import gporienteering.core.PlaceOfInterest;
import gporienteering.decisionprocess.DecisionProcessState;
import gporienteering.decisionprocess.Policy;
import gporienteering.decisionprocess.PoolFilter;
import gporienteering.decisionprocess.poolfilter.IdentityPoolFilter;
import gporienteering.gp.CalcPriorityProblem;
import gputils.DoubleData;

/**
 * A GP-evolved policy.
 *
 */
public class GPPolicy extends Policy {

    private GPTree gpTree;

    public GPPolicy(PoolFilter poolFilter, GPTree gpTree) {
        super(poolFilter);
        name = "\"GPPolicy\"";
        this.gpTree = gpTree;
    }

    public GPPolicy(GPTree gpTree) {
        this(new IdentityPoolFilter(), gpTree);
    }

    public GPTree getGPTree() {
        return gpTree;
    }

    public void setGPTree(GPTree gpTree) {
        this.gpTree = gpTree;
    }

    @Override
    public double priority(PlaceOfInterest candidate, DecisionProcessState state) {
        CalcPriorityProblem calcPrioProb =
                new CalcPriorityProblem(candidate, state);

        DoubleData tmp = new DoubleData();
        gpTree.child.eval(null, 0, tmp, null, null, calcPrioProb);

        return tmp.value;
    }
}
