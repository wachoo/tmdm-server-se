/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.server.routing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.AbstractJmsListeningContainer;
import org.springframework.stereotype.Component;

import bsh.EvalError;
import bsh.Interpreter;

import com.amalto.core.objects.ItemPOJO;
import com.amalto.core.objects.ItemPOJOPK;
import com.amalto.core.objects.Service;
import com.amalto.core.objects.datacluster.DataClusterPOJOPK;
import com.amalto.core.objects.routing.CompletedRoutingOrderV2POJO;
import com.amalto.core.objects.routing.FailedRoutingOrderV2POJO;
import com.amalto.core.objects.routing.RoutingRuleExpressionPOJO;
import com.amalto.core.objects.routing.RoutingRulePOJO;
import com.amalto.core.objects.routing.RoutingRulePOJOPK;
import com.amalto.core.server.api.Item;
import com.amalto.core.server.api.RoutingEngine;
import com.amalto.core.server.api.RoutingRule;
import com.amalto.core.server.security.SecurityConfig;
import com.amalto.core.util.PluginRegistry;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

@Component
public class DefaultRoutingEngine implements RoutingEngine {

    private static final String JMS_PK_PROPERTY = "pk"; //$NON-NLS-1

    private static final String JMS_TYPE_PROPERTY = "type"; //$NON-NLS-1

    private static final String JMS_CONTAINER_PROPERTY = "container"; //$NON-NLS-1

    private static final String JMS_RULES_PROPERTY = "rules"; //$NON-NLS-1

    public static final String EVENTS_DESTINATION = "org.talend.mdm.server.routing.events"; //$NON-NLS-1

    private static final Logger LOGGER = Logger.getLogger(DefaultRoutingEngine.class);

    private final Interpreter ntp = new Interpreter();

    @Autowired
    Item item;

    @Autowired
    RoutingRule routingRules;

    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier("activeEvents")
    AbstractJmsListeningContainer jmsListeningContainer;

    @Value("${routing.engine.max.execution.time.millis}")
    private long timeToLive = 0;

    private boolean isStopped;

    @PostConstruct
    public void init() {
        if (timeToLive > 0) {
            jmsTemplate.setExplicitQosEnabled(true);
            jmsTemplate.setTimeToLive(timeToLive);
        } else {
            jmsTemplate.setTimeToLive(0);
        }
    }

    private static String strip(String condition) {
        String compiled = condition;
        compiled = compiled.replaceAll("(\\s+)([aA][nN][dD])(\\s+|\\(+)", "$1&&$3"); //$NON-NLS-1$ //$NON-NLS-2$
        compiled = compiled.replaceAll("(\\s+)([oO][rR])(\\s+|\\(+)", "$1||$3"); //$NON-NLS-1$//$NON-NLS-2$
        compiled = compiled.replaceAll("(\\s*)([nN][oO][tT])(\\s+|\\(+)", "$1!$3"); //$NON-NLS-1$ //$NON-NLS-2$
        return compiled;
    }

