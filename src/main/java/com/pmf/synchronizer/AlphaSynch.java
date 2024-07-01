package com.pmf.synchronizer;

import com.pmf.network.Message;

public class AlphaSynch extends SafeSynch {
    public AlphaSynch(int id, int numProcesses, int[] neighbors) {
        super(id, numProcesses, neighbors);
    }

    public static AlphaSynch fromId(int id, int numProcesses) {
        int neighbors[] = new int[numProcesses];
        for (int i = 0; i < numProcesses; i++) {
            if (i < id) 
                neighbors[i] = i;
            if (i > id) 
                neighbors[i-1] = i;
        }
        return new AlphaSynch(id, numProcesses, neighbors);
    }

    @Override
    public synchronized void nextPulse() {
        while (acksNeeded != 0) myWait();
        
        sendToNeighbors("safe", "0");
        while (!neighboursSafe()) myWait();
        startPulse();
        while (!nextPulseMsgs.isEmpty()) {
            Message m = nextPulseMsgs.removeFirst();
            prog.handleMessage(m);
        }
        notifyAll();
    }
}
