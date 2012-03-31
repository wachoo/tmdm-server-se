package org.talend.mdm.webapp.base.client.util;

import java.util.List;

import org.talend.mdm.webapp.base.client.model.Image;

import com.google.gwt.junit.client.GWTTestCase;


public class ImageUitlGWTTest extends GWTTestCase{
    
    public void testGetImages() throws Exception
    {
        String xml = "<list><entry><name>aaa-aaa.jpeg</name><uri>/imageserver/upload/aaa/aaa.jpeg</uri></entry><entry><name>bbb-bbb.jpeg</name><uri>/imageserver/upload/bbb/bbb.jpeg</uri></entry><entry><name>yunjiao-yunjiao.jpeg</name><uri>/imageserver/upload/yunjiao/yunjiao.jpeg</uri></entry><entry><name>aaa-dfsd.jpeg</name><uri>/imageserver/upload/aaa/dfsd.jpeg</uri></entry><entry><name>aaa-0sd.jpeg</name><uri>/imageserver/upload/aaa/0sd.jpeg</uri></entry><entry><name>aaa-dfs.jpeg</name><uri>/imageserver/upload/aaa/dfs.jpeg</uri></entry><entry><name>aaa-dfsw.jpeg</name><uri>/imageserver/upload/aaa/dfsw.jpeg</uri></entry><entry><name>aaa-01.jpeg</name><uri>/imageserver/upload/aaa/01.jpeg</uri></entry><entry><name>aaa-0.jpeg</name><uri>/imageserver/upload/aaa/0.jpeg</uri></entry><entry><name>123-5.jpeg</name><uri>/imageserver/upload/123/5.jpeg</uri></entry><entry><name>aaa-0sdf.jpeg</name><uri>/imageserver/upload/aaa/0sdf.jpeg</uri></entry></list>"; //$NON-NLS-1$
        List<Image> images = ImageUtil.getImages(xml);
        assertEquals(11, images.size());
        Image image1 = images.get(0);
        assertEquals("aaa-aaa.jpeg",image1.getName()); //$NON-NLS-1$
        assertEquals("/imageserver/upload/aaa/aaa.jpeg",image1.getPath());             //$NON-NLS-1$
        Image image6 = images.get(6);
        assertEquals("aaa-dfsw.jpeg",image6.getName()); //$NON-NLS-1$
        assertEquals("/imageserver/upload/aaa/dfsw.jpeg",image6.getPath());             //$NON-NLS-1$
        Image image11 = images.get(10);
        assertEquals("aaa-0sdf.jpeg",image11.getName()); //$NON-NLS-1$
        assertEquals("/imageserver/upload/aaa/0sdf.jpeg",image11.getPath());             //$NON-NLS-1$
    }
    
    // GWTTestCase Required
    @Override
    public String getModuleName() {        
        return "org.talend.mdm.webapp.base.TestBase"; //$NON-NLS-1$
    }
}
