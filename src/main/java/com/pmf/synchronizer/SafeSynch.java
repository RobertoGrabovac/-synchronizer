package com.pmf.synchronizer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.pmf.network.Message;
import com.pmf.network.MsgHandler;
import com.pmf.network.Process;

public abstract class SafeSynch extends Process implements Synchronizer {
    int pulse = -1;
    protected int acksNeeded = 0;
    protected Map<Integer, Boolean> unsafe = new HashMap<>(); // Neighbors status
    // protected HashSet<Integer> neighbours = new HashSet<>();
    protected LinkedList<Message> nextPulseMsgs = new LinkedList<>(); // Messages for next pulse
    protected boolean meSafe;
    MsgHandler prog;

    public SafeSynch(int id, int numProcesses, int[] neighbors) {
        super(id, numProcesses);
        for (int neighbor : neighbors) {
            unsafe.put(neighbor, true);
        }
    }

    @Override
    public synchronized void initialize(MsgHandler initProg) {
        prog = initProg;
        startPulse();
        notifyAll();
    }

    void startPulse() {
        for (Map.Entry<Integer, Boolean> entry : unsafe.entrySet()) {
            entry.setValue(true);
        }
        meSafe = false;
        pulse++;
        System.out.println("**** new pulse ****: " + pulse);
    }

    @Override
    public synchronized void handleMessage(Message message) {
        int src = message.getSrcId();
        String tag = message.getTag();

        while (pulse < 0) myWait();

        if (tag.equals("synchAck")) {
            acksNeeded--;
            if (acksNeeded == 0) notifyAll();
        } else if (tag.equals("safe")) {
            while (unsafe.get(src)) myWait();
            unsafe.put(src, false);
            if (neighboursSafe()) notifyAll();
        } else {
            sendMessage(src, "synchAck", "0");
            while (unsafe.get(src)) myWait();
            if (meSafe) nextPulseMsgs.add(message);
            else prog.handleMessage(message);
        }
    }

    @Override
    public void onSendMessage(Message message) {
        if (!unsafe.containsKey(message.getDestId())) {
            System.out.print("Sending message to not neighbour! Proccess" + id + " to " + message.getDestId());
        }

        String tag = message.getTag();
        if (tag.equals("synchAck") || tag.equals("safe"))
            return;
        acksNeeded++;
    }

    protected void sendTo(Iterable<Integer> dest, String tag, String msg) {
        for (int destId : dest) {
            sendMessage(destId, tag, msg);
        }
    }

    protected boolean allSafe(Iterable<Integer> dest) {
        for (Integer destId : dest) {
            boolean isUnsafe = unsafe.get(destId);
            if (isUnsafe) return false;
        }
        return true;
    }

    public void sendToNeighbors(String tag, String msg) {
        sendTo(unsafe.keySet(), tag, msg);
    }

    public boolean neighboursSafe() {
        return allSafe(unsafe.keySet());
    }
}
