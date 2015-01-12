package com.amalto.core.server.routing;

import com.amalto.core.objects.ItemPOJO;
import com.amalto.core.objects.ItemPOJOPK;
import com.amalto.core.objects.datacluster.DataClusterPOJOPK;
import com.amalto.core.objects.datamodel.DataModelPOJO;
import com.amalto.core.objects.routing.RoutingRuleExpressionPOJO;
import com.amalto.core.objects.routing.RoutingRulePOJO;
import com.amalto.core.objects.routing.RoutingRulePOJOPK;
import com.amalto.core.server.api.Item;
import com.amalto.core.server.api.RoutingEngine;
import com.amalto.core.server.api.RoutingRule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.mock.env.MockEnvironment;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@Ignore
public class RoutingEngineTest {

    private static AbstractApplicationContext context;

    private static RoutingRule                routingRule;

    private static Item                       item;

    private final DataClusterPOJOPK           container = new DataClusterPOJOPK("Test");

    private final DataModelPOJO               dataModel = new DataModelPOJO("Test");

    @BeforeClass
    public static void setup() {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext();
        context.setResourceLoader(new PathMatchingResourcePatternResolver());
        MockEnvironment env = new MockEnvironment();
        env.setProperty("mdm.root", "");
        env.setProperty("mdm.root.url", "");
        context.setEnvironment(env);
        context.load("classpath:**/" + RoutingEngineTest.class.getName() + ".xml");
        // FIXME Setting default-lazy-init on the top level beans element seems not applied to beans inside an imported
        // resource
        // Workaround: set all beans to be lazy-init in a programmatic manner
        // See also https://gist.github.com/eeichinger/1979033 as an alternative
        for (String beanName : context.getBeanDefinitionNames()) {
            context.getBeanDefinition(beanName).setLazyInit(true);
        }
        context.refresh();
        RoutingEngineTest.context = context;
        RoutingEngineTest.routingRule = context.getBean(RoutingRule.class);
        RoutingEngineTest.item = context.getBean(Item.class);
    }

    @AfterClass
    public static void tearDown() {
        context.destroy();
    }

    private static void clearRules() throws com.amalto.core.util.XtentisException {
        Collection<RoutingRulePOJOPK> routingRulePKs = routingRule.getRoutingRulePKs(".*");
        for (RoutingRulePOJOPK pk : routingRulePKs) {
            routingRule.removeRoutingRule(pk);
        }
    }

    @Test
    public void testEmptyRules() throws Exception {
        clearRules();
        RoutingEngine routingEngine = context.getBean(RoutingEngine.class);
        RoutingRulePOJOPK[] routes = routingEngine.route(new ItemPOJOPK(container, "Person", new String[] { "1", "2" }));
        assertEquals(0, routes.length);
    }

    @Test
    public void testMatchRuleType() throws Exception {
        RoutingEngine routingEngine = context.getBean(RoutingEngine.class);
        // Match all rule
        clearRules();
        RoutingRulePOJO rule = new RoutingRulePOJO("testTypeMatchRule");
        rule.setConcept("*");
        routingRule.putRoutingRule(rule);
        RoutingRulePOJOPK[] routes = routingEngine.route(new ItemPOJOPK(container, "Person", new String[] { "1", "2" }));
        assertEquals(1, routes.length);
        // Match per type name
        clearRules();
        rule = new RoutingRulePOJO("testTypeMatchRule");
        rule.setConcept("Person");
        routingRule.putRoutingRule(rule);
        routes = routingEngine.route(new ItemPOJOPK(container, "Person", new String[] { "1", "2" }));
        assertEquals(1, routes.length);
        // Non match
        clearRules();
        rule = new RoutingRulePOJO("testTypeMatchRule");
        rule.setConcept("Address");
        routingRule.putRoutingRule(rule);
        routes = routingEngine.route(new ItemPOJOPK(container, "Person", new String[] { "1", "2" }));
        assertEquals(0, routes.length);
    }

    @Test
    public void testMatchRuleExpression() throws Exception {
        RoutingEngine routingEngine = context.getBean(RoutingEngine.class);
        clearRules();
        RoutingRulePOJO rule = new RoutingRulePOJO("testTypeMatchRule");
        rule.setConcept("*");
        List<RoutingRuleExpressionPOJO> expressions = Arrays.asList(new RoutingRuleExpressionPOJO("Person", "id",
                RoutingRuleExpressionPOJO.EQUALS, "1"), new RoutingRuleExpressionPOJO("Person", "id2",
                RoutingRuleExpressionPOJO.EQUALS, "2"));
        rule.setRoutingExpressions(expressions);
        routingRule.putRoutingRule(rule);
        item.putItem(new ItemPOJO(container, "Person", new String[] { "1", "2" }, 0, "<Person><id>1</id><id2>2</id2></Person>"),
                dataModel);
        RoutingRulePOJOPK[] routes = routingEngine.route(new ItemPOJOPK(container, "Person", new String[] { "1", "2" }));
        assertEquals(1, routes.length);
        routes = routingEngine.route(new ItemPOJOPK(container, "Person", new String[] { "2", "2" }));
        assertEquals(0, routes.length);
    }

}
