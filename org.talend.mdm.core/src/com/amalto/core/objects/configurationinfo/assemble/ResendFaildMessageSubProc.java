package com.amalto.core.objects.configurationinfo.assemble;

import com.amalto.core.delegator.BeanDelegatorContainer;

/**
 * resend the failed autocommittosvn message
 * 
 * @author achen
 * 
 */
public class ResendFaildMessageSubProc extends AssembleSubProc {

    @Override
    public void run() throws Exception {
        BeanDelegatorContainer.getInstance().getItemCtrlDelegator().resendFailtSvnMessage();
    }
}
