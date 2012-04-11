/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.save;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.save.context.DocumentSaver;
import com.amalto.core.save.context.SaverSource;
import com.amalto.core.schema.validation.SkipAttributeDocumentBuilder;
import com.amalto.core.schema.validation.Validator;
import com.amalto.core.schema.validation.XmlSchemaValidator;
import com.amalto.core.util.OutputReport;
import com.amalto.core.util.XtentisException;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.util.*;

@SuppressWarnings("nls")
public class DocumentSaveTest extends TestCase {

    private static Logger LOG = Logger.getLogger(DocumentSaveTest.class);

    private XPath xPath = XPathFactory.newInstance().newXPath();

    @Override
    public void setUp() throws Exception {
        XPathFactory xPathFactory = XPathFactory.newInstance();
        xPath = xPathFactory.newXPath();
        xPath.setNamespaceContext(new TestNamespaceContext());
    }

    private Object evaluate(Element committedElement, String path) throws XPathExpressionException {
        return xPath.evaluate(path, committedElement, XPathConstants.STRING);
    }

    public void testTypeCacheInvalidate() throws Exception {
        final MetadataRepository repository = new MetadataRepository();
        repository.load(DocumentSaveTest.class.getResourceAsStream("metadata1.xsd"));

        TestSaverSource source = new TestSaverSource(repository, false, "test1_original.xml", "metadata1.xsd");
        assertNull(source.getLastInvalidatedTypeCache());

        SaverSession session = SaverSession.newSession(source);
        InputStream recordXml = DocumentSaveTest.class.getResourceAsStream("test1.xml");
        DocumentSaverContext context = session.getContextFactory().create("MDM", "DStar", "Source", recordXml, true, true);
        DocumentSaver saver = context.createSaver();
        saver.save(session, context);
        MockCommitter committer = new MockCommitter();
        session.end(committer);
        assertTrue(committer.hasSaved());

        session = SaverSession.newSession(source);
        session.invalidateTypeCache("DStar");
        session.end(committer);
        assertEquals("DStar", source.getLastInvalidatedTypeCache());
    }

    public void testValidationWithXSINamespace() throws Exception {
        InputStream contractXML = DocumentSaveTest.class.getResourceAsStream("contract.xml");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        Document contract = new SkipAttributeDocumentBuilder(documentBuilderFactory.newDocumentBuilder()).parse(contractXML);

        XmlSchemaValidator validator = new XmlSchemaValidator("", DocumentSaveTest.class.getResourceAsStream("metadata3.xsd"), Validator.NO_OP_VALIDATOR);
        validator.validate(contract.getDocumentElement());
    }

    public void testCreate() throws Exception {
        final MetadataRepository repository = new MetadataRepository();
        repository.load(DocumentSaveTest.class.getResourceAsStream("metadata1.xsd"));

        SaverSource source = new TestSaverSource(repository, false, "test1_original.xml", "metadata1.xsd");

        SaverSession session = SaverSession.newSession(source);
        InputStream recordXml = DocumentSaveTest.class.getResourceAsStream("test1.xml");
        DocumentSaverContext context = session.getContextFactory().create("MDM", "DStar", "Source", recordXml, true, true);
        DocumentSaver saver = context.createSaver();
        saver.save(session, context);
        MockCommitter committer = new MockCommitter();
        session.end(committer);

        assertTrue(committer.hasSaved());
        Element committedElement = committer.getCommittedElement();
        assertEquals("Chicago", evaluate(committedElement, "/Agency/Name"));
        assertEquals("Chicago", evaluate(committedElement, "/Agency/City"));
    }

    public void testCreateFailure() throws Exception {
        final MetadataRepository repository = new MetadataRepository();
        repository.load(DocumentSaveTest.class.getResourceAsStream("metadata1.xsd"));

        SaverSource source = new TestSaverSource(repository, false, "", "metadata1.xsd");

        SaverSession session = SaverSession.newSession(source);
        InputStream recordXml = DocumentSaveTest.class.getResourceAsStream("test10.xml");
        DocumentSaverContext context = session.getContextFactory().create("MDM", "DStar", "Source", recordXml, true, true);
        DocumentSaver saver = context.createSaver();
        try {
            saver.save(session, context);
            fail();
        } catch (SaveException e) {
            assertTrue(e.getBeforeSavingMessage().isEmpty());
        }
    }

