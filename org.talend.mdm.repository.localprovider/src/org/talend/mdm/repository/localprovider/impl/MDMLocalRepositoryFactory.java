// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006-2011 Talend ¨C www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================
package org.talend.mdm.repository.localprovider.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.VersionUtils;
import org.talend.commons.utils.data.container.RootContainer;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.FolderHelper;
import org.talend.core.repository.model.ResourceModelUtils;
import org.talend.mdm.repository.model.mdmproperties.MDMItem;
import org.talend.mdm.repository.model.mdmproperties.MDMServerDefItem;
import org.talend.repository.localprovider.model.LocalRepositoryFactory;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 * 
 */
public class MDMLocalRepositoryFactory extends LocalRepositoryFactory {

    @Override
    public void create(Project project, Item item, IPath path, boolean... isImportItem) throws PersistenceException {
        if (!(item instanceof MDMItem)) {
            super.create(project, item, path, isImportItem);
        } else {
            computePropertyMaxInformationLevel(item.getProperty());

            if (item.getProperty().getVersion() == null) {
                item.getProperty().setVersion(VersionUtils.DEFAULT_VERSION);
            }
            if (item.getProperty().getAuthor() == null) {
                item.getProperty().setAuthor(getRepositoryContext().getUser());
            }

            if (item.getProperty().getCreationDate() == null) {
                item.getProperty().setCreationDate(new Date());
            }

            if (item.getProperty().getModificationDate() == null) {
                item.getProperty().setModificationDate(item.getProperty().getCreationDate());
            }

            ItemState itemState = PropertiesFactory.eINSTANCE.createItemState();
            if (item.getState() != null) {
                itemState.setDeleted(item.getState().isDeleted());
            } else {
                itemState.setDeleted(false);
            }
            itemState.setPath(path.toString());

            item.setState(itemState);
            IProject project2 = ResourceModelUtils.getProject(project);
            Resource itemResource;

            itemResource = create(project2, (MDMItem) item, path, ERepositoryObjectType.METADATA_MDM_SERVER_DEF);
            //
            Resource propertyResource = xmiResourceManager.createPropertyResource(itemResource);
            propertyResource.getContents().add(item.getProperty());
            propertyResource.getContents().add(item.getState());
            propertyResource.getContents().add(item);
            //
            String parentPath = getParentPath(project, item, path);
            FolderHelper folderHelper = getFolderHelper(project.getEmfProject());
            FolderItem parentFolderItem = folderHelper.getFolder(parentPath);
            if (parentFolderItem == null) {
                parentFolderItem = folderHelper.createFolder(parentPath);
            }
            boolean add = parentFolderItem.getChildren().add(item);
            //
            if (add) {
                item.setParent(parentFolderItem);
            }
            //
            xmiResourceManager.saveResource(itemResource);
            xmiResourceManager.saveResource(propertyResource);

            if (isImportItem.length == 0 || !isImportItem[0]) {
                saveProject(project);
            }
        }
    }

    @Override
    public void save(Project project, Item item) throws PersistenceException {
        if (!(item instanceof MDMItem)) {
            super.save(project, item);
        } else {
            Resource itemResource = xmiResourceManager.getItemResource(item);
            propagateFileName(project, item.getProperty());
            if (item.eResource() != null && itemResource != null) {
                xmiResourceManager.saveResource(item.eResource());
                xmiResourceManager.saveResource(itemResource);
            }
        }
    }

    private void propagateFileName(Project project, Property property) throws PersistenceException {
        List<IRepositoryViewObject> allVersionToMove = getAllVersion(project, property.getId(), false);
        for (IRepositoryViewObject object : allVersionToMove) {
            xmiResourceManager.propagateFileName(property, object.getProperty());
        }
    }

    // copy from parent class
    private String getParentPath(Project project, Item item, IPath path) {
        return ERepositoryObjectType.getFolderName(ERepositoryObjectType.getItemType(item)) + IPath.SEPARATOR + path.toString();
    }

    private Resource create(IProject project, MDMItem item, IPath path, ERepositoryObjectType type) throws PersistenceException {
        Resource itemResource = xmiResourceManager.createItemResource(project, item, path, type, false);
        if (item instanceof MDMServerDefItem) {
            itemResource.getContents().add(((MDMServerDefItem) item).getServerDef());
        }
       
        return itemResource;
    }

    @Override
    public List<IRepositoryViewObject> getAll(Project project, ERepositoryObjectType type, boolean withDeleted,
            boolean allVersions) throws PersistenceException {
//        if (type == ERepositoryObjectType.METADATA_MDM_SERVER_DEF) {
//            RootContainer<String, IRepositoryViewObject> objectFromFolder = getObjectFromFolder(project, type, true, true);
//            List<IRepositoryViewObject> result = new ArrayList<IRepositoryViewObject>(objectFromFolder.absoluteSize());
//            for (IRepositoryViewObject viewObject : objectFromFolder.getAbsoluteMembers().objects()) {
//                result.add(viewObject);
//                Property property = viewObject.getProperty();
//                Item item = property.getItem();
//                Resource eResource = property.eResource();
//                Resource eResource2 = item.eResource();
//                System.out.println();
//            }
//            return result;
//
//        }
        return super.getAll(project, type, withDeleted, allVersions);
    }
}
