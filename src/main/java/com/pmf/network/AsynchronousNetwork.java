package com.pmf.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AsynchronousNetwork {
    private static final List<Message> networkBuffer = new ArrayList<>();
    private static final Random random = new Random();

    public static void send(Message message) {
        // Simulate network delay
        int delay = random.nextInt(1000);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        networkBuffer.add(message);
    }

    public static List<Message> receive() {
        // Simulate out-of-order message delivery
        Collections.shuffle(networkBuffer);
        List<Message> messages = new ArrayList<>(networkBuffer);
        networkBuffer.clear();
        return messages;
    }
}