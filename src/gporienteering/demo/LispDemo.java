package gporienteering.demo;

import ec.gp.GPTree;
import gporienteering.gp.OrienteeringPrimitiveSet;
import gputils.LispUtils;

public class LispDemo {

    public static void main(String[] args) {
        String expression =
                "(- (max (- (max (/ SCORE TSV) (+ TO (- DUR (min SCORE 0.8617936491014373)))) (* (max ANS 0.4079763711957476) (/ SCORE TSV))) (min (max (+ ANS (- SCORE TFV)) (/ (- TSV SCORE) (/ (+ TFV 0.17031395543619865) (max ANS TC)))) (min 1 ANS))) (* (max ANS 0.4079763711957476) (/ SCORE TSV)))";
        expression = LispUtils.simplifyExpression(expression);

        GPTree gpTree = LispUtils.parseExpression(expression, OrienteeringPrimitiveSet.wholePrimitiveSet());
        System.out.println(gpTree.child.makeGraphvizTree());
    }
}
