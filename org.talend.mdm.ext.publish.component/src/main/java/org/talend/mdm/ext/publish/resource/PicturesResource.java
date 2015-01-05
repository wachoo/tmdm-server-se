// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.ext.publish.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.talend.mdm.ext.publish.model.PicturePojo;
import org.talend.mdm.ext.publish.util.CommonUtil;
import org.talend.mdm.ext.publish.util.DAOFactory;
import org.talend.mdm.ext.publish.util.PicturesDAO;

/**
 * Resource which has only one representation.
 * 
 */
public class PicturesResource extends BaseResource {

    private static final Logger LOGGER = Logger.getLogger(PicturesResource.class);

    // TODO See talend.ext.images.server.ImageServerInfoServlet#getUploadPath
    private static final String picturesLocation = System.getProperty("mdm.root") + File.separator + "resources" + File.separator + "upload"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    private PicturesDAO picturesDAO = null;

    private List<PicturePojo> PicturePojos = null;

    public PicturesResource(Context context, Request request, Response response) {

        super(context, request, response);

        picturesDAO = DAOFactory.getUniqueInstance().getPicturesDAO(picturesLocation);

        // get resource
        PicturePojos = new ArrayList<PicturePojo>();
        try {
            String[] pks = picturesDAO.getAllPKs();
            if (pks != null && pks.length > 0) {
                for (String pk : pks) {
                    String fileName = parseFileName(pk);
                    String catalog = parseCatalog(pk);
                    String uri = parsePath(pk);

                    PicturePojos.add(new PicturePojo(pk, fileName, catalog, uri));
                }
            }
        } catch (Exception e1) {
            LOGGER.error(e1.getMessage(), e1);
        }

    }

    private String parsePath(String pk) {

        if (pk == null) {
            return ""; //$NON-NLS-1$
        }

        String catalog;
        String file;
        String path = "/imageserver/upload"; //$NON-NLS-1$

        if (pk.indexOf("-") == -1) {
            pk = "-" + pk; //$NON-NLS-1$ 
        }
        int pos = pk.indexOf("-"); //$NON-NLS-1$
        if (pos != -1) {
            catalog = pk.substring(0, pos);
            file = pk.substring(pos + 1);
        } else {
            catalog = ""; //$NON-NLS-1$
            file = pk;
        }

        if (!catalog.equals("") && !catalog.equals("/") && !catalog.equals("//")) {
            path = path + "/" + CommonUtil.urlEncode(catalog); //$NON-NLS-1$
        }
        path = path + "/" + CommonUtil.urlEncode(file); //$NON-NLS-1$
        return path;
    }

    private String parseFileName(String pk) {

        String file;

        int pos = pk.indexOf("-"); //$NON-NLS-1$
        if (pos != -1) {
            file = pk.substring(pos + 1);
        } else {
            file = pk;
        }

        return file;
    }

    private String parseCatalog(String pk) {

        if (pk.indexOf("-") == -1) {
            pk = "-" + pk; //$NON-NLS-1$
        }
        String[] pkParts = pk.split("-"); //$NON-NLS-1$
        String catalog = pkParts[0];

        return catalog;
    }

    @Override
    protected Representation getResourceRepresent(Variant variant) throws ResourceException {

        // Generate the right representation according to its media type.
        if (MediaType.TEXT_XML.equals(variant.getMediaType())) {
            return generateListRepresentation4Pictures(PicturePojos);
        }
        return null;
    }

}
