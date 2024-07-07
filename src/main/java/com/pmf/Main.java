package com.pmf;

import com.pmf.synchronizer.GammaSynch;

public class Main {
    public static void main(String[] args) {
        int numProcesses = 9;
        int clusterSize = 3;
        int rootId = 4;

        // Pokrenuti s:
        // java -XX:+ShowCodeDetailsInExceptionMessages -cp C:\Users\Rango\Projects\Gamma-Synchronizer\target\classes com.pmf.Main <broj_procesa>
        for (String i : args) {
            System.out.println("arg " + i);
        }
        
        
        int id = Integer.parseInt(args[0]);
        if (args.length >= 1) {
            numProcesses = Integer.parseInt(args[1]);
        }
        if (args.length >= 2) {
            rootId = Integer.parseInt(args[2]);
        }
        if (args.length >= 3) {
            clusterSize = Integer.parseInt(args[3]);
        }
        
        GammaSynch synch = GammaSynch.fromId(id, numProcesses, clusterSize);
        SynchBfsTree bfs = new SynchBfsTree(id, synch, id == rootId, numProcesses);

        System.out.println("Process: " + id+ "/" + numProcesses + " [" + clusterSize + "] root " + rootId);
        bfs.run();

        System.out.println("Process: " + id + " finished running");
        System.out.println("Parent: " + bfs.parent);
        System.out.println("Level: " + bfs.level);
    }
}