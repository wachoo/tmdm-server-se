package com.amalto.core.server;

import com.amalto.core.objects.ObjectPOJO;
import com.amalto.core.objects.ObjectPOJOPK;
import com.amalto.core.objects.routing.*;
import com.amalto.core.util.RoutingException;
import com.amalto.core.util.XtentisException;
import com.amalto.xmlserver.interfaces.WhereAnd;
import com.amalto.xmlserver.interfaces.WhereCondition;
import org.apache.log4j.Logger;
import com.amalto.core.server.api.RoutingOrder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;


public class DefaultRoutingOrder implements RoutingOrder {

    private final static Logger LOGGER = Logger.getLogger(DefaultRoutingOrder.class);

    /**
     * Get Routing Order
     */
    public AbstractRoutingOrderV2POJO getRoutingOrder(AbstractRoutingOrderV2POJOPK pk) throws XtentisException {
        try {
            AbstractRoutingOrderV2POJO routingOrder = ObjectPOJO.load(pk.getRoutingOrderClass(), pk);
            if (routingOrder == null) {
                String err = "The Routing Order " + pk.getUniqueId() + " does not exist.";
                LOGGER.error(err);
                throw new XtentisException(err);
            }
            return routingOrder;
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to get the Routing Order of class " + pk.getRoutingOrderClass() + " and id " + pk.getName()
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.error(err);
            throw new XtentisException(err, e);
        }
    }

