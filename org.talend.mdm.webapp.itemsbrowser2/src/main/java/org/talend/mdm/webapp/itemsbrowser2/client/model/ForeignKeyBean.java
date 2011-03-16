package org.talend.mdm.webapp.itemsbrowser2.client.model;

public class ForeignKeyBean extends ItemBaseModel {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String key : this.getProperties().keySet()) {
            if (!key.equals("i")) {
                sb.append(this.getProperties().get(key));
                sb.append("-");
            }
        }
        return sb.toString().substring(0, sb.toString().length() - 1);
    }

}
