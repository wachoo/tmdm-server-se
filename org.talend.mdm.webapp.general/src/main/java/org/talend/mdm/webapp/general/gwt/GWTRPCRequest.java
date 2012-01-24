// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.general.gwt;

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
