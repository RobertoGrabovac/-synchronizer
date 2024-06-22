package com.pmf;

import com.pmf.network.AsynchronousNetwork;
import com.pmf.network.Message;
import com.pmf.synchronizer.GammaSynchronizer;

public class Main {
    public static void main(String[] args) {
        GammaSynchronizer synchronizer = new GammaSynchronizer();
        PaxosNode nodeA = new PaxosNode(1, synchronizer);
        PaxosNode nodeB = new PaxosNode(2, synchronizer);
        PaxosNode nodeC = new PaxosNode(3, synchronizer);

        // Proposer (Node A) initiates the prepare phase
        nodeA.prepare(1);

        // Proposer (Node B) initiates the prepare phase
        nodeB.prepare(2);

        // Simulate receiving messages
        for (Message message : AsynchronousNetwork.receive()) {
            if (message.getTarget().startsWith("Cluster")) {
                synchronizer.receiveGlobalMessage(message);
            } else {
                synchronizer.receiveLocalMessage(message);
            }
        }

        // Process messages
        nodeA.processMessages();
        nodeB.processMessages();
        nodeC.processMessages();
    }
}