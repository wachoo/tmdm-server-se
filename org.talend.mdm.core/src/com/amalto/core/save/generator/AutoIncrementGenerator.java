package com.amalto.core.save.generator;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

import com.amalto.core.server.Server;

@SuppressWarnings("nls")
public class AutoIncrementGenerator {

    private static final Logger LOGGER = Logger.getLogger(AutoIncrementGenerator.class);

    private static final boolean ENABLE_SAFE_CONCURRENT_INCREMENT;

    private static final InMemoryAutoIncrementGenerator inMemoryAutoIncrementGenerator = new InMemoryAutoIncrementGenerator();

    private static final StorageAutoIncrementGenerator storageAutoIncrementGenerator = new StorageAutoIncrementGenerator();

    static {
        // Enable concurrent auto increment generator
        Properties properties = MDMConfiguration.getConfiguration();
        boolean enableConcurrentIncrement = Boolean.parseBoolean(properties.getProperty(Server.SYSTEM_CLUSTER, Boolean.FALSE.toString()));
        if (enableConcurrentIncrement) {
            LOGGER.info("Enable concurrent access support for auto increment generator.");
        } else {
            LOGGER.info("Concurrent access support for auto increment generator is disabled.");
        }
        ENABLE_SAFE_CONCURRENT_INCREMENT = enableConcurrentIncrement;
    }

    public static AutoIdGenerator get() {
        if (ENABLE_SAFE_CONCURRENT_INCREMENT) {
            return storageAutoIncrementGenerator;
        }
        return inMemoryAutoIncrementGenerator;
    }

}
