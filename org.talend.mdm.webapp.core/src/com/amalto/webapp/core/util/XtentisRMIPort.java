package com.amalto.webapp.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.Interaction;
import javax.resource.cci.MappedRecord;

import org.jboss.security.Base64Encoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sun.misc.BASE64Decoder;

import com.amalto.connector.jca.InteractionSpecImpl;
import com.amalto.connector.jca.RecordFactoryImpl;
import com.amalto.core.ejb.DroppedItemPOJO;
import com.amalto.core.ejb.DroppedItemPOJOPK;
import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.TransformerCtrlBean;
import com.amalto.core.ejb.TransformerPOJO;
import com.amalto.core.ejb.TransformerPOJOPK;
import com.amalto.core.ejb.UpdateReportItemPOJO;
import com.amalto.core.ejb.UpdateReportPOJO;
import com.amalto.core.ejb.XtentisWSBean;
import com.amalto.core.ejb.local.TransformerCtrlLocal;
import com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJOPK;
import com.amalto.core.objects.backgroundjob.ejb.local.BackgroundJobCtrlUtil;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.objects.menu.ejb.MenuPOJO;
import com.amalto.core.objects.menu.ejb.MenuPOJOPK;
import com.amalto.core.objects.menu.ejb.local.MenuCtrlLocal;
import com.amalto.core.objects.role.ejb.RolePOJO;
import com.amalto.core.objects.role.ejb.RolePOJOPK;
import com.amalto.core.objects.role.ejb.local.RoleCtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.AbstractRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.AbstractRoutingOrderV2POJOPK;
import com.amalto.core.objects.routing.v2.ejb.ActiveRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.CompletedRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.FailedRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.RoutingEngineV2POJO;
import com.amalto.core.objects.routing.v2.ejb.RoutingRulePOJOPK;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingEngineV2CtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingOrderV2CtrlLocal;
import com.amalto.core.objects.storedprocedure.ejb.StoredProcedurePOJO;
import com.amalto.core.objects.storedprocedure.ejb.StoredProcedurePOJOPK;
import com.amalto.core.objects.storedprocedure.ejb.local.StoredProcedureCtrlLocal;
import com.amalto.core.objects.synchronization.ejb.SynchronizationItemPOJO;
import com.amalto.core.objects.synchronization.ejb.SynchronizationItemPOJOPK;
import com.amalto.core.objects.synchronization.ejb.SynchronizationPlanPOJO;
import com.amalto.core.objects.synchronization.ejb.SynchronizationPlanPOJOPK;
import com.amalto.core.objects.synchronization.ejb.local.SynchronizationItemCtrlLocal;
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
import com.amalto.core.objects.versioning.ejb.VersioningSystemPOJOPK;
import com.amalto.core.objects.versioning.ejb.local.VersioningSystemCtrlLocal;
import com.amalto.core.objects.view.ejb.ViewPOJOPK;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.UpdateReportItem;
import com.amalto.core.util.Util;
import com.amalto.core.util.Version;
import com.amalto.core.util.XSDKey;
import com.amalto.core.util.XtentisException;
import com.amalto.webapp.util.webservices.*;

/**
 * The list of web services implemented as RMI local calls
 * 
 * @author Bruno Grieder
 *
 */
@SuppressWarnings({"deprecation", "unchecked"})
public class XtentisRMIPort implements XtentisPort {


	/**
	 *  
	 */
	public XtentisRMIPort() {
		super();
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("XtentisRMIPort() Using RMI");
	}



	/***************************************************************************
	 * 
	 * S E R V I C E S
	 *  
	 *	 **************************************************************************/

	/***************************************************************************
	 * Components Management
	 * **************************************************************************/
	
