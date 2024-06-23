package com.pmf.network;

public record Message(int src, int dest, String tag, int content){
}
