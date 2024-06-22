package com.pmf;

import com.pmf.synchronizer.GammaSynchronizer;

import java.util.HashMap;
import java.util.Map;

public class PaxosNode {
    private int nodeId;
    private GammaSynchronizer synchronizer;
    private int proposalNumber;
    private String proposedValue;
    private Map<Integer, String> promises;
    private Map<Integer, String> acceptedValues;

    public PaxosNode(int nodeId, GammaSynchronizer synchronizer) {
        this.nodeId = nodeId;
        this.synchronizer = synchronizer;
        this.proposalNumber = 0;
        this.promises = new HashMap<>();
        this.acceptedValues = new HashMap<>();
    }

    public void prepare(int proposalNumber) {
        this.proposalNumber = proposalNumber;
        synchronizer.sendGlobalMessage("PREPARE " + proposalNumber, "acceptors");
    }

    public void promise(int proposalNumber) {
        synchronizer.sendLocalMessage("PROMISE " + proposalNumber, "proposer");
    }

    public void accept(int proposalNumber, String value) {
        synchronizer.sendGlobalMessage("ACCEPT " + proposalNumber + " " + value, "acceptors");
    }

    public void accepted(int proposalNumber, String value) {
        synchronizer.sendLocalMessage("ACCEPTED " + proposalNumber + " " + value, "proposer");
    }

    public void handleMessage(String message) {
        String[] parts = message.split(" ");
        String type = parts[0];
        int proposalNumber = Integer.parseInt(parts[1]);

        switch (type) {
            case "PREPARE":
                if (proposalNumber > this.proposalNumber) {
                    this.proposalNumber = proposalNumber;
                    promise(proposalNumber);
                }
                break;
            case "PROMISE":
                promises.put(proposalNumber, null);
                if (promises.size() > 1) { // Majority check
                    accept(proposalNumber, proposedValue);
                }
                break;
            case "ACCEPT":
                String value = parts[2];
                acceptedValues.put(proposalNumber, value);
                accepted(proposalNumber, value);
                break;
            case "ACCEPTED":
                // Handle accepted message
                break;
        }
    }

    public void processMessages() {
        synchronizer.processMessages();
    }
}

