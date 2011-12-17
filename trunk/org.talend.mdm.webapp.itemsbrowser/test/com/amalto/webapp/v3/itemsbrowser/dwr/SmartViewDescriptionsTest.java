// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.webapp.v3.itemsbrowser.dwr;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSTransformerPK;
import com.amalto.webapp.v3.itemsbrowser.dwr.SmartViewDescriptions.SmartViewDescription;
import com.amalto.webapp.v3.itemsbrowser.dwr.SmartViewDescriptions.SmartViewProvider;

@SuppressWarnings("nls")
public class SmartViewDescriptionsTest extends TestCase {

    public void testSmartViews() throws Exception {
        final String concept = "MyEntity";
        final FakeSmartViewProvider provider = new FakeSmartViewProvider();
        FakeWSTransformerPK transformerPK1 = new FakeWSTransformerPK("Smart_view_MyEntity", "[EN:Default][FR:Défaut]");
        provider.add(transformerPK1);
        FakeWSTransformerPK transformerPK2 = new FakeWSTransformerPK("Smart_view_MyEntity#Alt1",
                "[EN:Alternate1][FR:Alternatif1]");
        provider.add(transformerPK2);
        FakeWSTransformerPK transformerPK3 = new FakeWSTransformerPK("Smart_view_MyEntity_EN", "[EN:Default english]");
        provider.add(transformerPK3);
        //
        FakeWSTransformerPK transformerPK4 = new FakeWSTransformerPK("Smart_view_MyEntity_FR#Alt1", "[FR:Alt1 francais]");
        provider.add(transformerPK4);
        FakeWSTransformerPK transformerPK5 = new FakeWSTransformerPK("Smart_view_MyEntity_FR#Alt2", "[FR:Alt2 francais]");
        provider.add(transformerPK5);
        //
        FakeWSTransformerPK transformerPK6 = new FakeWSTransformerPK("Smart_view_DifferentEntity", "[EN:Default][FR:Défaut]");
        provider.add(transformerPK6);
        FakeWSTransformerPK transformerPK7 = new FakeWSTransformerPK("Smart_view_DifferentEntity_EN#Alt",
                "[EN:Default][FR:Défault]");
        provider.add(transformerPK7);

        SmartViewDescriptions smDescs;
        Set<SmartViewDescription> smSetDefault;

        // Build using EN isoLang
        smDescs = new SmartViewDescriptions();
        smDescs.setProvider(provider);
        smDescs.build(concept, "en");

        smSetDefault = smDescs.get(null);
        assertTrue(smSetDefault.size() == 2);
        for (SmartViewDescription smDesc : smSetDefault) {
            if (smDesc.getName().equals(transformerPK1.getPk()))
                assertEquals("Default", smDesc.getDisplayName());
            else
                assertEquals("Alternate1", smDesc.getDisplayName());
        }
        Set<SmartViewDescription> smSetEn = smDescs.get("en");
        assertTrue(smSetEn.size() == 1);
        smSetEn.addAll(smSetDefault);
        assertTrue(smSetEn.size() == 2);
        for (SmartViewDescription smDesc : smSetEn) {
            if (smDesc.getName().equals(transformerPK3.getPk()))
                assertEquals("Default english", smDesc.getDisplayName());
            else
                assertEquals("Alternate1", smDesc.getDisplayName());
        }

        // Build using FR isoLang
        smDescs = new SmartViewDescriptions();
        smDescs.setProvider(provider);
        smDescs.build(concept, "fr");

        smSetDefault = smDescs.get(null);
        assertTrue(smSetDefault.size() == 2);
        for (SmartViewDescription smDesc : smSetDefault) {
            if (smDesc.getName().equals(transformerPK1.getPk()))
                assertEquals("Défaut", smDesc.getDisplayName());
            else
                assertEquals("Alternatif1", smDesc.getDisplayName());
        }
        Set<SmartViewDescription> smSetFr = smDescs.get("fr");
        assertTrue(smSetFr.size() == 2);
        smSetFr.addAll(smSetDefault);
        assertTrue(smSetFr.size() == 3);

    }

    private static class FakeSmartViewProvider implements SmartViewProvider {

        private Map<String, FakeWSTransformerPK> map = new HashMap<String, FakeWSTransformerPK>();

        public void add(FakeWSTransformerPK transformerPK) {
            map.put(transformerPK.getPk(), transformerPK);
        }

        public WSTransformerPK[] getWSTransformerPKs() throws XtentisWebappException, RemoteException {
            Collection<FakeWSTransformerPK> list = map.values();
            WSTransformerPK[] array = new WSTransformerPK[list.size()];
            int i = 0;
            for (WSTransformerPK transformerPK : list)
                array[i++] = transformerPK;
            return array;
        }

        public String getDescription(WSTransformerPK transformerPK) throws XtentisWebappException, RemoteException {
            return ((FakeWSTransformerPK) transformerPK).description;
        }
    }

    private static class FakeWSTransformerPK extends WSTransformerPK {

        private String description;

        public FakeWSTransformerPK(String name, String description) {
            super(name);
            this.description = description;
        }
    }

}
