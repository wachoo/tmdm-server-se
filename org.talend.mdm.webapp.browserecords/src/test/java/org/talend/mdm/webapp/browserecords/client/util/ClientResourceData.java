package org.talend.mdm.webapp.browserecords.client.util;

@SuppressWarnings("nls")
public class ClientResourceData {

    public static String getRecordProduct1() {
        StringBuffer record = new StringBuffer();
        record.append("<Product>");
        record.append("<Id>1</Id>");
        record.append("<Name>Test product 1</Name>");
        record.append("<Price>10</Price>");
        record.append("<OnlineStore>@@http://</OnlineStore>");
        record.append("<Availability>false</Availability>");
        record.append("<Picture>http://www.talendforge.org/img/style/talendforge.jpg</Picture>");
        record.append("</Product>");
        return record.toString();
    }

    public static String getRecordProduct2() {
        StringBuffer record = new StringBuffer();
        record.append("<Product> ");
        record.append("<Id>231035935</Id> ");
        record.append("<Name>Talend Golf Shirt</Name> ");
        record.append("<Description>Golf-style, collared t-shirt</Description> ");
        record.append("<Features> ");
        record.append("<Colors> ");
        record.append("<Color></Color> ");
        record.append("</Colors> ");
        record.append("<Sizes> ");
        record.append("<Size></Size> ");
        record.append("<Size>Medium</Size> ");
        record.append("<Size/> ");
        record.append("</Sizes> ");
        record.append("</Features> ");
        record.append("<Availability>true</Availability> ");
        record.append("<Price>16.99</Price> ");
        record.append("<Family>[1]</Family> ");
        record.append("<Picture>/imageserver/upload/TalendShop/golf_shirt.jpg</Picture> ");
        record.append("<OnlineStore>https://unknownhost/test</OnlineStore> ");
        record.append("</Product> ");
        return record.toString();
    }

    public static String getRecordProduct3() {
        StringBuffer record = new StringBuffer();
        record.append("<Product>");
        record.append("<Id>1</Id>");
        record.append("<Name>Test product 1</Name>");
        record.append("<Price>10</Price>");
        record.append("<Features> ");
        record.append("<Sizes> ");
        record.append("<Size>Large</Size> ");
        record.append("</Sizes> ");
        record.append("</Features> ");
        record.append("<OnlineStore>@@http://</OnlineStore>");
        record.append("<Availability>false</Availability>");
        record.append("<Picture>http://www.talendforge.org/img/style/talendforge.jpg</Picture>");
        record.append("</Product>");
        return record.toString();
    }

    public static String getModelProduct() {
        StringBuffer model = new StringBuffer();
        model.append("<models concept=\"Product\">");
        model.append("  <model isSimple=\"false\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Product\"/>");
        model.append("  <model isSimple=\"true\" isKey=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" minOccurs=\"1\"  maxOccurs=\"1\" typePath=\"Product/Id\"/>");
        model.append("  <model isSimple=\"true\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" minOccurs=\"1\"  maxOccurs=\"1\" typePath=\"Product/Name\"/>");
        model.append("  <model isSimple=\"true\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"false\"  minOccurs=\"1\"  maxOccurs=\"1\" typePath=\"Product/Description\"/>");
        model.append("  <model isSimple=\"false\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"false\"  minOccurs=\"0\"  maxOccurs=\"1\" typePath=\"Product/Features\"/>");
        model.append("     <model isSimple=\"false\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" minOccurs=\"0\"  maxOccurs=\"1\" typePath=\"Product/Features/Sizes\"/>");
        model.append("        <model isSimple=\"true\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" minOccurs=\"1\"  maxOccurs=\"-1\" typePath=\"Product/Features/Sizes/Size\"/>");
        model.append("     <model isSimple=\"false\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" minOccurs=\"0\"  maxOccurs=\"1\" typePath=\"Product/Features/Colors\"/>");
        model.append("        <model isSimple=\"true\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\"  minOccurs=\"1\"  maxOccurs=\"-1\" typePath=\"Product/Features/Colors/Color\"/>");
        model.append("  <model isSimple=\"true\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" minOccurs=\"0\"  maxOccurs=\"1\" typePath=\"Product/Availability\"/>");
        model.append("  <model isSimple=\"true\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\"  minOccurs=\"1\"  maxOccurs=\"1\" typePath=\"Product/Price\"/>");
        model.append("  <model isSimple=\"true\" isKey=\"false\" isFk=\"true\" isReadOnly=\"false\" isVisible=\"true\"  minOccurs=\"0\"  maxOccurs=\"1\" typePath=\"Product/Family\"/>");
        model.append("  <model isSimple=\"true\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\"  minOccurs=\"0\"  maxOccurs=\"1\" typePath=\"Product/OnlineStore\"/>");
        model.append("     <model isSimple=\"false\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\"  minOccurs=\"0\"  maxOccurs=\"1\" typePath=\"Product/Stores\"/>");
        model.append("        <model isSimple=\"true\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\"  minOccurs=\"0\"  maxOccurs=\"-1\" typePath=\"Product/Stores/Store\"/>");
        model.append("  <model isSimple=\"true\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" minOccurs=\"0\"  maxOccurs=\"1\" typePath=\"Product/Picture\"/>");
        model.append("</models>");
        return model.toString();
    }

