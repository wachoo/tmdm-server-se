package org.talend.mdm.webapp.browserecords.client.widget.inputfield;

import com.google.gwt.junit.client.GWTTestCase;


public class PictureFieldGWTTest extends GWTTestCase{

    public String getModuleName() {
        return "org.talend.mdm.webapp.browserecords.TestBrowseRecords";
    }
    
    
    public void testSetEmptyValue() {
        PictureField pictureField =new PictureField();
        pictureField.setFireChangeEventOnSetValue(false);
        pictureField.setValue(null);//override method setValue 
        assertEquals(null,pictureField.getValue());
    }

}
