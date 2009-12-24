package org.talend.mdm.workbench.enterprice.availablemodel;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.xsd.XSDElementDeclaration;
import org.talend.mdm.workbench.enterprice.actions.XSDDeleteAnnotationSchematronAction;
import org.talend.mdm.workbench.enterprice.actions.XSDDeleteValidationRulesAction;
import org.talend.mdm.workbench.enterprice.actions.XSDSetAnnotationSchematronAction;
import org.w3c.dom.Element;

import com.amalto.workbench.availablemodel.AbstractAvailableModel;
import com.amalto.workbench.editors.DataModelMainPage;

public class SchematronAvailableModel extends AbstractAvailableModel {

	public void fillContextMenu(Object obj, IMenuManager manager,DataModelMainPage page,String dataModelName) {
		//delete schematron annotation
		if( obj instanceof Element){
			Element e= (Element)obj;
			if("X_Schematron".equals(e.getAttribute("source"))){
				manager.add(new XSDDeleteAnnotationSchematronAction(page, dataModelName));
			} 
		}
					
		if(obj instanceof XSDElementDeclaration){
			manager.add(new XSDSetAnnotationSchematronAction(page, dataModelName));
			manager.add(new XSDDeleteValidationRulesAction(page));
		}
		
	}
	

	public void doubleClickOnElement(int type,DataModelMainPage page,String dataModelName) {
		switch (type){
		case 109:
			new XSDSetAnnotationSchematronAction(page,dataModelName).run();
			break;
		}
	}
}