    public void testUpdate() throws Exception {
        // TODO Test for modification of id (this test modifies id but this is intentional).
        final MetadataRepository repository = new MetadataRepository();
        repository.load(DocumentSaveTest.class.getResourceAsStream("metadata1.xsd"));

        SaverSource source = new TestSaverSource(repository, true, "test1_original.xml", "metadata1.xsd");

        SaverSession session = SaverSession.newSession(source);
        InputStream recordXml = DocumentSaveTest.class.getResourceAsStream("test1.xml");
        DocumentSaverContext context = session.getContextFactory().create("MDM", "DStar", "Source", recordXml, true, false);
        DocumentSaver saver = context.createSaver();
        saver.save(session, context);
        MockCommitter committer = new MockCommitter();
        session.end(committer);

        assertTrue(committer.hasSaved());
        Element committedElement = committer.getCommittedElement();
        assertEquals("Chicago", evaluate(committedElement, "/Agency/Name"));
        assertEquals("Chicago", evaluate(committedElement, "/Agency/City"));
    }

    public void testWithClone() throws Exception {
        final MetadataRepository repository = new MetadataRepository();
        repository.load(DocumentSaveTest.class.getResourceAsStream("metadata3.xsd"));

        SaverSource source = new TestSaverSource(repository, true, "test13_original.xml", "metadata3.xsd");

        SaverSession session = SaverSession.newSession(source);
        InputStream recordXml = DocumentSaveTest.class.getResourceAsStream("test13.xml");
        DocumentSaverContext context = session.getContextFactory().create("MDM", "Contract", "Source", recordXml, true, false);
        DocumentSaver saver = context.createSaver();
        saver.save(session, context);
        MockCommitter committer = new MockCommitter();
        session.end(committer);

        assertTrue(committer.hasSaved());
        Element committedElement = committer.getCommittedElement();
        assertEquals("ContractDetailSubType", evaluate(committedElement, "/Contract/detail[1]/@xsi:type"));
        assertEquals("ContractDetailSubType", evaluate(committedElement, "/Contract/detail[2]/@xsi:type"));
        assertEquals("sdfsdf", evaluate(committedElement, "/Contract/detail[2]/code"));
        assertEquals("sdfsdf", evaluate(committedElement, "/Contract/detail[2]/features/actor"));
    }

    public void testSubclassTypeChange() throws Exception {
        final MetadataRepository repository = new MetadataRepository();
        repository.load(DocumentSaveTest.class.getResourceAsStream("metadata3.xsd"));

        SaverSource source = new TestSaverSource(repository, true, "test14_original.xml", "metadata3.xsd");

        SaverSession session = SaverSession.newSession(source);
        InputStream recordXml = DocumentSaveTest.class.getResourceAsStream("test14.xml");
        DocumentSaverContext context = session.getContextFactory().create("MDM", "Contract", "Source", recordXml, true, false);
        DocumentSaver saver = context.createSaver();
        saver.save(session, context);
        MockCommitter committer = new MockCommitter();
        session.end(committer);

        assertTrue(committer.hasSaved());
        Element committedElement = committer.getCommittedElement();
        assertEquals("ContractDetailType", evaluate(committedElement, "/Contract/detail[1]/@xsi:type"));
        assertEquals("sdfsdf", evaluate(committedElement, "/Contract/detail[1]/code"));
    }


