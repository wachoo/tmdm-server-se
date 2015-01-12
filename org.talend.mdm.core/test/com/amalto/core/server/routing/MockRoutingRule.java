package com.amalto.core.server.routing;

import com.amalto.core.objects.routing.RoutingRulePOJO;
import com.amalto.core.objects.routing.RoutingRulePOJOPK;
import com.amalto.core.server.api.RoutingRule;
import com.amalto.core.util.XtentisException;

import java.util.*;

public class MockRoutingRule implements RoutingRule {

    private final Map<RoutingRulePOJOPK, RoutingRulePOJO> store = new HashMap<RoutingRulePOJOPK, RoutingRulePOJO>();

    @Override
    public RoutingRulePOJOPK putRoutingRule(RoutingRulePOJO routingRule) throws XtentisException {
        RoutingRulePOJOPK key = new RoutingRulePOJOPK(routingRule.getPK().getUniqueId());
        store.put(key, routingRule);
        return key;
    }

    @Override
    public RoutingRulePOJO getRoutingRule(RoutingRulePOJOPK pk) throws XtentisException {
        return store.get(pk);
    }

    @Override
    public RoutingRulePOJO existsRoutingRule(RoutingRulePOJOPK pk) throws XtentisException {
        return store.get(pk);
    }

    @Override
    public RoutingRulePOJOPK removeRoutingRule(RoutingRulePOJOPK pk) throws XtentisException {
        store.remove(pk);
        return pk;
    }

    @Override
    public Collection<RoutingRulePOJOPK> getRoutingRulePKs(String regex) throws XtentisException {
        Set<RoutingRulePOJOPK> match = new HashSet<RoutingRulePOJOPK>();
        Set<RoutingRulePOJOPK> pkList = store.keySet();
        for (RoutingRulePOJOPK routingRulePOJOPK : pkList) {
            if (routingRulePOJOPK.getUniqueId().matches(regex)) {
                match.add(routingRulePOJOPK);
            }
        }
        return match;
    }
}
