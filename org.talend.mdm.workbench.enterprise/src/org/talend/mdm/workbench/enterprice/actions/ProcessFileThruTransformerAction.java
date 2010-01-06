package org.talend.mdm.workbench.enterprice.actions;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Event;
import org.talend.mdm.workbench.enterprice.dialog.ProcessResultsDialog;

import com.amalto.workbench.editors.XObjectEditor;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.BackgroundJobStatusType;
import com.amalto.workbench.webservices.WSBackgroundJob;
import com.amalto.workbench.webservices.WSBackgroundJobPK;
import com.amalto.workbench.webservices.WSExtractedContent;
import com.amalto.workbench.webservices.WSGetBackgroundJob;
import com.amalto.workbench.webservices.WSOutputDecisionTable;
import com.amalto.workbench.webservices.WSPipeline;
import com.amalto.workbench.webservices.WSPipelineTypedContentEntry;
import com.amalto.workbench.webservices.WSProcessBytesUsingTransformerWsOutputDecisionTableDecisions;
import com.amalto.workbench.webservices.WSProcessFileUsingTransformerAsBackgroundJob;
import com.amalto.workbench.webservices.WSTransformer;
import com.amalto.workbench.webservices.WSTransformerPK;
import com.amalto.workbench.webservices.XtentisPort;

public class ProcessFileThruTransformerAction extends Action{

	protected static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	
	protected XObjectEditor editor = null;
	protected LinkedHashMap<String, String> variablesMap = new LinkedHashMap<String, String>();
	protected TreeMap<String, WSExtractedContent> pipeline = new TreeMap<String, WSExtractedContent>();
	
	String localFilename = null;
	String encoding = null;
	String mimeType = null;
	//private TreeObject initialXObject = null;
	
	
	public ProcessFileThruTransformerAction(XObjectEditor editor, LinkedHashMap<String, String> variablesMap, String localFilename, String mimeType, String encoding) {
		super();
		this.editor = editor;
		this.variablesMap = variablesMap;
		this.localFilename = localFilename;
		this.mimeType = mimeType;
		this.encoding = encoding;
		
		setImageDescriptor(ImageCache.getImage( "icons/save_edit.gif"));
	}
	
	public void run() {

		TreeObject xobject = (TreeObject)((XObjectEditorInput)editor.getEditorInput()).getModel();			
        if (!xobject.isXObject()) return;
        
		try {

			new ProgressMonitorDialog(this.editor.getSite().getWorkbenchWindow().getShell()).run(
					true,  //fork 
					true, 	//cancelable
					new ProcessFileRunnable(
							this,
							xobject.getEndpointAddress(),
							xobject.getUniverse(),
							xobject.getUsername(),
							xobject.getPassword(),
							localFilename,
							mimeType,
							encoding,
							(WSTransformer)xobject.getWsObject(),
							variablesMap
					)
			);
			
    
		} catch (Exception e) {
			MessageDialog.openError(
					this.editor.getSite().getShell(),
					"Error Occured on Saving", 
					"An error occured trying to process the file "+localFilename+" using Transformer "
					+((WSTransformer)xobject.getWsObject()).getName()+" : "
					+(e.getCause()!=null ? e.getCause().getMessage() : e.getMessage()) 
			);
		}		
	}
	
	
	public void runWithEvent(Event event) {
		super.runWithEvent(event);
	}
	

	
	
	class ProcessFileRunnable implements IRunnableWithProgress{
		
		protected ProcessFileThruTransformerAction parent;
		private String endpointaddress;
		private String universe;
		private String username;
		private String password;
		private String localfile;
		private String mime;
		private String charset;
		private WSTransformer transformer;
		private LinkedHashMap<String, String> decisions = new LinkedHashMap<String, String>();
		
		protected WSBackgroundJob job = null;
		
