package com.amalto.workbench.views;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

import com.amalto.workbench.actions.AServerViewAction;
import com.amalto.workbench.actions.BrowseRevisionAction;
import com.amalto.workbench.actions.BrowseViewAction;
import com.amalto.workbench.actions.CopyXObjectAction;
import com.amalto.workbench.actions.DataClusterExportAction;
import com.amalto.workbench.actions.DataClusterImportAction;
import com.amalto.workbench.actions.DeleteXObjectAction;
import com.amalto.workbench.actions.EditXObjectAction;
import com.amalto.workbench.actions.NewCategoryAction;
import com.amalto.workbench.actions.NewUserAction;
import com.amalto.workbench.actions.NewXObjectAction;
import com.amalto.workbench.actions.PasteXObjectAction;
import com.amalto.workbench.actions.ServerLoginAction;
import com.amalto.workbench.actions.ServerRefreshAction;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.IXObjectModelListener;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeObjectTransfer;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.providers.ServerTreeContentProvider;
import com.amalto.workbench.providers.ServerTreeLabelProvider;
import com.amalto.workbench.utils.EXtentisObjects;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.utils.LocalTreeObjectRepository;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.WorkbenchClipboard;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.WSDataCluster;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSGetDataCluster;
import com.amalto.workbench.webservices.WSLogout;
import com.amalto.workbench.webservices.WSVersioningGetInfo;
import com.amalto.workbench.webservices.WSVersioningInfo;
import com.amalto.workbench.webservices.XtentisPort;

/**
 * The view allowing administration of the "+IConstants.TALEND+" Server
 * 
 * @author Bruno Grieder
 * 
 */
public class ServerView extends ViewPart implements IXObjectModelListener {
	
	public static final String VIEW_ID="org.talend.openmdm.workbench.views.ServerView";
	
	protected TreeViewer viewer;
	protected DrillDownAdapter drillDownAdapter;
	protected Action loginAction;
	protected Action logoutAction;
	protected Action newXObjectAction;
	protected Action editXObjectAction;
	protected Action deleteXObjectAction;
	protected Action serverRefreshAction;
	//protected Action serverInitAction;
	protected Action browseViewAction;
	protected Action copyAction;
	protected Action pasteAction;
	//protected Action versionAction;
	protected Action exportAction;
	protected Action NewCategoryAction;
	
	//test for NewUserAction 
	protected NewUserAction newUserActon;
	
	
	private ServerTreeContentProvider contentProvider;

	private ArrayList<TreeObject> dndTreeObjs = new ArrayList<TreeObject>();
    private int dragType = -1;

	private DataClusterImportAction importAction;


	private BrowseRevisionAction browseRevisionAction;
	/**********************************************************************************
	 * The VIEW
	 * 
	 **********************************************************************************/

	/**
	 * The constructor.
	 */
	public ServerView() {
	}

