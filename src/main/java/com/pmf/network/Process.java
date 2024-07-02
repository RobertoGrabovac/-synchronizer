package com.pmf.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public abstract class Process {
    protected int id;
    protected int numProcesses;
    protected Map<Integer, Socket> sockets = new HashMap<>();
    protected ServerSocket serverSocket;

    public Process(int id, int numProcesses) {
        this.id = id;
        this.numProcesses = numProcesses;
        int port = getPort(id);

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Process " + id + " listening on port " + port);
        } catch (IOException e) {
            // e.printStackTrace();
        }

        new Thread(this::acceptConnections).start();
    }

    private void acceptConnections() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleConnection(socket)).start();
            } catch (IOException e) {
                // e.printStackTrace();
            }
        }
    }

    private void handleConnection(Socket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message;
            while ((message = in.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(message);
                handleMessage(Message.parseMsg(st));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void connectTo(int destId) {
        int destPort = getPort(destId);
        try {
            Socket socket = new Socket(Symbols.ServerHost, destPort);
            sockets.put(destId, socket);
            // System.out.println("Process " + id + " connected to process " + destId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void onSendMessage(Message message) {};

    public void sendMessage(Message message) {
        onSendMessage(message);
        int destId = message.getDestId();
        Socket socket = sockets.get(destId);
        if (socket == null) {
            connectTo(destId);
            socket = sockets.get(destId);
        }
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(message.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(int destId, String tag, String msg) {
        sendMessage(new Message(id, destId, tag, msg));
    }

    protected abstract void handleMessage(Message message);

    public static int getPort(int index) {
        return Symbols.ServerPort + index;
    }
    
    protected void myWait() {
        try {
            // Thread.sleep(10);
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        notifyAll();
    }
}
