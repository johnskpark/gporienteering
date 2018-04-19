package gporienteering.gp;

import gporienteering.gp.terminal.feature.*;
import gputils.function.*;
import gputils.terminal.PrimitiveSet;

/**
 * The primitive set for orienteering.
 */

public class OrienteeringPrimitiveSet extends PrimitiveSet {
    /**
     * Return the basic terminals.
     * @return the basic terminal set.
     */
    public static OrienteeringPrimitiveSet basicTerminalSet() {
        OrienteeringPrimitiveSet terminalSet = new OrienteeringPrimitiveSet();

//        terminalSet.add(new DistanceFromHere());
//        terminalSet.add(new DistanceToEnd());
        terminalSet.add(new TimeToOpen());
        terminalSet.add(new TimeToClose());
        terminalSet.add(new TimeToArrive());
        terminalSet.add(new TimeToReturn());
        terminalSet.add(new TimeToStartVisit());
        terminalSet.add(new TimeToFinishVisit());
        terminalSet.add(new Duration());
        terminalSet.add(new Slack());
        terminalSet.add(new Score());
        terminalSet.add(new RemainingTime());
//        terminalSet.add(new RemainingNumPOIs());
//        terminalSet.add(new RemainingScore());

        return terminalSet;
    }

    /**
     * Return the extended terminals including the look-ahead terminals.
     * @return the extended terminal set.
     */
    public static OrienteeringPrimitiveSet extendedTerminalSet() {
        OrienteeringPrimitiveSet terminalSet = OrienteeringPrimitiveSet.basicTerminalSet();

        terminalSet.add(new MaxNextScore());
        terminalSet.add(new AvgNextScore());

        return terminalSet;
    }

    /**
     * The whole terminal set including all the possible terminals.
     * It is the extended terminal set in this case.
     * @return the whole terminal set.
     */
    public static OrienteeringPrimitiveSet wholeTerminalSet() {
        return extendedTerminalSet();
    }

    /**
     * The whole primitive set includes the whole terminal set
     * and all the function nodes.
     * @return the whole primitive set.
     */
    public static OrienteeringPrimitiveSet wholePrimitiveSet() {
        OrienteeringPrimitiveSet primitiveSet = wholeTerminalSet();

        primitiveSet.add(new Add());
        primitiveSet.add(new Sub());
        primitiveSet.add(new Mul());
        primitiveSet.add(new Div());
        primitiveSet.add(new Max());
        primitiveSet.add(new Min());
        primitiveSet.add(new If());

        return primitiveSet;
    }

}
