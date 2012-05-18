package org.talend.mdm.webapp.browserecords.server.ruleengine;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.talend.mdm.webapp.base.shared.SimpleTypeModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;


public class DisplayRuleTestData {

    private static final Logger LOG = Logger.getLogger(DisplayRuleTestData.class);

    public static Document getDocument(String name) {
        Document document = null;
        try {
            InputStream is = DisplayRuleTestData.class.getResourceAsStream(name);
            document = org.talend.mdm.webapp.base.server.util.XmlUtil.parse(is);

        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return document;
    }

    public static Map<String, TypeModel> get_Basic_DefaultValue_Rule() {
        Map<String, TypeModel> metaDatas = new LinkedHashMap<String, TypeModel>();

        SimpleTypeModel subElementType = new SimpleTypeModel();
        subElementType.setXpath("TestDefaultModel/subelement"); //$NON-NLS-1$
        metaDatas.put(subElementType.getXpath(), subElementType);

        SimpleTypeModel nameType = new SimpleTypeModel();
        nameType.setXpath("TestDefaultModel/name"); //$NON-NLS-1$
        nameType.setDefaultValueExpression("'zhang'"); //$NON-NLS-1$
        metaDatas.put(nameType.getXpath(), nameType);

        SimpleTypeModel ct_titleType = new SimpleTypeModel();
        ct_titleType.setXpath("TestDefaultModel/cp/title"); //$NON-NLS-1$
        ct_titleType.setDefaultValueExpression("'hello'"); //$NON-NLS-1$
        metaDatas.put(ct_titleType.getXpath(), ct_titleType);

        SimpleTypeModel cp_contentType = new SimpleTypeModel();
        cp_contentType.setXpath("TestDefaultModel/cp/content"); //$NON-NLS-1$
        cp_contentType.setDefaultValueExpression("1+2"); //$NON-NLS-1$
        metaDatas.put(cp_contentType.getXpath(), cp_contentType);

        return metaDatas;
    }

    public static Map<String, TypeModel> get_Basic_DefaultValue_Rule_WithFunction() {
        Map<String, TypeModel> metaDatas = new LinkedHashMap<String, TypeModel>();

        SimpleTypeModel subelementType = new SimpleTypeModel();
        subelementType.setXpath("TestDefaultModel/subelement"); //$NON-NLS-1$
        metaDatas.put(subelementType.getXpath(), subelementType);

        SimpleTypeModel nameType = new SimpleTypeModel();
        nameType.setXpath("TestDefaultModel/name"); //$NON-NLS-1$
        nameType.setDefaultValueExpression("'zhang'"); //$NON-NLS-1$
        metaDatas.put(nameType.getXpath(), nameType);

        SimpleTypeModel cp_titleType = new SimpleTypeModel();
        cp_titleType.setXpath("TestDefaultModel/cp/title"); //$NON-NLS-1$
        cp_titleType.setDefaultValueExpression("fn:concat('hello', 'world!!!')"); //$NON-NLS-1$
        metaDatas.put(cp_titleType.getXpath(), cp_titleType);

        SimpleTypeModel cp_contentType = new SimpleTypeModel();
        cp_contentType.setXpath("TestDefaultModel/cp/content"); //$NON-NLS-1$
        cp_contentType.setDefaultValueExpression("fn:abs(-5) + fn:abs(-5)"); //$NON-NLS-1$
        metaDatas.put(cp_contentType.getXpath(), cp_contentType);

        return metaDatas;
    }

    public static Map<String, TypeModel> get_DefaultValue_Rule_WithFunctionAndXPath() {
        Map<String, TypeModel> metaDatas = new LinkedHashMap<String, TypeModel>();

        SimpleTypeModel subelementType = new SimpleTypeModel();
        subelementType.setXpath("DefaultRuleWithFunctionAndXPath/subelement"); //$NON-NLS-1$
        metaDatas.put(subelementType.getXpath(), subelementType);

        SimpleTypeModel titleType = new SimpleTypeModel();
        titleType.setXpath("DefaultRuleWithFunctionAndXPath/title"); //$NON-NLS-1$
        titleType.setDefaultValueExpression("fn:concat('detail name is [', ../detail/name,']')"); //$NON-NLS-1$
        metaDatas.put(titleType.getXpath(), titleType);

        SimpleTypeModel detail_nameType = new SimpleTypeModel();
        detail_nameType.setXpath("DefaultRuleWithFunctionAndXPath/detail/name"); //$NON-NLS-1$
        detail_nameType.setDefaultValueExpression("fn:concat('zhang ','yang')"); //$NON-NLS-1$
        metaDatas.put(detail_nameType.getXpath(), detail_nameType);

        SimpleTypeModel detail_contentType = new SimpleTypeModel();
        detail_contentType.setXpath("DefaultRuleWithFunctionAndXPath/detail/content"); //$NON-NLS-1$
        detail_contentType.setDefaultValueExpression("fn:concat('name is [',../name,string,'] title is [', /DefaultRuleWithFunctionAndXPath/title,']')"); //$NON-NLS-1$
        metaDatas.put(detail_contentType.getXpath(), detail_contentType);

        return metaDatas;
    }

    public static Map<String, TypeModel> get_DefaultValueForBoolean() {
        Map<String, TypeModel> metaDatas = new LinkedHashMap<String, TypeModel>();

        SimpleTypeModel subelementType = new SimpleTypeModel();
        subelementType.setXpath("TestBoolean/subelement"); //$NON-NLS-1$
        metaDatas.put(subelementType.getXpath(), subelementType);

        SimpleTypeModel nameType = new SimpleTypeModel();
        nameType.setXpath("TestBoolean/name"); //$NON-NLS-1$
        nameType.setDefaultValueExpression("'zhang'"); //$NON-NLS-1$
        metaDatas.put(nameType.getXpath(), nameType);

        SimpleTypeModel finishedType = new SimpleTypeModel();
        finishedType.setXpath("TestBoolean/finished"); //$NON-NLS-1$
        finishedType.setDefaultValueExpression("fn:true()"); //$NON-NLS-1$
        metaDatas.put(finishedType.getXpath(), finishedType);

        SimpleTypeModel finished1Type = new SimpleTypeModel();
        finished1Type.setXpath("TestBoolean/finished1"); //$NON-NLS-1$
        finished1Type.setDefaultValueExpression("fn:false()"); //$NON-NLS-1$
        metaDatas.put(finished1Type.getXpath(), finished1Type);

        SimpleTypeModel finished2Type = new SimpleTypeModel();
        finished2Type.setXpath("TestBoolean/finished2"); //$NON-NLS-1$
        finished2Type.setDefaultValueExpression("1 != 2"); //$NON-NLS-1$
        metaDatas.put(finished2Type.getXpath(), finished2Type);

        SimpleTypeModel finished3Type = new SimpleTypeModel();
        finished3Type.setXpath("TestBoolean/finished3"); //$NON-NLS-1$
        finished3Type.setDefaultValueExpression("1 = 2"); //$NON-NLS-1$
        metaDatas.put(finished3Type.getXpath(), finished3Type);

        return metaDatas;
    }

    public static Map<String, TypeModel> get_DefaultValueForEnumeration() {
        Map<String, TypeModel> metaDatas = new LinkedHashMap<String, TypeModel>();

        SimpleTypeModel subelementType = new SimpleTypeModel();
        subelementType.setXpath("TestEnumeration/subelement"); //$NON-NLS-1$
        metaDatas.put(subelementType.getXpath(), subelementType);

        SimpleTypeModel nameType = new SimpleTypeModel();
        nameType.setXpath("TestEnumeration/name"); //$NON-NLS-1$
        nameType.setDefaultValueExpression("'zhang'"); //$NON-NLS-1$
        metaDatas.put(nameType.getXpath(), nameType);

        SimpleTypeModel ageType = new SimpleTypeModel();
        ageType.setXpath("TestEnumeration/age"); //$NON-NLS-1$
        ageType.setDefaultValueExpression("'21-30'"); //$NON-NLS-1$
        metaDatas.put(ageType.getXpath(), ageType);

        SimpleTypeModel favoriteType = new SimpleTypeModel();
        favoriteType.setXpath("TestEnumeration/favorite"); //$NON-NLS-1$
        favoriteType.setDefaultValueExpression("'Orange'"); //$NON-NLS-1$
        metaDatas.put(favoriteType.getXpath(), favoriteType);

        SimpleTypeModel numType = new SimpleTypeModel();
        numType.setXpath("TestEnumeration/num"); //$NON-NLS-1$
        numType.setDefaultValueExpression("6"); //$NON-NLS-1$
        metaDatas.put(numType.getXpath(), numType);

        return metaDatas;
    }

    public static Map<String, TypeModel> get_Multiple_Occurence_DefaultValueRule() {
        Map<String, TypeModel> metaDatas = new LinkedHashMap<String, TypeModel>();

        SimpleTypeModel subelementType = new SimpleTypeModel();
        subelementType.setXpath("MultipleOccurence/subelement"); //$NON-NLS-1$
        metaDatas.put(subelementType.getXpath(), subelementType);

        SimpleTypeModel nameType = new SimpleTypeModel();
        nameType.setXpath("MultipleOccurence/name"); //$NON-NLS-1$
        metaDatas.put(nameType.getXpath(), nameType);

        SimpleTypeModel cp_titleType = new SimpleTypeModel();
        cp_titleType.setXpath("MultipleOccurence/cp/title"); //$NON-NLS-1$
        metaDatas.put(cp_titleType.getXpath(), cp_titleType);

        SimpleTypeModel cp_addressType = new SimpleTypeModel();
        cp_addressType.setXpath("MultipleOccurence/cp/address"); //$NON-NLS-1$
        cp_addressType.setDefaultValueExpression("fn:concat('hello ','this is address')"); //$NON-NLS-1$
        metaDatas.put(cp_addressType.getXpath(), cp_addressType);

        SimpleTypeModel telType = new SimpleTypeModel();
        telType.setXpath("MultipleOccurence/tel"); //$NON-NLS-1$
        telType.setDefaultValueExpression("fn:concat('phone',': 1323234323')"); //$NON-NLS-1$
        metaDatas.put(telType.getXpath(), telType);

        return metaDatas;
    }

    public static Map<String, TypeModel> get_DefaultRuleForInheritance() {
        Map<String, TypeModel> metaDatas = new LinkedHashMap<String, TypeModel>();

        SimpleTypeModel subelementType = new SimpleTypeModel();
        subelementType.setXpath("DefaultRuleForInheritance/subelement"); //$NON-NLS-1$
        metaDatas.put(subelementType.getXpath(), subelementType);

        SimpleTypeModel nameType = new SimpleTypeModel();
        nameType.setXpath("DefaultRuleForInheritance/name"); //$NON-NLS-1$
        nameType.setDefaultValueExpression("fn:concat('zhang ','yang')"); //$NON-NLS-1$
        metaDatas.put(nameType.getXpath(), nameType);

        SimpleTypeModel person_nameType = new SimpleTypeModel();
        person_nameType.setXpath("DefaultRuleForInheritance/person/name"); //$NON-NLS-1$
        metaDatas.put(person_nameType.getXpath(), person_nameType);

        SimpleTypeModel personNameType = new SimpleTypeModel();
        personNameType.setXpath("DefaultRuleForInheritance/person/name"); //$NON-NLS-1$
        metaDatas.put(personNameType.getXpath(), personNameType);

        ComplexTypeModel person_StudentType = new ComplexTypeModel();
        person_StudentType.setXpath("DefaultRuleForInheritance/person/score"); //$NON-NLS-1$
        person_StudentType.setDefaultValueExpression("fn:concat(/DefaultRuleForInheritance/name,'''s score is 100')"); //$NON-NLS-1$
        metaDatas.put(person_StudentType.getXpath(), person_StudentType);

        ComplexTypeModel person_TeacherType = new ComplexTypeModel();
        person_TeacherType.setXpath("DefaultRuleForInheritance/person/salary"); //$NON-NLS-1$
        person_TeacherType.setDefaultValueExpression("fn:concat(/DefaultRuleForInheritance/name,'''s salary is 1000')"); //$NON-NLS-1$
        metaDatas.put(person_TeacherType.getXpath(), person_TeacherType);

        return metaDatas;
    }

    public static Map<String, TypeModel> get_Basic_VisibleRule() {
        Map<String, TypeModel> metaDatas = new LinkedHashMap<String, TypeModel>();

        SimpleTypeModel subelementType = new SimpleTypeModel();
        subelementType.setXpath("BasicVisibleRule/subelement"); //$NON-NLS-1$
        metaDatas.put(subelementType.getXpath(), subelementType);

        SimpleTypeModel nameType = new SimpleTypeModel();
        nameType.setXpath("BasicVisibleRule/name"); //$NON-NLS-1$
        metaDatas.put(nameType.getXpath(), nameType);

        SimpleTypeModel testfieldType = new SimpleTypeModel();
        testfieldType.setXpath("BasicVisibleRule/testfield"); //$NON-NLS-1$
        testfieldType.setVisibleExpression("1=1"); //$NON-NLS-1$
        metaDatas.put(testfieldType.getXpath(), testfieldType);

        SimpleTypeModel cp_titleType = new SimpleTypeModel();
        cp_titleType.setXpath("BasicVisibleRule/cp/title"); //$NON-NLS-1$
        metaDatas.put(cp_titleType.getXpath(), cp_titleType);

        SimpleTypeModel cp_addressType = new SimpleTypeModel();
        cp_addressType.setXpath("BasicVisibleRule/cp/address"); //$NON-NLS-1$
        cp_addressType.setVisibleExpression("1=2"); //$NON-NLS-1$
        metaDatas.put(cp_addressType.getXpath(), cp_addressType);

        return metaDatas;
    }

    public static Map<String, TypeModel> get_Basic_VisibleRule_Rule_WithFunction() {
        Map<String, TypeModel> metaDatas = new LinkedHashMap<String, TypeModel>();

        SimpleTypeModel subelementType = new SimpleTypeModel();
        subelementType.setXpath("BasicVisibleRuleFunction/subelement"); //$NON-NLS-1$
        metaDatas.put(subelementType.getXpath(), subelementType);

        SimpleTypeModel nameType = new SimpleTypeModel();
        nameType.setXpath("BasicVisibleRuleFunction/name"); //$NON-NLS-1$
        metaDatas.put(nameType.getXpath(), nameType);

        SimpleTypeModel testfieldType = new SimpleTypeModel();
        testfieldType.setXpath("BasicVisibleRuleFunction/testfield"); //$NON-NLS-1$
        testfieldType.setVisibleExpression("fn:string-length('hello') > 10"); //$NON-NLS-1$
        metaDatas.put(testfieldType.getXpath(), testfieldType);

        SimpleTypeModel cp_titleType = new SimpleTypeModel();
        cp_titleType.setXpath("BasicVisibleRuleFunction/cp/title"); //$NON-NLS-1$
        cp_titleType.setVisibleExpression("fn:ends-with('hello','h')"); //$NON-NLS-1$
        metaDatas.put(cp_titleType.getXpath(), cp_titleType);

        SimpleTypeModel cp_addressType = new SimpleTypeModel();
        cp_addressType.setXpath("BasicVisibleRuleFunction/cp/address"); //$NON-NLS-1$
        cp_addressType.setVisibleExpression("fn:ends-with('hello','o')"); //$NON-NLS-1$
        metaDatas.put(cp_addressType.getXpath(), cp_addressType);

        return metaDatas;
    }

    public static Map<String, TypeModel> get_VisibleRule_Rule_WithFunctionXPath() {
        Map<String, TypeModel> metaDatas = new LinkedHashMap<String, TypeModel>();

        SimpleTypeModel subelementType = new SimpleTypeModel();
        subelementType.setXpath("BasicVisibleRuleWithFunctionXPath/subelement"); //$NON-NLS-1$
        metaDatas.put(subelementType.getXpath(), subelementType);

        SimpleTypeModel nameType = new SimpleTypeModel();
        nameType.setXpath("BasicVisibleRuleWithFunctionXPath/name"); //$NON-NLS-1$
        metaDatas.put(nameType.getXpath(), nameType);

        SimpleTypeModel testfieldType = new SimpleTypeModel();
        testfieldType.setXpath("BasicVisibleRuleWithFunctionXPath/testfield"); //$NON-NLS-1$
        testfieldType.setVisibleExpression("fn:string-length(/BasicVisibleRuleWithFunctionXPath/name) > 3"); //$NON-NLS-1$
        metaDatas.put(testfieldType.getXpath(), testfieldType);

        SimpleTypeModel cp_titleType = new SimpleTypeModel();
        cp_titleType.setXpath("BasicVisibleRuleWithFunctionXPath/cp/title"); //$NON-NLS-1$
        cp_titleType.setVisibleExpression("fn:string-length(/BasicVisibleRuleWithFunctionXPath/name) > 4"); //$NON-NLS-1$
        metaDatas.put(cp_titleType.getXpath(), cp_titleType);

        SimpleTypeModel cp_addressType = new SimpleTypeModel();
        cp_addressType.setXpath("BasicVisibleRuleWithFunctionXPath/cp/address"); //$NON-NLS-1$
        cp_addressType.setVisibleExpression("fn:string-length(/BasicVisibleRuleWithFunctionXPath/name) > 5"); //$NON-NLS-1$
        metaDatas.put(cp_addressType.getXpath(), cp_addressType);

        return metaDatas;
    }

    public static Map<String, TypeModel> get_VisibleRule_UsingBoolean_Attribute() {
        Map<String, TypeModel> metaDatas = new LinkedHashMap<String, TypeModel>();

        SimpleTypeModel subelementType = new SimpleTypeModel();
        subelementType.setXpath("VisibleRuleBoolean/subelement"); //$NON-NLS-1$
        metaDatas.put(subelementType.getXpath(), subelementType);

        SimpleTypeModel nameType = new SimpleTypeModel();
        nameType.setXpath("VisibleRuleBoolean/name"); //$NON-NLS-1$
        metaDatas.put(nameType.getXpath(), nameType);

        SimpleTypeModel finishedType = new SimpleTypeModel();
        finishedType.setXpath("VisibleRuleBoolean/finished"); //$NON-NLS-1$
        finishedType.setVisibleExpression("fn:string-length('hello') > 10"); //$NON-NLS-1$
        metaDatas.put(finishedType.getXpath(), finishedType);

        SimpleTypeModel cp_titleType = new SimpleTypeModel();
        cp_titleType.setXpath("VisibleRuleBoolean/cp/title"); //$NON-NLS-1$
        metaDatas.put(cp_titleType.getXpath(), cp_titleType);

        SimpleTypeModel cp_finished1Type = new SimpleTypeModel();
        cp_finished1Type.setXpath("VisibleRuleBoolean/cp/finished1"); //$NON-NLS-1$
        cp_finished1Type.setVisibleExpression("fn:starts-with('hello','h')"); //$NON-NLS-1$
        metaDatas.put(cp_finished1Type.getXpath(), cp_finished1Type);

        SimpleTypeModel cp_finished2Type = new SimpleTypeModel();
        cp_finished2Type.setXpath("VisibleRuleBoolean/cp/finished2"); //$NON-NLS-1$
        cp_finished2Type.setVisibleExpression("fn:starts-with('hello','o')"); //$NON-NLS-1$
        metaDatas.put(cp_finished2Type.getXpath(), cp_finished2Type);

        return metaDatas;
    }

    public static Map<String, TypeModel> get_VisibleRuleForInheritance() {
        Map<String, TypeModel> metaDatas = new LinkedHashMap<String, TypeModel>();

        SimpleTypeModel subelementType = new SimpleTypeModel();
        subelementType.setXpath("VisibleRuleForInheritance/subelement"); //$NON-NLS-1$
        metaDatas.put(subelementType.getXpath(), subelementType);

        SimpleTypeModel nameType = new SimpleTypeModel();
        nameType.setXpath("VisibleRuleForInheritance/name"); //$NON-NLS-1$
        metaDatas.put(nameType.getXpath(), nameType);

        SimpleTypeModel testfieldType = new SimpleTypeModel();
        testfieldType.setXpath("VisibleRuleForInheritance/testfield"); //$NON-NLS-1$
        testfieldType.setVisibleExpression("fn:string-length('hello')>3"); //$NON-NLS-1$
        metaDatas.put(testfieldType.getXpath(), testfieldType);

        SimpleTypeModel person_nameType = new SimpleTypeModel();
        person_nameType.setXpath("VisibleRuleForInheritance/person/name"); //$NON-NLS-1$
        person_nameType.setVisibleExpression("fn:string-length('hello world')>8"); //$NON-NLS-1$
        metaDatas.put(person_nameType.getXpath(), person_nameType);

        ComplexTypeModel person_StudentType = new ComplexTypeModel();
        person_StudentType.setXpath("VisibleRuleForInheritance/person/score"); //$NON-NLS-1$
        person_StudentType.setVisibleExpression("fn:string-length('hello world')>15"); //$NON-NLS-1$
        metaDatas.put(person_StudentType.getXpath(), person_StudentType);

        ComplexTypeModel person_TeacherType = new ComplexTypeModel();
        person_TeacherType.setXpath("VisibleRuleForInheritance/person/salary"); //$NON-NLS-1$
        person_TeacherType.setVisibleExpression("fn:string-length('hello world')>10"); //$NON-NLS-1$
        metaDatas.put(person_TeacherType.getXpath(), person_TeacherType);

        return metaDatas;
    }
}