	public WSVersion getComponentVersion(WSGetComponentVersion wsGetComponentVersion) throws RemoteException {
		try {
			if (WSComponent.DataManager.equals(wsGetComponentVersion.getComponent())) {	
				Version version = Version.getVersion(XtentisWSBean.class);
				return new WSVersion(
						version.getMajor(),
						version.getMinor(),
						version.getRevision(),
						version.getBuild(),
						version.getDescription(),
						version.getDate()
				);
			}
			throw new RemoteException ("Version information is not available yet for "+wsGetComponentVersion.getComponent().getValue()+" components"); 
		} catch (RemoteException e) {
			throw(e);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}


	
	/***************************************************************************
	 * Initialize
	 * **************************************************************************/
	
	public WSInt initMDM(WSInitData initData) throws RemoteException {
		throw new RemoteException("initMDM not implemented as RMI call");
	}	
	
	/***************************************************************************
	 * Logout
	 * **************************************************************************/

	public WSString logout(WSLogout wsLogout) throws RemoteException {
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("logout() ");
		String msg = "OK";
		try {
		    LocalUser user = LocalUser.getLocalUser();
		    user.logout();
		} catch (Exception e) {
			String err = "Error trying to logout";
			org.apache.log4j.Logger.getLogger(this.getClass()).warn(err,e);
			msg = e.getMessage();
		}
		return new WSString(msg);
	}
	
	/***************************************************************************
	 * Data Model
	 * **************************************************************************/
    
    public WSDataModel getDataModel(WSGetDataModel wsDataModelget)
    throws RemoteException {
		try {
		    return XConverter.VO2WS( 
					com.amalto.core.util.Util.getDataModelCtrlLocal().getDataModel(
							new DataModelPOJOPK(wsDataModelget.getWsDataModelPK().getPk())
					)
			);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }
    
    
    public WSDataModelPKArray getDataModelPKs(WSRegexDataModelPKs regexp)
    throws RemoteException {
		try {
						
		    WSDataModelPKArray array = new WSDataModelPKArray();
		    Collection<DataModelPOJOPK> pks = com.amalto.core.util.Util.getDataModelCtrlLocal().getDataModelPKs(regexp.getRegex());
		    ArrayList<WSDataModelPK> list = new ArrayList<WSDataModelPK>();
		    for (Iterator iter = pks.iterator(); iter.hasNext(); ) {
				DataModelPOJOPK pk = (DataModelPOJOPK) iter.next();
				WSDataModelPK dmpk = new WSDataModelPK(pk.getUniqueId());
				list.add(dmpk);
			}
			array.setWsDataModelPKs(list.toArray(new WSDataModelPK[list.size()]));
			return array;
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }
    
    public WSDataModelPK deleteDataModel(WSDeleteDataModel wsDeleteDataModel)
    throws RemoteException {
		try {
		    return new WSDataModelPK(
		    		com.amalto.core.util.Util.getDataModelCtrlLocal().removeDataModel(
							new DataModelPOJOPK(wsDeleteDataModel.getWsDataModelPK().getPk())
					).getUniqueId()
			);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }	
    
    public WSBoolean existsDataModel(WSExistsDataModel wsExistsDataModel)
    throws RemoteException {
		try {
		    return new WSBoolean( 
					(Util.getDataModelCtrlLocal().existsDataModel(
							new DataModelPOJOPK(wsExistsDataModel.getWsDataModelPK().getPk())
					) != null)
			);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }
    
    public WSDataModelPK putDataModel(WSPutDataModel wsDataModel)
    throws RemoteException {
		try {
		    return new WSDataModelPK(
		    		com.amalto.core.util.Util.getDataModelCtrlLocal().putDataModel(
		    				XConverter.WS2VO(wsDataModel.getWsDataModel())
					).getUniqueId()
			);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }
 
    
	public WSString checkSchema(WSCheckSchema wsSchema) throws RemoteException {
		try {
		    return new WSString(
		    		com.amalto.core.util.Util.getDataModelCtrlLocal().checkSchema(wsSchema.getSchema())
			);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	
    public WSString putBusinessConcept(WSPutBusinessConcept wsPutBusinessConcept)
    throws RemoteException {
        WSBusinessConcept bc = wsPutBusinessConcept.getBusinessConcept();
		try {
		    String s = 
		    "<xsd:element name="+bc.getName()+" type="+bc.getBusinessTemplate()+">"+
		    "	<xsd:annotation>";
		    WSI18NString[] labels = bc.getWsLabel();
		    for (int i = 0; i < labels.length; i++) {
		        s+="<xsd:appinfo source=\""+labels[i].getLanguage().getValue()+"\">"+labels[i].getLabel()+"</xsd:appinfo>";
            }
		    WSI18NString[] docs = bc.getWsDescription();
		    for (int i = 0; i < docs.length; i++) {
		        s+="<xsd:documentation xml:lang=\""+docs[i].getLanguage().getValue()+"\">"+docs[i].getLabel()+"</xsd:documentation>";
            }
		    s+=
		    "	</xsd:annotation>"+
		    "	<xsd:unique name=\""+bc.getName()+"\">"+
		    "		<xsd:selector xpath=\""+bc.getWsUniqueKey().getSelectorpath()+"\"/>";
		    for (int i = 0; i < bc.getWsUniqueKey().getFieldpath().length; i++) {
			    s+="<xsd:field xpath=\""+bc.getWsUniqueKey().getFieldpath()[i]+"\"/>";	
            }
		    s+=
		    "	</xsd:unique>"+
		    "</xsd:element>";
		    return 
		    	new WSString(
					Util.getDataModelCtrlLocal().putBusinessConceptSchema(
					        new DataModelPOJOPK(wsPutBusinessConcept.getWsDataModelPK().getPk()),
					        s
					 )
				);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }
    
    public WSString putBusinessConceptSchema(WSPutBusinessConceptSchema wsPutBusinessConceptSchema)
    throws RemoteException {
		try {
		    return new WSString(
					Util.getDataModelCtrlLocal().putBusinessConceptSchema(
					        new DataModelPOJOPK(wsPutBusinessConceptSchema.getWsDataModelPK().getPk()),
					        wsPutBusinessConceptSchema.getBusinessConceptSchema()
					 )
				);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
    
    public WSString deleteBusinessConcept(
            WSDeleteBusinessConcept wsDeleteBusinessConcept)
            throws RemoteException {
		try {
		    return new WSString(
		    		com.amalto.core.util.Util.getDataModelCtrlLocal().deleteBusinessConcept(
							new DataModelPOJOPK(wsDeleteBusinessConcept.getWsDataModelPK().getPk()),
							wsDeleteBusinessConcept.getBusinessConceptName()
					 )
				);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }
	
    public WSStringArray getBusinessConcepts(
            WSGetBusinessConcepts wsGetBusinessConcepts) throws RemoteException {
		try {
		    return new WSStringArray(
		    		com.amalto.core.util.Util.getDataModelCtrlLocal().getAllBusinessConceptsNames(
							new DataModelPOJOPK(wsGetBusinessConcepts.getWsDataModelPK().getPk())
					 )
				);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }
    
    
	public WSConceptKey getBusinessConceptKey(WSGetBusinessConceptKey wsGetBusinessConceptKey) throws RemoteException {
		try {
			String schema = 
				Util.getDataModelCtrlLocal().getDataModel(
						new DataModelPOJOPK(wsGetBusinessConceptKey.getWsDataModelPK().getPk())
				).getSchema();

			XSDKey xsdKey = Util.getBusinessConceptKey(
					Util.parse(schema),
					wsGetBusinessConceptKey.getConcept()
			);
			return
				new WSConceptKey(
						xsdKey.getSelector(),
						xsdKey.getFields()
				);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	

	
    
	
	/***************************************************************************
	 * DataCluster
	 * **************************************************************************/
    	
	   public WSDataCluster getDataCluster(WSGetDataCluster wsDataClusterGet)
	    throws RemoteException {
			try {
			    return XConverter.VO2WS( 
			    		com.amalto.core.util.Util.getDataClusterCtrlLocal().getDataCluster(
								new DataClusterPOJOPK(wsDataClusterGet.getWsDataClusterPK().getPk())
						)
				);
			} catch (Exception e) {
				throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
			}
	    }
	    
	    
	   public WSBoolean existsDataCluster(WSExistsDataCluster wsExistsDataCluster)
	    throws RemoteException {
			try {
			    return new WSBoolean( 
						com.amalto.core.util.Util.getDataClusterCtrlLocal().existsDataCluster(
								new DataClusterPOJOPK(wsExistsDataCluster.getWsDataClusterPK().getPk())
						) != null
				);
			} catch (Exception e) {
				throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
			}
	    }
	   
	    public WSDataClusterPKArray getDataClusterPKs(WSRegexDataClusterPKs regexp)
	    throws RemoteException {
			try {
			    WSDataClusterPKArray array = new WSDataClusterPKArray();
			    Collection<DataClusterPOJOPK> pks = com.amalto.core.util.Util.getDataClusterCtrlLocal().getDataClusterPKs(regexp.getRegex());
			    ArrayList<WSDataClusterPK> list = new ArrayList<WSDataClusterPK>();
			    for (Iterator iter = pks.iterator(); iter.hasNext(); ) {
					DataClusterPOJOPK pk = (DataClusterPOJOPK) iter.next();
					list.add(new WSDataClusterPK(pk.getUniqueId()));
				}
				array.setWsDataClusterPKs(list.toArray(new WSDataClusterPK[list.size()]));
				return array;
			} catch (Exception e) {
				e.printStackTrace();
				throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
			}
	    }

	
	    public WSDataClusterPK deleteDataCluster(WSDeleteDataCluster wsDeleteDataCluster)
	    throws RemoteException {
			try {
			    return new WSDataClusterPK(
			    		com.amalto.core.util.Util.getDataClusterCtrlLocal().removeDataCluster(
								new DataClusterPOJOPK(wsDeleteDataCluster.getWsDataClusterPK().getPk())
						).getUniqueId()
				);
			} catch (Exception e) {
				throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
			}
	    }	
	    
	   
	public WSDataClusterPK putDataCluster(WSPutDataCluster wsDataCluster)
	throws RemoteException {
		try {
			return new WSDataClusterPK(
					com.amalto.core.util.Util.getDataClusterCtrlLocal().putDataCluster(
							XConverter.WS2VO(wsDataCluster.getWsDataCluster())
					).getUniqueId()
			);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	public WSStringArray getConceptsInDataCluster(WSGetConceptsInDataCluster wsGetConceptsInDataCluster) throws RemoteException {
		try {
			Collection<String> results = 
				com.amalto.core.util.Util.getItemCtrl2Local().getConceptsInDataCluster(
					new DataClusterPOJOPK(wsGetConceptsInDataCluster.getWsDataClusterPK().getPk())
				).keySet();
							
	 		return new WSStringArray(results.toArray(new String[results.size()]));

		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));			
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	    
		
	

	/***************************************************************************
	 * View
	 * **************************************************************************/
		
   public WSView getView(WSGetView wsViewGet)
    throws RemoteException {
		try {
    		return XConverter.VO2WS(com.amalto.core.util.Util.getViewCtrlLocalHome().create().getView(new ViewPOJOPK(wsViewGet.getWsViewPK().getPk())));
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));	
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }
	    
   public WSBoolean existsView(WSExistsView wsExistsView)
   throws RemoteException {
		try {
		    return new WSBoolean( 
		    		com.amalto.core.util.Util.getViewCtrlLocalHome().create().existsView(
							new ViewPOJOPK(wsExistsView.getWsViewPK().getPk())
					) != null
			);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
   }
	   

    public WSViewPKArray getViewPKs(WSGetViewPKs regexp)   throws RemoteException {
		try {
	        ArrayList l;
	        String regex = regexp.getRegex() != null && !"".equals(regexp.getRegex()) && !"*".equals(regexp.getRegex()) ? regexp.getRegex() : ".*";
	        Collection list = com.amalto.core.util.Util.getViewCtrlLocalHome().create().getViewPKs(regex);
	        l = new ArrayList();
	        ViewPOJOPK pk;
	        for(Iterator iter = list.iterator(); iter.hasNext(); l.add(new WSViewPK(pk.getIds()[0])))
	            pk = (ViewPOJOPK)iter.next();
	        return new WSViewPKArray((WSViewPK[])l.toArray(new WSViewPK[l.size()]));

		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));	
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }
    
	
    public WSViewPK deleteView(WSDeleteView wsDeleteView)
    throws RemoteException {
		try {
			return new WSViewPK(com.amalto.core.util.Util.getViewCtrlLocalHome().create().removeView(new ViewPOJOPK(wsDeleteView.getWsViewPK().getPk())).getIds()[0]);
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));	
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }	
    
	   
    public WSViewPK putView(WSPutView wsView)
    throws RemoteException {
		try {
			return new WSViewPK(com.amalto.core.util.Util.getViewCtrlLocalHome().create().putView(XConverter.WS2VO(wsView.getWsView())).getIds()[0]);
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));	
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }


	

	/***************************************************************************
	 * Search
	 * **************************************************************************/
	
	public WSStringArray viewSearch(WSViewSearch wsViewSearch) throws RemoteException {
		try {
			
			Collection res = Util.getItemCtrl2Local().viewSearch(
				new DataClusterPOJOPK(wsViewSearch.getWsDataClusterPK().getPk()),
				new ViewPOJOPK(wsViewSearch.getWsViewPK().getPk()),
				XConverter.WS2VO(wsViewSearch.getWhereItem()),
				wsViewSearch.getSpellTreshold(),
				wsViewSearch.getOrderBy(),
				wsViewSearch.getDirection(),
				wsViewSearch.getSkip(),
				wsViewSearch.getMaxItems()
    		);
    		return new WSStringArray((String[])res.toArray(new String[res.size()]));
			
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (RemoteException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
		
	public WSStringArray xPathsSearch(WSXPathsSearch wsXPathsSearch) throws RemoteException {
		try {
			Collection res = com.amalto.core.util.Util.getItemCtrl2Local().xPathsSearch(
				new DataClusterPOJOPK(wsXPathsSearch.getWsDataClusterPK().getPk()),
				wsXPathsSearch.getPivotPath(),
				new ArrayList<String>(Arrays.asList(wsXPathsSearch.getViewablePaths().getStrings())),
				XConverter.WS2VO(wsXPathsSearch.getWhereItem()),
				wsXPathsSearch.getSpellTreshold(),
				wsXPathsSearch.getOrderBy(),
				wsXPathsSearch.getDirection(),
				wsXPathsSearch.getSkip(),
				wsXPathsSearch.getMaxItems()
			);
			return new WSStringArray((String[])res.toArray(new String[res.size()]));
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));	
//		} catch (com.amalto.webapp.util.XtentisException e) {
//			throw(new RemoteException(e.getLocalizedMessage()));			
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	public WSStringArray getItemsPivotIndex(WSGetItemsPivotIndex wsGetItemsPivotIndex) throws RemoteException {
		try {
			Collection res = Util.getItemCtrl2Local().getItemsPivotIndex(
					wsGetItemsPivotIndex.getClusterName(), 
					wsGetItemsPivotIndex.getMainPivotName(),
					XConverter.WS2VO(wsGetItemsPivotIndex.getPivotWithKeys()), 
					wsGetItemsPivotIndex.getIndexPaths().getStrings(),
					XConverter.WS2VO(wsGetItemsPivotIndex.getWhereItem()), 
					wsGetItemsPivotIndex.getPivotDirections()==null?null:wsGetItemsPivotIndex.getPivotDirections().getStrings(),
					wsGetItemsPivotIndex.getIndexDirections()==null?null:wsGetItemsPivotIndex.getIndexDirections().getStrings(), 
					wsGetItemsPivotIndex.getStart(), 
					wsGetItemsPivotIndex.getLimit()
			);
			return new WSStringArray((String[])res.toArray(new String[res.size()]));
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));			
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSStringArray getItems(WSGetItems wsGetItems) throws RemoteException {
		try {
			Collection res = com.amalto.core.util.Util.getItemCtrl2Local().getItems(
					new DataClusterPOJOPK(wsGetItems.getWsDataClusterPK().getPk()), 
					wsGetItems.getConceptName(), 
					XConverter.WS2VO(wsGetItems.getWhereItem()), 
					wsGetItems.getSpellTreshold(), 
					wsGetItems.getSkip(), 
					wsGetItems.getMaxItems()
			);
			return new WSStringArray((String[])res.toArray(new String[res.size()]));
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));	
//		} catch (com.amalto.webapp.util.XtentisException e) {
//			throw(new RemoteException(e.getLocalizedMessage()));			
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	
	public WSItemPKsByCriteriaResponse getItemPKsByCriteria(WSGetItemPKsByCriteria wsGetItemPKsByCriteria) throws RemoteException {
		try {
			
			String dataClusterName = wsGetItemPKsByCriteria.getWsDataClusterPK().getPk();
			
			//Check if user is allowed to read the cluster
			LocalUser user = LocalUser.getLocalUser();			
			boolean authorized = false;
	    	if ("admin".equals(user.getUsername()) || LocalUser.UNAUTHENTICATED_USER.equals(user.getUsername())) { 
	    		authorized = true;
	    	} else if (user.userCanRead(DataClusterPOJO.class, dataClusterName)) {
	    		authorized = true;
	    	}
	    	if (! authorized) {
	    		throw new RemoteException("Unauthorized read access on data cluster '"+dataClusterName+"' by user '"+user.getUsername()+"'");
	    	}
			
			//If not all concepts are store in the same revision, 
			//force the concept to be specified by the user. 
			//It would be too demanding to get all the concepts in all revisions (?)
			//The meat of this method should be ported to ItemCtrlBean
			String revisionID = null;
			if (wsGetItemPKsByCriteria.getConceptName() == null) {
    			if (user.getUniverse().getItemsRevisionIDs().size()>0) {
    				throw new RemoteException(
    					"User "+user.getUsername()+" is using items coming from multiple revisions." +
    					" In that particular case, the concept must be specified"
    				);
    			} else {
    				revisionID = user.getUniverse().getDefaultItemRevisionID();
    			}
			} else {
				revisionID = user.getUniverse().getConceptRevisionID(wsGetItemPKsByCriteria.getConceptName());
			}
			
			//FIXME: xQuery only
	 		String query = 
					"for $ii in /ii"+
					(wsGetItemPKsByCriteria.getContentKeywords() == null ? "": "[./p &= '"+wsGetItemPKsByCriteria.getContentKeywords()+"']")+
					(wsGetItemPKsByCriteria.getFromDate().longValue()<=0 ? "" : "[./t >= "+wsGetItemPKsByCriteria.getFromDate().longValue()+"]")+
					(wsGetItemPKsByCriteria.getToDate().longValue()<=0 ? "" : "[./t <= "+wsGetItemPKsByCriteria.getToDate().longValue()+"]")+
					(wsGetItemPKsByCriteria.getKeysKeywords()==null ? "" : "[./i &= '"+wsGetItemPKsByCriteria.getKeysKeywords()+"']")+
					(wsGetItemPKsByCriteria.getConceptName()==null ? "" : "[./n &= '"+wsGetItemPKsByCriteria.getConceptName()+"']")+
					" return <r>{$ii/t}{$ii/n}<ids>{$ii/i}</ids></r>";
			
			DataClusterPOJOPK dcpk =	new DataClusterPOJOPK(wsGetItemPKsByCriteria.getWsDataClusterPK().getPk());
			Collection<String> results = 
				com.amalto.core.util.Util.getItemCtrl2Local().runQuery(
					revisionID,
					dcpk,
					query,
					null
				);
			
	 		WSItemPKsByCriteriaResponseResults[] res = new WSItemPKsByCriteriaResponseResults[results.size()];
	 		int i=0;
	 		for (Iterator iter = results.iterator(); iter.hasNext(); ) {
				String result = (String) iter.next();
	 			result = result.replaceAll("\\s*__h", "").replaceAll("h__\\s*", "");
	 			Element r = Util.parse(result).getDocumentElement();
	 			long t = new Long(Util.getFirstTextNode(r, "t")).longValue();
	 			String conceptName = Util.getFirstTextNode(r, "n");
	 			String[] ids = Util.getTextNodes(r, "ids/i");
	 			res[i++] = new WSItemPKsByCriteriaResponseResults(
	 					t,
	 					new WSItemPK(
	 							wsGetItemPKsByCriteria.getWsDataClusterPK(),
	 							conceptName,
	 							ids
	 					)
	 			);
			}
 			return new WSItemPKsByCriteriaResponse(res);

		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));			
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	public WSItem getItem(WSGetItem wsGetItem) throws RemoteException {
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("getItem() "+wsGetItem.getWsItemPK().getConceptName()+"    "+Util.joinStrings(wsGetItem.getWsItemPK().getIds(), "."));
		try {
			ItemPOJO vo = 
				com.amalto.core.util.Util.getItemCtrl2Local().getItem(
						new ItemPOJOPK(
								new DataClusterPOJOPK(wsGetItem.getWsItemPK().getWsDataClusterPK().getPk()),
								wsGetItem.getWsItemPK().getConceptName(),
								wsGetItem.getWsItemPK().getIds()
						)
				);
			return new WSItem(
					wsGetItem.getWsItemPK().getWsDataClusterPK(),
					vo.getDataModelName(),
					vo.getDataModelRevision(),
					wsGetItem.getWsItemPK().getConceptName(),
					wsGetItem.getWsItemPK().getIds(),
					vo.getInsertionTime(),
					vo.getProjectionAsString()
			);
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));	
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}	
	
	public WSBoolean existsItem(WSExistsItem wsExistsItem) throws RemoteException {
		try {
			return new WSBoolean(
					(com.amalto.core.util.Util.getItemCtrl2Local().existsItem(
						new ItemPOJOPK(
								new DataClusterPOJOPK(wsExistsItem.getWsItemPK().getWsDataClusterPK().getPk()),
								wsExistsItem.getWsItemPK().getConceptName(),
								wsExistsItem.getWsItemPK().getIds()
						)
				) != null)
			);
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}	
	
	public WSStringArray quickSearch(WSQuickSearch wsQuickSearch) throws RemoteException {
		try {
			Collection c = com.amalto.core.util.Util.getItemCtrl2Local().quickSearch(
				new DataClusterPOJOPK(wsQuickSearch.getWsDataClusterPK().getPk()),
				new ViewPOJOPK(wsQuickSearch.getWsViewPK().getPk()),
				wsQuickSearch.getSearchedValue(),
				wsQuickSearch.isMatchAllWords(),
				wsQuickSearch.getSpellTreshold(),
				wsQuickSearch.getOrderBy(),
				wsQuickSearch.getDirection(),
				wsQuickSearch.getMaxItems(),
				wsQuickSearch.getSkip()
			);
			if (c==null) return null;
			return new WSStringArray((String[])c.toArray(new String[c.size()]));
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));	
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
		
	public WSString getBusinessConceptValue(
			WSGetBusinessConceptValue wsGetBusinessConceptValue)
			throws RemoteException {
		try {
			ItemPOJO iv = com.amalto.core.util.Util.getItemCtrl2Local().getItem(
					new ItemPOJOPK(
							new DataClusterPOJOPK(wsGetBusinessConceptValue.getWsDataClusterPK().getPk()),
							wsGetBusinessConceptValue.getWsBusinessConceptPK().getConceptName(),
							wsGetBusinessConceptValue.getWsBusinessConceptPK().getIds()
							)
			);
			return new WSString(itemAsString(iv));
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));	
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	
	public WSStringArray getFullPathValues(WSGetFullPathValues wsGetFullPathValues) throws RemoteException {
		try {
			
			Collection res = Util.getItemCtrl2Local().getFullPathValues(
				new DataClusterPOJOPK(wsGetFullPathValues.getWsDataClusterPK().getPk()),
				wsGetFullPathValues.getFullPath(),
				XConverter.WS2VO(wsGetFullPathValues.getWhereItem()),
				wsGetFullPathValues.getSpellThreshold(),
				wsGetFullPathValues.getOrderBy(),
				wsGetFullPathValues.getDirection()
			);

        	if (res==null) return null;
        	
            return new WSStringArray((String[]) res.toArray(new String[res.size()]));
			
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (RemoteException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
    
    private String itemAsString(ItemPOJO iv) throws Exception{
    	
    	
        String item =
            "<businessconcept>" +
            "	<cluster>"+iv.getDataClusterPOJOPK().getUniqueId()+"</cluster>";
        	String[] ids = iv.getItemIds();
        	for (int i = 0; i < ids.length; i++) {
        		item+="	<id>"+ids[i]+"</id>";
			}
            item+=
            "	<lastmodifiedtime>"+iv.getInsertionTime()+"</lastmodifiedtime>";
            item+=        
            "	<projection>"+iv.getProjection()+"</projection>" ;
            item+=
            "</businessconcept>";
            
        return item;
    }

    
    
 
    
    /***************************************************************************
	 *Put Item
	 * **************************************************************************/

	public WSItemPK putItem(WSPutItem wsPutItem) throws RemoteException {
		try {
			String projection = wsPutItem.getXmlString();
			Element root = Util.parse(projection).getDocumentElement();
		
			String concept = root.getLocalName();

			DataModelPOJO dataModel = com.amalto.core.util.Util.getDataModelCtrlLocal().getDataModel(
					new DataModelPOJOPK(wsPutItem.getWsDataModelPK().getPk())
			);
			Document schema=Util.parse(dataModel.getSchema());
            XSDKey conceptKey = com.amalto.core.util.Util.getBusinessConceptKey(
            		schema,
					concept					
			);           
			//get key values            
			String[] itemKeyValues = com.amalto.core.util.Util.getKeyValuesFromItem(
       			root,
   				conceptKey
			);										
			DataClusterPOJOPK dcpk = new DataClusterPOJOPK(wsPutItem.getWsDataClusterPK().getPk());
			
			ItemPOJOPK itemPOJOPK =  
				com.amalto.core.util.Util.getItemCtrl2Local().putItem(
						new ItemPOJO(
								dcpk,
								concept,
								itemKeyValues,
								System.currentTimeMillis(),
								projection
						),
						dataModel
				);
			if (itemPOJOPK==null) return null;
			
			//update vocabulary
			com.amalto.core.util.Util.getDataClusterCtrlLocal().addToVocabulary(dcpk, projection);
			
			return new WSItemPK(
					new WSDataClusterPK(itemPOJOPK.getDataClusterPOJOPK().getUniqueId()),
					itemPOJOPK.getConceptName(),
					itemPOJOPK.getIds()
			);
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}	
	
	
    
	/***************************************************************************
	 *Extract Items
	 * **************************************************************************/
	public WSPipeline extractUsingTransformer(WSExtractUsingTransformer wsExtractUsingTransformer) throws RemoteException {
		try {
			com.amalto.core.util.TransformerPluginContext context = com.amalto.core.util.Util.getItemCtrl2Local().extractUsingTransformer(
					new ItemPOJOPK(
							new DataClusterPOJOPK(wsExtractUsingTransformer.getWsItemPK().getWsDataClusterPK().getPk()),
							wsExtractUsingTransformer.getWsItemPK().getConceptName(),
							wsExtractUsingTransformer.getWsItemPK().getIds()
					),
					new TransformerPOJOPK(wsExtractUsingTransformer.getWsTransformerPK().getPk())
			);
			HashMap<String, com.amalto.core.util.TypedContent> pipeline = (HashMap<String, com.amalto.core.util.TypedContent>)context.get(TransformerCtrlBean.CTX_PIPELINE);
			return XConverter.POJO2WSOLD(pipeline);				
		} catch (com.amalto.core.util.XtentisException e) {
			e.printStackTrace();
			throw(new RemoteException(e.getLocalizedMessage()));			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSPipeline extractUsingTransformerThruView(WSExtractUsingTransformerThruView wsExtractUsingTransformerThruView) throws RemoteException {
		try {
			TransformerContext context = com.amalto.core.util.Util.getItemCtrl2Local().extractUsingTransformerThroughView(
				new DataClusterPOJOPK(wsExtractUsingTransformerThruView.getWsDataClusterPK().getPk()),
				new TransformerV2POJOPK(wsExtractUsingTransformerThruView.getWsTransformerPK().getPk()),
				new ViewPOJOPK(wsExtractUsingTransformerThruView.getWsViewPK().getPk()),
				XConverter.WS2VO(wsExtractUsingTransformerThruView.getWhereItem()),
				wsExtractUsingTransformerThruView.getSpellTreshold(),
				wsExtractUsingTransformerThruView.getOrderBy(),
				wsExtractUsingTransformerThruView.getDirection(),
				wsExtractUsingTransformerThruView.getSkip(),
				wsExtractUsingTransformerThruView.getMaxItems()
			);
			HashMap<String, com.amalto.core.util.TypedContent> pipeline = (HashMap<String, com.amalto.core.util.TypedContent>)context.get(TransformerCtrlBean.CTX_PIPELINE);
			return XConverter.POJO2WSOLD(pipeline);		
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));			
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}


	
	/***************************************************************************
	 * Delete Items
	 * **************************************************************************/
	public WSItemPK deleteItem(WSDeleteItem wsDeleteItem)
	throws RemoteException {
		try {
			ItemPOJOPK itemPK = new ItemPOJOPK(
					new DataClusterPOJOPK(wsDeleteItem.getWsItemPK().getWsDataClusterPK().getPk()),
					wsDeleteItem.getWsItemPK().getConceptName(),
					wsDeleteItem.getWsItemPK().getIds()
					);
			ItemPOJOPK ipk = com.amalto.core.util.Util.getItemCtrl2Local().deleteItem(itemPK);
			return ipk == null ? null : wsDeleteItem.getWsItemPK();

		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));	
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}    
    
	public WSInt deleteItems(WSDeleteItems wsDeleteItems)
	throws RemoteException {
		try {
			int numItems = 
				com.amalto.core.util.Util.getItemCtrl2Local().deleteItems(
						new DataClusterPOJOPK(wsDeleteItems.getWsDataClusterPK().getPk()),
						wsDeleteItems.getConceptName(),
						XConverter.WS2VO(wsDeleteItems.getWsWhereItem()),
						wsDeleteItems.getSpellTreshold()
				);
				return new WSInt(numItems);
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));	
//		} catch (com.amalto.webapp.util.XtentisException e) {
//			throw(new RemoteException(e.getLocalizedMessage()));				
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}    
	
	
	/***************************************************************************
	 * DirectQuery
	 * **************************************************************************/
	
	public WSStringArray runQuery(WSRunQuery wsRunQuery) throws RemoteException {
		try {
			DataClusterPOJOPK dcpk = 
				(wsRunQuery.getWsDataClusterPK()== null) ?
						null:
						new DataClusterPOJOPK(wsRunQuery.getWsDataClusterPK().getPk());
			Collection<String> result = 
				com.amalto.core.util.Util.getItemCtrl2Local().runQuery(
					wsRunQuery.getRevisionID(),
					dcpk,
					wsRunQuery.getQuery(),
					wsRunQuery.getParameters()
				);
			return new WSStringArray(result.toArray(new String[result.size()]));
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));				
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	};
	

	
	
	/***************************************************************************
	 * RoutingRule
	 * **************************************************************************/
	public WSRoutingRule getRoutingRule(WSGetRoutingRule wsRoutingRuleGet)
	throws RemoteException {
		try {
		    return XConverter.VO2WS( 
		    		com.amalto.core.util.Util.getRoutingRuleCtrlLocal().getRoutingRule(
							new RoutingRulePOJOPK(wsRoutingRuleGet.getWsRoutingRulePK().getPk())
					)
			);
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));	
//		} catch (com.amalto.webapp.util.XtentisException e) {
//			throw(new RemoteException(e.getLocalizedMessage()));		    
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }
   
	  public WSBoolean existsRoutingRule(WSExistsRoutingRule wsExistsRoutingRule)
	   throws RemoteException {
			try {
			    return new WSBoolean( 
			    		com.amalto.core.util.Util.getRoutingRuleCtrlLocal().existsRoutingRule(
								new RoutingRulePOJOPK(wsExistsRoutingRule.getWsRoutingRulePK().getPk())
						) != null
				);
			} catch (com.amalto.core.util.XtentisException e) {
				throw(new RemoteException(e.getLocalizedMessage()));		    
			} catch (Exception e) {
				throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
			}
	   }	    

	    
    public WSRoutingRulePKArray getRoutingRulePKs(WSGetRoutingRulePKs regexp)
    throws RemoteException {
		try {
		    Collection<RoutingRulePOJOPK> pks = com.amalto.core.util.Util.getRoutingRuleCtrlLocal().getRoutingRulePKs(regexp.getRegex());
		    ArrayList<WSRoutingRulePK> list = new ArrayList<WSRoutingRulePK>();
		    for (Iterator iter = pks.iterator(); iter.hasNext(); ) {
				RoutingRulePOJOPK pk = (RoutingRulePOJOPK) iter.next();
				list.add(new WSRoutingRulePK(pk.getUniqueId()));
			}
			return new WSRoutingRulePKArray(list.toArray(new WSRoutingRulePK[list.size()]));
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));	
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }
		    
    public WSRoutingRulePK deleteRoutingRule(WSDeleteRoutingRule wsDeleteRoutingRule)
    throws RemoteException {
		try {
		    return new WSRoutingRulePK(
		    		com.amalto.core.util.Util.getRoutingRuleCtrlLocal().removeRoutingRule(
							new RoutingRulePOJOPK(wsDeleteRoutingRule.getWsRoutingRulePK().getPk())
					).getUniqueId()
			);
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));		    
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }	
    
    public WSRoutingRulePK putRoutingRule(WSPutRoutingRule wsRoutingRule)
    throws RemoteException {
		try {
		    return new WSRoutingRulePK(
		    		com.amalto.core.util.Util.getRoutingRuleCtrlLocal().putRoutingRule(
		    				XConverter.WS2VO(wsRoutingRule.getWsRoutingRule())
					).getUniqueId()
			);
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));	
//		} catch (com.amalto.webapp.util.XtentisException e) {
//			throw(new RemoteException(e.getLocalizedMessage()));		    
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }

    
    

	
	/***************************************************************************
	 * SERVICES
	 * **************************************************************************/
	
	public WSString getServiceConfiguration(WSServiceGetConfiguration wsGetConfiguration) throws RemoteException {
		try {
			Object service= 
				com.amalto.core.util.Util.retrieveComponent(
					null, 
					wsGetConfiguration.getJndiName()
				);
			
			String configuration = (String)
				com.amalto.core.util.Util.getMethod(service, "getConfiguration").invoke(
					service,
					new Object[] {
							wsGetConfiguration.getOptionalParameter()
					}
				);
			return new WSString(configuration);
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));	
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSString putServiceConfiguration(WSServicePutConfiguration wsPutConfiguration) throws RemoteException {
		try {
			Object service= 
				com.amalto.core.util.Util.retrieveComponent(
					null, 
					wsPutConfiguration.getJndiName()
				);
			
			com.amalto.core.util.Util.getMethod(service, "putConfiguration").invoke(
				service,
				new Object[] {
						wsPutConfiguration.getConfiguration()
				}
			);
			return new WSString(wsPutConfiguration.getConfiguration());
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));	
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSString serviceAction(WSServiceAction wsServiceAction) throws RemoteException {
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("serviceAction() "+wsServiceAction.getJndiName());
		try {
			Object service= 
				com.amalto.core.util.Util.retrieveComponent(
					null, 
					wsServiceAction.getJndiName()
				);
			String result = "";
			
			if (WSServiceActionCode.EXECUTE.equals(wsServiceAction.getWsAction())) {
				
					Method method = com.amalto.core.util.Util.getMethod(service, wsServiceAction.getMethodName());
					
					result = (String)method.invoke(
							service,
							wsServiceAction.getMethodParameters()
						);				
			} else {
				if (WSServiceActionCode.START.equals(wsServiceAction.getWsAction())) {
					com.amalto.core.util.Util.getMethod(service, "start").invoke(
							service,
							new Object[] {}
						);
				} else if (WSServiceActionCode.STOP.equals(wsServiceAction.getWsAction())) {
					com.amalto.core.util.Util.getMethod(service, "stop").invoke(
							service,
							new Object[] {}
						);				
				}
				result = (String)
					com.amalto.core.util.Util.getMethod(service, "getStatus").invoke(
							service,
							new Object[] {}
						);
			}
			return new WSString(result);
		} catch (com.amalto.core.util.XtentisException e) {
			e.printStackTrace();
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	
	public WSServicesList getServicesList(WSGetServicesList wsGetServicesList) throws RemoteException {
		try {
			ArrayList<WSServicesListItem> wsList = new ArrayList<WSServicesListItem>();
			InitialContext ctx = new InitialContext();
			NamingEnumeration<NameClassPair> list = ctx.list("amalto/local/service");
			while (list.hasMore()) {
			    NameClassPair nc = list.next();
			    WSServicesListItem item =new WSServicesListItem();
			    item.setJndiName(nc.getName());
			    wsList.add(item);
			}
			return new WSServicesList(wsList.toArray(new WSServicesListItem[wsList.size()]));
//		} catch (com.amalto.core.util.XtentisException e) {
//			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}



	/***************************************************************************
	 * Stored Procedures
	 * **************************************************************************/

    public WSStoredProcedurePK deleteStoredProcedure(WSDeleteStoredProcedure wsStoredProcedureDelete) throws RemoteException {
		try {
			StoredProcedureCtrlLocal ctrl = com.amalto.core.util.Util.getStoredProcedureCtrlLocal();
			StoredProcedurePOJOPK pk =
				ctrl.removeStoredProcedure(
					new StoredProcedurePOJOPK(
							wsStoredProcedureDelete.getWsStoredProcedurePK().getPk()
					)
				);
			return new WSStoredProcedurePK(pk.getIds()[0]);		
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));	
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
    
	public WSStringArray executeStoredProcedure(WSExecuteStoredProcedure wsExecuteStoredProcedure) throws RemoteException {
		try {
			StoredProcedureCtrlLocal ctrl = com.amalto.core.util.Util.getStoredProcedureCtrlLocal();
			Collection c =
				ctrl.execute(
					new StoredProcedurePOJOPK(
						wsExecuteStoredProcedure.getWsStoredProcedurePK().getPk()
    				),
    				wsExecuteStoredProcedure.getRevisionID(),
    				new DataClusterPOJOPK(
    						wsExecuteStoredProcedure.getWsDataClusterPK().getPk()
    				),
    				wsExecuteStoredProcedure.getParameters()
    			);
			if (c==null) return null;
			String[] xmls  = new String[c.size()];
			int i=0;
			for (Iterator iter = c.iterator(); iter.hasNext(); ) {
				xmls[i++] = (String) iter.next();
			}
			return new WSStringArray(xmls);		
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));	
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSStoredProcedure getStoredProcedure(WSGetStoredProcedure wsGetStoredProcedure) throws RemoteException {
		try {
			StoredProcedureCtrlLocal ctrl = com.amalto.core.util.Util.getStoredProcedureCtrlLocal();
			StoredProcedurePOJO pojo =
				ctrl.getStoredProcedure(
					new StoredProcedurePOJOPK(
							wsGetStoredProcedure.getWsStoredProcedurePK().getPk()
					)
				);
			return XConverter.POJO2WS(pojo);
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));	
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
	
	public WSBoolean existsStoredProcedure(WSExistsStoredProcedure wsExistsStoredProcedure) throws RemoteException {
		try {
			StoredProcedureCtrlLocal ctrl = com.amalto.core.util.Util.getStoredProcedureCtrlLocal();
			StoredProcedurePOJO pojo =
				ctrl.existsStoredProcedure(
					new StoredProcedurePOJOPK(
							wsExistsStoredProcedure.getWsStoredProcedurePK().getPk()
					)
				);
			return new WSBoolean(pojo!=null);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSStoredProcedurePKArray getStoredProcedurePKs(WSRegexStoredProcedure regex) throws RemoteException {
		try {
			StoredProcedureCtrlLocal ctrl = com.amalto.core.util.Util.getStoredProcedureCtrlLocal();
			Collection c =
				ctrl.getStoredProcedurePKs(
					regex.getRegex()
				);
			if (c==null) return null;
			WSStoredProcedurePK[] pks = new WSStoredProcedurePK[c.size()];
			int i=0;
			for (Iterator iter = c.iterator(); iter.hasNext(); ) {
				pks[i++] = new WSStoredProcedurePK(
						((StoredProcedurePOJOPK) iter.next()).getIds()[0]
				);
			}
			return new WSStoredProcedurePKArray(pks);
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));	
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSStoredProcedurePK putStoredProcedure(WSPutStoredProcedure wsStoredProcedure) throws RemoteException {
		try {
			StoredProcedureCtrlLocal ctrl = com.amalto.core.util.Util.getStoredProcedureCtrlLocal();
			StoredProcedurePOJOPK pk =
				ctrl.putStoredProcedure(
					XConverter.WS2POJO(wsStoredProcedure.getWsStoredProcedure())
				);
			return new WSStoredProcedurePK(pk.getIds()[0]);
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));	
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
    
	   
	
	/***************************************************************************
	 * Ping - test that we can authenticate by getting a server response
	 * **************************************************************************/
	
	public WSString ping(WSPing wsPing) throws RemoteException {
		try {
			return new WSString(wsPing.getEcho());
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}    
	
    	

	/***************************************************************************
	 * Xtentis JCA Connector support
	 * **************************************************************************/

	private transient ConnectionFactory cxFactory = null;
	
	public WSConnectorInteractionResponse connectorInteraction(WSConnectorInteraction wsConnectorInteraction) throws RemoteException {
		// This one does not call an EJB
		
		WSConnectorInteractionResponse response = new WSConnectorInteractionResponse();
		Connection conx = null;
		try {

			String JNDIName = wsConnectorInteraction.getJNDIName();
			conx = getConnection(JNDIName);
			
			Interaction interaction = conx.createInteraction();
	    	InteractionSpecImpl interactionSpec = new InteractionSpecImpl();
	    	
			MappedRecord recordIn = new RecordFactoryImpl().createMappedRecord(RecordFactoryImpl.RECORD_IN);
			
			WSConnectorFunction cf = wsConnectorInteraction.getFunction();
			if ((WSConnectorFunction.GET_STATUS).equals(cf)) {
				interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_GET_STATUS);
			} else 	if ((WSConnectorFunction.PULL).equals(cf)) {
				interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_PULL);
			} else 	if ((WSConnectorFunction.PUSH).equals(cf)) {
				interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_PUSH);
			} else 	if ((WSConnectorFunction.START).equals(cf)) {
				interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_START);
			} else 	if ((WSConnectorFunction.STOP).equals(cf)) {
				interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_STOP);
			}
			
			recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, getMapFromKeyValues(wsConnectorInteraction.getParameters()));
						
			MappedRecord recordOut = (MappedRecord)interaction.execute(interactionSpec, recordIn);

			String code = (String)recordOut.get(RecordFactoryImpl.STATUS_CODE_OUT);
			HashMap map = (HashMap)recordOut.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT);
			
			if ("OK".equals(code)) {
				response.setCode(WSConnectorResponseCode.OK);
			} else if ("STOPPED".equals(code)) {
				response.setCode(WSConnectorResponseCode.STOPPED);
			} else if ("ERROR".equals(code)) {
				response.setCode(WSConnectorResponseCode.ERROR);
			} else {
				throw new RemoteException("Unknown code: "+code);
			}
			response.setParameters(getKeyValuesFromMap(map));
			
		} catch (ResourceException e) {
			throw new RemoteException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		} finally {
			try {conx.close();} catch (Exception cx) {
				org.apache.log4j.Logger.getLogger(this.getClass()).debug("connectorInteraction() Connection close exception: "+cx.getLocalizedMessage());
			}
		}
		return response;		
		
	}

    private Connection getConnection(String JNDIName) throws RemoteException {
    	try {
    		if (cxFactory == null)
    			cxFactory = (ConnectionFactory)(new InitialContext()).lookup(JNDIName);
	    	return cxFactory.getConnection();
    	} catch (Exception e) {
    		throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
    	}
    }
    
	private HashMap getMapFromKeyValues(WSBase64KeyValue[] params) throws RemoteException{
		try {
	    	HashMap<String,Object> map = new HashMap<String,Object>();
	    	if (params != null) {
				for (int i = 0; i < params.length; i++) {
					if (params[i]!=null) {
						String key = params[i].getKey();
						byte[] bytes = (new BASE64Decoder()).decodeBuffer(params[i].getBase64StringValue());
						if (bytes!=null) {
							ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
							ObjectInputStream ois = new ObjectInputStream(bais);
							map.put(key, ois.readObject());
						} else {
							map.put(key, null);
						}
					}
				}
	    	}
			return map;
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}    	
    }
    
    private WSBase64KeyValue[] getKeyValuesFromMap(HashMap params) throws RemoteException{    	
    	try {
    		if (params==null) return null;
    		WSBase64KeyValue[] keyValues = new WSBase64KeyValue[params.size()];
    		Set keys = params.keySet();
    		int i=0;
    		for (Iterator iter = keys.iterator(); iter.hasNext(); ) {
				String key = (String) iter.next();
				Object value = params.get(key);
				if (value!=null) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(baos);
					oos.writeObject(value);
					String base64Value = Base64Encoder.encode(baos.toByteArray());
					keyValues[i] = new WSBase64KeyValue();
					keyValues[i].setKey(key);
					keyValues[i].setBase64StringValue(base64Value);
					i++;
				}
			}
			return keyValues;
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}    	
    }

	
	
	/***************************************************************************
	 * Transformer
	 * **************************************************************************/

    public WSTransformerPK deleteTransformer(WSDeleteTransformer wsTransformerDelete) throws RemoteException {
		try {
			TransformerCtrlLocal ctrl = com.amalto.core.util.Util.getTransformerCtrlLocal();
			return
				new WSTransformerPK(
					ctrl.removeTransformer(
						new TransformerPOJOPK(
								wsTransformerDelete.getWsTransformerPK().getPk()
						)
					).getUniqueId()
				);
	
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
    

	public WSTransformer getTransformer(WSGetTransformer wsGetTransformer) throws RemoteException {
		try {
			TransformerCtrlLocal ctrl = com.amalto.core.util.Util.getTransformerCtrlLocal();
			TransformerPOJO pojo =
				ctrl.getTransformer(
					new TransformerPOJOPK(
							wsGetTransformer.getWsTransformerPK().getPk()
					)
				);
			return XConverter.POJO2WS(pojo);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSBoolean existsTransformer(WSExistsTransformer wsExistsTransformer) throws RemoteException {
		try {
			TransformerCtrlLocal ctrl = com.amalto.core.util.Util.getTransformerCtrlLocal();
			TransformerPOJO pojo =
				ctrl.existsTransformer(
					new TransformerPOJOPK(
							wsExistsTransformer.getWsTransformerPK().getPk()
					)
				);
			return new WSBoolean(pojo!=null);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
	
	public WSTransformerPKArray getTransformerPKs(WSGetTransformerPKs regex) throws RemoteException {
		try {
			TransformerCtrlLocal ctrl = com.amalto.core.util.Util.getTransformerCtrlLocal();
			Collection c =
				ctrl.getTransformerPKs(
					regex.getRegex()
				);
			if (c==null) return null;
			WSTransformerPK[] pks = new WSTransformerPK[c.size()];
			int i=0;
			for (Iterator iter = c.iterator(); iter.hasNext(); ) {
				pks[i++] = new WSTransformerPK(
						((TransformerPOJOPK) iter.next()).getUniqueId()
				);
			}
			return new WSTransformerPKArray(pks);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSTransformerPK putTransformer(WSPutTransformer wsTransformer) throws RemoteException {
		try {
			TransformerCtrlLocal ctrl = com.amalto.core.util.Util.getTransformerCtrlLocal();
			TransformerPOJOPK pk =
				ctrl.putTransformer(
						XConverter.WS2POJO(wsTransformer.getWsTransformer())
				);
			return new WSTransformerPK(pk.getUniqueId());
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
	

	public WSPipeline processBytesUsingTransformer(WSProcessBytesUsingTransformer wsProcessBytesUsingTransformer) throws RemoteException {
		try {
			com.amalto.core.util.TransformerPluginContext context = 
				Util.getTransformerCtrlLocal().process(
						new com.amalto.core.util.TypedContent(
								null,
								wsProcessBytesUsingTransformer.getWsBytes().getBytes(),
								wsProcessBytesUsingTransformer.getContentType()
						),
						new TransformerPOJOPK(wsProcessBytesUsingTransformer.getWsTransformerPK().getPk()),
						XConverter.WS2POJO(wsProcessBytesUsingTransformer.getWsOutputDecisionTable())
				);
			HashMap<String, com.amalto.core.util.TypedContent> pipeline = (HashMap<String, com.amalto.core.util.TypedContent>)context.get(TransformerCtrlBean.CTX_PIPELINE);
			//Add the Item PKs to the pipeline as comma seperated lines
			String pksAsLine = "";
			Collection<ItemPOJOPK> pks = (Collection<ItemPOJOPK>)context.get(TransformerCtrlBean.CTX_PKS);
			for (Iterator iter = pks.iterator(); iter.hasNext(); ) {
				ItemPOJOPK pk = (ItemPOJOPK) iter.next();
				if(!"".equals(pksAsLine)) pksAsLine += "\n";
				pksAsLine += pk.getConceptName()+","+Util.joinStrings(pk.getIds(), ",");
			}
			pipeline.put(
					TransformerCtrlBean.CTX_PKS, 
					new com.amalto.core.util.TypedContent(
							null,
							pksAsLine.getBytes("UTF-8"),
							"text/plain; charset=\"utf-8\""
					)
			);
			//return the pipeline
			return XConverter.POJO2WSOLD(pipeline);
		} catch (com.amalto.core.util.XtentisException e) {
		throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
		throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}



	public WSBackgroundJobPK processBytesUsingTransformerAsBackgroundJob(WSProcessBytesUsingTransformerAsBackgroundJob wsProcessBytesUsingTransformerAsBackgroundJob) throws RemoteException {
		try {
			return new WSBackgroundJobPK(
					Util.getTransformerCtrlLocal().processBytesAsBackgroundJob(
							wsProcessBytesUsingTransformerAsBackgroundJob.getWsBytes().getBytes(),
							wsProcessBytesUsingTransformerAsBackgroundJob.getContentType(),
							new TransformerPOJOPK(wsProcessBytesUsingTransformerAsBackgroundJob.getWsTransformerPK().getPk()),
							XConverter.WS2POJO(wsProcessBytesUsingTransformerAsBackgroundJob.getWsOutputDecisionTable())
					).getUniqueId()
				);
		} catch (com.amalto.core.util.XtentisException e) {
		throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
		throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}

	}



	public WSPipeline processFileUsingTransformer(WSProcessFileUsingTransformer wsProcessFileUsingTransformer) throws RemoteException {
		try {
  		//read the entire file into bytes
    		
			com.amalto.core.util.TransformerPluginContext context = 
				Util.getTransformerCtrlLocal().process(
						new com.amalto.core.util.TypedContent(
								new FileInputStream(new File(wsProcessFileUsingTransformer.getFileName())),
								null,
								wsProcessFileUsingTransformer.getContentType()
						),
						new TransformerPOJOPK(wsProcessFileUsingTransformer.getWsTransformerPK().getPk()),
						XConverter.WS2POJO(wsProcessFileUsingTransformer.getWsOutputDecisionTable())
				);
			HashMap<String, com.amalto.core.util.TypedContent> pipeline = (HashMap<String, com.amalto.core.util.TypedContent>)context.get(TransformerCtrlBean.CTX_PIPELINE);
			//Add the Item PKs to the pipeline as comma seperated lines
			String pksAsLine = "";
			Collection<ItemPOJOPK> pks = (Collection<ItemPOJOPK>)context.get(TransformerCtrlBean.CTX_PIPELINE);
			for (Iterator iter = pks.iterator(); iter.hasNext(); ) {
				ItemPOJOPK pk = (ItemPOJOPK) iter.next();
				if(!"".equals(pksAsLine)) pksAsLine += "\n";
				pksAsLine += pk.getConceptName()+","+Util.joinStrings(pk.getIds(), ",");
			}
			pipeline.put(
					TransformerCtrlBean.CTX_PIPELINE, 
					new com.amalto.core.util.TypedContent(
							null,
							pksAsLine.getBytes("UTF-8"),
							"text/plain; charset=\"utf-8\""
					)
			);
			//return the pipeline
			return XConverter.POJO2WSOLD(pipeline);
		} catch (com.amalto.core.util.XtentisException e) {
		throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
		throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}

	}


	public WSBackgroundJobPK processFileUsingTransformerAsBackgroundJob(WSProcessFileUsingTransformerAsBackgroundJob wsProcessFileUsingTransformerAsBackgroundJob) throws RemoteException {
		try {
			return new WSBackgroundJobPK(
					Util.getTransformerCtrlLocal().processFileAsBackgroundJob(
							wsProcessFileUsingTransformerAsBackgroundJob.getFileName(),
							wsProcessFileUsingTransformerAsBackgroundJob.getContentType(),
							new TransformerPOJOPK(wsProcessFileUsingTransformerAsBackgroundJob.getWsTransformerPK().getPk()),
							XConverter.WS2POJO(wsProcessFileUsingTransformerAsBackgroundJob.getWsOutputDecisionTable())
					).getUniqueId()
				);
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	

	
	/***************************************************************************
	 * TransformerV2
	 * **************************************************************************/
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSTransformerV2PK deleteTransformerV2(WSDeleteTransformerV2 wsTransformerV2Delete) throws RemoteException {
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
    


	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformerV2 getTransformerV2(WSGetTransformerV2 wsGetTransformerV2) throws RemoteException {
		try {
			TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
			TransformerV2POJO pojo =
				ctrl.getTransformer(
					new TransformerV2POJOPK(
							wsGetTransformerV2.getWsTransformerV2PK().getPk()
					)
				);
			return XConverter.POJO2WS(pojo);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBoolean existsTransformerV2(WSExistsTransformerV2 wsExistsTransformerV2) throws RemoteException {
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

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformerV2PKArray getTransformerV2PKs(WSGetTransformerV2PKs regex) throws RemoteException {
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

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformerV2PK putTransformerV2(WSPutTransformerV2 wsTransformerV2) throws RemoteException {
		try {
			TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
			TransformerV2POJOPK pk =
				ctrl.putTransformer(
						XConverter.WS2POJO(wsTransformerV2.getWsTransformerV2())
				);
			return new WSTransformerV2PK(pk.getUniqueId());
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
	
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformerContext executeTransformerV2(WSExecuteTransformerV2 wsExecuteTransformerV2) throws RemoteException {
		try {
			final String RUNNING = "XtentisWSBean.executeTransformerV2.running";
			TransformerContext context = XConverter.WS2POJO(wsExecuteTransformerV2.getWsTransformerContext());
			context.put(RUNNING, Boolean.TRUE);
			TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
			ctrl.execute(
					context, 
					XConverter.WS2POJO(wsExecuteTransformerV2.getWsTypedContent()), 
					new TransformerCallBack() {
						public void contentIsReady(TransformerContext context) throws com.amalto.core.util.XtentisException {
							org.apache.log4j.Logger.getLogger(this.getClass()).debug("XtentisWSBean.executeTransformerV2.contentIsReady() ");
						}
						public void done(TransformerContext context) throws com.amalto.core.util.XtentisException {
							org.apache.log4j.Logger.getLogger(this.getClass()).debug("XtentisWSBean.executeTransformerV2.done() ");
							context.put(RUNNING, Boolean.FALSE);
						}
					}
			);
			while (((Boolean)context.get(RUNNING)).booleanValue()) {
				Thread.sleep(100);
			}
			return XConverter.POJO2WS(context);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBackgroundJobPK executeTransformerV2AsJob(WSExecuteTransformerV2AsJob wsExecuteTransformerV2AsJob) throws RemoteException {
		try {
			TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
			BackgroundJobPOJOPK bgPK = 
				ctrl.executeAsJob(					
						XConverter.WS2POJO(wsExecuteTransformerV2AsJob.getWsTransformerContext()),
						new TransformerCallBack() {
							public void contentIsReady(TransformerContext context) throws com.amalto.core.util.XtentisException {
								org.apache.log4j.Logger.getLogger(this.getClass()).debug("XtentisWSBean.executeTransformerV2AsJob.contentIsReady() ");
							}
							public void done(TransformerContext context) throws com.amalto.core.util.XtentisException {
								org.apache.log4j.Logger.getLogger(this.getClass()).debug("XtentisWSBean.executeTransformerV2AsJob.done() ");
							}
						}
				);
			return new WSBackgroundJobPK(bgPK.getUniqueId());
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
	
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformerContext extractThroughTransformerV2(WSExtractThroughTransformerV2 wsExtractThroughTransformerV2) throws RemoteException {
		try {
			TransformerV2CtrlLocal ctrl = Util.getTransformerV2CtrlLocal();
			return XConverter.POJO2WS(
				ctrl.extractThroughTransformer(
					new TransformerV2POJOPK(wsExtractThroughTransformerV2.getWsTransformerV2PK().getPk()),
					XConverter.WS2POJO(wsExtractThroughTransformerV2.getWsItemPK())
				)
			);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
	
	
//	private WSPipeline POJO2WS(HashMap<String,TypedContent> pipeline) throws Exception{
//		if (pipeline == null) return null;
//		
//		ArrayList<WSPipelineTypedContentEntry> entries = new ArrayList<WSPipelineTypedContentEntry>();
//		Set keys = pipeline.keySet();
//		for (Iterator iter = keys.iterator(); iter.hasNext(); ) {
//			String output = (String) iter.next();
//			TypedContent content = pipeline.get(output);
//			byte[] bytes = content.getContentBytes();
//			WSExtractedContent wsContent = new WSExtractedContent(
//				new WSByteArray(bytes),
//				content.getContentType()
//			);
//			WSPipelineTypedContentEntry wsEntry = new WSPipelineTypedContentEntry(
//				output,
//				wsContent
//			);
//			entries.add(wsEntry);
//		}
//		return new WSPipeline(entries.toArray(new WSPipelineTypedContentEntry[entries.size()]));
//	}
	
	
	
	/***************************************************************************
	 * TRANSFORMER PLUGINS V2
	 * **************************************************************************/
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 	 
	 *  	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBoolean existsTransformerPluginV2(WSExistsTransformerPluginV2 wsExistsTransformerPlugin) throws RemoteException {
		try {
			return new WSBoolean(
				Util.existsComponent(
					null, 
					wsExistsTransformerPlugin.getJndiName()
				)
			);
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSString getTransformerPluginV2Configuration(WSTransformerPluginV2GetConfiguration wsGetConfiguration) throws RemoteException {
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
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSString putTransformerPluginV2Configuration(WSTransformerPluginV2PutConfiguration wsPutConfiguration) throws RemoteException {
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
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 	 
	 *  	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformerPluginV2Details getTransformerPluginV2Details(WSGetTransformerPluginV2Details wsGetTransformerPluginDetails) throws RemoteException {
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
					wsInputVariableDescriptors.add(XConverter.POJO2WS(descriptor));
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
					wsOutputVariableDescriptors.add(XConverter.POJO2WS(descriptor));
				}
			}

			return new WSTransformerPluginV2Details(
					wsInputVariableDescriptors.toArray(new WSTransformerPluginV2VariableDescriptor[wsInputVariableDescriptors.size()]),
					wsOutputVariableDescriptors.toArray(new WSTransformerPluginV2VariableDescriptor[wsOutputVariableDescriptors.size()]),
					description,
					documentation,
					parametersSchema
			);
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}


	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 	 
	 *  	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformerPluginV2SList getTransformerPluginV2SList(WSGetTransformerPluginV2SList wsGetTransformerPluginsList) throws RemoteException {
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
	
	
	
	/***************************************************************************
	 * Role
	 * **************************************************************************/

    public WSRolePK deleteRole(WSDeleteRole wsRoleDelete) throws RemoteException {
		try {
			RoleCtrlLocal ctrl = com.amalto.core.util.Util.getRoleCtrlLocal();
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
    
	public WSRole getRole(WSGetRole wsGetRole) throws RemoteException {
		try {
			RoleCtrlLocal ctrl = com.amalto.core.util.Util.getRoleCtrlLocal();
			RolePOJO pojo =
				ctrl.getRole(
					new RolePOJOPK(
							wsGetRole.getWsRolePK().getPk()
					)
				);
			return XConverter.POJO2WS(pojo);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
	
	public WSBoolean existsRole(WSExistsRole wsExistsRole) throws RemoteException {
		try {
			RoleCtrlLocal ctrl = com.amalto.core.util.Util.getRoleCtrlLocal();
			RolePOJO pojo =
				ctrl.existsRole(
					new RolePOJOPK(
							wsExistsRole.getWsRolePK().getPk()
					)
				);
			return new WSBoolean(pojo!=null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSRolePKArray getRolePKs(WSGetRolePKs regex) throws RemoteException {
		try {
			RoleCtrlLocal ctrl = com.amalto.core.util.Util.getRoleCtrlLocal();
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
			RoleCtrlLocal ctrl = com.amalto.core.util.Util.getRoleCtrlLocal();
			RolePOJOPK pk =
				ctrl.putRole(
						XConverter.WS2POJO(wsRole.getWsRole())
				);
			return new WSRolePK(pk.getUniqueId());
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
	
	public WSStringArray getObjectsForRoles(WSGetObjectsForRoles wsRoleDelete) throws RemoteException {
		Set<String> names = ObjectPOJO.getObjectsNames2ClassesMap().keySet(); 
		return new WSStringArray(
				names.toArray(new String[names.size()])
		);
	}



	/***************************************************************************
	 * Menu
	 * **************************************************************************/

    public WSMenuPK deleteMenu(WSDeleteMenu wsMenuDelete) throws RemoteException {
		try {
			MenuCtrlLocal ctrl = com.amalto.core.util.Util.getMenuCtrlLocal();
			return
				new WSMenuPK(
					ctrl.removeMenu(
						new MenuPOJOPK(
								wsMenuDelete.getWsMenuPK().getPk()
						)
					).getUniqueId()
				);
	
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
    
	public WSMenu getMenu(WSGetMenu wsGetMenu) throws RemoteException {
		try {
			MenuCtrlLocal ctrl = com.amalto.core.util.Util.getMenuCtrlLocal();
			MenuPOJO pojo =
				ctrl.getMenu(
					new MenuPOJOPK(
							wsGetMenu.getWsMenuPK().getPk()
					)
				);
			return XConverter.POJO2WS(pojo);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSBoolean existsMenu(WSExistsMenu wsExistsMenu) throws RemoteException {
		try {
			MenuCtrlLocal ctrl = Util.getMenuCtrlLocal();
			MenuPOJO pojo =
				ctrl.existsMenu(
					new MenuPOJOPK(
							wsExistsMenu.getWsMenuPK().getPk()
					)
				);
			return new WSBoolean(pojo!=null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
	
	public WSMenuPKArray getMenuPKs(WSGetMenuPKs regex) throws RemoteException {
		try {
			MenuCtrlLocal ctrl = com.amalto.core.util.Util.getMenuCtrlLocal();
			Collection c =
				ctrl.getMenuPKs(
					regex.getRegex()
				);
			if (c==null) return null;
			WSMenuPK[] pks = new WSMenuPK[c.size()];
			int i=0;
			for (Iterator iter = c.iterator(); iter.hasNext(); ) {
				pks[i++] = new WSMenuPK(
						((MenuPOJOPK) iter.next()).getUniqueId()
				);
			}
			return new WSMenuPKArray(pks);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSMenuPK putMenu(WSPutMenu wsMenu) throws RemoteException {
		try {
			MenuCtrlLocal ctrl = com.amalto.core.util.Util.getMenuCtrlLocal();
			MenuPOJOPK pk =
				ctrl.putMenu(
						XConverter.WS2POJO(wsMenu.getWsMenu())
				);
			return new WSMenuPK(pk.getUniqueId());
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
		
	
	/***************************************************************************
	 * BACKGROUND JOBS
	 * **************************************************************************/

	public WSBackgroundJobPKArray findBackgroundJobPKs(WSFindBackgroundJobPKs status) throws RemoteException {
		try {
			 throw new RemoteException("WSBackgroundJobPKArray is not implemented in this version of the core");
		} catch (RemoteException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}


	public WSBackgroundJob getBackgroundJob(WSGetBackgroundJob wsGetBackgroundJob) throws RemoteException {
		try {
		    return XConverter.POJO2WS( 
					BackgroundJobCtrlUtil.getLocalHome().create().getBackgroundJob(
							new BackgroundJobPOJOPK(wsGetBackgroundJob.getPk())
					)
			);
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (RemoteException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSBackgroundJobPK putBackgroundJob(WSPutBackgroundJob wsPutBackgroundJob) throws RemoteException {
		try {
	        return new WSBackgroundJobPK(BackgroundJobCtrlUtil.getLocalHome().create().putBackgroundJob(
	        		XConverter.WS2POJO(wsPutBackgroundJob.getWsBackgroundJob())
					).getUniqueId());
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (RemoteException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}


	public WSString count(WSCount wsCount) throws RemoteException {
		try {
			long count = Util.getItemCtrl2Local().count(
				new DataClusterPOJOPK(wsCount.getWsDataClusterPK().getPk()),
				wsCount.getCountPath(),
				XConverter.WS2VO(wsCount.getWhereItem()),
				wsCount.getSpellTreshold()
			);
			return new WSString(count+"");
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (RemoteException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }

	/***************************************************************************
	 * Routing Order V2
	 * **************************************************************************/

	public WSRoutingOrderV2PK deleteRoutingOrderV2(WSDeleteRoutingOrderV2 wsDeleteRoutingOrder) throws RemoteException {
		try {
			RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
			return XConverter.POJO2WS(ctrl.removeRoutingOrder(XConverter.WS2POJO(wsDeleteRoutingOrder.getWsRoutingOrderPK())));
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (RemoteException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }



	public WSRoutingOrderV2PK executeRoutingOrderV2Asynchronously(WSExecuteRoutingOrderV2Asynchronously wsExecuteRoutingOrderAsynchronously) throws RemoteException {
		try {
			RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
			AbstractRoutingOrderV2POJO ro = ctrl.getRoutingOrder(XConverter.WS2POJO(wsExecuteRoutingOrderAsynchronously.getRoutingOrderV2PK()));
			ctrl.executeAsynchronously(ro);
			return XConverter.POJO2WS(ro.getAbstractRoutingOrderPOJOPK());
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (RemoteException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }



	public WSString executeRoutingOrderV2Synchronously(WSExecuteRoutingOrderV2Synchronously wsExecuteRoutingOrderSynchronously) throws RemoteException {
		try {
			RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
			AbstractRoutingOrderV2POJO ro = ctrl.getRoutingOrder(XConverter.WS2POJO(wsExecuteRoutingOrderSynchronously.getRoutingOrderV2PK()));
			return new WSString(ctrl.executeSynchronously(ro));
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (RemoteException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }



	public WSRoutingOrderV2 existsRoutingOrderV2(WSExistsRoutingOrderV2 wsExistsRoutingOrder) throws RemoteException {
		try {
			RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
			return XConverter.POJO2WS(ctrl.existsRoutingOrder(XConverter.WS2POJO(wsExistsRoutingOrder.getWsRoutingOrderPK())));
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (RemoteException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }



	public WSRoutingOrderV2 getRoutingOrderV2(WSGetRoutingOrderV2 wsGetRoutingOrderV2) throws RemoteException {
		try {
			RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
			return XConverter.POJO2WS(ctrl.getRoutingOrder(XConverter.WS2POJO(wsGetRoutingOrderV2.getWsRoutingOrderPK())));
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (RemoteException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }



	public WSRoutingOrderV2PKArray getRoutingOrderV2PKsByCriteria(WSGetRoutingOrderV2PKsByCriteria wsGetRoutingOrderV2PKsByCriteria) throws RemoteException {
		try {
			WSRoutingOrderV2PKArray wsPKArray = new WSRoutingOrderV2PKArray();
			ArrayList<WSRoutingOrderV2PK> list = new ArrayList<WSRoutingOrderV2PK>();
			//fetch results
			Collection<AbstractRoutingOrderV2POJOPK> pks = getRoutingOrdersByCriteria(wsGetRoutingOrderV2PKsByCriteria.getWsSearchCriteria());
			for (Iterator<AbstractRoutingOrderV2POJOPK> iterator = pks.iterator(); iterator.hasNext(); ) {
				AbstractRoutingOrderV2POJOPK abstractRoutingOrderV2POJOPK = iterator.next();
				list.add(XConverter.POJO2WS(abstractRoutingOrderV2POJOPK));
			}
			wsPKArray.setWsRoutingOrder(list.toArray(new WSRoutingOrderV2PK[list.size()]));
			return wsPKArray;
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (RemoteException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }



	public WSRoutingOrderV2Array getRoutingOrderV2SByCriteria(WSGetRoutingOrderV2SByCriteria wsGetRoutingOrderV2SByCriteria) throws RemoteException {
		try {
			RoutingOrderV2CtrlLocal ctrl = Util.getRoutingOrderV2CtrlLocal();
			WSRoutingOrderV2Array wsPKArray = new WSRoutingOrderV2Array();
			ArrayList<WSRoutingOrderV2> list = new ArrayList<WSRoutingOrderV2>();
			//fetch results
			Collection<AbstractRoutingOrderV2POJOPK> pks = getRoutingOrdersByCriteria(wsGetRoutingOrderV2SByCriteria.getWsSearchCriteria());
			for (Iterator<AbstractRoutingOrderV2POJOPK> iterator = pks.iterator(); iterator.hasNext(); ) {
				AbstractRoutingOrderV2POJOPK abstractRoutingOrderV2POJOPK = iterator.next();
				list.add(XConverter.POJO2WS(ctrl.getRoutingOrder(abstractRoutingOrderV2POJOPK)));
			}
			wsPKArray.setWsRoutingOrder(list.toArray(new WSRoutingOrderV2[list.size()]));
			return wsPKArray;
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (RemoteException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
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
	 * Versioning
	 * **************************************************************************/
	
	
	
	public WSVersioningSystemConfiguration getVersioningSystemConfiguration(WSGetVersioningSystemConfiguration wsGetVersioningSystemConfiguration) throws RemoteException {
		throw new RemoteException("Not Supported Yet");
    }



	public WSString putVersioningSystemConfiguration(WSPutVersioningSystemConfiguration wsPutVersioningSystemConfiguration) throws RemoteException {
		throw new RemoteException("Not Supported Yet");
    }




	public WSVersioningInfo versioningGetInfo(WSVersioningGetInfo wsVersioningGetInfo) throws RemoteException {
		try {
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
//		} catch (XtentisException e) {
//			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (RemoteException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }



	public WSVersioningItemsHistory versioningGetItemsHistory(WSVersioningGetItemsHistory wsVersioningGetItemsHistory) throws RemoteException {
		throw new RemoteException("Not Supported Yet");
    }



	public WSVersioningObjectsHistory versioningGetObjectsHistory(WSVersioningGetObjectsHistory wsVersioningGetObjectsHistory) throws RemoteException {
		try {
			VersioningSystemCtrlLocal ctrl = Util.getVersioningSystemCtrlLocal();
			com.amalto.core.webservice.WSVersioningObjectsHistoryObjects[] objects =
				ctrl.getObjectsHistory(
					wsVersioningGetObjectsHistory.getVersioningSystemName() == null ? null : new VersioningSystemPOJOPK(wsVersioningGetObjectsHistory.getVersioningSystemName()), 
					wsVersioningGetObjectsHistory.getType(), 
					wsVersioningGetObjectsHistory.getNames()
				).getObjects();
			if (objects == null) return null;
			
			WSVersioningObjectsHistoryObjects[] newObjects = new WSVersioningObjectsHistoryObjects[objects.length];
			for (int i = 0; i < objects.length; i++) {
				WSVersioningHistoryEntry[] newEntries = null;
				if (objects[i].getWsHistoryEntries() != null) {
					newEntries = new WSVersioningHistoryEntry[objects[i].getWsHistoryEntries().length];
					for (int j = 0; j < objects[i].getWsHistoryEntries().length; j++) {
						newEntries[j] = new WSVersioningHistoryEntry(
							objects[i].getWsHistoryEntries()[j].getTag(),
							objects[i].getWsHistoryEntries()[j].getRevision(),
							objects[i].getWsHistoryEntries()[j].getDate(),
							objects[i].getWsHistoryEntries()[j].getAuthor(),
							objects[i].getWsHistoryEntries()[j].getComments()
						);
					}
				}
				//set the new object
				newObjects[i] = new WSVersioningObjectsHistoryObjects(
					objects[i].getType(),
					objects[i].getName(),
					newEntries
				);
			}
			//set the main history
			return new WSVersioningObjectsHistory(newObjects);
			
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }



	public WSBackgroundJobPK versioningRestoreItems(WSVersioningRestoreItems wsVersioningRestoreItems) throws RemoteException {
		try {
			VersioningSystemCtrlLocal ctrl = Util.getVersioningSystemCtrlLocal();
			ArrayList<ItemPOJOPK> itemPKs = new ArrayList<ItemPOJOPK>();
			if (wsVersioningRestoreItems.getWsItemPKs()!=null) {
				for (int i = 0; i < wsVersioningRestoreItems.getWsItemPKs().length; i++) {
					itemPKs.add(XConverter.WS2POJO(wsVersioningRestoreItems.getWsItemPKs()[i]));
				}
			}
			return new WSBackgroundJobPK(
				ctrl.restoreItemsAsJob(
						wsVersioningRestoreItems.getVersioningSystemName() == null ? null : new VersioningSystemPOJOPK(wsVersioningRestoreItems.getVersioningSystemName()),
						wsVersioningRestoreItems.getTag(),
						itemPKs.toArray(new ItemPOJOPK[itemPKs.size()])
				).getUniqueId()
			);
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (RemoteException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }



	public WSBackgroundJobPK versioningRestoreObjects(WSVersioningRestoreObjects wsVersioningRestoreObjects) throws RemoteException {
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
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }



	public WSBackgroundJobPK versioningTagItems(WSVersioningTagItems wsVersioningTagItems) throws RemoteException {
		try {
			VersioningSystemCtrlLocal ctrl = Util.getVersioningSystemCtrlLocal();
			ArrayList<ItemPOJOPK> itemPKs = new ArrayList<ItemPOJOPK>();
			if (wsVersioningTagItems.getWsItemPKs()!=null) {
				for (int i = 0; i < wsVersioningTagItems.getWsItemPKs().length; i++) {
					itemPKs.add(XConverter.WS2POJO(wsVersioningTagItems.getWsItemPKs()[i]));
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
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (RemoteException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }



	public WSBackgroundJobPK versioningTagObjects(WSVersioningTagObjects wsVersioningTagObjects) throws RemoteException {
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
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }		
	
	
	
	/***************************************************************************
	 * Routing Engine V2
	 * **************************************************************************/
	
	public WSRoutingRulePKArray routeItemV2(WSRouteItemV2 wsRouteItem) throws RemoteException {
		try {
			RoutingEngineV2CtrlLocal ctrl = Util.getRoutingEngineV2CtrlLocal();
			RoutingRulePOJOPK[] rules = ctrl.route(XConverter.WS2POJO(wsRouteItem.getWsItemPK()));
			ArrayList<WSRoutingRulePK> list = new ArrayList<WSRoutingRulePK>();
			if(rules==null || rules.length==0) return null;
			for (int i = 0; i < rules.length; i++) {
				list.add(new WSRoutingRulePK(rules[i].getUniqueId()));
			}
			return new WSRoutingRulePKArray(list.toArray(new WSRoutingRulePK[list.size()]));
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (RemoteException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	
	
	public WSRoutingEngineV2Status routingEngineV2Action(WSRoutingEngineV2Action wsRoutingEngineAction) throws RemoteException {
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
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
		
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
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	
	/***************************************************************************
	 * Universes
	 ***************************************************************************/
	
	public WSUniversePK deleteUniverse(WSDeleteUniverse wsUniverseDelete) throws RemoteException {
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
			
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	public WSBoolean existsUniverse(WSExistsUniverse wsExistsUniverse) throws RemoteException {
		try {
			
			UniverseCtrlLocal ctrl = Util.getUniverseCtrlLocal();
			UniversePOJO pojo =
				ctrl.existsUniverse(
					new UniversePOJOPK(
							wsExistsUniverse.getWsUniversePK().getPk()
					)
				);
			return new WSBoolean(pojo!=null);
			
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	public WSStringArray getObjectsForUniverses(WSGetObjectsForUniverses regex) throws RemoteException {
		try {
			
			Set<String> names = UniversePOJO.getXtentisObjectName(); 
			return new WSStringArray(
					names.toArray(new String[names.size()])
			);
			
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	public WSUniverse getUniverse(WSGetUniverse wsGetUniverse) throws RemoteException {
		try {
			
			UniverseCtrlLocal ctrl = Util.getUniverseCtrlLocal();
			UniversePOJO pojo =
				ctrl.getUniverse(
					new UniversePOJOPK(
							wsGetUniverse.getWsUniversePK().getPk()
					)
				);
			return XConverter.POJO2WS(pojo);
			
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (RemoteException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	public WSUniversePKArray getUniversePKs(WSGetUniversePKs regex) throws RemoteException {
		try {
			
			UniverseCtrlLocal ctrl = Util.getUniverseCtrlLocal();
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
			
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	public WSUniversePK putUniverse(WSPutUniverse wsUniverse) throws RemoteException {
		try {
			
			UniverseCtrlLocal ctrl = Util.getUniverseCtrlLocal();
			UniversePOJOPK pk =
				ctrl.putUniverse(
						XConverter.WS2POJO(wsUniverse.getWsUniverse())
				);
			return new WSUniversePK(pk.getUniqueId());
			
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (RemoteException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	public WSUniverse getCurrentUniverse(WSGetCurrentUniverse wsGetCurrentUniverse) throws RemoteException {
		try {
			//this should force the User in the JACC context
			Util.getUniverseCtrlLocal();
			//Fetch the user
			LocalUser user = LocalUser.getLocalUser();
			return XConverter.POJO2WS(user.getUniverse());
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (RemoteException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	
	
	/***************************************************************************
	 * Syncronization Plan
	 ***************************************************************************/
	
	public WSSynchronizationPlanPK deleteSynchronizationPlan(WSDeleteSynchronizationPlan wsSynchronizationPlanDelete) throws RemoteException {
		try {
			
			SynchronizationPlanCtrlLocal ctrl = Util.getSynchronizationPlanCtrlLocal();
			return
				new WSSynchronizationPlanPK(
					ctrl.removeSynchronizationPlan(
						new SynchronizationPlanPOJOPK(
								wsSynchronizationPlanDelete.getWsSynchronizationPlanPK().getPk()
						)
					).getUniqueId()
				);
			
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	public WSBoolean existsSynchronizationPlan(WSExistsSynchronizationPlan wsExistsSynchronizationPlan) throws RemoteException {
		try {
			
			SynchronizationPlanCtrlLocal ctrl = Util.getSynchronizationPlanCtrlLocal();
			SynchronizationPlanPOJO pojo =
				ctrl.existsSynchronizationPlan(
					new SynchronizationPlanPOJOPK(
							wsExistsSynchronizationPlan.getWsSynchronizationPlanPK().getPk()
					)
				);
			return new WSBoolean(pojo!=null);
			
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	
	public WSStringArray getSynchronizationPlanItemsAlgorithms(WSGetSynchronizationPlanItemsAlgorithms regex) throws RemoteException {
		Set<String> names = SynchronizationPlanPOJO.getItemsAlgorithmNames(); 
		return new WSStringArray(
				names.toArray(new String[names.size()])
		);
	}
	
	public WSStringArray getSynchronizationPlanObjectsAlgorithms(WSGetSynchronizationPlanObjectsAlgorithms regex) throws RemoteException {
		Set<String> names = SynchronizationPlanPOJO.getObjectsAlgorithmNames(); 
		return new WSStringArray(
				names.toArray(new String[names.size()])
		);
	}

	
	public WSSynchronizationPlan getSynchronizationPlan(WSGetSynchronizationPlan wsGetSynchronizationPlan) throws RemoteException {
		try {
			
			SynchronizationPlanCtrlLocal ctrl = Util.getSynchronizationPlanCtrlLocal();
			SynchronizationPlanPOJO pojo =
				ctrl.getSynchronizationPlan(
					new SynchronizationPlanPOJOPK(
							wsGetSynchronizationPlan.getWsSynchronizationPlanPK().getPk()
					)
				);
			return XConverter.POJO2WS(pojo);
			
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (RemoteException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	public WSStringArray getObjectsForSynchronizationPlans(WSGetObjectsForSynchronizationPlans regex) throws RemoteException {
		try {
			
			Set<String> names = SynchronizationPlanPOJO.getXtentisObjectNames(); 
			return new WSStringArray(
					names.toArray(new String[names.size()])
			);
			
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	public WSSynchronizationPlanPKArray getSynchronizationPlanPKs(WSGetSynchronizationPlanPKs regex) throws RemoteException {
		try {
			
			SynchronizationPlanCtrlLocal ctrl = Util.getSynchronizationPlanCtrlLocal();
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
			
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	public WSSynchronizationPlanPK putSynchronizationPlan(WSPutSynchronizationPlan wsSynchronizationPlan) throws RemoteException {
		try {
			
			SynchronizationPlanCtrlLocal ctrl = Util.getSynchronizationPlanCtrlLocal();
			SynchronizationPlanPOJOPK pk =
				ctrl.putSynchronizationPlan(
						XConverter.WS2POJO(wsSynchronizationPlan.getWsSynchronizationPlan())
				);
			return new WSSynchronizationPlanPK(pk.getUniqueId());
			
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (RemoteException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	public WSSynchronizationPlanStatus synchronizationPlanAction(WSSynchronizationPlanAction wsSynchronizationPlanAction) throws RemoteException {
		try {
			
			int actionCode = -1;
			if (WSSynchronizationPlanActionCode.START_FULL.equals(wsSynchronizationPlanAction.getWsAction())) {
				actionCode = SynchronizationPlanPOJO.ACTION_START_FULL;
			} else if (WSSynchronizationPlanActionCode.START_DIFFERENTIAL.equals(wsSynchronizationPlanAction.getWsAction())) {
				actionCode = SynchronizationPlanPOJO.ACTION_START_DIFFERENTIAL;
			} else if (WSSynchronizationPlanActionCode.STOP.equals(wsSynchronizationPlanAction.getWsAction())) {
				actionCode = SynchronizationPlanPOJO.ACTION_STOP;
			} else if (WSSynchronizationPlanActionCode.STATUS.equals(wsSynchronizationPlanAction.getWsAction())) {
				actionCode = SynchronizationPlanPOJO.ACTION_GET_STATUS;
			} else if (WSSynchronizationPlanActionCode.RESET.equals(wsSynchronizationPlanAction.getWsAction())) {
				actionCode = SynchronizationPlanPOJO.ACTION_RESET;
			}
			
			SynchronizationPlanCtrlLocal ctrl = SynchronizationPlanCtrlUtil.getLocalHome().create();
			String[] res = ctrl.action(
				actionCode, 
				new SynchronizationPlanPOJOPK(wsSynchronizationPlanAction.getWsSynchronizationPlanPK().getPk())
			);
			String statusCode = res[0];
			String statusMsg = res[1];
			long lastRunStartedMillis = Long.parseLong(res[2]);
			long lastRunStoppedMillis = Long.parseLong(res[3]);
				
			Calendar lastRunStarted = Calendar.getInstance();
			lastRunStarted.setTimeInMillis(lastRunStartedMillis);
			
			Calendar lastRunStopped = Calendar.getInstance();
			lastRunStopped.setTimeInMillis(lastRunStoppedMillis);
			
			WSSynchronizationPlanStatusCode wsStatusCode = null;
			if (SynchronizationPlanPOJO.STATUS_COMPLETED.equals(statusCode)) {
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
				
				
			return new WSSynchronizationPlanStatus(
				wsStatusCode, 
				statusMsg,
				lastRunStarted,
				lastRunStopped
			);
			
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	
	public WSStringArray synchronizationGetUnsynchronizedObjectsIDs(WSSynchronizationGetUnsynchronizedObjectsIDs wsSynchronizationGetUnsynchronizedObjectsIDs) throws RemoteException {
		try {
			
			SynchronizationPlanCtrlLocal ctrl = Util.getSynchronizationPlanCtrlLocal();
			ArrayList<String> list = ctrl.synchronizationGetAllUnsynchronizedObjectsIDs(
				wsSynchronizationGetUnsynchronizedObjectsIDs.getRevisionID(), 
				wsSynchronizationGetUnsynchronizedObjectsIDs.getObjectName(), 
				wsSynchronizationGetUnsynchronizedObjectsIDs.getInstancePattern(), 
				wsSynchronizationGetUnsynchronizedObjectsIDs.getSynchronizationPlanName()
			);
			
			return new WSStringArray(list.toArray(new String[list.size()]));
			
			
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
//		} catch (RemoteException e) {
//			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	public WSString synchronizationGetObjectXML(WSSynchronizationGetObjectXML wsSynchronizationGetObjectXML) throws RemoteException {
		try {
			SynchronizationPlanCtrlLocal ctrl = Util.getSynchronizationPlanCtrlLocal();
			String xml = ctrl.synchronizationGetMarshaledObject(
				wsSynchronizationGetObjectXML.getRevisionID(), 
				wsSynchronizationGetObjectXML.getObjectName(), 
				wsSynchronizationGetObjectXML.getUniqueID()
			);
			return new WSString(xml);			
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
//		} catch (RemoteException e) {
//			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	
	public WSString synchronizationPutObjectXML(WSSynchronizationPutObjectXML wsSynchronizationPutObjectXML) throws RemoteException {
		try {
			SynchronizationPlanCtrlLocal ctrl = Util.getSynchronizationPlanCtrlLocal();
			ctrl.synchronizationPutMarshaledObject(
				wsSynchronizationPutObjectXML.getRevisionID(), 
				wsSynchronizationPutObjectXML.getObjectName(), 
				wsSynchronizationPutObjectXML.getUniqueID(),
				wsSynchronizationPutObjectXML.getXml()
			);
			return new WSString(System.currentTimeMillis()+"");

		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
//		} catch (RemoteException e) {
//			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	
	public WSItemPKArray synchronizationGetUnsynchronizedItemPKs(WSSynchronizationGetUnsynchronizedItemPKs wsSynchronizationGetUnsynchronizedItemPKs) throws RemoteException {
		try {
			
			SynchronizationPlanCtrlLocal ctrl = Util.getSynchronizationPlanCtrlLocal();
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
				result.add(XConverter.POJO2WS(itemPOJOPK));
			}
			
			return new WSItemPKArray(result.toArray(new WSItemPK[result.size()]));

		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
//		} catch (RemoteException e) {
//			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	public WSString synchronizationGetItemXML(WSSynchronizationGetItemXML wsSynchronizationGetItemXML) throws RemoteException {
		try {
			SynchronizationPlanCtrlLocal ctrl = Util.getSynchronizationPlanCtrlLocal();
			String xml = ctrl.synchronizationGetMarshaledItem(
				wsSynchronizationGetItemXML.getRevisionID(), 
				XConverter.WS2POJO(wsSynchronizationGetItemXML.getWsItemPOJOPK())
			);
			return new WSString(xml);
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
//		} catch (RemoteException e) {
//			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	public WSItemPK synchronizationPutItemXML(WSSynchronizationPutItemXML wsSynchronizationPutItemXML) throws RemoteException {
		try {
			SynchronizationPlanCtrlLocal ctrl = Util.getSynchronizationPlanCtrlLocal();
			ItemPOJOPK pojoPK = ctrl.synchronizationPutMarshaledItem(
				wsSynchronizationPutItemXML.getRevisionID(), 
				wsSynchronizationPutItemXML.getXml()
			);
			return XConverter.POJO2WS(pojoPK);
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
//		} catch (RemoteException e) {
//			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	
	/***************************************************************************
	 * Synchronization Item
	 * **************************************************************************/
	
	public WSSynchronizationItemPK deleteSynchronizationItem(WSDeleteSynchronizationItem wsSynchronizationItemDelete) throws RemoteException {
		try {
			SynchronizationItemCtrlLocal ctrl = Util.getSynchronizationItemCtrlLocal();
			
			SynchronizationItemPOJOPK pojoPK = ctrl.removeSynchronizationItem(
				new SynchronizationItemPOJOPK(wsSynchronizationItemDelete.getWsSynchronizationItemPK().getIds())
			);
			return new WSSynchronizationItemPK(pojoPK.getIds());
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
//		} catch (RemoteException e) {
//			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	public WSSynchronizationItem getSynchronizationItem(WSGetSynchronizationItem wsGetSynchronizationItem) throws RemoteException {
		try {
			SynchronizationItemCtrlLocal ctrl = Util.getSynchronizationItemCtrlLocal();
			SynchronizationItemPOJO pojo =
				ctrl.getSynchronizationItem(
					new SynchronizationItemPOJOPK(wsGetSynchronizationItem.getWsSynchronizationItemPK().getIds())
				);
			return XConverter.POJO2WS(pojo);
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
//		} catch (RemoteException e) {
//			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	public WSBoolean existsSynchronizationItem(WSExistsSynchronizationItem wsExistsSynchronizationItem) throws RemoteException {
		try {
			SynchronizationItemCtrlLocal ctrl = Util.getSynchronizationItemCtrlLocal();
			SynchronizationItemPOJO pojo =
				ctrl.existsSynchronizationItem(
					new SynchronizationItemPOJOPK(wsExistsSynchronizationItem.getWsSynchronizationItemPK().getIds())
				);
			return new WSBoolean(pojo!=null);
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
//		} catch (RemoteException e) {
//			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	public WSSynchronizationItemPKArray getSynchronizationItemPKs(WSGetSynchronizationItemPKs regex) throws RemoteException {
		try {
			SynchronizationItemCtrlLocal ctrl = Util.getSynchronizationItemCtrlLocal();
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
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
//		} catch (RemoteException e) {
//			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	public WSSynchronizationItemPK putSynchronizationItem(WSPutSynchronizationItem wsSynchronizationItem) throws RemoteException {
		try {
			SynchronizationItemCtrlLocal ctrl = Util.getSynchronizationItemCtrlLocal();
			SynchronizationItemPOJOPK pk =
				ctrl.putSynchronizationItem(
						XConverter.WS2POJO(wsSynchronizationItem.getWsSynchronizationItem())
				);
			return new WSSynchronizationItemPK(pk.getIds());
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
//		} catch (RemoteException e) {
//			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	public WSSynchronizationItem resolveSynchronizationItem(WSResolveSynchronizationItem wsResolveSynchronizationItem) throws RemoteException {
		try {
			SynchronizationItemCtrlLocal ctrl = Util.getSynchronizationItemCtrlLocal();
			SynchronizationItemPOJO pojo =
				ctrl.resolveSynchronization(
					new SynchronizationItemPOJOPK(wsResolveSynchronizationItem.getWsSynchronizationItemPK().getIds()),
					wsResolveSynchronizationItem.getNewProjection()
				);
			return XConverter.POJO2WS(pojo);
		} catch (com.amalto.core.util.XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
//		} catch (RemoteException e) {
//			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}


	public WSServiceGetDocument getServiceDocument(WSString serviceName)
	       throws RemoteException {
		try {
			Object service= 
				Util.retrieveComponent(
					null, 
					"amalto/local/service/"+serviceName.getValue()
				);

			String desc = (String)
			Util.getMethod(service, "getDescription").invoke(
				service,
				new Object[] {
						""
				}
			);
			String configuration = (String)
				Util.getMethod(service, "getConfiguration").invoke(
					service,
					new Object[] {
							""
					}
				);
			String doc = "";
			try{doc=(String)
			Util.getMethod(service, "getDocumentation").invoke(
				service,
				new Object[] {
						""
				}
			);
			}catch(Exception e){
				e.printStackTrace();
			}
			return new WSServiceGetDocument(desc,configuration,doc);
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }
		

	public WSDroppedItemPK dropItem(WSDropItem wsDropItem)
			throws RemoteException {
		try {
			WSItemPK wsItemPK=wsDropItem.getWsItemPK();
			String partPath=wsDropItem.getPartPath();
			
			DroppedItemPOJOPK droppedItemPOJOPK = Util.getItemCtrl2Local().dropItem(XConverter.WS2POJO(wsItemPK), partPath);
			
			return XConverter.POJO2WS(droppedItemPOJOPK);

		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));				
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}



	public WSDroppedItemPKArray findAllDroppedItemsPKs(
			WSFindAllDroppedItemsPKs regex) throws RemoteException {
        try {
			
			List droppedItemPOJOPKs=Util.getDroppedItemCtrlLocal().findAllDroppedItemsPKs(regex.getRegex());
			
			WSDroppedItemPK[] wsDroppedItemPKs=new WSDroppedItemPK[droppedItemPOJOPKs.size()];
			for (int i = 0; i < droppedItemPOJOPKs.size(); i++) {
				DroppedItemPOJOPK droppedItemPOJOPK = (DroppedItemPOJOPK) droppedItemPOJOPKs.get(i);
				wsDroppedItemPKs[i]=XConverter.POJO2WS(droppedItemPOJOPK);
			}
			
			return new WSDroppedItemPKArray(wsDroppedItemPKs);
			
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}


	public WSDroppedItem loadDroppedItem(WSLoadDroppedItem wsLoadDroppedItem)
			throws RemoteException {
        try {
			
        	DroppedItemPOJO droppedItemPOJO=Util.getDroppedItemCtrlLocal().loadDroppedItem(XConverter.WS2POJO(wsLoadDroppedItem.getWsDroppedItemPK()));
			
			return XConverter.POJO2WS(droppedItemPOJO);
			
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}



	public WSItemPK recoverDroppedItem(WSRecoverDroppedItem wsRecoverDroppedItem)
			throws RemoteException {
        try {
			
        	ItemPOJOPK itemPOJOPK=Util.getDroppedItemCtrlLocal().recoverDroppedItem(XConverter.WS2POJO(wsRecoverDroppedItem.getWsDroppedItemPK()));
			
			return XConverter.POJO2WS(itemPOJOPK);
			
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}



	public WSDroppedItemPK removeDroppedItem(
			WSRemoveDroppedItem wsRemoveDroppedItem) throws RemoteException {
        try {
			
        	DroppedItemPOJOPK droppedItemPOJOPK=Util.getDroppedItemCtrlLocal().removeDroppedItem(XConverter.WS2POJO(wsRemoveDroppedItem.getWsDroppedItemPK()));
			
			return XConverter.POJO2WS(droppedItemPOJOPK);
			
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	private UpdateReportItemPOJO WS2POJO(WSUpdateReportItemPOJO wsUpdateReportItemPOJO) throws Exception{
		return new UpdateReportItemPOJO(
				wsUpdateReportItemPOJO.getPath(),
				wsUpdateReportItemPOJO.getOldValue(),
				wsUpdateReportItemPOJO.getNewValue()
		);
	}

	public WSItemPK putItemWithReport(WSPutItemWithReport wsPutItemWithReport) throws RemoteException {
		try {
			
			com.amalto.webapp.util.webservices.WSPutItem wsPutItem=wsPutItemWithReport.getWsPutItem();
			String source=wsPutItemWithReport.getSource();
			String operationType="";
			Map<String,UpdateReportItemPOJO> updateReportItemsMap=new HashMap<String, UpdateReportItemPOJO>();

			//before saving
			String projection = wsPutItem.getXmlString();
			Element root = Util.parse(projection).getDocumentElement();
			
			String concept = root.getLocalName();

			DataModelPOJO dataModel  = Util.getDataModelCtrlLocal().getDataModel(
						new DataModelPOJOPK(wsPutItem.getWsDataModelPK().getPk())
				);
			Document schema=Util.parse(dataModel.getSchema());
	        XSDKey conceptKey = com.amalto.core.util.Util.getBusinessConceptKey(
	        		schema,
					concept					
			);
	       
			//get key values
			String[] ids = com.amalto.core.util.Util.getKeyValuesFromItem(
	   			root,
					conceptKey
			);				
			DataClusterPOJOPK dcpk = new DataClusterPOJOPK(wsPutItem.getWsDataClusterPK().getPk());
			ItemPOJOPK itemPOJOPK=new ItemPOJOPK(dcpk,concept, ids);			
			//get operationType
			ItemPOJO itemPoJo=ItemPOJO.load(itemPOJOPK);
			HashMap<String, UpdateReportItem> updatedPath=new HashMap<String, UpdateReportItem>();

			if(itemPoJo==null){
				operationType=UpdateReportPOJO.OPERATIONTYPE_CREATE;
			}else{
				operationType=UpdateReportPOJO.OPERATIONTYPE_UPDATEE;
				// get updated path			
				Element old=itemPoJo.getProjection();
				Element newNode=root;
				updatedPath=Util.compareElement("/"+old.getLocalName(), newNode, old);
				WSUpdateReportItemArray wsUpdateReportItemArray=new WSUpdateReportItemArray();
					for(Entry<String, UpdateReportItem> entry:updatedPath.entrySet()){
						UpdateReportItemPOJO pojo=new UpdateReportItemPOJO(entry.getValue().getPath(), entry.getValue().getOldValue(),entry.getValue().getNewValue());
						updateReportItemsMap.put(entry.getKey(), pojo);
				}				
			}
			//create resultUpdateReport
			
			String resultUpdateReport= Util.createUpdateReport(ids, concept, operationType, updatedPath, wsPutItem.getWsDataModelPK().getPk(), wsPutItem.getWsDataClusterPK().getPk());
			//invoke before saving
			if(wsPutItemWithReport.getInvokeBeforeSaving()){
				String err=Util.beforeSaving(concept, projection, resultUpdateReport);
				if(err!=null){
					err="execute beforeSaving ERROR:"+ err;
					org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
				}
			}
						
			String dataClusterPK = wsPutItem.getWsDataClusterPK().getPk();
	
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("[putItem-of-putItemWithReport] in dataCluster:"+dataClusterPK);
			WSItemPK wsi = putItem(wsPutItem);	
			
			concept=wsi.getConceptName();
			ids=wsi.getIds();			
			//additional attributes for data changes log
			LocalUser user = LocalUser.getLocalUser();
			String userName=user.getUsername();
			String revisionID ="";
			UniversePOJO universe = user.getUniverse();
            if(universe!=null){
            	revisionID=universe.getConceptRevisionID(concept);
            }
            String dataModelPK = wsPutItem.getWsDataModelPK().getPk();
			
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("[pushUpdateReport-of-putItemWithReport] with concept:"+concept+" operation:"+operationType);
			UpdateReportPOJO updateReportPOJO=new UpdateReportPOJO(concept, Util.joinStrings(ids, "."), operationType, source, System.currentTimeMillis(),dataClusterPK,dataModelPK,userName,revisionID,updateReportItemsMap);
			
				WSItemPK itemPK = putItem(
						new WSPutItem(
								new WSDataClusterPK("UpdateReport"), 
								updateReportPOJO.serialize(),
								new WSDataModelPK("UpdateReport")));
				
				routeItemV2(new WSRouteItemV2(itemPK));
				
			return wsi;			
		}catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));			
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}

	}
	
}
