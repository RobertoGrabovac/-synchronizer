package com.pmf.synchronizer;

import com.pmf.network.Message;
import com.pmf.network.MsgHandler;

public interface Synchronizer {
    void initialize(MsgHandler initProg);
    void sendMessage(Message message);
    void nextPulse();
}
