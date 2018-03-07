package main;

import util.decision.PrecomputedMSTCollection;
import util.log.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;

public class PrecomputeLauncher {

    public static void main(String[] args) {

        String fileName = "precomputed-msts";
        int maxVertices;

        if (args.length < 1) {
            System.err.println("Please supply the number of trees to precompute!");
            return;
        }
        if (args.length > 1) {
            fileName = args[1];
        }

        try {
            maxVertices = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Illegal input!");
            return;
        }

        System.out.printf("Starting precomputation of decision trees for graphs with up to %s vertices.\n", maxVertices);
        Logger.setActive(true);

        try (ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(fileName)))) {

            PrecomputedMSTCollection msts = PrecomputedMSTCollection.computeUpTo(maxVertices);
            oos.writeObject(msts);

        } catch (IOException e) {
            System.err.println("Cannot write to file!");
        }

        System.out.printf("Finished precomputation of decision trees for graphs with up to %s vertices.\n", maxVertices);
    }
}
