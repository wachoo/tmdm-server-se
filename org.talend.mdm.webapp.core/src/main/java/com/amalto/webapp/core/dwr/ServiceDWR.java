package com.amalto.webapp.core.dwr;

import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amalto.core.webservice.WSServiceAction;
import com.amalto.core.webservice.WSServiceActionCode;
import org.exolab.castor.xml.Unmarshaller;
import org.xml.sax.InputSource;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;


public abstract class ServiceDWR {

    private final String jndiName;

    public ServiceDWR(String jndiName) {
        this.jndiName = jndiName;
    }

    public String start() throws RemoteException, XtentisWebappException {
        return Util.getPort().serviceAction(new WSServiceAction(jndiName, WSServiceActionCode.STOP, null, null)).getValue();
    }

    public String stop() throws RemoteException, XtentisWebappException {
        return Util.getPort().serviceAction(new WSServiceAction(jndiName, WSServiceActionCode.STOP, null, null)).getValue();
    }

    public String getStatus() throws Exception {
        try {
            return Util.getPort().serviceAction(new WSServiceAction(jndiName, WSServiceActionCode.STATUS, null, null))
                    .getValue();
        } catch (Exception e) {
            Matcher m = Pattern.compile("(.*Exception:)(.+)", Pattern.DOTALL).matcher(e.getLocalizedMessage());
            if (m.matches())
                throw new Exception(m.group(2));
            throw new Exception(e.getClass().getName() + ": " + e.getLocalizedMessage());
        }
    }

    public Object getConfiguration(Class clazz) throws Exception {
        return fetchConfiguration(clazz);
    }

    private Object fetchConfiguration(Class clazz) throws Exception {
        String marshalledConfiguration = Util.getPort()
                .serviceAction(new WSServiceAction(jndiName, WSServiceActionCode.EXECUTE, "getConfiguration", null)).getValue();
        return Unmarshaller.unmarshal(clazz, new InputSource(new StringReader(marshalledConfiguration)));

    }

}
