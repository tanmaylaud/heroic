package com.spotify.heroic.metric.error;

public class BufferEnqueueException extends Exception {
    private static final long serialVersionUID = 745124664641286975L;

    public BufferEnqueueException(String message) {
        super(message);
    }
}