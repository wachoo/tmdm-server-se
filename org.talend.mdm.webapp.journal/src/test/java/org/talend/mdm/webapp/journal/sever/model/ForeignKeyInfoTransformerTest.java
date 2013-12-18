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
package org.talend.mdm.webapp.journal.sever.model;

import static org.mockito.Mockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.*;
import static org.powermock.api.support.membermodification.MemberModifier.*;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit3.PowerMockSuite;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.mdm.webapp.journal.server.model.ForeignKeyInfoTransformer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.local.ItemCtrl2Local;
import com.amalto.core.history.DocumentTransformer;
import com.amalto.core.history.EmptyDocument;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.save.DOMDocument;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

@SuppressWarnings("nls")
@PrepareForTest({ ItemCtrl2Local.class, Util.class })
public class ForeignKeyInfoTransformerTest extends TestCase {

    private DocumentBuilderFactory documentBuilderFactory;

    final String clusterName = "TestFKs";

    final String modelName = "TestFKs";

    MetadataRepository metadataRepository;

    String revisionId;

    ComplexTypeMetadata typeMetadata;

    com.amalto.core.history.Document document;

    com.amalto.core.history.Document transformedDocument;

    org.w3c.dom.Document dataExpected;

    DocumentTransformer transformer;

    Map<String, String> xmlDomRecordInputs = new HashMap<String, String>();

    Map<String, String> xmlDomRecordOutputs = new HashMap<String, String>();

    Map<String, String> fkPaths = new HashMap<String, String>();

    Map<String, ItemPOJO> itemPOJOs = new HashMap<String, ItemPOJO>();

    ItemCtrl2Local itemCtrl2Local = null;

    @SuppressWarnings("unchecked")
    public static TestSuite suite() throws Exception {
        return new PowerMockSuite("Unit tests for " + ForeignKeyInfoTransformerTest.class.getSimpleName(),
                ForeignKeyInfoTransformerTest.class);
    }