    public static ServerView show() {
        IViewPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(VIEW_ID);
        if (part == null) {
            try {
                part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(VIEW_ID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (ServerView) part;
    }
    
    public TreeParent getRoot(){
    	return contentProvider.getInvisibleRoot();
    }
    
    
    private DragSource createTreeDragSource(){
		DragSource dragSource = new DragSource(viewer.getTree(), DND.DROP_MOVE | DND.DROP_COPY);
		dragSource.setTransfer(new Transfer[] { TreeObjectTransfer.getInstance() });
		dragSource.addDragListener(new DragSourceListener() {
			IStructuredSelection dndSelection = null;
			public void dragStart(DragSourceEvent event){
			    dragType = -1;
				dndSelection  = (IStructuredSelection)viewer.getSelection();
				event.doit = dndSelection.size() > 0;
				dndTreeObjs.clear();
				for (Iterator<TreeObject> iter = dndSelection.iterator(); iter
						.hasNext();) {
					TreeObject xobject = iter.next();
					dndTreeObjs.add(xobject);
					if ((dragType != -1 && dragType != xobject.getType())
							|| xobject.getType() == TreeObject.CATEGORY_FOLDER
							|| (LocalTreeObjectRepository.getInstance().isInSystemCatalog(xobject))
							|| (xobject.getServerRoot() == xobject.getParent())) {
						event.doit = false;
						break;
					} else {
						dragType = xobject.getType();
					}
				}
			}
			
			public void dragFinished(DragSourceEvent event){
				dndSelection = null;
			}
			
			public void dragSetData(DragSourceEvent event){
				if (dndSelection == null || dndSelection.size() == 0) return;
				if (! TreeObjectTransfer.getInstance().isSupportedType(event.dataType)) return;
				
				TreeObject[] sourceObjs  = new TreeObject[dndSelection.size()];
				int index = 0;
				for (Iterator<TreeObject> iter = dndSelection.iterator(); iter.hasNext(); ) {
					TreeObject xobject = iter.next();
					sourceObjs[index++] = xobject;
				}
				event.data = sourceObjs;
			}
		});
		
		return dragSource;
    }
    
    
	private DropTarget createTreeDropTarget() {
		DropTarget dropTarget = new DropTarget(viewer.getTree(), DND.DROP_MOVE | DND.DROP_COPY);
		dropTarget.setTransfer(new Transfer[] { TreeObjectTransfer.getInstance() });
		dropTarget.addDropListener(new DropTargetAdapter() {
			public void dragEnter(DropTargetEvent event) {

			}
			public void dragLeave(DropTargetEvent event) {

			}
			public void dragOver(DropTargetEvent event) {
				dropTargetValidate(event);
				event.feedback |= DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
			}
			
			public void drop(DropTargetEvent event) {
				
				resetTargetTreeObject(event);
				if (dropTargetValidate(event))
					dropTargetHandleDrop(event);
			}
			
			
			private void resetTargetTreeObject(DropTargetEvent event) {
				// Determine the target XObject for the drop 
				IStructuredSelection dndSelection  = (IStructuredSelection)viewer.getSelection();
				for (Iterator<TreeObject> iter = dndSelection.iterator(); iter
						.hasNext();) {
					TreeObject xobject = iter.next();
					if (!xobject.isXObject() && xobject instanceof TreeParent) {
						if (dndTreeObjs.indexOf(xobject) >= 0)
							dndTreeObjs.remove(xobject);
						TreeParent dir = (TreeParent) xobject;
						for (TreeObject treeObj : dir.getChildren()) {
							if (dndTreeObjs.indexOf(treeObj) == -1)
								dndTreeObjs.add(treeObj);
						}
					}
				}
			}
		});
		
		return dropTarget;	
	}
	
	private boolean dropTargetValidate(DropTargetEvent event) {

		if (event.item == null) return false;
		Object obj = event.item.getData();
		if (obj instanceof TreeObject)
		{
			TreeObject treeObj = (TreeObject)obj;
			if (treeObj.getParent() == null)
				System.out.println(treeObj.getDisplayName());
			int xtentisType = LocalTreeObjectRepository.getInstance().receiveUnCertainTreeObjectType(treeObj);
			if ((treeObj.getType() != dragType && treeObj.getType() != TreeObject.CATEGORY_FOLDER)
					|| dragType == TreeObject.CATEGORY_FOLDER
					|| (treeObj.getType() == TreeObject.CATEGORY_FOLDER && xtentisType != dragType)
					|| (treeObj.getType() == TreeObject.CATEGORY_FOLDER
							&& treeObj.getParent().getType() == dragType && treeObj
							.getDisplayName().equals("System"))
					|| (LocalTreeObjectRepository.getInstance().isInSystemCatalog(treeObj.getParent()))) {
				event.detail = DND.DROP_NONE;
			} else  {
				for (TreeObject tos: dndTreeObjs)
				{
					if (tos == obj) {
						event.detail = DND.DROP_LINK;
						break;
					}
					else
					{
						if (tos.getParent().getType() == TreeObject.CATEGORY_FOLDER
								&& tos.getParent().getDisplayName().equals(
										"System")) {
							event.detail = DND.DROP_NONE;
						}
						event.detail = DND.DROP_MOVE;	
					}
				}
			}
		}
	
		return event.detail != DND.DROP_NONE;
	}
	

	private void dropTargetHandleDrop(DropTargetEvent event) {
		TreeObject remoteObj = (TreeObject)event.item.getData();
		TreeParent parent =null;
		if (remoteObj instanceof TreeParent)
			parent = (TreeParent)remoteObj;
		else
			parent = remoteObj.getParent();
		
		ArrayList<TreeObject> subDdnList = new ArrayList<TreeObject>();
		
		if (parent != null) {
			java.util.List<TreeObject> list = Arrays.asList(parent
					.getChildren());
			for (TreeObject xobj : dndTreeObjs) {
				for (TreeObject another : list) {
					if (another.getDisplayName().equals(xobj.getDisplayName()) && (another.getType() == xobj.getType())) {
						subDdnList.add(xobj);
					}
				}	
			}
			dndTreeObjs.removeAll(subDdnList);
		}
		
		transformCatalog(remoteObj);
		dndTreeObjs.clear();
		dndTreeObjs.addAll(subDdnList);
		
		if (!dndTreeObjs.isEmpty()) {
			WorkbenchClipboard.getWorkbenchClipboard().get().clear();
			WorkbenchClipboard.getWorkbenchClipboard().get().addAll(dndTreeObjs);
			((PasteXObjectAction)pasteAction).setXtentisPort(remoteObj);
			pasteAction.run();
		}
	}
	
	public void forceAllSiteToRefresh()
	{
		TreeObject[] childs = getTreeContentProvider().getInvisibleRoot().getChildren();
		for (TreeObject child: childs)
		{
			(new ServerRefreshAction(this,child.getServerRoot())).run();	
		}
	}
	
	private void transformCatalog(TreeObject remoteObj)
	{
		boolean transform = false;
		TreeParent catalog = remoteObj instanceof TreeParent ? (TreeParent) remoteObj
				: remoteObj.getParent();
		for (TreeObject theObj : dndTreeObjs)
		{
			theObj.getParent().removeChild(theObj);
			catalog.addChild(theObj);
			theObj.setServerRoot(catalog.getServerRoot());
			transform = true;
		}
		
		if (transform &&  getTreeContentProvider().getInvisibleRoot().getChildren().length > 1) {
			forceAllSiteToRefresh();
		}
		getViewer().refresh(false);
	}
	
	protected class DCDragSourceListener implements DragSourceListener {
		private int selected;

		public void dragFinished(DragSourceEvent event) {
			Control control = ((DragSource)event.widget).getControl();
			if ((control instanceof List) && ((event.detail & DND.DROP_MOVE) == DND.DROP_MOVE)) {
				((List)control).remove(selected);
			}
		}

		public void dragSetData(DragSourceEvent event) {
			Control control = ((DragSource)event.widget).getControl();
			if ((control instanceof List))
				if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
					this.selected = ((List)control).getSelectionIndex();
					event.data =  ((List)control).getSelection()[0];
				}
		}

	   public  void  dragStart(DragSourceEvent event){ 
			Control control = ((DragSource)event.widget).getControl();
	        if (control instanceof Tree){ 
	        	IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
	            if (selection.size() == 0 ){            
	              event.doit = false ; 
	          } 
	      } 
	  } 

	}
	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		drillDownAdapter = new DrillDownAdapter(viewer);
		 contentProvider=new ServerTreeContentProvider(this.getSite(),
				new TreeParent("INVISIBLE ROOT", null, TreeObject._ROOT_, null,
						null));
		setTreeContentProvider(contentProvider);
		viewer.setLabelProvider(new ServerTreeLabelProvider());
		viewer.setSorter(new ViewerSorter()
		{
		    public int category(Object element) {
		    	if (element instanceof TreeParent)
		    	{
		    		TreeParent category = (TreeParent)element;
		    		if (category.getType() == TreeObject.CATEGORY_FOLDER)
		    			return -1;
		    	}
		        return 0;
		    }
		}
		);
		viewer.setInput(getViewSite());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {

				TreeObject xobject = (TreeObject) ((IStructuredSelection) viewer
						.getSelection()).getFirstElement();

				if (xobject!=null && xobject.getType() == TreeObject.DATA_CLUSTER && xobject.isXObject()) {

					try {
						XtentisPort port = Util.getPort(new URL(
								xobject.getEndpointAddress()), 
								xobject.getUniverse(),
								xobject.getUsername(), 
								xobject.getPassword());
						WSDataCluster wsObject = port
								.getDataCluster(new WSGetDataCluster(
										(WSDataClusterPK) xobject.getWsKey()));
						String tip = "";
						if (wsObject != null&& wsObject.getDescription() != null){
							tip = wsObject.getDescription();
						    viewer.getControl().setToolTipText(tip);
						}
							
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (XtentisException e) {
						e.printStackTrace();
					} catch (RemoteException e) {
						e.printStackTrace();
					}

				}

			}

		});
		createTreeDragSource();
		createTreeDropTarget();
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		hookKeyPressAction();
		contributeToActionBars();
		hookKeyboard();
	}

