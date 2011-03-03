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
		    model = (D) value;
		} else if (dataType.equals(DataTypeConstants.URL)){
		    model = (D) value;
		} else {
			model = (D) value;
		}
		return model;

	}
}
