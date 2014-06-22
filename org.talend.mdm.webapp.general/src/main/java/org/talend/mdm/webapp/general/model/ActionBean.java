package org.talend.mdm.webapp.general.model;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ActionBean implements Serializable, IsSerializable {

    private static final long serialVersionUID = 2254323752634482658L;

    private List<ComboBoxModel> clusters;

    private List<ComboBoxModel> models;

    private String currentCluster;

    private String currentModel;

    public ActionBean() {
    }

    public List<ComboBoxModel> getClusters() {
        return clusters;
    }

    public void setClusters(List<ComboBoxModel> clusters) {
        this.clusters = clusters;
    }

    public List<ComboBoxModel> getModels() {
        return models;
    }

    public void setModels(List<ComboBoxModel> models) {
        this.models = models;
    }

    public String getCurrentCluster() {
        return currentCluster;
    }

    public void setCurrentCluster(String currentCluster) {
        this.currentCluster = currentCluster;
    }

    public String getCurrentModel() {
        return currentModel;
    }

    public void setCurrentModel(String currentModel) {
        this.currentModel = currentModel;
    }
}