    public void testUpdateSecurity() throws Exception {
        // TODO Test for modification of id (this test modifies id but this is intentional).
        final MetadataRepository repository = new MetadataRepository();
        repository.load(DocumentSaveTest.class.getResourceAsStream("metadata1.xsd"));

        TestSaverSource source = new TestSaverSource(repository, true, "test8_original.xml", "metadata1.xsd");

        SaverSession session = SaverSession.newSession(source);
        InputStream recordXml = DocumentSaveTest.class.getResourceAsStream("test8.xml");
        DocumentSaverContext context = session.getContextFactory().create("MDM", "DStar", "Source", recordXml, true, false);
        DocumentSaver saver = context.createSaver();
        try {
            saver.save(session, context);
            fail("Expected an exception: user not allowed to change some fields.");
        } catch (SaveException e) {
            // Expected
            assertTrue(e.getCause() instanceof IllegalStateException);
            // Don't expect error order to be the same from one run to another.
            assertTrue(e.getCause().getMessage().contains("'Zip'"));
            assertTrue(e.getCause().getMessage().contains("'State'"));
            assertTrue(e.getCause().getMessage().contains("'Agency'"));
        }

        // Test changing user name (and user's roles).
        source.setUserName("admin");
        session = SaverSession.newSession(source);
        recordXml = DocumentSaveTest.class.getResourceAsStream("test8.xml");
        context = session.getContextFactory().create("MDM", "DStar", "Source", recordXml, true, false);
        saver = context.createSaver();
        saver.save(session, context);
    }

    public void testNoUpdate() throws Exception {
        final MetadataRepository repository = new MetadataRepository();
        repository.load(DocumentSaveTest.class.getResourceAsStream("metadata1.xsd"));

        SaverSource source = new TestSaverSource(repository, true, "test4_original.xml", "metadata1.xsd");

        SaverSession session = SaverSession.newSession(source);
        InputStream recordXml = DocumentSaveTest.class.getResourceAsStream("test4.xml");
        DocumentSaverContext context = session.getContextFactory().create("MDM", "DStar", "Source", recordXml, true, false);
        DocumentSaver saver = context.createSaver();
        saver.save(session, context);
        MockCommitter committer = new MockCommitter();
        session.end(committer);

        assertFalse(committer.hasSaved());
    }

    public void testLegacyUpdate() throws Exception {
        final MetadataRepository repository = new MetadataRepository();
        repository.load(DocumentSaveTest.class.getResourceAsStream("metadata1.xsd"));

        SaverSource source = new TestSaverSource(repository, true, "test5_original.xml", "metadata1.xsd");

        SaverSession session = SaverSession.newSession(source);
        InputStream recordXml = DocumentSaveTest.class.getResourceAsStream("test5.xml");
        DocumentSaverContext context = session.getContextFactory().create("MDM", "DStar", "Source", recordXml, true, false);
        DocumentSaver saver = context.createSaver();
        saver.save(session, context);
        MockCommitter committer = new MockCommitter();
        session.end(committer);

        assertTrue(committer.hasSaved());
        Element committedElement = committer.getCommittedElement();
        assertEquals("Description", evaluate(committedElement, "/Product/Description"));
        assertEquals("60", evaluate(committedElement, "/Product/Price"));
        assertEquals("Lemon", evaluate(committedElement, "/Product/Features/Colors/Color[1]"));
        assertEquals("Light Blue", evaluate(committedElement, "/Product/Features/Colors/Color[2]"));
    }

    public void testLegacyUpdate2() throws Exception {
        final MetadataRepository repository = new MetadataRepository();
        repository.load(DocumentSaveTest.class.getResourceAsStream("metadata1.xsd"));

        SaverSource source = new TestSaverSource(repository, true, "test6_original.xml", "metadata1.xsd");

        SaverSession session = SaverSession.newSession(source);
        InputStream recordXml = DocumentSaveTest.class.getResourceAsStream("test6.xml");
        DocumentSaverContext context = session.getContextFactory().create("MDM", "DStar", "Source", recordXml, true, false);
        DocumentSaver saver = context.createSaver();
        saver.save(session, context);
        MockCommitter committer = new MockCommitter();
        session.end(committer);

        assertTrue(committer.hasSaved());
        Element committedElement = committer.getCommittedElement();
        assertEquals("Description", evaluate(committedElement, "/Product/Description"));
        assertEquals("60", evaluate(committedElement, "/Product/Price"));
        assertEquals("Lemon", evaluate(committedElement, "/Product/Features/Colors/Color[1]"));
        assertEquals("Light Blue", evaluate(committedElement, "/Product/Features/Colors/Color[2]"));
    }

