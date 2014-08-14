package com.amalto.core.objects.configurationinfo.assemble;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

import com.amalto.core.server.ConfigurationInfo;
import com.amalto.core.util.Util;

public class MigrateVersionSubProc extends AssembleSubProc {

    protected static final Logger LOGGER = Logger.getLogger(MigrateVersionSubProc.class);

    @Override
    public void run() throws Exception {
        // perform upgrades
        boolean autoupgrade = "true".equals(MDMConfiguration.getConfiguration().getProperty("system.data.auto.upgrade", "true"));
        if (autoupgrade) {
            try {
                ConfigurationInfo ctrl = Util.getConfigurationInfoCtrlLocal();
                ctrl.autoUpgrade();
            } catch (Exception e) {
                LOGGER.error("Migrate error! ", e);
            }
        }
    }
}
