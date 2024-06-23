package com.pmf.synchronizer;

import com.pmf.network.Message;

public interface Synchronizer {
    void initialize(); // TODO: find out parameters of this method
    void sendMessage(Message message);
    void nextPulse();
}
