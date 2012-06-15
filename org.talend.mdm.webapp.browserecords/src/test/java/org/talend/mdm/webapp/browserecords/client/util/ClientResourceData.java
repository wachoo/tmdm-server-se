package org.talend.mdm.webapp.browserecords.client.util;


public class ClientResourceData {

    public static String getRecordA() {
        StringBuffer recordA = new StringBuffer();
        recordA.append("<Test>"); //$NON-NLS-1$
        recordA.append("<subelement>111</subelement>"); //$NON-NLS-1$
        recordA.append("<name>zhang</name>"); //$NON-NLS-1$
        recordA.append("<age>25</age>"); //$NON-NLS-1$
        recordA.append("<memo>hello, morning</memo>"); //$NON-NLS-1$
        recordA.append("</Test>"); //$NON-NLS-1$
        return recordA.toString();
    }

    public static String getModelA() {
        StringBuffer modelA = new StringBuffer();
        modelA.append("<models concept=\"Test\">"); //$NON-NLS-1$
        modelA.append("<model isSimple=\"false\" isFk=\"false\" typePath=\"Test\"/>"); //$NON-NLS-1$
        modelA.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/subelement\"/>"); //$NON-NLS-1$
        modelA.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/name\"/>"); //$NON-NLS-1$
        modelA.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/age\"/>"); //$NON-NLS-1$
        modelA.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/memo\"/>"); //$NON-NLS-1$
        modelA.append("</models>"); //$NON-NLS-1$
        return modelA.toString();
    }

    public static String getRecordB() {
        StringBuffer recordB = new StringBuffer();
        recordB.append("<Test>"); //$NON-NLS-1$
        recordB.append("<subelement>111</subelement>"); //$NON-NLS-1$
        recordB.append("<name></name>"); //$NON-NLS-1$
        recordB.append("<age>25</age>"); //$NON-NLS-1$
        recordB.append("<memo></memo>"); //$NON-NLS-1$
        recordB.append("</Test>"); //$NON-NLS-1$
        return recordB.toString();
    }

    public static String getModelB() {
        StringBuffer modelB = new StringBuffer();
        modelB.append("<models concept=\"Test\">"); //$NON-NLS-1$
        modelB.append("<model isSimple=\"false\" isFk=\"false\" typePath=\"Test\"/>"); //$NON-NLS-1$
        modelB.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/subelement\"/>"); //$NON-NLS-1$
        modelB.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/name\"/>"); //$NON-NLS-1$
        modelB.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/age\"/>"); //$NON-NLS-1$
        modelB.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/memo\"/>"); //$NON-NLS-1$
        modelB.append("</models>"); //$NON-NLS-1$
        return modelB.toString();
    }

    public static String getRecordC() {
        StringBuffer recordC = new StringBuffer();
        recordC.append("<Test>"); //$NON-NLS-1$
        recordC.append("<subelement>111</subelement>"); //$NON-NLS-1$
        recordC.append("<name>zhang</name>"); //$NON-NLS-1$
        recordC.append("<age>25</age>"); //$NON-NLS-1$
        recordC.append("<memo>I'm zhang yang</memo>"); //$NON-NLS-1$
        recordC.append("</Test>"); //$NON-NLS-1$
        return recordC.toString();
    }

    public static String getModelC() {
        StringBuffer modelC = new StringBuffer();
        modelC.append("<models concept=\"Test\">"); //$NON-NLS-1$
        modelC.append("<model isSimple=\"false\" isFk=\"false\" typePath=\"Test\"/>"); //$NON-NLS-1$
        modelC.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/subelement\"/>"); //$NON-NLS-1$
        modelC.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/name\"/>"); //$NON-NLS-1$
        modelC.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/age\"/>"); //$NON-NLS-1$
        modelC.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/memo\"/>"); //$NON-NLS-1$
        modelC.append("</models>"); //$NON-NLS-1$
        return modelC.toString();
    }
    
    public static String getRecordD() {
        StringBuffer recordD = new StringBuffer();
        recordD.append("<Test>"); //$NON-NLS-1$
        recordD.append("<subelement>111</subelement>"); //$NON-NLS-1$
        recordD.append("<name>zhang</name>"); //$NON-NLS-1$
        recordD.append("<age>25</age>"); //$NON-NLS-1$
        recordD.append("<memo>I'm zhang yang</memo>"); //$NON-NLS-1$
        recordD.append("</Test>"); //$NON-NLS-1$
        return recordD.toString();
    }

    public static String getModelD() {
        StringBuffer modelD = new StringBuffer();
        modelD.append("<models concept=\"Test\">"); //$NON-NLS-1$
        modelD.append("<model isSimple=\"false\" isFk=\"false\" typePath=\"Test\"/>"); //$NON-NLS-1$
        modelD.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/subelement\"/>"); //$NON-NLS-1$
        modelD.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/name\"/>"); //$NON-NLS-1$
        modelD.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/age\"/>"); //$NON-NLS-1$
        modelD.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/memo\"/>"); //$NON-NLS-1$
        modelD.append("</models>"); //$NON-NLS-1$
        return modelD.toString();
    }

    public static String getRecordE() {
        StringBuffer recordE = new StringBuffer();
        recordE.append("<Test>"); //$NON-NLS-1$
        recordE.append("<subelement>111</subelement>"); //$NON-NLS-1$
        recordE.append("<name>zhang</name>"); //$NON-NLS-1$
        recordE.append("<age>25</age>"); //$NON-NLS-1$
        recordE.append("<memo></memo>"); //$NON-NLS-1$
        recordE.append("<memo></memo>"); //$NON-NLS-1$
        recordE.append("<memo></memo>"); //$NON-NLS-1$
        recordE.append("<memo></memo>"); //$NON-NLS-1$
        recordE.append("</Test>"); //$NON-NLS-1$
        return recordE.toString();
    }

    public static String getModelE() {
        StringBuffer modelE = new StringBuffer();
        modelE.append("<models concept=\"Test\">"); //$NON-NLS-1$
        modelE.append("<model isSimple=\"false\" isFk=\"false\" typePath=\"Test\"/>"); //$NON-NLS-1$
        modelE.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/subelement\"/>"); //$NON-NLS-1$
        modelE.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/name\"/>"); //$NON-NLS-1$
        modelE.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/age\"/>"); //$NON-NLS-1$
        modelE.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/memo\"/>"); //$NON-NLS-1$
        modelE.append("</models>"); //$NON-NLS-1$
        return modelE.toString();
    }
    
    public static String getRecordF() {
        StringBuffer recordF = new StringBuffer();
        recordF.append("<Test>"); //$NON-NLS-1$
        recordF.append("<subelement>111</subelement>"); //$NON-NLS-1$
        recordF.append("<name>zhang</name>"); //$NON-NLS-1$
        recordF.append("<age>25</age>"); //$NON-NLS-1$
        recordF.append("<memo></memo>"); //$NON-NLS-1$
        recordF.append("<memo>hello</memo>"); //$NON-NLS-1$
        recordF.append("<memo></memo>"); //$NON-NLS-1$
        recordF.append("<memo>bye</memo>"); //$NON-NLS-1$
        recordF.append("</Test>"); //$NON-NLS-1$
        return recordF.toString();
    }

    public static String getModelF() {
        StringBuffer modelF = new StringBuffer();
        modelF.append("<models concept=\"Test\">"); //$NON-NLS-1$
        modelF.append("<model isSimple=\"false\" isFk=\"false\" typePath=\"Test\"/>"); //$NON-NLS-1$
        modelF.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/subelement\"/>"); //$NON-NLS-1$
        modelF.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/name\"/>"); //$NON-NLS-1$
        modelF.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/age\"/>"); //$NON-NLS-1$
        modelF.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/memo\"/>"); //$NON-NLS-1$
        modelF.append("</models>"); //$NON-NLS-1$
        return modelF.toString();
    }

    public static String getRecordG() {
        StringBuffer recordG = new StringBuffer();
        recordG.append("<Test>"); //$NON-NLS-1$
        recordG.append("<subelement>111</subelement>"); //$NON-NLS-1$
        recordG.append("<name>zhang</name>"); //$NON-NLS-1$
        recordG.append("<age></age>"); //$NON-NLS-1$
        recordG.append("<memo></memo>"); //$NON-NLS-1$
        recordG.append("<memo>hello</memo>"); //$NON-NLS-1$
        recordG.append("<memo></memo>"); //$NON-NLS-1$
        recordG.append("<memo>bye</memo>"); //$NON-NLS-1$
        recordG.append("<cp>"); //$NON-NLS-1$
        recordG.append("<title></title>"); //$NON-NLS-1$
        recordG.append("<address></address>"); //$NON-NLS-1$
        recordG.append("<address></address>"); //$NON-NLS-1$
        recordG.append("<address></address>"); //$NON-NLS-1$
        recordG.append("</cp>"); //$NON-NLS-1$
        recordG.append("</Test>"); //$NON-NLS-1$
        return recordG.toString();
    }

    public static String getModelG() {
        StringBuffer modelG = new StringBuffer();
        modelG.append("<models concept=\"Test\">"); //$NON-NLS-1$
        modelG.append("<model isSimple=\"false\" isFk=\"false\" typePath=\"Test\"/>"); //$NON-NLS-1$
        modelG.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/subelement\"/>"); //$NON-NLS-1$
        modelG.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/name\"/>"); //$NON-NLS-1$
        modelG.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/age\"/>"); //$NON-NLS-1$
        modelG.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/memo\"/>"); //$NON-NLS-1$
        modelG.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/cp\"/>"); //$NON-NLS-1$
        modelG.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/cp/title\"/>"); //$NON-NLS-1$
        modelG.append("<model isSimple=\"true\" isFk=\"false\" typePath=\"Test/cp/address\"/>"); //$NON-NLS-1$
        modelG.append("</models>"); //$NON-NLS-1$
        return modelG.toString();
    }
}
