package com.pmf.network;


public class TestProcess extends Process {
    public TestProcess(int id, int numProcesses) {
        super(id, numProcesses);
    }

    @Override
    protected void handleMessage(Message message) {
        System.out.println("Process " + id + " received message: " + message.getMessage());
    }

    public static void main(String[] args) {
        int numProcesses = 3;
        TestProcess[] processes = new TestProcess[numProcesses];
        for (int i = 0; i < numProcesses; i++) {
            processes[i] = new TestProcess(i, numProcesses);
        }

        // Wait for processes to initialize
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Test sending messages
        processes[0].sendMessage(1, "test", "123");
        processes[1].sendMessage(2, "test", "456");
        processes[2].sendMessage(0, "test", "789");
    }
}