    /**
     * Get a RoutingOrder knowing its class - no exception is thrown: returns null if not found
     */
    public AbstractRoutingOrderV2POJO existsRoutingOrder(AbstractRoutingOrderV2POJOPK pk) throws XtentisException {
        try {
            return ObjectPOJO.load(pk.getRoutingOrderClass(), pk);
        } catch (XtentisException e) {
            return null;
        } catch (Exception e) {
            String info = "Unable to check the existence of the Routing Order of class " + pk.getRoutingOrderClass() + " and id " + pk.getName()
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("existsRoutingOrder() " + info, e);
            }
            return null;
        }
    }

    /**
     * Retrieve all Completed Routing Order PKs
     */
    public Collection<CompletedRoutingOrderV2POJOPK> getCompletedRoutingOrderPKs(String regex) throws XtentisException {
        Collection<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(CompletedRoutingOrderV2POJO.class, regex);
        ArrayList<CompletedRoutingOrderV2POJOPK> l = new ArrayList<CompletedRoutingOrderV2POJOPK>();
        for (ObjectPOJOPK completedRoutingOrder : c) {
            l.add(new CompletedRoutingOrderV2POJOPK(completedRoutingOrder));
        }
        return l;
    }

    /**
     * Retrieve all Failed Routing Order PKs
     */
    public Collection<FailedRoutingOrderV2POJOPK> getFailedRoutingOrderPKs(String regex) throws XtentisException {
        Collection<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(FailedRoutingOrderV2POJO.class, regex);
        ArrayList<FailedRoutingOrderV2POJOPK> l = new ArrayList<FailedRoutingOrderV2POJOPK>();
        for (ObjectPOJOPK failedRoutingOrder : c) {
            l.add(new FailedRoutingOrderV2POJOPK(failedRoutingOrder));
        }
        return l;
    }


    /**
     * Retrieve all RoutingOrder PKs whatever the class
     */
    public Collection<AbstractRoutingOrderV2POJOPK> getAllRoutingOrderPKs(String regex) throws XtentisException {
        ArrayList<AbstractRoutingOrderV2POJOPK> l = new ArrayList<AbstractRoutingOrderV2POJOPK>();
        l.addAll(getCompletedRoutingOrderPKs(regex));
        l.addAll(getFailedRoutingOrderPKs(regex));
        return l;
    }

    /**
     * Retrieve all RoutingOrder PKs by CriteriaWithPaging
     */
    public Collection<AbstractRoutingOrderV2POJOPK> getRoutingOrderPKsByCriteriaWithPaging(
            Class<? extends AbstractRoutingOrderV2POJO> routingOrderV2POJOClass, String anyFieldContains, String name,
            long timeCreatedMin, long timeCreatedMax, long timeScheduledMin, long timeScheduledMax, long timeLastRunStartedMin,
            long timeLastRunStartedMax, long timeLastRunCompletedMin, long timeLastRunCompletedMax, String itemConceptContains,
            String itemIDsContain, String serviceJNDIContains, String serviceParametersContains, String messageContains,
            int start, int limit, boolean withTotalCount) throws XtentisException {

        String pojoName = ObjectPOJO.getObjectRootElementName(ObjectPOJO.getObjectName(routingOrderV2POJOClass));

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                    "getRoutingOrderPKsByCriteriaWithPaging() " + routingOrderV2POJOClass + "  " //$NON-NLS-1$ //$NON-NLS-2$
                            + ObjectPOJO.getObjectName(routingOrderV2POJOClass) + "  " + pojoName); //$NON-NLS-1$
        }

        WhereAnd wAnd = new WhereAnd();

        if ((anyFieldContains != null) && (!"*".equals(anyFieldContains))) { //$NON-NLS-1$
            wAnd.add(new WhereCondition(pojoName + "//*", WhereCondition.CONTAINS, anyFieldContains, WhereCondition.PRE_NONE, //$NON-NLS-1$
                    false));
        }
        if ((name != null) && (!"*".equals(name))) { //$NON-NLS-1$
            wAnd.add(new WhereCondition(pojoName + "/name", WhereCondition.CONTAINS, name, WhereCondition.PRE_NONE, false)); //$NON-NLS-1$
        }
        if (timeCreatedMin > 0) {
            wAnd.add(new WhereCondition(pojoName + "/@time-created", WhereCondition.GREATER_THAN_OR_EQUAL, "" + timeCreatedMin, //$NON-NLS-1$ //$NON-NLS-2$
                    WhereCondition.PRE_NONE, false));
        }
        if (timeCreatedMax > 0) {
            wAnd.add(new WhereCondition(pojoName + "/@time-created", WhereCondition.LOWER_THAN_OR_EQUAL, "" + timeCreatedMax, //$NON-NLS-1$ //$NON-NLS-2$
                    WhereCondition.PRE_NONE, false));
        }
        if (timeScheduledMin > 0) {
            wAnd.add(new WhereCondition(pojoName + "/@time-scheduled", WhereCondition.GREATER_THAN_OR_EQUAL, "" //$NON-NLS-1$//$NON-NLS-2$
                    + timeScheduledMin, WhereCondition.PRE_NONE, false));
        }
        if (timeScheduledMax > 0) {
            wAnd.add(new WhereCondition(pojoName + "/@time-scheduled", WhereCondition.LOWER_THAN_OR_EQUAL, "" + timeScheduledMax, //$NON-NLS-1$ //$NON-NLS-2$
                    WhereCondition.PRE_NONE, false));
        }
        if (timeLastRunStartedMin > -2) {
            wAnd.add(new WhereCondition(pojoName + "/@time-last-run-started", WhereCondition.GREATER_THAN_OR_EQUAL, "" //$NON-NLS-1$//$NON-NLS-2$
                    + timeLastRunStartedMin, WhereCondition.PRE_NONE, false));
        }
        if (timeLastRunStartedMax > 0) {
            wAnd.add(new WhereCondition(pojoName + "/@time-last-run-started", WhereCondition.LOWER_THAN_OR_EQUAL, "" //$NON-NLS-1$//$NON-NLS-2$
                    + timeLastRunStartedMax, WhereCondition.PRE_NONE, false));
        }
        if (timeLastRunCompletedMin > 0) {
            wAnd.add(new WhereCondition(pojoName + "/@time-last-run-completed", WhereCondition.GREATER_THAN_OR_EQUAL, "" //$NON-NLS-1$//$NON-NLS-2$
                    + timeLastRunCompletedMin, WhereCondition.PRE_NONE, false));
        }
        if (timeLastRunCompletedMax > 0) {
            wAnd.add(new WhereCondition(pojoName + "/@time-last-run-completed", WhereCondition.LOWER_THAN_OR_EQUAL, "" //$NON-NLS-1$ //$NON-NLS-2$
                    + timeLastRunCompletedMax, WhereCondition.PRE_NONE, false));
        }
        if ((itemConceptContains != null) && (!"*".equals(itemConceptContains))) { //$NON-NLS-1$
            wAnd.add(new WhereCondition(pojoName + "/item-pOJOPK/concept-name", WhereCondition.CONTAINS, itemConceptContains, //$NON-NLS-1$
                    WhereCondition.PRE_AND, false));
        }
        if ((itemIDsContain != null) && (!"*".equals(itemIDsContain))) { //$NON-NLS-1$
            wAnd.add(new WhereCondition(pojoName + "/item-pOJOPK/ids", WhereCondition.CONTAINS, itemIDsContain, //$NON-NLS-1$
                    WhereCondition.PRE_AND, false));
        }
        if ((serviceJNDIContains != null) && (!"*".equals(serviceJNDIContains))) { //$NON-NLS-1$
            wAnd.add(new WhereCondition(pojoName + "/service-jNDI", WhereCondition.CONTAINS, serviceJNDIContains.contains("/") //$NON-NLS-1$ //$NON-NLS-2$
                    || serviceJNDIContains.startsWith("*") ? serviceJNDIContains : serviceJNDIContains, WhereCondition.PRE_AND, //$NON-NLS-1$
                    false));
        }
        if ((serviceParametersContains != null) && (!"*".equals(serviceParametersContains))) { //$NON-NLS-1$
            wAnd.add(new WhereCondition(pojoName + "/service-parameters", WhereCondition.CONTAINS, serviceParametersContains, //$NON-NLS-1$
                    WhereCondition.PRE_AND, false));
        }
        if ((messageContains != null) && (!"*".equals(messageContains))) { //$NON-NLS-1$
            wAnd.add(new WhereCondition(pojoName + "/message", WhereCondition.CONTAINS, messageContains, WhereCondition.PRE_AND, //$NON-NLS-1$
                    false));
        }
        ArrayList<AbstractRoutingOrderV2POJOPK> list = new ArrayList<AbstractRoutingOrderV2POJOPK>();
        Collection<ObjectPOJOPK> col = ObjectPOJO.findPKsByCriteriaWithPaging(routingOrderV2POJOClass, new String[]{
                pojoName + "/name", pojoName + "/@status"}, wAnd.getSize() == 0 ? null : wAnd, null, null, start, limit, withTotalCount);//$NON-NLS-1$ //$NON-NLS-2$
        for (ObjectPOJOPK objectPOJOPK : col) {
            if (routingOrderV2POJOClass.equals(CompletedRoutingOrderV2POJO.class)) {
                list.add(new CompletedRoutingOrderV2POJOPK(objectPOJOPK.getIds()[0]));
            } else if (routingOrderV2POJOClass.equals(FailedRoutingOrderV2POJO.class)) {
                list.add(new FailedRoutingOrderV2POJOPK(objectPOJOPK.getIds()[0]));
            }
        }
        return list;
    }

    /**
     * Retrieve all RoutingOrder PKs by Criteria
     */
    public Collection<AbstractRoutingOrderV2POJOPK> getRoutingOrderPKsByCriteria(
            Class<? extends AbstractRoutingOrderV2POJO> routingOrderV2POJOClass,
            String anyFieldContains,
            String name,
            long timeCreatedMin, long timeCreatedMax,
            long timeScheduledMin, long timeScheduledMax,
            long timeLastRunStartedMin, long timeLastRunStartedMax,
            long timeLastRunCompletedMin, long timeLastRunCompletedMax,
            String itemConceptContains,
            String itemIDsContain,
            String serviceJNDIContains,
            String serviceParametersContains,
            String messageContains) throws XtentisException {
        return getRoutingOrderPKsByCriteriaWithPaging(routingOrderV2POJOClass, anyFieldContains, name, timeCreatedMin,
                timeCreatedMax, timeScheduledMin, timeScheduledMax, timeLastRunStartedMin, timeLastRunStartedMax,
                timeLastRunCompletedMin, timeLastRunCompletedMax, itemConceptContains, itemIDsContain, serviceJNDIContains,
                serviceParametersContains, messageContains, 0, Integer.MAX_VALUE, false);
    }
}