package com.pmf.synchronizer;

import com.pmf.network.Message;

public class GammaSynchronizer {
    private final AlphaSynchronizer alphaSynchronizer;
    private final BetaSynchronizer betaSynchronizer;

    public GammaSynchronizer() {
        this.alphaSynchronizer = new AlphaSynchronizer();
        this.betaSynchronizer = new BetaSynchronizer();
    }

    public void sendGlobalMessage(String content, String targetCluster) {
        alphaSynchronizer.sendGlobalMessage(content, targetCluster);
    }

    public void sendLocalMessage(String content, String targetNode) {
        betaSynchronizer.sendLocalMessage(content, targetNode);
    }

    public void receiveGlobalMessage(Message message) {
        alphaSynchronizer.receiveGlobalMessage(message);
    }

    public void receiveLocalMessage(Message message) {
        betaSynchronizer.receiveLocalMessage(message);
    }

    public void processMessages() {
        alphaSynchronizer.processGlobalMessages();
        betaSynchronizer.processLocalMessages();
    }
}
