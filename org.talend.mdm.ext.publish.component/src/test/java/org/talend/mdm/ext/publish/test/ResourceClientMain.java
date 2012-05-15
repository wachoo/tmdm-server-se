/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */
package org.talend.mdm.ext.publish.test;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.restlet.Client;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

public class ResourceClientMain {

    private static Logger log = Logger.getLogger(ResourceClientMain.class);
    // Handle it using an HTTP client connector
    private static final Client client = new Client(Protocol.HTTP);

    private static final String dmuri = "http://localhost:8080/pubcomponent/secure/customTypesSets"; //$NON-NLS-1$

    private static final String picuri = "http://localhost:8080/pubcomponent/secure/pictures"; //$NON-NLS-1$

    private static final ChallengeResponse authentication = new ChallengeResponse(ChallengeScheme.HTTP_BASIC,
            MDMConfiguration.getAdminUser(), MDMConfiguration.getAdminPassword());

    public static void main(String[] args) throws IOException {

        // list
        // listDomainObject();
        listPictures();

        // Create
        // createDomainObject("TestDO1","<Test>Hello</Test>");

        // get
        // getDomainObject("TestDO1");
    }

    public static void getDomainObject(String domainObjectName) {
        Request request = new Request(Method.GET, dmuri + "/" + domainObjectName);//$NON-NLS-1$
        request.setChallengeResponse(authentication);

        // request and print response
        final Response response = client.handle(request);

        final Representation output = response.getEntity();
        try {
            output.write(System.out);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public static void listDomainObject() {

        Request request = new Request(Method.GET, dmuri);
        request.setChallengeResponse(authentication);

        // request and print response
        final Response response = client.handle(request);

        final Representation output = response.getEntity();
        try {
            output.write(System.out);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public static void createDomainObject(String name, String content) {
        // Gathering informations into a Web form.
        Form form = new Form();
        form.add("domainObjectName", name); //$NON-NLS-1$
        form.add("domainObjectContent", content); //$NON-NLS-1$
        Representation rep = form.getWebRepresentation();

        Request request = new Request(Method.POST, dmuri, rep);
        request.setChallengeResponse(authentication);

        final Response response = client.handle(request);

        final Representation output = response.getEntity();
        try {
            output.write(System.out);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public static void listPictures() {

        Request request = new Request(Method.GET, picuri);
        request.setChallengeResponse(authentication);

        // request and print response
        final Response response = client.handle(request);

        final Representation output = response.getEntity();
        try {
            output.write(System.out);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

}
