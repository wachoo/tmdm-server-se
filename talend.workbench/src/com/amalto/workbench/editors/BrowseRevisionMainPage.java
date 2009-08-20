package com.amalto.workbench.editors;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.models.IXObjectModelListener;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.providers.XObjectBrowserInput;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.XtentisPort;
import com.amalto.workbench.widgets.LabelCombo;

public class BrowseRevisionMainPage  extends AMainPage implements IXObjectModelListener {

	public BrowseRevisionMainPage(FormEditor editor) {
        super(
        		editor,
        		BrowseRevisionMainPage.class.getName(),
        		"Revision Browser " +((XObjectBrowserInput)editor.getEditorInput()).getName().replaceAll("\\[.*\\]", "").trim()
        ); 
	}
	@Override
	protected void createFormContent(IManagedForm managedForm) {

       
        	//sets the title
        	managedForm.getForm().setText(this.getTitle());
        	
        	//get the toolkit
        	FormToolkit toolkit = managedForm.getToolkit();
        	
        	//get the body
        	Composite composite = managedForm.getForm().getBody();
        	composite.setLayout(new GridLayout(2,false));
        	
    		//Revision
    		final LabelCombo comboRevision=new LabelCombo(toolkit,composite,"Revision ID",SWT.BORDER,1);
    		comboRevision.getCombo().setEditable(false);		

    		//Universe
    		final LabelCombo comboUniverse=new LabelCombo(toolkit,composite,"Universe",SWT.BORDER,1);
    		comboUniverse.getCombo().setEditable(false);
    		//List<String> dcs=Util.getCachedXObjectsNameSet(getXObject(), TreeObject.UNIVERSE);
    		List<String> dcs=Util.getChildren(getXObject().getServerRoot(), TreeObject.UNIVERSE);
    		comboUniverse.getCombo().setItems(dcs.toArray(new String[dcs.size()]));
    		comboUniverse.getCombo().add(IConstants.HEAD,0);
    		if(getXObject().getUniverse().trim().length()==0){
    			comboUniverse.getCombo().setText(IConstants.HEAD);
    		}else{
    			comboUniverse.getCombo().setText(getXObject().getUniverse());
    		}
    		try {
    			XtentisPort port = Util.getPort(
    					new URL(getXObject().getEndpointAddress()),
    					getXObject().getUniverse(),
    					getXObject().getUsername(),
    					getXObject().getPassword()
    			);
    			Map<String, List<String>> map=Util.getUniverseMap(port);
    			String name=getXObject().getDisplayName().replaceAll("\\[.*\\]", "");
    			List<String > revisions=map.get(name.trim());
    			if(revisions!=null)
    				comboRevision.getCombo().setItems(revisions.toArray(new String[revisions.size()]));
    			comboRevision.getCombo().add(IConstants.HEAD,0);
        		Matcher m=Pattern.compile("(.*?)\\[(.*?)\\]").matcher(getXObject().getDisplayName());
        		if(m.matches()){        			
        			String revision=m.group(2);
        			comboRevision.getCombo().setText(revision);
        		}
        		final Map<String, List<String>> map2=Util.getUniverseMap2(port);
        		comboRevision.getCombo().addSelectionListener(new SelectionListener(){

					public void widgetDefaultSelected(SelectionEvent e) {
												
					}

					public void widgetSelected(SelectionEvent e) {
						if(comboRevision.getCombo().getText().equals(IConstants.HEAD)){
							comboUniverse.getCombo().setText(IConstants.HEAD);
							return;
						}
						for(Entry<String, List<String>> entry: map2.entrySet()){
							if(entry.getValue().contains(comboRevision.getCombo().getText())){
								comboUniverse.getCombo().setText(entry.getKey());
								break;
							}
						}						
					}        			
        		});
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}

	}
	@Override
	protected void createCharacteristicsContent(FormToolkit toolkit,
			Composite mainComposite) {
	}

	@Override
	protected void commit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createActions() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void refreshData() {
		// TODO Auto-generated method stub
		
	}

	public void handleEvent(int type, TreeObject parent, TreeObject child) {
		// TODO Auto-generated method stub
		
	}

}
