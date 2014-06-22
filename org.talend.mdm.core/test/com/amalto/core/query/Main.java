/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.query;

import org.talend.mdm.commmon.metadata.MetadataRepository;
import com.amalto.core.server.MockServerLifecycle;
import com.amalto.core.server.Server;
import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.StorageWrapper;

@SuppressWarnings("nls")
public class Main {

    public static void main(String[] args) {
        String fieldName = "/home[1]/child[0]";
        System.out.println("wrong = " + isWrong(fieldName));
        fieldName = "/home/child[0]";
        System.out.println("wrong = " + isWrong(fieldName));
        fieldName = "/home/child";
        System.out.println("wrong = " + isWrong(fieldName));
    }

    private static boolean isWrong(String fieldName) {
        return fieldName.indexOf('[', fieldName.indexOf('[') + 1) > 0;
    }

    public static void main2(String[] args) throws Exception {
        Server server = ServerContext.INSTANCE.get(new MockServerLifecycle());
        server.getMetadataRepositoryAdmin().get("metadata.xsd");

        StorageWrapper wrapper = new StorageWrapper();
        wrapper.createCluster(null, "metadata.xsd");

        System.out.println("Waiting write...");
        // Thread.sleep(10000);
        System.out.println("Resuming.");

        int count = 10000;
        long insertTime = System.currentTimeMillis();
        wrapper.start("metadata.xsd");
        {
            for (int i = 0; i < count; i++) {
                wrapper.putDocumentFromString(newXML(i), "BusinessFunction.BusinessFunction." + i, "metadata.xsd", null);
            }
        }
        wrapper.commit("metadata.xsd");
        System.out.println("Insert time: " + (System.currentTimeMillis() - insertTime) / (float) count + " ms.");

        System.out.println("Waiting read...");
        // Thread.sleep(10000);
        System.out.println("Resuming.");

        long loadTime = System.currentTimeMillis();
        {
            for (int i = 0; i < count; i++) {
                wrapper.getDocumentAsString(null, "metadata.xsd", "BusinessFunction.BusinessFunction." + i);
            }
        }
        System.out.println("Read time = " + (System.currentTimeMillis() - loadTime) / (float) count + " ms.");

        wrapper.deleteCluster(null, "metadata.xsd");
    }

    private static String newXML(int id) {
        /*
         * return "<Product>\n" + "    <Id>" + id + "</Id>\n" + "    <Name>Product name</Name>\n" +
         * "    <ShortDescription>Short description word</ShortDescription>\n" +
         * "    <LongDescription>Long description</LongDescription>\n" + "    <Price>10</Price>\n" + "    <Features>\n"
         * + "        <Sizes>\n" + "            <Size>XL</Size>\n" + "            <Size>L</Size>\n" +
         * "            <Size>S</Size>\n" + "        </Sizes>\n" + "        <Colors>\n" +
         * "            <Color>Blue</Color>\n" + "            <Color>Red</Color>\n" + "        </Colors>\n" +
         * "    </Features>\n" + "</Product>";
         */
        return "<BusinessFunction xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" + "<Id>" + id + "</Id>\n"
                + "<ZthesId>asssad</ZthesId>\n" + "<Release>\n" + "<Created>2012-03-01</Created>\n" + "<Updated/>\n"
                + "<Published>false</Published>\n" + "<PublishedReleaseNumber/>\n" + "</Release>\n"
                + "<Deleted>false</Deleted>\n" + "<DeletedChange>\n" + "<NewDeletedFlag/>\n" + "<Status/>\n"
                + "</DeletedChange>\n" + "<CrcCode>123</CrcCode>\n" + "<ClassificationCode>dsadas123</ClassificationCode>\n"
                + "<ClassificationCodeChange>\n" + "<NewClassificationCode/>\n" + "<Status/>\n" + "</ClassificationCodeChange>\n"
                + "<BusinessFunction>fdggf</BusinessFunction>\n" + "<BusinessFunctionChange>\n"
                + "<NewTermName>Term name</NewTermName>\n" + "<Status>Pending</Status>\n" + "</BusinessFunctionChange>\n"
                + "<SaleableTerm/>\n" + "<VideoCategory/>\n" + "<MultiClassTerm>true</MultiClassTerm>\n" + "<GasSafeAlert/>\n"
                + "<LHDRestricted/>\n" + "<Brands>\n" + "<BrandGroup_id/>\n" + "</Brands>\n" + "<BrandsChange>\n" + "<RC>\n"
                + "<ForeignKey/>\n" + "<Action/>\n" + "<Preferred/>\n" + "<Status/>\n" + "</RC>\n" + "</BrandsChange>\n"
                + "</BusinessFunction>";
    }

    private static MetadataRepository prepareMetadata(String dataModelFile) {
        MetadataRepository repository = new MetadataRepository();
        repository.load(Main.class.getResourceAsStream(dataModelFile));
        return repository;
    }
}
