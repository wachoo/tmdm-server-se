package com.amalto.core.delegator.enterprise;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.runtime.ProcessInstance;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.core.delegator.XtentisWSBeanDefaultDelegator;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJOPK;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.role.ejb.RolePOJO;
import com.amalto.core.objects.role.ejb.RolePOJOPK;
import com.amalto.core.objects.role.ejb.local.RoleCtrlLocal;
import com.amalto.core.objects.role.ejb.local.RoleCtrlUtil;
import com.amalto.core.objects.routing.v2.ejb.AbstractRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.AbstractRoutingOrderV2POJOPK;
import com.amalto.core.objects.routing.v2.ejb.ActiveRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.CompletedRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.FailedRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.RoutingEngineV2POJO;
import com.amalto.core.objects.routing.v2.ejb.RoutingRulePOJOPK;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingEngineV2CtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingOrderV2CtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingRuleCtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingRuleCtrlUtil;
import com.amalto.core.objects.synchronization.ejb.SynchronizationItemPOJO;
import com.amalto.core.objects.synchronization.ejb.SynchronizationItemPOJOPK;
import com.amalto.core.objects.synchronization.ejb.SynchronizationPlanPOJO;
import com.amalto.core.objects.synchronization.ejb.SynchronizationPlanPOJOPK;
import com.amalto.core.objects.synchronization.ejb.local.SynchronizationItemCtrlLocal;
import com.amalto.core.objects.synchronization.ejb.local.SynchronizationItemCtrlUtil;
import com.amalto.core.objects.synchronization.ejb.local.SynchronizationPlanCtrlLocal;
import com.amalto.core.objects.synchronization.ejb.local.SynchronizationPlanCtrlUtil;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJO;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJOPK;
import com.amalto.core.objects.transformers.v2.ejb.local.TransformerV2CtrlLocal;
import com.amalto.core.objects.transformers.v2.util.TransformerCallBack;
import com.amalto.core.objects.transformers.v2.util.TransformerContext;
import com.amalto.core.objects.transformers.v2.util.TransformerPluginVariableDescriptor;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.objects.universe.ejb.UniversePOJOPK;
import com.amalto.core.objects.universe.ejb.local.UniverseCtrlLocal;
import com.amalto.core.objects.universe.ejb.local.UniverseCtrlUtil;
import com.amalto.core.objects.versioning.ejb.VersioningSystemPOJO;
import com.amalto.core.objects.versioning.ejb.VersioningSystemPOJOPK;
import com.amalto.core.objects.versioning.ejb.local.VersioningSystemCtrlLocal;
import com.amalto.core.objects.versioning.util.HistoryInfos;
import com.amalto.core.objects.versioning.util.TagStructureInfo;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import com.amalto.core.webservice.*;

public class XtentisWSBeanEnterpriseDelegator extends XtentisWSBeanDefaultDelegator{
		
	/***************************************************************************
	 * RoutingRule
	 * **************************************************************************/

