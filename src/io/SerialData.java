package io;

import java.util.concurrent.ConcurrentLinkedQueue;

public final class SerialData {
    
    byte[] buffer;
    ConcurrentLinkedQueue<Byte> queue = new ConcurrentLinkedQueue<>();
    public int 
    	a, b, bytesReceived, bytesSent;
}