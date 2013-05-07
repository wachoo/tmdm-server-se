package com.amalto.core.query.optimization;

import com.amalto.core.query.user.Select;

/**
 *
 */
public interface Optimizer {

    void optimize(Select select);

}
