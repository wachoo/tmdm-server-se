/*
 * Created on 27 oct. 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.talend.mdm.workbench.enterprice.editors;


import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.mdm.workbench.enterprice.dialog.ProcessResultsDialog;
import org.talend.mdm.workbench.enterprice.dialog.SetupTransformerInputVariablesDialog;

import com.amalto.workbench.dialogs.PluginDetailsDialog;
import com.amalto.workbench.dialogs.VariableDefinitionDialog;
import com.amalto.workbench.editors.AMainPageV2;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.Line;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.utils.EInputTemplate;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.WidgetUtils;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.BackgroundJobStatusType;
import com.amalto.workbench.webservices.WSBackgroundJob;
import com.amalto.workbench.webservices.WSBackgroundJobPK;
import com.amalto.workbench.webservices.WSByteArray;
import com.amalto.workbench.webservices.WSExecuteTransformerV2AsJob;
import com.amalto.workbench.webservices.WSExtractedContent;
import com.amalto.workbench.webservices.WSGetBackgroundJob;
import com.amalto.workbench.webservices.WSGetTransformerPluginV2Details;
import com.amalto.workbench.webservices.WSGetTransformerPluginV2SList;
import com.amalto.workbench.webservices.WSPipeline;
import com.amalto.workbench.webservices.WSPipelineTypedContentEntry;
import com.amalto.workbench.webservices.WSTransformerContext;
import com.amalto.workbench.webservices.WSTransformerContextPipeline;
import com.amalto.workbench.webservices.WSTransformerContextPipelinePipelineItem;
import com.amalto.workbench.webservices.WSTransformerPluginV2Details;
import com.amalto.workbench.webservices.WSTransformerPluginV2SList;
import com.amalto.workbench.webservices.WSTransformerPluginV2SListItem;
import com.amalto.workbench.webservices.WSTransformerPluginV2VariableDescriptor;
import com.amalto.workbench.webservices.WSTransformerProcessStep;
import com.amalto.workbench.webservices.WSTransformerV2;
import com.amalto.workbench.webservices.WSTransformerV2PK;
import com.amalto.workbench.webservices.WSTransformerVariablesMapping;
import com.amalto.workbench.webservices.WSTypedContent;
import com.amalto.workbench.webservices.XtentisPort;
import com.amalto.workbench.widgets.LabelCombo;

public class TransformerMainPage extends AMainPageV2 {
	static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	
	public final static String DEFAULT_VAR = "_DEFAULT_";
	public final static String DEFAULT_DISPLAY = "{}";
	
	public final static String TRANSFORMER_PLUGIN="amalto/local/transformer/plugin/";
	
	protected Text descriptionText;
	
	//protected Text inputText;
	protected Text stepText;
	//protected Text outputText;
	protected List stepsList;
	
	protected Label stepLabel;
	//protected Text jndiText;
	protected CCombo pluginsCombo;
	String currentPluginName;
	
	protected Text pluginDescription;
	protected TextViewer parametersTextViewer;
	
	SetupTransformerInputVariablesDialog transformerDialog =null;
	
	protected DropTarget windowTarget;	
	
	protected AbstractFormPart topPart;
	
	protected boolean refreshing = false;
	protected boolean comitting = false;
	
	protected String filePath;
	
	protected int currentPlugin = -1; 

	
	protected TreeMap<String, String> pluginDescriptions = new TreeMap<String, String>();
	
	protected TreeMap<String, java.util.List<String>> inputVariablesMap = new TreeMap<String, java.util.List<String>>();
	protected TreeMap<String, java.util.List<String>> outputVariablesMap = new TreeMap<String, java.util.List<String>>();
	private XtentisPort    port;
	private TransformerStepWidget stepWidget;
	private Button disabledButton;
	protected WSTransformerV2 transformer;
	private Composite specsComposite;
	private Section section;
	
	java.util.List<Line> cacheList; //remember the setup transformerinputvariablesdialog's input list

	
    public TransformerMainPage(FormEditor editor) {
        super(
        		editor,
        		TransformerMainPage.class.getName(),
        		"Transformer "+((XObjectEditorInput)editor.getEditorInput()).getName()
        		+Util.getRevision((TreeObject)((XObjectEditorInput)editor.getEditorInput()).getModel())
        );

    }
    
    public java.util.List<Line> getCacheList() {
		return cacheList;
	}

	public void setCacheList(java.util.List<Line> cacheList) {
		this.cacheList = cacheList;
	}

	public void execute(){
		try{
			WSTransformerContextPipelinePipelineItem []items=new WSTransformerContextPipelinePipelineItem[cacheList.size()];
			int i=0;
			for(Line line:cacheList){
				String variableName=line.keyValues.get(0).value;
				String contentType=line.keyValues.get(1).value;
				String value=line.keyValues.get(2).value;
				
				items[i] = new WSTransformerContextPipelinePipelineItem(
						variableName,
						new WSTypedContent(
							null,
							new WSByteArray(value.getBytes("utf-8")),  
							contentType
						)
				);		
				i++;
			}

			final WSBackgroundJobPK jobPK=port.executeTransformerV2AsJob(
					new WSExecuteTransformerV2AsJob(
						new WSTransformerContext(
							new WSTransformerV2PK(transformer.getName()),
							new WSTransformerContextPipeline(items ),
							null)
						));
			
			IRunnableWithProgress progress=new IRunnableWithProgress(){
				
				WSBackgroundJob job=null;
				
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					/******************************************
					 * Watch the Background Job
					 ******************************************/
					try{
						boolean firstTime = true;
						do {							
							if (firstTime) {
								Thread.sleep(1500L);
								firstTime = false;
							} else {
								Thread.sleep(5000L);
							}
							
							if (monitor.isCanceled()) {
								throw new InterruptedException("User Cancel");
							}
							
							job  =  port.getBackgroundJob(new WSGetBackgroundJob(jobPK.getPk()));	
							monitor.subTask(job.getMessage());
	
						} while (
									job.getStatus().equals(BackgroundJobStatusType.RUNNING) 
									|| job.getStatus().equals(BackgroundJobStatusType.SCHEDULED)
									);
	
						if (job.getStatus().equals(BackgroundJobStatusType.STOPPED)) {
							getSite().getShell().getDisplay().syncExec(
								new Runnable() {
									public void run() {
										MessageDialog.openError(
											TransformerMainPage.this.getEditor().getSite().getShell(), 
											"ERROR processing '"+transformer.getName()+"'", 
											job.getMessage()
										);
									    
									}
								}
							);
							throw new XtentisException("The job was stopped. "+job.getMessage());
						}
						
						monitor.worked(1);			
						monitor.done();
						
						
						/******************************************
						 * Build the result console
						 ******************************************/
	
						//Auto sorts the entries
						final TreeMap pipeline = new TreeMap<String, WSExtractedContent>();
						WSPipeline wsPipeline = job.getPipeline();
						WSPipelineTypedContentEntry[] entries = wsPipeline.getTypedContentEntry();
						for (int i = 0; i < entries.length; i++) {
							pipeline.put(entries[i].getOutput(), entries[i].getWsExtractedContent());
						}
						getSite().getShell().getDisplay().asyncExec (new Runnable () {
							public void run () {
								try {
									/*
									ProcessResultsPage page = new ProcessResultsPage(editor,pipeline);
									parent.editor.addPage(page);
									parent.editor.setActivePage(page.getId());
									*
									*parent.editor.getEditorSite().getShell()
									*/
									//Shell shell = new Shell(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
									ProcessResultsDialog dialog = new ProcessResultsDialog(getSite().getShell() ,"Results at "+sdf.format(new Date(System.currentTimeMillis())), pipeline);
									dialog.setBlockOnOpen(false);
									dialog.open();
								} catch (Exception e) {
									e.printStackTrace();
									throw new RuntimeException(e);
								}
							}
						});	
					
					}catch(Exception e1){
						e1.printStackTrace();
					}					
				}				
			};
			
			new ProgressMonitorDialog(TransformerMainPage.this.getSite().getWorkbenchWindow().getShell()).run(
					true,  //fork 
					true,
					progress
			);
			
		}catch(Exception e1){
			e1.printStackTrace();
		}

    }
    @Override
	protected void createCharacteristicsContent(final FormToolkit toolkit, Composite topComposite) {
    	try {
    	    port=Util.getPort(getXObject());
    	    transformer = (WSTransformerV2) getXObject().getWsObject();
    	    
            //Description and File Process
            Composite descriptionComposite = toolkit.createComposite(topComposite,SWT.NONE);
            descriptionComposite.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
            );
            descriptionComposite.setLayout(   new GridLayout(3 ,false ));

            //description
            Label descriptionLabel = toolkit.createLabel(descriptionComposite, "Description", SWT.NULL);
            descriptionLabel.setLayoutData(
                    new GridData(SWT.FILL,SWT.CENTER,false,true,1,1)
            );
            descriptionText = toolkit.createText(descriptionComposite, "",SWT.BORDER|SWT.MULTI);
            descriptionText.setLayoutData(    
                    new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
            );
            ((GridData)descriptionText.getLayoutData()).minimumHeight = 30;
            descriptionText.addModifyListener(new ModifyListener() {
            	public void modifyText(ModifyEvent e) {
            		if (refreshing) return;
            		//commit as we go
            		TransformerMainPage.this.comitting= true;
            		//((WSTransformerV2)getXObject().getWsObject())
            		transformer.setDescription(descriptionText.getText());
            		TransformerMainPage.this.comitting= false;
            		//markDirty();
            		markDirtyWithoutCommit();
            	}
            });
            
            
            //File Process
            Button processButton = toolkit.createButton(descriptionComposite,"",SWT.PUSH);
            processButton.setLayoutData(
                    new GridData(SWT.BEGINNING,SWT.CENTER,false,false,1,1)
            );
            processButton.setImage(ImageCache.getCreatedImage(EImage.RUN_EXC.getPath()));
            processButton.setToolTipText("Execute...");
            processButton.addSelectionListener(new SelectionListener() {
            	public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
            	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
            		try {
            			//check if we have a step to perfom
            			WSTransformerProcessStep[] steps = //((WSTransformerV2)getXObject().getWsObject())
            			transformer.getProcessSteps();
            			if ((steps==null) || (steps.length == 0)) {
            				MessageDialog.openError(TransformerMainPage.this.getSite().getShell(), "Unable to process a file", "The transformer must have at least one step!");
            				return;
            			}
            			//perform save
            			if (TransformerMainPage.this.getEditor().isDirty()) {
            				if (MessageDialog.openConfirm(TransformerMainPage.this.getSite().getShell(), "Executing the Transformer", "The Transformer was changed and will be executed using the saved version.\nSave the transformer before executing it?"))
            					TransformerMainPage.this.getEditor().doSave(new NullProgressMonitor());
            			}
            			//Open form Dialog	   
            			if(transformerDialog==null){
	            			transformerDialog=new SetupTransformerInputVariablesDialog(
	            				TransformerMainPage.this.getSite().getShell(),
	            				toolkit,
	            				getXObject(), 
	            				TransformerMainPage.this
	            			);
	            			transformerDialog.create();
	            			transformerDialog.getShell().setText("Setup Transformer's input variables");
            			}
            			transformerDialog.open();

            		} catch (Exception ex) {
            			ex.printStackTrace();
            		}
            	};
            });
                        
            
            //make the Page window a DropTarget - we need to dispose it
            windowTarget = new DropTarget(this.getPartControl(), DND.DROP_MOVE);
            windowTarget.setTransfer(new Transfer[]{TextTransfer.getInstance()});
            windowTarget.addDropListener(new DCDropTargetListener());
            

           
            //Sequence
            Composite sequenceGroup = this.getNewSectionComposite("Steps Sequence");
            sequenceGroup.setLayout(new GridLayout(1,false));

            Composite sequenceComposite = toolkit.createComposite(sequenceGroup,SWT.NONE);
            sequenceComposite.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
            );
            sequenceComposite.setLayout(new GridLayout(6,false));
            

            Label l3 = toolkit.createLabel(sequenceComposite, "Step Description", SWT.NULL);
            l3.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,true,1,1));
          
            stepText = toolkit.createText(sequenceComposite, "",SWT.BORDER|SWT.SINGLE);
            stepText.setLayoutData(    
                    new GridData(SWT.FILL,SWT.FILL,true,true,4,1)
            );
            stepText.addKeyListener(new KeyListener(){

				public void keyPressed(KeyEvent e) {
					// TODO Auto-generated method stub
					
				}

				public void keyReleased(KeyEvent e) {
					if(e.keyCode == 13){ //enter
						performAdd();
						int index = stepsList.getItemCount()-1;
						performSelect(index);
					}					
				}
            	
            });
            Button addStepButton = toolkit.createButton(sequenceComposite,"",SWT.PUSH);
            addStepButton.setLayoutData(
                    new GridData(SWT.CENTER,SWT.FILL,false,true,1,1)
            );
            addStepButton.setToolTipText("Add");
            addStepButton.setImage(ImageCache.getCreatedImage(EImage.ADD_OBJ.getPath()));
            addStepButton.addSelectionListener(new SelectionListener() {
            	public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
            	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
            		//commit as we go
            		performAdd();
            	};
            });
            
            stepsList = new List(sequenceComposite,SWT.V_SCROLL | SWT.BORDER);
            stepsList.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,true,true,5,1)
            );
            ((GridData)stepsList.getLayoutData()).heightHint = 40;
            
            /*DragSource stepsSource = new DragSource(stepsList,DND.DROP_MOVE);
            stepsSource.setTransfer(new Transfer[]{TextTransfer.getInstance()});
            stepsSource.addDragListener(new DCDragSourceListener());*/
            
            stepsList.addSelectionListener(new SelectionListener() {
            	public void widgetDefaultSelected(SelectionEvent e) {widgetSelected(e);}
            	public void widgetSelected(SelectionEvent e) {
            		performSelect(stepsList.getSelectionIndex());
            	}
            });


            
            wrap.Wrap(this, stepsList);
            stepsList.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
					if (stepsList.getSelectionIndex() >= 0)
					{
						refreshStep(stepsList.getSelectionIndex());
						section.setVisible(true);
					}
				}
				public void focusLost(FocusEvent e) {
					}
				}
            );
            
            Composite stepUpDownComposite = toolkit.createComposite(sequenceComposite,SWT.NONE);
            stepUpDownComposite.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
            );
            stepUpDownComposite.setLayout(new GridLayout(1,false));
            
            Button stepUpButton = toolkit.createButton(stepUpDownComposite,"",SWT.PUSH | SWT.CENTER);
            stepUpButton.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
            );
            stepUpButton.setImage(ImageCache.getCreatedImage(EImage.PREV_NAV.getPath()));
            stepUpButton.setToolTipText("Move up the selected item");
            stepUpButton.addSelectionListener(new SelectionListener() {
            	public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
            	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
            		int index =TransformerMainPage.this.stepsList.getSelectionIndex();
            		if (index>0) {
						//commit as we go
						TransformerMainPage.this.comitting= true;
            			String val = TransformerMainPage.this.stepsList.getItem(index);
            			TransformerMainPage.this.stepsList.remove(index);
            			TransformerMainPage.this.stepsList.add(val, index-1);
            			TransformerMainPage.this.stepsList.select(index-1);            			
            			WSTransformerV2 wsTransformer = transformer;//(WSTransformerV2)getXObject().getWsObject(); 
	            		ArrayList<WSTransformerProcessStep> list = new ArrayList<WSTransformerProcessStep>(
	            				Arrays.asList(wsTransformer.getProcessSteps())
	            		);
	            		WSTransformerProcessStep spec = list.get(index);
	            		list.remove(index);
	            		list.add(index-1, spec);
	            		performSelect(index-1);
	            		wsTransformer.setProcessSteps(list.toArray(new WSTransformerProcessStep[list.size()]));
	            		TransformerMainPage.this.comitting= false;
	            		TransformerMainPage.this.stepsList.forceFocus();
	            		markDirty();
            		}
            	};
            });
            Button stepDownButton = toolkit.createButton(stepUpDownComposite,"",SWT.PUSH | SWT.CENTER);
            stepDownButton.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
            );
            stepDownButton.setImage(ImageCache.getCreatedImage(EImage.NEXT_NAV.getPath()));
            stepDownButton.setToolTipText("Move down the selected item");
            stepDownButton.addSelectionListener(new SelectionListener() {
            	public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
            	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
            		int index =TransformerMainPage.this.stepsList.getSelectionIndex();
            		if ((index>=0) && (index < TransformerMainPage.this.stepsList.getItemCount()-1)) {
						//commit as we go
						TransformerMainPage.this.comitting= true;
            			String val = TransformerMainPage.this.stepsList.getItem(index);
            			TransformerMainPage.this.stepsList.remove(index);
            			TransformerMainPage.this.stepsList.add(val, index+1);
            			TransformerMainPage.this.stepsList.select(index+1);            			
            			WSTransformerV2 wsTransformer = transformer;//(WSTransformerV2)getXObject().getWsObject(); 
	            		ArrayList<WSTransformerProcessStep> list = new ArrayList<WSTransformerProcessStep>(
	            				Arrays.asList(wsTransformer.getProcessSteps())
	            		);
	            		WSTransformerProcessStep spec = list.get(index);
	            		list.remove(index);
	            		list.add(index+1, spec);
	            		wsTransformer.setProcessSteps(list.toArray(new WSTransformerProcessStep[list.size()]));
	            		TransformerMainPage.this.comitting= false;
	            		TransformerMainPage.this.stepsList.forceFocus();
	            		markDirty();
            		}
            	};
            });
            Button deleteStepButton = toolkit.createButton(stepUpDownComposite,"",SWT.PUSH | SWT.CENTER);
            deleteStepButton.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
            );
            deleteStepButton.setImage(ImageCache.getCreatedImage(EImage.DELETE_OBJ.getPath()));
            deleteStepButton.setToolTipText("Delete the selected item");
            deleteStepButton.addSelectionListener(new SelectionListener() {
            	public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
            	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
            		int index =TransformerMainPage.this.stepsList.getSelectionIndex();
            		if ((index>=0) && (index < TransformerMainPage.this.stepsList.getItemCount())) {
                		removeStep(index);
            		}
            	};
            });
    
            //Plugin Specifications
            //Sequence
            section = this.getNewSection("Step Specification");
            section.setVisible(false);

            sequenceGroup.setLayout(new GridLayout(4,false));
	        disabledButton= toolkit.createButton((Composite)section.getClient(), "Disabled", SWT.CHECK);
	        disabledButton.setLayoutData(
	                new GridData(SWT.LEFT,SWT.FILL,false,true,4,1)
	        );
            
	       specsComposite = toolkit.createComposite((Composite)section.getClient(),SWT.NULL);
	        specsComposite.setLayoutData(
	                new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
	        );
	        specsComposite.setLayout(new GridLayout(4,false));

	        disabledButton.addSelectionListener(new SelectionAdapter(){
	        	@Override
	        	public void widgetSelected(SelectionEvent e) {	        		
	        		WidgetUtils.enable(specsComposite, !disabledButton.getSelection());
	        		markDirty();
	        		if(stepsList.getSelectionIndex()>=0)
	        		transformer.getProcessSteps()[stepsList.getSelectionIndex()].setDisabled(disabledButton.getSelection());
	        	}
	        });
	        stepLabel = toolkit.createLabel(specsComposite, "", SWT.NULL);
	        stepLabel.setLayoutData(
	                new GridData(SWT.FILL,SWT.FILL,true,true,4,1)
	        );
	        FontData fd = stepLabel.getFont().getFontData()[0];
	        fd.setStyle(SWT.BOLD);
	        stepLabel.setFont(new Font(Display.getDefault(),fd));
	        

            stepWidget=new TransformerStepWidget(toolkit,specsComposite);
            stepWidget.create();
	        Group parametersGroup = new Group(specsComposite,SWT.SHADOW_NONE);
	        parametersGroup.setText("Parameters");
	        parametersGroup.setLayoutData(
	                new GridData(SWT.FILL,SWT.FILL,true,true,4,1)
	        );
	        ((GridData)parametersGroup.getLayoutData()).minimumHeight = 300;
	        parametersGroup.setLayout(new GridLayout(1,true));
	        
	        parametersTextViewer = new SourceViewer(parametersGroup, new VerticalRuler(10), SWT.V_SCROLL);
	        parametersTextViewer.getControl().setLayoutData(
	                new GridData(SWT.FILL,SWT.FILL,true,true,4,1)
	        );
	        WidgetUtils.initRedoUndo(parametersTextViewer);
	        parametersTextViewer.addTextListener(new ITextListener() {
	        	public void textChanged(TextEvent event) {
	        		if (refreshing) return;
	        		if (TransformerMainPage.this.stepsList.getSelectionIndex()==-1) return;
	        		//commit as we go
	        		TransformerMainPage.this.comitting= true;
	        		//((WSTransformerV2)getXObject().getWsObject())
	        		transformer.getProcessSteps()[stepsList.getSelectionIndex()].setParameters(parametersTextViewer.getDocument().get());
	        		TransformerMainPage.this.comitting= false;
	        		markDirty();            		
	        	}

	        });          
            refreshData();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    protected void performSelect(int index){		
		currentPlugin = index;
		if (index>=0) {
			section.setVisible(true);
			refreshStep(index);
		}
    }
    protected void performAdd(){
		try {
			if(stepText.getText().trim().length()==0) return;
    		TransformerMainPage.this.comitting= true;

       		TransformerMainPage.this.stepsList.add(

       				TransformerMainPage.this.stepText.getText()

       		);
    		WSTransformerV2 wsTransformer = transformer;//(WSTransformerV2)getXObject().getWsObject();
    		ArrayList<WSTransformerProcessStep> list = new ArrayList<WSTransformerProcessStep>();
    		if (wsTransformer.getProcessSteps() != null) { 
        		list = new ArrayList<WSTransformerProcessStep>(
        				Arrays.asList(wsTransformer.getProcessSteps())
        		);
    		}
    		list.add(new WSTransformerProcessStep(
    				"",
    				TransformerMainPage.this.stepText.getText(),
    				"",
    				new WSTransformerVariablesMapping[0],
    				new WSTransformerVariablesMapping[0],		            				
    				false
    		));
    		
    		wsTransformer.setProcessSteps(list.toArray(new WSTransformerProcessStep[list.size()]));
    		TransformerMainPage.this.comitting= false;
    		int index = TransformerMainPage.this.stepsList.getItemCount()-1;
			TransformerMainPage.this.stepsList.select(index);
			refreshStep(index);
			TransformerMainPage.this.stepsList.forceFocus();
    		markDirty();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
    	
    }
    protected void refreshStep(int index) {
    	if (index < 0) {
			stepWidget.inputViewer.setInput(new ArrayList<WSTransformerVariablesMapping>());
			stepWidget.outputViewer.setInput(new ArrayList<WSTransformerVariablesMapping>());
			return;
    	}
		TransformerMainPage.this.refreshing = true;
		
		stepLabel.setText(transformer.getProcessSteps()[index].getDescription());
		
		String jndi = transformer.getProcessSteps()[index].getPluginJNDI().replaceAll(TRANSFORMER_PLUGIN, "");
		pluginsCombo.setText(jndi);
		currentPluginName=jndi;
		pluginDescription.setText(pluginDescriptions.get(jndi)== null ? "" : pluginDescriptions.get(jndi));
		stepText.setText("");
		parametersTextViewer.setDocument(
				new Document(
						transformer.getProcessSteps()[index].getParameters()
				)
		);
		stepWidget.setProcessStep(transformer.getProcessSteps()[index], index);
		disabledButton.setSelection(transformer.getProcessSteps()[index].getDisabled());
		WidgetUtils.enable(specsComposite, !disabledButton.getSelection());
		
		TransformerMainPage.this.refreshing = false;
    }
    
    
    protected void removeStep(int index) {
		WSTransformerV2 wsTransformer = transformer;

		//clean up boxes at the bottom

		pluginsCombo.setText("");
		pluginsCombo.select(-1);
		
		parametersTextViewer.setDocument(
				new Document("")
		);

		TransformerMainPage.this.comitting= true;
		TransformerMainPage.this.stepsList.remove(index);
		TransformerMainPage.this.stepsList.select(index-1);
		refreshStep(stepsList.getSelectionIndex());
		TransformerMainPage.this.stepsList.forceFocus();
		
		//commit as we go		
		ArrayList<WSTransformerProcessStep> list = new ArrayList<WSTransformerProcessStep>(
				Arrays.asList(wsTransformer.getProcessSteps())
		);
		list.remove(index);
		currentPlugin = stepsList.getSelectionIndex();
		wsTransformer.setProcessSteps(list.toArray(new WSTransformerProcessStep[list.size()]));
		TransformerMainPage.this.comitting= false;
		markDirty();
    }
    

    public void update(Observable o, Object arg)
    {
    	if (arg != null
				&& (arg == stepsList || arg == stepWidget.inputViewer || arg == stepWidget.outputViewer)) {
			deleteItems(arg);
		}

    }
    
    private void deleteItems(Object view)
    {
    	if (view == stepsList)
    	{
			int[] index = stepsList.getSelectionIndices();
			boolean firstPos = false;
			for (int i = index.length - 1; i >= 0; i--) {
				if (index[i] == 0) {
					firstPos = true;
				}
				removeStep(index[i]);
			}

			if (stepsList.getItemCount() == 0) {
				section.setVisible(false);
			}
			if (stepsList.getItemCount() >= 1 && firstPos) {
				stepsList.select(0);
				refreshStep(0);
			}	
    	}
    	else if (view == stepWidget.inputViewer || view == stepWidget.outputViewer)
    	{
    		TableViewer viewer = (TableViewer)view;
			IStructuredSelection selections = (IStructuredSelection) viewer.getSelection();
			java.util.List list = (java.util.List) Arrays.asList(selections.toArray());
			if (list.size() == 0)return;
			java.util.List<WSTransformerVariablesMapping> items=(java.util.List<WSTransformerVariablesMapping>)viewer.getInput();
			items.removeAll(list);
			
			if(view == stepWidget.inputViewer)
				stepWidget.processStep.setInputMappings(items.toArray(new WSTransformerVariablesMapping[items.size()]));
			else
				stepWidget.processStep.setOutputMappings(items.toArray(new WSTransformerVariablesMapping[items.size()]));
			//refresh
			viewer.refresh();
			//mark for update
			markDirty();
    	}
    }
	protected void refreshData() {
		try {
//			System.out.println("refreshData() ");
			if (this.comitting) return;
			
			this.refreshing = true;
			
			WSTransformerV2 wsTransformer = (WSTransformerV2) (getXObject().getWsObject());    	
			
			descriptionText.setText(wsTransformer.getDescription() == null ? "" : wsTransformer.getDescription());
			
			stepsList.removeAll();
			WSTransformerProcessStep[] specs =  wsTransformer.getProcessSteps();
			if (specs != null) {
				for (int i = 0; i < specs.length; i++) {
					stepsList.add(
	           				specs[i].getDescription()						
					);	
				}
			}

			stepsList.select(currentPlugin);
			if(stepsList.getItemCount()>0 && stepsList.getSelectionIndex()==-1){
				refreshStep(0);
			}else{
				refreshStep(stepsList.getSelectionIndex());
			}
			stepsList.forceFocus();
            this.refreshing = false;

		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(this.getSite().getShell(), "Error refreshing the page", "Error refreshing the page: "+e.getLocalizedMessage());
		}    	
	}
	
	protected void commit() { 
		//changes are committed as we go
	}

	protected void createActions() {
	}


	public void textChanged(TextEvent event) {
		markDirty();
	}

	public void dispose() {
		super.dispose();
		if (parametersTextViewer.getUndoManager() != null)
		  parametersTextViewer.getUndoManager().disconnect();
		windowTarget.dispose();
	}
	@Override
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		if(stepsList.getItemCount()>0 && currentPlugin ==-1){
			refreshStep(0);
		}else{
			refreshStep(currentPlugin);
		}
		parametersTextViewer.getUndoManager().disconnect();
	}
	
	class TransformerStepWidget {

		FormToolkit toolkit;
		Composite parent;
		
		
		CCombo inputVariables;
		CCombo inputParams;
		
		CCombo outputParams;
		CCombo outputVariables;
		private Composite mainComposite;
		
		private Button inputLinkButton;
		private Button outputLinkButton;
		
		WSTransformerProcessStep processStep;
		private TableViewer inputViewer;
		private TableViewer outputViewer;
		
		private KeyListener variableListener;
		private MouseTrackAdapter mouseListener;
		
		Set<String> availableVariables=new HashSet<String>();
		public TransformerStepWidget(FormToolkit toolkit,Composite parent){
			this.toolkit=toolkit;
			this.parent=parent;

		}
		
		public WSTransformerProcessStep getProcessStep() {
			return processStep;
		}

		public void setProcessStep(WSTransformerProcessStep processStep,int index) {
			this.processStep = processStep;
			//reset the available variables
			availableVariables.clear();
			for(int i=0; i<=index; i++){
				if(transformer.getProcessSteps()[i].getDisabled()) continue;
				for(WSTransformerVariablesMapping mapping:transformer.getProcessSteps()[i].getInputMappings()){
					availableVariables.add(mapping.getPipelineVariable()==null?DEFAULT_VAR:mapping.getPipelineVariable());
				}
				for(WSTransformerVariablesMapping mapping:transformer.getProcessSteps()[i].getOutputMappings()){
					availableVariables.add(mapping.getPipelineVariable()==null?DEFAULT_VAR:mapping.getPipelineVariable());
				}
			}
			refreshViewers();
			refreshCombo();
		}

		private TableViewer createViewer(final java.util.List<String> columns,Composite parent, final boolean isInput){
	        Table table =new Table(parent,SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER|SWT.FULL_SELECTION);
	        final TableViewer viewer = new TableViewer(table);
	        table.setToolTipText("Press 'DEL' key to delete the selected item.");
	        table.setLayoutData(    
	                new GridData(SWT.FILL,SWT.FILL,true,true,3,1)
	        );
	        ((GridData)viewer.getControl().getLayoutData()).heightHint=60;
	        
	        //table.setLayoutData(new GridData(GridData.FILL_BOTH));
	        for(String column:columns){
	        	TableColumn tableColumn=new TableColumn(table, SWT.CENTER);
	        	tableColumn.setText(column);
	        	tableColumn.setWidth(200);
	        	tableColumn.pack();
	        }  
	        table.setHeaderVisible(true);
	        table.setLinesVisible(true);

	           
	        //set the content provider
	        viewer.setContentProvider(new IStructuredContentProvider() {
	        	public void dispose() {}
	        	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
	        	public Object[] getElements(Object inputElement) {
	        		if(inputElement==null)return null;
	        		java.util.List<WSTransformerVariablesMapping> lines = (java.util.List<WSTransformerVariablesMapping>)inputElement;
	        		return lines.toArray(new WSTransformerVariablesMapping[lines.size()]);
	        	}
	        });
	        
	        //set the label provider
	        viewer.setLabelProvider(new ITableLabelProvider() {
	        	public boolean isLabelProperty(Object element, String property) {return false;}
	        	public void dispose() {}
	        	public void addListener(ILabelProviderListener listener) {}
	        	public void removeListener(ILabelProviderListener listener) {}
	        	public String getColumnText(Object element, int columnIndex) {
	        		WSTransformerVariablesMapping line = (WSTransformerVariablesMapping) element;
	        		switch(columnIndex){
	        		
	        		case 0:
	        			return isInput?line.getPipelineVariable():line.getPluginVariable();
	        		case 1:
	        			return isInput?line.getPluginVariable():line.getPipelineVariable();
	        				
	        		}
	        		return "";
	        	}
	        	public Image getColumnImage(Object element, int columnIndex) {return null;}
	        });

	        // Set the column properties
	        viewer.setColumnProperties(columns.toArray(new String[columns.size()]));
	    	
	        return viewer;
		}
		void refreshViewers(){
			if(processStep!=null){
				java.util.List<WSTransformerVariablesMapping> items=new ArrayList<WSTransformerVariablesMapping>();
				for(WSTransformerVariablesMapping map: processStep.getInputMappings()){
					items.add(map);
				}
				inputViewer.setInput(items);
				
				items=new ArrayList<WSTransformerVariablesMapping>();
				for(WSTransformerVariablesMapping map: processStep.getOutputMappings()){
					items.add(map);
				}
				outputViewer.setInput(items);
				
			}
		}
		void refreshCombo(){
			inputParams.removeAll();
			java.util.List<String> list=inputVariablesMap.get(pluginsCombo.getText().trim());
			if(list!=null){
				inputParams.setItems(list.toArray(new String[list.size()]));
			}
			outputParams.removeAll();
			list=outputVariablesMap.get(pluginsCombo.getText().trim());
			if(list!=null){
				outputParams.setItems(list.toArray(new String[list.size()]));
			}

			inputVariables.setItems(availableVariables.toArray(new String[availableVariables.size()]));
			outputVariables.setItems(availableVariables.toArray(new String[availableVariables.size()]));
		}
		private boolean isExist(java.util.List<WSTransformerVariablesMapping> list, String parameterName){
			for(WSTransformerVariablesMapping map: list){
				if(map.getPluginVariable().equals(parameterName)){
					MessageDialog.openInformation(null, "Warning", parameterName+" already Exists!");
					return true;
				}
			}
			return false;
		}
		private void createInput(){
			Composite inputComposite= toolkit.createComposite(mainComposite,SWT.BORDER);
			inputComposite.setLayoutData(
	                new GridData(SWT.FILL,SWT.RIGHT,true,true,1,1)
	        );		
			inputComposite.setLayout(new GridLayout(3,false));
			
			LabelCombo inputV=new LabelCombo(toolkit,inputComposite,"Input Variables",SWT.BORDER,1);		
			inputVariables=inputV.getCombo();
			inputVariables.addKeyListener(variableListener);
			inputVariables.getTabList()[0].addMouseTrackListener(mouseListener);
			
	        inputLinkButton = toolkit.createButton(inputComposite,"",SWT.PUSH | SWT.CENTER);
	        inputLinkButton.setImage(ImageCache.getCreatedImage(EImage.SYNCED.getPath()));
	        inputLinkButton.setToolTipText("Link");
	        inputLinkButton.setToolTipText("Add a link for Input Variables and Transformer Plugin's Input Parameters");
	        inputLinkButton.setLayoutData(
	                new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
	        );
	        inputLinkButton.addSelectionListener(new SelectionAdapter(){
	        	@Override
	        	public void widgetSelected(SelectionEvent e) {
	        		if(inputParams.getText().length()==0) return;
        			java.util.List<WSTransformerVariablesMapping> items=(java.util.List<WSTransformerVariablesMapping>)inputViewer.getInput();
	        		if(isExist(items, inputParams.getText())) return;
	        		
        			WSTransformerVariablesMapping line = new WSTransformerVariablesMapping();
        			if(inputVariables.getText().trim().length()>0)
        				line.setPipelineVariable(inputVariables.getText());
        			else
        				line.setPipelineVariable(DEFAULT_VAR);
        			if(inputParams.getText().trim().length()>0)
        				line.setPluginVariable(inputParams.getText());
        			
        			items.add(line);
        			processStep.setInputMappings(items.toArray(new WSTransformerVariablesMapping[items.size()]));
        			inputViewer.refresh();
        			if(line.getPipelineVariable()!=null)availableVariables.add(line.getPipelineVariable());
        			outputVariables.setItems(availableVariables.toArray(new String[availableVariables.size()]));
        			markDirty();
	        	}
	        });
			LabelCombo inputP=new LabelCombo(toolkit,inputComposite,"Input Parameters",SWT.BORDER|SWT.READ_ONLY,1);		
			inputParams=inputP.getCombo();
			
			//create table
			java.util.List<String> columns=new ArrayList<String>();
			columns.add("Input Variables");
			columns.add("Input Parameters");
			inputViewer=createViewer(columns,inputComposite,true);
			wrap.Wrap(TransformerMainPage.this, inputViewer);
		}

		private void createOutput(){
			Composite outputComposite= toolkit.createComposite(mainComposite,SWT.BORDER);
			outputComposite.setLayoutData(
	                new GridData(SWT.FILL,SWT.RIGHT,true,true,1,1)
	        );		
			outputComposite.setLayout(new GridLayout(3,false));

			LabelCombo outputP=new LabelCombo(toolkit,outputComposite,"Output Parameters",SWT.BORDER|SWT.READ_ONLY,1);		
			outputParams=outputP.getCombo();

			
	        outputLinkButton = toolkit.createButton(outputComposite,"",SWT.PUSH | SWT.CENTER);
	        outputLinkButton.setImage(ImageCache.getCreatedImage(EImage.SYNCED.getPath()));
	        outputLinkButton.setToolTipText("Link");
	        outputLinkButton.setToolTipText("Add a link for output Variables and output Parameters");
	        outputLinkButton.setLayoutData(
	                new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
	        );
	        outputLinkButton.addSelectionListener(new SelectionAdapter(){
	        	@Override
	        	public void widgetSelected(SelectionEvent e) {
	        		if(outputParams.getText().length()==0) return;
        			java.util.List<WSTransformerVariablesMapping> items=(java.util.List<WSTransformerVariablesMapping>)outputViewer.getInput();
	        		if(isExist(items, outputParams.getText())) return;
	        		
        			WSTransformerVariablesMapping line = new WSTransformerVariablesMapping();
        			if(outputVariables.getText().length()>0)
        				line.setPipelineVariable(outputVariables.getText());
        			else
        				line.setPipelineVariable(DEFAULT_VAR);
        			
        			if(outputParams.getText().trim().length()>0)
        				line.setPluginVariable(outputParams.getText());
         			items.add(line);
        			processStep.setOutputMappings(items.toArray(new WSTransformerVariablesMapping[items.size()]));
        			outputViewer.refresh();
        			if(line.getPipelineVariable()!=null)availableVariables.add(line.getPipelineVariable());
        			outputVariables.setItems(availableVariables.toArray(new String[availableVariables.size()]));
        			markDirty();
	        	}
	        });
			LabelCombo outputV=new LabelCombo(toolkit,outputComposite,"Output Variables",SWT.BORDER,1);		
			outputVariables=outputV.getCombo();
			outputVariables.addKeyListener(variableListener);
			outputVariables.addMouseTrackListener(mouseListener);
			outputVariables.getTabList()[0].addMouseTrackListener(mouseListener);
			
			//create table
			java.util.List<String> columns=new ArrayList<String>();
			columns.add("Output Parameters");
			columns.add("Output Variables");
			outputViewer=createViewer(columns,outputComposite,false);
			wrap.Wrap(TransformerMainPage.this, outputViewer);
		}
		private void createPlugin() throws Exception{
	        Composite specsComposite = toolkit.createComposite(mainComposite,SWT.NONE);
	        specsComposite.setLayoutData(
	                new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
	        );
	        specsComposite.setLayout(new GridLayout(4,false));
	        

	        pluginDescription = toolkit.createText(specsComposite, "", SWT.MULTI|SWT.WRAP);
	        pluginDescription.setEditable(false);
	        pluginDescription.setLayoutData(
	                new GridData(SWT.FILL,SWT.FILL,true,false,4,2)
	        );
	        ((GridData)pluginDescription.getLayoutData()).heightHint = 35;
	            Label jndiLabel = toolkit.createLabel(specsComposite, "Plugin name", SWT.NULL);
	            jndiLabel.setLayoutData(
	                    new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
	            );
	            pluginsCombo = new CCombo(specsComposite, SWT.BORDER|SWT.READ_ONLY);
//	            pluginsCombo.addModifyListener(new ModifyListener() {
//	            	public void modifyText(ModifyEvent e) {
//	            		if (TransformerMainPage.this.refreshing) return;
//	            		String jndi = pluginsCombo.getText();
//	            		//update the description
//	            		String description = pluginDescriptions.get(jndi);
//	            		pluginDescription.setText(description == null ? "" : description);
//	            		if (stepsList.getSelectionIndex()==-1) return;
//	            		//commit as we go
//	            		//TransformerMainPage.this.comitting= true;	            		
//	            		if (! jndi.contains("/")) jndi=TRANSFORMER_PLUGIN+jndi;
//	            		//((WSTransformerV2)getXObject().getWsObject())
//	            		transformer.getProcessSteps()[stepsList.getSelectionIndex()]
//	            			                  	.setPluginJNDI(jndi);
//	            		TransformerMainPage.this.comitting= false;
//	            		markDirty();
//	            	}	            	
//	            });
	            
	            pluginsCombo.addSelectionListener(new SelectionAdapter(){
					public void widgetSelected(SelectionEvent e) {	
						if(pluginsCombo.getText().equals(currentPluginName)){
							return;
						}
						refreshCombo();	
						if(stepsList.getSelectionIndex()>=0 && stepsList.getSelectionIndex()< transformer.getProcessSteps().length)
						transformer.getProcessSteps()[stepsList.getSelectionIndex()].setPluginJNDI(TRANSFORMER_PLUGIN+pluginsCombo.getText());
						inputViewer.setInput(new ArrayList<WSTransformerVariablesMapping>());
						outputViewer.setInput(new ArrayList<WSTransformerVariablesMapping>());
						String jndi = pluginsCombo.getText();
						String document = EInputTemplate.getXtentisObjexts().get(jndi).getContent();
						parametersTextViewer.setDocument(new Document(document));
					}	            	
	            });
	            //feed the combo once
	            WSTransformerPluginV2SList list = port.getTransformerPluginV2SList(new WSGetTransformerPluginV2SList("EN"));
	            
	            WSTransformerPluginV2SListItem[] items = list.getItem();
	            
	            if (items!=null) {
	            	for (int i = 0; i < items.length; i++) {	            		
						pluginDescriptions.put(items[i].getJndiName(), items[i].getDescription());
					}
	            	//get the sorted list and feed the combo
	            	Set<String> jndis = pluginDescriptions.keySet();
	            	for (Iterator<String> iterator = jndis.iterator(); iterator.hasNext(); ) {
						String jndi = iterator.next();
						pluginsCombo.add(jndi);	
						//add input variables and output variables
			            WSTransformerPluginV2Details details=port.getTransformerPluginV2Details(new WSGetTransformerPluginV2Details(
	        							jndi.contains("/") ? jndi : TRANSFORMER_PLUGIN+jndi,
	        							"en"
	        					));
			            java.util.List<String>input=new ArrayList<String>();
			            for(WSTransformerPluginV2VariableDescriptor v:details.getInputVariableDescriptors()){
			            	input.add(v.getVariableName());
			            	
			            }		
			            inputVariablesMap.put(jndi, input);
			            
			            java.util.List<String>output=new ArrayList<String>();
			            for(WSTransformerPluginV2VariableDescriptor v:details.getOutputVariableDescriptors()){
			            	output.add(v.getVariableName());
			            }		
			            outputVariablesMap.put(jndi, output);		            
					}
	            }
	        Button detailsButton = toolkit.createButton(specsComposite,"",SWT.PUSH | SWT.CENTER);
	        detailsButton.setImage(ImageCache.getCreatedImage(EImage.HELP_CONTENTS.getPath()));
	        detailsButton.setToolTipText("Help...");
	        detailsButton.setLayoutData(
	                new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
	        );
	        detailsButton.addSelectionListener(new SelectionListener() {
	        	public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
	        	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
	        		try {
	        			String jndi;

	            			jndi = pluginsCombo.getText();
	            		if(jndi.length()==0)return;

	        			WSTransformerPluginV2Details details =port.getTransformerPluginV2Details(
	        					new WSGetTransformerPluginV2Details(
	        							jndi.contains("/") ? jndi : TRANSFORMER_PLUGIN+jndi,
	        							"en"
	        					)
	        			);
	        			final PluginDetailsDialog dialog = new PluginDetailsDialog(
	        					getSite().getShell(),
	        					details.getDescription(),
	        					details.getDocumentation(),
	        					details.getParametersSchema(),
	        					"Documentation"
	        			);
	        			dialog.addListener(new Listener() {
	        				public void handleEvent(Event event) {dialog.close();}
	        			});
	        			
	        			dialog.setBlockOnOpen(true);
	        			dialog.open();
	        			
	        		} catch (Exception ex) {
	        			String jndi;

	            		jndi = pluginsCombo.getText();

	        			MessageDialog.openError(getSite().getShell(), "Check "+jndi, "The plugin \""+jndi+"\" did NOT respond correctly");
	        			return;
	        		}      		
	        	}
	        });
	        

		}

		public void create() throws Exception{
			mainComposite= toolkit.createComposite(parent,SWT.BORDER);
			mainComposite.setLayoutData(
	                new GridData(SWT.FILL,SWT.RIGHT,true,true,4,1)
	        );		
			mainComposite.setLayout(new GridLayout(3,false));	
			
			variableListener = new KeyListener()
			{
				public void keyPressed(KeyEvent e)
				{
	        		if (e.keyCode == '/' && e.stateMask ==
	        			SWT.ALT) {
	        			
	        			VariableDefinitionDialog dlg = new VariableDefinitionDialog(
								mainComposite.getShell(),
								TransformerMainPage.this.getPartName(),
								e.widget == inputVariables ? true : false,
								pluginsCombo.getText());
						dlg.setBlockOnOpen(true);
						dlg.open();
						if (dlg.getReturnCode() == Window.OK)
						{
							Widget wdg = e.widget;
							if (wdg instanceof CCombo) {
								CCombo combo = (CCombo) wdg;
								if (!Arrays.asList(combo.getItems()).contains(
										dlg.outPutVariable())) {
									combo.add(dlg.outPutVariable());
									TransformerMainPage.this.markDirty();
								}
								
								for (int idx = 0; idx < combo.getItems().length; idx++)
								{
									if (dlg.outPutVariable().equals(combo.getItem(idx)))
									{
										combo.select(idx);
										break;
									}
								}
							}
							dlg.close();
						}
	        		}
				}
				
				public void keyReleased(KeyEvent e) {
				}
			};
			
			mouseListener = new MouseTrackAdapter()
			{
				public void mouseEnter(MouseEvent e) {
					((Text) e.getSource())
							.setToolTipText("please click 'ALT + /' to pop up Variable Dialog");
				}
			};
			
			createInput();
			createPlugin();				
			createOutput();
		}
	}

}
