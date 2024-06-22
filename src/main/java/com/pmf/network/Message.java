package com.pmf.network;

public class Message implements Comparable<Message> {
    private int timestamp;
    private String content;
    private String target;

    public Message(int timestamp, String content, String target) {
        this.timestamp = timestamp;
        this.content = content;
        this.target = target;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public int compareTo(Message other) {
        return Integer.compare(this.timestamp, other.timestamp);
    }
}
