// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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

import java.util.ArrayList;
import java.util.List;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.ext.publish.model.PicturePojo;
import org.talend.mdm.ext.publish.util.CommonUtil;
import org.talend.mdm.ext.publish.util.DAOFactory;
import org.talend.mdm.ext.publish.util.PicturesDAO;

import com.amalto.core.util.XtentisException;

/**
 * Resource which has only one representation.
 * 
 */
public class PicturesResource extends BaseResource {

    PicturesDAO picturesDAO = null;

    List<PicturePojo> PicturePojos = null;

    /**
     * DOC Starkey PicturesResource constructor comment.
     * 
     * @param context
     * @param request
     * @param response
     */
    public PicturesResource(Context context, Request request, Response response) {

        super(context, request, response);

        picturesDAO = DAOFactory.getUniqueInstance().getPicturesDAO("http://localhost:" + MDMConfiguration.getHttpPort()); //$NON-NLS-1$

        // get resource
        PicturePojos = new ArrayList<PicturePojo>();
        try {
            String[] pks = picturesDAO.getAllPKs();
            if (pks != null && pks.length > 0) {
                for (String pk : pks) {
                    String fileName = parseFileName(pk);
                    String catalog = parseCatalog(pk);
                    String uri = parsePath(pk);
                    String redirectUri = parseRedirectUri(pk);

                    PicturePojos.add(new PicturePojo(pk, fileName, catalog, uri, redirectUri));
                }
            }
        } catch (XtentisException e1) {
            e1.printStackTrace();
        }

    }

    /**
     * DOC Starkey Comment method "parsePath".
     * 
     * @param pk
     * @return
     */
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

    /**
     * DOC Starkey Comment method "parseRedirectUri".
     * 
     * @param pk
     * @return
     */
    private String parseRedirectUri(String pk) {

        if (pk == null) {
            return ""; //$NON-NLS-1$
        }

        String path = "/imageserver/locator"; //$NON-NLS-1$
        String catalog = ""; //$NON-NLS-1$
        String file;

        int pos = pk.indexOf("-"); //$NON-NLS-1$
        if (pos != -1) {
            catalog = pk.substring(0, pos);
            file = pk.substring(pos + 1);
        } else {
            file = pk;
        }

        if (file != null && file.length() > 0 && catalog != null && catalog.length() > 0) {
            path = path + "?imgId=" + CommonUtil.urlEncode(file) + "&imgCatalog=" + CommonUtil.urlEncode(catalog); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return path;
    }

    /**
     * DOC Starkey Comment method "parseFileName".
     * 
     * @param pk
     * @return
     */
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

    /**
     * DOC Starkey Comment method "parseCatalog".
     * 
     * @param pk
     * @return
     */
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
