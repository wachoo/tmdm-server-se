package com.amalto.webapp.core.gwt;

import com.google.gwt.user.server.rpc.RPCRequest;


public class GWTRPCRequest {

    private final String serviceIntfName;

    private final RPCRequest rpcRequest;

    public GWTRPCRequest(String serviceIntfName, RPCRequest rpcRequest) {
        this.serviceIntfName = serviceIntfName;
        this.rpcRequest = rpcRequest;
    }

    public String getServiceIntfName() {
        return serviceIntfName;
    }

    public RPCRequest getRpcRequest() {
        return rpcRequest;
    }
}
