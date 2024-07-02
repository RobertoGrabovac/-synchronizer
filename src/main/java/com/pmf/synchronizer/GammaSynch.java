package com.pmf.synchronizer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.pmf.network.Message;

public class GammaSynch extends BetaSynch {
    private final Set<Integer> clusterNeighbours = new HashSet<>();
    private final Set<Integer> notClusterNeighbours = new HashSet<>();

    public GammaSynch(int id, int numProcesses, int[] neighbors,
            int[] clusterNeighbours, int[] children, int parent) {
        super(id, numProcesses, neighbors, children, parent);
        for (int child : clusterNeighbours) {
            this.clusterNeighbours.add(child);
        }
        notClusterNeighbours.addAll(unsafe.keySet());
        notClusterNeighbours.removeAll(this.clusterNeighbours);
    }

    public static GammaSynch fromId(int id, int numProcesses, int clusterSize) {
        int inClusterId = id % clusterSize;
        boolean isRoot = inClusterId == 0;
        int thisClusterRoot = id - inClusterId;
        int thisClusterSize = numProcesses - thisClusterRoot > clusterSize 
            ? clusterSize : numProcesses - thisClusterRoot;
        int neighbours[], notClusterNeighbours[], children[], parent;

        // Wether it is root or not, add all nodes inside the tree to 
        // cluster neighbours
        int clusterNeighbours[] = new int[thisClusterSize];
        for (int i = 0; i < thisClusterSize; i++) {
            if (i < inClusterId)
                clusterNeighbours[i] = i + thisClusterRoot;
            if (i > inClusterId)
                clusterNeighbours[i-1] = i + thisClusterRoot;
        }

        if (isRoot) {
            // If root, add other roots to not cluster neighbours
            int numberOfRoots = (numProcesses + clusterSize - 1) / clusterSize;
            notClusterNeighbours = new int[numberOfRoots - 1];
            for (int i = 0; i < numberOfRoots; i ++) {
                if (i * clusterSize < id)
                    notClusterNeighbours[i] = i * clusterSize;
                if (i * clusterSize > id)
                    notClusterNeighbours[i-1] = i * clusterSize;
            }
            parent = -1;
        } else {
            // If not root, there are no not cluster neighbours
            parent = thisClusterRoot + (inClusterId-1) / 2;
            notClusterNeighbours = new int[0];
        }

        // Calculate children
        children = getChildrenArray(inClusterId, thisClusterSize, 2);
        for (int child = 0; child < children.length; child++) {
            children[child] += id - id % clusterSize;
        }

        // Neighbours are all nodes inside the tree, and all other roots
        // if this is also the root
        // bilo bi lakse da su odmah liste ili setovi umjesto arraysa
        neighbours = Arrays.copyOf(
            notClusterNeighbours,
            notClusterNeighbours.length + clusterNeighbours.length);
        System.arraycopy(
            clusterNeighbours, 0, neighbours, 
            notClusterNeighbours.length, clusterNeighbours.length);

        return new GammaSynch(id, numProcesses, neighbours, 
            clusterNeighbours, children, parent);
    }

    @Override
    public synchronized void nextPulse() {
        while (acksNeeded != 0) myWait();
        // Alpha synchronizer
        sendToNotClusterNeighbours("safe", "0");
        while (!allNotClusterNeighboursSafe()) myWait();

        // Beta synchronizer
        while (!allChildrenSafe()) myWait();
        sendToParent("safe", "0");
        while (!isParentSafe()) myWait();
        sendToChildren("safe", "0");

        // New pulse
        startPulse();
        while (!nextPulseMsgs.isEmpty()) {
            Message m = nextPulseMsgs.removeFirst();
            prog.handleMessage(m);
        }
        notifyAll();
    }

    public void sendToNotClusterNeighbours(String tag, String msg) {
        sendTo(notClusterNeighbours, tag, msg);
    }

    public boolean allNotClusterNeighboursSafe() {
        return allSafe(notClusterNeighbours);
    }
}