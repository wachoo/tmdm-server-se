package com.amalto.core.server.routing;

import com.amalto.core.server.api.RoutingEngine;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/*
 * This is <b>not</b> an actual Factory, but more a workaround to allow initialization of a RoutingEngine by Spring
 * instead of direct initialization.
 */
public class RoutingEngineFactory implements ApplicationContextAware {

    private static RoutingEngine routingEngine;

    public static RoutingEngine getRoutingEngine() {
        if (routingEngine == null) {
            // Spring is expected to call setApplicationContext(...) before anyone gets interested in a
            // RoutingEngine instance.
            throw new IllegalStateException();
        }
        return routingEngine;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        routingEngine = applicationContext.getBean(RoutingEngine.class);
    }
}
