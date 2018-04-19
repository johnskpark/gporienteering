package gporienteering.demo;

import gporienteering.core.PlaceOfInterest;
import gporienteering.core.TDOPInstance;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;

public class TDOPInstanceReaderDemo {

    public static void main(String[] args) {
        File speedFile = new File("data/tdop/speedmatrix.txt");
        File categoryFile = new File("data/tdop/dataset 1/arc_cat_1.txt");
        File file = new File("data/tdop/dataset 1/OP_instances/p1.1.a.txt");

        TDOPInstance instance = new TDOPInstance();
        instance.readFromVerbeeckFile(file);
        instance.readSpeedMatrixFromFile(speedFile);
        instance.readArcCategoryFromFile(categoryFile);

//        for (Pair<PlaceOfInterest, PlaceOfInterest> key : instance.getDistMap().keySet()) {
//            if (key.getLeft().equals(instance.getStartPOI()))
//            System.out.println(key.getLeft().toString() + " -> " + key.getRight().toString() + ": dist = " + instance.getDistance(key.getLeft(), key.getRight()));
//        }

        for (Pair<PlaceOfInterest, PlaceOfInterest> key : instance.getDistMap().keySet()) {
            System.out.println(key.getLeft().toString() + " -> " + key.getRight().toString() + ": cat = " + instance.getCategory(key.getLeft(), key.getRight()));
        }

        System.out.println("finished");
    }
}
