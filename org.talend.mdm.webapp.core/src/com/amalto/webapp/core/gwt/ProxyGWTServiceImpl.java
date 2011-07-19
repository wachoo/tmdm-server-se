package com.amalto.webapp.core.gwt;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.SerializationPolicyLoader;

public class ProxyGWTServiceImpl extends RemoteServiceServlet {

    /**
     * 
     */
    private static final long serialVersionUID = -3043769203139170342L;

    Properties properties = new Properties();

    Map<String, Object> actions = new HashMap<String, Object>();

    // String actionsFile;

    @Override
    protected SerializationPolicy doGetSerializationPolicy(HttpServletRequest request, String moduleBaseURL, String strongName) {
        String modulePath = null;
        String hostAddress = null;
        SerializationPolicy serializationPolicy = null;
        try {
            URL url = new URL(moduleBaseURL);
            modulePath = url.getPath();

            hostAddress = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort(); //$NON-NLS-1$ //$NON-NLS-2$

            String serializationPolicyFilePath = SerializationPolicyLoader
                    .getSerializationPolicyFileName(modulePath + strongName);

            InputStream is = new URL(hostAddress + serializationPolicyFilePath).openStream();

            try {
                if (is != null) {
                    try {
                        serializationPolicy = SerializationPolicyLoader.loadFromStream(is, null);
                    } catch (ParseException e) {
                        this.log("ERROR: Failed to parse the policy file '" + serializationPolicyFilePath + "'", e); //$NON-NLS-1$ //$NON-NLS-2$
                    } catch (IOException e) {
                        this.log("ERROR: Could not read the policy file '" + serializationPolicyFilePath + "'", e); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                } else {
                    String message = "ERROR: The serialization policy file '" + serializationPolicyFilePath //$NON-NLS-1$
                            + "' was not found; did you forget to include it in this deployment?"; //$NON-NLS-1$
                    this.log(message);
                }
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        // Ignore this error
                    }
                }
            }

        } catch (MalformedURLException ex) {
            this.log("Malformed moduleBaseURL: " + moduleBaseURL, ex); //$NON-NLS-1$
        } catch (IOException ex) {
            // Ignore this error
        }
        return serializationPolicy;
    }


    private void loadActions() {
        InputStream in = null;
        try {
            in = this.getServletContext().getResourceAsStream("/WEB-INF/actions.properties"); //$NON-NLS-1$
            Properties properties = new Properties();
            properties.load(in);
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<Object> services = properties.keys();
            while (services.hasMoreElements()) {
                String service = (String) services.nextElement();
                Object action = Class.forName(properties.getProperty(service), false, classLoader).newInstance();
                actions.put(service, action);
            }
        } catch (Exception e) {
            // Ignore this error
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // Ignore this error
                }
            }
        }
    }

    private synchronized Object getAction(String serviceIntfName) {
        Object action = actions.get(serviceIntfName);
        if (action == null){
            loadActions();
        }
        return actions.get(serviceIntfName);
    }

    public String processCall(String payload) throws SerializationException {
        try {

            HttpSession session = this.getThreadLocalRequest().getSession();
            List<String> list = (List<String>) session.getAttribute("testSession");
            if (list == null) {
                list = new ArrayList<String>();
            }
            list.add(new Date().toString());
            session.setAttribute("testSession", list);

            GWTRPCRequest gwtRpc = GWTRPC.decodeRequest(payload, this);
            RPCRequest rpcRequest = gwtRpc.getRpcRequest();
            onAfterRequestDeserialized(rpcRequest);

            Object action = getAction(gwtRpc.getServiceIntfName());
            if (action == null) {
                throw new IncompatibleRemoteServiceException(gwtRpc.getServiceIntfName()
                        + " undefined in actions.properties\nPlease check"); //$NON-NLS-1$
            }
            Class<?> type = action.getClass();
            if (!GWTRPC.implementsInterface(type, gwtRpc.getServiceIntfName())) {
                // The service does not implement the requested interface
                throw new IncompatibleRemoteServiceException(
                        "Blocked attempt to access interface '" + gwtRpc.getServiceIntfName() //$NON-NLS-1$
                                + "', which is not implemented by '" + GWTRPC.printTypeName(type) //$NON-NLS-1$
                                + "'; this is either misconfiguration or a hack attempt"); //$NON-NLS-1$
            }

            GwtWebContextFactory.set(new GwtWebContext(this.getThreadLocalRequest(), this.getThreadLocalResponse(), this));
            return GWTRPC.invokeAndEncodeResponse(action, rpcRequest.getMethod(), rpcRequest.getParameters(), rpcRequest
                    .getSerializationPolicy(), rpcRequest.getFlags());

        } catch (IncompatibleRemoteServiceException ex) {
            log("An IncompatibleRemoteServiceException was thrown while processing this call.", ex); //$NON-NLS-1$
            return GWTRPC.encodeResponseForFailure(null, ex);
        }
    }


}
