package org.talend.mdm.webapp.base.client.util;

import java.util.List;

import org.talend.mdm.webapp.base.client.model.Image;

import com.google.gwt.junit.client.GWTTestCase;


public class ImageUitlGWTTest extends GWTTestCase{
    
    public void testGetImages() throws Exception
    {
        String xml = "<list><entry><name>c201204-0.jpeg</name><catalog>c201204</catalog><uri>/imageserver/upload/c201204/0.jpeg</uri><redirectUri>/imageserver/locator?imgId=0.jpeg</redirectUri></entry><entry><name>c201204-000.jpeg</name><catalog>c201204</catalog><uri>/imageserver/upload/c201204/000.jpeg</uri><redirectUri>/imageserver/locator?imgId=000.jpeg</redirectUri></entry><entry><name>c201204-01.jpeg</name><catalog>c201204</catalog><uri>/imageserver/upload/c201204/01.jpeg</uri><redirectUri>/imageserver/locator?imgId=01.jpeg</redirectUri></entry><entry><name>c201204-0sd.jpeg</name><catalog>c201204</catalog><uri>/imageserver/upload/c201204/0sd.jpeg</uri><redirectUri>/imageserver/locator?imgId=0sd.jpeg</redirectUri></entry><entry><name>c201204-111.jpeg</name><catalog>c201204</catalog><uri>/imageserver/upload/c201204/111.jpeg</uri><redirectUri>/imageserver/locator?imgId=111.jpeg</redirectUri></entry><entry><name>c201204-1234.jpeg</name><catalog>c201204</catalog><uri>/imageserver/upload/c201204/1234.jpeg</uri><redirectUri>/imageserver/locator?imgId=1234.jpeg</redirectUri></entry></list>"; //$NON-NLS-1$
        List<Image> images = ImageUtil.getImages(xml);
        assertEquals(6, images.size());
        Image image1 = images.get(0);
        assertEquals("c201204-0.jpeg",image1.getName()); //$NON-NLS-1$
        assertEquals("/imageserver/upload/c201204/0.jpeg",image1.getUri());             //$NON-NLS-1$
        assertEquals("/imageserver/locator?imgId=0.jpeg",image1.getRedirectUri());             //$NON-NLS-1$
        Image image3 = images.get(2);
        assertEquals("c201204-01.jpeg",image3.getName()); //$NON-NLS-1$
        assertEquals("/imageserver/upload/c201204/01.jpeg",image3.getUri());             //$NON-NLS-1$
        assertEquals("/imageserver/locator?imgId=01.jpeg",image3.getRedirectUri());             //$NON-NLS-1$
        Image image6 = images.get(5);
        assertEquals("c201204-1234.jpeg",image6.getName()); //$NON-NLS-1$
        assertEquals("/imageserver/upload/c201204/1234.jpeg",image6.getUri());             //$NON-NLS-1$
        assertEquals("/imageserver/locator?imgId=1234.jpeg",image6.getRedirectUri());             //$NON-NLS-1$
    }
    
    // GWTTestCase Required
    @Override
    public String getModuleName() {        
        return "org.talend.mdm.webapp.base.TestBase"; //$NON-NLS-1$
    }
}