    private static Integer safeParse(String content) {
        try {
            return Integer.parseInt(content);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Check that a rule actually matches a document
     * 
     * @return true if it matches
     * @throws XtentisException
     */
    private static boolean ruleExpressionMatches(ItemPOJO itemPOJO, RoutingRuleExpressionPOJO exp) throws XtentisException {
        Integer contentInt, expInt;
        String expXpath = exp.getXpath();
        if (expXpath.startsWith(itemPOJO.getConceptName())) {
            expXpath = StringUtils.substringAfter(expXpath, itemPOJO.getConceptName() + '/');
        }
        try {
            String[] contents = Util.getTextNodes(itemPOJO.getProjection(), expXpath);
            if (contents.length == 0 && exp.getOperator() == RoutingRuleExpressionPOJO.IS_NULL) {
                return true;
            }
            boolean match = false;
            for (String content : contents) {
                if (match) {
                    break;
                }
                switch (exp.getOperator()) {
                case RoutingRuleExpressionPOJO.CONTAINS:
                    match = StringUtils.contains(content, exp.getValue());
                    break;
                case RoutingRuleExpressionPOJO.EQUALS:
                    match = StringUtils.equals(content, exp.getValue());
                    break;
                case RoutingRuleExpressionPOJO.GREATER_THAN:
                    expInt = safeParse(exp.getValue());
                    contentInt = safeParse(content);
                    if (expInt == null || contentInt == null) {
                        continue;
                    }
                    if (contentInt > expInt) {
                        match = true;
                    }
                    break;
                case RoutingRuleExpressionPOJO.GREATER_THAN_OR_EQUAL:
                    expInt = safeParse(exp.getValue());
                    contentInt = safeParse(content);
                    if (expInt == null || contentInt == null) {
                        continue;
                    }
                    if (contentInt >= expInt) {
                        match = true;
                    }
                    break;
                case RoutingRuleExpressionPOJO.IS_NOT_NULL:
                    match = content != null;
                    break;
                case RoutingRuleExpressionPOJO.LOWER_THAN:
                    expInt = safeParse(exp.getValue());
                    contentInt = safeParse(content);
                    if (expInt == null || contentInt == null) {
                        continue;
                    }
                    if (contentInt < expInt) {
                        match = true;
                    }
                    break;
                case RoutingRuleExpressionPOJO.LOWER_THAN_OR_EQUAL:
                    expInt = safeParse(exp.getValue());
                    contentInt = safeParse(content);
                    if (expInt == null || contentInt == null) {
                        continue;
                    }
                    if (contentInt <= expInt) {
                        match = true;
                    }
                    break;
                case RoutingRuleExpressionPOJO.MATCHES:
                    match = StringUtils.countMatches(content, exp.getValue()) > 0;
                    break;
                case RoutingRuleExpressionPOJO.NOT_EQUALS:
                    match = !StringUtils.equals(content, exp.getValue());
                    break;
                case RoutingRuleExpressionPOJO.STARTSWITH:
                    if (content != null) {
                        match = content.startsWith(exp.getValue());
                    }
                    break;
                }
            }
            return match;
        } catch (TransformerException e) {
            String err = "Subscription rule expression match: unable extract xpath '" + exp.getXpath() + "' from Item '"
                    + itemPOJO.getItemPOJOPK().getUniqueID() + "': " + e.getLocalizedMessage();
            LOGGER.error(err, e);
            throw new XtentisException(err, e);
        }
    }
    
    private void sendMessage(final ItemPOJOPK itemPOJOPK, ArrayList<RoutingRulePOJO> routingRulesThatMatched) {

        // Sort execution order and send JMS message
        Collections.sort(routingRulesThatMatched);
        final String[] ruleNames = new String[routingRulesThatMatched.size()];
        final String[] ruleParameters = new String[routingRulesThatMatched.size()];
        int i = 0;

        for (RoutingRulePOJO rulePOJO : routingRulesThatMatched) {
            ruleNames[i] = rulePOJO.getPK().getUniqueId();
            ruleParameters[i] = rulePOJO.getParameters();
            i++;
        }

        jmsTemplate.send(EVENTS_DESTINATION, new MessageCreator() {

            @Override
            public Message createMessage(Session session) throws JMSException {
                Message message = session.createMessage();
                message.setObjectProperty(JMS_RULES_PROPERTY, Arrays.asList(ruleNames));
                message.setStringProperty(JMS_CONTAINER_PROPERTY, itemPOJOPK.getDataClusterPOJOPK().getUniqueId());
                message.setStringProperty(JMS_TYPE_PROPERTY, itemPOJOPK.getConceptName());
                message.setStringProperty(JMS_PK_PROPERTY, Util.joinStrings(itemPOJOPK.getIds(), ".")); //$NON-NLS-1$
                return message;
            }
        });

    }

    @Override
    public RoutingRulePOJOPK[] route(final ItemPOJOPK itemPOJOPK) throws XtentisException {
        if (isStopped) {
            LOGGER.error("Not publishing event for '" + itemPOJOPK + "' (event manager is stopped).");
            return new RoutingRulePOJOPK[0];
        }
        // The cached ItemPOJO - will only be retrieved if needed: we have expressions on the routing rules
        String type = itemPOJOPK.getConceptName();
        // Get the item
        ItemPOJO itemPOJO = item.getItem(itemPOJOPK);
        if (itemPOJO == null) { // Item does not exist, no rule can apply.
            return new RoutingRulePOJOPK[0];
        }
        // Rules that matched
        ArrayList<RoutingRulePOJO> routingRulesThatSyncMatched = new ArrayList<>();
        ArrayList<RoutingRulePOJO> routingRulesThatAsyncMatched = new ArrayList<>();
        // loop over the known rules
        Collection<RoutingRulePOJOPK> routingRulePOJOPKs = routingRules.getRoutingRulePKs(".*"); //$NON-NLS-1$
        for (RoutingRulePOJOPK routingRulePOJOPK : routingRulePOJOPKs) {
            RoutingRulePOJO routingRule = routingRules.getRoutingRule(routingRulePOJOPK);
            if (routingRule.isDeActive()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(routingRule.getName() + " disabled, skip it!");
                }
                continue;
            }
            // check if type matches the routing rule
            if (!"*".equals(routingRule.getConcept())) { //$NON-NLS-1$
                if (!type.equals(routingRule.getConcept())) {
                    continue;
                }
            }
            // check if all routing rule expression matches - null: always matches
            // aiming modify see 4572 add condition to check
            if (routingRule.getCondition() == null || routingRule.getCondition().trim().length() == 0) {
                boolean matches = true;
                Collection<RoutingRuleExpressionPOJO> routingExpressions = routingRule.getRoutingExpressions();
                if (routingExpressions != null) {
                    for (RoutingRuleExpressionPOJO routingExpression : routingExpressions) {
                        if (!ruleExpressionMatches(itemPOJO, routingExpression)) {
                            matches = false; // Rule doesn't match: expect a full match to consider routing rule.
                            break;
                        }
                    }
                }
                if (!matches) {
                    continue;
                }
            } else {
                String condition = routingRule.getCondition();
                String compileCondition = strip(condition);
                Collection<RoutingRuleExpressionPOJO> routingExpressions = routingRule.getRoutingExpressions();
                try {
                    for (RoutingRuleExpressionPOJO pojo : routingExpressions) {
                        if (pojo.getName() != null && pojo.getName().trim().length() > 0) {
                            Pattern p1 = Pattern.compile(pojo.getName(), Pattern.CASE_INSENSITIVE);
                            Matcher m1 = p1.matcher(condition);
                            while (m1.find()) {
                                ntp.set(m1.group(), ruleExpressionMatches(itemPOJO, pojo));
                            }
                        }
                    }
                    // compile
                    ntp.eval("routingRuleResult = " + compileCondition + ";"); //$NON-NLS-1$ //$NON-NLS-2$
                    boolean result = (Boolean) ntp.get("routingRuleResult"); //$NON-NLS-1$
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(condition + " : " + result);
                        if (result) {
                            LOGGER.debug("Trigger \"" + (routingRule.getName() == null ? "" : routingRule.getName())
                                    + "\" matched! ");
                        }
                    }
                    if (!result) {
                        continue;
                    }
                } catch (EvalError e) {
                    String err = "Condition compile error :" + e.getMessage();
                    LOGGER.error(err, e);
                    throw new XtentisException(err, e);
                }
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("route() Routing Rule MATCH '" + routingRulePOJOPK.getUniqueId() + "' for item '"
                        + itemPOJOPK.getUniqueID() + "'");
            }
            // increment matching routing rules counter
            if (routingRule.isSynchronous()) {
                routingRulesThatSyncMatched.add(routingRule);
            } else {
                routingRulesThatAsyncMatched.add(routingRule);
            }
        }

        // Contract imposes to send matching rule names
        List<RoutingRulePOJOPK> pks = new ArrayList<RoutingRulePOJOPK>(routingRulesThatSyncMatched.size() + routingRulesThatAsyncMatched.size());

        // Log debug information if no rule found for document
        if (routingRulesThatSyncMatched.size() == 0 && routingRulesThatAsyncMatched.size() == 0) {
            if (LOGGER.isDebugEnabled()) {
                String err = "Unable to find a routing rule for document " + itemPOJOPK.getUniqueID();
                LOGGER.debug(err);
            }
            return new RoutingRulePOJOPK[0];
        }
        
        // execute asynchronous triggers (send JMS message)
        if (routingRulesThatAsyncMatched.size() > 0) {
            this.sendMessage(itemPOJOPK, routingRulesThatAsyncMatched);
            pks.addAll(buildListOfRulePK(routingRulesThatAsyncMatched));
        }
        
        // execute synchronous triggers directly
        if (routingRulesThatSyncMatched.size() > 0) {
            Collections.sort(routingRulesThatSyncMatched);
            String routingOrder = UUID.randomUUID().toString();
            for(RoutingRulePOJO rule : routingRulesThatSyncMatched){
                applyRule(itemPOJOPK, rule, routingOrder);
            }
            pks.addAll(buildListOfRulePK(routingRulesThatSyncMatched));
        }
        return pks.toArray(new RoutingRulePOJOPK[pks.size()]);
    }
    