    public static String getRecordCustomer1() {
        StringBuffer record = new StringBuffer();
        record.append("<Customer>");
        record.append("<firstname>Mike</firstname>");
        record.append("<lastname>Burn</lastname>");
        record.append("<description>VIP Customer</description>");
        record.append("</Customer>");
        return record.toString();
    }

    public static String getModelCustomer() {
        StringBuffer model = new StringBuffer();
        model.append("<models concept=\"Customer\">");
        model.append("  <model isSimple=\"false\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Customer\"/>");
        model.append("  <model isSimple=\"true\" isKey=\"true\" isFk=\"false\" isReadOnly=\"true\" isVisible=\"true\" minOccurs=\"1\"  maxOccurs=\"1\" typePath=\"Customer/firstname\"/>");
        model.append("  <model isSimple=\"true\" isKey=\"true\" isFk=\"false\" isReadOnly=\"true\" isVisible=\"false\" minOccurs=\"1\"  maxOccurs=\"1\" typePath=\"Customer/lastname\"/>");
        model.append("  <model isSimple=\"true\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" minOccurs=\"0\"  maxOccurs=\"1\" typePath=\"Customer/description\"/>");
        model.append("</models>");
        return model.toString();
    }

    public static String getRecordContract1() {
        StringBuffer record = new StringBuffer();
        record.append("<Contract>");
        record.append("<id>001</id>");
        record.append("<description>VIP Customer</description>");
        record.append("<owner>Starkey</owner>");
        record.append("<effectiveDate>1999-09-01</effectiveDate>");
        record.append("<partyNotes>");
        record.append("<firstPartyNote>First party agree</firstPartyNote>");
        record.append("<secondPartyNote>Second party agree</secondPartyNote>");
        record.append("</partyNotes>");
        record.append("</Contract>");
        return record.toString();
    }

    public static String getModelContract() {
        StringBuffer model = new StringBuffer();
        model.append("<models concept=\"Contract\">");
        model.append("  <model isSimple=\"false\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Contract\"/>");
        model.append("  <model isSimple=\"true\" isKey=\"true\" isFk=\"false\" isReadOnly=\"true\" isVisible=\"true\" minOccurs=\"1\"  maxOccurs=\"1\" typePath=\"Contract/id\"/>");
        model.append("  <model isSimple=\"true\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" minOccurs=\"0\"  maxOccurs=\"1\" typePath=\"Contract/description\"/>");
        model.append("  <model isSimple=\"true\" isKey=\"false\" isFk=\"false\" isReadOnly=\"true\" isVisible=\"true\" minOccurs=\"1\"  maxOccurs=\"1\" typePath=\"Contract/owner\"/>");
        model.append("  <model isSimple=\"true\" isKey=\"false\" isFk=\"false\" isReadOnly=\"true\" isVisible=\"true\" minOccurs=\"1\"  maxOccurs=\"1\" typePath=\"Contract/effectiveDate\"/>");
        model.append("  <model isSimple=\"false\" isKey=\"false\" isFk=\"false\" isReadOnly=\"true\" isVisible=\"true\"  minOccurs=\"0\"  maxOccurs=\"1\" typePath=\"Contract/partyNotes\"/>");
        model.append("     <model isSimple=\"true\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" minOccurs=\"0\"  maxOccurs=\"1\" typePath=\"Contract/partyNotes/firstPartyNote\"/>");
        model.append("     <model isSimple=\"true\" isKey=\"false\" isFk=\"false\" isReadOnly=\"true\" isVisible=\"true\" minOccurs=\"0\"  maxOccurs=\"1\" typePath=\"Contract/partyNotes/secondPartyNote\"/>");
        model.append("</models>");
        return model.toString();
    }

