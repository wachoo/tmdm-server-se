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
//        StringBuilder sb = new StringBuilder();
//        if (this.getProperties().keySet().size() > 1) {
//            for (String key : this.getProperties().keySet()) {
//                if (!key.equals("i")) { //$NON-NLS-1$
//                    sb.append(this.getProperties().get(key));
//                    sb.append("-"); //$NON-NLS-1$
//                }
//            }
//            return sb.toString().substring(0, sb.toString().length() - 1);
//        } else
//            for (String key : this.getProperties().keySet()) {
//                sb.append(this.getProperties().get(key));
//            }
//
//        return sb.toString();
        return id;
    }
}
