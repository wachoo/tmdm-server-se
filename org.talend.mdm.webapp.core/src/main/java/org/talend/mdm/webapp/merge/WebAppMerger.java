package org.talend.mdm.webapp.merge;

import org.apache.log4j.Logger;
import org.codehaus.cargo.module.merge.MergeException;
import org.codehaus.cargo.module.merge.MergeProcessor;
import org.codehaus.cargo.module.webapp.WarArchive;

public class WebAppMerger implements MergeProcessor {

    private static final Logger LOGGER = Logger.getLogger(WebAppMerger.class);

    @Override
    public void addMergeItem(Object o) throws MergeException {
        System.out.println("o = " + o);
        try {
            if (o instanceof WarArchive) {
                WarArchive archive = (WarArchive) o;
                System.out.println("archive = " + archive);
                LOGGER.info("Adding WAR '" + archive.getWebXml().getFileName() + "'");
            }
        } catch (Exception e) {
            LOGGER.error("Unexpected exception occurred during merge preparation.", e);
        }
    }

    @Override
    public Object performMerge() throws MergeException {
        LOGGER.info("Performing merge...");
        return null;
    }
}
