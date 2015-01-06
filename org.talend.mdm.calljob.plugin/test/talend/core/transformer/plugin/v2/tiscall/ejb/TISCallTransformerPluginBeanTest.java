// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package talend.core.transformer.plugin.v2.tiscall.ejb;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.amalto.core.objects.Plugin;
import com.amalto.core.util.PluginRegistry;

@SuppressWarnings("nls")
public class TISCallTransformerPluginBeanTest {

    @Test
    public void testGetPlugins() {
        GenericXmlApplicationContext context = new GenericXmlApplicationContext();
        context.setResourceLoader(new PathMatchingResourcePatternResolver());
        context.load("classpath*:mdm-context.xml");
        context.refresh();
        PluginRegistry registry = (PluginRegistry) context.getBean("pluginRegistry");
        Plugin plugin = registry.getPlugin("amalto/local/transformer/plugin/callJob");
        assertNotNull(plugin);
        assert (plugin instanceof TISCallTransformerPluginBean);
        try {
            plugin = registry.getPlugin("amalto/local/transformer/plugin/xslt");
            fail();
        } catch (NoSuchBeanDefinitionException e) {

        }
    }
}