    public void testLegacyUpdate3() throws Exception {
        final MetadataRepository repository = new MetadataRepository();
        repository.load(DocumentSaveTest.class.getResourceAsStream("metadata1.xsd"));

        SaverSource source = new TestSaverSource(repository, true, "test7_original.xml", "metadata1.xsd");

        SaverSession session = SaverSession.newSession(source);
        InputStream recordXml = DocumentSaveTest.class.getResourceAsStream("test7.xml");
        DocumentSaverContext context = session.getContextFactory().create("MDM", "DStar", "Source", recordXml, true, false);
        DocumentSaver saver = context.createSaver();
        saver.save(session, context);
        MockCommitter committer = new MockCommitter();
        session.end(committer);

        assertTrue(committer.hasSaved());
        Element committedElement = committer.getCommittedElement();
        assertEquals("60", evaluate(committedElement, "/Product/Price"));
    }

    public void testSchematronValidation() throws Exception {
        final MetadataRepository repository = new MetadataRepository();
        repository.load(DocumentSaveTest.class.getResourceAsStream("metadata1.xsd"));

        SaverSource source = new TestSaverSource(repository, true, "test9_original.xml", "metadata1.xsd");

        SaverSession session = SaverSession.newSession(source);
        InputStream recordXml = DocumentSaveTest.class.getResourceAsStream("test9.xml");
        DocumentSaverContext context = session.getContextFactory().create("MDM", "DStar", "Source", recordXml, true, false);
        DocumentSaver saver = context.createSaver();
        saver.save(session, context);
        MockCommitter committer = new MockCommitter();
        session.end(committer);

        assertTrue(committer.hasSaved());
        Element committedElement = committer.getCommittedElement();
        assertEquals("JohDo", evaluate(committedElement, "/Agent/Id"));
        assertEquals("John", evaluate(committedElement, "/Agent/Firstname"));
        assertEquals("Doe", evaluate(committedElement, "/Agent/Lastname"));
        assertEquals("1", evaluate(committedElement, "/Agent/CommissionCode"));
        assertEquals("2010-01-01", evaluate(committedElement, "/Agent/StartDate"));
    }

    public void testSystemUpdate() throws Exception {
        final MetadataRepository repository = new MetadataRepository();
        repository.load(DocumentSaveTest.class.getResourceAsStream("metadata1.xsd"));

        SaverSource source = new TestSaverSource(repository, true, "test3_original.xml", "metadata1.xsd");

        SaverSession session = SaverSession.newSession(source);
        InputStream recordXml = DocumentSaveTest.class.getResourceAsStream("test3.xml");
        DocumentSaverContext context = session.getContextFactory().create("UpdateReport", "UpdateReport", "Source", recordXml,
                true, false);
        DocumentSaver saver = context.createSaver();
        saver.save(session, context);
        MockCommitter committer = new MockCommitter();
        session.end(committer);

        assertTrue(committer.hasSaved());
        Element committedElement = committer.getCommittedElement();
        assertEquals("LOGICAL_DELETE", evaluate(committedElement, "/Update/OperationType"));
    }

    public void testProductUpdate() throws Exception {
        final MetadataRepository repository = new MetadataRepository();
        repository.load(DocumentSaveTest.class.getResourceAsStream("metadata1.xsd"));

        SaverSource source = new TestSaverSource(repository, true, "test2_original.xml", "metadata1.xsd");

        SaverSession session = SaverSession.newSession(source);
        InputStream recordXml = DocumentSaveTest.class.getResourceAsStream("test2.xml");
        DocumentSaverContext context = session.getContextFactory().create("MDM", "DStar", "Source", recordXml, true, false);
        DocumentSaver saver = context.createSaver();
        saver.save(session, context);
        MockCommitter committer = new MockCommitter();
        session.end(committer);

        assertTrue(committer.hasSaved());
        Element committedElement = committer.getCommittedElement();
        assertEquals("60", evaluate(committedElement, "/Product/Price"));
        assertEquals("TT", evaluate(committedElement, "/Product/Description"));
        assertEquals("New product name", evaluate(committedElement, "/Product/Name"));
    }

