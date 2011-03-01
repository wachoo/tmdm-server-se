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
package org.talend.mdm.webapp.itemsbrowser2.client.widget;

import java.io.Serializable;

import org.talend.mdm.webapp.itemsbrowser2.client.model.DataTypeConstants;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.PictureField;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.UrlField;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;

public class FieldCreator {


	
	public static <D extends Serializable, F extends Field<D>>  F createField(String dataType){
		F field = null;
		if(dataType.equals(DataTypeConstants.STRING)){
			TextField<String> textField = new TextField<String>();
			
			field = (F) textField;
		} else if (dataType.equals(DataTypeConstants.UUID)){
			
		} else if (dataType.equals(DataTypeConstants.AUTO_INCREMENT)){
			
		} else if (dataType.equals(DataTypeConstants.PICTURE)){
			PictureField pictureField = new PictureField();
			field = (F) pictureField;
		} else if (dataType.equals(DataTypeConstants.URL)){
			UrlField urlField = new UrlField();
			field = (F) urlField;
		} else {
			field = (F) new TextField<String>();
		}
		
		return field;
	}
}
