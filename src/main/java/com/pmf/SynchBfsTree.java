package com.pmf;

import com.pmf.network.Message;
import com.pmf.network.MsgHandler;
import com.pmf.synchronizer.SafeSynch;

public class SynchBfsTree implements MsgHandler {
    int parent = -1;
    int level; 
    int myId;
    SafeSynch synch; 
    boolean isRoot;
    int numProcesses;
 
    public SynchBfsTree(int myId, SafeSynch synch, boolean isRoot, int numProcesses) { 
        this.myId = myId;
        this.synch = synch;
        this.isRoot = isRoot; 
        this.numProcesses = numProcesses;
    }

    public void run() { 
        if (isRoot) { 
            parent = myId;
            level = 0; 
        } 
        synch.initialize(this);

        for (int pulse = 0; pulse < numProcesses; pulse++) { 
            if ((pulse == 0) && isRoot) { 
                synch.sendToNeighbors("invite", Integer.toString(level + 1));
            } 
            synch.nextPulse();
        } 
    } 

    @Override
    public void handleMessage(Message message) { 
        if (message.getTag().equals("invite")) { 
            if (parent == -1) { 
                parent = message.getSrcId(); 
                level = message.getMessageInt(); 
                System.out.println(myId + " is at level " + level);
                synch.sendToNeighbors("invite", Integer.toString(level + 1));
            } 
        } 
    } 
}