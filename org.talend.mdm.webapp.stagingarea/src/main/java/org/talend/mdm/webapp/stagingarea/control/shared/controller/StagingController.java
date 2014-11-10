package org.talend.mdm.webapp.stagingarea.control.shared.controller;

import com.google.gwt.user.client.Timer;
import org.talend.mdm.webapp.stagingarea.control.client.StagingAreaControl;

public class StagingController {

    private Timer timer;

    private boolean autoRefresh;

    public StagingController() {
    }

    public void autoRefresh(boolean auto) {
        if (this.autoRefresh != auto) {
            this.autoRefresh = auto;
            if (auto) {
                timer = new Timer() {

                    @Override
                    public void run() {
                        Controllers.get().getSummaryController().refresh();
                        Controllers.get().getValidationController().refresh();
                        schedule(StagingAreaControl.getStagingAreaConfig().getRefreshIntervals());
                    }
                };
            } else {
                timer.cancel();
            }
        }
    }
}
