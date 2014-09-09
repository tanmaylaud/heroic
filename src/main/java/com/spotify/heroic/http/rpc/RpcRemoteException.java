package com.spotify.heroic.http.rpc;

import java.net.URI;

public class RpcRemoteException extends RpcNodeException {
    private static final long serialVersionUID = 4669248130340709248L;

    public RpcRemoteException(URI uri, String message) {
        super(uri, message);
    }
}
