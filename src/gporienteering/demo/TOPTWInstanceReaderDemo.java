package gporienteering.demo;

import gporienteering.core.PlaceOfInterest;
import gporienteering.core.TDOPInstance;
import gporienteering.core.TOPTWInstance;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;

public class TOPTWInstanceReaderDemo {

    public static void main(String[] args) {
        File file = new File("data/toptw/c_r_rc_200_100/r204.txt");

        TOPTWInstance instance = new TOPTWInstance();
        instance.readFromFile(file);

        double totalScore = 0;
        for (PlaceOfInterest poi : instance.getPlaceOfInterests()) {
            totalScore += poi.getScore(0);
        }

        System.out.println(totalScore);

//        for (Pair<PlaceOfInterest, PlaceOfInterest> key : instance.getDistMap().keySet()) {
//            if (key.getLeft().equals(instance.getStartPOI()))
//            System.out.println(key.getLeft().toString() + " -> " + key.getRight().toString() + ": dist = " + instance.getDistance(key.getLeft(), key.getRight()));
//        }

        System.out.println("finished");
    }
}
