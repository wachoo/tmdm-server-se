package com.amalto.core.ejb;

import com.amalto.core.ejb.remote.ServiceValue;
import com.amalto.core.metadata.LongString;

/**
 * @ejb.bean name="Service" display-name="Service" description="Service" jndi-name="amalto/remote/core/service"
 * local-jndi-name = "amalto/local/core/service" type="BMP" view-type="local" reentrant="true"
 * @ejb.value-object
 * @ejb.pk
 * @ejb.permission view-type = "local" unchecked = "true"
 */
public abstract class ServiceBean {

    public ServiceBean() {
    }

    /**
     * @ejb.interface-method view-type="local"
     * @ejb.value-object
     * @ejb.persistence
     * @ejb.pk-field
     */
    public abstract String getServiceName();

    public abstract void setServiceName(String name);

    /**
     * @ejb.interface-method view-type="local"
     * @ejb.value-object
     * @ejb.persistence
     */
    @LongString
    public abstract String getConfiguration();

    /**
     * @ejb.interface-method view-type="local"
     */
    public abstract void setConfiguration(String configuration);

    /**
     * Any Data hat is not configuration
     * 
     * @ejb.interface-method view-type="local"
     * @ejb.value-object
     * @ejb.persistence
     */
    @LongString
    public abstract String getServiceData();

    /**
     * @ejb.interface-method view-type="local"
     */
    public abstract void setServiceData(String serviceData);

    /**
     * @ejb.interface-method view-type="local"
     */
    @LongString
    public abstract ServiceValue getServiceValue();

}
