package com.amalto.core.objects.datacluster;

import com.amalto.core.objects.ObjectPOJOPK;


public class DataClusterPOJOPK extends ObjectPOJOPK {

    public DataClusterPOJOPK(ObjectPOJOPK pk) {
        super(pk.getIds());
    }

    /**
     * For Marshaling purposes only
     */
    public DataClusterPOJOPK() {
        super();
    }

    public DataClusterPOJOPK(String name) {
        super(new String[]{name});
    }

}
