package com.pmf.synchronizer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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

    public static BetaSynch fromId(int id, int numProcesses, int children) {
        // children - fiksiran broj djece po roditelju
        // Moze komunicirati sa svima u klasteru, ali jedino djeci i roditelju salje poruku safe
        int neighbors[] = new int[numProcesses - 1];
        for (int i = 0; i < numProcesses; i++) {
            if (i < id) 
                neighbors[i] = i;
            if (i > id) 
                neighbors[i-1] = i;
        }
        int parent = (id-1) / children;
        if (id == 0)
            parent = -1;
        int childrenArray[] = getChildrenArray(id, numProcesses, children);
        return new BetaSynch(id, numProcesses, neighbors, childrenArray, parent);
    }

    protected static int[] getChildrenArray(int id, int numProcesses, int children) {
        List<Integer> childrenList = new LinkedList<>();
        for (int i = 0; i < children; i++) {
            int childId = id * children + i + 1;
            if (childId >= numProcesses)
                break;
            childrenList.add(childId);
        }
        int[] childrenArray = childrenList.stream().mapToInt(Integer::intValue).toArray();
        return childrenArray;
    }

    public static BetaSynch fromId(int id, int numProcesses) {
        return BetaSynch.fromId(id, numProcesses, 2);
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

    protected boolean allChildrenSafe() {
        return allSafe(children);
    }

    protected void sendToChildren(String tag, String msg) {
        sendTo(children, tag, msg);
    }

    protected void sendToParent(String tag, String msg) {
        if (parent != -1)
            sendMessage(parent, tag, msg);
    }
    
    protected boolean isParentSafe() {
        if (parent == -1) return true;
        boolean isUnsafe = unsafe.get(parent);
        return !isUnsafe;
    }
}
