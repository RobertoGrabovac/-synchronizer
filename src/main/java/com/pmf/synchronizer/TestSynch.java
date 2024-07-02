package com.pmf.synchronizer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.pmf.network.Message;
import com.pmf.network.MsgHandler;
import com.pmf.network.Symbols;


public class TestSynch implements MsgHandler{
    private SafeSynch synchronizer;
    int pulse;

    public TestSynch(int id, int numProcesses, int clusterSize) {
        // synchronizer = AlphaSynch.fromId(id, numProcesses);
        // synchronizer = BetaSynch.fromId(id, numProcesses);
        synchronizer = GammaSynch.fromId(id, numProcesses, clusterSize);
    }

    @Override
    public void handleMessage(Message message) {
        System.out.println(message.toString());
    }

    private static class SynchTask implements Runnable {
        private final TestSynch myProcess;

        public SynchTask(TestSynch process) {
            myProcess = process;
        }

        @Override
        public void run() {
            myProcess.run();
        }
    }

    public void run() {
        synchronizer.initialize(this);
        synchronizer.nextPulse();

        for (int i = 0; i < 10; i++) {
            step();
        }
    }

    public void step() {
        pulse += 1;
        synchronizer.sendToNeighbors("step" + pulse, "poruka u " + pulse + " pulsu");
        try {
            Thread.sleep(Symbols.TickWait);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronizer.nextPulse();
    }

    public static void main(String[] args) {
        int numProcesses = 4;
        SynchTask[] processes = new SynchTask[numProcesses];
        for (int i = 0; i < numProcesses; i++) {
            processes[i] = new SynchTask(new TestSynch(i, numProcesses, 2));
        }

        ExecutorService executor = Executors.newCachedThreadPool();
        for (SynchTask process : processes)
            executor.execute(process);
    }
}
