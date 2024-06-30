package com.pmf.synchronizer;


import com.pmf.network.Message;

// TODO: add parameter k which represents size of clusters inside of network
public class GammaSynch implements Synchronizer {
    private final AlphaSynch alphaSynch;
    private final BetaSynch betaSynch;

    // k je velicina klastera, i je index, a 
    public GammaSynchronizer(int i, int k) {
        // ako je korijen u beta klasteru, onda je ready kad je i alfa ready i kad je beta ready
        // this.alphaSynchronizer = new AlphaSynchronizer();
        // this.betaSynchronizer = new BetaSynchronizer();
    }

    @Override
    public void initialize() {

    }

    @Override
    public void sendMessage(Message message) {

    }

    @Override
    public void nextPulse() {

    }
}