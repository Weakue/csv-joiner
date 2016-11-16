package ru.griddynamics.puks;

import java.io.*;

import static com.google.code.externalsorting.ExternalSort.sort;
import static java.lang.Integer.parseInt;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.nanoTime;

/**
 * Created by Alexey Puks on 16.11.16.
 */
public class Joiner {
    private static File inputA = null;
    private static File inputB = null;
    private static File tmpA = null;
    private static File tmpB = null;
    private static File output = null;
    private static int count = 0;

    /**
     * Idea is to sort and then join sorted files,
     * its easier to implement, than some variants with binary indexes since
     * there is good open source implementation of external sort algorithm already
     */

    public static void main(String[] args) {
        init(args);
    }

    private static void init(String[] args) {
        long start = nanoTime();

        System.out.println("Trying to sort using " + getRuntime().freeMemory() / 1024 / 1024 + "MB");
        inputA = new File(args[0]);
        inputB = new File(args[1]);
        tmpA = new File(inputA.getAbsolutePath() + ".tmp");
        tmpB = new File(inputB.getAbsolutePath() + ".tmp");
        sortAndJoin();
        System.out.println("Completed in: " + (nanoTime() - start) / 1000000000.0 + " seconds");

    }

    private static void sortAndJoin() {
        // ExternalSort`s defaultcomparator uses intrinsic string comparator,
        // so fancy stuff like comparing only keys only increases GC pressure and considered as overkill
        long start = nanoTime();
        try {
            System.out.println("start sort " + inputA.getAbsolutePath());
            sort(inputA, tmpA);
            System.out.println("start sort " + inputB.getAbsolutePath());
            sort(inputB, tmpB);
        } catch (IOException e) {
            System.out.println("Error while sorting files");
        }
        System.out.println("Sorted in: " + (nanoTime() - start) / 1000000000.0 + " seconds");
        start = nanoTime();
        joinSorted(tmpA, tmpB);
        System.out.println("Joined in: " + (nanoTime() - start) / 1000000000.0 + " seconds");
    }

    private static void joinSorted(File a, File b) {
        System.out.println("start joining");
        try (BufferedReader brA = new BufferedReader(new FileReader(a));
             BufferedReader brB = new BufferedReader(new FileReader(b));
             Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream
                ("output.txt"), "utf-8"))) {

            String lineA;
            String lineB = brB.readLine();
            int keyA;
            int keyB;
            StringBuilder sb = new StringBuilder();

            while ((lineA = brA.readLine()) != null) {
                keyA = parseInt(lineA.split(",")[0]);
                while (lineB != null) {
                    keyB = parseInt(lineB.split(",")[0]);
                    if (keyA > keyB) {
                        lineB = brB.readLine();
                        continue;
                    }
                    if (keyA < keyB) {
                        lineA = brA.readLine();
                        if (lineA == null) break;
                        keyA = parseInt(lineA.split(",")[0]);
                        continue;
                    }
                    sb.setLength(0);
                    sb.append(lineB);
                    sb.append(",");
                    sb.append(lineA.split(",")[1]);
                    sb.append("\n");
                    writer.write(sb.toString());
                    lineB = brB.readLine();
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR occurred");
            e.printStackTrace();
            System.exit(-1);
        }

    }
}