    private List<RoutingRulePOJOPK> buildListOfRulePK(final List<RoutingRulePOJO> routingRules){
        List<RoutingRulePOJOPK> result = new ArrayList<RoutingRulePOJOPK>(routingRules.size());
        for (RoutingRulePOJO rulePOJO : routingRules) {
            result.add(new RoutingRulePOJOPK(rulePOJO.getPK().getUniqueId()));
        }
        return result;
    }
    
    /**
     * Proceed with rule execution, either the rule is synchronous or asynchronous.
     * 
     * @param itemPOJOPK source instance PK
     * @param routingRule the rule to execute
     * @param routingOrderId routing id
     */
    private void applyRule(ItemPOJOPK itemPOJOPK, RoutingRulePOJO routingRule, String routingOrderId) {
        // Proceed with rule execution
        final Service service = PluginRegistry.getInstance().getService(routingRule.getServiceJNDI());
        try {
            if(service != null){
                service.receiveFromInbound(itemPOJOPK, routingOrderId, routingRule.getParameters());
                // Record routing order was successfully executed.
                CompletedRoutingOrderV2POJO completedRoutingOrder = new CompletedRoutingOrderV2POJO();
                completedRoutingOrder.setItemPOJOPK(itemPOJOPK);
                completedRoutingOrder.setName(itemPOJOPK.toString());
                completedRoutingOrder.setServiceJNDI(routingRule.getServiceJNDI());
                completedRoutingOrder.setServiceParameters(routingRule.getParameters());
                try {
                    completedRoutingOrder.store();
                } catch (Throwable e) {
                    LOGGER.error("Unable to store completed routing order (enable DEBUG for details).");
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Unable to store completed routing order.", e);
                    }
                }
            }
            else {
                final String errorMessage = "Service '" + routingRule.getServiceJNDI() + "' does not exist.";
                LOGGER.error(errorMessage);
                createAndStoreFailedRoutingOrder(itemPOJOPK, routingRule, errorMessage);
            }
        }
        catch(Exception e){
            String errorMessage = "Unable to execute the Routing Order '" + routingOrderId + "'."
                    + " The service: '" + routingRule.getServiceJNDI() + "' failed to execute. "
                    + e.getMessage();
            LOGGER.error(errorMessage, e);
            createAndStoreFailedRoutingOrder(itemPOJOPK, routingRule, errorMessage);
        }
    }
    
    /**
     * In case of rule execution error: creates and stores a new FailedRoutingOrderV2POJO regarding the entity identified by itemPOJOPK
     * when executing rule routingRule, with provided error message. 
     * 
     * In case of storage failure, logs error but does not throw exception.
     * 
     * @param itemPOJOPK the entity PK
     * @param routingRule the rule
     * @param errorMessage the error message to save
     * @return the new FailedRoutingOrderV2POJO
     */
    private FailedRoutingOrderV2POJO createAndStoreFailedRoutingOrder(ItemPOJOPK itemPOJOPK, RoutingRulePOJO routingRule, String errorMessage) {
        // Record routing order has failed.
        FailedRoutingOrderV2POJO failedRoutingOrder = new FailedRoutingOrderV2POJO();
        failedRoutingOrder.setMessage(errorMessage);
        failedRoutingOrder.setItemPOJOPK(itemPOJOPK);
        failedRoutingOrder.setName(itemPOJOPK.toString());
        failedRoutingOrder.setServiceJNDI(routingRule.getServiceJNDI());
        failedRoutingOrder.setServiceParameters(routingRule.getParameters());
        try {
            failedRoutingOrder.store();
        } catch (XtentisException e) {
            LOGGER.error("Unable to store failed routing order (enable DEBUG for details).");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Unable to store failed routing order.", e);
            }
        }
        return failedRoutingOrder;
    }

    // Called by Spring, do not remove
    @Override
    public void consume(final Message message) {
        try {
            @SuppressWarnings("unchecked")
            final List<String> rules = (List<String>) message.getObjectProperty(JMS_RULES_PROPERTY);
            final String pk = message.getStringProperty(JMS_PK_PROPERTY);
            final String type = message.getStringProperty(JMS_TYPE_PROPERTY);
            final String container = message.getStringProperty(JMS_CONTAINER_PROPERTY);
            final String routingOrderId = message.getJMSMessageID();
            for (int ruleIndex = 0; ruleIndex < rules.size(); ruleIndex++) {
                String rule = rules.get(ruleIndex);
                final RoutingRulePOJO routingRule = routingRules.getRoutingRule(new RoutingRulePOJOPK(rule));
                // Apparently rule is not (yet) deployed onto this system's DB instance, but... that 's
                // rather unexpected since all nodes in cluster are supposed to share same system DB.
                if (routingRule == null) {
                    throw new RuntimeException("Cannot execute rule(s) " + rules + ": routing rule '" + rule
                            + "' can not be found.");
                }
                SecurityConfig.invokeSynchronousPrivateInternal(new Runnable() {
                    @Override
                    public void run() {
                        // execute all rules synchronously
                        final ItemPOJOPK itemPOJOPK = new ItemPOJOPK(new DataClusterPOJOPK(container), type, pk.split("\\.")); //$NON-NLS-1$
                        applyRule(itemPOJOPK, routingRule, routingOrderId);
                    }
                });
            }
            // acknowledge message once all rules are executed
            message.acknowledge();
        } 
        catch (Exception e) {
            throw new RuntimeException("Unable to process message.", e);
        }
    }

    @Override
    public void start() throws XtentisException {
        jmsListeningContainer.start();
    }

    @Override
    public void stop() throws XtentisException {
        jmsListeningContainer.stop();
        isStopped = true;
    }

    @Override
    public void suspend(boolean suspend) throws XtentisException {
        if (suspend) {
            jmsListeningContainer.stop();
        } else {
            jmsListeningContainer.start();
        }
    }

    @Override
    public int getStatus() throws XtentisException {
        boolean running = jmsListeningContainer.isRunning();
        if (running) {
            return RoutingEngine.RUNNING;
        } else {
            return isStopped ? RoutingEngine.STOPPED : RoutingEngine.SUSPENDED;
        }
    }
    
}
