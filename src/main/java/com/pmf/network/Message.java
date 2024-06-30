package com.pmf.network;

import java.util.StringTokenizer;

public class Message {
    int srcId, destId;
    String tag;
    String msg;

    public Message(int s, int t, String msgType, String buf) {
        this.srcId = s;
        destId = t;
        tag = msgType;
        msg = buf;
    }

    public int getSrcId() {
        return srcId;
    }

    public int getDestId() {
        return destId;
    }

    public String getTag() {
        return tag;
    }

    public String getMessage() {
        return msg;
    }

    public int getMessageInt() {
        StringTokenizer st = new StringTokenizer(msg);
        return Integer.parseInt(st.nextToken());
    }

    public static Message parseMsg(StringTokenizer st) {
        int srcId = Integer.parseInt(st.nextToken());
        int destId = Integer.parseInt(st.nextToken());
        String tag = st.nextToken();
        String msg = st.nextToken("#");
        return new Message(srcId, destId, tag, msg);
    }

    public String toString() {
        String s = String.valueOf(srcId) + " " +
                String.valueOf(destId) + " " +
                tag + " " + msg + "#";
        return s;
    }
}