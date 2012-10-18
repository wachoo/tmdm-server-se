package com.amalto.core.objects.datamodel.ejb;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.webapp.XObjectType;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import com.amalto.commons.core.datamodel.synchronization.DMUpdateEvent;
import com.amalto.commons.core.datamodel.synchronization.DatamodelChangeNotifier;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.server.MetadataRepositoryAdmin;
import com.amalto.core.server.Server;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.Storage;
import com.amalto.core.util.XtentisException;



/**
 * @author Bruno Grieder
 * 
 */
public class DataModelPOJO extends ObjectPOJO{
   
		
    private String name;
    private String description;
    private String schema;

    private static final Logger LOGGER = Logger.getLogger(DataModelPOJO.class);
    private static final Map<String, XSystemObjects> SYSTEM_OBJECTS = XSystemObjects.getXSystemObjects(XObjectType.DATA_MODEL);
    /**
     * 
     */
    public DataModelPOJO() {
    }    
	public DataModelPOJO(String name) {
		super();
		this.name = name;
	}
	public DataModelPOJO(String name, String desc,String schema){
		this.name=name;
		this.description=desc;
		this.schema=schema;
	}

	/**
	 * @return Returns the Name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	
	/**
	 * @return Returns the Description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * 
	 * @return the xsd Schema
	 */
	public String getSchema() {
		return schema;		
	}
	
	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	
	@Override
	public ObjectPOJOPK getPK() {
		if (getName()==null) return null;
		return new ObjectPOJOPK(new String[] {name});
	}
	

	@Override
	public ObjectPOJOPK store() throws XtentisException {
		ObjectPOJOPK pk=super.store();
		return pk;
	}
	
	/* (non-Javadoc)
	 * @see com.amalto.core.ejb.ObjectPOJO#store(java.lang.String)
	 */
	@Override
	public ObjectPOJOPK store(String revisionID) throws XtentisException {
	    
	    ObjectPOJOPK objectPK=super.store(revisionID);
	    
        // TMDM-4621: Update operation has to be synchronous
        String updatedDataModelName = getPK().getUniqueId();
        if (isUserDataModel(updatedDataModelName)) { // Do not update system data clusters
            Server server = ServerContext.INSTANCE.get();
            MetadataRepositoryAdmin metadataRepositoryAdmin = server.getMetadataRepositoryAdmin();
            metadataRepositoryAdmin.remove(updatedDataModelName);
            StorageAdmin storageAdmin = server.getStorageAdmin();
            Storage storage = storageAdmin.get(updatedDataModelName, revisionID);
            if (storage != null) {
                // Storage already exists so update it.
                MetadataRepository repository = metadataRepositoryAdmin.get(updatedDataModelName);
                Set<FieldMetadata> indexedFields = metadataRepositoryAdmin.getIndexedFields(updatedDataModelName);
                storage.prepare(repository, indexedFields, true, false);
            } else {
                LOGGER.warn("No SQL storage defined for data model '" + updatedDataModelName + "'. No SQL storage to update."); //$NON-NLS-1$//$NON-NLS-2$
            }

            Storage stagingStorage = storageAdmin.get(updatedDataModelName + StorageAdmin.STAGING_SUFFIX, revisionID);
            if (stagingStorage != null) {
                // Storage already exists so update it.
                MetadataRepository stagingRepository = metadataRepositoryAdmin.get(updatedDataModelName
                        + StorageAdmin.STAGING_SUFFIX);
                Set<FieldMetadata> indexedFields = metadataRepositoryAdmin.getIndexedFields(updatedDataModelName);
                stagingStorage.prepare(stagingRepository, indexedFields, true, false);
            } else {
                LOGGER.warn("No SQL staging storage defined for data model '" + updatedDataModelName //$NON-NLS-1$
                        + "'. No SQL staging storage to update."); //$NON-NLS-1$
            }
        }

        //synchronize with outer agents
        DatamodelChangeNotifier dmUpdateEventNotifer = new DatamodelChangeNotifier();
        dmUpdateEventNotifer.addUpdateMessage(new DMUpdateEvent(getPK().getUniqueId(), revisionID));
        dmUpdateEventNotifer.sendMessages();
        
        return objectPK;
	}
	
    public static boolean isUserDataModel(String updatedDataModelName) {
        return !SYSTEM_OBJECTS.containsKey(updatedDataModelName) || "Update".equals(updatedDataModelName); //$NON-NLS-1$
    }
}