    @Override
    protected void setUp() throws Exception {

        initData();

        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setValidating(false);

        InputStream dataExpectedStream = this.getClass().getResourceAsStream("dataExpected.xml");
        dataExpected = documentBuilderFactory.newDocumentBuilder().parse(new InputSource(dataExpectedStream));

        InputStream dataModelStream = this.getClass().getResourceAsStream("TestFKs.xsd");
        metadataRepository = new MetadataRepository();
        metadataRepository.load(dataModelStream);

        itemCtrl2Local = PowerMockito.mock(ItemCtrl2Local.class);

        stub(method(Util.class, "getItemCtrl2Local")).toReturn(itemCtrl2Local);

        String[] fks = new String[] { "b1", "b2", "c1", "c2", "e1" };

        ItemPOJO item = null;
        for (String fk : fks) {
            item = PowerMockito.mock(ItemPOJO.class);
            itemPOJOs.put(fk, item);
            when(itemCtrl2Local.getItem(getItemPOJOPK(fk))).thenReturn(itemPOJOs.get(fk));
            when(itemPOJOs.get(fk).getProjection()).thenReturn(getElement(fk));
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        metadataRepository.close();
        metadataRepository = null;
    }

    public void testCase0_FK_defined_in_anonymoustype_and_fKInfo_directly_under_root() {
        String recordId = "d1";
        String conceptName = "D";
        executeTestFor(recordId, conceptName);
    }

    public void testCase1_FK_defined_in_anonymoustype_fKInfo_indirectly_under_root() {
        String recordId = "a1";
        String conceptName = "A";
        executeTestFor(recordId, conceptName);
    }

    public void testCase2_FK_defined_in_reusabletype_fKInfo_indirectly_under_root() {
        String recordId = "a2";
        String conceptName = "A";
        executeTestFor(recordId, conceptName);
    }

    public void testCase3_Collection_anonymoustype_FK_defined_in_anonymoustype() {
        String recordId = "a3";
        String conceptName = "A";
        executeTestFor(recordId, conceptName);
    }

    public void testCase4_Collection_reusabletype_FK_defined_in_anonymoustype() {
        String recordId = "a4";
        String conceptName = "A";
        executeTestFor(recordId, conceptName);
    }

    public void testCase5_Collection_anonymoustype_FK_defined_in_reusabletype() {
        String recordId = "a5";
        String conceptName = "A";
        executeTestFor(recordId, conceptName);
    }

    public void testCase6_Collection_reusabletype_FK_defined_in_reusabletype() {
        String recordId = "a6";
        String conceptName = "A";
        executeTestFor(recordId, conceptName);
    }

    public void testCase7_subElementD1_as_anonymoustype_FK_defined_in_anonymoustype() {
        String recordId = "a7";
        String conceptName = "A";
        executeTestFor(recordId, conceptName);
    }

    public void testCase8_subElementD2_as_reusabletype__FK_defined_in_anonymoustype() {
        String recordId = "a8";
        String conceptName = "A";
        executeTestFor(recordId, conceptName);
    }

    public void testCase9_subElementE1_as_anonymoustype_FK_defined_in_reusabletype() {
        String recordId = "a9";
        String conceptName = "A";
        executeTestFor(recordId, conceptName);
    }

    public void testCase10_subElementE2_as_reusabletype_FK_defined_in_reusabletype() {
        String recordId = "a10";
        String conceptName = "A";
        executeTestFor(recordId, conceptName);
    }

    private void executeTestFor(String id, String concept) {
        typeMetadata = (ComplexTypeMetadata) metadataRepository.getType(concept);

        document = new DOMDocument(getDocument(id), typeMetadata, revisionId, clusterName, modelName);

        transformer = new ForeignKeyInfoTransformer(typeMetadata, clusterName);
        ((ForeignKeyInfoTransformer) transformer).setMetadataRepository(metadataRepository);

        transformedDocument = document.transform(transformer);

        String path = fkPaths.get(id);
        List<String> actualFKInfo = extractFKInfo(((DOMDocument) transformedDocument).asDOM(), path);

        List<String> expectedFKInfo = extractFKInfo(dataExpected, path);

        assertEquals(actualFKInfo.size(), expectedFKInfo.size());

        for (int i = 0; i < expectedFKInfo.size(); i++) {
            assertTrue(expectedFKInfo.get(i).contains(actualFKInfo.get(i)));
            assertTrue(expectedFKInfo.get(i).equals(actualFKInfo.get(i)));
        }
    }

    private List<String> extractFKInfo(org.w3c.dom.Document doc, String path) {
        NodeList nodeList;
        List<String> fieldValues = new ArrayList<String>();
        try {
            nodeList = Util.getNodeList(doc, path);
            for (int i = 0; i < nodeList.getLength(); i++) {
                fieldValues.add(nodeList.item(i).getNodeValue());
            }
        } catch (XtentisException e) {
            new RuntimeException("Exception during extracting element value", e);
        }
        return fieldValues;
    }

    private org.w3c.dom.Document getDocument(String id) {
        String xmlString = xmlDomRecordInputs.get(id);
        org.w3c.dom.Document documentAsDOM;
        if (xmlString != null) {
            try {
                documentAsDOM = documentBuilderFactory.newDocumentBuilder().parse(new InputSource(new StringReader(xmlString)));
            } catch (Exception e) {
                throw new RuntimeException("Exception during initial content build", e);
            }
        } else {
            documentAsDOM = EmptyDocument.EMPTY_DOCUMENT;
        }
        return documentAsDOM;
    }

    private ItemPOJOPK getItemPOJOPK(String fk) {
        ItemPOJOPK pk = new ItemPOJOPK();
        String referencedTypeName = null;
        char c = fk.charAt(0);
        switch (c) {
        case 'b':
            referencedTypeName = "B";
            break;
        case 'c':
            referencedTypeName = "C";
            break;
        case 'e':
            referencedTypeName = "E";
            break;
        }

        pk.setConceptName(referencedTypeName);
        pk.setDataClusterPOJOPK(new DataClusterPOJOPK(clusterName));
        String[] key = new String[] { fk };
        pk.setIds(key);

        return pk;
    }

    private Element getElement(String id) {
        String recordXML = xmlDomRecordInputs.get(id);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document doc = null;
        Element root = null;
        try {
            builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(recordXML));

            doc = builder.parse(inputSource);
            root = doc.getDocumentElement();
        } catch (Exception e) {
            throw new RuntimeException("Exception during initial content build", e);
        }

        return root;
    }

