package com.pmf.synchronizer;

import com.pmf.network.Message;

public class AlphaSynch extends SafeSynch {
    public AlphaSynch(int id, int numProcesses, int[] neighbors) {
        super(id, numProcesses, neighbors);
    }

    @Override
    public synchronized void nextPulse() {
        while (acksNeeded != 0) myWait();
        
        sendToNeighbors("safe", "0");
        while (!allSafe()) myWait();
        startPulse();
        while (!nextPulseMsgs.isEmpty()) {
            Message m = nextPulseMsgs.removeFirst();
            prog.handleMessage(m);
        }
        notifyAll();
    }
}