	/**
	 * // fliu 
	 *  add keyboard listener into tree to assist ctrl+c, ctrl+v and del
	 */
	private void hookKeyboard()
	{
		viewer.getControl().addKeyListener(new KeyListener() {
        	public void keyPressed(KeyEvent e) {}
        	public void keyReleased(KeyEvent e) {
        		if (e.keyCode == 'c' && e.stateMask ==
        			SWT.CTRL) {
        			copyAction.run();
        		}
        		else if(e.keyCode == 'v' && e.stateMask ==
        			SWT.CTRL){
        			pasteAction.run();
        		}
        		else if (e.keyCode == SWT.DEL){
        			deleteXObjectAction.run();
        		}
        		}
        	});
	}
	
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ServerView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(loginAction);
		/*
		 * manager.add(new Separator()); manager.add(logoutAction);
		 */
	}

	protected void fillContextMenu(IMenuManager manager) {
		TreeObject xobject = (TreeObject) ((IStructuredSelection) viewer
				.getSelection()).getFirstElement();

		boolean hasVersioning = false;
		try {
			XtentisPort port = Util.getPort(xobject);
			if (port == null)
				return;
			WSVersioningInfo info = port
					.versioningGetInfo(new WSVersioningGetInfo(null));
			hasVersioning = info.isEnabled();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (xobject == null) {
			manager.add(loginAction);
		} else {
			switch (xobject.getType()) {
			case TreeObject._SERVER_:
				manager.add(loginAction);
				manager.add(logoutAction);
				manager.add(serverRefreshAction);
				if (!WorkbenchClipboard.getWorkbenchClipboard().isEmpty())
					manager.add(pasteAction);
//				if ("admin".equalsIgnoreCase(xobject.getUsername()))
//					manager.add(serverInitAction);
				break;
			case TreeObject._ACTION_:
				manager.add((Action) xobject.getWsObject());
				break;
			case TreeObject.SUBSCRIPTION_ENGINE:
				manager.add(browseViewAction);
				break;
			case TreeObject.ROLE:
				if (xobject instanceof TreeParent)
				   manager.add(newUserActon);
//				if (xobject.getDisplayName()!=null&&xobject.getDisplayName().equals(ESystemDefaultObjects.DC_MDMITEMSTRASH.getName())) {
//					break;
//				}	
			case TreeObject.VIEW:
				
			case TreeObject.DATA_CLUSTER:
				if (xobject.isXObject()) {
					manager.add(exportAction);
					manager.add(importAction);
					manager.add(browseViewAction);
				}
		
			default:
				if (!xobject.isXObject() && xobject.getDisplayName().equalsIgnoreCase(EXtentisObjects.DataCluster.getDisplayName())) {
					manager.add(exportAction);
					manager.add(importAction);
				}
			
			    int type = LocalTreeObjectRepository.getInstance().receiveUnCertainTreeObjectType(xobject);
			    if (!LocalTreeObjectRepository.getInstance().isInSystemCatalog(
						xobject)
						&& type != TreeObject.ROLE)
			    {
			    	manager.add(newXObjectAction);	
			    }
			    else if (type == TreeObject.ROLE && xobject.getType() == TreeObject.CATEGORY_FOLDER)
			    {
			    	manager.add(newUserActon);
			    }
			    
				if(Util.hasUniverse(xobject))
					manager.add(browseRevisionAction);
			    
				if (xobject.isXObject()) {
					manager.add(editXObjectAction);
					manager.add(deleteXObjectAction);
					manager.add(copyAction);
				}
				else if (LocalTreeObjectRepository.getInstance().isInSystemCatalog(xobject) == false){
					manager.add(NewCategoryAction);
				}
//				if (hasVersioning)
//					manager.add(versionAction);
				if (!WorkbenchClipboard.getWorkbenchClipboard().isEmpty())
					manager.add(pasteAction);
			}
		}
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(loginAction);
		// manager.add(logoutAction);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() {
		loginAction = new ServerLoginAction(this);

		logoutAction = new Action() {
			public void run() {
				TreeParent serverRoot = (TreeParent) ((IStructuredSelection) ServerView.this.viewer
						.getSelection()).getFirstElement();

				final String universe = serverRoot.getUniverse();
				final String username = serverRoot.getUsername();
				final String password = serverRoot.getPassword();
				final String endpointAddress = serverRoot.getEndpointAddress();
               
				TreeParent root = serverRoot.getParent();

				LocalTreeObjectRepository.getInstance().switchOffListening();
				serverRoot.getParent().removeChild(serverRoot);
				ServerView.this.viewer.refresh();
				
				// attempt logout on the server side
				ServerView.this.viewer.getControl().getDisplay().syncExec(
						new Runnable() {
							public void run() {
								try {
									Util.getPort(new URL(endpointAddress),
											universe, username, password)
											.logout(new WSLogout());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
			}
		};

		logoutAction.setText("Logout");
		logoutAction.setToolTipText("Logout From the " + IConstants.TALEND
				+ " Server");
		logoutAction.setImageDescriptor(ImageCache.getImage(EImage.LOGOUT.getPath()));

		editXObjectAction = new EditXObjectAction(this);
		newXObjectAction = new NewXObjectAction(this);
		browseRevisionAction = new BrowseRevisionAction(this);
		deleteXObjectAction = new DeleteXObjectAction(this);
		serverRefreshAction = new ServerRefreshAction(this);
		//serverInitAction = new ServerInitAction(this);
		browseViewAction = new BrowseViewAction(this);
		copyAction = new CopyXObjectAction(this);
		pasteAction = new PasteXObjectAction(this);
		
		exportAction=new DataClusterExportAction(this);
		importAction=new DataClusterImportAction(this);
		NewCategoryAction = new NewCategoryAction(this);
		//versionAction = new VersioningXObjectAction(this);
		newUserActon = new NewUserAction(this);
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				ISelection selection = ServerView.this.getViewer()
						.getSelection();
				TreeObject xo = (TreeObject) ((IStructuredSelection) selection)
						.getFirstElement();
				if (xo.getType() == TreeObject._ACTION_) {
					Class<?> actionClass = (Class<?>) xo.getWsKey();
					try {
						AServerViewAction action = (AServerViewAction) actionClass
								.newInstance();
						action.setServerView(ServerView.this);
						action.run();
					} catch (Exception ex) {
						MessageDialog.openError(viewer.getControl().getShell(),
								"Error", "Unable to run action");
					}
					return;
				}// if action
				if (xo.getType() == TreeObject.SUBSCRIPTION_ENGINE||xo.getType()==TreeObject.DATA_CLUSTER)
					browseViewAction.run();
				else
					editXObjectAction.run();
			}
		});
	}

	private void hookKeyPressAction() {
		viewer.getTree().addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {

				ISelection selection = ServerView.this.getViewer()
						.getSelection();
				TreeObject xo = (TreeObject) ((IStructuredSelection) selection)
						.getFirstElement();

				// delete
				if ((e.stateMask==0) && (e.keyCode == SWT.DEL)) {

					switch (xo.getType()) {

					case TreeObject.DATA_MODEL:
						if(xo.getParent().getType()==TreeObject.DATA_MODEL){
							deleteXObjectAction.run();
						}
						
						break;
					default:
//						MessageDialog.openError(getSite().getShell(),
//								"Error", "Unknown " + IConstants.TALEND
//										+ " Object Type: " + xo.getType());
						return;
					}// switch

				}

			}

			public void keyReleased(KeyEvent e) {

			}

		});

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/**
	 * Access to the Tree Model
	 * 
	 */
	public ServerTreeContentProvider getTreeContentProvider() {
		return (ServerTreeContentProvider) viewer.getContentProvider();
	}

	public void setTreeContentProvider(
			ServerTreeContentProvider treeContentProvider) {
		ServerTreeContentProvider oldProvider = (ServerTreeContentProvider) this.viewer
				.getContentProvider();
		if (oldProvider != null)
			oldProvider.removeListener(this);

		viewer.setContentProvider(treeContentProvider);
		treeContentProvider.addListener(this);
	}

	public void handleEvent(int type, TreeObject parent, TreeObject child) {
		// System.out.println("VIEWER HANDLE EVENT "+type+" Parent:  "+(parent==
		// null
		// ?" no parent":parent.getDisplayName())+"-->"+child.getDisplayName());

		switch (type) {
		case IXObjectModelListener.NEED_REFRESH:
			new ServerRefreshAction(this, (TreeParent) child).run();
			break;
			
		}

		this.viewer.refresh(false);
	}

	public TreeViewer getViewer() {
		return viewer;
	}

	public void setViewer(TreeViewer viewer) {
		this.viewer = viewer;
	}

	public void dispose() {
		ServerTreeContentProvider oldProvider = (ServerTreeContentProvider) this.viewer
				.getContentProvider();
		if (oldProvider != null)
			oldProvider.removeListener(this);
		
		super.dispose();
	}

}