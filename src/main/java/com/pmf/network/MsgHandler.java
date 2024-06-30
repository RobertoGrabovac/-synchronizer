package com.pmf.network;

import java.io.IOException;

public interface MsgHandler {
    public void handleMsg(Message m, int srcsId, String tag);
    public Message receiveMsg(int fromId) throws IOException;
}