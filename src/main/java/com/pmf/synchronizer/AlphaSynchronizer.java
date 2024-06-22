package com.pmf.synchronizer;

import com.pmf.network.AsynchronousNetwork;
import com.pmf.network.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlphaSynchronizer {
    private int globalClock;
    private final List<Message> globalMessageBuffer;

    public AlphaSynchronizer() {
        this.globalClock = 0;
        this.globalMessageBuffer = new ArrayList<>();
    }

    public void sendGlobalMessage(String content, String targetCluster) {
        globalClock++;
        Message message = new Message(globalClock, content, targetCluster);
        AsynchronousNetwork.send(message);
    }

    public void receiveGlobalMessage(Message message) {
        globalMessageBuffer.add(message);
        Collections.sort(globalMessageBuffer);
    }

    public void processGlobalMessages() {
        while (!globalMessageBuffer.isEmpty()) {
            Message message = globalMessageBuffer.removeFirst();
            globalClock = Math.max(globalClock, message.getTimestamp());
            handleGlobalMessage(message.getContent());
        }
    }

    private void handleGlobalMessage(String content) {
        // Implement global message handling logic
        System.out.println("Handling global message: " + content);
    }
}