    private void initData() {
        // setup xpaths to FK fields
        fkPaths.put("d1", "//D[D_Id='d1']/FK_to_E/text()");
        fkPaths.put("a1", "//A[A_Id='a1']/FK_to_B/text()");
        fkPaths.put("a2", "//A[A_Id='a2']/FK_to_C/text()");
        fkPaths.put("a3", "//A[A_Id='a3']/BCollection1/B/text()");
        fkPaths.put("a4", "//A[A_Id='a4']/BCollection2/B/text()");
        fkPaths.put("a5", "//A[A_Id='a5']/CCollection1/C/text()");
        fkPaths.put("a6", "//A[A_Id='a6']/CCollection2/C/text()");
        fkPaths.put("a7", "//A[A_Id='a7']/D1/B/text()");
        fkPaths.put("a8", "//A[A_Id='a8']/D2/B/text()");
        fkPaths.put("a9", "//A[A_Id='a9']/E1/C/text()");
        fkPaths.put("a10", "//A[A_Id='a10']/E2/C/text()");

        // entity data to be referenced by other entity data
        xmlDomRecordInputs.put("e1", "<E><E_Id>e1</E_Id><E_Name>ename1</E_Name><E_Title>etitle1</E_Title></E>");
        xmlDomRecordInputs
                .put("b1",
                        "<B><B_Id>b1</B_Id><B_Name>bname1</B_Name><B_Title>btitle1</B_Title><B_SubInfo><B_SubInfo_Id>bsubId1</B_SubInfo_Id><B_Sub_Name>bsubname1</B_Sub_Name><B_Sub_Title>bsubtitle1</B_Sub_Title></B_SubInfo></B>");
        xmlDomRecordInputs
                .put("b2",
                        "<B><B_Id>b2</B_Id><B_Name>bname2</B_Name><B_Title>btitle2</B_Title><B_SubInfo><B_SubInfo_Id>bsubId2</B_SubInfo_Id><B_Sub_Name>bsubname2</B_Sub_Name><B_Sub_Title>bsubtitle2</B_Sub_Title></B_SubInfo></B>");
        xmlDomRecordInputs
                .put("c1",
                        "<C><C_Id>c1</C_Id><C_Name>cname1</C_Name><C_Title>ctitle1</C_Title><C_SubInfo><C_SubInfo_Id>csubId1</C_SubInfo_Id><C_Sub_Name>csubname1</C_Sub_Name><C_Sub_Title>csubtitle1</C_Sub_Title></C_SubInfo></C>");
        xmlDomRecordInputs
                .put("c2",
                        "<C><C_Id>c2</C_Id><C_Name>cname2</C_Name><C_Title>ctitle2</C_Title><C_SubInfo><C_SubInfo_Id>csubId2</C_SubInfo_Id><C_Sub_Name>csubname2</C_Sub_Name><C_Sub_Title>csubtitle2</C_Sub_Title></C_SubInfo></C>");

        // case 0:
        xmlDomRecordInputs.put("d1", "<D><D_Id>d1</D_Id><D_Name>dName1</D_Name><FK_to_E>[e1]</FK_to_E></D>");
        // case 1:
        xmlDomRecordInputs.put("a1", "<A><A_Id>a1</A_Id><A_Name>aName1</A_Name><FK_to_B>[b1]</FK_to_B></A>");
        // case 2:
        xmlDomRecordInputs.put("a2", "<A><A_Id>a2</A_Id><A_Name>aName1</A_Name><FK_to_C>[c1]</FK_to_C></A>");
        // case 3:
        xmlDomRecordInputs.put("a3",
                "<A><A_Id>a3</A_Id><A_Name>aname1</A_Name><BCollection1><B>[b1]</B><B>[b2]</B></BCollection1></A>");
        // case 4:
        xmlDomRecordInputs.put("a4",
                "<A><A_Id>a4</A_Id><A_Name>aname1</A_Name><BCollection2><B>[b1]</B><B>[b2]</B></BCollection2></A>");
        // case 5:
        xmlDomRecordInputs.put("a5",
                "<A><A_Id>a5</A_Id><A_Name>aname1</A_Name><CCollection1><C>[c1]</C><C>[c2]</C></CCollection1></A>");
        // case 6:
        xmlDomRecordInputs.put("a6",
                "<A><A_Id>a6</A_Id><A_Name>aname1</A_Name><CCollection2><C>[c1]</C><C>[c2]</C></CCollection2></A>");
        // case 7:
        xmlDomRecordInputs.put("a7", "<A><A_Id>a7</A_Id><A_Name>aname1</A_Name><D1><B>[b1]</B></D1></A>");
        // case 8:
        xmlDomRecordInputs.put("a8", "<A><A_Id>a8</A_Id><A_Name>aname1</A_Name><D2><B>[b1]</B></D2></A>");
        // case 9:
        xmlDomRecordInputs.put("a9", "<A><A_Id>a9</A_Id><A_Name>aname1</A_Name><E1><C>[c1]</C></E1></A>");
        // case 10:
        xmlDomRecordInputs.put("a10", "<A><A_Id>a10</A_Id><A_Name>aname1</A_Name><E2><C>[c1]</C></E2></A>");
    }
}
