package com.amalto.workbench.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintCategory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDXPathDefinition;
import org.eclipse.xsd.XSDXPathVariety;

import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.providers.XSDTreeContentProvider;
import com.amalto.workbench.utils.Util;

public class XSDDeleteConceptWrapAction extends UndoAction{

	private TreeViewer viewer = null;
	private XSDDeleteConceptAction deleteConceptAction = null;
	
	private List<XSDConcreteComponent> delObjs = new ArrayList<XSDConcreteComponent>();
	private Map<Class, UndoAction> clsAction = new HashMap<Class, UndoAction>();
	
	private List<Class> extraClsToDel = new ArrayList<Class>();
	
	public XSDDeleteConceptWrapAction(DataModelMainPage page) {
		super(page);
		viewer = page.getTreeViewer();
		setImageDescriptor(ImageCache.getImage(EImage.DELETE_OBJ.getPath()));
	}
	
	public void regisDelAction(Class cls, UndoAction action)
	{
		clsAction.put(cls, action);
	}
	
	public void regisExtraClassToDel(Class cls)
	{
		extraClsToDel.add(cls);
	}
	
	public void clearExtraClassToDel()
	{
		extraClsToDel.clear();
	}
	
	public IStatus doAction() {
		
		List<IStatus> results = new ArrayList<IStatus>();
		try {
			IStructuredSelection selection = (IStructuredSelection)page.getTreeViewer().getSelection();
			if(delObjs.isEmpty()){
	            return Status.CANCEL_STATUS;
			}else{
				boolean sameType = checkInSameClassType(delObjs.toArray(), delObjs.get(0).getClass());
				String deleteLabel = "Are you sure you want to ";
				String elemDesc = ((Action)clsAction.get(delObjs.get(0).getClass())).getText();
				int backPos = elemDesc.indexOf(" ");

				if (delObjs.size() > 1)
					deleteLabel += elemDesc.substring(0, backPos)
							+ " these "
							+ delObjs.size() + " "
							+ (!sameType ? "Objects" : elemDesc
									.substring(backPos + 1)
									+ "s");
				else
					deleteLabel += elemDesc.substring(0, backPos)
							+ " the selected "
							+ (!sameType ? "Objects" : elemDesc
									.substring(backPos + 1));


				if (!MessageDialog.openConfirm(page.getSite().getShell(),
						"Delete Model", deleteLabel)) {
					return Status.CANCEL_STATUS;
				}
			}

			for (Iterator iterator = delObjs.iterator(); iterator.hasNext();) {
				Object toDel = (Object) iterator.next();
				UndoAction delExecute = null;
				boolean isElem = true;
				if (toDel instanceof XSDElementDeclaration) {
					XSDElementDeclaration decl = (XSDElementDeclaration) toDel;

					EList l = decl.getIdentityConstraintDefinitions();
					for (Iterator iter = l.iterator(); iter.hasNext();) {
						XSDIdentityConstraintDefinition icd = (XSDIdentityConstraintDefinition) iter
								.next();
						if (icd.getIdentityConstraintCategory().equals(
								XSDIdentityConstraintCategory.UNIQUE_LITERAL)) {
							isElem = false;
							break;
						}
					}
				}
				if (toDel instanceof XSDXPathDefinition)
				{
					XSDXPathDefinition xpath = (XSDXPathDefinition) toDel;
					if (!xpath.getVariety().equals(XSDXPathVariety.FIELD_LITERAL))
						continue;
				}
				
				delExecute = clsAction.get(toDel.getClass());
				if (isElem && toDel instanceof XSDElementDeclaration) {
					delExecute = clsAction.get(null);
				}
				
				if (delExecute instanceof XSDDeleteConceptAction && toDel instanceof XSDElementDeclaration)
				{
					((XSDDeleteConceptAction)delExecute).setXSDTODel((XSDElementDeclaration)toDel);
				}
				else if (delExecute instanceof XSDDeleteElementAction && toDel instanceof XSDElementDeclaration)
				{
					((XSDDeleteElementAction)delExecute).setXSDTODel((XSDElementDeclaration)toDel);
				}
				else if (delExecute instanceof XSDDeleteParticleAction && toDel instanceof XSDParticle)
				{
					((XSDDeleteParticleAction)delExecute).setXSDTODel((XSDParticle)toDel);
				}
				else if (delExecute instanceof XSDDeleteXPathAction && toDel instanceof XSDXPathDefinition)
				{
					((XSDDeleteXPathAction)delExecute).setXSDTODel((XSDXPathDefinition)toDel);
				}
				else if (delExecute instanceof XSDDeleteIdentityConstraintAction && toDel instanceof XSDIdentityConstraintDefinition)
				{
					((XSDDeleteIdentityConstraintAction)delExecute).setXSDTODel((XSDIdentityConstraintDefinition)toDel);
				}
				else if (delExecute instanceof XSDDeleteTypeDefinition && toDel instanceof XSDComplexTypeDefinition)
				{
					((XSDDeleteTypeDefinition)delExecute).setXSDTODel((XSDComplexTypeDefinition)toDel);
				}
				else if (delExecute instanceof XSDDeleteTypeDefinition && toDel instanceof XSDSimpleTypeDefinition)
				{
					((XSDDeleteTypeDefinition)delExecute).setXSDTODel((XSDSimpleTypeDefinition)toDel);
				}
				else
					return Status.CANCEL_STATUS;
				
				results.add(delExecute.doAction());
			}
       
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					page.getSite().getShell(),
					"Error", 
					"An error occured trying to remove Concept: "+e.getLocalizedMessage()
			);
			
			return (results.indexOf(Status.OK_STATUS)>= 0 ? Status.OK_STATUS : Status.CANCEL_STATUS);
		}	
		
		
		return  (results.indexOf(Status.OK_STATUS)>= 0 ? Status.OK_STATUS : Status.CANCEL_STATUS);
	}
	
	private void wrapDelObj(Object[] toDels) {
		clearDelData();
		for (Object del: toDels)
			delObjs.add((XSDConcreteComponent)del);
		if (delObjs != null)
		{
			refreshAction();
		}
	}


    /**
     * author: Fliu
	 * it is meant to support multiple deletions on data modules on key press
     * @param selections: tree node picking up in the data module view
     */
	public void prepareToDelSelectedItems(IStructuredSelection selections, TreeViewer treeView) {
		viewer = treeView;
		Object[] objs = selections.toArray();
		objs = filterSelectedItemsToDel(selections);
		wrapDelObj(objs);
	}
	/**
	 * Author: Fliu
	 * this fun is to filter out all the children listed in the selections, 
	 * all left is the top parent level ones in the selections
	 * @param selections
	 * @return all parent array with no corresponding children in the selection list
	 */
	private Object[] filterSelectedItemsToDel(IStructuredSelection selections) {
		Object[] objs = selections.toArray();
		List lst = new ArrayList();

		for (Object obj : objs) {
			for (Object objOther : objs) {
				if (obj == objOther) {
					continue;
				}
				Object[] offsprings = populateAllOffspring(objOther,
						new ArrayList());
				for (Object offspring : offsprings) {
					if (offspring == obj) {
						lst.add(obj);
					}
				}
			}
		}

		for (Object ca : objs) {
			if (lst.indexOf(ca) >= 0) {
				lst.remove(ca);
			} else {
				lst.add(ca);
			}
		}
		return lst.toArray();
	}
	
	/**
	 * Author: Fliu
	 * this fun is to populate all offsprings for a specific object
	 */
	private Object[] populateAllOffspring(Object obj, ArrayList offspringList) {
		XSDTreeContentProvider provider = (XSDTreeContentProvider) viewer
				.getContentProvider();
		Object[] offersprings = provider.getChildren(obj);

		for (Object subObj : offersprings) {
			if (!offspringList.contains(subObj))
			{
				offspringList.add(subObj);
				if (provider.hasChildren(subObj)) {
					populateAllOffspring(subObj, offspringList);
				}
			}
			else
			{
				continue;
			}

		}
		return offspringList.toArray();
	}
	
	private void refreshAction()
	{
		XSDConcreteComponent comp = (XSDConcreteComponent)delObjs.get(0);
		if (checkInSameClassType(delObjs.toArray(), comp.getClass()))
			setText(clsAction.get(comp.getClass()).getText() + (delObjs.size() > 1 ? "s" : ""));
		else
			setText("Delete Objects");
		setToolTipText("Delete Business Concepts");
	}
	
	private void clearDelData() {
		delObjs.clear();
	}
	
	private boolean checkInSameClassType(Object[] selects, Class type) {
		for (Object obj : selects) {
			if (obj.getClass() != type) {
				return false;
			}
		}

		if (selects[0] instanceof XSDElementDeclaration) {
			boolean isConcept = Util.checkConcept((XSDElementDeclaration) selects[0]);
			for (Object obj : selects) {
				if (Util.checkConcept((XSDElementDeclaration) obj) != isConcept) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean checkInDeletableType(Object[] selects)
	{
		clearDelData();
		
		for (Object obj : selects) {
			if (obj instanceof XSDElementDeclaration
					|| obj instanceof XSDParticle
					|| obj instanceof XSDIdentityConstraintDefinition) {
				continue;
			}
			else if (obj instanceof XSDXPathDefinition) {
				XSDXPathDefinition xpath = (XSDXPathDefinition) obj;
				if (xpath.getVariety().equals(XSDXPathVariety.FIELD_LITERAL))
					continue;
				else
					return false;
			}
			else if (extraClsToDel.indexOf(obj.getClass()) < 0)
				return false;
		}

		return true;
	}
	
	
	public boolean checkOutAllConcept(Object[] selections)
	{
		for (Object obj: selections)
		{
			if (!(obj instanceof XSDElementDeclaration))return false;
			if (!Util.checkConcept((XSDElementDeclaration)obj)) return false;
		}
		
		return true;
	}
	
	public Action outPutDeleteActions() {
		if (delObjs.size() == 0)
			return null;

		return this;
	}
	
	public void runWithEvent(Event event) {
		super.runWithEvent(event);
	}
	

}