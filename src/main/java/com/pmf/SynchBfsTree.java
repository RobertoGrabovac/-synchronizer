package com.pmf;

import com.pmf.network.AsynchronousNetwork;
import com.pmf.network.Message;
import com.pmf.synchronizer.Synchronizer;

import java.util.stream.IntStream;

// TODO: defining clusters logic
public class SynchBfsTree {
    int parent = -1;
    int level;
    int id;
    Synchronizer synchronizer;
    AsynchronousNetwork asynchronousNetwork;

    public SynchBfsTree(AsynchronousNetwork asynchronousNetwork, Synchronizer initS, int id) {
        this.asynchronousNetwork = asynchronousNetwork;
        this.synchronizer = initS;
        this.id = id;
    }

    public void initiate() {
        if (id == 0) {
            parent = 0; // node (process) with id = 0 is a root of BFS tree by definition
            level = 0;
        }

        synchronizer.initialize();

        int networkSize = asynchronousNetwork.size();
        IntStream.range(0, networkSize).forEach(pulse -> { //TODO: check if we need this line of code
            if (pulse == 0 && id == 0) {
                IntStream.range(0, networkSize)
                        .filter(i -> asynchronousNetwork.areNeighbours(id, i))
                        .forEach(i -> synchronizer.sendMessage(new Message(id, i, "invite", level + 1)));
            }
            synchronizer.nextPulse();
        });
    }

    public synchronized void handleMsg(Message message) {
        if (unvisitedNodeInvited(message)) {
            parent = message.src();
            level = message.content();
            System.out.println(id + " is at level " + level);

            IntStream.range(0, asynchronousNetwork.size())
                    .filter(i -> asynchronousNetwork.areNeighbours(id, i) && i != message.src())
                    .forEach(i -> synchronizer.sendMessage(new Message(id, i, "invite", level + 1)));
        }
    }

    private boolean unvisitedNodeInvited(Message message) {
        return "invite".equals(message.tag()) && parent == -1;
    }

}