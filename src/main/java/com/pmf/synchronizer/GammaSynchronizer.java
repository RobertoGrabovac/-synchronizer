package com.pmf.synchronizer;


import com.pmf.network.Message;

// TODO: add parameter k which represents size of clusters inside of network
public class GammaSynchronizer implements Synchronizer {
    private final AlphaSynchronizer alphaSynchronizer;
    private final BetaSynchronizer betaSynchronizer;

    public GammaSynchronizer() {
        this.alphaSynchronizer = new AlphaSynchronizer();
        this.betaSynchronizer = new BetaSynchronizer();
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