	public WSRoutingRulePK deleteRoutingRule(
			WSDeleteRoutingRule wsDeleteRoutingRule) throws RemoteException {
		try {
		    return new WSRoutingRulePK(
					RoutingRuleCtrlUtil.getLocalHome().create().removeRoutingRule(
							new RoutingRulePOJOPK(wsDeleteRoutingRule.getWsRoutingRulePK().getPk())
					).getUniqueId()
			);
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));		    
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSBoolean existsRoutingRule(WSExistsRoutingRule wsExistsRoutingRule)
			throws RemoteException {
		try {
		    return new WSBoolean( 
					RoutingRuleCtrlUtil.getLocalHome().create().existsRoutingRule(
							new RoutingRulePOJOPK(wsExistsRoutingRule.getWsRoutingRulePK().getPk())
					) != null
			);
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));		    
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSRoutingRule getRoutingRule(WSGetRoutingRule wsRoutingRuleGet)
			throws RemoteException {
		try {
		    return VO2WS( 
					RoutingRuleCtrlUtil.getLocalHome().create().getRoutingRule(
							new RoutingRulePOJOPK(wsRoutingRuleGet.getWsRoutingRulePK().getPk())
					)
			);
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));		    
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSRoutingRulePKArray getRoutingRulePKs(WSGetRoutingRulePKs regex)
			throws RemoteException {
		try {
			RoutingRuleCtrlLocal ctrl = Util.getRoutingRuleCtrlLocal();
			Collection c =
				ctrl.getRoutingRulePKs(
					regex.getRegex()
				);
			if (c==null) return null;
			WSRoutingRulePK[] pks = new WSRoutingRulePK[c.size()];
			int i=0;
			for (Iterator iter = c.iterator(); iter.hasNext(); ) {
				pks[i++] = new WSRoutingRulePK(
						((RoutingRulePOJOPK) iter.next()).getUniqueId()
				);
			}
			return new WSRoutingRulePKArray(pks);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSRoutingRulePK putRoutingRule(WSPutRoutingRule wsRoutingRule)
			throws RemoteException {
		try {
		    return new WSRoutingRulePK(
					RoutingRuleCtrlUtil.getLocalHome().create().putRoutingRule(
							WS2VO(wsRoutingRule.getWsRoutingRule())
					).getUniqueId()
			);
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));		    
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	/***************************************************************************
	 * TransformerV2
	 * **************************************************************************/
	public WSTransformerV2PK deleteTransformerV2(
			WSDeleteTransformerV2 wsTransformerV2Delete) throws RemoteException {
		try {
			TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
			return
				new WSTransformerV2PK(
					ctrl.removeTransformer(
						new TransformerV2POJOPK(
								wsTransformerV2Delete.getWsTransformerV2PK().getPk()
						)
					).getUniqueId()
				);
	
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSTransformerContext executeTransformerV2(
			WSExecuteTransformerV2 wsExecuteTransformerV2)
			throws RemoteException {
		try {
			final String RUNNING = "XtentisWSBean.executeTransformerV2.running";
			TransformerContext context = WS2POJO(wsExecuteTransformerV2.getWsTransformerContext());
			context.put(RUNNING, Boolean.TRUE);
			TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
			ctrl.execute(
					context, 
					WS2POJO(wsExecuteTransformerV2.getWsTypedContent()), 
					new TransformerCallBack() {
						public void contentIsReady(TransformerContext context) throws XtentisException {
							org.apache.log4j.Logger.getLogger(this.getClass()).debug("XtentisWSBean.executeTransformerV2.contentIsReady() ");
						}
						public void done(TransformerContext context) throws XtentisException {
							org.apache.log4j.Logger.getLogger(this.getClass()).debug("XtentisWSBean.executeTransformerV2.done() ");
							context.put(RUNNING, Boolean.FALSE);
						}
					}
			);
			while (((Boolean)context.get(RUNNING)).booleanValue()) {
				Thread.sleep(100);
			}
			return POJO2WS(context);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSBackgroundJobPK executeTransformerV2AsJob(
			WSExecuteTransformerV2AsJob wsExecuteTransformerV2AsJob)
			throws RemoteException {
		try {
			TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
			BackgroundJobPOJOPK bgPK = 
				ctrl.executeAsJob(					
						WS2POJO(wsExecuteTransformerV2AsJob.getWsTransformerContext()),
						new TransformerCallBack() {
							public void contentIsReady(TransformerContext context) throws XtentisException {
								org.apache.log4j.Logger.getLogger(this.getClass()).debug("XtentisWSBean.executeTransformerV2AsJob.contentIsReady() ");
							}
							public void done(TransformerContext context) throws XtentisException {
								org.apache.log4j.Logger.getLogger(this.getClass()).debug("XtentisWSBean.executeTransformerV2AsJob.done() ");
							}
						}
				);
			return new WSBackgroundJobPK(bgPK.getUniqueId());
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSBoolean existsTransformerV2(
			WSExistsTransformerV2 wsExistsTransformerV2) throws RemoteException {
		try {
			TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
			TransformerV2POJO pojo =
				ctrl.existsTransformer(
					new TransformerV2POJOPK(
							wsExistsTransformerV2.getWsTransformerV2PK().getPk()
					)
				);
			return new WSBoolean(pojo!=null);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSTransformerContext extractThroughTransformerV2(
			WSExtractThroughTransformerV2 wsExtractThroughTransformerV2)
			throws RemoteException {
		try {
			TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
			return POJO2WS(
				ctrl.extractThroughTransformer(
					new TransformerV2POJOPK(wsExtractThroughTransformerV2.getWsTransformerV2PK().getPk()),
					WS2POJO(wsExtractThroughTransformerV2.getWsItemPK())
				)
			);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSTransformerV2 getTransformerV2(
			WSGetTransformerV2 wsGetTransformerV2) throws RemoteException {
		try {
			TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
			TransformerV2POJO pojo =
				ctrl.getTransformer(
					new TransformerV2POJOPK(
							wsGetTransformerV2.getWsTransformerV2PK().getPk()
					)
				);
			return POJO2WS(pojo);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSTransformerV2PKArray getTransformerV2PKs(
			WSGetTransformerV2PKs regex) throws RemoteException {
		try {
			TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
			Collection c =
				ctrl.getTransformerPKs(
					regex.getRegex()
				);
			if (c==null) return null;
			WSTransformerV2PK[] pks = new WSTransformerV2PK[c.size()];
			int i=0;
			for (Iterator iter = c.iterator(); iter.hasNext(); ) {
				pks[i++] = new WSTransformerV2PK(
						((TransformerV2POJOPK) iter.next()).getUniqueId()
				);
			}
			return new WSTransformerV2PKArray(pks);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSTransformerV2PK putTransformerV2(WSPutTransformerV2 wsTransformerV2)
			throws RemoteException {
		try {
			TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
			TransformerV2POJOPK pk =
				ctrl.putTransformer(
					WS2POJO(wsTransformerV2.getWsTransformerV2())
				);
			return new WSTransformerV2PK(pk.getUniqueId());
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}	
	
	/***************************************************************************
	 * TRANSFORMER PLUGINS V2
	 * **************************************************************************/

	public WSBoolean existsTransformerPluginV2(
			WSExistsTransformerPluginV2 wsExistsTransformerPlugin)
			throws RemoteException {
		try {
			return new WSBoolean(
				Util.existsComponent(
					null, 
					wsExistsTransformerPlugin.getJndiName()
				)
			);
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSString getTransformerPluginV2Configuration(
			WSTransformerPluginV2GetConfiguration wsGetConfiguration)
			throws RemoteException {
		try {
			Object service= 
				Util.retrieveComponent(
					null, 
					wsGetConfiguration.getJndiName()
				);
			
			String configuration = (String)
				Util.getMethod(service, "getConfiguration").invoke(
					service,
					new Object[] {
							wsGetConfiguration.getOptionalParameter()
					}
				);
			return new WSString(configuration);
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSTransformerPluginV2Details getTransformerPluginV2Details(
			WSGetTransformerPluginV2Details wsGetTransformerPluginDetails)
			throws RemoteException {
		try {
			Object service= 
				Util.retrieveComponent(
					null, 
					wsGetTransformerPluginDetails.getJndiName()
				);
			String description = (String)Util.getMethod(service, "getDescription").invoke(
				service,
				new Object[] {
						wsGetTransformerPluginDetails.getLanguage() == null ? "" : wsGetTransformerPluginDetails.getLanguage() 
				}
			);
			String documentation = (String)Util.getMethod(service, "getDocumentation").invoke(
					service,
					new Object[] {
							wsGetTransformerPluginDetails.getLanguage() == null ? "" : wsGetTransformerPluginDetails.getLanguage() 
					}
				);
			String parametersSchema = (String)Util.getMethod(service, "getParametersSchema").invoke(
					service,
					new Object[] {}
				);
			
			ArrayList<TransformerPluginVariableDescriptor> inputVariableDescriptors = 
				(ArrayList<TransformerPluginVariableDescriptor>)Util.getMethod(service, "getInputVariableDescriptors").invoke(
						service,
						new Object[] {
								wsGetTransformerPluginDetails.getLanguage() == null ? "" : wsGetTransformerPluginDetails.getLanguage() 
						}
				);
			ArrayList<WSTransformerPluginV2VariableDescriptor> wsInputVariableDescriptors = new ArrayList<WSTransformerPluginV2VariableDescriptor>();
			if (inputVariableDescriptors != null) {
				for (Iterator iter = inputVariableDescriptors.iterator(); iter.hasNext(); ) {
					TransformerPluginVariableDescriptor descriptor = (TransformerPluginVariableDescriptor) iter.next();
					wsInputVariableDescriptors.add(POJO2WS(descriptor));
				}
			}
			
			ArrayList<TransformerPluginVariableDescriptor> outputVariableDescriptors = 
				(ArrayList<TransformerPluginVariableDescriptor>)Util.getMethod(service, "getOutputVariableDescriptors").invoke(
						service,
						new Object[] {
							wsGetTransformerPluginDetails.getLanguage() == null ? "" : wsGetTransformerPluginDetails.getLanguage() 
						}
				);
			ArrayList<WSTransformerPluginV2VariableDescriptor> wsOutputVariableDescriptors = new ArrayList<WSTransformerPluginV2VariableDescriptor>();
			if (outputVariableDescriptors != null) {
				for (Iterator iter = outputVariableDescriptors.iterator(); iter.hasNext(); ) {
					TransformerPluginVariableDescriptor descriptor = (TransformerPluginVariableDescriptor) iter.next();
					wsOutputVariableDescriptors.add(POJO2WS(descriptor));
				}
			}

			return new WSTransformerPluginV2Details(
					wsInputVariableDescriptors.toArray(new WSTransformerPluginV2VariableDescriptor[wsInputVariableDescriptors.size()]),
					wsOutputVariableDescriptors.toArray(new WSTransformerPluginV2VariableDescriptor[wsOutputVariableDescriptors.size()]),
					description,
					documentation,
					parametersSchema
			);
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSTransformerPluginV2SList getTransformerPluginV2SList(
			WSGetTransformerPluginV2SList wsGetTransformerPluginsList)
			throws RemoteException {
		try {
			ArrayList<WSTransformerPluginV2SListItem> wsList = new ArrayList<WSTransformerPluginV2SListItem>();
			InitialContext ctx = new InitialContext();
			NamingEnumeration<NameClassPair> list = ctx.list("amalto/local/transformer/plugin");
			while (list.hasMore()) {
			    NameClassPair nc = list.next();
			    WSTransformerPluginV2SListItem item =new WSTransformerPluginV2SListItem();
			    item.setJndiName(nc.getName());
				Object service= 
					Util.retrieveComponent(
						null, 
						"amalto/local/transformer/plugin/"+nc.getName()
					);
				String description = (String)Util.getMethod(service, "getDescription").invoke(
					service,
					new Object[] {
							wsGetTransformerPluginsList.getLanguage() == null ? "" : wsGetTransformerPluginsList.getLanguage() 
					}
				);
				item.setDescription(description);
			    wsList.add(item);
			}
			return new WSTransformerPluginV2SList(wsList.toArray(new WSTransformerPluginV2SListItem[wsList.size()]));
//		} catch (XtentisException e) {
//			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}

	}

	public WSString putTransformerPluginV2Configuration(
			WSTransformerPluginV2PutConfiguration wsPutConfiguration)
			throws RemoteException {
		try {
			Object service= 
				Util.retrieveComponent(
					null, 
					wsPutConfiguration.getJndiName()
				);
			
			Util.getMethod(service, "putConfiguration").invoke(
				service,
				new Object[] {
						wsPutConfiguration.getConfiguration()
				}
			);
			return new WSString(wsPutConfiguration.getConfiguration());
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	/***************************************************************************
	 * Role
	 * **************************************************************************/
	public WSRolePK deleteRole(WSDeleteRole wsRoleDelete)
			throws RemoteException {
		try {
			RoleCtrlLocal ctrl = Util.getRoleCtrlLocal();
			return
				new WSRolePK(
					ctrl.removeRole(
						new RolePOJOPK(
								wsRoleDelete.getWsRolePK().getPk()
						)
					).getUniqueId()
				);
	
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSBoolean existsRole(WSExistsRole wsExistsRole)
			throws RemoteException {
		try {
			RoleCtrlLocal ctrl = RoleCtrlUtil.getLocalHome().create();
			RolePOJO pojo =
				ctrl.existsRole(
					new RolePOJOPK(
							wsExistsRole.getWsRolePK().getPk()
					)
				);
			return new WSBoolean(pojo!=null);
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSStringArray getObjectsForRoles(WSGetObjectsForRoles wsRoleDelete)
			throws RemoteException {
		Set<String> names = ObjectPOJO.getObjectsNames2ClassesMap().keySet(); 
		return new WSStringArray(
				names.toArray(new String[names.size()])
		);
	}

	public WSRole getRole(WSGetRole wsGetRole) throws RemoteException {
		try {
			RoleCtrlLocal ctrl = RoleCtrlUtil.getLocalHome().create();
			RolePOJO pojo =
				ctrl.getRole(
					new RolePOJOPK(
							wsGetRole.getWsRolePK().getPk()
					)
				);
			return POJO2WS(pojo);
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSRolePKArray getRolePKs(WSGetRolePKs regex) throws RemoteException {
		try {
			RoleCtrlLocal ctrl = RoleCtrlUtil.getLocalHome().create();
			Collection c =
				ctrl.getRolePKs(
					regex.getRegex()
				);
			if (c==null) return null;
			WSRolePK[] pks = new WSRolePK[c.size()];
			int i=0;
			for (Iterator iter = c.iterator(); iter.hasNext(); ) {
				pks[i++] = new WSRolePK(
						((RolePOJOPK) iter.next()).getUniqueId()
				);
			}
			return new WSRolePKArray(pks);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSRolePK putRole(WSPutRole wsRole) throws RemoteException {
		try {
			RoleCtrlLocal ctrl = RoleCtrlUtil.getLocalHome().create();
			RolePOJOPK pk =
				ctrl.putRole(
					WS2POJO(wsRole.getWsRole())
				);
			LocalUser.resetLocalUsers();
			return new WSRolePK(pk.getUniqueId());
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}	
	
	/***************************************************************************
	 * Versioning
	 * **************************************************************************/
	
	private WSVersioningHistoryEntry[] convertHistoryInfosToWSVersioningHistoryEntries(
			HistoryInfos infos) {
		String [] authors = infos.getAuthors();
    	String [] comments = infos.getComments();
    	String [] dates = infos.getDates();
    	String [] revisions = infos.getRevisions();
    	String [] tags = infos.getTagNames();
    	int size = authors.length;
    	WSVersioningHistoryEntry[] entries = new WSVersioningHistoryEntry[size];
    	for (int i=0; i<size; i++) {
    		WSVersioningHistoryEntry entry = new WSVersioningHistoryEntry();

    		String author = authors[i];
    		String comment = comments[i];

    		String authorAndComment = comments[i];
    		int indexAuthorBegin = authorAndComment.indexOf("Author : ");
    		int indexAuthorEnd = authorAndComment.indexOf(" Comment : ");

    		if ((indexAuthorBegin == 0) && (indexAuthorEnd!=-1)) {
    			author = authorAndComment.substring(9, indexAuthorEnd);
    			comment = authorAndComment.substring(indexAuthorEnd+11);
    		}

    		entry.setAuthor(author);
    		entry.setComments(comment);
    		entry.setDate(dates[i]);
    		entry.setRevision(revisions[i]);
    		entry.setTag(tags[i]);
    		entries[i] = entry;
    		org.apache.log4j.Logger.getLogger(this.getClass()).debug("Entry => Author : "+author+" - Comment : "+comment+" - Date : "+dates[i]+" - Revision : "+revisions[i]+" - Tag : "+tags[i]);
    	}
		return entries;
	}
	public WSString putVersioningSystemConfiguration(
			WSPutVersioningSystemConfiguration wsPutVersioningSystemConfiguration)
			throws RemoteException {
		 try {
			  WSVersioningSystemConfiguration conf=wsPutVersioningSystemConfiguration.getVersioningSystemConfiguration();
			  VersioningSystemCtrlLocal ctrl = Util.getVersioningSystemCtrlLocal();
			  VersioningSystemPOJO pojo=new VersioningSystemPOJO(conf.getName(),conf.getJndi(),conf.getDescription(),conf.getUrl(),conf.getUsername(),conf.getPassword(),conf.getAutocommit());
			  VersioningSystemPOJOPK pk=ctrl.putVersioningSystem(pojo);
			  return new WSString(pk.getUniqueId());
		} catch (Exception e) {
			   String err = "ERROR SYSTRACE: "+e.getMessage();
			   org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			   throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSBackgroundJobPK versioningCommitItems(
			WSVersioningCommitItems wsVersioningCommitItems)
			throws RemoteException {
		try {
			VersioningSystemCtrlLocal ctrl = Util.getVersioningSystemCtrlLocal();
			
			return new WSBackgroundJobPK(
				ctrl.commitItemsAsJob(
						wsVersioningCommitItems.getVersioningSystemName() == null ? null : new VersioningSystemPOJOPK(wsVersioningCommitItems.getVersioningSystemName()),
						WS2POJO(wsVersioningCommitItems.getWsItemPKs()),
						wsVersioningCommitItems.getComment()
				).getUniqueId()
			);
			
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSVersioningInfo versioningGetInfo(
			WSVersioningGetInfo wsVersioningGetInfo) throws RemoteException {
		try {
			VersioningSystemCtrlLocal ctrl = Util.getVersioningSystemCtrlLocal();
			String res = 
				ctrl.getVersioningSystemAvailability(
						wsVersioningGetInfo.getVersioningSystemName() == null ? null : new VersioningSystemPOJOPK(wsVersioningGetInfo.getVersioningSystemName())
				);
			return new WSVersioningInfo(res.startsWith("OK"),res);
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSString versioningGetItemContent(
			WSVersioningGetItemContent wsVersioningGetItemContent)
			throws RemoteException {
	      try {
				
				VersioningSystemCtrlLocal ctrl = Util.getVersioningSystemCtrlLocal();
				
				String result = ctrl.getItemContent(
						wsVersioningGetItemContent.getVersioningSystemName() == null ? null : new VersioningSystemPOJOPK(wsVersioningGetItemContent.getVersioningSystemName()),
						WS2POJO(wsVersioningGetItemContent.getWsItemPK()),
						wsVersioningGetItemContent.getRevision()
					);
				
				return new WSString(result);
				
			} catch (Exception e) {
				String err = "ERROR SYSTRACE: "+e.getMessage();
				org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
				throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
			}
	}

	public WSVersioningItemHistory versioningGetItemHistory(
			WSVersioningGetItemHistory wsVersioningGetItemHistory)
			throws RemoteException {
		try {
			
			VersioningSystemCtrlLocal ctrl = Util.getVersioningSystemCtrlLocal();
			
			HistoryInfos historyInfos = ctrl.getItemHistory(
						wsVersioningGetItemHistory.getVersioningSystemName() == null ? null : new VersioningSystemPOJOPK(wsVersioningGetItemHistory.getVersioningSystemName()),
						WS2POJO(wsVersioningGetItemHistory.getWsItemPK())
				);
			
			WSVersioningHistoryEntry[] wsVersioningHistoryEntries= convertHistoryInfosToWSVersioningHistoryEntries(historyInfos);
			
			return new WSVersioningItemHistory(wsVersioningGetItemHistory.getWsItemPK(),wsVersioningHistoryEntries);
			
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSVersioningItemsVersions versioningGetItemsVersions(
			WSVersioningGetItemsVersions wsVersioningGetItemsVersions)
			throws RemoteException {
	     try {
				
				VersioningSystemCtrlLocal ctrl = Util.getVersioningSystemCtrlLocal();
				
				HistoryInfos historyInfos =	ctrl.getItemsVersions(
						wsVersioningGetItemsVersions.getVersioningSystemName() == null ? null : new VersioningSystemPOJOPK(wsVersioningGetItemsVersions.getVersioningSystemName()), 
						WS2POJO(wsVersioningGetItemsVersions.getWsItemPKs())
					);
				
				WSVersioningHistoryEntry[] wsVersioningHistoryEntries= convertHistoryInfosToWSVersioningHistoryEntries(historyInfos);
				
				WSItemPK[] itemPKs= wsVersioningGetItemsVersions.getWsItemPKs();
				if(itemPKs==null)new RemoteException("No item PK! ");
				
				WSVersioningItemsVersionsItems[] wsVersioningItemsVersionsItems=new WSVersioningItemsVersionsItems[itemPKs.length];
					for (int i = 0; i < itemPKs.length; i++) {
						WSItemPK itemPK=itemPKs[i];
						WSVersioningItemsVersionsItems wsVersioningItemsVersionsItem=new WSVersioningItemsVersionsItems(itemPK,wsVersioningHistoryEntries);
						wsVersioningItemsVersionsItems[i]=wsVersioningItemsVersionsItem;
					}
					
				return new WSVersioningItemsVersions(wsVersioningItemsVersionsItems);
				
			} catch (Exception e) {
				String err = "ERROR SYSTRACE: "+e.getMessage();
				org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
				throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
			}
	}

	public WSVersioningObjectsVersions versioningGetObjectsVersions(
			WSVersioningGetObjectsVersions wsVersioningGetObjectsVersions)
			throws RemoteException {
		try {
			
			VersioningSystemCtrlLocal ctrl = Util.getVersioningSystemCtrlLocal();
			
			HistoryInfos historyInfos =	ctrl.getObjectsVersions(
					wsVersioningGetObjectsVersions.getVersioningSystemName() == null ? null : new VersioningSystemPOJOPK(wsVersioningGetObjectsVersions.getVersioningSystemName()), 
					wsVersioningGetObjectsVersions.getType(), 
					wsVersioningGetObjectsVersions.getNames()
				);
			
			WSVersioningHistoryEntry[] wsVersioningHistoryEntries= convertHistoryInfosToWSVersioningHistoryEntries(historyInfos);
			
			String[] names= wsVersioningGetObjectsVersions.getNames();
			if(names==null){
				return new WSVersioningObjectsVersions(
						new WSVersioningObjectsVersionsObjects[]{
								new WSVersioningObjectsVersionsObjects(wsVersioningGetObjectsVersions.getType(),null,wsVersioningHistoryEntries)
								});
			}else{
				WSVersioningObjectsVersionsObjects[] wsVersioningObjectsVersionsObjects=new WSVersioningObjectsVersionsObjects[names.length];
				for (int i = 0; i < names.length; i++) {
					String name=names[i];
					WSVersioningObjectsVersionsObjects wsVersioningObjectsVersionsObject=new WSVersioningObjectsVersionsObjects(wsVersioningGetObjectsVersions.getType(),name,wsVersioningHistoryEntries);
					wsVersioningObjectsVersionsObjects[i]=wsVersioningObjectsVersionsObject;
				}
				
				return new WSVersioningObjectsVersions(
						wsVersioningObjectsVersionsObjects);
			}
			
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSVersioningUniverseVersions versioningGetUniverseVersions(
			WSVersioningGetUniverseVersions wsVersioningGetUniverseVersions)
			throws RemoteException {
	try {
			
			VersioningSystemCtrlLocal ctrl = Util.getVersioningSystemCtrlLocal();
			TagStructureInfo[] tagsStructureInfos=ctrl.getUniverseVersions(
					wsVersioningGetUniverseVersions.getVersioningSystemName() == null ? null : new VersioningSystemPOJOPK(wsVersioningGetUniverseVersions.getVersioningSystemName())
			);
			
			
			//TagStructureInfoArray 2 WSVersioningUniverseVersions
			WSVersioningUniverseVersionsTagStructure[] tagsStructure=new WSVersioningUniverseVersionsTagStructure[tagsStructureInfos.length];
			for (int i = 0; i < tagsStructure.length; i++) {
				String tagName=tagsStructureInfos[i].getTagName();
				String lastDate="";
				if(tagsStructureInfos[i].getLastDate()!=null){
					lastDate=TagStructureInfo.sdf.format(tagsStructureInfos[i].getLastDate());
				}
				String lastAuthor=tagsStructureInfos[i].getLastAuthor();
				String lastComment=tagsStructureInfos[i].getLastComment();
				WSStringArray clusters=new WSStringArray(tagsStructureInfos[i].getClusters().toArray(new String[tagsStructureInfos[i].getClusters().size()]));
				tagsStructure[i]=new WSVersioningUniverseVersionsTagStructure(tagName,lastDate,lastAuthor,lastComment,clusters);
			}
			
			
			return new WSVersioningUniverseVersions(tagsStructure);
			
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSBoolean versioningRestoreItemByRevision(
			WSVersioningRestoreItemByRevision wsVersioningRestoreItemByRevision)
			throws RemoteException {
try {
			
			VersioningSystemCtrlLocal ctrl = Util.getVersioningSystemCtrlLocal();
			
			
				ctrl.restoreItemByRevision(
						wsVersioningRestoreItemByRevision.getVersioningSystemName() == null ? null : new VersioningSystemPOJOPK(wsVersioningRestoreItemByRevision.getVersioningSystemName()), 
						WS2POJO(wsVersioningRestoreItemByRevision.getWsItemPK()), 
						wsVersioningRestoreItemByRevision.getRevision()
					);
				
			return new WSBoolean(true);
			
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			return new WSBoolean(false);
		}
	}

	public WSBackgroundJobPK versioningRestoreItems(
			WSVersioningRestoreItems wsVersioningRestoreItems)
			throws RemoteException {
		try {
			VersioningSystemCtrlLocal ctrl = Util.getVersioningSystemCtrlLocal();
			ArrayList<ItemPOJOPK> itemPKs = new ArrayList<ItemPOJOPK>();
			if (wsVersioningRestoreItems.getWsItemPKs()!=null) {
				for (int i = 0; i < wsVersioningRestoreItems.getWsItemPKs().length; i++) {
					itemPKs.add(WS2POJO(wsVersioningRestoreItems.getWsItemPKs()[i]));
				}
			}
			return new WSBackgroundJobPK(
				ctrl.restoreItemsAsJob(
						wsVersioningRestoreItems.getVersioningSystemName() == null ? null : new VersioningSystemPOJOPK(wsVersioningRestoreItems.getVersioningSystemName()),
						wsVersioningRestoreItems.getTag(),
						itemPKs.toArray(new ItemPOJOPK[itemPKs.size()])
				).getUniqueId()
			);
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSBackgroundJobPK versioningRestoreUniverse(
			WSVersioningRestoreUniverse wsVersioningRestoreUniverse)
			throws RemoteException {
		try {
			VersioningSystemCtrlLocal ctrl = Util.getVersioningSystemCtrlLocal();
			return new WSBackgroundJobPK(
				ctrl.restoreUniverseAsJob(
						wsVersioningRestoreUniverse.getVersioningSystemName() == null ? null : new VersioningSystemPOJOPK(wsVersioningRestoreUniverse.getVersioningSystemName()),
						wsVersioningRestoreUniverse.getTag(),
						wsVersioningRestoreUniverse.getEncodedClusterNames()
				).getUniqueId()
			);
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSBackgroundJobPK versioningTagItems(
			WSVersioningTagItems wsVersioningTagItems) throws RemoteException {
		try {
			VersioningSystemCtrlLocal ctrl = Util.getVersioningSystemCtrlLocal();
			ArrayList<ItemPOJOPK> itemPKs = new ArrayList<ItemPOJOPK>();
			if (wsVersioningTagItems.getWsItemPKs()!=null) {
				for (int i = 0; i < wsVersioningTagItems.getWsItemPKs().length; i++) {
					itemPKs.add(WS2POJO(wsVersioningTagItems.getWsItemPKs()[i]));
				}
			}
			return new WSBackgroundJobPK(
				ctrl.tagItemsAsJob(
						wsVersioningTagItems.getVersioningSystemName() == null ? null : new VersioningSystemPOJOPK(wsVersioningTagItems.getVersioningSystemName()),
						wsVersioningTagItems.getTag(),
						wsVersioningTagItems.getComment(),
						itemPKs.toArray(new ItemPOJOPK[itemPKs.size()])
				).getUniqueId()
			);	
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSBackgroundJobPK versioningTagObjects(
			WSVersioningTagObjects wsVersioningTagObjects)
			throws RemoteException {
		try {
			VersioningSystemCtrlLocal ctrl = Util.getVersioningSystemCtrlLocal();
			return new WSBackgroundJobPK(
				ctrl.tagObjectsAsJob(
						wsVersioningTagObjects.getVersioningSystemName() == null ? null : new VersioningSystemPOJOPK(wsVersioningTagObjects.getVersioningSystemName()),
						wsVersioningTagObjects.getTag(),
						wsVersioningTagObjects.getComment(),
						wsVersioningTagObjects.getType(),
						wsVersioningTagObjects.getNames()
				).getUniqueId()
			);
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSBackgroundJobPK versioningTagUniverse(
			WSVersioningTagUniverse wsVersioningTagUniverse)
			throws RemoteException {
		try {
			VersioningSystemCtrlLocal ctrl = Util.getVersioningSystemCtrlLocal();
			return new WSBackgroundJobPK(
				ctrl.tagUniverseAsJob(
						wsVersioningTagUniverse.getVersioningSystemName() == null ? null : new VersioningSystemPOJOPK(wsVersioningTagUniverse.getVersioningSystemName()),
						wsVersioningTagUniverse.getTag(),
						wsVersioningTagUniverse.getComment(),
						WS2VO(wsVersioningTagUniverse.getTypeInstances())
				).getUniqueId()
			);
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}	
	public WSBackgroundJobPK versioningRestoreObjects(
			WSVersioningRestoreObjects wsVersioningRestoreObjects)
			throws RemoteException {
		try {
			VersioningSystemCtrlLocal ctrl = Util.getVersioningSystemCtrlLocal();
			return new WSBackgroundJobPK(
				ctrl.restoreObjectsAsJob(
						wsVersioningRestoreObjects.getVersioningSystemName() == null ? null : new VersioningSystemPOJOPK(wsVersioningRestoreObjects.getVersioningSystemName()),
						wsVersioningRestoreObjects.getTag(),
						wsVersioningRestoreObjects.getType(),
						wsVersioningRestoreObjects.getNames()
				).getUniqueId()
			);
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}	
	}
	
	private Collection<AbstractRoutingOrderV2POJOPK> getRoutingOrdersByCriteria(WSRoutingOrderV2SearchCriteria criteria) throws Exception{
		try {
			RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
			Class<? extends AbstractRoutingOrderV2POJO> clazz = null;
		    if (criteria.getStatus().equals(WSRoutingOrderV2Status.ACTIVE)) {
		    	clazz = ActiveRoutingOrderV2POJO.class;
		    } else if (criteria.getStatus().equals(WSRoutingOrderV2Status.COMPLETED)) {
		    	clazz = CompletedRoutingOrderV2POJO.class;
		    } else if (criteria.getStatus().equals(WSRoutingOrderV2Status.FAILED)) {
		    	clazz = FailedRoutingOrderV2POJO.class;
		    }  
			Collection<AbstractRoutingOrderV2POJOPK> pks = ctrl.getRoutingOrderPKsByCriteria(
				clazz, 
				criteria.getAnyFieldContains(), 
				criteria.getNameContains(), 
				criteria.getTimeCreatedMin(),
				criteria.getTimeCreatedMax(), 
				criteria.getTimeScheduledMin(), 
				criteria.getTimeScheduledMax(), 
				criteria.getTimeLastRunStartedMin(), 
				criteria.getTimeLastRunStartedMax(), 
				criteria.getTimeLastRunCompletedMin(), 
				criteria.getTimeLastRunCompletedMax(),
				criteria.getItemPKConceptContains(),
				criteria.getItemPKIDFieldsContain(),
				criteria.getServiceJNDIContains(),
				criteria.getServiceParametersContain(),
				criteria.getMessageContain()
			);
			
			return pks;
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
    /***************************************************************************
	 * Routing Order V2
	 * **************************************************************************/
	public WSRoutingOrderV2PK deleteRoutingOrderV2(
			WSDeleteRoutingOrderV2 wsDeleteRoutingOrder) throws RemoteException {
		try {
			RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
			return POJO2WS(ctrl.removeRoutingOrder(WS2POJO(wsDeleteRoutingOrder.getWsRoutingOrderPK())));
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSRoutingOrderV2PK executeRoutingOrderV2Asynchronously(
			WSExecuteRoutingOrderV2Asynchronously wsExecuteRoutingOrderAsynchronously)
			throws RemoteException {
		try {
			RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
			AbstractRoutingOrderV2POJO ro = ctrl.getRoutingOrder(WS2POJO(wsExecuteRoutingOrderAsynchronously.getRoutingOrderV2PK()));
			ctrl.executeAsynchronously(ro);
			return POJO2WS(ro.getAbstractRoutingOrderPOJOPK());
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSString executeRoutingOrderV2Synchronously(
			WSExecuteRoutingOrderV2Synchronously wsExecuteRoutingOrderSynchronously)
			throws RemoteException {
		try {
			RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
			AbstractRoutingOrderV2POJO ro = ctrl.getRoutingOrder(WS2POJO(wsExecuteRoutingOrderSynchronously.getRoutingOrderV2PK()));
			return new WSString(ctrl.executeSynchronously(ro));
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSRoutingOrderV2 existsRoutingOrderV2(
			WSExistsRoutingOrderV2 wsExistsRoutingOrder) throws RemoteException {
		try {
			RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
			return POJO2WS(ctrl.existsRoutingOrder(WS2POJO(wsExistsRoutingOrder.getWsRoutingOrderPK())));
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSRoutingOrderV2 getRoutingOrderV2(
			WSGetRoutingOrderV2 wsGetRoutingOrder) throws RemoteException {
		try {
			RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
			return POJO2WS(ctrl.getRoutingOrder(WS2POJO(wsGetRoutingOrder.getWsRoutingOrderPK())));
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSRoutingOrderV2PKArray getRoutingOrderV2PKsByCriteria(
			WSGetRoutingOrderV2PKsByCriteria wsGetRoutingOrderV2PKsByCriteria)
			throws RemoteException {
		try {
			WSRoutingOrderV2PKArray wsPKArray = new WSRoutingOrderV2PKArray();
			ArrayList<WSRoutingOrderV2PK> list = new ArrayList<WSRoutingOrderV2PK>();
			//fetch results
			Collection<AbstractRoutingOrderV2POJOPK> pks = getRoutingOrdersByCriteria(wsGetRoutingOrderV2PKsByCriteria.getWsSearchCriteria());
			for (Iterator<AbstractRoutingOrderV2POJOPK> iterator = pks.iterator(); iterator.hasNext(); ) {
				AbstractRoutingOrderV2POJOPK abstractRoutingOrderV2POJOPK = iterator.next();
				list.add(POJO2WS(abstractRoutingOrderV2POJOPK));
			}
			wsPKArray.setWsRoutingOrder(list.toArray(new WSRoutingOrderV2PK[list.size()]));
			return wsPKArray;
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSRoutingOrderV2Array getRoutingOrderV2SByCriteria(
			WSGetRoutingOrderV2SByCriteria wsGetRoutingOrderV2SByCriteria)
			throws RemoteException {
		try {
			RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
			WSRoutingOrderV2Array wsPKArray = new WSRoutingOrderV2Array();
			ArrayList<WSRoutingOrderV2> list = new ArrayList<WSRoutingOrderV2>();
			//fetch results
			Collection<AbstractRoutingOrderV2POJOPK> pks = getRoutingOrdersByCriteria(wsGetRoutingOrderV2SByCriteria.getWsSearchCriteria());
			for (Iterator<AbstractRoutingOrderV2POJOPK> iterator = pks.iterator(); iterator.hasNext(); ) {
				AbstractRoutingOrderV2POJOPK abstractRoutingOrderV2POJOPK = iterator.next();
				list.add(POJO2WS(ctrl.getRoutingOrder(abstractRoutingOrderV2POJOPK)));
			}
			wsPKArray.setWsRoutingOrder(list.toArray(new WSRoutingOrderV2[list.size()]));
			return wsPKArray;
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		} 
	}
	/***************************************************************************
	 * Routing Engine V2
	 * **************************************************************************/
	
	public WSRoutingRulePKArray routeItemV2(WSRouteItemV2 wsRouteItem)
			throws RemoteException {
		try {
			RoutingEngineV2CtrlLocal ctrl = Util.getRoutingEngineV2CtrlLocal();
			RoutingRulePOJOPK[] rules = ctrl.route(WS2POJO(wsRouteItem.getWsItemPK()));
			ArrayList<WSRoutingRulePK> list = new ArrayList<WSRoutingRulePK>();
			for (int i = 0; i < rules.length; i++) {
				list.add(new WSRoutingRulePK(rules[i].getUniqueId()));
			}
			return new WSRoutingRulePKArray(list.toArray(new WSRoutingRulePK[list.size()]));
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
	public WSRoutingEngineV2Status routingEngineV2Action(
			WSRoutingEngineV2Action wsRoutingEngineAction)
			throws RemoteException {
		try {
			RoutingEngineV2CtrlLocal ctrl = Util.getRoutingEngineV2CtrlLocal();
			if (wsRoutingEngineAction.getWsAction().equals(WSRoutingEngineV2ActionCode.START)) {
				ctrl.start();
			} else if (wsRoutingEngineAction.getWsAction().equals(WSRoutingEngineV2ActionCode.STOP)) {
				ctrl.stop();
			} else if (wsRoutingEngineAction.getWsAction().equals(WSRoutingEngineV2ActionCode.SUSPEND)) {
				ctrl.suspend(true);
			} else if (wsRoutingEngineAction.getWsAction().equals(WSRoutingEngineV2ActionCode.RESUME)) {
				ctrl.suspend(false);
			} else if (wsRoutingEngineAction.getWsAction().equals(WSRoutingEngineV2ActionCode.STATUS)) {
				//done below;
			}
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
		
		//get status
		try {
			RoutingEngineV2CtrlLocal ctrl = Util.getRoutingEngineV2CtrlLocal();
			int status = ctrl.getStatus();
			switch (status) {
				case RoutingEngineV2POJO.RUNNING:
					return WSRoutingEngineV2Status.RUNNING;
				case RoutingEngineV2POJO.STOPPED:
					return WSRoutingEngineV2Status.STOPPED;
				case RoutingEngineV2POJO.SUSPENDED:
					return WSRoutingEngineV2Status.SUSPENDED;
				default:
					return WSRoutingEngineV2Status.DEAD;
			}
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
	/***************************************************************************
	 * Universe
	 * **************************************************************************/
	public WSUniversePK deleteUniverse(WSDeleteUniverse wsUniverseDelete)
			throws RemoteException {
		try {
			UniverseCtrlLocal ctrl = Util.getUniverseCtrlLocal();
			return
				new WSUniversePK(
					ctrl.removeUniverse(
						new UniversePOJOPK(
								wsUniverseDelete.getWsUniversePK().getPk()
						)
					).getUniqueId()
				);
	
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSBoolean existsUniverse(WSExistsUniverse wsExistsUniverse)
			throws RemoteException {
		try {
			UniverseCtrlLocal ctrl = UniverseCtrlUtil.getLocalHome().create();
			UniversePOJO pojo =
				ctrl.existsUniverse(
					new UniversePOJOPK(
							wsExistsUniverse.getWsUniversePK().getPk()
					)
				);
			return new WSBoolean(pojo!=null);
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSUniverse getCurrentUniverse(
			WSGetCurrentUniverse wsGetCurrentUniverse) throws RemoteException {
		try {
			//thei should force the User in the JACC context
			UniverseCtrlUtil.getLocalHome().create();
			//Fetch the user
			LocalUser user = LocalUser.getLocalUser();
			return POJO2WS(user.getUniverse());
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSStringArray getObjectsForUniverses(
			WSGetObjectsForUniverses wsUniverseDelete) throws RemoteException {
		Set<String> names = UniversePOJO.getXtentisObjectName(); 
		return new WSStringArray(
				names.toArray(new String[names.size()])
		);
	}

	public WSUniverse getUniverse(WSGetUniverse wsGetUniverse)
			throws RemoteException {
		try {
			UniverseCtrlLocal ctrl = UniverseCtrlUtil.getLocalHome().create();
			UniversePOJO pojo =
				ctrl.getUniverse(
					new UniversePOJOPK(
							wsGetUniverse.getWsUniversePK().getPk()
					)
				);
			return POJO2WS(pojo);
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSUniversePKArray getUniverseByRevision(
			WSGetUniverseByRevision revision) throws RemoteException {
		try {
			UniverseCtrlLocal ctrl = UniverseCtrlUtil.getLocalHome().create();
			return ctrl.getUniverseByRevision(revision.getNamepattern(), revision.getRevision(), revision.getType().getValue());
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSUniversePKArray getUniversePKs(WSGetUniversePKs regex)
	throws RemoteException {
		try {
			UniverseCtrlLocal ctrl = UniverseCtrlUtil.getLocalHome().create();
			Collection c =
				ctrl.getUniversePKs(
					regex.getRegex()
				);
			if (c==null) return null;
			WSUniversePK[] pks = new WSUniversePK[c.size()];
			int i=0;
			for (Iterator iter = c.iterator(); iter.hasNext(); ) {
				pks[i++] = new WSUniversePK(
						((UniversePOJOPK) iter.next()).getUniqueId()
				);
			}
			return new WSUniversePKArray(pks);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSUniversePK putUniverse(WSPutUniverse wsUniverse)
			throws RemoteException {
		try {
			UniverseCtrlLocal ctrl = UniverseCtrlUtil.getLocalHome().create();
			UniversePOJOPK pk =
				ctrl.putUniverse(
					WS2POJO(wsUniverse.getWsUniverse())
				);
			return new WSUniversePK(pk.getUniqueId());
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}	
	/***************************************************************************
	 * SynchronizationPlan
	 * **************************************************************************/
	public WSSynchronizationPlanPK deleteSynchronizationPlan(
			WSDeleteSynchronizationPlan wsSynchronizationPlanDelete)
			throws RemoteException {
		try {
			SynchronizationPlanCtrlLocal ctrl = SynchronizationPlanCtrlUtil.getLocalHome().create();
			return
				new WSSynchronizationPlanPK(
					ctrl.removeSynchronizationPlan(
						new SynchronizationPlanPOJOPK(
								wsSynchronizationPlanDelete.getWsSynchronizationPlanPK().getPk()
						)
					).getUniqueId()
				);
	
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSBoolean existsSynchronizationPlan(
			WSExistsSynchronizationPlan wsExistsSynchronizationPlan)
			throws RemoteException {
		try {
			SynchronizationPlanCtrlLocal ctrl = SynchronizationPlanCtrlUtil.getLocalHome().create();
			SynchronizationPlanPOJO pojo =
				ctrl.existsSynchronizationPlan(
					new SynchronizationPlanPOJOPK(
							wsExistsSynchronizationPlan.getWsSynchronizationPlanPK().getPk()
					)
				);
			return new WSBoolean(pojo!=null);
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSStringArray getObjectsForSynchronizationPlans(
			WSGetObjectsForSynchronizationPlans wsSynchronizationPlanDelete)
			throws RemoteException {
		Set<String> names = SynchronizationPlanPOJO.getXtentisObjectNames(); 
		return new WSStringArray(
				names.toArray(new String[names.size()])
		);
	}

	public WSSynchronizationPlan getSynchronizationPlan(
			WSGetSynchronizationPlan wsGetSynchronizationPlan)
			throws RemoteException {
		try {
			SynchronizationPlanCtrlLocal ctrl = SynchronizationPlanCtrlUtil.getLocalHome().create();
			SynchronizationPlanPOJO pojo =
				ctrl.getSynchronizationPlan(
					new SynchronizationPlanPOJOPK(
							wsGetSynchronizationPlan.getWsSynchronizationPlanPK().getPk()
					)
				);
			return POJO2WS(pojo);
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSStringArray getSynchronizationPlanItemsAlgorithms(
			WSGetSynchronizationPlanItemsAlgorithms regex)
			throws RemoteException {
		Set<String> names = SynchronizationPlanPOJO.getItemsAlgorithmNames(); 
		return new WSStringArray(
				names.toArray(new String[names.size()])
		);
	}

	public WSStringArray getSynchronizationPlanObjectsAlgorithms(
			WSGetSynchronizationPlanObjectsAlgorithms regex)
			throws RemoteException {
		Set<String> names = SynchronizationPlanPOJO.getObjectsAlgorithmNames(); 
		return new WSStringArray(
				names.toArray(new String[names.size()])
		);
	}

	public WSSynchronizationPlanPKArray getSynchronizationPlanPKs(
			WSGetSynchronizationPlanPKs regex) throws RemoteException {
		try {
			SynchronizationPlanCtrlLocal ctrl = SynchronizationPlanCtrlUtil.getLocalHome().create();
			Collection c =
				ctrl.getSynchronizationPlanPKs(
					regex.getRegex()
				);
			if (c==null) return null;
			WSSynchronizationPlanPK[] pks = new WSSynchronizationPlanPK[c.size()];
			int i=0;
			for (Iterator iter = c.iterator(); iter.hasNext(); ) {
				pks[i++] = new WSSynchronizationPlanPK(
						((SynchronizationPlanPOJOPK) iter.next()).getUniqueId()
				);
			}
			return new WSSynchronizationPlanPKArray(pks);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSSynchronizationPlanPK putSynchronizationPlan(
			WSPutSynchronizationPlan wsSynchronizationPlan)
			throws RemoteException {
		try {
			SynchronizationPlanCtrlLocal ctrl = SynchronizationPlanCtrlUtil.getLocalHome().create();
			SynchronizationPlanPOJOPK pk =
				ctrl.putSynchronizationPlan(
					WS2POJO(wsSynchronizationPlan.getWsSynchronizationPlan())
				);
			return new WSSynchronizationPlanPK(pk.getUniqueId());
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSString synchronizationGetItemXML(
			WSSynchronizationGetItemXML wsSynchronizationGetItemXML)
			throws RemoteException {
		try {
			SynchronizationPlanCtrlLocal ctrl = SynchronizationPlanCtrlUtil.getLocalHome().create();
			String xml = ctrl.synchronizationGetMarshaledItem(
				wsSynchronizationGetItemXML.getRevisionID(), 
				WS2POJO(wsSynchronizationGetItemXML.getWsItemPOJOPK())
			);
			return new WSString(xml);
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSString synchronizationGetObjectXML(
			WSSynchronizationGetObjectXML wsSynchronizationGetObjectXML)
			throws RemoteException {
try {
			
			SynchronizationPlanCtrlLocal ctrl = SynchronizationPlanCtrlUtil.getLocalHome().create();
			String xml = ctrl.synchronizationGetMarshaledObject(
				wsSynchronizationGetObjectXML.getRevisionID(), 
				wsSynchronizationGetObjectXML.getObjectName(), 
				wsSynchronizationGetObjectXML.getUniqueID()
			);
			return new WSString(xml);
			
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSItemPKArray synchronizationGetUnsynchronizedItemPKs(
			WSSynchronizationGetUnsynchronizedItemPKs wsSynchronizationGetUnsynchronizedItemPKs)
			throws RemoteException {
try {
			
			SynchronizationPlanCtrlLocal ctrl = SynchronizationPlanCtrlUtil.getLocalHome().create();
			ArrayList<ItemPOJOPK> list = ctrl.synchronizationGetAllUnsynchronizedItemPOJOPKs(
				wsSynchronizationGetUnsynchronizedItemPKs.getRevisionID(),
				new DataClusterPOJOPK(wsSynchronizationGetUnsynchronizedItemPKs.getWsDataClusterPOJOPK().getPk()),
				wsSynchronizationGetUnsynchronizedItemPKs.getConceptName(), 
				wsSynchronizationGetUnsynchronizedItemPKs.getInstancePattern(), 
				new SynchronizationPlanPOJOPK(wsSynchronizationGetUnsynchronizedItemPKs.getSynchronizationPlanName()),
				wsSynchronizationGetUnsynchronizedItemPKs.getStart(),
				wsSynchronizationGetUnsynchronizedItemPKs.getLimit()
			);
			
			ArrayList<WSItemPK> result = new ArrayList<WSItemPK>();
			for (Iterator<ItemPOJOPK> iterator = list.iterator(); iterator.hasNext(); ) {
				ItemPOJOPK itemPOJOPK = iterator.next();
				result.add(POJO2WS(itemPOJOPK));
			}
			
			return new WSItemPKArray(result.toArray(new WSItemPK[result.size()]));
			
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSStringArray synchronizationGetUnsynchronizedObjectsIDs(
			WSSynchronizationGetUnsynchronizedObjectsIDs wsSynchronizationGetUnsynchronizedObjectsIDs)
			throws RemoteException {
try {
			
			SynchronizationPlanCtrlLocal ctrl = SynchronizationPlanCtrlUtil.getLocalHome().create();
			ArrayList<String> list = ctrl.synchronizationGetAllUnsynchronizedObjectsIDs(
				wsSynchronizationGetUnsynchronizedObjectsIDs.getRevisionID(), 
				wsSynchronizationGetUnsynchronizedObjectsIDs.getObjectName(), 
				wsSynchronizationGetUnsynchronizedObjectsIDs.getInstancePattern(), 
				wsSynchronizationGetUnsynchronizedObjectsIDs.getSynchronizationPlanName()
			);
			
			return new WSStringArray(list.toArray(new String[list.size()]));
			
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSSynchronizationPlanStatus synchronizationPlanAction(
			WSSynchronizationPlanAction wsSynchronizationPlan)
			throws RemoteException {
try {
			
			int actionCode = -1;
			if (WSSynchronizationPlanActionCode.START_FULL.equals(wsSynchronizationPlan.getWsAction())) {
				actionCode = SynchronizationPlanPOJO.ACTION_START_FULL;
			} else if (WSSynchronizationPlanActionCode.START_DIFFERENTIAL.equals(wsSynchronizationPlan.getWsAction())) {
				actionCode = SynchronizationPlanPOJO.ACTION_START_DIFFERENTIAL;
			} else if (WSSynchronizationPlanActionCode.STOP.equals(wsSynchronizationPlan.getWsAction())) {
				actionCode = SynchronizationPlanPOJO.ACTION_STOP;
			} else if (WSSynchronizationPlanActionCode.STATUS.equals(wsSynchronizationPlan.getWsAction())) {
				actionCode = SynchronizationPlanPOJO.ACTION_GET_STATUS;
			} else if (WSSynchronizationPlanActionCode.RESET.equals(wsSynchronizationPlan.getWsAction())) {
				actionCode = SynchronizationPlanPOJO.ACTION_RESET;
			}
			
			SynchronizationPlanCtrlLocal ctrl = SynchronizationPlanCtrlUtil.getLocalHome().create();
			String[] res = ctrl.action(
				actionCode, 
				new SynchronizationPlanPOJOPK(wsSynchronizationPlan.getWsSynchronizationPlanPK().getPk())
			);
			String statusCode = res[0];
			String statusMsg = res[1];
			long lastRunStarted = Long.parseLong(res[2]);
			long lastRunStopped = Long.parseLong(res[3]);
				
			WSSynchronizationPlanStatusCode wsStatusCode = null;
			if (SynchronizationPlanPOJO.STATUS_DOES_NOT_EXIST.equals(statusCode)) {
				wsStatusCode = WSSynchronizationPlanStatusCode.DOES_NOT_EXIST;
			} else if (SynchronizationPlanPOJO.STATUS_COMPLETED.equals(statusCode)) {
				wsStatusCode = WSSynchronizationPlanStatusCode.COMPLETED;
			} else if (SynchronizationPlanPOJO.STATUS_FAILED.equals(statusCode)) {
				wsStatusCode = WSSynchronizationPlanStatusCode.FAILED;
			} else if (SynchronizationPlanPOJO.STATUS_RUNNING.equals(statusCode)) {
				wsStatusCode = WSSynchronizationPlanStatusCode.RUNNING;
			} else if (SynchronizationPlanPOJO.STATUS_SCHEDULED.equals(statusCode)) {
				wsStatusCode = WSSynchronizationPlanStatusCode.SCHEDULED;
			} else if (SynchronizationPlanPOJO.STATUS_STOPPING.equals(statusCode)) {
				wsStatusCode = WSSynchronizationPlanStatusCode.STOPPING;
			}
			
			Calendar lastRunStartedCalendar = Calendar.getInstance();
			lastRunStartedCalendar.setTimeInMillis(lastRunStarted);
			
			Calendar lastRunStoppedCalendar = Calendar.getInstance();
			lastRunStoppedCalendar.setTimeInMillis(lastRunStopped);
				
			return new WSSynchronizationPlanStatus(wsStatusCode, statusMsg, lastRunStartedCalendar, lastRunStoppedCalendar);
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSItemPK synchronizationPutItemXML(
			WSSynchronizationPutItemXML wsSynchronizationPutItemXML)
			throws RemoteException {
		try {
			SynchronizationPlanCtrlLocal ctrl = SynchronizationPlanCtrlUtil.getLocalHome().create();
			ItemPOJOPK pojoPK = ctrl.synchronizationPutMarshaledItem(
				wsSynchronizationPutItemXML.getRevisionID(), 
				wsSynchronizationPutItemXML.getXml()
			);
			return POJO2WS(pojoPK);
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSString synchronizationPutObjectXML(
			WSSynchronizationPutObjectXML wsSynchronizationPutObjectXML)
			throws RemoteException {
		try {
			SynchronizationPlanCtrlLocal ctrl = SynchronizationPlanCtrlUtil.getLocalHome().create();
			ctrl.synchronizationPutMarshaledObject(
				wsSynchronizationPutObjectXML.getRevisionID(), 
				wsSynchronizationPutObjectXML.getObjectName(), 
				wsSynchronizationPutObjectXML.getUniqueID(),
				wsSynchronizationPutObjectXML.getXml()
			);
			return new WSString(System.currentTimeMillis()+"");
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}	
	
	/***************************************************************************
	 * Synchronization Item
	 * **************************************************************************/
	public WSSynchronizationItemPK deleteSynchronizationItem(
			WSDeleteSynchronizationItem wsSynchronizationItemDelete)
			throws RemoteException {
		try {
			SynchronizationItemCtrlLocal ctrl = SynchronizationItemCtrlUtil.getLocalHome().create();
				
			SynchronizationItemPOJOPK pojoPK = ctrl.removeSynchronizationItem(
				new SynchronizationItemPOJOPK(wsSynchronizationItemDelete.getWsSynchronizationItemPK().getIds())
			);
			return new WSSynchronizationItemPK(pojoPK.getIds());
	
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSBoolean existsSynchronizationItem(
			WSExistsSynchronizationItem wsExistsSynchronizationItem)
			throws RemoteException {
		try {
			SynchronizationItemCtrlLocal ctrl = SynchronizationItemCtrlUtil.getLocalHome().create();
			SynchronizationItemPOJO pojo =
				ctrl.existsSynchronizationItem(
					new SynchronizationItemPOJOPK(wsExistsSynchronizationItem.getWsSynchronizationItemPK().getIds())
				);
			return new WSBoolean(pojo!=null);
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSSynchronizationItem getSynchronizationItem(
			WSGetSynchronizationItem wsGetSynchronizationItem)
			throws RemoteException {
		try {
			SynchronizationItemCtrlLocal ctrl = SynchronizationItemCtrlUtil.getLocalHome().create();
			SynchronizationItemPOJO pojo =
				ctrl.getSynchronizationItem(
					new SynchronizationItemPOJOPK(wsGetSynchronizationItem.getWsSynchronizationItemPK().getIds())
				);
			return POJO2WS(pojo);
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSSynchronizationItemPKArray getSynchronizationItemPKs(
			WSGetSynchronizationItemPKs regex) throws RemoteException {
		try {
			SynchronizationItemCtrlLocal ctrl = SynchronizationItemCtrlUtil.getLocalHome().create();
			Collection c =
				ctrl.getSynchronizationItemPKs(
					regex.getRegex()
				);
			if (c==null) return null;
			WSSynchronizationItemPK[] pks = new WSSynchronizationItemPK[c.size()];
			int i=0;
			for (Iterator iter = c.iterator(); iter.hasNext(); ) {
				pks[i++] = new WSSynchronizationItemPK(((SynchronizationItemPOJOPK) iter.next()).getIds());
			}
			return new WSSynchronizationItemPKArray(pks);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSSynchronizationItemPK putSynchronizationItem(
			WSPutSynchronizationItem wsSynchronizationItem)
			throws RemoteException {
		try {
			SynchronizationItemCtrlLocal ctrl = SynchronizationItemCtrlUtil.getLocalHome().create();
			SynchronizationItemPOJOPK pk =
				ctrl.putSynchronizationItem(
					WS2POJO(wsSynchronizationItem.getWsSynchronizationItem())
				);
			return new WSSynchronizationItemPK(pk.getIds());
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSSynchronizationItem resolveSynchronizationItem(
			WSResolveSynchronizationItem wsResolveSynchronizationItem)
			throws RemoteException {
		try {
			SynchronizationItemCtrlLocal ctrl = SynchronizationItemCtrlUtil.getLocalHome().create();
			SynchronizationItemPOJO pojo =
				ctrl.resolveSynchronization(
					new SynchronizationItemPOJOPK(wsResolveSynchronizationItem.getWsSynchronizationItemPK().getIds()),
					wsResolveSynchronizationItem.getNewProjection()
				);
			return POJO2WS(pojo);
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
	/***************************************************************************
	 * Workflow
	 * **************************************************************************/
	public WSBoolean workflowDeleteProcessInstances(
			WSWorkflowDeleteProcessInstancesRequest deleteWolkflowRequest)
			throws RemoteException {
		try {
			Util.getWorkflowService().deleteProcessInstance(new ProcessInstanceUUID(deleteWolkflowRequest.getProcessName()));
		} catch (XtentisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return new WSBoolean(true);
	}

	public WSWorkflowProcessDefinitionUUID workflowDeploy(
			WSWorkflowDeploy deploy) throws RemoteException {
		try {
			ProcessDefinitionUUID uuid=Util.getWorkflowService().deploy(deploy.getFilename());			
			return new WSWorkflowProcessDefinitionUUID(uuid.getProcessName(),uuid.getProcessVersion());
		} catch (XtentisException e) {			
			throw(new RemoteException(e.getLocalizedMessage()));
		}	
	}

	public WSWorkflowProcessDefinitionUUIDArray workflowGetProcessDefinitions(
			WSWorkflowGetProcessDefinitions wsWorkflowGetProcessDefinitions)
			throws RemoteException {
		List<WSWorkflowProcessDefinitionUUID> rtnList=new ArrayList<WSWorkflowProcessDefinitionUUID>();
        try {
			
        	Set<ProcessDefinition> processes=Util.getWorkflowService().getProcessDefinitions();
			
			if(processes!=null){
				for (Iterator iterator = processes.iterator(); iterator.hasNext();) {
					ProcessDefinition processDefinition = (ProcessDefinition) iterator.next();
					String processName=processDefinition.getUUID().getProcessName();
					String processVersion=processDefinition.getUUID().getProcessVersion();
					WSWorkflowProcessDefinitionUUID wsWorkflowProcessDefinitionUUID=new WSWorkflowProcessDefinitionUUID(processName,processVersion);
					rtnList.add(wsWorkflowProcessDefinitionUUID);
				}
			}
			
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
		WSWorkflowProcessDefinitionUUIDArray rtnArray=new WSWorkflowProcessDefinitionUUIDArray(rtnList.toArray(new WSWorkflowProcessDefinitionUUID[rtnList.size()]));
		return rtnArray;
	}

	public WSProcessInstanceArray workflowGetProcessInstances(
			WSWorkflowGetProcessInstances instance) throws RemoteException {
		try {
			Set<ProcessInstance> set=Util.getWorkflowService().getProcessInstances(new ProcessDefinitionUUID(instance.getUuid().getProcessName(),instance.getUuid().getProcessVersion()));
			List<WSProcessInstance> list=new ArrayList<WSProcessInstance>();
			for(ProcessInstance in: set){	
				WSProcessInstance pi=new WSProcessInstance();
				pi.setName(in.getProcessInstanceUUID().getValue());
				pi.setState(in.getInstanceState().toString());
				list.add(pi);
			}
			return new WSProcessInstanceArray(list.toArray(new WSProcessInstance[list.size()]));
			
		} catch (XtentisException e) {			
			throw(new RemoteException(e.getLocalizedMessage()));
		}		
	}

	public WSProcessTaskInstanceArray workflowGetTaskList(
			WSWorkflowGetTaskList tasklist) throws RemoteException {
		try {
			Collection<TaskInstance> col=Util.getWorkflowService().getTaskList(new ProcessInstanceUUID(tasklist.getProcessinstanceuuid()));
			List<WSProcessTaskInstance> list=new ArrayList<WSProcessTaskInstance>();
			
			for(TaskInstance instance: col){
				String id=instance.getUUID().getValue();
				String status=instance.getState().toString();
				Set<String> candidates=instance.getTaskCandidates();
				StringBuffer sb=new StringBuffer();
				int i=0;
				for(String str:candidates){
					if(i== candidates.size()-1){
						sb=sb.append(str).append(",");
					}else{
						sb=sb.append(str);
					}
					i++;
				}
				WSProcessTaskInstance p=new WSProcessTaskInstance(id,status, sb.toString());
				list.add(p);
			}
			return new WSProcessTaskInstanceArray(list.toArray(new WSProcessTaskInstance[list.size()]));
			
		} catch (XtentisException e) {			
			throw(new RemoteException(e.getLocalizedMessage()));
		}		
	}

	public WSBoolean workflowUnDeploy(WSWorkflowUnDeploy undeploy)
			throws RemoteException {
		try {
			Util.getWorkflowService().undeploy(new ProcessDefinitionUUID(undeploy.getUuid().getProcessName(),undeploy.getUuid().getProcessVersion()));
			return new WSBoolean(true);
		} catch (XtentisException e) {			
			throw(new RemoteException(e.getLocalizedMessage()));
		}	
	}	
	
	/***************************************************************************
	 * Job
	 * **************************************************************************/
	public WSBoolean deleteMDMJob(WSDELMDMJob job) throws RemoteException {
		Document doc = null;
    	try {
			String xmlData = Util.getXmlServerCtrlLocal().getDocumentAsString(null, "MDMTISJOB", "JOB");
			doc = Util.parse(xmlData);
			NodeList list = Util.getNodeList(doc, "/jobs/job[@name='" + job.getJobName() + "']");
			if(list.getLength() > 0)
			{
				doc.getDocumentElement().removeChild(list.item(0));
				xmlData = Util.nodeToString(doc);
				Util.getXmlServerCtrlLocal().putDocumentFromString(xmlData, "JOB", "MDMTISJOB", null);
				return new WSBoolean(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
		
		return new WSBoolean(false);
	}

	public WSMDMJobArray getMDMJob(WSMDMNULL job) throws RemoteException {
		Document doc = null;
		String xmlData;
		WSMDMJobArray jobSet = new WSMDMJobArray();

		try {
			xmlData = Util.getXmlServerCtrlLocal().getDocumentAsString(null, "MDMTISJOB", "JOB");
			if(xmlData==null) return jobSet;
			doc = Util.parse(xmlData);
			NodeList list = Util.getNodeList(doc, "/jobs/child::*");
			WSMDMJob[] jobs = new WSMDMJob[list.getLength()];
			for(int i = 0; i < list.getLength(); i++)
			{
			   Node node = list.item(i);
			   jobs[i] = new WSMDMJob(node.getAttributes().getNamedItem("name").getNodeValue(), 
					                  node.getAttributes().getNamedItem("version").getNodeValue());
			}
			jobSet.setWsMDMJob(jobs);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
		
		return jobSet;
	}

	public WSBoolean putMDMJob(WSPUTMDMJob job) throws RemoteException {
		DocumentBuilder documentBuilder;
		try 
		{
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = null;
			Element jobElem = null, newOne = null;
		    String xmlData = Util.getXmlServerCtrlLocal().getDocumentAsString(null, "MDMTISJOB", "JOB");
		    if(xmlData == null || xmlData.equals(""))
		    {
			   doc = documentBuilder.newDocument();
			   jobElem = doc.createElement("jobs");
			   doc.appendChild(jobElem);
		    }
		    else
		    {
			   doc = Util.parse(xmlData);
			   jobElem = doc.getDocumentElement();
		    }
		   
		   
		   newOne = doc.createElement("job");
		   newOne.setAttribute("name", job.getJobName());
		   newOne.setAttribute("version", job.getJobVersion());
		   jobElem.appendChild(newOne);

		   Util.getXmlServerCtrlLocal().putDocumentFromString(Util.nodeToString(doc.getDocumentElement()), "JOB", "MDMTISJOB", null);
		   return new WSBoolean(true);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
			
		}
	}	
}
