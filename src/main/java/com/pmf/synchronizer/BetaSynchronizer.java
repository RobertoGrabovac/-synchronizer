package com.pmf.synchronizer;

import com.pmf.network.AsynchronousNetwork;
import com.pmf.network.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BetaSynchronizer {
    private int localClock;
    private final List<Message> localMessageBuffer;

    public BetaSynchronizer() {
        this.localClock = 0;
        this.localMessageBuffer = new ArrayList<>();
    }

    public void sendLocalMessage(String content, String targetNode) {
        localClock++;
        Message message = new Message(localClock, content, targetNode);
        AsynchronousNetwork.send(message);
    }

    public void receiveLocalMessage(Message message) {
        localMessageBuffer.add(message);
        Collections.sort(localMessageBuffer);
    }

    public void processLocalMessages() {
        while (!localMessageBuffer.isEmpty()) {
            Message message = localMessageBuffer.removeFirst();
            localClock = Math.max(localClock, message.getTimestamp());
            handleLocalMessage(message.getContent());
        }
    }

    private void handleLocalMessage(String content) {
        // Implement local message handling logic
        System.out.println("Handling local message: " + content);
    }
}

