// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.metadata;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashSet;

import junit.framework.TestCase;

import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.mdm.commmon.util.bean.MDMContext;

@SuppressWarnings("nls")
public class MDMContextTest extends TestCase {

    private MDMContext mdmContext = new MDMContext();

    private final static int BUFFER_SIZE = 4096;

    @Override
    protected void setUp() throws Exception {
        mdmContext.setHost("192.168.30.1");
        mdmContext.setPort("8180");
        mdmContext.setUsername("talend");
        mdmContext.setPassword("talend");
        HashSet<String> roles = new HashSet<String>();
        roles.add("System_Admin");
        roles.add("administration");
        mdmContext.setRoles(roles);
        mdmContext
                .setEntityXml("<Product><Id>1</Id><Name>test</Name><ShortDescription>Test</ShortDescription><Price>123</Price></Product>");
        MetadataRepository repository = new MetadataRepository();
        mdmContext.setXsdSchema(InputStreamToString(MDMContextTest.class.getResourceAsStream("product.xsd"), "UTF-8"));
        repository.load(MDMContextTest.class.getResourceAsStream("product.xsd"));
        mdmContext.setRepository(repository);
    }

    public void testDeserialize() throws Exception {
        String _mdmContext = mdmContext.serialize();
        assertNotNull(_mdmContext);
        MDMContext context = MDMContext.deserialize(_mdmContext);
        assertEquals("192.168.30.1", context.getHost());
        assertEquals("talend", context.getPassword());
        assertEquals(2, context.getRoles().size());
        assertEquals("<Product><Id>1</Id><Name>test</Name><ShortDescription>Test</ShortDescription><Price>123</Price></Product>",
                context.getEntityXml());
        assertEquals("Product", context.getEntityDocument().getDocumentElement().getNodeName());
        MetadataRepository repository = context.getRepository();
        assertNotNull(repository);
        assertEquals(3, repository.getUserComplexTypes().size());
    }

    public static String InputStreamToString(InputStream in, String encoding) throws Exception {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {
            outStream.write(data, 0, count);
        }

        data = null;
        return new String(outStream.toByteArray(), encoding);
    }

}