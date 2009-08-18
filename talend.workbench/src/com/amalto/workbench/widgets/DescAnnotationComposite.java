package com.amalto.workbench.widgets;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.dialogs.AnnotationLanguageLabelsDialog;
import com.amalto.workbench.editors.AMainPageV2;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;

/**
 * This class is meant to work out a composite populated with
 * label, button, text which stand next to each other
 * the composite will in the parent composite holder 
 * @author fliu
 *
 */

public class DescAnnotationComposite implements  SelectionListener{

	private Button annotationButton;
	private Text descriptionText;
	
	private Composite descAntionHolder;
    private AMainPageV2 accommodation;
    
    private String descriptionValue;
    private String dlgTitle;
    
    private LinkedHashMap<String, String> dataStore = new LinkedHashMap<String, String>();
    private static final String REGEXP_METACHARACTERS = "\\[([\\w]+)[\\s]*:[\\s]*([^]]*)\\]";  
    private static final Pattern DESC_PATTERN = Pattern.compile(REGEXP_METACHARACTERS);
    
	public DescAnnotationComposite(String labelName, String buttonName, FormToolkit toolkit, Composite parent, AMainPageV2 dialog)
	{
		descAntionHolder = toolkit.createComposite(parent);
		descAntionHolder.setLayoutData( new GridData(SWT.FILL,SWT.RIGHT,true,true,2,1));
		descAntionHolder.setLayout(new GridLayout(3,false));
		
		Label descriptionLabel = toolkit.createLabel(descAntionHolder, labelName, SWT.NULL);
        descriptionLabel.setLayoutData(
                new GridData(SWT.FILL,SWT.CENTER,false,true,1,1));
        
		annotationButton = toolkit.createButton(descAntionHolder, "", SWT.PUSH);
		annotationButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		annotationButton.addSelectionListener(this);
		annotationButton.setImage(ImageCache.getCreatedImage(EImage.DOTS_BUTTON.getPath()));
		annotationButton.setToolTipText("Set the Descriptions");
		
		descriptionText = toolkit.createText(descAntionHolder, "",SWT.BORDER|SWT.MULTI);
        descriptionText.setLayoutData(    
                 new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
         );
        descriptionText.addModifyListener(new ModifyListener() {
        	public void modifyText(ModifyEvent e) {
        		if (descriptionValue != null && !descriptionValue.equals(descriptionText.getText())) {
					accommodation.markDirty();
				}
        		descriptionValue = descriptionText.getText();
        		fillDataStore(descriptionText.getText());
        	}
        }); 
        
        accommodation = dialog;
        dlgTitle = "Set the Descriptions";
	}
	
	public void setAnnotationDialogTitle(String title)
	{
		dlgTitle = title;
	}
	
	public void setText(String text)
	{
		descriptionText.setText(text);
		descriptionValue = text;
		
		fillDataStore(text);
	}
	
	private void fillDataStore(String text)
	{
		dataStore.clear();
		boolean find = false;
		Matcher match = DESC_PATTERN.matcher(text);
		while (match.find())
		{	
			find = true;
			String country = match.group(1).trim().toLowerCase();
			String desc = match.group(2).trim();
			if (Util.iso2lang.get(country)!= null)
			{
				dataStore.put(country, desc);
			}
		}
		
		if (!find && !text.equals(""))
		{
			dataStore.put("en", text);
		}
	}
	
	public String getText()
	{
		return descriptionText.getText();
	}
	
	public void widgetSelected(SelectionEvent e)
	{
        AnnotationLanguageLabelsDialog dlg = new AnnotationLanguageLabelsDialog(
        		dataStore,
        		new DescAnnotationListener(),
				accommodation.getPartControl().getShell(),
				dlgTitle
		);
        
        dlg.setBlockOnOpen(true);
		dlg.open();
		
		if (dlg.getReturnCode() == Window.OK)  {
			
			dlg.close();
		}
	}
	
	public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
	
	public String commitChange()
	{
		return descriptionText.getText();
	}
	
	public Composite getComposite()
	{
		return descAntionHolder;
	}
	
	private class DescAnnotationListener implements SelectionListener{
		
		public DescAnnotationListener() {
			super();
		}
		public void widgetSelected(SelectionEvent e) {
			AnnotationLanguageLabelsDialog dlg = (AnnotationLanguageLabelsDialog)((Widget)e.getSource()).getData("dialog");
			if (dlg.getReturnCode() == Window.OK)  {
				String outPut = "";
				for (Map.Entry<String, String> m : dataStore.entrySet())
				{
					outPut += "[" + m.getKey().toUpperCase() + ":" + m.getValue() + "]";
				}
				
				if (!outPut.equals(descriptionText.getText()))
				{
					descriptionText.setText(outPut);
					accommodation.markDirty();
				}

	        }
			dlg.close();

		}
		public void widgetDefaultSelected(SelectionEvent e) {};
	}
}
