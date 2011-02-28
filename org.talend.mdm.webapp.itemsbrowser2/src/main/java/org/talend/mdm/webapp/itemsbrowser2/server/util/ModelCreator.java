// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.itemsbrowser2.server.util;

import java.io.Serializable;

import org.talend.mdm.webapp.itemsbrowser2.client.model.DataTypeConstants;
import org.talend.mdm.webapp.itemsbrowser2.client.model.PictureBean;

import com.extjs.gxt.ui.client.data.BaseModel;

/**
 * DOC chliu  class global comment. Detailled comment
 */
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
				pictureBean.setWidth(150);
				pictureBean.setHeight(90);
				pictureBean.setPreserveAspectRatio(true);
			}
			model = (D) pictureBean;
		} else if (dataType.equals(DataTypeConstants.URL)){
			String[] url = value.split("@@");
			BaseModel urlBean = new BaseModel();
			urlBean.set("name", url[0]);
			urlBean.set("address", url[1]);
			model = (D) urlBean;
		} else {
			model = (D) value;
		}
		return model;

	}
}
