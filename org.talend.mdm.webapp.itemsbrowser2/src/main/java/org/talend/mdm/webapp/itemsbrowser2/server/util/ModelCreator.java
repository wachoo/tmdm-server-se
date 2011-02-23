package org.talend.mdm.webapp.itemsbrowser2.server.util;

import java.io.Serializable;

import org.talend.mdm.webapp.itemsbrowser2.client.model.DataTypeConstants;
import org.talend.mdm.webapp.itemsbrowser2.client.model.PictureBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.UrlBean;

public class ModelCreator {

	public static <D extends Serializable> D createModel(String dataType, String value){
		D model = null;
		
		if(dataType.equals(DataTypeConstants.STRING)){
			model = (D) value;
		} else if (dataType.equals(DataTypeConstants.UUID)){
			
		} else if (dataType.equals(DataTypeConstants.AUTO_INCREMENT)){
			
		} else if (dataType.equals(DataTypeConstants.PICTURE)){
			String[] prop = value.split(";");
			PictureBean pictureBean = new PictureBean();
			if (prop.length == 4){
				pictureBean.setUrl(prop[0]);
				pictureBean.setWidth(Integer.parseInt(prop[1]));
				pictureBean.setHeight(Integer.parseInt(prop[2]));
				pictureBean.setPreserveAspectRatio(prop[3]);
			}
			model = (D) pictureBean;
		} else if (dataType.equals(DataTypeConstants.URL)){
			String[] url = value.split("@@");
			UrlBean urlBean = new UrlBean(url[0], url[1]);
			model = (D) urlBean;
		} else {
			model = (D) value;
		}
		return model;

	}
}
