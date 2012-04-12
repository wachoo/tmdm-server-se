package org.talend.mdm.webapp.browserecords.server.ruleengine;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.QName;
import org.talend.mdm.webapp.base.shared.SimpleTypeModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;

public class DisplayRuleTest extends TestCase {

    // TODO --------------------test default value rule---------------------
    public void test_Basic_DefaultValue_Rule() {
        // TODO test basic default value rule;
        Map<String, TypeModel> metaDatas = DisplayRuleTestData.getMetaData("DefaultValueModel.xml", "TestDefaultModel"); //$NON-NLS-1$ //$NON-NLS-2$
        DisplayRuleEngine engine = new DisplayRuleEngine(metaDatas, "TestDefaultModel"); //$NON-NLS-1$
        Document dom4jDoc = DisplayRuleTestData.getDocument("DefaultValueRecord.xml"); //$NON-NLS-1$
        List<RuleValueItem> ruleValueItems = engine.execDefaultValueRule(dom4jDoc);

        assertEquals(ruleValueItems.size(), 3);

        assertEquals(ruleValueItems.get(0).getXpath(), "TestDefaultModel/name[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(0).getValue(), "zhang"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(1).getXpath(), "TestDefaultModel/cp[1]/title[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(1).getValue(), "hello"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(2).getXpath(), "TestDefaultModel/cp[1]/content[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(2).getValue(), "3"); //$NON-NLS-1$

    }

     public void test_Basic_DefaultValue_Rule_WithFunction() {
        // TODO test basic default value rule with xslt function

        Map<String, TypeModel> metaDatas = DisplayRuleTestData.getMetaData("DefaultValueWithFunctionModel.xml", "TestDefaultModel"); //$NON-NLS-1$ //$NON-NLS-2$
        DisplayRuleEngine engine = new DisplayRuleEngine(metaDatas, "TestDefaultModel"); //$NON-NLS-1$
        Document dom4jDoc = DisplayRuleTestData.getDocument("DefaultValueRecord.xml"); //$NON-NLS-1$
        List<RuleValueItem> ruleValueItems = engine.execDefaultValueRule(dom4jDoc);

        assertEquals(ruleValueItems.size(), 3);

        assertEquals(ruleValueItems.get(0).getXpath(), "TestDefaultModel/name[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(0).getValue(), "zhang"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(1).getXpath(), "TestDefaultModel/cp[1]/title[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(1).getValue(), "helloworld!!!"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(2).getXpath(), "TestDefaultModel/cp[1]/content[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(2).getValue(), "10"); //$NON-NLS-1$
    }

    public void test_Basic_DefaultValue_Rule_WithFunctionAndXPath() {
        // TODO test basic default value rule with xslt function and xpath

        Map<String, TypeModel> metaDatas = DisplayRuleTestData.getMetaData(
                "DefaultRuleWithFunctionAndXPath.xml", "DefaultRuleWithFunctionAndXPath"); //$NON-NLS-1$ //$NON-NLS-2$
        DisplayRuleEngine engine = new DisplayRuleEngine(metaDatas, "DefaultRuleWithFunctionAndXPath"); //$NON-NLS-1$
        Document dom4jDoc = DisplayRuleTestData.getDocument("DefaultRuleWithFunctionAndXPathRecord.xml"); //$NON-NLS-1$
        List<RuleValueItem> ruleValueItems = engine.execDefaultValueRule(dom4jDoc);

        assertEquals(ruleValueItems.size(), 3);

        assertEquals(ruleValueItems.get(0).getXpath(), "DefaultRuleWithFunctionAndXPath/detail[1]/name[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(0).getValue(), "zhang yang"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(1).getXpath(), "DefaultRuleWithFunctionAndXPath/title[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(1).getValue(), "detail name is [zhang yang]"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(2).getXpath(), "DefaultRuleWithFunctionAndXPath/detail[1]/content[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(2).getValue(), "name is [zhang yang] title is [detail name is [zhang yang]]"); //$NON-NLS-1$
    }

    public void test_DefaultValueForBoolean() {
        // TODO test default rule for boolean type

        Map<String, TypeModel> metaDatas = DisplayRuleTestData.getMetaData("DefaultValueForBoolean.xml", "TestBoolean"); //$NON-NLS-1$ //$NON-NLS-2$
        DisplayRuleEngine engine = new DisplayRuleEngine(metaDatas, "TestBoolean"); //$NON-NLS-1$
        Document dom4jDoc = DisplayRuleTestData.getDocument("DefaultValueForBooleanRecord.xml"); //$NON-NLS-1$
        List<RuleValueItem> ruleValueItems = engine.execDefaultValueRule(dom4jDoc);

        assertEquals(ruleValueItems.size(), 5);

        assertEquals(ruleValueItems.get(0).getXpath(), "TestBoolean/name[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(0).getValue(), "zhang"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(1).getXpath(), "TestBoolean/finished[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(1).getValue(), "true"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(2).getXpath(), "TestBoolean/finished1[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(2).getValue(), "false"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(3).getXpath(), "TestBoolean/finished2[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(3).getValue(), "true"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(4).getXpath(), "TestBoolean/finished3[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(4).getValue(), "false"); //$NON-NLS-1$
    }

     public void test_DefaultValueForEnumeration() {
        // TODO test default rule for enumeration

        Map<String, TypeModel> metaDatas = DisplayRuleTestData.getMetaData("DefaultValueForEnumeration.xml", "TestEnumeration"); //$NON-NLS-1$ //$NON-NLS-2$
        DisplayRuleEngine engine = new DisplayRuleEngine(metaDatas, "TestEnumeration"); //$NON-NLS-1$
        Document dom4jDoc = DisplayRuleTestData.getDocument("DefaultValueForEnumerationRecord.xml"); //$NON-NLS-1$
        List<RuleValueItem> ruleValueItems = engine.execDefaultValueRule(dom4jDoc);

        assertEquals(ruleValueItems.size(), 4);

        assertEquals(ruleValueItems.get(0).getXpath(), "TestEnumeration/name[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(0).getValue(), "zhang"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(1).getXpath(), "TestEnumeration/age[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(1).getValue(), "21-30"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(2).getXpath(), "TestEnumeration/favorite[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(2).getValue(), "Orange"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(3).getXpath(), "TestEnumeration/num[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(3).getValue(), "6"); //$NON-NLS-1$
    }

    public void test_Multiple_Occurence_DefaultValueRule() {
        // TODO test Multiple occurence default rule

        Map<String, TypeModel> metaDatas = DisplayRuleTestData.getMetaData("MultipleOccurenceDefaultValueRule.xml", "MultipleOccurence"); //$NON-NLS-1$ //$NON-NLS-2$
        DisplayRuleEngine engine = new DisplayRuleEngine(metaDatas, "MultipleOccurence"); //$NON-NLS-1$
        Document dom4jDoc = DisplayRuleTestData.getDocument("MultipleOccurenceDefaultValueRuleRecord.xml"); //$NON-NLS-1$
        List<RuleValueItem> ruleValueItems = engine.execDefaultValueRule(dom4jDoc);

        assertEquals(ruleValueItems.size(), 5);

        assertEquals(ruleValueItems.get(0).getXpath(), "MultipleOccurence/cp[1]/address[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(0).getValue(), "hello this is address"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(1).getXpath(), "MultipleOccurence/cp[1]/address[2]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(1).getValue(), "hello this is address"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(2).getXpath(), "MultipleOccurence/cp[1]/address[3]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(2).getValue(), "hello this is address"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(3).getXpath(), "MultipleOccurence/tel[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(3).getValue(), "phone: 1323234323"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(4).getXpath(), "MultipleOccurence/tel[2]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(4).getValue(), "phone: 1323234323"); //$NON-NLS-1$
    }

    public void test_DefaultRuleForInheritance() {
        // TODO test default rule for inheritance

        Map<String, TypeModel> metaDatas = DisplayRuleTestData.getMetaData("DefaultRuleForInheritance.xml", "DefaultRuleForInheritance"); //$NON-NLS-1$ //$NON-NLS-2$

        SimpleTypeModel personNameType = new SimpleTypeModel();
        personNameType.setXpath("DefaultRuleForInheritance/person/name"); //$NON-NLS-1$
        metaDatas.put("DefaultRuleForInheritance/person/name", personNameType); //$NON-NLS-1$

        ComplexTypeModel person_StudentType = new ComplexTypeModel();
        person_StudentType.setXpath("DefaultRuleForInheritance/person/score"); //$NON-NLS-1$
        person_StudentType.setDefaultValueExpression("fn:concat(/DefaultRuleForInheritance/name,'''s score is 100')"); //$NON-NLS-1$
        metaDatas.put("DefaultRuleForInheritance/person/score", person_StudentType); //$NON-NLS-1$

        ComplexTypeModel person_TeacherType = new ComplexTypeModel();
        person_TeacherType.setXpath("DefaultRuleForInheritance/person/salary"); //$NON-NLS-1$
        person_TeacherType.setDefaultValueExpression("fn:concat(/DefaultRuleForInheritance/name,'''s salary is 1000')"); //$NON-NLS-1$
        metaDatas.put("DefaultRuleForInheritance/person/salary", person_TeacherType); //$NON-NLS-1$

        DisplayRuleEngine engine = new DisplayRuleEngine(metaDatas, "DefaultRuleForInheritance"); //$NON-NLS-1$
        Document dom4jDoc = DisplayRuleTestData.getDocument("DefaultRuleForInheritanceRecord.xml"); //$NON-NLS-1$
        List<RuleValueItem> ruleValueItems = engine.execDefaultValueRule(dom4jDoc);

        assertEquals(ruleValueItems.size(), 1);

        assertEquals(ruleValueItems.get(0).getXpath(), "DefaultRuleForInheritance/name[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(0).getValue(), "zhang yang"); //$NON-NLS-1$

        dom4jDoc.selectSingleNode(ruleValueItems.get(0).getXpath()).setText(ruleValueItems.get(0).getValue());

        changeDocForDefaultRuleForInheritance_Student(dom4jDoc);
        ruleValueItems = engine.execDefaultValueRule(dom4jDoc);

        assertEquals(ruleValueItems.size(), 2);

        assertEquals(ruleValueItems.get(0).getXpath(), "DefaultRuleForInheritance/name[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(0).getValue(), "zhang yang"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(1).getXpath(), "DefaultRuleForInheritance/person[1]/score[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(1).getValue(), "zhang yang's score is 100"); //$NON-NLS-1$

        changeDocForDefaultRuleForInheritance_Teacher(dom4jDoc);
        ruleValueItems = engine.execDefaultValueRule(dom4jDoc);

        assertEquals(ruleValueItems.size(), 2);

        assertEquals(ruleValueItems.get(0).getXpath(), "DefaultRuleForInheritance/name[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(0).getValue(), "zhang yang"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(1).getXpath(), "DefaultRuleForInheritance/person[1]/salary[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(1).getValue(), "zhang yang's salary is 1000"); //$NON-NLS-1$

    }

    private void changeDocForDefaultRuleForInheritance_Student(Document doc) {
        Element el = (Element) doc.selectSingleNode("/DefaultRuleForInheritance/person"); //$NON-NLS-1$

        Attribute attr = el.attribute(new QName("type", new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance"))); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        if (attr == null) {
            el.addAttribute(new QName("type", new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance")), "Student"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        } else {
            attr.setValue("Student"); //$NON-NLS-1$
        }
        el.clearContent();
        el.addElement("name"); //$NON-NLS-1$
        el.addElement("score"); //$NON-NLS-1$
    }

    private void changeDocForDefaultRuleForInheritance_Teacher(Document doc) {
        Element el = (Element) doc.selectSingleNode("/DefaultRuleForInheritance/person"); //$NON-NLS-1$
        Attribute attr = el.attribute(new QName("type", new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance"))); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        if (attr == null) {
            el.addAttribute(new QName("type", new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance")), "Teacher"); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
        } else {
            attr.setValue("Teacher"); //$NON-NLS-1$            
        }
        el.clearContent();
        el.addElement("name"); //$NON-NLS-1$
        el.addElement("salary"); //$NON-NLS-1$
    }

    // TODO ---------------------test visible rule---------------------------
    public void test_Basic_VisibleRule() {
        // TODO test basic visible rule
        Map<String, TypeModel> metaDatas = DisplayRuleTestData.getMetaData("BasicVisibleRule.xml", "BasicVisibleRule"); //$NON-NLS-1$ //$NON-NLS-2$
        DisplayRuleEngine engine = new DisplayRuleEngine(metaDatas, "BasicVisibleRule"); //$NON-NLS-1$
        Document dom4jDoc = DisplayRuleTestData.getDocument("BasicVisibleRuleRecord.xml"); //$NON-NLS-1$
        List<RuleValueItem> ruleValueItems = engine.execVisibleRule(dom4jDoc);

        assertEquals(ruleValueItems.size(), 4);

        assertEquals(ruleValueItems.get(0).getXpath(), "BasicVisibleRule/testfield[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(0).getValue(), "true"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(1).getXpath(), "BasicVisibleRule/cp[1]/address[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(1).getValue(), "false"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(2).getXpath(), "BasicVisibleRule/cp[1]/address[2]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(2).getValue(), "false"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(3).getXpath(), "BasicVisibleRule/cp[1]/address[3]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(3).getValue(), "false"); //$NON-NLS-1$


    }

    public void test_Basic_VisibleRule_Rule_WithFunction() {
        // TODO test visible rule with xslt function
        
        Map<String, TypeModel> metaDatas = DisplayRuleTestData.getMetaData("BasicVisibleRuleWithFunction.xml", "BasicVisibleRuleFunction"); //$NON-NLS-1$ //$NON-NLS-2$
        DisplayRuleEngine engine = new DisplayRuleEngine(metaDatas, "BasicVisibleRuleFunction"); //$NON-NLS-1$
        Document dom4jDoc = DisplayRuleTestData.getDocument("BasicVisibleRuleWithFunctionRecord.xml"); //$NON-NLS-1$
        List<RuleValueItem> ruleValueItems = engine.execVisibleRule(dom4jDoc);

        assertEquals(ruleValueItems.size(), 5);

        assertEquals(ruleValueItems.get(0).getXpath(), "BasicVisibleRuleFunction/testfield[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(0).getValue(), "false"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(1).getXpath(), "BasicVisibleRuleFunction/cp[1]/title[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(1).getValue(), "false"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(2).getXpath(), "BasicVisibleRuleFunction/cp[1]/address[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(2).getValue(), "true"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(3).getXpath(), "BasicVisibleRuleFunction/cp[1]/address[2]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(3).getValue(), "true"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(4).getXpath(), "BasicVisibleRuleFunction/cp[1]/address[3]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(4).getValue(), "true"); //$NON-NLS-1$

    }

    public void test_Basic_VisibleRule_Rule_WithFunctionXPath() {
        // TODO test visible rule with xslt function and xpath
        
        Map<String, TypeModel> metaDatas = DisplayRuleTestData.getMetaData("BasicVisibleRuleWithFunctionXPath.xml", "BasicVisibleRuleWithFunctionXPath"); //$NON-NLS-1$ //$NON-NLS-2$
        DisplayRuleEngine engine = new DisplayRuleEngine(metaDatas, "BasicVisibleRuleWithFunctionXPath"); //$NON-NLS-1$
        Document dom4jDoc = DisplayRuleTestData.getDocument("BasicVisibleRuleWithFunctionXPathRecord.xml"); //$NON-NLS-1$
        List<RuleValueItem> ruleValueItems = engine.execVisibleRule(dom4jDoc);

        assertEquals(ruleValueItems.size(), 5);

        assertEquals(ruleValueItems.get(0).getXpath(), "BasicVisibleRuleWithFunctionXPath/testfield[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(0).getValue(), "false"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(1).getXpath(), "BasicVisibleRuleWithFunctionXPath/cp[1]/title[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(1).getValue(), "false"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(2).getXpath(), "BasicVisibleRuleWithFunctionXPath/cp[1]/address[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(2).getValue(), "false"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(3).getXpath(), "BasicVisibleRuleWithFunctionXPath/cp[1]/address[2]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(3).getValue(), "false"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(4).getXpath(), "BasicVisibleRuleWithFunctionXPath/cp[1]/address[3]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(4).getValue(), "false"); //$NON-NLS-1$
        
        Node node = dom4jDoc.selectSingleNode("/BasicVisibleRuleWithFunctionXPath/name"); //$NON-NLS-1$
        node.setText("1234"); //$NON-NLS-1$
        ruleValueItems = engine.execVisibleRule(dom4jDoc);

        assertEquals(ruleValueItems.size(), 5);

        assertEquals(ruleValueItems.get(0).getXpath(), "BasicVisibleRuleWithFunctionXPath/testfield[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(0).getValue(), "true"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(1).getXpath(), "BasicVisibleRuleWithFunctionXPath/cp[1]/title[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(1).getValue(), "false"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(2).getXpath(), "BasicVisibleRuleWithFunctionXPath/cp[1]/address[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(2).getValue(), "false"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(3).getXpath(), "BasicVisibleRuleWithFunctionXPath/cp[1]/address[2]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(3).getValue(), "false"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(4).getXpath(), "BasicVisibleRuleWithFunctionXPath/cp[1]/address[3]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(4).getValue(), "false"); //$NON-NLS-1$


        node.setText("12345"); //$NON-NLS-1$
        ruleValueItems = engine.execVisibleRule(dom4jDoc);

        assertEquals(ruleValueItems.size(), 5);

        assertEquals(ruleValueItems.get(0).getXpath(), "BasicVisibleRuleWithFunctionXPath/testfield[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(0).getValue(), "true"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(1).getXpath(), "BasicVisibleRuleWithFunctionXPath/cp[1]/title[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(1).getValue(), "true"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(2).getXpath(), "BasicVisibleRuleWithFunctionXPath/cp[1]/address[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(2).getValue(), "false"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(3).getXpath(), "BasicVisibleRuleWithFunctionXPath/cp[1]/address[2]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(3).getValue(), "false"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(4).getXpath(), "BasicVisibleRuleWithFunctionXPath/cp[1]/address[3]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(4).getValue(), "false"); //$NON-NLS-1$

        node.setText("123456"); //$NON-NLS-1$
        ruleValueItems = engine.execVisibleRule(dom4jDoc);

        assertEquals(ruleValueItems.size(), 5);

        assertEquals(ruleValueItems.get(0).getXpath(), "BasicVisibleRuleWithFunctionXPath/testfield[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(0).getValue(), "true"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(1).getXpath(), "BasicVisibleRuleWithFunctionXPath/cp[1]/title[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(1).getValue(), "true"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(2).getXpath(), "BasicVisibleRuleWithFunctionXPath/cp[1]/address[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(2).getValue(), "true"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(3).getXpath(), "BasicVisibleRuleWithFunctionXPath/cp[1]/address[2]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(3).getValue(), "true"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(4).getXpath(), "BasicVisibleRuleWithFunctionXPath/cp[1]/address[3]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(4).getValue(), "true"); //$NON-NLS-1$

    }

    public void test_VisibleRule_UsingBoolean_Attribute() {
        // TODO Visibility rule using Boolean attribute
        
        Map<String, TypeModel> metaDatas = DisplayRuleTestData.getMetaData("VisibleRuleBoolean.xml", "VisibleRuleBoolean"); //$NON-NLS-1$ //$NON-NLS-2$
        DisplayRuleEngine engine = new DisplayRuleEngine(metaDatas, "VisibleRuleBoolean"); //$NON-NLS-1$
        Document dom4jDoc = DisplayRuleTestData.getDocument("VisibleRuleBooleanRecord.xml"); //$NON-NLS-1$
        List<RuleValueItem> ruleValueItems = engine.execVisibleRule(dom4jDoc);

        assertEquals(ruleValueItems.size(), 3);

        assertEquals(ruleValueItems.get(0).getXpath(), "VisibleRuleBoolean/finished[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(0).getValue(), "false"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(1).getXpath(), "VisibleRuleBoolean/cp[1]/finished1[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(1).getValue(), "true"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(2).getXpath(), "VisibleRuleBoolean/cp[1]/finished2[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(2).getValue(), "false"); //$NON-NLS-1$

    }

    public void test_VisibleRuleForInheritance() {
        // TODO test visible rule for inheritance

        Map<String, TypeModel> metaDatas = DisplayRuleTestData.getMetaData("VisibleRuleForInheritance.xml", "VisibleRuleForInheritance"); //$NON-NLS-1$ //$NON-NLS-2$
        //

        SimpleTypeModel personNameType = new SimpleTypeModel();
        personNameType.setXpath("VisibleRuleForInheritance/person/name"); //$NON-NLS-1$
        personNameType.setVisibleExpression("fn:string-length('hello world')>8"); //$NON-NLS-1$
        metaDatas.put("VisibleRuleForInheritance/person/name", personNameType); //$NON-NLS-1$

        ComplexTypeModel person_StudentType = new ComplexTypeModel();
        person_StudentType.setXpath("VisibleRuleForInheritance/person/score"); //$NON-NLS-1$
        person_StudentType.setVisibleExpression("fn:string-length('hello world')>15"); //$NON-NLS-1$
        metaDatas.put("VisibleRuleForInheritance/person/score", person_StudentType); //$NON-NLS-1$

        ComplexTypeModel person_TeacherType = new ComplexTypeModel();
        person_TeacherType.setXpath("VisibleRuleForInheritance/person/salary"); //$NON-NLS-1$
        person_TeacherType.setVisibleExpression("fn:string-length('hello world')>10"); //$NON-NLS-1$
        metaDatas.put("VisibleRuleForInheritance/person/salary", person_TeacherType); //$NON-NLS-1$

        DisplayRuleEngine engine = new DisplayRuleEngine(metaDatas, "VisibleRuleForInheritance"); //$NON-NLS-1$
        Document dom4jDoc = DisplayRuleTestData.getDocument("VisibleRuleForInheritanceRecord.xml"); //$NON-NLS-1$
        List<RuleValueItem> ruleValueItems = engine.execVisibleRule(dom4jDoc);

        assertEquals(ruleValueItems.size(), 2);

        assertEquals(ruleValueItems.get(0).getXpath(), "VisibleRuleForInheritance/testfield[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(0).getValue(), "true"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(1).getXpath(), "VisibleRuleForInheritance/person[1]/name[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(1).getValue(), "true"); //$NON-NLS-1$


        changeDocForVisibleRuleForInheritance_Student(dom4jDoc);

        ruleValueItems = engine.execVisibleRule(dom4jDoc);

        assertEquals(ruleValueItems.size(), 3);

        assertEquals(ruleValueItems.get(0).getXpath(), "VisibleRuleForInheritance/testfield[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(0).getValue(), "true"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(1).getXpath(), "VisibleRuleForInheritance/person[1]/name[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(1).getValue(), "true"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(2).getXpath(), "VisibleRuleForInheritance/person[1]/score[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(2).getValue(), "false"); //$NON-NLS-1$

        changeDocForVisibleRuleForInheritance_Teacher(dom4jDoc);
        ruleValueItems = engine.execVisibleRule(dom4jDoc);

        assertEquals(ruleValueItems.get(0).getXpath(), "VisibleRuleForInheritance/testfield[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(0).getValue(), "true"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(1).getXpath(), "VisibleRuleForInheritance/person[1]/name[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(1).getValue(), "true"); //$NON-NLS-1$

        assertEquals(ruleValueItems.get(2).getXpath(), "VisibleRuleForInheritance/person[1]/salary[1]"); //$NON-NLS-1$
        assertEquals(ruleValueItems.get(2).getValue(), "true"); //$NON-NLS-1$


    }

    private void changeDocForVisibleRuleForInheritance_Student(Document doc) {
        Element el = (Element) doc.selectSingleNode("/VisibleRuleForInheritance/person"); //$NON-NLS-1$
        Attribute attr = el.attribute(new QName("type", new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance"))); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        attr.setValue("Student"); //$NON-NLS-1$
        el.clearContent();
        el.addElement("name"); //$NON-NLS-1$
        el.addElement("score"); //$NON-NLS-1$
    }

    private void changeDocForVisibleRuleForInheritance_Teacher(Document doc) {
        Element el = (Element) doc.selectSingleNode("/VisibleRuleForInheritance/person"); //$NON-NLS-1$
        Attribute attr = el.attribute(new QName("type", new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance"))); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        attr.setValue("Teacher"); //$NON-NLS-1$
        el.clearContent();
        el.addElement("name"); //$NON-NLS-1$
        el.addElement("salary"); //$NON-NLS-1$
    }

}