    public static String getRecordA() {
        StringBuffer recordA = new StringBuffer();
        recordA.append("<Test>");
        recordA.append("<subelement>111</subelement>");
        recordA.append("<name>zhang</name>");
        recordA.append("<age>25</age>");
        recordA.append("<memo>hello, morning</memo>");
        recordA.append("</Test>");
        return recordA.toString();
    }

    public static String getModelA() {
        StringBuffer modelA = new StringBuffer();
        modelA.append("<models concept=\"Test\">");
        modelA.append("<model isSimple=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test\"/>");
        modelA.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/subelement\"/>");
        modelA.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/name\"/>");
        modelA.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/age\"/>");
        modelA.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/memo\"/>");
        modelA.append("</models>");
        return modelA.toString();
    }

    public static String getRecordB() {
        StringBuffer recordB = new StringBuffer();
        recordB.append("<Test>");
        recordB.append("<subelement>111</subelement>");
        recordB.append("<name></name>");
        recordB.append("<age>25</age>");
        recordB.append("<memo></memo>");
        recordB.append("</Test>");
        return recordB.toString();
    }

    public static String getModelB() {
        StringBuffer modelB = new StringBuffer();
        modelB.append("<models concept=\"Test\">");
        modelB.append("<model isSimple=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test\"/>");
        modelB.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/subelement\"/>");
        modelB.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/name\"/>");
        modelB.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/age\"/>");
        modelB.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/memo\"/>");
        modelB.append("</models>");
        return modelB.toString();
    }

    public static String getRecordC() {
        StringBuffer recordC = new StringBuffer();
        recordC.append("<Test>");
        recordC.append("<subelement>111</subelement>");
        recordC.append("<name>zhang</name>");
        recordC.append("<age>25</age>");
        recordC.append("<memo>I'm zhang yang</memo>");
        recordC.append("</Test>");
        return recordC.toString();
    }

    public static String getModelC() {
        StringBuffer modelC = new StringBuffer();
        modelC.append("<models concept=\"Test\">");
        modelC.append("<model isSimple=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test\"/>");
        modelC.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/subelement\"/>");
        modelC.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"false\" typePath=\"Test/name\"/>");
        modelC.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"false\" typePath=\"Test/age\"/>");
        modelC.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"false\" typePath=\"Test/memo\"/>");
        modelC.append("</models>");
        return modelC.toString();
    }

    public static String getRecordD() {
        StringBuffer recordD = new StringBuffer();
        recordD.append("<Test>");
        recordD.append("<subelement>111</subelement>");
        recordD.append("<name>zhang</name>");
        recordD.append("<age>25</age>");
        recordD.append("<memo>I'm zhang yang</memo>");
        recordD.append("</Test>");
        return recordD.toString();
    }

    public static String getModelD() {
        StringBuffer modelD = new StringBuffer();
        modelD.append("<models concept=\"Test\">");
        modelD.append("<model isSimple=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test\"/>");
        modelD.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/subelement\"/>");
        modelD.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/name\"/>");
        modelD.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"true\" isVisible=\"true\" typePath=\"Test/age\"/>");
        modelD.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"true\" isVisible=\"true\" typePath=\"Test/memo\"/>");
        modelD.append("</models>");
        return modelD.toString();
    }

    public static String getRecordE() {
        StringBuffer recordE = new StringBuffer();
        recordE.append("<Test>");
        recordE.append("<subelement>111</subelement>");
        recordE.append("<name>zhang</name>");
        recordE.append("<age>25</age>");
        recordE.append("<memo></memo>");
        recordE.append("<memo></memo>");
        recordE.append("<memo></memo>");
        recordE.append("<memo></memo>");
        recordE.append("</Test>");
        return recordE.toString();
    }

    public static String getModelE() {
        StringBuffer modelE = new StringBuffer();
        modelE.append("<models concept=\"Test\">");
        modelE.append("<model isSimple=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test\"/>");
        modelE.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/subelement\"/>");
        modelE.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/name\"/>");
        modelE.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/age\"/>");
        modelE.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" minOccurs=\"0\"  maxOccurs=\"-1\" typePath=\"Test/memo\"/>");
        modelE.append("</models>");
        return modelE.toString();
    }

    public static String getRecordF() {
        StringBuffer recordF = new StringBuffer();
        recordF.append("<Test>");
        recordF.append("<subelement>111</subelement>");
        recordF.append("<name>zhang</name>");
        recordF.append("<age>25</age>");
        recordF.append("<memo></memo>");
        recordF.append("<memo>hello</memo>");
        recordF.append("<memo></memo>");
        recordF.append("<memo>bye</memo>");
        recordF.append("</Test>");
        return recordF.toString();
    }

    public static String getModelF() {
        StringBuffer modelF = new StringBuffer();
        modelF.append("<models concept=\"Test\">");
        modelF.append("<model isSimple=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test\"/>");
        modelF.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/subelement\"/>");
        modelF.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/name\"/>");
        modelF.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/age\"/>");
        modelF.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/memo\"/>");
        modelF.append("</models>");
        return modelF.toString();
    }

    public static String getRecordG() {
        StringBuffer recordG = new StringBuffer();
        recordG.append("<Test>");
        recordG.append("<subelement>111</subelement>");
        recordG.append("<name>zhang</name>");
        recordG.append("<age></age>");
        recordG.append("<memo></memo>");
        recordG.append("<memo>hello</memo>");
        recordG.append("<memo></memo>");
        recordG.append("<memo>bye</memo>");
        recordG.append("<cp>");
        recordG.append("<title></title>");
        recordG.append("<address></address>");
        recordG.append("<address></address>");
        recordG.append("<address></address>");
        recordG.append("</cp>");
        recordG.append("</Test>");
        return recordG.toString();
    }

    public static String getModelG() {
        StringBuffer modelG = new StringBuffer();
        modelG.append("<models concept=\"Test\">");
        modelG.append("<model isSimple=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test\"/>");
        modelG.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/subelement\"/>");
        modelG.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/name\"/>");
        modelG.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/age\"/>");
        modelG.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/memo\"/>");
        modelG.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/cp\"/>");
        modelG.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/cp/title\"/>");
        modelG.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/cp/address\"/>");
        modelG.append("</models>");
        return modelG.toString();
    }

    public static String getRecordH() {
        StringBuffer recordH = new StringBuffer();
        recordH.append("<Test>"); //$NON-NLS-1$
        recordH.append("<id>1</id>"); //$NON-NLS-1$
        recordH.append("<name>test</name>"); //$NON-NLS-1$
        recordH.append("<cp>"); //$NON-NLS-1$
        recordH.append("<title>Hello</title>"); //$NON-NLS-1$
        recordH.append("<address>Beijing</address>"); //$NON-NLS-1$
        recordH.append("<fk>North Five Loop</fk>"); //$NON-NLS-1$
        recordH.append("<cpChild>"); //$NON-NLS-1$
        recordH.append("<tel>13800000000</tel>"); //$NON-NLS-1$
        recordH.append("</cpChild>"); //$NON-NLS-1$
        recordH.append("</cp>"); //$NON-NLS-1$
        recordH.append("</Test>"); //$NON-NLS-1$
        return recordH.toString();
    }

    public static String getModelH() {
        StringBuffer modelH = new StringBuffer();
        modelH.append("<models concept=\"Test\">"); //$NON-NLS-1$
        modelH.append("<model isSimple=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test\"/>"); //$NON-NLS-1$
        modelH.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/id\"/>"); //$NON-NLS-1$
        modelH.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/name\"/>"); //$NON-NLS-1$
        modelH.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/cp\"/>"); //$NON-NLS-1$
        modelH.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/cp/title\"/>"); //$NON-NLS-1$
        modelH.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/cp/address\"/>"); //$NON-NLS-1$
        modelH.append("<model isSimple=\"true\" isFk=\"true\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/cp/fk\"/>"); //$NON-NLS-1$
        modelH.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/cp/cpChild\"/>"); //$NON-NLS-1$
        modelH.append("<model isSimple=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Test/cp/cpChild/tel\"/>"); //$NON-NLS-1$
        modelH.append("</models>"); //$NON-NLS-1$
        return modelH.toString();
    }

    public static String getRecordProductWithStore() {
        StringBuffer record = new StringBuffer();
        record.append("<Product>");
        record.append("<Id>2000</Id>");
        record.append("<Name>Talend Golf Shirt</Name>");
        record.append("<Family>[1]</Family>");
        record.append("<Stores>");
        record.append("<Store>[1]</Store>");
        record.append("<Store>[2]</Store>");
        record.append("<Store>[3]</Store>");
        record.append("</Stores>");
        record.append("</Product>");
        return record.toString();
    }

    public static String getModelProductWithStore() {
        StringBuffer model = new StringBuffer();
        model.append("<models concept=\"Product\">");
        model.append("  <model isSimple=\"false\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Product\"/>");
        model.append("  <model isSimple=\"true\" isKey=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" minOccurs=\"1\"  maxOccurs=\"1\" typePath=\"Product/Id\"/>");
        model.append("  <model isSimple=\"true\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" minOccurs=\"1\"  maxOccurs=\"1\" typePath=\"Product/Name\"/>");
        model.append("  <model isSimple=\"true\" isKey=\"false\" isFk=\"true\" isReadOnly=\"false\" isVisible=\"true\"  minOccurs=\"0\"  maxOccurs=\"1\" typePath=\"Product/Family\"/>");
        model.append("  <model isSimple=\"false\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\"  minOccurs=\"0\"  maxOccurs=\"1\" typePath=\"Product/Stores\"/>");
        model.append("  <model isSimple=\"true\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\"  minOccurs=\"0\"  maxOccurs=\"-1\" typePath=\"Product/Stores/Store\"/>");
        model.append("</models>");
        return model.toString();
    }

    public static String getModelProductFamily() {
        StringBuffer model = new StringBuffer();
        model.append("<models concept=\"ProductFamily\">");
        model.append("  <model isSimple=\"false\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"ProductFamily\"/>");
        model.append("  <model isSimple=\"true\" isKey=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" minOccurs=\"1\"  maxOccurs=\"1\" typePath=\"ProductFamily/Id\"/>");
        model.append("  <model isSimple=\"true\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" minOccurs=\"1\"  maxOccurs=\"1\" typePath=\"ProductFamily/Name\"/>");
        model.append("</models>");
        return model.toString();
    }

    public static String getModelProductWithSupplier() {
        StringBuffer model = new StringBuffer();
        model.append("<models concept=\"Product\">");
        model.append("  <model isSimple=\"false\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" typePath=\"Product\"/>");
        model.append("  <model isSimple=\"true\" isKey=\"true\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" minOccurs=\"1\"  maxOccurs=\"1\" typePath=\"Product/Id\"/>");
        model.append("  <model isSimple=\"true\" isKey=\"false\" isFk=\"false\" isReadOnly=\"false\" isVisible=\"true\" minOccurs=\"1\"  maxOccurs=\"1\" typePath=\"Product/Name\"/>");
        model.append("  <model isSimple=\"true\" isKey=\"false\" isFk=\"true\" isReadOnly=\"false\" isVisible=\"true\"  minOccurs=\"0\"  maxOccurs=\"1\" typePath=\"Product/Family\"/>");
        model.append("  <model isSimple=\"true\" isKey=\"false\" isFk=\"true\" isReadOnly=\"false\" isVisible=\"true\"  minOccurs=\"0\"  maxOccurs=\"1\" typePath=\"Product/Supplier\"/>");
        model.append("</models>");
        return model.toString();
    }

    public static String getRecordProductWithSupplier() {
        StringBuffer record = new StringBuffer();
        record.append("<Product xmlns:tmdm=\"http://www.talend.com/mdm\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
        record.append("<Id>1</Id>");
        record.append("<Name>Talend MDM</Name>");
        record.append("<Family>[1]</Family>");
        record.append("<Supplier tmdm:type=\"Company\">[1]</Supplier>");
        record.append("</Product>");
        return record.toString();
    }
}