    public void testProductUpdate2() throws Exception {
        final MetadataRepository repository = new MetadataRepository();
        repository.load(DocumentSaveTest.class.getResourceAsStream("metadata1.xsd"));

        SaverSource source = new TestSaverSource(repository, true, "test11_original.xml", "metadata1.xsd");

        SaverSession session = SaverSession.newSession(source);
        InputStream recordXml = DocumentSaveTest.class.getResourceAsStream("test11.xml");
        DocumentSaverContext context = session.getContextFactory().create("MDM", "DStar", "Source", recordXml, true, false);
        DocumentSaver saver = context.createSaver();
        saver.save(session, context);
        MockCommitter committer = new MockCommitter();
        session.end(committer);

        assertTrue(committer.hasSaved());
        Element committedElement = committer.getCommittedElement();
        assertEquals("222", evaluate(committedElement, "/Product/Price"));
        assertEquals("Small", evaluate(committedElement, "/Product/Features/Sizes/Size[1]"));
        assertEquals("", evaluate(committedElement, "/Product/Features/Sizes/Size[2]"));
        assertEquals("", evaluate(committedElement, "/Product/Features/Sizes/Size[3]"));
        assertEquals("White", evaluate(committedElement, "/Product/Features/Colors/Color[1]"));
        assertEquals("", evaluate(committedElement, "/Product/Features/Colors/Color[2]"));
        assertEquals("", evaluate(committedElement, "/Product/Features/Colors/Color[3]"));
    }

    public void testInheritance() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        repository.load(DocumentSaveTest.class.getResourceAsStream("metadata2.xsd"));

        SaverSource source = new TestSaverSource(repository, true, "test12_original.xml", "metadata1.xsd");

        SaverSession session = SaverSession.newSession(source);
        InputStream recordXml = DocumentSaveTest.class.getResourceAsStream("test12.xml");
        DocumentSaverContext context = session.getContextFactory().create("MDM", "DStar", "Source", recordXml, true, false);
        DocumentSaver saver = context.createSaver();
        saver.save(session, context);
        MockCommitter committer = new MockCommitter();
        session.end(committer);