		public ProcessFileRunnable(
				ProcessFileThruTransformerAction parent, 
				String endpointaddress, 
				String universe,
				String username, 
				String password, 
				String localFileName,
				String mime,
				String encoding, 
				WSTransformer transformer,
				LinkedHashMap<String, String> decisions
			) {
			this.parent = parent;
			this.endpointaddress = endpointaddress;
			this.universe = universe;
			this.username = username;
			this.password = password;
			this.localfile = localFileName;
			this.mime = mime;
			this.charset = encoding;
			this.transformer = transformer;
			this.decisions = decisions;
		}
	
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			try {
				
				monitor.beginTask("Processing the file "+localfile, 4);
	
				/******************************************
				 * File Upload
				 ******************************************/
				monitor.subTask("Uploading File: "+localfile);		
				String uploadURL = new URL(endpointaddress).getProtocol()+"://"+new URL(endpointaddress).getHost()+":"+new URL(endpointaddress).getPort()+"/datamanager/uploadFile";
				String remoteFile = Util.uploadFileToAppServer(uploadURL, localfile,username,password);
				
				monitor.worked(1);
								
				if (monitor.isCanceled()) throw new InterruptedException("User Cancel");
				
				monitor.subTask("Accessing web Services on "+endpointaddress);
				XtentisPort port = Util.getPort(
						new URL(endpointaddress),
						universe,
						username,
						password
				);
				monitor.worked(1);
				if (monitor.isCanceled()) throw new InterruptedException("User Cancel");
				
				/******************************************
				 * Starts File Processing
				 ******************************************/
				monitor.subTask("Projecting document "+transformer.getName());

				ArrayList<WSProcessBytesUsingTransformerWsOutputDecisionTableDecisions> wsDecisions = new ArrayList<WSProcessBytesUsingTransformerWsOutputDecisionTableDecisions>();
				Set<String> outputVariables = decisions.keySet();
				for (Iterator<String> iter = outputVariables.iterator(); iter.hasNext(); ) {
					String output = iter.next();
					String decision = decisions.get(output);
					wsDecisions.add(new WSProcessBytesUsingTransformerWsOutputDecisionTableDecisions(output,decision));
				}
				WSOutputDecisionTable decisionTable = new WSOutputDecisionTable(
						wsDecisions.toArray(new WSProcessBytesUsingTransformerWsOutputDecisionTableDecisions[decisions.size()])
				);
				
				WSBackgroundJobPK jobPK = port.processFileUsingTransformerAsBackgroundJob(
						new WSProcessFileUsingTransformerAsBackgroundJob(
								remoteFile,
								mime+"; charset="+charset,
								new WSTransformerPK(transformer.getName()),
								decisionTable
						)
				);

				monitor.worked(1);
				if (monitor.isCanceled()) throw new InterruptedException("User Cancel");
				
				
				/******************************************
				 * Watch the Background Job
				 ******************************************/
				
				boolean firstTime = true;
				
				do {
					
					if (firstTime) {
						Thread.sleep(1500L);
						firstTime = false;
					} else {
						Thread.sleep(5000L);
					}
					
					
					if (monitor.isCanceled()) {
						/*
						port.putJob(
								new WSPutJob(
										new WSJob(
												jobpk.getPk(),
												"Loading of document "+transformer.getName(),
												JobStatusType.CANCEL_REQUESTED,
												"The job was canceled by the user",
												1,
												null
										)
								)
						);
						*/
						throw new InterruptedException("User Cancel");
					}
					
					job  =  port.getBackgroundJob(new WSGetBackgroundJob(jobPK.getPk()));	
					monitor.subTask(job.getMessage());

				} while (
							job.getStatus().equals(BackgroundJobStatusType.RUNNING) 
							|| job.getStatus().equals(BackgroundJobStatusType.SCHEDULED)
							);

				if (job.getStatus().equals(BackgroundJobStatusType.STOPPED)) 
					throw new XtentisException("The job was stopped. "+job.getMessage());
				
				monitor.worked(1);			
				monitor.done();
				
				parent.editor.getEditorSite().getShell().getDisplay().syncExec (new Runnable () {
					public void run () {
							MessageDialog.openInformation(
									parent.editor.getEditorSite().getShell(),
									"Process a File Using a Transformer", 
									"The file processed successfully.\n\n"+job.getMessage()
							);
					}
				});
				
				/******************************************
				 * Build the result console
				 ******************************************/

				//Auto sorts the entries
				pipeline = new TreeMap<String, WSExtractedContent>();
				WSPipeline wsPipeline = job.getPipeline();
				WSPipelineTypedContentEntry[] entries = wsPipeline.getTypedContentEntry();
				for (int i = 0; i < entries.length; i++) {
					pipeline.put(entries[i].getOutput(), entries[i].getWsExtractedContent());
				}

				parent.editor.getEditorSite().getShell().getDisplay().asyncExec (new Runnable () {
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
							ProcessResultsDialog dialog = new ProcessResultsDialog(parent.editor.getEditorSite().getShell() ,"Results at "+sdf.format(new Date(System.currentTimeMillis())), pipeline);
							dialog.setBlockOnOpen(false);
							dialog.open();
						} catch (Exception e) {
							e.printStackTrace();
							throw new RuntimeException(e);
						}
					}
				});
				
				/*
				Set outputs = map.keySet();
				for (Iterator iter = outputs.iterator(); iter.hasNext(); ) {
					String output = (String) iter.next();
					WSExtractedContent typedContent = map.get(output);
					String contentType =  typedContent.getContentType();
					byte[] bytes = typedContent.getWsByteArray().getBytes();
					if (contentType.startsWith("text")) {
						//extract charset
						String charset="UTF8";
						Pattern p=Pattern.compile(".*charset\\s*=(.*?)");
						Matcher m = p.matcher(contentType);
						if (m.matches()) {
							charset = m.group(1).trim();
						}
						if ("UTF-8".equals(charset.toUpperCase())) charset = "UTF8";
						System.out.println("-------------------------------------------------------------------------------------------------------------------");
						System.out.println("**    Output: "+output+ "   ---   Content Type: "+contentType);
						System.out.println("-------------------------------------------------------------------------------------------------------------------");
						if (bytes == null) {
							System.out.println("[NULL]");
						} else {
							try {
								System.out.println(new String(bytes,charset.toUpperCase()));
							} catch (UnsupportedEncodingException e) {
								System.out.println(new String(bytes,"UTF8")); 
							}
						}
					}
				}
				*/
				
			} catch (InterruptedException ie) {
				throw (ie);
			} catch (XtentisException e) {
				throw(new InvocationTargetException(new XtentisException(e.getMessage())));
			} catch (Exception e) {
				throw new InvocationTargetException(new XtentisException("Could not Process the File. "+e.getClass().getName()+": "+e.getLocalizedMessage()));
			}		
		}//run
		
	}//PutDocumentRunnable


}