package com.pmf.synchronizer;

import java.util.HashSet;
import java.util.Set;

import com.pmf.network.Message;

public class BetaSynch extends SafeSynch {
    private final Set<Integer> children = new HashSet<>();
    private final int parent;
    
    public BetaSynch(int id, int numProcesses, int[] neighbors, int[] children, int parent) {
        super(id, numProcesses, neighbors);
        for (int child : children) {
            this.children.add(child);
        }
        // -1 if root of a tree
        this.parent = parent;
    }

    @Override
    public synchronized void nextPulse() {
        while (acksNeeded != 0) myWait();
        while (!allChildrenSafe()) myWait();
        // If node is root, this will be ignored
        sendToParent("safe", "0");
        // If node is root, this will immediately be true
        while (!isParentSafe()) myWait();
        sendToChildren("safe", "0");
        startPulse();
        while (!nextPulseMsgs.isEmpty()) {
            Message m = nextPulseMsgs.removeFirst();
            prog.handleMessage(m);
        }
        notifyAll();
    }

    private boolean allChildrenSafe() {
        for (int child : children) {
            boolean isUnsafe = unsafe.get(child);
            if (isUnsafe) return false;
        }
        return true;
    }

    private void sendToChildren(String tag, String msg) {
        for (int childId : children) {
            sendMessage(childId, tag, msg);
        }
    }

    private void sendToParent(String tag, String msg) {
        if (parent != -1)
            sendMessage(parent, tag, msg);
    }
    
    private boolean isParentSafe() {
        if (parent == -1) return true;
        boolean isUnsafe = unsafe.get(parent);
        return !isUnsafe;
    }
}