        assertFalse(committer.hasSaved()); // Should be true once there are actual changes in test12.xml
    }

    public void testBeforeSavingWithAlterRecord() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        repository.load(DocumentSaveTest.class.getResourceAsStream("metadata1.xsd"));

        //
        boolean isOK = true;
        boolean newOutput = true;
        SaverSource source = new AlterRecordTestSaverSource(repository, false, "test1_original.xml", isOK, newOutput);

        SaverSession session = SaverSession.newSession(source);
        InputStream recordXml = DocumentSaveTest.class.getResourceAsStream("test1.xml");
        DocumentSaverContext context = session.getContextFactory().create("MDM", "DStar", "Source", recordXml, true, true);
        DocumentSaver saver = context.createSaver();
        saver.save(session, context);
        assertEquals("change the value successfully!", saver.getBeforeSavingMessage());
        MockCommitter committer = new MockCommitter();
        session.end(committer);
        assertTrue(committer.hasSaved());

        //
        isOK = false;
        newOutput = true;
        source = new AlterRecordTestSaverSource(repository, false, "test1_original.xml", isOK, newOutput);

        session = SaverSession.newSession(source);
        recordXml = DocumentSaveTest.class.getResourceAsStream("test1.xml");
        context = session.getContextFactory().create("MDM", "DStar", "Source", recordXml, true, true);
        saver = context.createSaver();
        try {
            saver.save(session, context);
            fail("Expected an exception.");
        } catch (SaveException e) {
            // Expected
            assertEquals("change the value failed!", e.getBeforeSavingMessage());
        }
        committer = new MockCommitter();
        session.end(committer);
        assertFalse(committer.hasSaved());

        //
        isOK = false;
        newOutput = false;
        source = new AlterRecordTestSaverSource(repository, false, "test1_original.xml", isOK, newOutput);

        session = SaverSession.newSession(source);
        recordXml = DocumentSaveTest.class.getResourceAsStream("test1.xml");
        context = session.getContextFactory().create("MDM", "DStar", "Source", recordXml, true, true);
        saver = context.createSaver();
        try {
            saver.save(session, context);
            fail("Expected an exception.");
        } catch (SaveException e) {
            // Expected
            assertEquals("change the value failed!", e.getBeforeSavingMessage());
        }
        committer = new MockCommitter();
        session.end(committer);
        assertFalse(committer.hasSaved());

        //
        isOK = true;
        newOutput = false;
        source = new AlterRecordTestSaverSource(repository, false, "test1_original.xml", isOK, newOutput);

        session = SaverSession.newSession(source);
        recordXml = DocumentSaveTest.class.getResourceAsStream("test1.xml");
        context = session.getContextFactory().create("MDM", "DStar", "Source", recordXml, true, true);
        saver = context.createSaver();
        saver.save(session, context);
        assertEquals("change the value successfully!", saver.getBeforeSavingMessage());
        committer = new MockCommitter();
        session.end(committer);
        assertTrue(committer.hasSaved());
    }

    public void testCreatePerformance() throws Exception {
        final MetadataRepository repository = new MetadataRepository();
        repository.load(DocumentSaveTest.class.getResourceAsStream("metadata1.xsd"));

        SaverSource source = new TestSaverSource(repository, false, "test1_original.xml", "metadata1.xsd");
        {
            SaverSession session = SaverSession.newSession(source);
            {
                for (int i = 0; i < 10; i++) {
                    InputStream recordXml = DocumentSaveTest.class.getResourceAsStream("test1.xml");
                    DocumentSaverContext context = session.getContextFactory().create("MDM", "DStar", "Source", recordXml, true,
                            false);
                    DocumentSaver saver = context.createSaver();
                    saver.save(session, context);
                }
            }
            session.end(new MockCommitter());
        }

        long saveTime = System.currentTimeMillis();
        long max = 0;
        long min = Long.MAX_VALUE;
        {
            SaverSession session = SaverSession.newSession(source);
            {
                for (int i = 0; i < 200; i++) {
                    long singleExecTime = System.currentTimeMillis();
                    {
                        InputStream recordXml = DocumentSaveTest.class.getResourceAsStream("test1.xml");
                        DocumentSaverContext context = session.getContextFactory().create("MDM", "DStar", "Source", recordXml,
                                true, false);
                        DocumentSaver saver = context.createSaver();
                        saver.save(session, context);
                    }
                    singleExecTime = (System.currentTimeMillis() - singleExecTime);
                    if (singleExecTime > max) {
                        max = singleExecTime;
                    }
                    if (singleExecTime < min) {
                        min = singleExecTime;
                    }
                }
            }
            session.end(new MockCommitter());
        }
        LOG.info("Time (mean): " + (System.currentTimeMillis() - saveTime) / 200f + " ms.");
        LOG.info("Time (min): " + min);
        LOG.info("Time (max): " + max);
    }

    public void testUpdatePerformance() throws Exception {
        final MetadataRepository repository = new MetadataRepository();
        repository.load(DocumentSaveTest.class.getResourceAsStream("metadata1.xsd"));

        SaverSource source = new TestSaverSource(repository, true, "test1_original.xml", "metadata1.xsd");
        {
            SaverSession session = SaverSession.newSession(source);
            {
                for (int i = 0; i < 10; i++) {
                    InputStream recordXml = DocumentSaveTest.class.getResourceAsStream("test1.xml");
                    DocumentSaverContext context = session.getContextFactory().create("MDM", "DStar", "Source", recordXml, true,
                            false);
                    DocumentSaver saver = context.createSaver();
                    saver.save(session, context);
                }
            }
            session.end(new MockCommitter());
        }

        long saveTime = System.currentTimeMillis();
        long max = 0;
        long min = Long.MAX_VALUE;
        {
            SaverSession session = SaverSession.newSession(source);
            {
                for (int i = 0; i < 200; i++) {
                    long singleExecTime = System.currentTimeMillis();
                    {
                        InputStream recordXml = DocumentSaveTest.class.getResourceAsStream("test1.xml");
                        DocumentSaverContext context = session.getContextFactory().create("MDM", "DStar", "Source", recordXml,
                                true, false);
                        DocumentSaver saver = context.createSaver();
                        saver.save(session, context);
                    }
                    singleExecTime = (System.currentTimeMillis() - singleExecTime);
                    if (singleExecTime > max) {
                        max = singleExecTime;
                    }
                    if (singleExecTime < min) {
                        min = singleExecTime;
                    }
                }
            }
            session.end(new MockCommitter());
        }
        LOG.info("Time (mean): " + (System.currentTimeMillis() - saveTime) / 200f + " ms.");
        LOG.info("Time (min): " + min);
        LOG.info("Time (max): " + max);
    }

    public void testConcurrentUpdates() throws Exception {
        final MetadataRepository repository = new MetadataRepository();
        repository.load(DocumentSaveTest.class.getResourceAsStream("metadata1.xsd"));
        SaverSource source = new TestSaverSource(repository, true, "test1_original.xml", "metadata1.xsd");

        Set<UpdateRunnable> updateRunnables = new HashSet<UpdateRunnable>();
        for (int i = 0; i < 10; i++) {
            updateRunnables.add(new UpdateRunnable(source));
        }
        Set<Thread> updateThreads = new HashSet<Thread>();
        for (Runnable updateRunnable : updateRunnables) {
            updateThreads.add(new Thread(updateRunnable));
        }
        for (Thread updateThread : updateThreads) {
            updateThread.start();
        }
        for (Thread updateThread : updateThreads) {
            updateThread.join();
        }

        for (UpdateRunnable updateRunnable : updateRunnables) {
            assertTrue(updateRunnable.isSuccess());
        }
    }


    private static class MockCommitter implements SaverSession.Committer {

        private Element committedElement;

        private boolean hasSaved = false;

        public void begin(String dataCluster) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Start on '" + dataCluster + "'");
            }
        }

        public void commit(String dataCluster) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Commit on '" + dataCluster + "'");
            }
        }

        public void save(ItemPOJO item, String revisionId) {
            hasSaved = true;
            try {
                committedElement = item.getProjection();
            } catch (XtentisException e) {
                throw new RuntimeException(e);
            }
            if (LOG.isDebugEnabled()) {
                try {
                    LOG.debug(item.getProjectionAsString());
                } catch (XtentisException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public void rollback(String dataCluster) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Rollback on '" + dataCluster + "'");
            }
        }

        public Element getCommittedElement() {
            return committedElement;
        }

        public boolean hasSaved() {
            return hasSaved;
        }
    }

    private static class TestSaverSource implements SaverSource {

        private final MetadataRepository repository;

        private final boolean exist;

        private final String originalDocumentFileName;

        private MetadataRepository updateReportRepository;

        private String userName = "User";

        private String lastInvalidatedTypeCache;

        private final String schemaFileName;

        public TestSaverSource(MetadataRepository repository, boolean exist, String originalDocumentFileName, String schemaFileName) {
            this.repository = repository;
            this.exist = exist;
            this.originalDocumentFileName = originalDocumentFileName;
            this.schemaFileName = schemaFileName;
        }

        public InputStream get(String dataClusterName, String typeName, String revisionId, String[] key) {
            return DocumentSaveTest.class.getResourceAsStream(originalDocumentFileName);
        }

        public boolean exist(String dataCluster, String typeName, String revisionId, String[] key) {
            return exist;
        }

        public synchronized MetadataRepository getMetadataRepository(String dataModelName) {
            if ("UpdateReport".equals(dataModelName)) {
                if (updateReportRepository == null) {
                    updateReportRepository = new MetadataRepository();
                    updateReportRepository.load(DocumentSaveTest.class.getResourceAsStream("updateReport.xsd"));
                }
                return updateReportRepository;
            }
            return repository;
        }

        public InputStream getSchema(String dataModelName) {
            return DocumentSaveTest.class.getResourceAsStream(schemaFileName);
        }

        public String getUniverse() {
            return "Universe";
        }

        public OutputReport invokeBeforeSaving(DocumentSaverContext context, MutableDocument updateReportDocument) {
            String message = "<report><message type=\"info\">change the value successfully!</message></report>";
            return new OutputReport(message, null);
        }

        public Set<String> getCurrentUserRoles() {
            if ("User".equals(userName)) {
                return Collections.singleton("User");
            } else {
                return Collections.singleton("System_Admin");
            }
        }

        public String getUserName() {
            return userName;
        }

        public boolean existCluster(String revisionID, String dataClusterName) {
            return true;
        }

        public String getConceptRevisionID(String typeName) {
            return "HEAD";
        }

        public void resetLocalUsers() {
        }

        public void initAutoIncrement() {
        }

        public void routeItem(String dataCluster, String typeName, String[] id) {
        }

        public void invalidateTypeCache(String dataModelName) {
            lastInvalidatedTypeCache = dataModelName;
        }

        public String getLastInvalidatedTypeCache() {
            return lastInvalidatedTypeCache;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }

    private static class AlterRecordTestSaverSource extends DocumentSaveTest.TestSaverSource {

        private final boolean OK;

        private final boolean newOutput;

        public AlterRecordTestSaverSource(MetadataRepository repository, boolean exist, String fileName, boolean OK,
                                          boolean newOutput) {
            super(repository, exist, fileName, "metadata1.xsd");
            this.OK = OK;
            this.newOutput = newOutput;
        }

        @Override
        public OutputReport invokeBeforeSaving(DocumentSaverContext context, MutableDocument updateReportDocument) {
            String message = "<report><message type=\"info\">change the value successfully!</message></report>";
            if (!OK) {
                message = "<report><message type=\"error\">change the value failed!</message></report>";
            }
            String item = null;
            OutputReport report = new OutputReport(message, item);

            if (newOutput) {
                item = "<exchange><item>" + "<Agency><Id>1</Id><Name>beforeSaving_Agency</Name></Agency></item></exchange>";
                report.setItem(item);
            }
            return report;
        }
    }

    private static class UpdateRunnable implements Runnable {

        private final SaverSource source;

        private boolean success = false;

        public UpdateRunnable(SaverSource source) {
            this.source = source;
        }

        public void run() {
            SaverSession session = SaverSession.newSession(source);
            {
                for (int i = 0; i < 100; i++) {
                    InputStream recordXml = DocumentSaveTest.class.getResourceAsStream("test1.xml");
                    DocumentSaverContext context = session.getContextFactory().create("MDM", "DStar", "Source", recordXml,
                            true, false);
                    DocumentSaver saver = context.createSaver();
                    saver.save(session, context);
                }
            }
            session.end(new MockCommitter());
            success = true;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    private static class TestNamespaceContext implements NamespaceContext {

        private Map<String, String> declaredPrefix = new HashMap<String, String>();

        private TestNamespaceContext() {
            declaredPrefix.put("xsi", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
        }

        public String getNamespaceURI(String prefix) {
            return declaredPrefix.get(prefix);
        }

        public String getPrefix(String namespaceURI) {
            Set<Map.Entry<String, String>> entries = declaredPrefix.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                if (entry.getValue().equals(namespaceURI)) {
                    return entry.getKey();
                }
            }
            return null;
        }

        public Iterator getPrefixes(String namespaceURI) {
            return declaredPrefix.keySet().iterator();
        }
    }
}
